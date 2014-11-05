package org.pikater.web.visualisation.implementation.generator.base;

import java.io.IOException;
import java.io.PrintStream;

import org.pikater.web.visualisation.definition.result.AbstractDSVisResult;
import org.pikater.web.visualisation.implementation.exceptions.ChartException;
import org.pikater.web.visualisation.implementation.renderer.RendererInterface;

/**
 * Abstract class implementing common functionality used for chart generation.
 * 
 * @author siposp
 *
 */
public abstract class Generator {
	protected AbstractDSVisResult<?, ?> progressListener;
	protected PrintStream output;

	protected int instNum;
	protected int lastPercentage = -1;
	protected int percentage = 0;
	protected int count = 0;

	protected RendererInterface renderer;
	/**
	 * Default size of charts created by some {@link Generator} object.
	 */
	public static final int DEFAULTCHARTSIZE = 1000;

	/**
	 * Constructor for a new {@link Generator}
	 * @param progressListener {@link AbstractDSVisResult} to inform about the generation status
	 * @param output {@link PrintStream} where the chart is being stored
	 */
	protected Generator(AbstractDSVisResult<?, ?> progressListener, PrintStream output) {
		this.progressListener = progressListener;
		this.output = output;
	}

	/**
	 * Creates the chart for dataset, that has previously been set for this {@link Generator}.
	 * @throws IOException when some I/O error occured when accessing the data set
	 * @throws ChartException when some chart-specific error occured e.g. can't join two axes
	 */
	public abstract void create() throws IOException, ChartException;

	/**
	 * Call this function every time, when there was processed one row from the
	 * data set, for that chart is being created. 
	 */
	protected void signalOneProcessedRow() {
		count++;

		percentage = 100 * count / instNum;

		if ((progressListener != null) && (percentage > lastPercentage)) {
			progressListener.updateProgress(percentage / new Float(100.0));
			lastPercentage = percentage;
		}
	}
}
