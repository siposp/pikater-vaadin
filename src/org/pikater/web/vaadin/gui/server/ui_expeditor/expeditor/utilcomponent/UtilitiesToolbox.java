package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.utilcomponent;

import org.pikater.web.vaadin.gui.server.components.toolbox.Toolbox;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Label;

/**
 * Not used at the moment but might be sometime in the future.
 * 
 * @author SkyCrawl
 */
@StyleSheet("utilitiesToolbox.css")
public class UtilitiesToolbox extends Toolbox {
	private static final long serialVersionUID = 1668816947520512948L;

	public UtilitiesToolbox(String caption, ClickListener minimizeAction) {
		super(caption, minimizeAction);
		setSizeFull();

		setToolboxContent(new Label("Poliket"));
	}
}