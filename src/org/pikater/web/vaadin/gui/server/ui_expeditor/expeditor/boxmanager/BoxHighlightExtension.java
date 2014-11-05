package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager;

import org.pikater.web.vaadin.gui.client.extensions.BoxHighlightExtensionSharedState;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.HorizontalFlowLayout;

import com.vaadin.server.AbstractExtension;

/**
 * A special extension to arbitrary components highlighting
 * the given experiment boxes in the given experiment/
 * kinetic canvas.
 * 
 * @author SkyCrawl
 */
public class BoxHighlightExtension extends AbstractExtension {
	private static final long serialVersionUID = 3081925647057133214L;

	public BoxHighlightExtension(String kineticConnectorID, Integer[] boxesToBeHighlighted) {
		/*
		registerRpc(new BoxHighlightExtensionServerRpc()
		{
		});
		*/

		getState().kineticConnectorID = kineticConnectorID;
		getState().boxIDs = boxesToBeHighlighted;
	}

	@Override
	protected BoxHighlightExtensionSharedState getState() {
		return (BoxHighlightExtensionSharedState) super.getState();
	}

	/**
	 * Exposing the inherited API.
	 */
	public void extend(HorizontalFlowLayout component) {
		super.extend(component);
	}
}
