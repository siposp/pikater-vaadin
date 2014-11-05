package org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.drag;

import java.util.Set;
import net.edzard.kinetic.Node;
import net.edzard.kinetic.Vector2d;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;

/**
 * Context for box dragging operations.
 *
 * @author SkyCrawl
 */
public interface IBoxDragContext {
	KineticEngine getParentEngine();

	/**
	 * Gets the current position of the box (or group of boxes) being
	 * moved.
	 */
	Vector2d getCurrentBasePosition();

	Set<BoxGraphItemClient> getBoxesBeingMoved();

	/**
	 * Gets edges that are connected to a box from
	 * {@link #getBoxesBeingMoved()} but still not
	 * being moved directly.
	 */
	Set<EdgeGraphItemClient> getEdgesInBetween();

	/**
	 * Gets all kinetic {@link Node nodes} representing
	 * boxes and edges being moved. 
	 */
	Node[] getNodesBeingMoved();

	void setNewPositions(Node[] allMovedNodes, DragParameters params);

	void setOriginalPositions(Node[] allMovedNodes, DragParameters params);
}
