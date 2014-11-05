package org.pikater.web.vaadin.gui.server.components.forms.validators;

/**
 * Generalizes number types (Integer, Float and Double) and
 * provides some basic information and methods for them.
 * 
 * @author SkyCrawl
 */
public enum NumberConstant {
	INTEGER("an integer", Integer.MIN_VALUE, Integer.MAX_VALUE),
	FLOAT("a float", Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY),
	DOUBLE("a double", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

	public final String errorDisplayString;
	public final Number absoluteMin;
	public final Number absoluteMax;

	private NumberConstant(String errorDisplayString, Number min, Number max) {
		this.errorDisplayString = errorDisplayString;
		this.absoluteMin = min;
		this.absoluteMax = max;
	}

	public Number parse(String value) throws NumberFormatException {
		switch (this) {
		case DOUBLE:
			return Double.parseDouble(value);
		case FLOAT:
			return Float.parseFloat(value);
		case INTEGER:
			return Integer.parseInt(value);
		default:
			throw new IllegalStateException("Unknown state: " + name());
		}
	}

	public static NumberConstant fromNumberClass(Class<? extends Number> clazz) {
		if (clazz.equals(Integer.class)) {
			return INTEGER;
		} else if (clazz.equals(Float.class)) {
			return FLOAT;
		} else if (clazz.equals(Double.class)) {
			return DOUBLE;
		} else {
			throw new IllegalArgumentException("Unknown number type: " + clazz.getName());
		}
	}
}