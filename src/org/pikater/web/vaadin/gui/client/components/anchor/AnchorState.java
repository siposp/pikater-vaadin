package org.pikater.web.vaadin.gui.client.components.anchor;

import com.vaadin.shared.AbstractComponentState;

/** 
 * Server settings and variables shared to the client.
 * 
 * @author SkyCrawl
 */
public class AnchorState extends AbstractComponentState {
	private static final long serialVersionUID = -7896962673311413906L;

	/**
	 * This anchor's text.
	 */
	public String text = "";

	/**
	 * Client-side click handler, if any.
	 */
	public String hrefAttrContent = "";

	/**
	 * Clicks will be forwarded to server for handling.
	 */
	public boolean forwardClickToServer = false;
}