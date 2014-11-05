package org.pikater.web.vaadin.gui.server.components.wizards.steps;

import org.pikater.web.vaadin.gui.server.components.wizards.IWizardCommon;
import org.pikater.web.vaadin.gui.server.components.wizards.WizardWithOutput;

/**
 * Wizard step for general use (see {@link ParentAwareWizardStep}), that
 * may be refreshed when activated (if the parent wizard is set to do so).
 * 
 * @author SkyCrawl
 *
 */
public abstract class RefreshableWizardStep<O extends IWizardCommon, W extends WizardWithOutput<O>> extends ParentAwareWizardStep<O, W> {
	public RefreshableWizardStep(W parentWizard) {
		super(parentWizard);
	}

	public abstract void refresh();
}
