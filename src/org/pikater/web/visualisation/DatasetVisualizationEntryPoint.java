package org.pikater.web.visualisation;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.quartz.jobs.InterruptibleJobHelper;
import org.pikater.web.quartzjobs.visualization.DSVisOneGeneratorJob;
import org.pikater.web.quartzjobs.visualization.DSVisTwoGeneratorJob;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogResultHandler;
import org.pikater.web.visualisation.definition.AttrComparisons;
import org.pikater.web.visualisation.definition.task.IDSVisOne;
import org.pikater.web.visualisation.definition.task.IDSVisTwo;

/**
 * <p>Base class for any interaction with the visualization package from outside.</p>
 * 
 * <p>At present time, this class only triggers visualization background tasks.</p>
 * 
 * @author SkyCrawl
 */
public class DatasetVisualizationEntryPoint extends InterruptibleJobHelper implements IDSVisOne, IDSVisTwo {
	private final IProgressDialogResultHandler context;

	public DatasetVisualizationEntryPoint(IProgressDialogResultHandler context) {
		this.context = context;
	}

	@Override
	public void visualizeDataset(JPADataSetLO dataset, JPAAttributeMetaData[] attrs, JPAAttributeMetaData attrTarget) throws Exception {
		start(DSVisOneGeneratorJob.class, new Object[] { dataset, attrs, attrTarget, context });
	}

	@Override
	public void visualizeDatasetComparison(JPADataSetLO dataset1, JPADataSetLO dataset2, AttrComparisons attrsToCompare) throws Exception {
		start(DSVisTwoGeneratorJob.class, new Object[] { dataset1, dataset2, attrsToCompare, context });
	}
}