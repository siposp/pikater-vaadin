package org.pikater.web.visualisation.implementation.charts.coloring;

import java.awt.Color;

import org.pikater.web.visualisation.implementation.exceptions.ColorerNotMergeableException;

/**
 * Class implementing colorer used for numerical values.
 * 
 * @author siposp
 * 
 */
public class LinearColorer implements Colorer {

	private double min;
	private double max;

	public LinearColorer() {
		this.min = 0.0;
		this.max = 10.0;
	}

	public LinearColorer(double min, double max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * <p>
	 * Returns color for the value and the color's Saturation in HSB color space
	 * is based on the value. Hue and Brightness are 1.0f except for some
	 * extreme cases.
	 * </p>
	 * <p>
	 * Colors returned by the function are the following:
	 * <ul>
	 * <li>if value is within interval [minimum,maximum] than the returned color
	 * is within interval [red,white] - simply several shades of red</li>
	 * <li>if value is Double.NaN then returns {@link Color.BLACK}
	 * <li>if the value is {@link Double#POSITIVE_INFINITY} or
	 * {@link Double#NEGATIVE_INFINITY} then returns {@link Color.GRAY}</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public Color getColor(double value) {
		if (Double.isNaN(value))
			return Color.BLACK;
		if (Double.isInfinite(value))
			return Color.GRAY;
		float saturation = (float) (0.0 + ((value - min) / (max - min)) * 1.0);
		return Color.getHSBColor(1.0f, saturation, 1.0f);
	}

	@Override
	public Colorer merge(Colorer colorer) throws ColorerNotMergeableException {
		if (colorer instanceof LinearColorer) {
			LinearColorer linCol = (LinearColorer) colorer;
			if (linCol.min < this.min) {
				this.min = linCol.min;
			}

			if (linCol.max > this.max) {
				this.max = linCol.max;
			}

			return this;
		} else {
			throw new ColorerNotMergeableException();
		}
	}

}
