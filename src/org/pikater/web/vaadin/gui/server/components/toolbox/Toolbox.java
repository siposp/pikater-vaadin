package org.pikater.web.vaadin.gui.server.components.toolbox;

import org.pikater.web.sharedresources.ThemeResources;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.event.MouseEvents;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * A Vaadin component looking like a small window, with
 * a minimize button, allowing arbitrary components as
 * its content.</br>
 * It looks like a window but it is
 * not. It belongs to the document flow just like any
 * other Vaadin component (without special modifications).
 * 
 * @author SkyCrawl
 */
@StyleSheet("toolbox.css")
public class Toolbox extends VerticalLayout {
	private static final long serialVersionUID = 5925115342286749639L;

	private Component currentContent;

	public Toolbox(String caption, MouseEvents.ClickListener minimizeAction) {
		this(caption, new Label(), minimizeAction);
	}

	public Toolbox(String caption, Component content, MouseEvents.ClickListener minimizeAction) {
		super();
		setSizeUndefined();
		setSpacing(false);

		Image bar_minimizeIcon = new Image(null, ThemeResources.img_minimizeIcon16);
		bar_minimizeIcon.setStyleName("toolbox-bar-buttons-min");
		bar_minimizeIcon.addClickListener(minimizeAction);

		Label bar_label = new Label(caption);
		bar_label.setStyleName("toolbox-bar-caption-label");

		CustomLayout bar = new CustomLayout("toolboxBar");
		bar.addComponent(bar_label, "caption");
		bar.addComponent(bar_minimizeIcon, "buttons");

		addComponent(bar);
		addComponent(content);
		setExpandRatio(content, 1);
		currentContent = content;
	}

	public void setToolboxContent(Component newContent) {
		replaceComponent(currentContent, newContent);
		setExpandRatio(newContent, 1);
		currentContent = newContent;
	}
}