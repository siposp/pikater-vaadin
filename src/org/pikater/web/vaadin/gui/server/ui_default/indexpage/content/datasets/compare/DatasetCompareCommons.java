package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets.compare;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.vaadin.gui.server.components.forms.DatasetVisualizationForm;
import org.pikater.web.vaadin.gui.server.components.wizards.IWizardCommon;

/**
 * Class encapsulating all information required to compare
 * datasets.
 * 
 * @author SkyCrawl
 */
class DatasetCompareCommons implements IWizardCommon {
	private final JPADataSetLO dataset_original;
	private final DatasetVisualizationForm form_original;

	private JPADataSetLO dataset_compareTo;
	private DatasetVisualizationForm form_compareTo;

	public DatasetCompareCommons(JPADataSetLO dataset_original) {
		this.dataset_original = dataset_original;
		this.form_original = new DatasetVisualizationForm(dataset_original);

		this.dataset_compareTo = null;
		this.form_compareTo = null;
	}

	public JPADataSetLO getDatasetOriginal() {
		return dataset_original;
	}

	public DatasetVisualizationForm getFormOriginal() {
		return form_original;
	}

	public JPADataSetLO getDatasetCompareTo() {
		return dataset_compareTo;
	}

	public void setDatasetCompareTo(JPADataSetLO dataset) {
		this.dataset_compareTo = dataset;
		this.form_compareTo = dataset != null ? new DatasetVisualizationForm(dataset) : null;
	}

	public DatasetVisualizationForm getFormCompareTo() {
		return form_compareTo;
	}
}