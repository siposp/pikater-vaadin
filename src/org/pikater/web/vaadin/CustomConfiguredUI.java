package org.pikater.web.vaadin;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;

import org.pikater.shared.database.jpa.JPAUser;
import org.pikater.shared.database.jpa.daos.DAOs;
import org.pikater.shared.database.jpa.status.JPAUserStatus;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.requests.HttpRequestComponent;
import org.pikater.web.requests.HttpRequestUtils;
import org.pikater.web.vaadin.CustomConfiguredUIServlet.PikaterUI;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogResultHandler;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.SpecialDialogs;
import org.pikater.web.vaadin.gui.server.components.popups.MyFancyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;

import com.porotype.iconfont.FontAwesome;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * A UI encapsulating all other specific UIs in this application so that child
 * UIs don't have to bother with the same old routines of setting everything
 * up.</br> Takes care of various things:
 * <ol>
 * <li>Checking whether the application has been launched properly and if not,
 * handles all required actions.
 * <li>Setting up notifications so that they can be used with the
 * "MyNotifications.show" notation anywhere in the application.
 * <li>Sets the default error handler to prevent errors bubbling right to the
 * client's browser.
 * <li>Provides some additional interface for child UIs.
 * </ol>
 */
public abstract class CustomConfiguredUI extends UI {
	private static final long serialVersionUID = 3280691990478021417L;

	// --------------------------------------------------------
	// APPLICATION SERVLET DEFINITION

	/**
	 * <font color="red">A single servlet to serve all content of this
	 * application, whether static or dynamic.</font></br></br> This means that
	 * as long as all used UIs inherit from {@link CustomConfiguredUI}, the
	 * following rules take place:
	 * <ul>
	 * <li>everything is taken care of and you need not bother defining your own
	 * servlets,
	 * <li>if you add new UIs, you still have to create a URL mapping for them
	 * in {@link CustomConfiguredUIServlet}.
	 * </ul>
	 */
	@WebServlet(value = { "/*", "/VAADIN/*" }, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = CustomConfiguredUI.class, widgetset = "org.pikater.web.vaadin.gui.PikaterWidgetset")
	public static class AppServlet extends CustomConfiguredUIServlet {
		private static final long serialVersionUID = 2623580800316091787L;
	}

	// --------------------------------------------------------
	// FIELDS & METHODS

	private final UniversalUIExtension universalUIExt = new UniversalUIExtension();
	private final CssLayout topLayout = new CssLayout();
	private final MyFancyNotifications notifications = new MyFancyNotifications();

	@Override
	protected void init(VaadinRequest request) {
		/*
		 * Set some basic stuff.
		 */
		FontAwesome.load();

		/*
		 * Enable sending client errors to the server, where they will be
		 * logged.
		 */
		universalUIExt.extend(this);

		/*
		 * Make inner layout independent of the content's size.
		 */
		topLayout.setSizeFull();

		/*
		 * Prevent errors from directly being displayed on the client. Rather
		 * log the errors on server and send a notification to the client. NOTE:
		 * this is the last hook to catch the exception. If another error
		 * handler is set on a child component of a UI, this error handler will
		 * not be used.
		 */
		setErrorHandler(new DefaultErrorHandler() {
			private static final long serialVersionUID = -4395506937938101756L;

			@Override
			public void error(com.vaadin.server.ErrorEvent event) {
				PikaterWebLogger.logThrowable(
						"Default UI error handler caught the following error:",
						event.getThrowable());
				MyNotifications.showApplicationError();
			}
		});

		/*
		 * Default initialization of the UI.
		 */
		if (!WebAppConfiguration.isApplicationReadyToServe()) // application has
																// not yet been
																// setup and
																// pikater has
																// not yet been
																// launched
		{
			// force the user to authenticate so that he can setup and launch
			// pikater on remote machines
			forceUserToAuthenticate(new IDialogResultHandler() {
				@Override
				public boolean handleResult(Object[] args) {
					/*
					 * Try to authenticate using the database. Note: database
					 * connection is assumed to have been checked in {@link
					 * StartupAndQuitListener}.
					 */
					String login = (String) args[0];
					String password = (String) args[1];

					try {
						authenticateUser(login, password, true);
					} catch (IllegalStateException e) {
						MyNotifications.showWarning("Access denied",
								e.getMessage());
						return false;
					}

					// authentication succeeded
					displayApplicationSetupWizard();
					return true;
				}
			});
		} else {
			displayChildContent();
		}
	}

