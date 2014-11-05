package org.pikater.web.experiment.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pikater.core.CoreConstant.SlotDirection;
import org.pikater.core.ontology.subtrees.agentinfo.Slot;

/**
 * <p>Maps connected slots of a given {@link ExperimentGraphServer}.</br>
 * In each pair of connected slots, one has to be an input slot and 
 * the other an output slot. Each slot belongs to exactly one box. A
 * box may have multiple slots defined (either input or output).</p>
 * 
 * <p>All slots ARE REQUIRED to be represented by exactly one
 * {@link Slot} instance in the whole web application.</p>
 * 
 * @author SkyCrawl
 */
public class SlotConnections {
	/**
	 * Context for this class providing some additional interface.
	 */
	private final ISlotConnectionsContext<Integer, BoxInfoServer> context;

	/**
	 * A special field that lets us avoid indexing connections by
	 * {@link BoxSlot} and specifying additional arguments (beside
	 * {@link Slot slots}) to methods of this class. Let the usage be
	 * simple :).</br>
	 * This field requires the same condition as {@link #connectionMap}. 
	 */
	private final Map<Slot, BoxSlot> slotToBoxSlot;

	/**
	 * Field storing all the slot connections.</br>
	 * There are several very important things about this:
	 * <ul>
	 * <li> {@link Slot} is required NOT to have hashCode and equals
	 * methods defined, otherwise the Map will not compare keys by instance.
	 * <li> {@link BoxSlot} should not be used as key in this collection,
	 * because equals & hashCode methods would be required for it, which in
	 * turn require the same methods on subtypes.
	 * </ul>  
	 */
	private final Map<Slot, Set<BoxSlot>> connectionMap;

	/**
	 * Ctor.
	 * @param context Context for the slot mapper. See {@link ISlotConnectionsContext}
	 * for more information.
	 */
	public SlotConnections(ISlotConnectionsContext<Integer, BoxInfoServer> context) {
		this.context = context;
		this.slotToBoxSlot = new HashMap<Slot, BoxSlot>();
		this.connectionMap = new HashMap<Slot, Set<BoxSlot>>();
	}

	/**
	 * Connects the given 2 {@link BoxSlot endpoints}/slots, if possible, and throws exceptions
	 * otherwise.</br>
	 * No need to distinguish between input/output {@link BoxSlot endpoints}.
	 */
	public void connect(BoxSlot endPoint1, BoxSlot endPoint2) {
		if (!endPoint1.isValid() || !endPoint2.isValid()) {
			throw new IllegalStateException("One of the 2 arguments was not valid.");
		} else if (endPoint1.getChildSlotsType() == endPoint2.getChildSlotsType()) {
			throw new IllegalStateException("Can not make a connection between INPUT-INPUT or OUTPUT-OUTPUT slots.");
		} else if (!endPoint1.getChildSlot().isCompatibleWith(endPoint2.getChildSlot())) {
			throw new IllegalStateException("Slots were not compatible.");
		} else {
			BoxSlot ep1 = slotToBoxSlot.get(endPoint1.getChildSlot());
			BoxSlot ep2 = slotToBoxSlot.get(endPoint2.getChildSlot());
			oneWayConnect(ep1 != null ? ep1 : endPoint1, ep2 != null ? ep2 : endPoint2);
			oneWayConnect(ep2 != null ? ep2 : endPoint2, ep1 != null ? ep1 : endPoint1);
		}
	}

	/**
	 * Disconnects the given slots, if they were previously connected.
	 * Throws exceptions otherwise.</br>
	 * Input/output slots are not distinguished in this method. Both can be passed.
	 */
	public void disconnect(Slot slot1, Slot slot2) {
		if (!connectionExistsFor(slot1)) {
			throw new IllegalStateException("No connections exist for the given slot.");
		} else if (!connectionMap.get(slot1).remove(slotToBoxSlot.get(slot2))) {
			throw new IllegalStateException("The slots are not connected. Can not disconnect.");
		} else if (!connectionMap.get(slot2).remove(slotToBoxSlot.get(slot1))) {
			throw new IllegalStateException("Slot connection was only defined in one way. There is a bug in this class.");
		}
	}

	/**
	 * A self explanatory method.</br>
	 * No need to distinguish between input/output slots.
	 */
	public boolean areSlotsConnected(Slot slot1, Slot slot2) {
		if (connectionExistsFor(slot1) && connectionExistsFor(slot2)) {
			return connectionMap.get(slot1).contains(slotToBoxSlot.get(slot2));
		}
		return false;
	}

