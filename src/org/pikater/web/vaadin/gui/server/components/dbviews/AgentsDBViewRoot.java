package org.pikater.web.vaadin.gui.server.components.dbviews;

import java.io.InputStream;
import java.util.UUID;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.externalagents.ExternalAgentTableDBRow;
import org.pikater.shared.database.views.tableview.externalagents.ExternalAgentTableDBView;
import org.pikater.shared.database.views.tableview.externalagents.ExternalAgentTableDBView.Column;
import org.pikater.web.HttpContentType;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.sharedresources.ResourceExpiration;
import org.pikater.web.sharedresources.ResourceRegistrar;
import org.pikater.web.sharedresources.download.IDownloadResource;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.AbstractDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextField;

/**
 * Configuration for {@link DBTable DB tables} working with agents.
 * 
 * @author SkyCrawl
 */
public class AgentsDBViewRoot extends AbstractDBViewRoot<ExternalAgentTableDBView> {
	public AgentsDBViewRoot(ExternalAgentTableDBView view) {
		super(view);
	}

	@Override
	public int getColumnSize(ITableColumn column) {
		ExternalAgentTableDBView.Column specificColumn = (ExternalAgentTableDBView.Column) column;
		switch (specificColumn) {
		case OWNER:
		case CREATED:
			return 100;
		case NAME:
			return 150;
		case AGENT_CLASS:
			return 150;
		case DESCRIPTION:
			return 250;
		case APPROVED:
			return 75;
		case DOWNLOAD:
			return 100;
		case DELETE:
			return 75;
		default:
			throw new IllegalStateException("Unknown state: " + specificColumn.name());
		}
	}

	@Override
	public ITableColumn getExpandColumn() {
		return ExternalAgentTableDBView.Column.DESCRIPTION;
	}

	@Override
	public void onCellCreate(ITableColumn column, AbstractDBViewValue<?> value, AbstractComponent component) {
		ExternalAgentTableDBView.Column specificColumn = (ExternalAgentTableDBView.Column) column;
		if ((specificColumn == Column.AGENT_CLASS) || (specificColumn == Column.DESCRIPTION)) {
			TextField tf_value = (TextField) component;
			tf_value.setDescription(tf_value.getValue());
		}
	}

	@Override
	public void approveAction(ITableColumn column, AbstractTableRowDBView row, final Runnable action) {
		String agentName = (String) row.getValueWrapper(ExternalAgentTableDBView.Column.NAME).getValue();

		ExternalAgentTableDBView.Column specificColumn = (ExternalAgentTableDBView.Column) column;
		if (specificColumn == Column.DOWNLOAD) {
			// download, don't run action
			final ExternalAgentTableDBRow rowView = (ExternalAgentTableDBRow) row;
			try {
				final UUID agentDownloadResourceUUID = ResourceRegistrar.registerResource(VaadinSession.getCurrent(), new IDownloadResource() {
					@Override
					public ResourceExpiration getLifeSpan() {
						return ResourceExpiration.ON_FIRST_PICKUP;
					}

					@Override
					public InputStream getStream() {
						return rowView.getAgent().getInputStream();
					}

					@Override
					public long getSize() {
						return rowView.getAgent().getJar().length;
					}

					@Override
					public String getMimeType() {
						return HttpContentType.APPLICATION_JAR.getMimeType();
					}

					@Override
					public String getFilename() {
						String fileName = rowView.getAgent().getName();
						if (!fileName.endsWith(".jar")) {
							fileName += ".jar";
						}
						return fileName;
					}
				});
				Page.getCurrent().setLocation(ResourceRegistrar.getDownloadURL(agentDownloadResourceUUID));
			} catch (Exception e) {
				// ResourceRegistrar.handleError(e, resp); // whatever the case here, we want it logged
				PikaterWebLogger.logThrowable("Could not issue the download because:", e);
				throw new RuntimeException(e);
			}
		} else if (specificColumn == Column.DELETE) {
			GeneralDialogs.confirm(null, String.format("Really delete agent '%s'?", agentName), new GeneralDialogs.IDialogResultHandler() {
				@Override
				public boolean handleResult(Object[] args) {
					action.run();
					getParentTable().rebuildRowCache();
					return true; // close the dialog
				}
			});
		} else {
			throw new IllegalStateException(String.format("Action '%s' has to be approved before being executed", specificColumn.name()));
		}
	}
}