	// -------------------------------------------------------------------
	// PRIVATE INTERFACE AND TYPES - UI building

	/**
	 * Make the user launch the application first (to be usable at all) and then
	 * display index page to him if authenticated for that.
	 */
	private void displayApplicationSetupWizard() {
		setPageCroppedAndHorizontallyCentered(false);
		// TODO: display welcome tour wizard
	}

	/**
	 * A special primary routine for setting this UI's content. All methods in
	 * this class should use this method instead of the inherited
	 * {@link #setContent()} which is now solely dedicated to the child classes.
	 * 
	 * REASON: The notifications feature has a tendency to cause a lot of
	 * confusion because the notifications component has to always be present in
	 * UI's layout and this should be done transparently...
	 * 
	 */
	private void setMyContent(Component content) {
		if (content == null) // most likely Vaadin's own initialization,
								// unfortunately...
		{
			/*
			 * We have to forward the call and return because nothing is
			 * initialized at this point. This method has been called from super
			 * constructor.
			 */
			super.setContent(content);
		} else {
			topLayout.removeAllComponents();
			topLayout.addComponent(content);
			content.setSizeFull();
			topLayout.addComponent(notifications); // always ensure that
													// notifications component
													// is added
			super.setContent(topLayout); // the method is overriden in these
											// class - let's avoid an infinite
											// loop
		}
	}

	@Override
	public void setContent(Component content) {
		/*
		 * Reserved for setting the child content.
		 */
		setMyContent(content);
	}

	// -------------------------------------------------------------------
	// AUTHENTICATION RELATED INTERFACE

	/**
	 * A low-level implementation that supports custom authentication mechanism.
	 * Should not be seen by children.
	 * 
	 * @param authHandler
	 *            the authentication mechanism
	 */
	private void forceUserToAuthenticate(
			final IDialogResultHandler resultHandler) {
		/*
		 * This is necessary for two reasons: 1) As a precaution to the user
		 * entering invalid auth info. If no content component is supplied to
		 * the UI, the login dialog disappears after the "init()" method
		 * finishes. 2) Notifications won't be displayed otherwise.
		 */
		setMyContent(new Label());

		// display the login dialog
		SpecialDialogs.loginDialog(resultHandler);
	}

	/**
	 * A high-level implementation with built-in authentication mechanism that
	 * calls a custom callback, when authentication is successful. Should be
	 * used in child classes.
	 * 
	 * @param authHandler
	 *            the callback
	 */
	protected void forceUserToAuthenticate(
			final IAuthenticationSuccessful authHandler) {
		forceUserToAuthenticate(new IDialogResultHandler() {
			@Override
			public boolean handleResult(Object[] args) {
				/*
				 * Try to authenticate using the database. Note: database
				 * connection is assumed to have been checked in {@link
				 * StartupAndQuitListener}.
				 */

				String login = (String) args[0];
				String password = (String) args[1];
				try {
					authenticateUser(login, password, false);
				} catch (IllegalStateException e) {
					MyNotifications
							.showWarning("Access denied", e.getMessage());
					return false;
				}

				// authentication succeeded
				authHandler.onSuccessfulAuth();
				return true;
			}
		});
	}

