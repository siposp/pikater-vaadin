package org.pikater.web.vaadin.gui.server.layouts.flowlayout;

import org.pikater.web.vaadin.gui.server.StyleBuilder;

import com.vaadin.ui.Component;

/**
 * This interface represents a dynamic style mapping to various
 * components within a single container. Avoids having to define
 * a lot of style names and individual style sheets.
 * 
 * @author SkyCrawl
 */
public interface IFlowLayoutStyleProvider {
	/**
	 * Sets styles to be applied to the given compoment via
	 * a {@link StyleBuilder} instance.
	 */
	void setStylesForInnerComponent(Component c, StyleBuilder builder);
}
