package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets;

import org.pikater.shared.database.views.tableview.datasets.DataSetTableDBView;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.UserSession;
import org.pikater.web.vaadin.UserUploads;
import org.pikater.web.vaadin.gui.server.components.dbviews.DatasetDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.components.upload.MyUploadStateWindow;
import org.pikater.web.vaadin.gui.server.components.upload.UploadLimitReachedException;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets.upload.DatasetUploadWizard;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import de.steinwedel.messagebox.MessageBox;

/**
 * View implementing the user dataset feature.
 * 
 * @author SkyCrawl
 * 
 * @see {@link DefaultUI}
 * @see {@link ContentProvider}
 */
public class UserDatasetsView extends DatasetsView {
	private static final long serialVersionUID = 5568928739598297585L;

	/**
	 * This should be a constant reference across all UI instances.
	 */
	private UserUploads uploadManager;

	/**
	 * One upload manager per UI instance.
	 */
	private MyUploadStateWindow uploadInfoProvider;

	public UserDatasetsView() {
		super();

		getMainLayout().addCustomActionComponent(new Button("Upload a new dataset", new Button.ClickListener() {
			private static final long serialVersionUID = -1045335713385385849L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					MessageBox mb = GeneralDialogs.wizardDialog("Dataset upload guide", new DatasetUploadWizard(uploadManager, uploadInfoProvider));
					mb.setWidth("600px");
					mb.setHeight("500px");
				} catch (UploadLimitReachedException e) {
					MyNotifications.showWarning("Try later", "Only 3 concurrent uploads allowed.");
					return;
				}
			}
		}));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		this.uploadManager = UserSession.getUserUploadManager(VaadinSession.getCurrent());
		this.uploadInfoProvider = uploadManager.createUploadInfoProvider();

		// always call these last, when you're absolutely ready to display the content
		getMainLayout().setView(new DatasetDBViewRoot<DataSetTableDBView>(new DataSetTableDBView(UserAuth.getUserEntity(VaadinSession.getCurrent()))));
		super.finishInit(); // don't forget to!
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