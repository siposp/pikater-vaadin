package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.batches.compare;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.vaadin.gui.server.components.forms.DatasetVisualizationForm;
import org.pikater.web.vaadin.gui.server.components.wizards.IWizardCommon;

/**
 * Class encapsulating all information required to compare
 * experiment results.
 * 
 * @author SkyCrawl
 */
class ResultCompareCommons implements IWizardCommon {
	private final JPADataSetLO dataset_result;
	private final JPADataSetLO dataset_input;
	private final DatasetVisualizationForm form_result;
	private final DatasetVisualizationForm form_input;

	public ResultCompareCommons(JPADataSetLO dataset_result, JPADataSetLO dataset_input) {
		this.dataset_result = dataset_result;
		this.dataset_input = dataset_input;
		this.form_result = new DatasetVisualizationForm(dataset_result);
		this.form_input = new DatasetVisualizationForm(dataset_input);
	}

	public JPADataSetLO getResultDataset() {
		return dataset_result;
	}

	public JPADataSetLO getInputDataset() {
		return dataset_input;
	}

	public DatasetVisualizationForm getResultDatasetForm() {
		return form_result;
	}

	public DatasetVisualizationForm getInputDatasetForm() {
		return form_input;
	}
}