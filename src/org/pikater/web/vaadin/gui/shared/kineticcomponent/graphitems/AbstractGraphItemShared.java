package org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems;

/**
 * Base class for all graph items of client kinetic canvas, made especially
 * for communication between server and client, carrying only the most
 * essential information.
 * 
 * @author SkyCrawl
 */
public class AbstractGraphItemShared {
	/**
	 * Registration operation type (for client kinetic canvas and a particular item
	 * or set of items).
	 * 
	 * @author SkyCrawl
	 */
	public enum RegistrationOperation {
		/**
		 * Register and display items immediately.
		 */
		REGISTER,

		/**
		 * Remove items from the graph immediately but don't destroy or disconnect them.
		 */
		UNREGISTER
	}
}