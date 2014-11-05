package org.pikater.web.visualisation.implementation.exceptions;

public class ChartException extends Exception {
	private static final long serialVersionUID = -8585443792545613531L;

	public ChartException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChartException(String message) {
		super(message);
	}

	public ChartException(Throwable cause) {
		super(cause);
	}
}