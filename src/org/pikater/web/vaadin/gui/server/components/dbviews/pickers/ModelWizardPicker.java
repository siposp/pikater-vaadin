package org.pikater.web.vaadin.gui.server.components.dbviews.pickers;

import java.util.List;

import org.pikater.shared.database.jpa.JPAUser;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBRow;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBViewUserScheduled;
import org.pikater.shared.database.views.tableview.batches.experiments.ExperimentTableDBRow;
import org.pikater.shared.database.views.tableview.batches.experiments.ExperimentTableDBViewMin;
import org.pikater.shared.database.views.tableview.batches.experiments.results.ResultTableDBRow;
import org.pikater.shared.database.views.tableview.batches.experiments.results.ResultTableDBViewExperimentAgent;
import org.pikater.web.vaadin.gui.server.components.dbviews.BatchDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.ExperimentDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.ResultDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.TableRowPicker;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogResultPreparer;
import org.pikater.web.vaadin.gui.server.components.wizards.WizardForDialog;
import org.pikater.web.vaadin.gui.server.components.wizards.steps.ParentAwareWizardStep;
import org.pikater.web.vaadin.gui.server.components.wizards.steps.RefreshableWizardStep;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;

/**
 * Expands on the idea of {@link TableRowPicker} by incorporating
 * wizards (a series of {@link DBTable DB tables}. User navigates
 * through individual steps/tables by selecting a row, just like
 * before and the selected row of the final step is our result.
 * 
 * @author SkyCrawl
 */
public class ModelWizardPicker extends WizardForDialog<ModelWizardPickerOutput> implements IDialogResultPreparer {
	private static final long serialVersionUID = -2782484084003504941L;

	public ModelWizardPicker(JPAUser owner, String agentClassSimpleName) {
		super(new ModelWizardPickerOutput(owner, agentClassSimpleName));
		setRefreshActivatedSteps(true);

		addStep(new Step1(this));
		addStep(new Step2(this));
		addStep(new Step3(this));
	}

	@Override
	public boolean isResultReadyToBeHandled() {
		if (getOutput().getResult() != null) {
			if (getOutput().getResult().getCreatedModel() == null) {
				MyNotifications.showError(null, "No model for selected result.");
				return false;
			}
		} else {
			MyNotifications.showError(null, "No table row (result) is selected.");
			return false;
		}
		return true;
	}

	@Override
	public void addArgs(List<Object> arguments) {
		arguments.add(getOutput());
	}

	//--------------------------------------------------------------
	// INDIVIDUAL STEPS

	private class Step1 extends ParentAwareWizardStep<ModelWizardPickerOutput, ModelWizardPicker> {
		private final TableRowPicker innerLayout;

		public Step1(ModelWizardPicker parentWizard) {
			super(parentWizard);

			this.innerLayout = new TableRowPicker("Select a row and click 'Next':");
			this.innerLayout.setView(new BatchDBViewRoot<BatchTableDBViewUserScheduled>(new BatchTableDBViewUserScheduled(getOutput().getOwner())));
			this.innerLayout.setSizeFull();
		}

		@Override
		public String getCaption() {
			return "Select batch...";
		}

		@Override
		public Component getContent() {
			return innerLayout;
		}

		@Override
		public boolean onAdvance() {
			AbstractTableRowDBView[] selectedViews = innerLayout.getTable().getViewsOfSelectedRows();
			if (selectedViews.length > 0) {
				BatchTableDBRow selectedView = (BatchTableDBRow) selectedViews[0];
				getOutput().setBatch(selectedView.getBatch());
				return true;
			} else {
				MyNotifications.showError(null, "No table row (batch) is selected.");
				return false;
			}
		}

		@Override
		public boolean onBack() {
			return false;
		}
	}

	private class Step2 extends RefreshableWizardStep<ModelWizardPickerOutput, ModelWizardPicker> {
		private TableRowPicker innerLayout;

		public Step2(ModelWizardPicker parentWizard) {
			super(parentWizard);
			this.innerLayout = new TableRowPicker("Select a row and click 'Next':");
			this.innerLayout.setSizeFull();
		}

		@Override
		public String getCaption() {
			return "Select experiment...";
		}

		@Override
		public Component getContent() {
			return innerLayout;
		}

		@Override
		public boolean onAdvance() {
			AbstractTableRowDBView[] selectedViews = innerLayout.getTable().getViewsOfSelectedRows();
			if (selectedViews.length > 0) {
				ExperimentTableDBRow selectedView = (ExperimentTableDBRow) selectedViews[0];
				getOutput().setExperiment(selectedView.getExperiment());
				return true;
			} else {
				MyNotifications.showError(null, "No table row (experiment) is selected.");
				return false;
			}
		}

		@Override
		public boolean onBack() {
			getOutput().setExperiment(null);
			return true;
		}

		@Override
		public void refresh() {
			this.innerLayout.setView(new ExperimentDBViewRoot<ExperimentTableDBViewMin>(new ExperimentTableDBViewMin(getOutput().getOwner(), getOutput().getBatch())));
		}
	}

	private class Step3 extends RefreshableWizardStep<ModelWizardPickerOutput, ModelWizardPicker> {
		private TableRowPicker innerLayout;

		public Step3(ModelWizardPicker parentWizard) {
			super(parentWizard);
			this.innerLayout = new TableRowPicker("Select a row and click 'Finish':");
			this.innerLayout.setSizeFull();
			this.innerLayout.getTable().addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 2551264104188838012L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					AbstractTableRowDBView[] selectedRows = innerLayout.getTable().getViewsOfSelectedRows();
					if (selectedRows.length == 1) {
						ResultTableDBRow selectedSingleRow = (ResultTableDBRow) selectedRows[0];
						getOutput().setResult(selectedSingleRow.getResult());
					} else {
						getOutput().setResult(null);
					}
				}
			});
		}

		@Override
		public String getCaption() {
			return "Select result...";
		}

		@Override
		public Component getContent() {
			return innerLayout;
		}

		@Override
		public boolean onAdvance() {
			return false;
		}

		@Override
		public boolean onBack() {
			getOutput().setResult(null);
			return true;
		}

		@Override
		public void refresh() {
			this.innerLayout.setView(new ResultDBViewRoot<ResultTableDBViewExperimentAgent>(new ResultTableDBViewExperimentAgent(getOutput().getExperiment(), getOutput().getAgentClassSimpleName())));
		}
	}
}