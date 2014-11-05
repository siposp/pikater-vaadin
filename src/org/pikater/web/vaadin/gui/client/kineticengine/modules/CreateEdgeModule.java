package org.pikater.web.vaadin.gui.client.kineticengine.modules;

import net.edzard.kinetic.event.EventType;
import net.edzard.kinetic.event.IEventListener;
import net.edzard.kinetic.event.KineticEvent;

import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTKeyboardManager;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTMisc;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.client.kineticengine.GraphItemCreator.GraphItemRegistration;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient.VisualStyle;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient.EndPoint;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.BoxListener;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.IEngineModule;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.ItemRegistrationOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.AbstractGraphItemShared.RegistrationOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.EdgeGraphItemShared;

/**
 * Module implementing creating edges, from start to end.
 * 
 * @author SkyCrawl
 */
@SuppressWarnings("deprecation")
public final class CreateEdgeModule implements IEngineModule {
	public static String moduleID;

	/**
	 * The engine instance to work with.
	 */
	private final KineticEngine kineticEngine;

	/**
	 * The selection module instance to use.
	 */
	private SelectionModule selectionModule;

	/**
	 * The instance handling this operation's state.
	 */
	private final CreateEdgeContext edgeCreationContext;

	/**
	 * The class to handle all logic behind edge creating.
	 */
	private class CreateEdgeContext {
		/**
		 * The box that starts this operation.
		 */
		private BoxGraphItemClient fromEndPoint;

		/**
		 * The edge that is being created.
		 */
		private EdgeGraphItemClient newEdge;

		public CreateEdgeContext() {
			reset();
		}

		// --------------------------------------------------------
		// OPERATION START/END ROUTINES

		public void startOperation(BoxGraphItemClient box) {
			fromEndPoint = box;

			// create the new edge and convert it to baseline
			newEdge = kineticEngine
					.getContext()
					.getGraphItemCreator()
					.createEdge(GraphItemRegistration.MANUAL, fromEndPoint,
							null);
			newEdge.edgeDrag_toBaseLine(kineticEngine.getMousePosition(),
					fromEndPoint);
		}

		/**
		 * <p>
		 * Start creation of a new edge.
		 * </p>
		 * 
		 * <p>
		 * This method has to call {@link finishOperation(BoxPrototype
		 * toEndPoint)} for consistency.
		 * </p>
		 */
		public void stopOperation() {
			finishOperation(null);
		}

		/**
		 * Finish creation of a new edge.
		 */
		public void finishOperation(BoxGraphItemClient toEndPoint) {
			// IMPORTANT: don't violate the call order
			fromEndPoint.setVisualStyle(VisualStyle.NOT_HIGHLIGHTED_EDGE);
			if (toEndPoint != null) {
				toEndPoint.setVisualStyle(VisualStyle.NOT_HIGHLIGHTED_EDGE);
				if (fromEndPoint.isNotConnectedTo(toEndPoint)) {
					newEdge.setEndpoint(EndPoint.TO, toEndPoint);
					newEdge.edgeDrag_toEdge(true); // switches the edge's
													// internal state and
													// updates it, doesn't draw
													// anything
					selectionModule.onNewEdgeCreated(newEdge);

					/*
					 * Duplicate code from {@link ItemRegistrationModule}. Not
					 * so easy to do it with another way.
					 */
					EdgeGraphItemShared[] serializedEdges = EdgeGraphItemClient
							.toShared(newEdge);
					if (serializedEdges.length == 0) {
						throw new IllegalStateException(
								"Something weird is happening... Looks like we broke "
										+ "some conditions for new edges in this module.");
					} else {
						kineticEngine.getContext()
								.command_edgeSetChange(
										RegistrationOperation.REGISTER,
										serializedEdges);
					}
				} else {
					kineticEngine.getContext().getHistoryManager()
							.undoAndDiscard();
				}
			} else {
				kineticEngine.getContext().getHistoryManager().undoAndDiscard();
			}
			reset();

			kineticEngine.setFillRectangleHandlers(); // automatically removes
														// these handlers before
														// setting the originals
			kineticEngine.draw(EngineComponent.STAGE);
		}

		// --------------------------------------------------------
		// VARIOUS

		public BoxGraphItemClient getFromEndPoint() {
			return fromEndPoint;
		}

		public EdgeGraphItemClient getNewEdge() {
			return newEdge;
		}

		public boolean isAnEdgeBeingCreated() {
			return getNewEdge() != null;
		}

		public void reset() {
			this.fromEndPoint = null;
			this.newEdge = null;
		}
	}

	// **********************************************************************************************
	// ITEM LISTENERS

	/**
	 * The special event handlers/listeners to attach to graph items.
	 */
	private class BoxClickListener extends BoxListener {
		public BoxClickListener(BoxGraphItemClient parentBox) {
			super(parentBox);
		}

