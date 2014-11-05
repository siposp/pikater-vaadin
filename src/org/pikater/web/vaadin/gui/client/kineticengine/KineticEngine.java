package org.pikater.web.vaadin.gui.client.kineticengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.edzard.kinetic.Box2d;
import net.edzard.kinetic.Container;
import net.edzard.kinetic.Kinetic;
import net.edzard.kinetic.Layer;
import net.edzard.kinetic.Node;
import net.edzard.kinetic.Rectangle;
import net.edzard.kinetic.Stage;
import net.edzard.kinetic.Vector2d;
import net.edzard.kinetic.event.EventType;
import net.edzard.kinetic.event.IEventListener;
import net.edzard.kinetic.event.KineticEvent;

import org.pikater.web.experiment.client.ExperimentGraphClient;
import org.pikater.web.vaadin.gui.client.components.kineticcomponent.KineticComponentWidget;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTMisc;
import org.pikater.web.vaadin.gui.client.kineticengine.GraphItemCreator.GraphItemRegistration;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient.VisualStyle;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.CreateEdgeModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.DragEdgeModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.ItemRegistrationModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.SelectionModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.SelectionModule.SelectionOperation;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.TrackMouseModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.IEngineModule;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.base.BiDiOperation;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.ItemRegistrationOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.visualstyle.KineticBoxSettings;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.visualstyle.KineticEdgeSettings;

/**
 * The center of the whole kinetic environment implementation. Defines
 * the essential infrastructure for all the rest of the code.
 * 
 * @author SkyCrawl
 */
@SuppressWarnings("deprecation")
public final class KineticEngine {
	/**
	 * Definition of components to publicly invoke draw commands on.
	 */
	public static enum EngineComponent {
		STAGE, LAYER_BOXES, LAYER_EDGES, LAYER_SELECTION
	}

	/**
	 * The kinetic stage to draw everything in.
	 */
	private Stage stage;

	/**
	 * The layer where boxes and related shapes will be drawn. This layer will have the lowest ZIndex.
	 */
	private final Layer layer1_boxes;

	/**
	 * Special layer for selected content. This layer will have the highest ZIndex.
	 */
	private final Layer layer2_selection;

	/**
	 * The layer where edges and related shapes will be drawn. This layer will be above layer1 and below layer3.
	 */
	private final Layer layer3_edges;

	/**
	 * A rectangle with dimensions identical to dimensions of the whole stage. This is solely to allow
	 * mouse events capturing outside of any other groups or shapes.
	 */
	private final Rectangle fillRectangle;

	/**
	 * A "rectangle" to display the selection area. The PathSVG shape is used to emulate the Rectangle shape because:
	 * 1) It doesn't cover the rectangle area and thus doesn't overdraw the shapes it intersects.
	 * 2) The Line shape no longer extends Polygon shape and can not consist of more than 2 points. 
	 */
	private final MultiSelectionRectangle multiSelectionRectangle;

	/**
	 * Plugins extend the functionality of the engine to allow nice and easy separation of problems and code. This avoids an incredible mess
	 * and hugely improves bug-free modifiability.
	 * Maps plugin ids to instances so each plugin is only held in a single instance.
	 */
	private final Map<String, IEngineModule> modules;

	/**
	 * Plugins will want to attach event handlers/listeners to graph items.
	 * Maps names of graph items to a list of plugins that need to attach event handlers/listeners to them.
	 */
	private final Map<String, Set<IEngineModule>> modulesForGraphItem;

	/**
	 * The wrapper class providing contextual information and server communication.
	 */
	private KineticComponentWidget context;

	/*
	 * Individual plugins that will be used a lot in this class:
	 */
	private final ItemRegistrationModule itemRegistrationModule;
	private final SelectionModule selectionModule;

	/*
	 * References to graph item visual settings.
	 */
	private KineticBoxSettings settings_box;
	private KineticEdgeSettings settings_edge;

