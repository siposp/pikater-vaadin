package org.pikater.web.experiment.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.pikater.core.ontology.subtrees.agentinfo.AgentInfo;
import org.pikater.core.ontology.subtrees.agentinfo.Slot;
import org.pikater.shared.experiment.UniversalComputationDescription;
import org.pikater.shared.experiment.UniversalElement;
import org.pikater.shared.experiment.UniversalElementConnector;
import org.pikater.shared.util.SimpleIDGenerator;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.config.KnownCoreAgents;
import org.pikater.web.experiment.IExperimentGraph;
import org.pikater.web.experiment.client.ExperimentGraphClient;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.ConversionException;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;

/**
 * Web application's notion of experiment graph.
 * 
 * @author SkyCrawl
 * @see {@link KineticComponent}
 */
public class ExperimentGraphServer implements IExperimentGraph<Integer, BoxInfoServer>, ISlotConnectionsContext<Integer, BoxInfoServer> {
	/**
	 * ID generator for boxes. 
	 */
	private final SimpleIDGenerator boxIDGenerator;

	/**
	 * The 1:1 map containing all the boxes.
	 */
	private final Map<Integer, BoxInfoServer> leafBoxes;

	/**
	 * Collection of oriented edges between boxes, sorted by the "from end point".</br>
	 * Unlike slots, only one edge is allowed between a pair of boxes. The purpose of
	 * this is to eliminate the need of having a multi-graph.
	 */
	private final Map<Integer, Set<Integer>> edges;

	/**
	 * Stores connection related information about all (input, output) slots of every box.</br>
	 * Unlike edges, slots of a single box may be connected to slots of multiple other boxes.
	 */
	private final SlotConnections slotConnections;

	// ------------------------------------------------------------------
	// CONSTRUCTOR

	/**
	 * Default public constructor keeps Vaadin happy.
	 */
	public ExperimentGraphServer() {
		this.boxIDGenerator = new SimpleIDGenerator();
		this.leafBoxes = new HashMap<Integer, BoxInfoServer>();
		this.edges = new HashMap<Integer, Set<Integer>>();
		this.slotConnections = new SlotConnections(this);
	}

	// ------------------------------------------------------------------
	// INHERITED INTERFACE

	@Override
	public Iterator<BoxInfoServer> iterator() {
		return leafBoxes.values().iterator();
	}

	@Override
	public boolean isEmpty() {
		return leafBoxes.isEmpty();
	}

	@Override
	public BoxInfoServer getBox(Integer boxID) {
		return leafBoxes.get(boxID);
	}

	@Override
	public boolean containsBox(Integer boxID) {
		return leafBoxes.containsKey(boxID);
	}

	@Override
	public BoxInfoServer addBox(BoxInfoServer box) {
		box.setID(boxIDGenerator.getAndIncrement());
		leafBoxes.put(box.getID(), box);
		return box;
	}

	@Override
	public void clear() {
		leafBoxes.clear();
		edges.clear();
		boxIDGenerator.reset();
	}

	@Override
	public boolean hasOutputEdges(Integer boxID) {
		return (edges.get(boxID) != null) && !edges.get(boxID).isEmpty();
	}

	@Override
	public Set<BoxInfoServer> getFromNeighbours(Integer boxID) {
		Set<BoxInfoServer> result = new HashSet<BoxInfoServer>();
		if (hasOutputEdges(boxID)) {
			for (Integer otherBoxID : edges.get(boxID)) {
				if (edgeExistsBetween(boxID, otherBoxID)) {
					result.add(getBox(otherBoxID));
				}
			}
		}
		return result;
	}

	@Override
	public Set<BoxInfoServer> getToNeighbours(Integer boxID) {
		Set<BoxInfoServer> result = new HashSet<BoxInfoServer>();
		for (BoxInfoServer otherBox : leafBoxes.values()) {
			if (edgeExistsBetween(otherBox.getID(), boxID)) {
				result.add(otherBox);
			}
		}
		return result;
	}

	@Override
	public boolean edgeExistsBetween(Integer fromBoxID, Integer toBoxID) {
		if (!containsBox(fromBoxID) || !containsBox(toBoxID)) {
			throw new IllegalStateException("There seems to be a BIG problem somewhere...");
		} else {
			return (edges.get(fromBoxID) != null) && edges.get(fromBoxID).contains(toBoxID);
		}
	}

