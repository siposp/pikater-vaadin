package org.pikater.web.vaadin.gui.shared.kineticcomponent.visualstyle;

import net.edzard.kinetic.Colour;

import org.pikater.web.vaadin.gui.client.kineticengine.graph.IGraphItemSettings;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient.VisualStyle;

/**
 * <p>
 * Visual style settings of boxes in the client kinetic canvas.
 * </p>
 * 
 * <p>
 * All functionality used by the "server" package should be static and not
 * involve constructors.
 * </p>
 * 
 * @author SkyCrawl
 */
public class KineticBoxSettings implements IGraphItemSettings {
	private final boolean iconsVisible;
	private final double scale;

	/**
	 * Constructor to use from "client" or "shared" packages.
	 */
	public KineticBoxSettings(boolean iconsVisible, double scale) {
		this.iconsVisible = iconsVisible;
		this.scale = scale;
	}

	public boolean areIconsVisible() {
		return iconsVisible;
	}

	// --------------------------------------------------------------
	// POSITIONING

	public double getTextOffsetLeft() {
		if (areIconsVisible()) {
			return getInnerComponentSpace() + getIconWidth()
					+ (getInnerComponentSpace() >> 1);
		} else {
			return getInnerComponentSpace();
		}
	}

	// --------------------------------------------------------------
	// SIZING

	public double getBoxWidth() {
		return 175;
	}

	public double getBoxHeight() {
		return getBoxWidth() / 2;
	}

	public double getIconWidth() {
		return 48;
	}

	public double getIconHeight() {
		return 48;
	}

	public double getTextWidth(double textOffsetLeft) {
		return getBoxWidth() - textOffsetLeft - getInnerComponentSpace();
	}

	public double getTextHeight() {
		return getIconHeight() / 2;
	}

	// --------------------------------------------------------------
	// VARIOUS

	public int getInnerComponentSpace() {
		return 13;
	}

	public double getScale() {
		return scale;
	}

	// --------------------------------------------------------------
	// SOME STATIC METHODS

	public static Colour getColor(VisualStyle graphItemStyle) {
		switch (graphItemStyle) {
			case SELECTED:
				return Colour.gold;
			case HIGHLIGHTED_EDGE:
				return Colour.red;
			case HIGHLIGHTED_SLOT:
				return Colour.lime;

			default:
				return Colour.black;
		}
	}

	// --------------------------------------------------------------
	// INSTANCE COMPARING - GENERATED WITH ECLIPSE

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (iconsVisible ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(scale);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KineticBoxSettings other = (KineticBoxSettings) obj;
		if (iconsVisible != other.iconsVisible)
			return false;
		if (Double.doubleToLongBits(scale) != Double
				.doubleToLongBits(other.scale))
			return false;
		return true;
	}
}
