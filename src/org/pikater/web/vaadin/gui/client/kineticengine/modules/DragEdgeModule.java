package org.pikater.web.vaadin.gui.client.kineticengine.modules;

import net.edzard.kinetic.Rectangle.RectanglePoint;
import net.edzard.kinetic.Vector2d;
import net.edzard.kinetic.event.EventType;
import net.edzard.kinetic.event.IEventListener;
import net.edzard.kinetic.event.KineticEvent;

import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTMisc;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient.VisualStyle;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient.EndPoint;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.BoxListener;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.IEngineModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.ModuleEventListener;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.DeleteEdgeOperation;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.SwapEdgeEndPointOperation;

/**
 * Module implementing dragging edges (one of the endpoint boxes is changed).
 * 
 * @author SkyCrawl
 */
@SuppressWarnings("deprecation")
public final class DragEdgeModule implements IEngineModule {
	public static String moduleID;

	/**
	 * The engine instance to work with.
	 */
	private final KineticEngine kineticEngine;

	/**
	 * Selection module is needed to keep consistency of selected graph items
	 * set.
	 */
	private SelectionModule selectionModule;

	/**
	 * The edge that is currently being dragged.
	 */
	private EdgeGraphItemClient draggedEdge;

	/**
	 * The dragged edge's endpoint that is being dragged.
	 */
	private EndPoint draggedEdgeEndpoint;

	/**
	 * The special event handlers/listeners to attach to graph items.
	 */
	private class DragMarkMouseDownListener extends ModuleEventListener {
		private final EdgeGraphItemClient edgeBeingDragged;
		private final EndPoint draggedEdgeEndpoint;

		public DragMarkMouseDownListener(EdgeGraphItemClient edgeBeingDragged,
				EndPoint draggedEdgeEndpoint) {
			this.edgeBeingDragged = edgeBeingDragged;
			this.draggedEdgeEndpoint = draggedEdgeEndpoint;
		}

		@Override
		protected void handleInner(KineticEvent event) {
			edgeBeingDragged.edgeDrag_toBaseLine(
					edgeBeingDragged.getDragMark(draggedEdgeEndpoint)
							.getAbsolutePointPosition(RectanglePoint.CENTER,
									Vector2d.xyUnit), edgeBeingDragged
							.getEndPoint(draggedEdgeEndpoint.getInverted()));
			setEdgeBeingDragged(edgeBeingDragged, draggedEdgeEndpoint);

			event.setProcessed();
			event.stopVerticalPropagation();
		}
	}

	private class BoxMouseEnterListener extends BoxListener {
		public BoxMouseEnterListener(BoxGraphItemClient parentBox) {
			super(parentBox);
		}

		@Override
		protected void handleInner(KineticEvent event) {
			if (isAnEdgeBeingDragged()
					&& !isStaticEndpointForThisDrag(getEventSourceBox())) {
				getEventSourceBox()
						.setVisualStyle(VisualStyle.HIGHLIGHTED_EDGE);
				kineticEngine.draw(getEventSourceBox().getComponentToDraw());

				event.setProcessed();
				event.stopVerticalPropagation();
			}
		}
	}

	private class BoxMouseLeaveListener extends BoxListener {
		public BoxMouseLeaveListener(BoxGraphItemClient parentBox) {
			super(parentBox);
		}

		@Override
		protected void handleInner(KineticEvent event) {
			if (isAnEdgeBeingDragged()) {
				getEventSourceBox().setVisualStyle(
						VisualStyle.NOT_HIGHLIGHTED_EDGE);
				kineticEngine.draw(getEventSourceBox().getComponentToDraw());

				event.setProcessed();
				event.stopVerticalPropagation();
			}
		}
	}

	private class BoxMouseUpListener extends ModuleEventListener {
		@Override
		protected void handleInner(KineticEvent event) {
			if (isAnEdgeBeingDragged()) {
				// KineticJS bug: events can not be fired from named events, so
				// a workaround:
				fillRectangleEdgeDragMouseUpHandler.handle(null);
				event.setProcessed();
			}
			// event.stopPropagation(); // SERIOUSLY... don't set this up
			// everytime. It can have devastating effects like preventing the
			// Click event.
		}
	}

