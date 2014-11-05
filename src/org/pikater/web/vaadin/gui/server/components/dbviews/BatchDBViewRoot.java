package org.pikater.web.vaadin.gui.server.components.dbviews;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.pikater.core.agents.gateway.WebToCoreEntryPoint;
import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBView;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBRow;
import org.pikater.shared.quartz.jobs.InterruptibleJobHelper;
import org.pikater.shared.util.IOUtils;
import org.pikater.web.HttpContentType;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.quartzjobs.results.ExportBatchResultsJob;
import org.pikater.web.sharedresources.ResourceExpiration;
import org.pikater.web.sharedresources.ResourceRegistrar;
import org.pikater.web.sharedresources.download.IDownloadResource;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.AbstractDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogResultHandler;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogTaskResult;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextField;

/**
 * Configuration for {@link DBTable DB tables} working with batches.
 * 
 * @author SkyCrawl
 */
public class BatchDBViewRoot<V extends BatchTableDBView> extends AbstractDBViewRoot<V> {
	public BatchDBViewRoot(V view) {
		super(view);
	}

	@Override
	public int getColumnSize(ITableColumn column) {
		BatchTableDBView.Column specificColumn = (BatchTableDBView.Column) column;
		switch (specificColumn) {
		case FINISHED:
		case CREATED:
			return 75;

		case TOTAL_PRIORITY:
		case USER_PRIORITY:
		case STATUS:
			return 100;

		case OWNER:
			return 100;
		case NAME:
			return 125;
		case NOTE:
			return 150;
		case ABORT:
			return 75;
		case RESULTS:
			return 100;

		default:
			throw new IllegalStateException("Unknown state: " + specificColumn.name());
		}
	}

	@Override
	public ITableColumn getExpandColumn() {
		return BatchTableDBView.Column.NOTE;
	}

	@Override
	public void onCellCreate(ITableColumn column, AbstractDBViewValue<?> value, AbstractComponent component) {
		BatchTableDBView.Column specificColumn = (BatchTableDBView.Column) column;
		if (specificColumn == BatchTableDBView.Column.NOTE) {
			TextField tf_value = (TextField) component;
			tf_value.setDescription(tf_value.getValue());
		} else if (specificColumn == BatchTableDBView.Column.TOTAL_PRIORITY) {
			value.setOnCommitCallback(new AbstractDBViewValue.IOnValueCommitted() {
				@Override
				public void onCommitted(AbstractTableRowDBView row, AbstractDBViewValue<?> value) {
					final BatchTableDBRow specificRow = (BatchTableDBRow) row;
					if (WebAppConfiguration.isCoreEnabled()) {
						try {
							WebToCoreEntryPoint.notify_batchPriorityChanged(specificRow.getBatch().getId());
						} catch (Exception e) {
							PikaterWebLogger.logThrowable(String.format("Could not notify core about a priority change of batch '%d':", specificRow.getBatch().getId()), e);
							MyNotifications.showApplicationError();
						}
					} else {
						GeneralDialogs.info("Core not available", "Priority was changed but it won't be effective.");
					}
				}
			});
		}
	}

	@Override
	public void approveAction(ITableColumn column, final AbstractTableRowDBView row, Runnable action) {
		final BatchTableDBRow specificRow = (BatchTableDBRow) row;

		BatchTableDBView.Column specificColumn = (BatchTableDBView.Column) column;
		if (specificColumn == BatchTableDBView.Column.ABORT) {
			if (WebAppConfiguration.isCoreEnabled()) {
				try {
					WebToCoreEntryPoint.notify_killBatch(specificRow.getBatch().getId());
				} catch (Exception e) {
					PikaterWebLogger.logThrowable(String.format("Could not kill batch '%d':", specificRow.getBatch().getId()), e);
					MyNotifications.showApplicationError();
				}
			} else {
				GeneralDialogs.info("Core not available", "Experiments can not be aborted at this time.");
			}
		} else if (specificColumn == BatchTableDBView.Column.RESULTS) {
			final File tmpFile = IOUtils.createTemporaryFile("results", ".csv");

			// download, don't run action
			ProgressDialog.show("Export progress...", new ProgressDialog.IProgressDialogTaskHandler() {
				private InterruptibleJobHelper underlyingTask;

				@Override
				public void startTask(IProgressDialogResultHandler contextForTask) throws Exception {
					// start the task and bind it with the progress dialog
					underlyingTask = new InterruptibleJobHelper();
					underlyingTask.start(ExportBatchResultsJob.class, new Object[] { specificRow.getBatch(), tmpFile, contextForTask });
				}

				@Override
				public void abortTask() {
					underlyingTask.abort();
				}

				@Override
				public void onTaskFinish(IProgressDialogTaskResult result) {
					try {
						UUID resultsDownloadResourceUI = ResourceRegistrar.registerResource(VaadinSession.getCurrent(), new IDownloadResource() {
							@Override
							public ResourceExpiration getLifeSpan() {
								return ResourceExpiration.ON_FIRST_PICKUP;
							}

							@Override
							public InputStream getStream() throws Exception {
								return new FileInputStream(tmpFile);
							}

							@Override
							public long getSize() {
								return tmpFile.length();
							}

							@Override
							public String getMimeType() {
								return HttpContentType.TEXT_CSV.getMimeType();
							}

							@Override
							public String getFilename() {
								return tmpFile.getName();
							}
						});
						Page.getCurrent().setLocation(ResourceRegistrar.getDownloadURL(resultsDownloadResourceUI));
					} catch (Exception e) {
						// ResourceRegistrar.handleError(e, resp); // whatever the case here, we want it logged
						PikaterWebLogger.logThrowable("Could not issue the download because:", e);
						throw new RuntimeException(e);
					}
				}
			});
		} else {
			throw new IllegalStateException(String.format("Action '%s' has to be approved before being executed", specificColumn.name()));
		}
	}
}