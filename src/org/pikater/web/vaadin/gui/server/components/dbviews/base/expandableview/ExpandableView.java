package org.pikater.web.vaadin.gui.server.components.dbviews.base.expandableview;

import org.pikater.web.vaadin.gui.server.components.wizards.IWizardCommon;
import org.pikater.web.vaadin.gui.server.components.wizards.WizardWithDynamicSteps;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.IContentComponent;

/**
 * Abstract class for expandable tables (one row expands into another
 * table).
 * 
 * @author SkyCrawl
 */
public abstract class ExpandableView extends WizardWithDynamicSteps<IWizardCommon> implements IContentComponent {
	private static final long serialVersionUID = -7316996184841925957L;

	public ExpandableView() {
		super(new IWizardCommon() {
		}, false);
		getNextButton().setEnabled(false); // first step is added via an abstract method
		setContentPadding(true);
	}
}