	/**
	 * The special handlers that will temporarily replace the engine's when an
	 * edge drag is triggered.
	 */
	private final IEventListener fillRectangleEdgeDragMouseMoveHandler = new IEventListener() {
		@Override
		public void handle(KineticEvent event) {
			draggedEdge.edgeDrag_updateBaseLine(kineticEngine
					.getMousePosition());
			kineticEngine.draw(EngineComponent.LAYER_EDGES);
		}
	};
	private final IEventListener fillRectangleEdgeDragMouseUpHandler = new IEventListener() {
		@Override
		public void handle(KineticEvent event) {
			// IMPORTANT: don't violate the call order
			draggedEdge.edgeDrag_toEdge(false); // only switches the edge's
												// internal state, doesn't
												// update the edge or draws
												// anything whatsoever
			BoxGraphItemClient newEndpoint = kineticEngine.getHoveredBox();
			if (newEndpoint != null) // edge was dragged onto a box
			{
				newEndpoint.setVisualStyle(VisualStyle.NOT_HIGHLIGHTED_EDGE);
				BoxGraphItemClient originalEndpoint = draggedEdge
						.getEndPoint(draggedEdgeEndpoint);
				if (newEndpoint != originalEndpoint) // now we know for sure we
														// are going to change
														// the endpoint
				{
					// IMPORTANT: don't violate the call order
					selectionModule.onEdgeDragFinish(draggedEdge,
							originalEndpoint, newEndpoint, draggedEdge
									.getEndPoint(draggedEdgeEndpoint
											.getInverted()));
					kineticEngine.pushToHistory(new SwapEdgeEndPointOperation(
							kineticEngine, getDraggedEdge(),
							getEndPointBeingChanged())); // changes the
															// endpoint, updates
															// the edge,
															// notifies server
															// and draws
				} else {
					kineticEngine.draw(EngineComponent.STAGE);
				}
			} else // edge was dragged off the previous box onto blank space
			{
				// delete it and also draw changes
				kineticEngine.pushToHistory(new DeleteEdgeOperation(
						kineticEngine, draggedEdge));
			}
			resetEdgeDraggingVars();
		}
	};

	/**
	 * Constructor.
	 */
	public DragEdgeModule(KineticEngine kineticEngine) {
		moduleID = GWTMisc.getSimpleName(this.getClass());
		this.kineticEngine = kineticEngine;
		this.draggedEdge = null;
	}

	// **********************************************************************************************
	// INHERITED INTERFACE

	@Override
	public String getModuleID() {
		return moduleID;
	}

	@Override
	public void createModuleCrossReferences() {
		selectionModule = (SelectionModule) kineticEngine
				.getModule(SelectionModule.moduleID);
	}

	@Override
	public String[] getGraphItemTypesToAttachHandlersTo() {
		return new String[] { GWTMisc.getSimpleName(EdgeGraphItemClient.class),
				GWTMisc.getSimpleName(BoxGraphItemClient.class), };
	}

	@Override
	public void attachHandlers(AbstractGraphItemClient<?> graphItem) {
		if (graphItem instanceof EdgeGraphItemClient) {
			EdgeGraphItemClient edge = (EdgeGraphItemClient) graphItem;
			edge.getDragMark(EndPoint.FROM).addEventListener(
					new DragMarkMouseDownListener(edge, EndPoint.FROM),
					EventType.Basic.MOUSEDOWN.withName(moduleID));
			edge.getDragMark(EndPoint.TO).addEventListener(
					new DragMarkMouseDownListener(edge, EndPoint.TO),
					EventType.Basic.MOUSEDOWN.withName(moduleID));
		} else if (graphItem instanceof BoxGraphItemClient) {
			BoxGraphItemClient box = (BoxGraphItemClient) graphItem;
			box.getMasterNode().addEventListener(
					new BoxMouseEnterListener(box),
					EventType.Basic.MOUSEENTER.withName(moduleID));
			box.getMasterNode().addEventListener(
					new BoxMouseLeaveListener(box),
					EventType.Basic.MOUSELEAVE.withName(moduleID));
			box.getMasterNode().addEventListener(new BoxMouseUpListener(),
					EventType.Basic.MOUSEUP.withName(moduleID));
		} else {
			throw new IllegalStateException();
		}
	}

	// **********************************************************************************************
	// PUBLIC INTERFACE

	public boolean isAnEdgeBeingDragged() {
		return draggedEdge != null;
	}

	public EdgeGraphItemClient getDraggedEdge() {
		return draggedEdge;
	}

	public EndPoint getEndPointBeingChanged() {
		return draggedEdgeEndpoint;
	}

	// **********************************************************************************************
	// PRIVATE INTERFACE

	private void setEdgeBeingDragged(EdgeGraphItemClient edge, EndPoint endPoint) {
		draggedEdge = edge;
		draggedEdgeEndpoint = endPoint;

		kineticEngine.removeFillRectangleHandlers();
		kineticEngine.getFillRectangle().addEventListener(
				fillRectangleEdgeDragMouseMoveHandler,
				EventType.Basic.MOUSEMOVE);
		kineticEngine.getFillRectangle().addEventListener(
				fillRectangleEdgeDragMouseUpHandler, EventType.Basic.MOUSEUP);
	}

	private boolean isStaticEndpointForThisDrag(BoxGraphItemClient box) {
		return draggedEdge.getEndPoint(draggedEdgeEndpoint.getInverted()) == box;
	}

	private void resetEdgeDraggingVars() {
		draggedEdge = null;
		kineticEngine.setFillRectangleHandlers(); // automatically removes these
													// handlers before setting
													// the originals
	}
}