	/**
	 * Gets the list of {@link BoxSlot endpoints} that can be connected to the given
	 * {@link BoxSlot endpoint}. 
	 */
	public Set<BoxSlot> getCandidateEndpoints(BoxSlot endpoint) {
		Set<BoxSlot> result = new HashSet<BoxSlot>();
		Set<BoxInfoServer> otherBoxes = endpoint.getChildSlotsType() == SlotDirection.INPUT ? context.getToNeighbours(endpoint.getParentBox().getID()) : context.getFromNeighbours(endpoint
				.getParentBox().getID());
		for (BoxInfoServer otherBox : otherBoxes) {
			List<Slot> slotCollection = endpoint.getChildSlotsType() == SlotDirection.INPUT ? otherBox.getAssociatedAgent().getOutputSlots() : otherBox.getAssociatedAgent().getInputSlots();
			for (Slot otherSlot : slotCollection) {
				if (otherSlot.isCompatibleWith(endpoint.getChildSlot())) {
					BoxSlot endpointToAdd = slotToBoxSlot.get(otherSlot);
					result.add(endpointToAdd == null ? new BoxSlot(otherBox, otherSlot) : endpointToAdd);
				}
			}
		}
		return result;
	}

	/**
	 * Get all {@link BoxSlot endpoints} backed by an edge between the owner boxes
	 * and whose slots are connected to the given slot 
	 */
	public Set<BoxSlot> getConnectedValidEndpoints(Slot slot) {
		Set<BoxSlot> result = new HashSet<BoxSlot>();
		if (connectionExistsFor(slot)) {
			for (BoxSlot endPoint : connectionMap.get(slot)) {
				Integer fromBoxID = null;
				Integer toBoxID = null;
				if (endPoint.isFromEndpoint()) {
					fromBoxID = endPoint.getParentBox().getID();
					toBoxID = slotToBoxSlot.get(slot).getParentBox().getID();
				} else {
					fromBoxID = slotToBoxSlot.get(slot).getParentBox().getID();
					toBoxID = endPoint.getParentBox().getID();
				}
				if (context.edgeExistsBetween(fromBoxID, toBoxID)) {
					result.add(endPoint);
				}
			}
		}
		return result;
	}

	/** 
	 * Whether {@link #getConnectedValidEndpoints(Slot)} returns a non-empty result.
	 */
	public boolean isSlotConnectedToAValidEndpoint(Slot slot) {
		return !getConnectedValidEndpoints(slot).isEmpty();
	}

	/**
	 * A self explanatory convenience method.
	 */
	public boolean hasASlotConnected(BoxInfoServer box) {
		for (Slot inputSlot : box.getAssociatedAgent().getInputSlots()) {
			if (isSlotConnectedToAValidEndpoint(inputSlot)) {
				return true;
			}
		}
		for (Slot outputSlot : box.getAssociatedAgent().getOutputSlots()) {
			if (isSlotConnectedToAValidEndpoint(outputSlot)) {
				return true;
			}
		}
		return false;
	}

	//-------------------------------------------
	// PRIVATE INTERFACE

	/**
	 * IMPORTANT: doesn't distinguish between valid and invalid connections
	 * (whether an edge exists between the affected boxes or not). 
	 */
	private boolean connectionExistsFor(Slot slot) {
		if (connectionMap.containsKey(slot)) {
			Set<BoxSlot> connections = connectionMap.get(slot);
			return (connections != null) && !connections.isEmpty();
		} else {
			return false;
		}
	}

	/**
	 * Connects the given {@link BoxSlot endpoints} in the defined direction.
	 * @param from can be arbitrary {@link BoxSlot} instance
	 * @param to can be arbitrary {@link BoxSlot} instance
	 */
	private void oneWayConnect(BoxSlot from, BoxSlot to) {
		/*
		 * Create connections for slot if not yet defined.
		 */
		if (!connectionExistsFor(from.getChildSlot())) {
			connectionMap.put(from.getChildSlot(), new HashSet<BoxSlot>());
		}

		// connect the slots in the given order
		if (!connectionMap.get(from.getChildSlot()).add(to)) {
			/*
			 * A simple "safety" check (not really necessary to make this work but it could identify
			 * a potential bug).
			 */
			throw new IllegalStateException("These endpoints are already connected.");
		}
		slotToBoxSlot.put(from.getChildSlot(), from);
	}
}
