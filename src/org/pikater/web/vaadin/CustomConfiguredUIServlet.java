package org.pikater.web.vaadin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.pikater.web.PikaterWebLogger;
import org.pikater.web.requests.HttpRequestUtils;
import org.pikater.web.sharedresources.ResourceRegistrar;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_expeditor.ExpEditorUI;
import org.pikater.web.vaadin.gui.server.ui_visualization.VisualizationUI;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.UI;

public class CustomConfiguredUIServlet extends VaadinServlet implements
		SessionInitListener, SessionDestroyListener {
	private static final long serialVersionUID = -8268135994612358127L;

	// --------------------------------------------------------
	// UI TO URL MAPPING

	private static final Map<String, PikaterUI> urlPatternToUI = new HashMap<String, PikaterUI>();
	static {
		for (PikaterUI ui : PikaterUI.values()) {
			urlPatternToUI.put(ui.getURLPattern(), ui);
		}
	}

	/**
	 * Public definition of all used UIs. By default, the application won't
	 * serve any UIs that are not declared in this enumeration.
	 */
	public enum PikaterUI {
		INDEX_PAGE("index", DefaultUI.class), EXP_EDITOR("editor",
				ExpEditorUI.class), DATASET_VISUALIZATION("visualization",
				VisualizationUI.class);

		private final String mappedURL;
		private final Class<? extends UI> mappedUI;

		private PikaterUI(String mappedURL, Class<? extends UI> mappedUI) {
			this.mappedURL = mappedURL;
			this.mappedUI = mappedUI;
		}

		public String getURLPattern() {
			return mappedURL;
		}

		public Class<? extends UI> getUI() {
			return mappedUI;
		}
	}

	// --------------------------------------------------------
	// INHERITED INTERFACE

	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized(); // this should always be called first

		getService().addSessionInitListener(this);
		getService().addSessionDestroyListener(this);
	}

	@Override
	protected VaadinServletService createServletService(
			DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		VaadinServletService vaadinServletService = new VaadinServletService(
				this, deploymentConfiguration) {
			private static final long serialVersionUID = 8833521650509773424L;

			@Override
			protected List<RequestHandler> createRequestHandlers()
					throws ServiceException {
				/*
				 * Specify request handlers in the order they will be called
				 * (LIFO) on incoming requests. This is useful for constructing
				 * dynamic content where no UIs are necessary.
				 */
				List<RequestHandler> requestHandlerList = super
						.createRequestHandlers();
				// requestHandlerList.add(new RequestHandler());
				return requestHandlerList;
			}
		};
		vaadinServletService.init();
		return vaadinServletService;
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		event.getSession().addUIProvider(new UIProvider() {
			private static final long serialVersionUID = -1327822157863022893L;

			@Override
			public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
				String servletPath = HttpRequestUtils
						.getServletPathWhetherMappedOrNot(VaadinServletService
								.getCurrentServletRequest());
				PikaterUI resultUI = urlPatternToUI.get(servletPath);
				if (resultUI == null) {
					try {
						VaadinServletService.getCurrentResponse().sendError(
								HttpServletResponse.SC_NOT_FOUND);
					} catch (IOException e) {
						PikaterWebLogger.logThrowable(
								String.format(
										"An undefined resource with servlet path '%s' was requested "
												+ "but writing an error code of 404 (NOT_FOUND) to the response failed because of the "
												+ "following exception. Vaadin should have defaulted to error code 500 instead.",
										servletPath), e);
					}
					return null;
				} else {
					return resultUI.getUI();
				}
			}
		});
	}

	@Override
	public void sessionDestroy(SessionDestroyEvent event) {
		/*
		 * Manually log user out (if authenticated with this session) so that
		 * any eventual background threads can correctly use the updated state.
		 * Also detach the session from {@link NoSessionStore} since it is
		 * Vaadin independent. IMPORTANT NOTE: this method might get called
		 * several times when closing a session because VaadinServlet
		 * implementations of our UI objects extend this class.
		 */

		ResourceRegistrar.expireSessionResources(event.getSession());
		if (UserAuth.isUserAuthenticated(event.getSession())) {
			UserAuth.logout(event.getSession());
		}

		/*
		 * IMPORTANT NOTES: - UIs are bound to a VaadinSession. We don't need to
		 * close and destroy the UIs because that is already done automatically
		 * for us - their detach listeners are called. - A UI expires when no
		 * requests are received by it from the client and after the client
		 * engine sends 3 keep-alive messages. - All UIs of a session expire =>
		 * session still remains and is cleaned up from the server when the
		 * session timeout configured in the web application expires.
		 */
	}
}