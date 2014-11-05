package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.agents;

import org.pikater.shared.database.views.tableview.externalagents.ExternalAgentTableDBView;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.UserSession;
import org.pikater.web.vaadin.UserUploads;
import org.pikater.web.vaadin.gui.server.components.dbviews.AgentsDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.forms.AgentUploadForm;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.MyPopup;
import org.pikater.web.vaadin.gui.server.components.upload.MyUploadStateWindow;
import org.pikater.web.vaadin.gui.server.components.upload.UploadLimitReachedException;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

/**
 * View implementing the user agent feature.
 * 
 * @author SkyCrawl
 * 
 * @see {@link DefaultUI}
 * @see {@link ContentProvider}
 */
@StyleSheet("userAgentsView.css")
public class UserAgentsView extends AgentsView {
	private static final long serialVersionUID = 1257881871718854102L;

	/**
	 * This should be a constant reference across all UI instances.
	 */
	private UserUploads uploadManager;

	/**
	 * One upload manager per UI instance.
	 */
	private MyUploadStateWindow uploadInfoProvider;

	public UserAgentsView() {
		super();

		addCustomActionComponent(new Button("Upload a new agent", new Button.ClickListener() {
			private static final long serialVersionUID = 2295909499889740333L;

			@Override
			public void buttonClick(ClickEvent event) {
				MyPopup agentsUploadWizardWindow = new MyPopup("Agent upload guide");
				agentsUploadWizardWindow.setWidth("500px");
				agentsUploadWizardWindow.setHeight("300px");

				try {
					AgentUploadForm form = new AgentUploadForm(agentsUploadWizardWindow, uploadManager, uploadInfoProvider);
					form.setStyleName("agentsUploadForm");
					agentsUploadWizardWindow.setContent(form);
					agentsUploadWizardWindow.show();
				} catch (UploadLimitReachedException e) {
					MyNotifications.showWarning("Try later", "Only 3 concurrent uploads allowed.");
				}
			}
		}));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		this.uploadManager = UserSession.getUserUploadManager(VaadinSession.getCurrent());
		this.uploadInfoProvider = uploadManager.createUploadInfoProvider();

		// required to be executed after initializing DB view
		setView(new AgentsDBViewRoot(new ExternalAgentTableDBView(UserAuth.getUserEntity(VaadinSession.getCurrent()))));
	}

	@Override
	public boolean isReadyToClose() {
		return !uploadInfoProvider.isAFileBeingUploaded(); // only inspect uploads in this UI instance
	}

	@Override
	public String getCloseMessage() {
		return "Uploads will be interrupted. Continue?";
	}

	@Override
	public void beforeClose() {
		super.beforeClose();
		this.uploadInfoProvider.interruptAll(); // only interrupt uploads originated in this UI instance
	}
}