package org.pikater.web.experiment.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.pikater.shared.util.SimpleIDGenerator;
import org.pikater.web.experiment.IExperimentGraph;
import org.pikater.web.experiment.server.ExperimentGraphServer;

/**
 * A special (GWT&Vaadin)-compliant experiment graph implementation. As such,
 * fields are required to be public and a public default constructor is needed.
 * 
 * This class is only used to efficiently transport already defined experiments
 * to the client. All experiments in experiment editor are actually stored on
 * the server.
 * 
 * @author SkyCrawl
 * @see {@link ExperimentGraphServer}
 */
public class ExperimentGraphClient implements Serializable,
		IExperimentGraph<Integer, BoxInfoClient> {
	private static final long serialVersionUID = 5653859184631065811L;

	/**
	 * ID generator for boxes.
	 */
	public SimpleIDGenerator boxIDGenerator;

	/**
	 * The 1:1 map containing all the boxes.
	 */
	public Map<Integer, BoxInfoClient> leafBoxes;

	/**
	 * Collection of oriented edges (between boxes), sorted by the
	 * "from end point".
	 */
	public Map<Integer, Set<Integer>> edges;

	// ------------------------------------------------------------------
	// CONSTRUCTOR

	/**
	 * Default public constructor keeps Vaadin happy.
	 */
	public ExperimentGraphClient() {
		this.boxIDGenerator = new SimpleIDGenerator();
		this.leafBoxes = new HashMap<Integer, BoxInfoClient>();
		this.edges = new HashMap<Integer, Set<Integer>>();
	}

	// ------------------------------------------------------------------
	// INHERITED INTERFACE

	@Override
	public Iterator<BoxInfoClient> iterator() {
		return leafBoxes.values().iterator();
	}

	@Override
	public boolean containsBox(Integer boxID) {
		return leafBoxes.containsKey(boxID);
	}

	@Override
	public BoxInfoClient getBox(Integer boxID) {
		return leafBoxes.get(boxID);
	}

	@Override
	public BoxInfoClient addBox(BoxInfoClient box) {
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
	public boolean isEmpty() {
		return leafBoxes.isEmpty();
	}

	@Override
	public boolean hasOutputEdges(Integer boxID) {
		return (edges.get(boxID) != null) && !edges.get(boxID).isEmpty();
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
	// PRIVATE INTERFACE

	/**
	 * Either connects or disconnects two given boxes, depending on the the last
	 * argument.
	 */
	private void doEdgeAction(Integer fromBoxKey, Integer toBoxKey,
			boolean connect) {
		/*
		 * Let this method have a transaction-like manner and only alter the
		 * data when everything has been checked and approved.
		 */

		// first, all kinds of checks before actually doing anything significant
		if ((fromBoxKey == null) || (toBoxKey == null)) { // boxes have not been
															// added to the
															// structure
			throw new IllegalArgumentException(
					"Cannot add this edge because at least one of the boxes was not added to the structure. "
							+ "Call the 'addBox()' method first and try again.");
		}
		if (!leafBoxes.containsKey(fromBoxKey)
				|| !leafBoxes.containsKey(toBoxKey)) {
			throw new IllegalArgumentException(
					"One of the supplied box keys represents a wrapper box. Cannot add edges to wrapper boxes.");
		}
		if (connect) { // we want to connect the boxes
			if ((edges.get(fromBoxKey) != null)
					&& edges.get(fromBoxKey).contains(toBoxKey)) // an edge
																	// already
																	// exists
			{
				throw new IllegalStateException("Cannot add an edge from box '"
						+ String.valueOf(fromBoxKey) + "' to box '"
						+ String.valueOf(toBoxKey)
						+ "': they are already connected.");
			}
		} else { // we want to disconnect the boxes
			if ((edges.get(fromBoxKey) == null)
					|| !edges.get(fromBoxKey).contains(toBoxKey)) // the edge
																	// doesn't
																	// exist
			{
				throw new IllegalStateException(
						"Cannot remove the edge from box '"
								+ String.valueOf(fromBoxKey) + "' to box '"
								+ String.valueOf(toBoxKey)
								+ "': they are not connected.");
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
}