	/**
	 * All the event handlers of the engine.
	 */
	private final IEventListener fillRectangleMouseDownHandler = new IEventListener() {
		@Override
		public void handle(KineticEvent event) {
			// first deselect everything and draw selection layer if anything is deselected
			selectionModule.doSelectionRelatedOperation(SelectionOperation.DESELECTION, true, true, selectionModule.getSelectedBoxes());

			// preparation for mousemove
			multiSelectionRectangle.setOriginalMousePosition(getMousePosition());
			multiSelectionRectangle.getMasterNode().show();
			multiSelectionRectangle.getMasterNode().moveToTop();
			fillRectangle.moveToTop();

			fillRectangle.addEventListener(fillRectangleMouseMoveHandler, EventType.Basic.MOUSEMOVE);
			event.stopVerticalPropagation();
		}
	};
	private final IEventListener fillRectangleMouseMoveHandler = new IEventListener() {
		@Override
		public void handle(KineticEvent event) {
			Vector2d currentPosition = getMousePosition();
			multiSelectionRectangle.updatePath(new Vector2d(multiSelectionRectangle.getOriginalMousePosition().x, currentPosition.y), currentPosition, new Vector2d(currentPosition.x,
					multiSelectionRectangle.getOriginalMousePosition().y));
			draw(EngineComponent.LAYER_SELECTION);
			event.stopVerticalPropagation();
		}
	};
	private final IEventListener fillRectangleMouseUpHandler = new IEventListener() {
		@Override
		public void handle(KineticEvent event) {
			// TODO: make this faster with smarter collision detection!

			// handle the event
			Set<BoxGraphItemClient> boxesToSelect = new HashSet<BoxGraphItemClient>();
			for (BoxGraphItemClient box : itemRegistrationModule.getRegisteredBoxes()) {
				if (!box.isSelected() && box.intersects(multiSelectionRectangle.getSelection())) {
					boxesToSelect.add(box);
				}
			}
			if (!boxesToSelect.isEmpty()) {
				selectionModule.doSelectionRelatedOperation(SelectionOperation.SELECTION, false, true, boxesToSelect.toArray(new BoxGraphItemClient[0]));
			}

			// return the dynamic layer state to the original
			multiSelectionRectangle.reset();
			multiSelectionRectangle.getMasterNode().hide();
			fillRectangle.moveToBottom();
			fillRectangle.removeEventListener(EventType.Basic.MOUSEMOVE);

			// and draw
			draw(EngineComponent.STAGE);
			event.stopVerticalPropagation();
		}
	};

	/*
	 * GENERAL NOTES ABOUT KINETIC BEHAVIOUR:
	 * - Each node can only have 1 parent (container) - a group or a layer! Beware of what happens when you cross add a node into a different layer...
	 * - When moving a node from a group to a layer, both layers need to be drawn, if distinct.
	 * - Hidden objects don't listen for events... makes sense :).
	 * - When a group is dragged, leaf draggable nodes get updated (X and Y attributes)... not the group itself. Make sure to set children not draggable :).
	 * - When something really odd happens:
	 * 		a) check whether event handlers are not "stealing" events with the "event.stopPropagation()" command, 
	 * 		b) debug thoroughly to check whether a silent javascript exception is caused (happens sometimes).
	 * - Events are fired in the order they were added, whether named or not.
	 * - Events can not be simulated from named events... bug?
	 * - Javascript can not convert objects to arbitrary type, so beware of using the "toArray()" method. Use "toArray(T[] array)" instead.
	 */

