package org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.drag;

import net.edzard.kinetic.Node;

import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.base.BiDiOperation;

/**
 * <p>Undoable operation to move some graph items as a group and update related
 * edges in the environment/canvas.</p>
 * 
 * <p>This operation is selection independent - it doesn't select/deselect anything.</p>
 * 
 * @author SkyCrawl
 */
public class MoveBoxesOperation extends BiDiOperation {
	/*
	 * Required operation related variables and contexts.
	 */
	private final IBoxDragContext context;
	private final DragParameters params;

	/*
	 * Local immutable copy of all nodes/items being involved.
	 */
	private final BoxGraphItemClient[] boxesBeingMoved;
	private final Node[] allMovedNodes; // boxes and edges
	private final EdgeGraphItemClient[] edgesInBetween;

	/**
	 * Constructor. Use AFTER the drag itself.
	 */
	public MoveBoxesOperation(IBoxDragContext context, DragParameters params) {
		super(context.getParentEngine());

		this.context = context;
		this.params = params.copy(); // this is important...local copy

		this.boxesBeingMoved = context.getBoxesBeingMoved().toArray(new BoxGraphItemClient[0]);
		this.allMovedNodes = context.getNodesBeingMoved();
		this.edgesInBetween = context.getEdgesInBetween().toArray(new EdgeGraphItemClient[0]);

		// switch from drag mode back to normal mode (but don't recompute the edges yet)
		for (EdgeGraphItemClient edge : edgesInBetween) {
			edge.endPointDrag_toEdge();
		}
	}

	// **********************************************************************************
	// INHERITED OPERATION INTERFACE

	@Override
	public void undo() {
		// first restore original positions
		context.setOriginalPositions(allMovedNodes, params);

		// and finally, update the edges caught between selection and dynamic layer and draw changes
		recomputeEdgesInBetweenAndDraw();

		// notify server about the changes - compute new serialized objects with current positions
		kineticEngine.getContext().command_boxPositionsChanged(BoxGraphItemClient.toShared(boxesBeingMoved));
	}

	@Override
	public void redo() {
		// first move the nodes
		context.setNewPositions(allMovedNodes, params);

		// and then update the edges caught between selection and dynamic layer and draw changes
		recomputeEdgesInBetweenAndDraw();

		// notify server about the changes - compute new serialized objects with current positions
		kineticEngine.getContext().command_boxPositionsChanged(BoxGraphItemClient.toShared(boxesBeingMoved));
	}

	// **********************************************************************************
	// PRIVATE INTERFACE

	private void recomputeEdgesInBetweenAndDraw() {
		// update the edges caught between selection and dynamic layer
		for (EdgeGraphItemClient edge : edgesInBetween) {
			edge.updateEdge();
		}

		// and finally, draw changes
		kineticEngine.draw(EngineComponent.STAGE); // items may be deselected later when repeating the operation
	}
}
