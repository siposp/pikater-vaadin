package org.pikater.web.visualisation;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.logging.PikaterLogger;
import org.pikater.shared.quartz.PikaterJobScheduler;
import org.pikater.web.quartzjobs.visualization.DSVisTwoGeneratorJob;
import org.pikater.web.quartzjobs.visualization.DSVisOneGeneratorJob;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogResultHandler;
import org.pikater.web.visualisation.definition.AttrComparisons;
import org.pikater.web.visualisation.definition.task.IDSVisOne;
import org.pikater.web.visualisation.definition.task.IDSVisTwo;
import org.quartz.JobKey;

public class DatasetVisualizationEntryPoint implements IDSVisOne, IDSVisTwo
{
	private final IProgressDialogResultHandler context;
	private JobKey jobKey;
	
	public DatasetVisualizationEntryPoint(IProgressDialogResultHandler context)
	{
		this.context = context;
		this.jobKey = null;
	}
	
	@Override
	public void visualizeDataset(JPADataSetLO dataset, JPAAttributeMetaData[] attrs, JPAAttributeMetaData attrTarget) throws Throwable
	{
		jobKey = PikaterJobScheduler.getJobScheduler().defineJob(DSVisOneGeneratorJob.class, new Object[]
		{
			dataset,
			attrs,
			attrTarget,
			context
		});
	}
	
	@Override
	public void visualizeDatasetComparison(JPADataSetLO dataset1, JPADataSetLO dataset2, AttrComparisons attrsToCompare) throws Throwable
	{
		jobKey = PikaterJobScheduler.getJobScheduler().defineJob(DSVisTwoGeneratorJob.class, new Object[]
		{
			dataset1,
			dataset2,
			attrsToCompare,
			context
		});
	}
	
	public void abortVisualization()
	{
		if(jobKey == null)
		{
			throw new IllegalStateException("Can not abort a task that has not started.");
		}
		else
		{
			try
			{
				PikaterJobScheduler.getJobScheduler().interruptJob(jobKey);
			}
			catch (Throwable t)
			{
				PikaterLogger.logThrowable(String.format("Could not interrupt job: '%s'. What now?", jobKey.toString()), t);
			}
		}
	}
}