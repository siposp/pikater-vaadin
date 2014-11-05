package org.pikater.web.vaadin.gui.client.kineticengine.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTMisc;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.SelectionModule.SelectionOperation;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.IEngineModule;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.AbstractGraphItemShared.RegistrationOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.EdgeGraphItemShared;

/**
 * Module implementing item registering/unregistering in the kinetic canvas
 * (environment).
 * 
 * @author SkyCrawl
 */
public class ItemRegistrationModule implements IEngineModule {
	public static String moduleID;

	/**
	 * The engine instance to work with.
	 */
	private final KineticEngine kineticEngine;

	/**
	 * Selection plugin is needed for manual deselection.
	 */
	private SelectionModule selectionModule;

	/**
	 * A self-explanatory variable.
	 */
	private final Map<Integer, BoxGraphItemClient> allRegisteredBoxes;

	/**
	 * A self-explanatory variable.
	 */
	private final Set<EdgeGraphItemClient> allRegisteredEdges;

	/**
	 * Constructor.
	 */
	public ItemRegistrationModule(KineticEngine engine) {
		moduleID = GWTMisc.getSimpleName(this.getClass());
		this.kineticEngine = engine;
		this.allRegisteredBoxes = new HashMap<Integer, BoxGraphItemClient>();
		this.allRegisteredEdges = new HashSet<EdgeGraphItemClient>();
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
		return new String[0];
	}

	@Override
	public void attachHandlers(AbstractGraphItemClient<?> graphItem) {
	}

	// *****************************************************************************************************
	// PUBLIC TYPES AND INTERFACE TO PERFORM ALL ITEM
	// REGISTRATION/UNREGISTRATION RELATED OPERATIONS

	/**
	 * Does the operation corresponding to the arguments, doesn't handle edges
	 * in any way. The following things are required to be done in the calling
	 * code:
	 * <ul>
	 * <li>Proper initialization of the given items.
	 * </ul>
	 */
	public void doOperation(RegistrationOperation opKind, boolean drawOnFinish,
			boolean notifyServer, BoxGraphItemClient... boxes) {
		// first deselect provided boxes, if necessary
		if (opKind == RegistrationOperation.UNREGISTER) {
			selectionModule.doSelectionRelatedOperation(
					SelectionOperation.DESELECTION, false, true, boxes);
		}

		// then do the action
		if (opKind == RegistrationOperation.REGISTER) {
			for (BoxGraphItemClient box : boxes) {
				box.applySettings(kineticEngine.getBoxSettings());
				box.setVisibleInKinetic(true);
				allRegisteredBoxes.put(box.getInfo().getID(), box);
			}
		} else {
			for (BoxGraphItemClient box : boxes) {
				box.setVisibleInKinetic(false);
				allRegisteredBoxes.remove(box.getInfo().getID());
			}
		}

		// send info to the server
		if ((boxes.length != 0) && notifyServer) {
			kineticEngine.getContext().command_boxSetChange(opKind,
					BoxGraphItemClient.toShared(boxes));
		}

		// and finally, request redraw of the stage
		if (drawOnFinish) {
			kineticEngine.draw(EngineComponent.STAGE);
		}
	}

	/**
	 * Does the operation corresponding to the arguments, doesn't handle boxes
	 * in any way. The following things are required to be done in the calling
	 * code:
	 * <ul>
	 * <li>Proper initialization of the given items.
	 * </ul>
	 */
	public void doOperation(RegistrationOperation opKind, boolean drawOnFinish,
			boolean notifyServer, EdgeGraphItemClient... edges) {
		boolean visible = opKind == RegistrationOperation.REGISTER;
		for (EdgeGraphItemClient edge : edges) {
			if (edge.isSelected()) {
				/*
				 * Edges are assumed to have been deselected in {@link
				 * #doOperation(RegistrationOperation, boolean,
				 * BoxGraphItemClient...)} or the calling code.
				 */
				throw new IllegalStateException(
						"Can not register a selected edge. Deselect first.");
			}
			if (edge.areBothEndsDefined()) {
				edge.setEdgeRegisteredInEndpoints(visible);
			}

			// only register unregistered edges and vice versa - don't register
			// an already registered edge again
			if ((opKind == RegistrationOperation.REGISTER)
					&& !edge.getMasterNode().isRegistered()) {
				edge.applySettings(kineticEngine.getEdgeSettings());
				edge.setVisibleInKinetic(true);
				allRegisteredEdges.add(edge);
			} else if ((opKind == RegistrationOperation.UNREGISTER)
					&& edge.getMasterNode().isRegistered()) {
				edge.setVisibleInKinetic(false);
				allRegisteredEdges.remove(edge);
			}
		}

		// send info to the server
		if ((edges.length != 0) && notifyServer) {
			EdgeGraphItemShared[] serializedEdges = EdgeGraphItemClient
					.toShared(edges);
			if (serializedEdges.length > 0) {
				kineticEngine.getContext().command_edgeSetChange(opKind,
						serializedEdges);
			}
		}

		if (drawOnFinish) {
			kineticEngine.draw(EngineComponent.STAGE);
		}
	}

	/**
	 * Does exactly what the method's name suggests.</br> This method is
	 * supposed to be called from the server and only resets the engine (boxes,
	 * edges and selection). Further cleanup (e.g. history) is expected to be
	 * done in the calling code.
	 */
	public void destroyGraphAndClearStage() {
		// then destroy edges
		for (EdgeGraphItemClient edge : allRegisteredEdges) {
			edge.setEdgeRegisteredInEndpoints(false);
			edge.setVisibleInKinetic(false);
			edge.destroy();
		}

		// destroy boxes
		for (BoxGraphItemClient box : allRegisteredBoxes.values()) {
			box.destroy();
		}

		// and final cleanup
		allRegisteredBoxes.clear();
		allRegisteredEdges.clear();
	}

	// *****************************************************************************************************
	// OTHER PUBLIC INTERFACE

	public BoxGraphItemClient getBoxByID(Integer id) {
		return allRegisteredBoxes.get(id);
	}

	public BoxGraphItemClient[] getRegisteredBoxes() {
		return allRegisteredBoxes.values().toArray(new BoxGraphItemClient[0]);
	}

	public EdgeGraphItemClient[] getRegisteredEdges() {
		return allRegisteredEdges.toArray(new EdgeGraphItemClient[0]);
	}
}
