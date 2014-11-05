package org.pikater.web.vaadin.gui.server.layouts.verticalgrouplayout;

import org.pikater.web.vaadin.gui.server.layouts.SimplePanel;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * A {@link VerticalLayout vertical layout} with a caption
 * and CSS border around it.
 * 
 * @author SkyCrawl
 */
@StyleSheet("verticalGroupLayout.css")
public class VerticalGroupLayout extends SimplePanel {
	private static final long serialVersionUID = -9149639230181359413L;

	private VerticalLayout inner;

	public VerticalGroupLayout() {
		super();
		setSizeUndefined();
	}

	/**
	 * Gets the "logical" inner layout that makes up this layout.
	 */
	public VerticalLayout getInnerLayout() {
		return this.inner;
	}

	/**
	 * Sets the "logical" inner layout that makes up this layout.
	 */
	public void setInnerLayout(String caption, VerticalLayout layout) {
		this.inner = layout;
		this.inner.setSizeFull();
		this.inner.setCaption(caption);
		this.inner.setStyleName("verticalGroupLayout");
		this.inner.setSpacing(true);

		setContent(this.inner);
	}
}
