package org.pikater.web.vaadin.gui.shared.kineticcomponent;

import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.visualstyle.KineticBoxSettings;

/**
 * Interface that both server and client counterparts of {@link KineticComponent}
 * must implement. The server counterpart always forwards calls to these methods
 * to the client implementation. 
 * 
 * @author SkyCrawl
 */
public interface IKineticComponent {
	/**
	 * Highlights the given boxes in the kinetic canvas according to
	 * the {@link KineticBoxSettings visual style} specified.
	 */
	void highlightBoxes(Integer[] boxIDs);

	/**
	 * Cancels any highlights made with the {@link #highlightBoxes(Integer[])}
	 * method.
	 */
	void cancelBoxHighlight();

	/**
	 * Deselects all selected boxes in the client kinetic canvas.
	 */
	void cancelSelection();

	/**
	 * Clears the client kinetic canvas's content and resets inner state.
	 */
	void resetEnvironment();
}
