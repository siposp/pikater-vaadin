package org.pikater.web.vaadin.gui.server.components.wizards;

/**
 * A special wizard spin-off to look better in dialogs.
 * 
 * @author SkyCrawl
 * 
 * @param <O>
 *            The wizard's "state" class.
 */
public abstract class WizardForDialog<O extends IWizardCommon> extends
		WizardWithOutput<O> {
	private static final long serialVersionUID = -6211218565088889148L;

	public WizardForDialog(O output) {
		super(output);
		setSizeFull();
		addStyleName("dialogWizard");
		setContentPadding(true);
	}
}