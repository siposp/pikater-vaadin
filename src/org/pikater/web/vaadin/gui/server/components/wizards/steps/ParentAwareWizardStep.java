package org.pikater.web.vaadin.gui.server.components.wizards.steps;

import org.pikater.web.vaadin.gui.server.components.wizards.IWizardCommon;
import org.pikater.web.vaadin.gui.server.components.wizards.WizardWithOutput;
import org.vaadin.teemu.wizards.WizardStep;

/**
 * Wizard step for general use (by {@link WizardWithOutput} or its descendants).
 * 
 * @author SkyCrawl
 *
 */
public abstract class ParentAwareWizardStep<O extends IWizardCommon, W extends WizardWithOutput<O>> implements WizardStep {
	private final W parentWizard;

	public ParentAwareWizardStep(W parentWizard) {
		this.parentWizard = parentWizard;
	}

	public W getParentWizard() {
		return parentWizard;
	}

	public O getOutput() {
		return parentWizard.getOutput();
	}
}
