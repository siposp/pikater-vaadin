package org.pikater.web.vaadin.gui.server.components.forms;

import java.io.File;
import java.util.EnumSet;

import org.pikater.shared.quartz.PikaterJobScheduler;
import org.pikater.web.HttpContentType;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.quartzjobs.UploadedAgentHandler;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.UserUploads;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.components.upload.IFileUploadEvents;
import org.pikater.web.vaadin.gui.server.components.upload.MyMultiUpload;
import org.pikater.web.vaadin.gui.server.components.upload.MyUploadStateWindow;
import org.pikater.web.vaadin.gui.server.components.upload.UploadLimitReachedException;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;

import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.StreamVariable.StreamingEndEvent;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.server.StreamVariable.StreamingStartEvent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 * A form to save/queue experiments.
 * 
 * @author SkyCrawl
 */
public class AgentUploadForm extends CustomFormLayout {
	private static final long serialVersionUID = -7727756941636752874L;

	private final TextField tf_agentClass;
	private final TextArea tf_agentDescription;
	private final MyMultiUpload upload;

	public AgentUploadForm(final Window parentPopup, UserUploads uploadManager, MyUploadStateWindow uploadInfoProvider) throws UploadLimitReachedException {
		super(null);

		this.tf_agentClass = FormFieldFactory.createTextField("Agent class (incl. package):", "Class of the agent?", null, false, false);
		this.tf_agentClass.setSizeFull();
		this.tf_agentClass.addValidator(new RegexpValidator("^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*$", "Not a valid package name. Press ALT+H to display online help."));
		this.tf_agentClass.addShortcutListener(new ShortcutListener("", KeyCode.H, new int[] { ModifierKey.ALT }) {
			private static final long serialVersionUID = -6065729102159170915L;

			@Override
			public void handleAction(Object sender, Object target) {
				Page.getCurrent().open("http://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html", "_blank");
			}
		});
		addField("class name", tf_agentClass);
		this.tf_agentDescription = FormFieldFactory.createTextArea("Optional description:", "Any description of the agent for future reference?", null, false, false);
		this.tf_agentDescription.setSizeFull();
		addField("description", tf_agentDescription);

		this.upload = uploadManager.createUploadButton("Choose file to upload (.jar)", uploadInfoProvider, EnumSet.of(HttpContentType.APPLICATION_JAR));
		this.upload.addFileUploadEventsCallback(new IFileUploadEvents() {
			@Override
			public void uploadStarted(StreamingStartEvent event) {
				parentPopup.setVisible(false);
			}

			@Override
			public void uploadFailed(StreamingErrorEvent event) {
				/*
				 * Single file upload is assumed here.
				 */

				parentPopup.close();
			}

			@Override
			public void uploadFinished(StreamingEndEvent event, File uploadedTemporaryFile) {
				/*
				 * Single file upload is assumed here.
				 */

				if (!WebAppConfiguration.avoidUsingDBForNow()) {
					Object[] jobParams = new Object[] { UserAuth.getUserEntity(VaadinSession.getCurrent()), event.getFileName(), tf_agentClass.getValue(), tf_agentDescription.getValue(),
							uploadedTemporaryFile, };
					try {
						PikaterJobScheduler.getJobScheduler().defineJob(UploadedAgentHandler.class, jobParams);
					} catch (Exception e) {
						PikaterWebLogger.logThrowable("Could not issue an uploaded agent handling job.", e);
						MyNotifications.showError("Upload failed", event.getFileName());
						return; // don't let the success notification be displayed
					} finally {
						parentPopup.close();
					}
				}

				if (WebAppConfiguration.isCoreEnabled()) {
					GeneralDialogs.info("Upload successful", "It may take a while before your agent is processed and made available in the experiment " + "editor.");
				} else {
					GeneralDialogs.info("Core not available at this moment", "Your agent has been saved and designated "
							+ "for registration but the actual registration may be pending until a running pikater core picks your agent up.");
				}
			}
		});
		this.upload.setEnabled(false);

		attachToButtonInterface(this.upload);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public IOnSubmit getSubmitAction() {
		return null; // we will not be using the clasical submit button
	}

	@Override
	protected synchronized void updateActionButton() {
		upload.setEnabled(isFormValidAndUpdated());
	}
}