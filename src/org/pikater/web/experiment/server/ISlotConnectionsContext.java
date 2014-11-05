package org.pikater.web.experiment.server;

import java.util.Set;

import org.pikater.web.experiment.IBoxInfoCommon;

/**  
 * Context for {@link SlotConnections}.
 * 
 * @author SkyCrawl
 *
 * @param <I> The type for IDs. Typically Integers or Strings.
 * @param <B> The supported box type. Currently, experiments 
 * are stored on the server and hence, only {@link BoxInfoServer}
 * should be used.
 */
public interface ISlotConnectionsContext<I extends Object, B extends IBoxInfoCommon<I>> {
	/**
	 * Gets the list of boxes to which an edge leads from box
	 * with the given ID.
	 */
	Set<B> getFromNeighbours(I boxID);

	/**
	 * Gets the list of boxes from which an edge leads to box
	 * with the given ID.
	 */
	Set<B> getToNeighbours(I boxID);

	/**
	 * Does an edge lead from box with the given ID to box with the given ID?
	 */
	boolean edgeExistsBetween(I fromBoxID, I toBoxID);
}
