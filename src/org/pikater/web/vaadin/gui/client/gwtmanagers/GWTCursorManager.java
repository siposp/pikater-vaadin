package org.pikater.web.vaadin.gui.client.gwtmanagers;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.google.gwt.user.client.Element;

/**
 * A utility class to provide easy stack-like cursor
 * style management.
 *  
 * @author SkyCrawl
 *
 */
public class GWTCursorManager {
	/**
	 * Currently supported cursor styles.
	 * 
	 * @author SkyCrawl
	 */
	public static enum MyCursor {
		/*
		 * All of these are standard CSS cursor styles.
		 */

		AUTO, CROSSHAIR, DEFAULT, POINTER, MOVE, TEXT, WAIT, HELP;

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}

	// *************************************************************************************
	// PUBLIC METHODS

	/**
	 * Set special cursor style to display when hovering over the specified element.
	 * 
	 */
	public static void setCursorType(Element element, MyCursor cursorType) {
		// backup the current cursor style
		int hashCode = element.hashCode();
		if (!previousCursors.containsKey(hashCode)) {
			previousCursors.put(hashCode, new Stack<String>());
		}
		previousCursors.get(hashCode).push(element.getStyle().getCursor());

		// and set the new
		element.getStyle().setProperty("cursor", cursorType.toString());
	}

	/**
	 * Set cursor style for this element that was previous to the current one.
	 * 
	 */
	public static void rollBackCursor(Element element) {
		element.getStyle().setProperty("cursor", previousCursors.get(element.hashCode()).pop());
	}

	// *************************************************************************************
	// PRIVATE FIELDS

	/**
	 * Stores cursor styles for elements when desired and to be changed, so that the change can be rolled back later. 
	 */
	private static final Map<Integer, Stack<String>> previousCursors = new HashMap<Integer, Stack<String>>();
}