	@Override
	public void connect(Integer fromBoxKey, Integer toBoxKey) {
		doEdgeAction(fromBoxKey, toBoxKey, true);
	}

	@Override
	public void disconnect(Integer fromBoxKey, Integer toBoxKey) {
		doEdgeAction(fromBoxKey, toBoxKey, false);
	}

	// ------------------------------------------------------------------
	// OTHER PUBLIC INTERFACE

	/**
	 * See {@link SlotConnections}.
	 */
	public SlotConnections getSlotConnections() {
		return slotConnections;
	}

	// ------------------------------------------------------------------
	// PRIVATE INTERFACE

	/**
	 * Either connects or disconnects two given boxes, depending on the the last argument.
	 */
	private void doEdgeAction(Integer fromBoxKey, Integer toBoxKey, boolean connect) {
		/*
		 * Let this method have a transaction-like manner and only alter the data when
		 * everything has been checked and approved.
		 */

		// first, all kinds of checks before actually doing anything significant
		if ((fromBoxKey == null) || (toBoxKey == null)) { // boxes have not been added to the structure
			throw new IllegalArgumentException("Cannot add this edge because at least one of the boxes was not added to the structure. " + "Call the 'addBox()' method first and try again.");
		}
		if (!leafBoxes.containsKey(fromBoxKey) || !leafBoxes.containsKey(toBoxKey)) {
			throw new IllegalArgumentException("One of the supplied box keys represents a wrapper box. Cannot add edges to wrapper boxes.");
		}
		if (connect) { // we want to connect the boxes
			if ((edges.get(fromBoxKey) != null) && edges.get(fromBoxKey).contains(toBoxKey)) { // an edge already exists
				throw new IllegalStateException("Cannot add an edge from box '" + String.valueOf(fromBoxKey) + "' to box '" + String.valueOf(toBoxKey) + "': they are already connected.");
			}
		} else { // we want to disconnect the boxes
			if ((edges.get(fromBoxKey) == null) || !edges.get(fromBoxKey).contains(toBoxKey)) { // the edge doesn't exist
				throw new IllegalStateException("Cannot remove the edge from box '" + String.valueOf(fromBoxKey) + "' to box '" + String.valueOf(toBoxKey) + "': they are not connected.");
			}
		}

		// and finally, let's add or remove the edge
		if (connect) {
			// add edge
			if (!edges.containsKey(fromBoxKey)) {
				edges.put(fromBoxKey, new HashSet<Integer>());
			}
			edges.get(fromBoxKey).add(toBoxKey);
		} else {
			// remove edge
			edges.get(fromBoxKey).remove(toBoxKey);
		}
	}

	//---------------------------------------------------------------
	// FORMAT CONVERSIONS

	public ExperimentGraphClient toClientFormat() {
		ExperimentGraphClient result = new ExperimentGraphClient();
		for (BoxInfoServer box : leafBoxes.values()) {
			result.addBox(box.toClientFormat());
		}
		result.edges = new HashMap<Integer, Set<Integer>>(edges); // simply copy, all IDs are kept intact
		return result;
	}

