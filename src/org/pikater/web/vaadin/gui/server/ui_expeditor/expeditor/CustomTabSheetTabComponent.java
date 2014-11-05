package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor;

import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.customtabsheet.TabSheet;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.customtabsheet.TabSheetTabComponent;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;

/**
 * Custom tab component for {@link TabSheet}, specific to {@link ExpEditor
 * experiment editor}, associated to a certain {@link KineticComponent}. 
 * 
 * @author SkyCrawl
 */
public class CustomTabSheetTabComponent extends TabSheetTabComponent {
	private static final long serialVersionUID = 2019691593787134700L;

	private final KineticComponent contentComponent;

	public CustomTabSheetTabComponent(String caption, KineticComponent contentComponent) {
		super(caption);

		this.contentComponent = contentComponent;
		this.contentComponent.setParentTab(this);
	}

	@Override
	public boolean canCloseTab() {
		if (contentComponent.getExperimentGraph() != null) {
			// TODO: check whether content has been modified when that particular feature is supported
			return contentComponent.getExperimentGraph().isEmpty();
		} else {
			return true;
		}
	}

	public KineticComponent getContentComponent() {
		return contentComponent;
	}

	/**
	 * @deprecated Not supported at the moment.
	 */
	@Deprecated
	public void setTabContentModified(boolean modified) {
		if (modified) {
			if (!getCaption().startsWith("* ")) {
				setCaption("* " + getCaption());
			}
		} else {
			if (getCaption().startsWith("* ")) {
				setCaption(getCaption().substring(2));
			}
		}
	}
}
