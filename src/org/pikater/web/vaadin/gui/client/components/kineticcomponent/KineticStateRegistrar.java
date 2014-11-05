package org.pikater.web.vaadin.gui.client.components.kineticcomponent;

import java.util.HashMap;
import java.util.Map;

/** 
 * Utility class temporarily storing client-side kinetic states,
 * until picked up again. For more information about what it is
 * supposed to mean, refer to {@link KineticState}.
 * 
 * @author SkyCrawl
 */
public class KineticStateRegistrar {
	private static final Map<String, KineticState> connectorIDToSavedState = new HashMap<String, KineticState>();

	public static void saveState(String connectorID, KineticState state) {
		connectorIDToSavedState.put(connectorID, state);
	}

	public static KineticState getSavedState(String connectorID) {
		return connectorIDToSavedState.get(connectorID);
	}
}