package org.pikater.web.vaadin.gui.server.components.iconbutton;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;

/** 
 * An image-button.
 * 
 * @author SkyCrawl
 */
@StyleSheet("iconButton.css")
public class IconButton extends CustomComponent {
	private static final long serialVersionUID = 4029033501431550617L;

	private final Image img;

	public IconButton(Resource source) {
		this.img = new Image(null, source);
		this.img.setStyleName("icon-button");
		setCompositionRoot(this.img);
	}

	public void addClickListener(ClickListener clickListener) {
		img.addClickListener(clickListener);
	}
}