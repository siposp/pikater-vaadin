package org.pikater.web.vaadin.gui.client.kineticengine.graph;

import net.edzard.kinetic.Node;

/**
 * Common interface for all classes representing a kinetic node but not
 * extending it.
 * 
 * @author SkyCrawl
 */
public interface IKineticShapeWrapper {
	/**
	 * Gets the master node.
	 * 
	 * @return The node that contains the whole underlying structure.
	 */
	public Node getMasterNode();
}