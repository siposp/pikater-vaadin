package org.pikater.web.vaadin.gui.shared.kineticcomponent.visualstyle;

import net.edzard.kinetic.Colour;
import net.edzard.kinetic.Vector2d;

import org.pikater.web.vaadin.gui.client.kineticengine.graph.IGraphItemSettings;

/**
 * Visual settings of edges in the client kinetic canvas.
 * 
 * @author SkyCrawl
 */
public class KineticEdgeSettings implements IGraphItemSettings {
	/*
	 * Parameters defining the looks of the arrowhead (how "sharp" it is). In
	 * this case, it is a little bit longer than wider, which makes for a bit
	 * "sharper" arrowhead.
	 */

	public int getArrowHeight() {
		return 7;
	}

	public int getArrowWidth() {
		return 6;
	}

	/*
	 * Parameter defining the looks of drag marks.
	 */

	public Vector2d getDragMarkSize() {
		return new Vector2d(12, 12);
	}

	public Colour getDragMarkFill() {
		return Colour.darkorchid;
	}

	// private static final Vector2d dragMarkHalfSize = new
	// Vector2d(dragMarkDimensionInPixels >> 1, dragMarkDimensionInPixels >>
	// 1).;

	// --------------------------------------------------------------
	// INSTANCE COMPARING

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		// KineticEdgeSettings other = (KineticEdgeSettings) obj;
		return true;
	}
}