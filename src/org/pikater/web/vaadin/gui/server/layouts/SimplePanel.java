package org.pikater.web.vaadin.gui.server.layouts;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

/**
 * A simple "div" wrapper for a component. 
 * 
 * @author SkyCrawl
 */
public class SimplePanel extends CustomLayout {
	private static final long serialVersionUID = -4653502684891551427L;

	public SimplePanel() {
		super("simplePanel");
		setPrimaryStyleName("simplePanel");
	}

	public SimplePanel(Component content) {
		this();
		setContent(content);
	}

	public void setContent(Component content) {
		addComponent(content, "CONTAINER");
	}
}