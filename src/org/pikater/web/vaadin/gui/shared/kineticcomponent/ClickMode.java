package org.pikater.web.vaadin.gui.shared.kineticcomponent;

/**
 * This enumeration defines what happens when the user clicks on
 * a box in the client kinetic canvas.
 * 
 * @author SkyCrawl
 */
public enum ClickMode {
	/**
	 * The clicked box is selected/deselected.
	 */
	SELECTION,

	/**
	 * Triggers creation of a new edge. Two clicks on 2 distinct boxes define an oriented edge.
	 */
	CONNECTION;

	/**
	 * Returns the complement of this enum constant.
	 */
	public ClickMode getOther() {
		switch (this) {
		case CONNECTION:
			return SELECTION;
		case SELECTION:
			return CONNECTION;
		default:
			throw new IllegalStateException("Unknown click mode: " + name());
		}
	}
}
