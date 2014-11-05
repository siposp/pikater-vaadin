package org.pikater.web.vaadin.gui.client.kineticengine;

import net.edzard.kinetic.Box2d;
import net.edzard.kinetic.Colour;
import net.edzard.kinetic.Kinetic;
import net.edzard.kinetic.Node;
import net.edzard.kinetic.Path;
import net.edzard.kinetic.PathSVG;
import net.edzard.kinetic.Vector2d;

import org.pikater.web.vaadin.gui.client.kineticengine.graph.IKineticShapeWrapper;

/**
 * A shape always present in {@link KineticEngine} which is used
 * to capture movement of the user's mouse. The movement selects
 * a boundary within which we will later be looking for boxes.
 * 
 * @author SkyCrawl
 */
public class MultiSelectionRectangle implements IKineticShapeWrapper {
	/**
	 * The kinetic shape representing the rectangle.
	 */
	private final PathSVG path;

	/**
	 * The point, where the user started his selection with a mouse down event.
	 */
	private Vector2d originalMousePosition;

	/**
	 * The latest mouse position.
	 */
	private Vector2d currentMousePosition;

	public MultiSelectionRectangle() {
		super();

		this.path = Kinetic.createPathSVG(Vector2d.origin, "");
		this.path.setStroke(Colour.red);
		this.path.setListening(false);

		reset();
	}

	// **********************************************************************************************
	// INHERITED INTERFACE

	@Override
	public Node getMasterNode() {
		return path;
	}

	// **********************************************************************************************
	// PUBLIC INTERFACE

	public Vector2d getOriginalMousePosition() {
		return this.originalMousePosition;
	}

	/**
	 * Sets the starting point of a selection. Use this method when user clicks
	 * the left mouse button and holds it.
	 */
	public void setOriginalMousePosition(Vector2d position) {
		this.originalMousePosition = position;
		this.currentMousePosition = position;
	}

	/**
	 * <p>Updates the rectangle with a new mouse position somehow. Use this
	 * method when user moves their mouse while still holding the left
	 * mouse button.</p>
	 * 
	 * <p>A little bit of "magic" is used here that even us don't understand anymore...
	 * This should probably be made more clear (and complex)</p>
	 * 
	 */
	public void updatePath(Vector2d left, Vector2d up, Vector2d right) {
		this.currentMousePosition = up;
		this.path.setData(new Path().moveTo(originalMousePosition).lineTo(left).lineTo(up).lineTo(right).lineTo(originalMousePosition).toSVGPath());
	}

	/**
	 * Returns the box (position and size) of the current selection.
	 */
	public Box2d getSelection() {
		Vector2d pos, size;
		if (originalMousePosition.x < currentMousePosition.x) {
			pos = originalMousePosition;
			size = currentMousePosition.sub(originalMousePosition);
		} else {
			pos = currentMousePosition;
			size = originalMousePosition.sub(currentMousePosition);
		}
		if (size.x < 0) {
			pos.x += size.x;
			size.x = Math.abs(size.x);
		}
		if (size.y < 0) {
			pos.y += size.y;
			size.y = Math.abs(size.y);
		}
		return new Box2d(pos, size);
	}

	public void reset() {
		this.originalMousePosition = Vector2d.origin;
		this.currentMousePosition = Vector2d.origin;
	}
}
