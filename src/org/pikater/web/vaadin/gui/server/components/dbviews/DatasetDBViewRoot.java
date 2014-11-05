package org.pikater.web.vaadin.gui.server.components.dbviews;

import java.io.InputStream;
import java.util.UUID;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.database.postgre.largeobject.PGLargeObjectReader;
import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.datasets.DataSetTableDBRow;
import org.pikater.shared.database.views.tableview.datasets.DataSetTableDBView;
import org.pikater.shared.database.views.tableview.datasets.DataSetTableDBView.Column;
import org.pikater.web.HttpContentType;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.sharedresources.ResourceExpiration;
import org.pikater.web.sharedresources.ResourceRegistrar;
import org.pikater.web.sharedresources.download.IDownloadResource;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.AbstractDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;
import org.pikater.web.vaadin.gui.server.components.forms.DatasetVisualizationForm;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogResultHandler;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogTaskResult;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets.compare.DatasetCompareWizard;
import org.pikater.web.vaadin.gui.server.ui_visualization.VisualizationUI.DSVisOneUIArgs;
import org.pikater.web.visualisation.DatasetVisualizationEntryPoint;
import org.pikater.web.visualisation.definition.result.DSVisOneResult;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextField;

import de.steinwedel.messagebox.MessageBox;

/**
 * Configuration for {@link DBTable DB tables} working with datasets.
 * 
 * @author SkyCrawl
 */
public class DatasetDBViewRoot<V extends DataSetTableDBView> extends AbstractDBViewRoot<V> {
	public DatasetDBViewRoot(V view) {
		super(view);
	}

	@Override
	public int getColumnSize(ITableColumn column) {
		DataSetTableDBView.Column specificColumn = (DataSetTableDBView.Column) column;
		switch (specificColumn) {
		case OWNER:
			return 125;

		case CREATED:
			return 100;

		case SIZE:
			return 60;

		case DEFAULT_TASK_TYPE:
			return 100;

		case FILENAME:
			return 100;

		case NUMBER_OF_INSTANCES:
			return 75;

		case DESCRIPTION:
			return 300;

		case APPROVED:
			return 75;

		case VISUALIZE:
		case COMPARE:
		case DOWNLOAD:
			return 100;

		case DELETE:
			return 75;

		default:
			throw new IllegalStateException(String.format("No sizing information found for column '%s'", specificColumn.name()));
		}
	}

	@Override
	public ITableColumn getExpandColumn() {
		return DataSetTableDBView.Column.DESCRIPTION;
	}

	@Override
	public void onCellCreate(ITableColumn column, AbstractDBViewValue<?> value, AbstractComponent component) {
		DataSetTableDBView.Column specificColumn = (DataSetTableDBView.Column) column;
		if ((specificColumn == Column.DESCRIPTION) || (specificColumn == Column.FILENAME)) {
			TextField tf_value = (TextField) component;
			tf_value.setDescription(tf_value.getValue());
		}
	}

	@Override
	public void approveAction(ITableColumn column, AbstractTableRowDBView row, final Runnable action) {
		final JPADataSetLO dataset = ((DataSetTableDBRow) row).getDataset();

		DataSetTableDBView.Column specificColumn = (DataSetTableDBView.Column) column;
		if (specificColumn == Column.VISUALIZE) {
			GeneralDialogs.componentDialog("Attributes to visualize", new DatasetVisualizationForm(dataset), new DialogCommons.IDialogResultHandler() {
				@Override
				public boolean handleResult(final Object[] args) {
					// show progress dialog
					ProgressDialog.show("Vizualization progress...", new ProgressDialog.IProgressDialogTaskHandler() {
						private DatasetVisualizationEntryPoint underlyingTask;

						@Override
						public void startTask(IProgressDialogResultHandler contextForTask) throws Exception {
							JPAAttributeMetaData[] attrsToCompare = (JPAAttributeMetaData[]) args[0];
							JPAAttributeMetaData attrTarget = (JPAAttributeMetaData) args[1];

							// start the task and bind it with the progress dialog
							underlyingTask = new DatasetVisualizationEntryPoint(contextForTask);
							underlyingTask.visualizeDataset(dataset, attrsToCompare, attrTarget);
						}

						@Override
						public void abortTask() {
							underlyingTask.abort();
						}

						@Override
						public void onTaskFinish(IProgressDialogTaskResult result) {
							// and when the task finishes, construct the UI
							DSVisOneUIArgs uiArgs = new DSVisOneUIArgs(dataset, (DSVisOneResult) result);
							Page.getCurrent().setLocation(uiArgs.toRedirectURL());
						}
					});
					return true;
				}
			});
		} else if (specificColumn == Column.COMPARE) {
			MessageBox mb = GeneralDialogs.wizardDialog("Dataset compare guide", new DatasetCompareWizard(dataset));
			mb.setWidth("800px");
			mb.setHeight("500px");
		} else if (specificColumn == Column.DOWNLOAD) {
			try {
				// download, don't run action
				final DataSetTableDBRow rowView = (DataSetTableDBRow) row;
				final UUID datasetDownloadResourceUI = ResourceRegistrar.registerResource(VaadinSession.getCurrent(), new IDownloadResource() {
					@Override
					public ResourceExpiration getLifeSpan() {
						return ResourceExpiration.ON_FIRST_PICKUP;
					}

					@Override
					public InputStream getStream() throws Exception {
						return PGLargeObjectReader.getForLargeObject(rowView.getDataset().getOID()).getInputStream();
					}

					@Override
					public long getSize() {
						return rowView.getDataset().getSize();
					}

					@Override
					public String getMimeType() {
						return HttpContentType.APPLICATION_JAR.getMimeType();
					}

					@Override
					public String getFilename() {
						return rowView.getDataset().getFileName();
					}
				});
				Page.getCurrent().setLocation(ResourceRegistrar.getDownloadURL(datasetDownloadResourceUI));
			} catch (Exception e) {
				// ResourceRegistrar.handleError(e, resp); // whatever the case here, we want it logged
				PikaterWebLogger.logThrowable("Could not issue the download because:", e);
				throw new RuntimeException(e);
			}
		} else if (specificColumn == Column.DELETE) {
			GeneralDialogs.confirm(null, "Really delete this dataset?", new GeneralDialogs.IDialogResultHandler() {
				@Override
				public boolean handleResult(Object[] args) {
					action.run(); // approve
					getParentTable().rebuildRowCache();
					return true;
				}
			});
		} else {
			throw new IllegalStateException(String.format("Action '%s' has to be approved before being executed", specificColumn.name()));
		}
	}
}