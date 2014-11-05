package org.pikater.web.visualisation.implementation.exceptions;

public class ColorerNotMergeableException extends ChartException {
	private static final long serialVersionUID = -5358709112652550410L;

	public ColorerNotMergeableException() {
		super("Colorer can not be merged.");
	}
}