package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.batches.compare;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.util.Tuple;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogComponent;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogResultHandler;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogTaskResult;
import org.pikater.web.vaadin.gui.server.components.wizards.WizardForDialog;
import org.pikater.web.vaadin.gui.server.components.wizards.steps.ParentAwareWizardStep;
import org.pikater.web.vaadin.gui.server.ui_visualization.VisualizationUI.DSVisTwoUIArgs;
import org.pikater.web.visualisation.DatasetVisualizationEntryPoint;
import org.pikater.web.visualisation.DatasetVisualizationValidation;
import org.pikater.web.visualisation.definition.AttrComparisons;
import org.pikater.web.visualisation.definition.AttrMapping;
import org.pikater.web.visualisation.definition.result.DSVisTwoResult;

import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Wizard to be displayed in a dialog, guiding users through setup of
 * experiment results comparison.
 * 
 * @author SkyCrawl
 */
public class ResultCompareWizard extends WizardForDialog<ResultCompareCommons> implements IDialogComponent {
	private static final long serialVersionUID = -2782484084003504941L;

	private Set<AttrMapping> attrComps_original;
	private Set<AttrMapping> attrComps_compared;
	private final AttrComparisons attrComparisons;

	public ResultCompareWizard(JPADataSetLO resultDataset, JPADataSetLO inputDataset) {
		super(new ResultCompareCommons(resultDataset, inputDataset));
		setRefreshActivatedSteps(true);

		this.attrComps_original = null;
		this.attrComps_compared = null;
		this.attrComparisons = new AttrComparisons();

		addStep(new Step1(this));
		addStep(new Step2(this));
	}

	@Override
	public boolean isResultReadyToBeHandled() {
		// determine attribute sets to (potentially) compare
		attrComps_original = getCompatibleAttributes(getOutput().getResultDatasetForm().getSelectedAttributes(), getOutput().getResultDatasetForm()
				.getSelectedTargetAttribute());
		attrComps_compared = getCompatibleAttributes(getOutput().getInputDatasetForm().getSelectedAttributes(), getOutput().getInputDatasetForm()
				.getSelectedTargetAttribute());

		// determine attribute pairs to compare
		attrComparisons.clear();
		for (AttrMapping mapping1 : attrComps_original) {
			for (AttrMapping mapping2 : attrComps_compared) {
				if (DatasetVisualizationValidation.areCompatible(mapping1, mapping2)) {
					attrComparisons.add(new Tuple<AttrMapping, AttrMapping>(mapping1, mapping2));
				}
			}
		}
		if (!attrComparisons.isEmpty()) {
			return true;
		} else {
			MyNotifications.showWarning("Nothing to compare", "No compatible mappings found.");
			return false;
		}
	}

	@Override
	public void addArgs(List<Object> arguments) {
	}

	@Override
	public boolean handleResult(Object[] args) {
		// show progress dialog
		ProgressDialog.show("Vizualization progress...", new ProgressDialog.IProgressDialogTaskHandler() {
			private DatasetVisualizationEntryPoint underlyingTask;

			@Override
			public void startTask(IProgressDialogResultHandler contextForTask) throws Exception {
				// start the task and bind it with the progress dialog
				underlyingTask = new DatasetVisualizationEntryPoint(contextForTask);
				underlyingTask.visualizeDatasetComparison(getOutput().getResultDataset(), getOutput().getInputDataset(), attrComparisons);
			}

			@Override
			public void abortTask() {
				underlyingTask.abort();
			}

			@Override
			public void onTaskFinish(IProgressDialogTaskResult result) {
				// and when the task finishes, construct the UI
				DSVisTwoUIArgs uiArgs = new DSVisTwoUIArgs(getOutput().getResultDataset(), getOutput().getInputDataset(), (DSVisTwoResult) result);
				Page.getCurrent().setLocation(uiArgs.toRedirectURL());
			}
		});
		return true;
	}

	private Set<AttrMapping> getCompatibleAttributes(JPAAttributeMetaData[] selectedAttrs, JPAAttributeMetaData attrTarget) {
		Set<AttrMapping> attrComps = new HashSet<AttrMapping>();
		for (JPAAttributeMetaData attrX : selectedAttrs) {
			for (JPAAttributeMetaData attrY : selectedAttrs) {
				AttrMapping attributes = new AttrMapping(attrX, attrY, attrTarget);
				if (DatasetVisualizationValidation.isCompatible(attributes)) {
					attrComps.add(attributes);
				}
			}
		}
		return attrComps;
	}

	//--------------------------------------------------------------
	// INDIVIDUAL STEPS

	private class Step1 extends ParentAwareWizardStep<ResultCompareCommons, ResultCompareWizard> {
		private final VerticalLayout vLayout;

		public Step1(ResultCompareWizard parentWizard) {
			super(parentWizard);

			this.vLayout = new VerticalLayout();
			this.vLayout.setSizeFull();
			this.vLayout.setSpacing(true);

			Label label = new Label("First select attributes to be compared for result dataset:");
			label.setSizeUndefined();
			label.setStyleName("v-label-undefWidth-wordWrap");

			this.vLayout.addComponent(label);
			this.vLayout.addComponent(getOutput().getResultDatasetForm());
			this.vLayout.setExpandRatio(getOutput().getResultDatasetForm(), 1);
		}

		@Override
		public String getCaption() {
			return "Select attributes...";
		}

		@Override
		public Component getContent() {
			return vLayout;
		}

		@Override
		public boolean onAdvance() {
			return true;
		}

		@Override
		public boolean onBack() {
			return false;
		}
	}

	private class Step2 extends ParentAwareWizardStep<ResultCompareCommons, ResultCompareWizard> {
		private final VerticalLayout vLayout;

		public Step2(ResultCompareWizard parentWizard) {
			super(parentWizard);

			this.vLayout = new VerticalLayout();
			this.vLayout.setSizeFull();
			this.vLayout.setSpacing(true);

			Label label = new Label("Then select attributes to be compared for input dataset:");
			label.setSizeUndefined();
			label.setStyleName("v-label-undefWidth-wordWrap");

			this.vLayout.addComponent(label);
			this.vLayout.addComponent(getOutput().getInputDatasetForm());
			this.vLayout.setExpandRatio(getOutput().getInputDatasetForm(), 1);
		}

		@Override
		public String getCaption() {
			return "Select attributes...";
		}

		@Override
		public Component getContent() {
			return vLayout;
		}

		@Override
		public boolean onAdvance() {
			return false;
		}

		@Override
		public boolean onBack() {
			return true;
		}
	}
}