		@Override
		protected void handleInner(KineticEvent event) {
			if (edgeCreationContext.isAnEdgeBeingCreated()) // whichever
															// clickmode is
															// currently set,
															// finish the
															// operation if
															// started
			{
				/*
				 * Finish this create edge operation.
				 */
				if (getEventSourceBox() != edgeCreationContext
						.getFromEndPoint()) {
					edgeCreationContext.finishOperation(getEventSourceBox());
				}

				event.stopVerticalPropagation();
				event.setProcessed();
			} else if ((kineticEngine.getContext().getClickMode() == ClickMode.CONNECTION)
					|| GWTKeyboardManager.isAltKeyDown()) // when it's either
															// clear we want to
															// connect or the
															// right modifier
															// key is down
			{
				/*
				 * Start this create edge operation.
				 */

				edgeCreationContext.startOperation(getEventSourceBox());
				getEventSourceBox()
						.setVisualStyle(VisualStyle.HIGHLIGHTED_EDGE);

				// register the required handlers
				kineticEngine.removeFillRectangleHandlers();
				kineticEngine.getFillRectangle().addEventListener(
						fillRectangleEdgeDragMouseMoveHandler,
						EventType.Basic.MOUSEMOVE);
				kineticEngine.getFillRectangle().addEventListener(
						fillRectangleEdgeDragMouseDownHandler,
						EventType.Basic.MOUSEDOWN);

				// register the new edge and also draw the stage
				kineticEngine.pushToHistory(new ItemRegistrationOperation(
						kineticEngine, null,
						new EdgeGraphItemClient[] { edgeCreationContext
								.getNewEdge() }, true));

				// and display changes - not needed at this point (done in the
				// previous command)
				// kineticEngine.draw(parentBox.getComponentToDraw());

				event.stopVerticalPropagation();
				event.setProcessed();
			}
		}
	}

	private class BoxMouseEnterListener extends BoxListener {
		public BoxMouseEnterListener(BoxGraphItemClient parentBox) {
			super(parentBox);
		}

		@Override
		protected void handleInner(KineticEvent event) {
			if (edgeCreationContext.isAnEdgeBeingCreated()
					&& (getEventSourceBox() != edgeCreationContext
							.getFromEndPoint())) {
				getEventSourceBox()
						.setVisualStyle(VisualStyle.HIGHLIGHTED_EDGE);
				kineticEngine.draw(getEventSourceBox().getComponentToDraw());

				event.stopVerticalPropagation();
				event.setProcessed();
			}
		}
	}

	private class BoxMouseLeaveListener extends BoxListener {
		public BoxMouseLeaveListener(BoxGraphItemClient parentBox) {
			super(parentBox);
		}

		@Override
		protected void handleInner(KineticEvent event) {
			if (edgeCreationContext.isAnEdgeBeingCreated()
					&& (getEventSourceBox() != edgeCreationContext
							.getFromEndPoint())) {
				getEventSourceBox().setVisualStyle(
						VisualStyle.NOT_HIGHLIGHTED_EDGE);
				kineticEngine.draw(getEventSourceBox().getComponentToDraw());

				event.stopVerticalPropagation();
				event.setProcessed();
			}
		}
	}

	/**
	 * The special handlers that will temporarily replace the engine's when an
	 * edge drag is triggered.
	 */
	private final IEventListener fillRectangleEdgeDragMouseMoveHandler = new IEventListener() {
		@Override
		public void handle(KineticEvent event) {
			edgeCreationContext.getNewEdge().edgeDrag_updateBaseLine(
					kineticEngine.getMousePosition());
			kineticEngine.draw(edgeCreationContext.getNewEdge()
					.getComponentToDraw());
		}
	};
	private final IEventListener fillRectangleEdgeDragMouseDownHandler = new IEventListener() {
		@Override
		public void handle(KineticEvent event) {
			edgeCreationContext.stopOperation();

			event.stopVerticalPropagation();
			event.setProcessed();
		}
	};

	// **********************************************************************************************
	// CONSTRUCTOR

	public CreateEdgeModule(KineticEngine kineticEngine) {
		moduleID = GWTMisc.getSimpleName(this.getClass());
		this.kineticEngine = kineticEngine;
		this.edgeCreationContext = new CreateEdgeContext();
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
		return new String[] { GWTMisc.getSimpleName(BoxGraphItemClient.class) };
	}

	@Override
	public void attachHandlers(AbstractGraphItemClient<?> graphItem) {
		if (graphItem instanceof BoxGraphItemClient) {
			BoxGraphItemClient box = (BoxGraphItemClient) graphItem;
			box.getMasterNode().addEventListener(new BoxClickListener(box),
					EventType.Basic.CLICK.withName(moduleID));
			box.getMasterNode().addEventListener(
					new BoxMouseEnterListener(box),
					EventType.Basic.MOUSEENTER.withName(moduleID));
			box.getMasterNode().addEventListener(
					new BoxMouseLeaveListener(box),
					EventType.Basic.MOUSELEAVE.withName(moduleID));
		} else {
			throw new IllegalStateException();
		}
	}
}
