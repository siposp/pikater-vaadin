package org.pikater.web.vaadin.gui.shared.borderlayout;

import java.io.Serializable;

/**
 * Custom CSS dimension implementation.  There are other implementations but none of them
 * contained what we needed and could be integrated in all parts of the application.
 * 
 * @author SkyCrawl
 */
public class Dimension implements Serializable {
	private static final long serialVersionUID = 6907449348645122997L;

	/**
	 * CSS dimension mode enumeration
	 * 
	 * @author SkyCrawl
	 */
	public static enum DimensionMode {
		MAX, AUTO;

		@Override
		public String toString() {
			switch (this) {
			case AUTO:
				return "auto";
			case MAX:
				return "100%";
			default:
				throw new IllegalStateException("Unknown state: " + name());
			}
		}
	}

	/**
	 * CSS dimension unit enumeration.  There are other implementations but none of them
	 * contained what we needed and could be integrated in all parts of the application.
	 * 
	 * @author SkyCrawl
	 */
	public static enum DimensionUnit {
		PCT, PX;

		public String toString() {
			switch (this) {
			case PCT:
				return "%";
			case PX:
				return "px";
			default:
				throw new IllegalStateException("Unknown state: " + name());
			}
		}
	}

	public String cssDimensionString;

	/**
	 * Default public constructor keeps Vaadin happy.
	 */
	@Deprecated
	public Dimension() {
	}

	/**
	 * The actual constructor to use.
	 * 
	 */
	public Dimension(DimensionMode dimension) {
		this(dimension.toString());
	}

	public Dimension(double width, DimensionUnit unit) {
		this(String.valueOf(width) + unit.toString());
	}

	public Dimension(String dimension) {
		this.cssDimensionString = dimension;
	}

	/**
	 * Does the currently defined dimension equal "100%"? 
	 */
	public boolean isSetToMax() {
		return cssDimensionString.equals("100%");
	}
}