	/**
	 * Tries to authenticate the user. If the provided auth info is correct,
	 * automatically authenticates the user in this UI.
	 * 
	 * @return true if the provided auth info was correct
	 */
	private void authenticateUser(String login, String password,
			boolean adminCheck) {
		if (!WebAppConfiguration.avoidUsingDBForNow()) {
			JPAUser user = DAOs.userDAO.getByLoginAndPassword(login, password);
			if (user != null) {
				if (user.getStatus() != JPAUserStatus.ACTIVE) {
					throw new IllegalStateException("Account suspended.");
				} else if (adminCheck && !user.isAdmin()) {
					throw new IllegalStateException("Not an admin.");
				} else {
					UserAuth.login(VaadinSession.getCurrent(), user.getId()); // actually
																				// authenticate
																				// in
																				// this
																				// UI
				}
			} else {
				throw new IllegalStateException("Invalid auth information.");
			}
		}
	}

	protected interface IAuthenticationSuccessful {
		void onSuccessfulAuth();
	}

	// -----------------------------------------------------------
	// PROTECTED CONVENIENCE INTERFACE

	protected void returnErrorCode(int errorCode) {
		returnErrorCode(errorCode, "");
	}

	/**
	 * Only to be used from the {@link #init(VaadinRequest)} method when an
	 * unrecoverable error occurs.
	 */
	protected void returnErrorCode(int errorCode, String message) {
		try {
			VaadinServletService.getCurrentResponse().sendError(errorCode,
					message);
		} catch (IOException e) {
			PikaterWebLogger
					.logThrowable(
							String.format(
									"UI could not be created but writing an error code of '%d' "
											+ "to the response failed because of the following exception. Vaadin should have "
											+ "defaulted to error code 500 instead.",
									errorCode), e);
		}
	}

	// -----------------------------------------------------------
	// SOME ABSTRACT INTERFACE TO IMPLEMENT BY CHILD UIs

	/**
	 * Callback to the child indicating that it can start setting the UI as it
	 * wishes. Until that time, it must not change the UI content in any way.
	 */
	protected abstract void displayChildContent();

	// -----------------------------------------------------------
	// PUBLIC INSTANCE INTERFACE

	public void setPageCroppedAndHorizontallyCentered(boolean centered) {
		if (centered) {
			topLayout.setSizeUndefined();
			topLayout.setStyleName("topLevelElement");
		} else {
			topLayout.removeStyleName("topLevelElement");
			topLayout.setSizeFull();
		}
	}

	/**
	 * Access to special client features.
	 */
	public UniversalUIExtension getUniversalUIExtension() {
		return universalUIExt;
	}

	public MyFancyNotifications getNotificationsComponent() {
		return notifications;
	}

	// -----------------------------------------------------------
	// STATIC INTERFACE

	/**
	 * Returns something like "http://my.domain/Pikater". A URL that can be used
	 * to access various application artifacts or servlets.
	 */
	public static String getBaseAppURLFromLastRequest() {
		return HttpRequestUtils.getURLPrefix(
				VaadinServletService.getCurrentServletRequest(),
				HttpRequestComponent.P4_APPCONTEXT);
	}

	/**
	 * Returns something like "http://my.domain/Pikater/index". A URL that, if
	 * used in the browser's address bar, redirects the user to the given UI.
	 */
	public static String getRedirectURLToUI(PikaterUI ui) {
		return CustomConfiguredUI.getBaseAppURLFromLastRequest() + "/"
				+ ui.getURLPattern();
	}

	public static boolean isProductionModeActive() {
		return VaadinSession.getCurrent().getConfiguration().isProductionMode();
	}

	public static boolean isDebugModeActive() {
		return !isProductionModeActive();
	}

	public static String getURIFragment() {
		String result = UI.getCurrent().getPage().getUriFragment();
		if ((result == null) || !result.startsWith("!")) {
			return result;
		} else {
			return result.substring(1);
		}
	}

	public static boolean isURIFragmentDefined() {
		String uriFragment = getURIFragment();
		return (uriFragment != null) && !uriFragment.isEmpty();
	}
}
