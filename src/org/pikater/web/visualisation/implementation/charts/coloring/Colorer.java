package org.pikater.web.visualisation.implementation.charts.coloring;

import java.awt.Color;

import org.pikater.web.visualisation.implementation.exceptions.ColorerNotMergeableException;

/**
 * Interface representing functions necessary for points' color represenation in
 * the charts.
 * 
 * @author siposp
 * 
 */
public interface Colorer {
	/**
	 * Returns the color representing the given value
	 * 
	 * @param value
	 *            to be converted to a color
	 * @return {@link Color} object of the computed color
	 */
	public Color getColor(double value);

	/**
	 * Merges the given colorer to the actual colorer
	 * 
	 * @param colorer
	 *            the colorer to be merged into the actual one
	 * @return new colorer that was created by the merger
	 * @throws ColorerNotMergeableException
	 *             when two incompatible colorers are intended to be merged
	 */
	public Colorer merge(Colorer colorer) throws ColorerNotMergeableException;
}
