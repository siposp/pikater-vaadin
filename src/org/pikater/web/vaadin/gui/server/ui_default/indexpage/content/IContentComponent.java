package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content;

import org.pikater.web.vaadin.gui.server.ui_default.indexpage.IndexPage;

import com.vaadin.navigator.View;
import com.vaadin.ui.Component;

/**
 * Implementations of this interface are to be used as
 * content components for {@link IndexPage}.
 * 
 * @author SkyCrawl
 */
public interface IContentComponent extends View, Component {
	/**
	 * Programmatic check whether the view can be closed.
	 */
	boolean isReadyToClose();

	/**
	 * If {@link #isReadyToClose()} returns true, this method is
	 * called and should return the message that will be shown
	 * in the close-confirming dialog. User needs to approve of it
	 * as well. 
	 */
	String getCloseMessage();

	/**
	 * If close is confirmed in the dialog, this method will be
	 * called prior to closing the view.
	 */
	void beforeClose();
}
