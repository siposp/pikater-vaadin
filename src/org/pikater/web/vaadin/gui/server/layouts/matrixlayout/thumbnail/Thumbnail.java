package org.pikater.web.vaadin.gui.server.layouts.matrixlayout.thumbnail;

import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.MatrixLayout;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/** 
 * <p>A component representing a given item of the {@link MatrixLayout} at
 * the given coordinates. Overriding classes need to define the content
 * component and the text identifying this item.</p>
 * 
 * <p>IMPORTANT NOTE: after creating the subclass, the {@link #init()}
 * method needs to be called.</p>
 */
@StyleSheet("thumbnail.css")
public abstract class Thumbnail extends VerticalLayout {
	private static final long serialVersionUID = -308511560261756785L;

	public Thumbnail() {
		super();
		setSizeUndefined();
		setStyleName("thumbnail");
	}

	/**
	 * Initializes the thumbnail, asks the subclass for content
	 * component and caption.
	 */
	protected void init() {
		Component content = getContent();
		content.setSizeUndefined();
		content.addStyleName("itemContent");

		Label lbl_caption = new Label(getContentCaption());
		lbl_caption.setDescription(lbl_caption.getValue());

		addComponent(lbl_caption);
		addComponent(content);
		setExpandRatio(content, 1);
	}

	protected abstract String getContentCaption();

	protected abstract Component getContent();
}