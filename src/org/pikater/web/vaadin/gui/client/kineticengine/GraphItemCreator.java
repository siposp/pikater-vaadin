package org.pikater.web.vaadin.gui.client.kineticengine;

import org.pikater.web.experiment.client.BoxInfoClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient.EdgeState;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient.EndPoint;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.ItemRegistrationOperation;

/**
 * A helper class to create (and register, should you wish it)
 * graph items.
 * 
 * @author SkyCrawl
 */
public class GraphItemCreator {
	/**
	 * Denotes whether a newly created item should be added to the environment
	 * automatically or the calling code takes responsibility to do it.</br>
	 * Always use AUTOMATIC unless we add many nodes at once - then it is more
	 * efficient to call batch creation callback on kinetic state.  
	 */
	public static enum GraphItemRegistration {
		AUTOMATIC, MANUAL
	}

	/**
	 * The main structure of kinetic's own variables. 
	 */
	private final KineticEngine kineticEngine;

	public GraphItemCreator(KineticEngine kineticEngine) {
		this.kineticEngine = kineticEngine;
	}

	/**
	 * Creates a fully initialized box, if registered automatically. Only use this method when creating boxes in the GUI.
	 */
	public BoxGraphItemClient createBox(GraphItemRegistration nrt, BoxInfoClient info) {
		BoxGraphItemClient result = new BoxGraphItemClient(kineticEngine, info);
		result.reloadVisualStyles(false);
		if (nrt == GraphItemRegistration.AUTOMATIC) {
			kineticEngine.pushToHistory(new ItemRegistrationOperation(kineticEngine, new BoxGraphItemClient[] { result }, null, true));
		}
		return result;
	}

	/**
	 * Creates a fully initialized edge, if registered automatically and both endpoints are not null (they can be).
	 */
	public EdgeGraphItemClient createEdge(GraphItemRegistration nrt, BoxGraphItemClient fromBox, BoxGraphItemClient toBox) {
		EdgeGraphItemClient result = new EdgeGraphItemClient(kineticEngine);
		result.reloadVisualStyles(false);
		result.setEndpoint(EndPoint.FROM, fromBox);
		result.setEndpoint(EndPoint.TO, toBox);
		if ((fromBox != null) && (toBox != null)) {
			result.setInternalState(EdgeState.EDGE);
			result.updateEdge();
		} else {
			result.setInternalState(EdgeState.BASELINE);
		}
		if (nrt == GraphItemRegistration.AUTOMATIC) {
			kineticEngine.pushToHistory(new ItemRegistrationOperation(kineticEngine, null, new EdgeGraphItemClient[] { result }, true));
		}
		return result;
	}
}