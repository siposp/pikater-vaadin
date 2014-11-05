package org.pikater.web.vaadin.gui.client.kineticengine;

import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;

import com.google.gwt.user.client.Element;

/**
 * Defines everything that the outside GWT world needs to know about
 * our kinetic canvas.
 *  
 * @author SkyCrawl
 */
public interface IKineticEngineContext {
	Element getStageDOMElement();

	KineticEngine getEngine();

	GraphItemCreator getGraphItemCreator();

	KineticUndoRedoManager getHistoryManager();

	ClickMode getClickMode();
}