package org.pikater.web.vaadin.gui.server.components.wizards.steps;

import org.pikater.web.vaadin.gui.server.components.wizards.IWizardCommon;
import org.pikater.web.vaadin.gui.server.components.wizards.WizardWithDynamicSteps;

/**
 * Wizard step to be exclusively used by {@link WizardWithDynamicSteps}.
 * 
 * @author SkyCrawl
 *
 */
public abstract class DynamicNeighbourWizardStep<O extends IWizardCommon, W extends WizardWithDynamicSteps<O>> extends ParentAwareWizardStep<O, W> {
	private final boolean isLeaf;

	public DynamicNeighbourWizardStep(W parentWizard, boolean isLeaf) {
		super(parentWizard);

		this.isLeaf = isLeaf;
	}

	//----------------------------------------------------
	// INHERITED INTERFACE

	@Override
	public boolean onBack() {
		return true;
	}

	/**
	 * <font color="red">RED ALERT:</font> this is a "system" method, called in ActiveStepChanged event etc.</br>
	 * Unless you want to suffer from heap out of memory error, don't add or activate steps in here.
	 */
	@Override
	public boolean onAdvance() {
		return true;
	}

	//----------------------------------------------------
	// OTHER PUBLIC INTERFACE

	/**
	 * Whether this step doesn't construct any further steps.
	 */
	public boolean isLeaf() {
		return isLeaf;
	}

	//----------------------------------------------------
	// ABSTRACT INTERFACE

	public abstract DynamicNeighbourWizardStep<O, W> constructNextStep();
}
