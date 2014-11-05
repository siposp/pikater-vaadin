package org.pikater.web.vaadin.gui.client.components.kineticcomponent;

import org.pikater.web.vaadin.gui.client.kineticengine.IKineticEngineContext;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.GraphItemCreator;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticUndoRedoManager;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;

import com.google.gwt.user.client.Element;

/** 
 * A wrapper class to store the complete state of the client-side
 * {@link KineticComponent}. Under normal circumstances, all this
 * could be defined in {@link KineticComponentWidget} (and it
 * previously was). However, this class is needed when tabs are changed.
 * Vaadin then destroys the currently displayed client-side {@link
 * KineticComponent} and recreates it again later from the shared
 * state, if needed (where none of this information is stored, mainly
 * because of performance).</br>
 * If such a situation occurs, the current client-side state defined
 * in this class is backed up in {@link KineticStateRegistrar}, from
 * which it is also later picked up and restored.
 * 
 * @author SkyCrawl
 */
public class KineticState implements IKineticEngineContext {
	private KineticComponentWidget parentWidget;

	/*
	 * All-purpose kinetic engine components.
	 */
	private final KineticEngine engine;
	private final GraphItemCreator itemCreator;
	private final KineticUndoRedoManager historyManager;

	public KineticState(KineticComponentWidget parentWidget) {
		this.engine = new KineticEngine();
		this.itemCreator = new GraphItemCreator(this.engine);
		this.historyManager = new KineticUndoRedoManager(parentWidget);

		setParentWidget(parentWidget); // requires engine to be set
	}

	/**
	 * This method is needed to be called after the client-side kinetic
	 * state defined by this class is recreated (see the class's Javadoc).</br>
	 * Vaadin then creates a new instance of {@link KineticComponentWidget}
	 * and it needs to be registered with the state, otherwise weird
	 * errors will pop up.
	 * 
	 */
	public void setParentWidget(KineticComponentWidget parentWidget) {
		this.parentWidget = parentWidget;
		this.engine.setWidgetContext(parentWidget);
	}

	@Override
	public Element getStageDOMElement() {
		return parentWidget.getStageDOMElement();
	}

	@Override
	public KineticEngine getEngine() {
		return engine;
	}

	@Override
	public GraphItemCreator getGraphItemCreator() {
		return itemCreator;
	}

	@Override
	public KineticUndoRedoManager getHistoryManager() {
		return historyManager;
	}

	@Override
	public ClickMode getClickMode() {
		return parentWidget.getClickMode();
	}
}
