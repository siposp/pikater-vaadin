package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.datasets.DataSetTableDBRow;
import org.pikater.shared.database.views.tableview.datasets.DataSetTableDBView;
import org.pikater.shared.database.views.tableview.datasets.metadata.CategoricalMetaDataTableDBView;
import org.pikater.shared.database.views.tableview.datasets.metadata.NumericalMetaDataTableDBView;
import org.pikater.web.vaadin.gui.server.components.dbviews.CategoricalMetadataDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.DatasetDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.NumericalMetadataDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.expandableview.ExpandableView;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.expandableview.ExpandableViewStep;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTableLayout;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.wizards.IWizardCommon;
import org.pikater.web.vaadin.gui.server.components.wizards.WizardWithDynamicSteps;
import org.pikater.web.vaadin.gui.server.components.wizards.steps.DynamicNeighbourWizardStep;
import org.pikater.web.vaadin.gui.server.layouts.SimplePanel;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;

/**
 * View implementing the administrator dataset feature.
 * 
 * @author SkyCrawl
 * 
 * @see {@link DefaultUI}
 * @see {@link ContentProvider}
 */
public class DatasetsView extends ExpandableView {
	private static final long serialVersionUID = -1564809345462937610L;

	private final DBTableLayout mainDatasetsLayout;

	public DatasetsView() {
		super();
		setSizeUndefined();
		setWidth("100%");

		this.mainDatasetsLayout = new DBTableLayout();
		this.mainDatasetsLayout.setSizeFull();
		this.mainDatasetsLayout.getTable().setMultiSelect(false); // this is required below
	}

	//----------------------------------------------------
	// VIEW INTERFACE

	@Override
	public void enter(ViewChangeEvent event) {
		// always call these last, when you're absolutely ready to display the content
		this.mainDatasetsLayout.setView(new DatasetDBViewRoot<DataSetTableDBView>(new DataSetTableDBView()));
		super.finishInit(); // don't forget to!
	}

	@Override
	public boolean isReadyToClose() {
		return true; // datasets are completely read-only, except for adding new datasets which is independent of Vaadin anyway
	}

	@Override
	public String getCloseMessage() {
		return null;
	}

	@Override
	public void beforeClose() {
	}

	//----------------------------------------------------
	// OTHER INTERFACE

	@Override
	protected DynamicNeighbourWizardStep<IWizardCommon, WizardWithDynamicSteps<IWizardCommon>> createFirstStep() {
		// this is called in the constructor via super
		return new DatasetOverviewStep(this);
	}

	protected DBTableLayout getMainLayout() {
		return mainDatasetsLayout;
	}

	//----------------------------------------------------------------------------
	// INDIVIDUAL STEPS OF THIS VIEW

	protected class DatasetOverviewStep extends ExpandableViewStep {
		public DatasetOverviewStep(WizardWithDynamicSteps<IWizardCommon> parentWizard) {
			super(parentWizard, false);

			registerDBViewLayout(mainDatasetsLayout);
		}

		@Override
		public String getCaption() {
			return "Datasets overview";
		}

		@Override
		public DBTableLayout getContent() {
			return mainDatasetsLayout;
		}

		@Override
		public ExpandableViewStep constructNextStep() {
			if (!mainDatasetsLayout.getTable().isARowSelected()) {
				MyNotifications.showWarning(null, "No table row (dataset) is selected.");
				return null;
			} else {
				// this assumes single select mode
				AbstractTableRowDBView[] selectedRowsViews = mainDatasetsLayout.getTable().getViewsOfSelectedRows();
				return constructNextStepFromView(selectedRowsViews[0]);
			}
		}

		@Override
		protected ExpandableViewStep constructNextStepFromView(AbstractTableRowDBView view) {
			DataSetTableDBRow selectedDatasetView = (DataSetTableDBRow) view;
			return new DatasetDetailStep(getParentWizard(), selectedDatasetView.getDataset());
		}
	}

	protected class DatasetDetailStep extends ExpandableViewStep {
		private final SimplePanel panel;

		public DatasetDetailStep(WizardWithDynamicSteps<IWizardCommon> parentWizard, JPADataSetLO dataset) {
			super(parentWizard, true);

			DBTableLayout categoricalMetadataTable = new DBTableLayout();
			categoricalMetadataTable.setReadOnly(true);
			categoricalMetadataTable.setPagingPadding(true);
			categoricalMetadataTable.setView(new CategoricalMetadataDBViewRoot(new CategoricalMetaDataTableDBView(dataset)));

			DBTableLayout numericalMetadataTable = new DBTableLayout();
			numericalMetadataTable.setReadOnly(true);
			numericalMetadataTable.setPagingPadding(true);
			numericalMetadataTable.setView(new NumericalMetadataDBViewRoot(new NumericalMetaDataTableDBView(dataset)));

			TabSheet tabSheet = new TabSheet();
			tabSheet.setSizeFull();
			tabSheet.setImmediate(true);
			tabSheet.addTab(categoricalMetadataTable, "Categorical");
			tabSheet.addTab(numericalMetadataTable, "Numerical");

			this.panel = new SimplePanel(tabSheet);
			this.panel.setSizeFull();
		}

		@Override
		public String getCaption() {
			return "Dataset metadata";
		}

		@Override
		public Component getContent() {
			return this.panel;
		}

		@Override
		public ExpandableViewStep constructNextStep() {
			return null;
		}

		@Override
		protected ExpandableViewStep constructNextStepFromView(AbstractTableRowDBView view) {
			return null;
		}
	}
}