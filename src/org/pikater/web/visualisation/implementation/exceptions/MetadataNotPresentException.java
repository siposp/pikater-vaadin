package org.pikater.web.visualisation.implementation.exceptions;

public class MetadataNotPresentException extends Exception {
	private static final long serialVersionUID = 2836255324545952145L;

	private final String datasetName;

	public MetadataNotPresentException(String datasetName) {
		this.datasetName = datasetName;
	}

	@Override
	public String getMessage() {
		return "No metadata found for dataset: " + this.datasetName;
	}
}