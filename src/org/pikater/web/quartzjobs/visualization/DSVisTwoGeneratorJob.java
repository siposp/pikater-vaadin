package org.pikater.web.quartzjobs.visualization;

import java.io.File;
import java.io.PrintStream;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.database.postgre.largeobject.IPGLOActionContext;
import org.pikater.shared.database.postgre.largeobject.PGLargeObjectAction;
import org.pikater.shared.quartz.jobs.base.InterruptibleImmediateOneTimeJob;
import org.pikater.shared.util.Tuple;
import org.pikater.web.ImageType;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogResultHandler;
import org.pikater.web.visualisation.definition.AttrComparisons;
import org.pikater.web.visualisation.definition.AttrMapping;
import org.pikater.web.visualisation.definition.result.DSVisTwoResult;
import org.pikater.web.visualisation.definition.result.DSVisTwoSubresult;
import org.pikater.web.visualisation.definition.task.IDSVisTwo;
import org.pikater.web.visualisation.implementation.exceptions.MetadataNotPresentException;
import org.pikater.web.visualisation.implementation.generator.ComparisonPNGGenerator;
import org.pikater.web.visualisation.implementation.generator.base.Generator;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionException;

/**
 * Background task that generates visualization images comparing two datasets.
 * 
 * @author SkyCrawl
 */
public class DSVisTwoGeneratorJob extends InterruptibleImmediateOneTimeJob
		implements IDSVisTwo, IPGLOActionContext {
	private IProgressDialogResultHandler context;

	public DSVisTwoGeneratorJob() {
		super(4);
	}

	@Override
	public boolean argumentCorrect(Object argument, int argIndex) {
		switch (argIndex) {
			case 0:
			case 1:
				return argument instanceof JPADataSetLO;
			case 2:
				return argument instanceof AttrComparisons;
			case 3:
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
		JPADataSetLO dataset1 = getArg(0);
		JPADataSetLO dataset2 = getArg(1);
		AttrComparisons comparisonList = getArg(2);
		context = getArg(3);
		visualizeDatasetComparison(dataset1, dataset2, comparisonList);
	}

	@Override
	public boolean isInterrupted() {
		return super.isInterrupted();
	}

	@Override
	public void visualizeDatasetComparison(JPADataSetLO dataset1,
			JPADataSetLO dataset2, AttrComparisons comparisonList) {
		DSVisTwoResult result = new DSVisTwoResult(context,
				Generator.DEFAULTCHARTSIZE, Generator.DEFAULTCHARTSIZE);
		try {
			if (dataset1.getHash().equals(dataset2.getHash())) {
				throw new IllegalArgumentException(
						"Identical datasets were received for comparison.");
			} else if (!dataset1.hasComputedMetadata()) {
				throw new MetadataNotPresentException(dataset1.getDescription());
			} else if (!dataset2.hasComputedMetadata()) {
				throw new MetadataNotPresentException(dataset2.getDescription());
			}

			PGLargeObjectAction downloadAction = new PGLargeObjectAction(this);
			File datasetCachedFile1 = downloadAction.downloadLOFromDB(dataset1
					.getOID());
			File datasetCachedFile2 = downloadAction.downloadLOFromDB(dataset2
					.getOID());

			float subresultsGenerated = 0;
			float finalCountOfSubresults = comparisonList.size();
			for (Tuple<AttrMapping, AttrMapping> attrsToCompare : comparisonList) {
				// interrupt generation when the user commands it
				if (isInterrupted()) {
					return;
				}

				// otherwise continue generating
				DSVisTwoSubresult imageResult = result
						.createAndRegisterSubresult(attrsToCompare,
								ImageType.PNG);
				new ComparisonPNGGenerator(
						null, // no need to pass in progress listener - progress
								// is updated below
						new PrintStream(imageResult.getFile()), dataset1,
						dataset2, datasetCachedFile1, datasetCachedFile2,
						attrsToCompare.getValue1().getAttrX().getName(),
						attrsToCompare.getValue2().getAttrX().getName(),
						attrsToCompare.getValue1().getAttrY().getName(),
						attrsToCompare.getValue2().getAttrY().getName(),
						attrsToCompare.getValue1().getAttrTarget().getName(),
						attrsToCompare.getValue2().getAttrTarget().getName())
						.create();
				subresultsGenerated++;
				result.updateProgress(subresultsGenerated
						/ finalCountOfSubresults);
			}
			result.finished();
		} catch (InterruptedException e) {
			// user interrupted visualization, don't log
			result.failed(); // don't forget to... important cleanup will take
								// place
		} catch (Exception e) {
			PikaterWebLogger.logThrowable(
					"Job could not finish because of the following error:", e);
			result.failed(); // don't forget to... important cleanup will take
								// place
		} finally {
			// generated temporary files will be deleted when the JVM exits
		}
	}
}