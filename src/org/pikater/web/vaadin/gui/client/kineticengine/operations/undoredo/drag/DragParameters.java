package org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.drag;

import net.edzard.kinetic.Vector2d;

/**
 * Important parameters needed to finish a drag operation. 
 * 
 * @author SkyCrawl
 */
public class DragParameters {
	private Vector2d originalPosition;
	private Vector2d newPosition;

	public DragParameters() {
	}

	private DragParameters(Vector2d originalPosition, Vector2d newPosition) {
		this.originalPosition = originalPosition;
		this.newPosition = newPosition;
	}

	public Vector2d getOriginalPosition() {
		return originalPosition;
	}

	public void setOriginalPosition(Vector2d originalPosition) {
		this.originalPosition = originalPosition;
	}

	public Vector2d getNewPosition() {
		return newPosition;
	}

	public void setNewPosition(Vector2d newPosition) {
		this.newPosition = newPosition;
	}

	public Vector2d getDelta() {
		if ((originalPosition == null) || (newPosition == null)) {
			throw new IllegalStateException("Original or new position has not been set.");
		} else {
			return new Vector2d(newPosition).sub(originalPosition);
		}
	}

	public DragParameters copy() {
		return new DragParameters(originalPosition, newPosition);
	}
}