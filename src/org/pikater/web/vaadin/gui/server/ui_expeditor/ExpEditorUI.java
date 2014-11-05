package org.pikater.web.vaadin.gui.server.ui_expeditor;

import org.pikater.core.agents.gateway.WebToCoreEntryPoint;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.config.KnownCoreAgents;
import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.vaadin.CustomConfiguredUI;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;

/**
 * Displays the experiment editor.
 * 
 * @author SkyCrawl
 */
@Title("Experiments")
@Theme("pikater")
@Push(value = PushMode.AUTOMATIC)
public class ExpEditorUI extends CustomConfiguredUI {
	private static final long serialVersionUID = -797960197800185978L;

	@Override
	protected void init(VaadinRequest request) {
		/*
		 * Don't forget to call this.
		 * IMPORTANT:
		 * 1) You shouldn't update the UI in this method. You only provide the content component
		 * when you're asked to in the {@link #displayChildContent()} method.
		 * 2) When {@link #displayChildContent()} is called, this method is still not finished.
		 * You shouldn't have any initializing code after the super.init() call.
		 */
		super.init(request);
	}

	@Override
	protected void displayChildContent() {
		/*
		 * Display index page if user is authenticated or make him authenticate first
		 * and then display it.
		 */
		if (UserAuth.isUserAuthenticated(VaadinSession.getCurrent())) {
			displayExperimentEditor();
		} else {
			forceUserToAuthenticate(new CustomConfiguredUI.IAuthenticationSuccessful() {
				@Override
				public void onSuccessfulAuth() {
					displayExperimentEditor();
				}
			});
		}
	}

	/**
	 * Method displaying the content of this UI. We want each instance of this
	 * UI to have its own copy of experiment "boxes". {@link #createAgentInfoProvider()}
	 * is used to provide that.
	 */
	private void displayExperimentEditor() {
		final KnownCoreAgents agentInfoProvider = createAgentInfoProvider();
		if (agentInfoProvider != null) {
			// disable regular page layout
			setPageCroppedAndHorizontallyCentered(false);

			// simply create a new empty editor and let the user handle the rest
			ExpEditor editor = new ExpEditor(agentInfoProvider);
			setContent(editor);

			// display an empty experiment by default
			editor.addEmptyTab();
		}
	}

	private KnownCoreAgents createAgentInfoProvider() {
		// fetch information about available agents from core or create dummy
		KnownCoreAgents agentInfoProvider = null;
		if (WebAppConfiguration.isCoreEnabled()) {
			try {
				agentInfoProvider = KnownCoreAgents.getFrom(WebToCoreEntryPoint.getAgentInfosVisibleForUser(UserAuth.getUserID(VaadinSession.getCurrent())));
			} catch (Exception t) {
				PikaterWebLogger.logThrowable("No information about agents received from core.", t);
				GeneralDialogs.error("Not available at this moment", "Information about agents has not been received from core.");
			}
		} else {
			agentInfoProvider = KnownCoreAgents.getDummy();
		}
		return agentInfoProvider;
	}
}