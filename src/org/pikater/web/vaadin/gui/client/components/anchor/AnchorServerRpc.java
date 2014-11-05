package org.pikater.web.vaadin.gui.client.components.anchor;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/** 
 * @author SkyCrawl
 */
public interface AnchorServerRpc extends ServerRpc {
	/**
	 * Method called (on the server side) when the client side anchor is clicked.
	 * @param mouseDetails details of the click
	 */
	public void clicked(MouseEventDetails mouseDetails);
}