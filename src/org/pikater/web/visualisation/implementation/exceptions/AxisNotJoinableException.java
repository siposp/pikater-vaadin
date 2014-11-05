package org.pikater.web.visualisation.implementation.exceptions;

public class AxisNotJoinableException extends ChartException {
	private static final long serialVersionUID = 6877395628715369645L;

	public AxisNotJoinableException() {
		super("Axis can not be joined.");
	}
}