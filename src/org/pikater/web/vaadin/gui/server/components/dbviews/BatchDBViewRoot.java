package org.pikater.web.vaadin.gui.server.components.dbviews;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.pikater.core.agents.gateway.WebToCoreEntryPoint;
import org.pikater.shared.database.views.tableview.base.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.base.ITableColumn;
import org.pikater.shared.database.views.tableview.batches.AbstractBatchTableDBView;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBRow;
import org.pikater.shared.logging.PikaterLogger;
import org.pikater.shared.util.IOUtils;
import org.pikater.web.HttpContentType;
import org.pikater.web.config.ServerConfigurationInterface;
import org.pikater.web.sharedresources.ResourceExpiration;
import org.pikater.web.sharedresources.ResourceRegistrar;
import org.pikater.web.sharedresources.download.IDownloadResource;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.AbstractDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextField;

public class BatchDBViewRoot<V extends AbstractBatchTableDBView> extends AbstractDBViewRoot<V>
{
	public BatchDBViewRoot(V view)
	{
		super(view);
	}

	@Override
	public int getColumnSize(ITableColumn column)
	{
		AbstractBatchTableDBView.Column specificColumn = (AbstractBatchTableDBView.Column) column;
		switch(specificColumn)
		{
			case FINISHED:
			case CREATED:
				return 75;
			
			case MAX_PRIORITY:
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
	public ITableColumn getExpandColumn()
	{
		return AbstractBatchTableDBView.Column.NOTE;
	}
	
	@Override
	public void onCellCreate(ITableColumn column, AbstractComponent component)
	{
		AbstractBatchTableDBView.Column specificColumn = (AbstractBatchTableDBView.Column) column;
		if(specificColumn == AbstractBatchTableDBView.Column.NOTE)
		{
			TextField tf_value = (TextField) component;
			tf_value.setDescription(tf_value.getValue());
		}
	}

	@Override
	public void approveAction(ITableColumn column, AbstractTableRowDBView row, Runnable action)
	{
		BatchTableDBRow specificRow = (BatchTableDBRow) row;
		
		AbstractBatchTableDBView.Column specificColumn = (AbstractBatchTableDBView.Column) column;
		if(specificColumn == AbstractBatchTableDBView.Column.ABORT)
		{
			if(ServerConfigurationInterface.getConfig().coreEnabled)
			{
				try
				{
					WebToCoreEntryPoint.notify_killBatch(specificRow.getBatch().getId());
				}
				catch (Throwable e)
				{
					PikaterLogger.logThrowable(String.format("Could not kill batch '%d':", specificRow.getBatch().getId()), e);
					MyNotifications.showApplicationError();
				}
			}
			else
			{
				GeneralDialogs.info("Core not available at this moment", "No experiment can be aborted.");
			}
		}
		else if(specificColumn == AbstractBatchTableDBView.Column.RESULTS)
		{
			// TODO: progress dialog
			
			// download, don't run action
			final BatchTableDBRow rowView = (BatchTableDBRow) row;
			final File tmpFile = IOUtils.createTemporaryFile("results", ".csv");
			rowView.getBatch().toCSV(tmpFile);
			UUID resultsDownloadResourceUI = ResourceRegistrar.registerResource(VaadinSession.getCurrent(), new IDownloadResource()
			{
				@Override
				public ResourceExpiration getLifeSpan()
				{
					return ResourceExpiration.ON_FIRST_PICKUP;
				}

				@Override
				public InputStream getStream() throws Throwable
				{
					return new FileInputStream(tmpFile);
				}

				@Override
				public long getSize()
				{
					return tmpFile.length();
				}

				@Override
				public String getMimeType()
				{
					return HttpContentType.TEXT_CSV.toString();
				}

				@Override
				public String getFilename()
				{
					return tmpFile.getName();
				}
			});
			Page.getCurrent().setLocation(ResourceRegistrar.getDownloadURL(resultsDownloadResourceUI));
		}
		else
		{
			throw new IllegalStateException(String.format("Action '%s' has to be approved before being executed", specificColumn.name())); 
		}
	}
}