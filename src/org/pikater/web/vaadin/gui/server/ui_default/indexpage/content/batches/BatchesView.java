package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.batches;

import org.pikater.shared.database.jpa.JPABatch;
import org.pikater.shared.database.jpa.JPAExperiment;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBView;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBViewAll;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBRow;
import org.pikater.shared.database.views.tableview.batches.experiments.ExperimentTableDBRow;
import org.pikater.shared.database.views.tableview.batches.experiments.ExperimentTableDBView;
import org.pikater.shared.database.views.tableview.batches.experiments.results.ResultTableDBViewExperiment;
import org.pikater.web.vaadin.gui.server.components.dbviews.BatchDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.ExperimentDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.ResultDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.expandableview.ExpandableView;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.expandableview.ExpandableViewStep;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTableLayout;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.wizards.IWizardCommon;
import org.pikater.web.vaadin.gui.server.components.wizards.WizardWithDynamicSteps;
import org.pikater.web.vaadin.gui.server.components.wizards.steps.DynamicNeighbourWizardStep;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;

/**
 * View implementing the administrator batch feature.
 * 
 * @author SkyCrawl
 * 
 * @see {@link DefaultUI}
 * @see {@link ContentProvider}
 */
public class BatchesView extends ExpandableView {
	private static final long serialVersionUID = 5847910505253647132L;

	public BatchesView() {
		super();
		setSizeUndefined();
		setWidth("100%");
	}

	//----------------------------------------------------
	// VIEW INTERFACE

	@Override
	public void enter(ViewChangeEvent event) {
		super.finishInit(); // don't forget to!
	}

	@Override
	public boolean isReadyToClose() {
		return true;
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
		return new BatchStep(this, new BatchTableDBViewAll());
	}

	//----------------------------------------------------------------------------
	// INDIVIDUAL STEPS OF THIS VIEW

	protected class BatchStep extends ExpandableViewStep {
		private final DBTableLayout innerLayout;

		public BatchStep(WizardWithDynamicSteps<IWizardCommon> parentWizard, BatchTableDBView dbView) {
			super(parentWizard, false);

			this.innerLayout = new DBTableLayout();
			this.innerLayout.getTable().setMultiSelect(false); // this is required below
			this.innerLayout.setView(new BatchDBViewRoot<BatchTableDBView>(dbView));
			registerDBViewLayout(this.innerLayout);
		}

		@Override
		public String getCaption() {
			return "Batches overview";
		}

		@Override
		public Component getContent() {
			return innerLayout;
		}

		@Override
		public ExpandableViewStep constructNextStep() {
			if (!innerLayout.getTable().isARowSelected()) {
				MyNotifications.showWarning(null, "No table row (batch) is selected.");
				return null;
			} else {
				// this assumes single select mode
				AbstractTableRowDBView[] selectedRowsViews = innerLayout.getTable().getViewsOfSelectedRows();
				return constructNextStepFromView(selectedRowsViews[0]);
			}
		}

		@Override
		protected ExpandableViewStep constructNextStepFromView(AbstractTableRowDBView view) {
			BatchTableDBRow specificView = (BatchTableDBRow) view;
			return new ExperimentStep(BatchesView.this, specificView.getBatch());
		}
	}

	protected class ExperimentStep extends ExpandableViewStep {
		private final DBTableLayout innerLayout;

		public ExperimentStep(WizardWithDynamicSteps<IWizardCommon> parentWizard, JPABatch batch) {
			super(parentWizard, false);

			this.innerLayout = new DBTableLayout();
			this.innerLayout.getTable().setMultiSelect(false); // this is required below
			this.innerLayout.setReadOnly(true);
			this.innerLayout.setView(new ExperimentDBViewRoot<ExperimentTableDBView>(new ExperimentTableDBView(batch.getOwner(), batch)));
			registerDBViewLayout(this.innerLayout);
		}

		@Override
		public String getCaption() {
			return "Experiments overview";
		}

		@Override
		public Component getContent() {
			return innerLayout;
		}

		@Override
		public ExpandableViewStep constructNextStep() {
			if (!innerLayout.getTable().isARowSelected()) {
				MyNotifications.showWarning(null, "No table row (experiment) is selected.");
				return null;
			} else {
				// this assumes single select mode
				AbstractTableRowDBView[] selectedRowsViews = innerLayout.getTable().getViewsOfSelectedRows();
				return constructNextStepFromView(selectedRowsViews[0]);
			}
		}

		@Override
		protected ExpandableViewStep constructNextStepFromView(AbstractTableRowDBView view) {
			ExperimentTableDBRow selectedExperimentView = (ExperimentTableDBRow) view;
			return new ResultStep(BatchesView.this, selectedExperimentView.getExperiment());
		}
	}

	protected class ResultStep extends ExpandableViewStep {
		private final DBTableLayout innerLayout;

		public ResultStep(WizardWithDynamicSteps<IWizardCommon> parentWizard, JPAExperiment experiment) {
			super(parentWizard, true);

			this.innerLayout = new DBTableLayout();
			this.innerLayout.setReadOnly(true);
			this.innerLayout.setView(new ResultDBViewRoot<ResultTableDBViewExperiment>(new ResultTableDBViewExperiment(experiment)));
			// registerDBViewLayout(this.innerLayout); // POINTLESS (leaf)
		}

		@Override
		public String getCaption() {
			return "Results overview";
		}

		@Override
		public Component getContent() {
			return innerLayout;
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