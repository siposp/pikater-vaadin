package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;

/**
 * Generic view for features that are not implemented yet.
 * 
 * @author SkyCrawl
 */
public class UnimplementedView extends Label implements IContentComponent {
	private static final long serialVersionUID = -7610583075707286907L;

	public UnimplementedView() {
		// super("<font color=\"red\">Unimplemented yet.</font>", ContentMode.HTML);
		super("Unimplemented yet");
		setStyleName("errorLabel");
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public boolean isReadyToClose() {
		return true;
	}

	@Override
	public String getCloseMessage() {
		return null;
	}

	@Override
	public void beforeClose() {
	}
}
