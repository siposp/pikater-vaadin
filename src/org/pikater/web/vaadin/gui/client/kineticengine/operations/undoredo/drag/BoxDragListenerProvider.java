package org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.drag;

import net.edzard.kinetic.event.IEventListener;
import net.edzard.kinetic.event.KineticEvent;

import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTCursorManager;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTCursorManager.MyCursor;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;

/**
 * Wrapper class providing movement functionality (also in groups) to
 * boxes. While this MAY be a part of {@link BoxGraphItemClient}, as
 * a result it also clutters the code a little and that's why it
 * was separated into this class. As a side effect, all box movement
 * code is focused into a single exclusive package.
 * 
 * @author SkyCrawl
 */
@SuppressWarnings("deprecation")
public class BoxDragListenerProvider {
	private final IBoxDragContext context;
	private final DragParameters positionParams;

	public BoxDragListenerProvider(IBoxDragContext context) {
		this.context = context;
		this.positionParams = new DragParameters();
	}

	//-----------------------------------------------------------
	// EVENT LISTENERS

	private BoxGraphItemClient boxBeingMoved;

	public IEventListener getDragStartListener() {
		return new IEventListener() {
			@Override
			public void handle(KineticEvent event) {
				GWTCursorManager.setCursorType(context.getParentEngine().getContext().getStageDOMElement(), MyCursor.MOVE);

				// first and foremost, remember & compute some basic information
				positionParams.setOriginalPosition(context.getCurrentBasePosition());
				boxBeingMoved = (context.getBoxesBeingMoved() != null) || (context.getBoxesBeingMoved().size() == 1) ? context.getBoxesBeingMoved().iterator().next() : null;

				/*
				 * Switch from normal mode to drag mode - turn all edges in between into dashed
				 * lines of a special color that connect the centers of their endpoint boxes.
				 */

				for (EdgeGraphItemClient edge : context.getEdgesInBetween()) {
					edge.endPointDrag_toBaseLine(boxBeingMoved != null ? boxBeingMoved : edge.getSelectedEndpoint());
				}

				// draw and finish
				context.getParentEngine().draw(EngineComponent.LAYER_EDGES);
				event.stopVerticalPropagation();
			}
		};
	}

	public IEventListener getDragMoveListener() {
		return new IEventListener() {
			@Override
			public void handle(KineticEvent event) {
				// update the dashed edges
				for (EdgeGraphItemClient edge : context.getEdgesInBetween()) {
					edge.endPointDrag_updateBaseLine(boxBeingMoved != null ? boxBeingMoved : edge.getSelectedEndpoint());
				}

				// draw and finish
				context.getParentEngine().draw(EngineComponent.LAYER_EDGES);
				event.stopVerticalPropagation();
			}
		};
	}

	public IEventListener getDragEndListener() {
		return new IEventListener() {
			/**
			 * End of a drag operation.
			 * Undo the effects of drag start and propagate the drag changes to selection items.
			 */
			@Override
			public void handle(KineticEvent event) {
				GWTCursorManager.rollBackCursor(context.getParentEngine().getContext().getStageDOMElement());

				// propagate the new position to selected items and switch edges in between back to normal mode
				MoveBoxesOperation operation = new MoveBoxesOperation(context, positionParams);
				context.getParentEngine().pushToHistory(operation);
				operation.redo();

				// finish
				event.stopVerticalPropagation();
			}
		};
	}
}