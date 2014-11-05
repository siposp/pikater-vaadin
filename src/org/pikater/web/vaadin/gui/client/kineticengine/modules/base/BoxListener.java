package org.pikater.web.vaadin.gui.client.kineticengine.modules.base;

import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;

/**
 * General template for listeners attached to a box.
 * 
 * @author SkyCrawl
 */
public abstract class BoxListener extends ModuleEventListener {
	private final BoxGraphItemClient parentBox;

	public BoxListener(BoxGraphItemClient parentBox) {
		this.parentBox = parentBox;
	}

	protected BoxGraphItemClient getEventSourceBox() {
		return parentBox;
	}
}