	/**
	 * Converts web experiment format into universal experiment format.</br>
	 * This conversion is substantially simpler than its counterpart. It should always work.
	 * @throws ConversionException
	 */
	public UniversalComputationDescription toUniversalFormat(KnownCoreAgents agentInfoProvider) throws ConversionException {
		try {
			// first some checks
			if (agentInfoProvider == null) {
				throw new NullPointerException("The argument agent info provider is null.");
			}

			// create the result uni-format experiment
			UniversalComputationDescription result = new UniversalComputationDescription();

			// create uni-format master elements for all boxes that are registered
			// from now on, only iterate this collection
			Map<BoxInfoServer, UniversalElement> webBoxToUniBox = new HashMap<BoxInfoServer, UniversalElement>();
			for (BoxInfoServer webBox : leafBoxes.values()) {
				if (webBox.isRegistered()) {
					UniversalElement uniBox = new UniversalElement();
					webBoxToUniBox.put(webBox, uniBox);
				}
			}

			// traverse all boxes and pass all available/needed information to result uni-format
			for (Entry<BoxInfoServer, UniversalElement> entry : webBoxToUniBox.entrySet()) {
				// determine basic information and references
				BoxInfoServer webBox = entry.getKey();
				UniversalElement uniBox = entry.getValue();

				/*
				 * Set the static stuff.
				 */

				try {
					uniBox.getOntologyInfo().setOntologyClass(Class.forName(webBox.getAssociatedAgent().getOntologyClassName()));
					uniBox.getOntologyInfo().setAgentClass(webBox.getAssociatedAgent().getAgentClassName());
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException(String.format("Could not convert '%s' to a class instance. Has it been hardcoded to an agent and renamed? "
							+ "Or is the pikater core running in different version than web?", webBox.getAssociatedAgent().getOntologyClassName()), e);
				}
				uniBox.getOntologyInfo().setOptions(webBox.getAssociatedAgent().getOptions());

				/*
				 * Set the dynamic stuff.
				 */

				// position
				uniBox.getPresentationInfo().setX(webBox.getPosX());
				uniBox.getPresentationInfo().setY(webBox.getPosY());

				// edges
				if (hasOutputEdges(webBox.getID())) { // these edges are all valid (currently registered)
					for (Integer neighbourWebBoxID : edges.get(webBox.getID())) { // these edges are all valid (currently registered)
						BoxInfoServer neighbourWebBox = getBox(neighbourWebBoxID);
						UniversalElement neighbourUniBox = webBoxToUniBox.get(leafBoxes.get(neighbourWebBoxID));
						if (neighbourUniBox == null) {
							// edge leads to an unknown or unregistered box
							throw new IllegalStateException("Can not transform an edge with an unknown/unregistered endpoint.");
						} else {
							/*
							 * SERIALIZATION SCHEME:
							 * - edge: A (uniBox; webBox) -> B (neighbourUniBox, neighbourWebBox)
							 * - connector: outputDataType (output slot) -> inputDataType (input slot)
							 */

							boolean aConnectedWasAdded = false;
							for (Slot outputSlot : webBox.getAssociatedAgent().getOutputSlots()) {
								for (Slot inputSlot : neighbourWebBox.getAssociatedAgent().getInputSlots()) {
									if (slotConnections.areSlotsConnected(inputSlot, outputSlot)) {
										// construct the edge, with exact slot connection
										UniversalElementConnector connector = new UniversalElementConnector();
										connector.setFromElement(uniBox);
										connector.setOutputDataIdentifier(outputSlot.getName());
										connector.setInputDataIdentifier(inputSlot.getName());

										// distinguish between error and data slots
										if (inputSlot.isErrorSlot() && outputSlot.isErrorSlot()) {
											neighbourUniBox.getOntologyInfo().addInputErrorSlot(connector);
										} else {
											neighbourUniBox.getOntologyInfo().addInputDataSlot(connector);
										}
										aConnectedWasAdded = true;
									}
								}
							}
							/*
							 * It is necessary to check like below.
							 * <pre>neighbourUniBox.getOntologyInfo().getInputDataSlots().isEmpty()</pre>
							 * is not safe because connectors may have been added previously (for another pair of boxes).
							 */
							if (!aConnectedWasAdded) { // no slot connections are defined
								// at least remember the edge, with no slot connections whatsoever
								UniversalElementConnector connector = new UniversalElementConnector();
								connector.setFromElement(uniBox);
								neighbourUniBox.getOntologyInfo().addInputDataSlot(connector);
							}
						}
					}
				}
			}

			for (UniversalElement element : webBoxToUniBox.values()) {
				result.addElement(element);
			}
			return result;
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}

	/**
	 * Converts universal format experiments into web format experiments that are used
	 * to do the actual loading in kinetic environment within the user's browser.</br> 
	 * This method is very sensitive to changes (because of serialization to XML
	 * and back) to:
	 * <ul>
	 * <li> Universal format.
	 * <li> NewOption ontology. 
	 * </ul>
	 * These changes may cause exceptions when trying to convert (previously converted)
	 * experiments back to web format.
	 * @throws ConversionException
	 */
	public static ExperimentGraphServer fromUniversalFormat(KnownCoreAgents agentInfoProvider, UniversalComputationDescription uniFormat) throws ConversionException {
		try {
			// first some checks
			if (agentInfoProvider == null) {
				throw new NullPointerException("The argument agent info provider is null.");
			}
			if (uniFormat == null) {
				throw new NullPointerException("The argument uni-format is null.");
			} else if (!uniFormat.isValid()) {
				throw new NullPointerException("The argument uni-format is not valid.");
			} else if (!uniFormat.isPresentationCompatible()) {
				throw new IllegalArgumentException(String.format("The universal format below is not fully compatible with the GUI (web) format.\n%s", uniFormat.toXML()));
			} else {
				/*
				 * IMPORTANT: from this point, we assume that the argument universal format is valid.
				 */

				// and then onto the conversion
				ExperimentGraphServer webFormat = new ExperimentGraphServer();

				// first convert all boxes, set box positions
				Map<UniversalElement, Integer> uniBoxToWebBoxID = new HashMap<UniversalElement, Integer>();
				for (UniversalElement element : uniFormat.getAllElements()) {
					// determine agent info instance
					AgentInfo agentInfo = null;
					try {
						// guarantees the correct result object or an exception
						agentInfo = agentInfoProvider.getUnique(element.getOntologyInfo().getOntologyClass().getName(), element.getOntologyInfo().getAgentClass());
					} catch (Exception e) {
						PikaterWebLogger.logThrowable(String.format("No agent info instance was found for ontology '%s' and agent '%s'.", element.getOntologyInfo().getOntologyClass().getName(),
								element.getOntologyInfo().getAgentClass()), e);
						throw e;
					}

					// create web-format box and link it to uni-format box
					BoxInfoServer webBox = webFormat.addBox(new BoxInfoServer(agentInfo, element.getPresentationInfo().getX(), element.getPresentationInfo().getY()));
					uniBoxToWebBoxID.put(element, webBox.getID());
				}

				// then convert all edges
				for (UniversalElement element : uniFormat.getAllElements()) {
					Collection<UniversalElementConnector> dataAndErrorSlotConnections = new HashSet<UniversalElementConnector>();
					dataAndErrorSlotConnections.addAll(element.getOntologyInfo().getInputDataSlots());
					dataAndErrorSlotConnections.addAll(element.getOntologyInfo().getInputErrorSlots());
					for (UniversalElementConnector slotConnection : dataAndErrorSlotConnections) {
						/*
						 * SERIALIZATION SCHEME:
						 * - edge: A (edge.getFromElement(); fromBox) -> B (element, toBox)
						 * - connector: outputDataType (output slot) -> inputDataType (input slot)
						 */

						// connect boxes
						BoxInfoServer fromBox = webFormat.getBox(uniBoxToWebBoxID.get(slotConnection.getFromElement()));
						BoxInfoServer toBox = webFormat.getBox(uniBoxToWebBoxID.get(element));
						if (!webFormat.edgeExistsBetween(fromBox.getID(), toBox.getID())) { // protection against multiple slot connections through the same edge
							webFormat.connect(fromBox.getID(), toBox.getID());
						}

						// also connect slots if needed
						if ((slotConnection.getInputDataIdentifier() != null) && (slotConnection.getOutputDataIdentifier() != null)) {
							Slot fromBoxSlot = fromBox.getAssociatedAgent().fetchOutputSlotByName(slotConnection.getOutputDataIdentifier());
							Slot toBoxSlot = toBox.getAssociatedAgent().fetchInputSlotByName(slotConnection.getInputDataIdentifier());
							if ((fromBoxSlot == null) || (toBoxSlot == null)) {
								throw new IllegalStateException("Could not find a slot by name in resolved agent info. Invalid binding.");
							} else {
								webFormat.getSlotConnections().connect(new BoxSlot(fromBox, fromBoxSlot), new BoxSlot(toBox, toBoxSlot));
							}
						}
					}
				}

				// and finally, options... THIS IS THE TRICKY PART
				for (UniversalElement element : uniFormat.getAllElements()) {
					BoxInfoServer boxInfo = webFormat.getBox(uniBoxToWebBoxID.get(element));
					boxInfo.getAssociatedAgent().getOptions().mergeWith(element.getOntologyInfo().getOptions());
				}

				// conversion is finished, return:
				return webFormat;
			}
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}
}