	/**
	 * Constructor.
	 * Remember that this constructor only creates and partly sets up inner fields. The
	 * {@link #setContext()} method needs to be used to fully initialize the engine. 
	 */
	public KineticEngine() {
		/*
		 * First setup the basic Kinetic variables.
		 */

		this.stage = null;
		this.layer1_boxes = Kinetic.createLayer();
		this.layer2_selection = Kinetic.createLayer();
		this.layer3_edges = Kinetic.createLayer();

		/*
		 * Setup internal layer variables and objects.
		 */

		this.fillRectangle = Kinetic.createRectangle(new Box2d(Vector2d.origin, Vector2d.origin));
		this.fillRectangle.setOpacity(0); // hidden objects don't listen for events, so it has to be fully transparent instead
		this.fillRectangle.setDraggable(false);
		this.layer1_boxes.add(this.fillRectangle); // has to be the selection layer since it is the topmost layer by default
		setFillRectangleHandlers();

		/* 
		// TODO: simpler multi-selection using the stage... but event propagation bug!
		final IEventListener stageContentMouseOver = new IEventListener()
		{
			@Override
			public void handle(KineticEvent event)
			{
				System.out.println("MouseMove!");
			}
		};
		this.stage.addEventListener(EventTypeStageBasic.contentMousedown, new IEventListener()
		{
			@Override
			public void handle(KineticEvent event)
			{
				System.out.println("MouseDown!");
				stage.addEventListener(EventTypeStageBasic.contentMousemove, stageContentMouseOver);
			}
		});
		this.stage.addEventListener(EventTypeStageBasic.contentMouseup, new IEventListener()
		{
			@Override
			public void handle(KineticEvent event)
			{
				stage.removeEventListener(EventTypeStageBasic.contentMousemove);
				System.out.println("MouseUp!");
			}
		});
		*/

		this.multiSelectionRectangle = new MultiSelectionRectangle();
		this.layer2_selection.add(multiSelectionRectangle.getMasterNode());
		multiSelectionRectangle.getMasterNode().hide();

		/*
		 * Setup special feature modules.
		 * 
		 * IMPORTANT: don't violate the call order. Modules add click handlers to graph
		 * items (in the order they are defined here) and some prevent further processing of
		 * events with other modules.
		 */
		this.modules = new HashMap<String, IEngineModule>();
		this.modulesForGraphItem = new HashMap<String, Set<IEngineModule>>();
		addModule(new TrackMouseModule(this));
		addModule(new CreateEdgeModule(this)); // this has to be before selection module
		addModule(new SelectionModule(this));
		addModule(new ItemRegistrationModule(this));
		addModule(new DragEdgeModule(this));
		this.itemRegistrationModule = (ItemRegistrationModule) getModule(ItemRegistrationModule.moduleID);
		this.selectionModule = (SelectionModule) getModule(SelectionModule.moduleID);

		for (IEngineModule module : modules.values()) {
			module.createModuleCrossReferences();
		}

		this.context = null;
		this.settings_box = null;
		this.settings_edge = null;
	}

	// *****************************************************************************************************
	// SERIALIZATION/DESERIALIZATION INTERFACE

	/*
	public AbstractExperimentGraph toIntermediateFormat()
	{
		AbstractExperimentGraph result = new AbstractExperimentGraph();
		
		// first convert all boxes
		Map<BoxGraphItemClient, String> boxToWebFormatID = new HashMap<BoxGraphItemClient, String>();
		for(BoxGraphItemClient box : itemRegistrationModule.getRegisteredBoxes())
		{
			Vector2d currentPosition = box.getAbsoluteNodePosition();
			box.getInfo().initialX = (int) currentPosition.x;
			box.getInfo().initialY = (int) currentPosition.y;
			boxToWebFormatID.put(box, result.addLeafBoxAndReturnID(box.getInfo()));
		}
		
		// then convert all edges
		Set<EdgeGraphItemClient> serializedEdges = new HashSet<EdgeGraphItemClient>(); 
		for(BoxGraphItemClient box : itemRegistrationModule.getRegisteredBoxes())
		{
			for(EdgeGraphItemClient edge : box.connectedEdges)
			{
				if(!serializedEdges.contains(edge))
				{
					String fromBoxID = boxToWebFormatID.get(edge.getEndPoint(EndPoint.FROM));
					String toBoxID = boxToWebFormatID.get(edge.getEndPoint(EndPoint.TO));
					result.connect(fromBoxID, toBoxID);
					serializedEdges.add(edge);
				}
			}
		}
		
		return result;
	}
	*/

