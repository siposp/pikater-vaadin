package org.pikater.web.vaadin.gui.server.components.dbviews;

import java.io.InputStream;
import java.util.UUID;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.batches.experiments.results.ResultTableDBRow;
import org.pikater.shared.database.views.tableview.batches.experiments.results.ResultTableDBView;
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
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.batches.compare.ResultCompareWizard;
import org.pikater.web.vaadin.gui.server.ui_visualization.VisualizationUI.DSVisOneUIArgs;
import org.pikater.web.visualisation.DatasetVisualizationEntryPoint;
import org.pikater.web.visualisation.definition.result.DSVisOneResult;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextField;

import de.steinwedel.messagebox.MessageBox;

/**
 * Configuration for {@link DBTable DB tables} working with experiment results.
 * 
 * @author SkyCrawl
 */
public class ResultDBViewRoot<V extends ResultTableDBView> extends AbstractDBViewRoot<V> {
	public ResultDBViewRoot(V view) {
		super(view);
	}

	@Override
	public int getColumnSize(ITableColumn column) {
		ResultTableDBView.Column specificColumn = (ResultTableDBView.Column) column;
		switch (specificColumn) {
		case AGENT_NAME:
		case ERROR_RATE:
		case KAPPA:
		case REL_ABS_ERR:
		case MEAN_ABS_ERR:
		case VISUALIZE:
		case COMPARE:
			return 100;

		case ROOT_REL_SQR_ERR:
		case TRAINED_MODEL:
			return 115;

		case WEKA_OPTIONS:
		case NOTE:
		case ROOT_MEAN_SQR_ERR:
			return 125;

		default:
			throw new IllegalStateException("Unknown state: " + specificColumn.name());
		}
	}

	@Override
	public ITableColumn getExpandColumn() {
		return null;
	}

	@Override
	public void onCellCreate(ITableColumn column, AbstractDBViewValue<?> value, AbstractComponent component) {
		ResultTableDBView.Column specificColumn = (ResultTableDBView.Column) column;
		if (specificColumn == ResultTableDBView.Column.NOTE) {
			TextField tf_value = (TextField) component;
			tf_value.setDescription(tf_value.getValue());
		}
	}

	@Override
	public void approveAction(ITableColumn column, AbstractTableRowDBView row, Runnable action) {
		final ResultTableDBRow specificRow = (ResultTableDBRow) row;
		ResultTableDBView.Column specificColumn = (ResultTableDBView.Column) column;
		if (specificColumn == ResultTableDBView.Column.TRAINED_MODEL) {
			try {
				// download, don't run action
				UUID resultsDownloadResourceUI = ResourceRegistrar.registerResource(VaadinSession.getCurrent(), new IDownloadResource() {
					@Override
					public ResourceExpiration getLifeSpan() {
						return ResourceExpiration.ON_FIRST_PICKUP;
					}

					@Override
					public InputStream getStream() throws Exception {
						return specificRow.getResult().getCreatedModel().getInputStream();
					}

					@Override
					public long getSize() {
						return specificRow.getResult().getCreatedModel().getSerializedAgent().length;
					}

					@Override
					public String getMimeType() {
						return HttpContentType.APPLICATION_OCTET_STREAM.getMimeType();
					}

					@Override
					public String getFilename() {
						return specificRow.getResult().getCreatedModel().getFileName();
					}
				});
				Page.getCurrent().setLocation(ResourceRegistrar.getDownloadURL(resultsDownloadResourceUI));
			} catch (Exception e) {
				// ResourceRegistrar.handleError(e, resp); // whatever the case here, we want it logged
				PikaterWebLogger.logThrowable("Could not issue the download because:", e);
				throw new RuntimeException(e);
			}
		} else if (specificColumn == ResultTableDBView.Column.VISUALIZE) {
			final JPADataSetLO resultsDataset = specificRow.getFirstValidOutput(); // this is a temporary solution
			GeneralDialogs.componentDialog("Attributes to visualize", new DatasetVisualizationForm(resultsDataset), new DialogCommons.IDialogResultHandler() {
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
							underlyingTask.visualizeDataset(resultsDataset, attrsToCompare, attrTarget);
						}

						@Override
						public void abortTask() {
							underlyingTask.abort();
						}

						@Override
						public void onTaskFinish(IProgressDialogTaskResult result) {
							// and when the task finishes, construct the UI
							DSVisOneUIArgs uiArgs = new DSVisOneUIArgs(resultsDataset, (DSVisOneResult) result);
							Page.getCurrent().setLocation(uiArgs.toRedirectURL());
						}
					});
					return true;
				}
			});
		} else if (specificColumn == ResultTableDBView.Column.COMPARE) {
			final JPADataSetLO resultsDataset = specificRow.getFirstValidOutput(); // this is a temporary solution
			final JPADataSetLO inputDataset = specificRow.getFirstValidInput(); // this is a temporary solution
			MessageBox mb = GeneralDialogs.wizardDialog("Result compare guide", new ResultCompareWizard(resultsDataset, inputDataset));
			mb.setWidth("800px");
			mb.setHeight("500px");
		} else {
			throw new IllegalStateException(String.format("Action '%s' has to be approved before being executed", specificColumn.name()));
		}
	}
}