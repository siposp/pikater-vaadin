package org.pikater.web.visualisation.implementation.charts.coloring;

import java.awt.Color;

import org.pikater.web.visualisation.implementation.exceptions.ColorerNotMergeableException;

/**
 * Class implementing colorer used for categorical values.
 * 
 * @author siposp
 * 
 */
public class CategoricalColorer implements Colorer {

	private int numberOfCategories;

	public int getNumberOfCategories() {
		return numberOfCategories;
	}

	public CategoricalColorer(int numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	/**
	 * Changes the hue of the color based on the value if the value is
	 * Double.NaN then returns Color.BLACK if the value is Infinite of Double
	 * the returns Color.GRAY
	 */
	@Override
	public Color getColor(double value) {
		if (Double.isNaN(value))
			return Color.BLACK;
		if (Double.isInfinite(value))
			return Color.GRAY;
		return Color.getHSBColor((float) (value / this.numberOfCategories),
				1.0f, 1.0f);
	}

	/**
	 * Merges two {@link CategoricalColorer} objects if they have the same
	 * number of categories.
	 */
	@Override
	public Colorer merge(Colorer colorer) throws ColorerNotMergeableException {
		if (colorer instanceof CategoricalColorer) {
			if (this.numberOfCategories == ((CategoricalColorer) colorer)
					.getNumberOfCategories()) {
				return this;
			} else {
				throw new ColorerNotMergeableException();
			}
		} else {
			throw new ColorerNotMergeableException();
		}
	}
}