	public void setExperiment(ExperimentGraphClient experiment) {
		// first convert all boxes
		Map<Integer, BoxGraphItemClient> guiBoxes = new HashMap<Integer, BoxGraphItemClient>();
		for (Integer leafBoxID : experiment.leafBoxes.keySet()) {
			guiBoxes.put(leafBoxID, getContext().getGraphItemCreator().createBox(GraphItemRegistration.MANUAL, experiment.leafBoxes.get(leafBoxID)));
		}

		// then convert all edges
		List<EdgeGraphItemClient> edges = new ArrayList<EdgeGraphItemClient>();
		for (Entry<Integer, Set<Integer>> entry : experiment.edges.entrySet()) {
			BoxGraphItemClient fromBox = guiBoxes.get(entry.getKey());
			for (Integer toLeafBoxID : entry.getValue()) {
				BoxGraphItemClient toBox = guiBoxes.get(toLeafBoxID);
				edges.add(getContext().getGraphItemCreator().createEdge(GraphItemRegistration.MANUAL, fromBox, toBox));
			}
		}

		// and finally, put everything into the environment but don't apply it to history so that the user can not erase the graph
		new ItemRegistrationOperation(this, guiBoxes.values().toArray(new BoxGraphItemClient[0]), edges.toArray(new EdgeGraphItemClient[0]), false).redo();
	}

	// *****************************************************************************************************
	// INTERFACE TO USE FROM CONTEXT

	/**
	 * This method only resets the engine, e.g. boxes, edges and selection. Further cleanup (e.g. history)
	 * is expected to be done in the calling code.
	 */
	public void destroyGraphAndClearStage() {
		itemRegistrationModule.destroyGraphAndClearStage();
	}

	public void resize(int newWidth, int newHeight) {
		Vector2d newSize = new Vector2d(newWidth, newHeight);
		stage.setSize(newSize);
		fillRectangle.setSize(newSize);
		stage.draw();
	}

	public void setWidgetContext(KineticComponentWidget context) {
		/*
		 * First setup the stage.
		 */
		if (this.stage != null) {
			this.layer1_boxes.remove();
			this.layer2_selection.remove();
			this.layer3_edges.remove();
			this.stage.destroy();
		}
		this.stage = Kinetic.createStage(context.getStageDOMElement());
		this.stage.add(layer1_boxes);
		this.stage.add(layer2_selection);
		this.stage.add(layer3_edges);

		/*
		 * Then setup layer z-indexes.
		 * NOTE: ZIndex = value that determines priority for event handlers when 2 objects collide. The higher ZIndex, the closer to the user. 
		 */

		setFirstArgHigherZIndex(layer2_selection, layer1_boxes);
		setFirstArgHigherZIndex(layer3_edges, layer2_selection);

		// and do the rest
		this.fillRectangle.setSize(this.stage.getSize());
		this.context = context;
	}

	/**
	 * Highlight boxes only for a single stage repaint.
	 */
	public void highlightUntilNextRepaint(Integer[] boxIDs) {
		for (Integer id : boxIDs) {
			BoxGraphItemClient box = itemRegistrationModule.getBoxByID(id);
			box.setVisualStyle(VisualStyle.HIGHLIGHTED_SLOT);
		}
		draw(EngineComponent.LAYER_BOXES);
		for (Integer id : boxIDs) {
			BoxGraphItemClient box = itemRegistrationModule.getBoxByID(id);
			box.setVisualStyle(VisualStyle.NOT_HIGHLIGHTED_SLOT);
		}
	}

	public void cancelSelection() {
		selectionModule.doSelectionRelatedOperation(SelectionOperation.DESELECTION, true, false, selectionModule.getSelectedBoxes());
	}

	public void applyVisualStyle(KineticBoxSettings boxSettings, KineticEdgeSettings edgeSettings) {
		if ((this.settings_box == null) || (this.settings_edge == null) || !this.settings_box.equals(boxSettings) || !this.settings_edge.equals(edgeSettings)) {
			this.settings_box = boxSettings;
			this.settings_edge = edgeSettings;

			for (BoxGraphItemClient box : itemRegistrationModule.getRegisteredBoxes()) {
				box.applySettings(boxSettings);
			}
			for (EdgeGraphItemClient edge : itemRegistrationModule.getRegisteredEdges()) {
				edge.applySettings(edgeSettings);
			}
			draw(EngineComponent.STAGE);
		}
	}

