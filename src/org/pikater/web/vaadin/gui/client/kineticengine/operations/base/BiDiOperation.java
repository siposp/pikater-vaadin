package org.pikater.web.vaadin.gui.client.kineticengine.operations.base;

import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;

/**
 * Base class for "back&forth" operations suitable to be used
 * in history managers.
 * 
 * @author SkyCrawl
 */
public abstract class BiDiOperation {
	protected final KineticEngine kineticEngine;

	public BiDiOperation(KineticEngine kineticEngine) {
		this.kineticEngine = kineticEngine;
	}

	/**
	 * Undo this operation.
	 */
	public abstract void undo();

	/**
	 * Redo this operation.
	 */
	public abstract void redo();
}