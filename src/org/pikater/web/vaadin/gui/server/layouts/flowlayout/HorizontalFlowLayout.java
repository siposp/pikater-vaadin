package org.pikater.web.vaadin.gui.server.layouts.flowlayout;

import java.util.HashSet;
import java.util.Set;

import org.pikater.web.vaadin.gui.server.StyleBuilder;

import com.vaadin.ui.Component;

/**
 * Horizontal flow layout implementation - inner components are placed
 * one after another, each displayed to its full width (may vary).
 * If the element would exceed the width of the container, the element
 * is placed in the next "row" even if the container's height would be
 * exceeded (container doesn't expand to fit the content).
 * 
 * @author SkyCrawl
 */
public class HorizontalFlowLayout extends AbstractFlowLayout {
	private static final long serialVersionUID = 6951568571587805444L;

	private final Set<Component> innerComponents;

	/**
	 * Creates a horizontal flow layout with the given style provider for
	 * inner components.
	 */
	public HorizontalFlowLayout(IFlowLayoutStyleProvider styleProvider) {
		super(styleProvider);

		this.innerComponents = new HashSet<Component>();
	}

	/**
	 * Adds the specified component to the right (applies a float: right property).
	 */
	public void addComponentToRight(Component c) {
		super.addComponent(c);
		innerComponents.add(c);
	}

	@Override
	public void removeComponent(Component c) {
		// TODO: this is not resistant to multiple addition
		super.removeComponent(c);
		innerComponents.remove(c);
	}

	@Override
	public void removeAllComponents() {
		super.removeAllComponents();
		innerComponents.clear();
	}

	@Override
	protected String getCss(Component c) {
		StyleBuilder builder = new StyleBuilder();
		if (getStyleProvider() != null) {
			getStyleProvider().setStylesForInnerComponent(c, builder);
		}
		builder.setProperty("float", innerComponents.contains(c) ? "right" : "left");
		return builder.build();
	}
}