	// *****************************************************************************************************
	// IMPLEMENTATION INDEPENDENT PUBLIC INTERFACE

	/*
	 * Module related public routines.
	 */

	public IEngineModule getModule(String id) {
		return modules.get(id);
	}

	public void addModule(IEngineModule module) {
		modules.put(module.getModuleID(), module);
		String[] itemsToAttachTo = module.getGraphItemTypesToAttachHandlersTo();
		if (itemsToAttachTo != null) {
			for (String graphItemName : itemsToAttachTo) {
				if (!modulesForGraphItem.containsKey(graphItemName)) {
					// LinkedHashSet retains the insertion order which is very important here
					modulesForGraphItem.put(graphItemName, new LinkedHashSet<IEngineModule>());
				}
				modulesForGraphItem.get(graphItemName).add(module);
			}
		}
	}

	public void attachModuleHandlersTo(AbstractGraphItemClient<?> graphItem) {
		for (IEngineModule module : modulesForGraphItem.get(GWTMisc.getSimpleName(graphItem.getClass()))) {
			module.attachHandlers(graphItem);
		}
	}

	public BoxGraphItemClient getHoveredBox() {
		return ((TrackMouseModule) getModule(TrackMouseModule.moduleID)).getCurrentlyHoveredBox();
	}

	/*
	 * Miscellaneous public routines.
	 */

	public KineticComponentWidget getContext() {
		return context;
	}

	public KineticBoxSettings getBoxSettings() {
		return settings_box;
	}

	public KineticEdgeSettings getEdgeSettings() {
		return settings_edge;
	}

	public Container getContainer(EngineComponent component) {
		switch (component) {
		case LAYER_BOXES:
			return layer1_boxes;
		case LAYER_EDGES:
			return layer3_edges;
		case LAYER_SELECTION:
			return layer2_selection;
		case STAGE:
			return stage;
		default:
			throw new IllegalStateException();
		}
	}

	public Container getSelectionContainer() {
		return selectionModule.getSelectionContainer();
	}

	public Node getFillRectangle() {
		return fillRectangle;
	}

	public Vector2d getMousePosition() {
		return stage.getPointerPosition();
	}

	public void pushToHistory(BiDiOperation operation) {
		getContext().getHistoryManager().push(operation);
		operation.redo();
	}

	public void draw(EngineComponent component) {
		getContainer(component).draw();
	}

	public void removeFillRectangleHandlers() {
		this.fillRectangle.removeEventListener(EventType.Basic.MOUSEDOWN, EventType.Basic.MOUSEMOVE, EventType.Basic.MOUSEUP);
	}

	public void setFillRectangleHandlers() {
		removeFillRectangleHandlers();
		this.fillRectangle.addEventListener(fillRectangleMouseDownHandler, EventType.Basic.MOUSEDOWN);
		// this.fillRectangle.addEventListener(fillRectangleMouseMoveHandler, EventType.Basic.MOUSEMOVE); // don't uncomment - mouse down handler does this
		this.fillRectangle.addEventListener(fillRectangleMouseUpHandler, EventType.Basic.MOUSEUP);
	}

	// *****************************************************************************************************
	// MISCELLANEOUS PRIVATE INTERFACE

	private void setFirstArgHigherZIndex(Node node1, Node node2) {
		if (node1.getZIndex() == node2.getZIndex()) {
			node1.setZIndex(node2.getZIndex() + 1);
		} else if (node1.getZIndex() < node2.getZIndex()) {
			swapZIndexes(node1, node2);
		}
	}

	private void swapZIndexes(Node node1, Node node2) {
		int tempZIndex = node1.getZIndex();
		node1.setZIndex(node2.getZIndex());
		node2.setZIndex(tempZIndex);
	}
}
