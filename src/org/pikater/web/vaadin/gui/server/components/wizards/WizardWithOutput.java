package org.pikater.web.vaadin.gui.server.components.wizards;

import org.pikater.web.vaadin.gui.server.components.wizards.steps.RefreshableWizardStep;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.Button;

/**
 * Our own version of a wizard taken from the Wizards Vaadin add-on, that uses a
 * global "state" object (and is thus capable of producing an "output") and is
 * capable of refreshing the newly activated steps, if set to do so (steps may
 * then be a result of previous steps, for example).
 * 
 * @author SkyCrawl
 * 
 * @param <O>
 *            The wizard's "state" class.
 */
@StyleSheet("wizards.css")
public class WizardWithOutput<O extends IWizardCommon> extends Wizard {
	private static final long serialVersionUID = -673702186069757491L;

	private final O output;
	private boolean refreshActivatedSteps;

	public WizardWithOutput(O output) {
		super();
		setSizeUndefined();
		setStyleName("myWizard");

		this.output = output;
		this.refreshActivatedSteps = false;

		addListener(new WizardProgressListener() {
			@Override
			public void wizardCompleted(WizardCompletedEvent event) {
			}

			@Override
			public void wizardCancelled(WizardCancelledEvent event) {
			}

			@Override
			public void stepSetChanged(WizardStepSetChangedEvent event) {
			}

			@Override
			public void activeStepChanged(WizardStepActivationEvent event) {
				if (refreshActivatedSteps
						&& (event.getActivatedStep() instanceof RefreshableWizardStep<?, ?>)) {
					((RefreshableWizardStep<?, ?>) event.getActivatedStep())
							.refresh();
				}
			}
		});
	}

	public O getOutput() {
		return output;
	}

	public void setRefreshActivatedSteps(boolean refresh) {
		this.refreshActivatedSteps = refresh;
	}

	public void setContentPadding(boolean enabled) {
		if (enabled) {
			addStyleName("contentPadding");
		} else {
			removeStyleName("contentPadding");
		}
	}

	protected static void setButtonUsed(Button btn, boolean used) {
		btn.setEnabled(used);
		btn.setVisible(used);
	}
}