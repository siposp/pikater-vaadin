package org.pikater.web.vaadin.gui.server.layouts.flowlayout;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.CssLayout;

/**
 * Base class for flow layouts wrapped as Vaadin components.
 * 
 * @author SkyCrawl
 */
@StyleSheet("flowLayout.css")
public abstract class AbstractFlowLayout extends CssLayout {
	private static final long serialVersionUID = -6039020584387592993L;

	/**
	 * Provides styles for inner components.
	 */
	private final IFlowLayoutStyleProvider styleProvider;

	public AbstractFlowLayout() {
		this(null);
	}

	/**
	 * Creates a flow layout with the given style provider for
	 * inner components.
	 */
	public AbstractFlowLayout(IFlowLayoutStyleProvider styleProvider) {
		super();
		setPrimaryStyleName("flowLayout");

		this.styleProvider = styleProvider;
	}

	/**
	 * Gets the style provider for inner components associated with this flow layout.
	 */
	protected IFlowLayoutStyleProvider getStyleProvider() {
		return styleProvider;
	}
}
