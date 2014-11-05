package org.pikater.web.quartzjobs.results;

import java.io.File;
import java.io.FileOutputStream;

import org.pikater.shared.database.jpa.JPAExperiment;
import org.pikater.shared.database.jpa.JPAResult;
import org.pikater.shared.database.util.ResultExporter;
import org.pikater.shared.quartz.jobs.base.InterruptibleImmediateOneTimeJob;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogResultHandler;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionException;

/**
 * Background task that exports batch results into a human-readable format.
 * 
 * @author SkyCrawl
 */
public class ExportExperimentResultsJob extends
		InterruptibleImmediateOneTimeJob {
	public ExportExperimentResultsJob() {
		super(3);
	}

	@Override
	public boolean argumentCorrect(Object argument, int argIndex) {
		switch (argIndex) {
			case 0:
				return argument instanceof JPAExperiment;
			case 1:
				return argument instanceof File;
			case 2:
				return argument instanceof IProgressDialogResultHandler;

			default:
				return false;
		}
	}

	@Override
	public void buildJob(JobBuilder builder) {
	}

	@Override
	protected void execute() throws JobExecutionException {
		// information from GUI
		JPAExperiment experiment = getArg(0);
		File resultFile = getArg(1);
		IProgressDialogResultHandler resultHandler = getArg(2);

		// the actual action
		try {
			ResultExporter re = new ResultExporter(new FileOutputStream(
					resultFile));

			re.header((JPAExperiment) null, (JPAResult) null);

			float resultsExported = 0;
			float resultCount = experiment.getResults().size();
			for (JPAResult result : experiment.getResults()) {
				re.row(experiment, result);

				resultsExported++;
				resultHandler.updateProgress(resultsExported / resultCount);
			}

			re.close();
			resultHandler.finished(null);
		} catch (Exception e) {
			PikaterWebLogger.logThrowable(
					"Job could not finish because of the following error:", e);
			resultHandler.failed(); // don't forget to... important cleanup will
									// take place
		} finally {
			// generated temporary files will be deleted when the JVM exits
		}
	}
}