package org.pikater.web.visualisation.implementation.charts.axis;

import java.awt.Color;

import org.pikater.web.visualisation.implementation.exceptions.AxisNotJoinableException;

/**
 * Abstract class containing implementations of functions common for all axis
 * types.
 * 
 * @author siposp
 * 
 */
public abstract class Axis {
	/**
	 * Returns the position of the given value relative to this axis
	 * 
	 * @param value
	 *            the value, which position we want to determine
	 * @return the position expressed as a value from 0.0 to 1.0 where 0.0
	 *         corresponds to the minimal value on this axis and 1.0 to the
	 *         maximum
	 */
	public abstract double getValuePos(double value);

	public abstract int getTickCount();

	public abstract String getTickString(int tick);

	/**
	 * Returns the position of the tick on the axis
	 * 
	 * @param tick
	 *            the index of the tick
	 * @return 0.0 for the tick, which equals to minimum and 1.0 for the tick,
	 *         that equals to maximum
	 */
	public abstract double getTickPosition(int tick);

	private Color axisColor = Color.BLACK;

	/**
	 * Retuns the color of the axis
	 * 
	 * @return {@link Color} object
	 */
	public Color getAxisColor() {
		return axisColor;
	}

	/**
	 * Sets the new color for the axis
	 * 
	 * @param axisColor
	 *            {@link Color} object of the new color
	 */
	public void setAxisColor(Color axisColor) {
		this.axisColor = axisColor;
	}

	private String caption;

	/**
	 * Returns the caption for the axis
	 * 
	 * @return axis' caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the caption for the axis
	 * 
	 * @param caption
	 *            the new caption
	 */
	public void setCaption(String caption) {
		this.hasCaption = true;
		this.caption = caption;
	}

	private boolean hasCaption = false;

	/**
	 * Returns true if the caption for this axis is visible or false for
	 * invisible caption
	 * 
	 * @return true for visible caption
	 */
	public boolean isCaptionVisible() {
		return this.hasCaption;
	}

	/**
	 * Sets the caption visibility
	 * 
	 * @param visible
	 *            true for visible caption
	 */
	public void setCaptionVisible(boolean visible) {
		this.hasCaption = visible;
	}

	/**
	 * Joins the two given axises to one The new axis has minimum and maximum
	 * value from both axis and the other properties from the first one
	 * (color,caption)
	 * 
	 * @throws AxisNotJoinableException
	 */
	public static Axis join(Axis axis1, Axis axis2)
			throws AxisNotJoinableException {
		if ((axis1 instanceof ValueAxis) && (axis2 instanceof ValueAxis)) {
			ValueAxis res = (ValueAxis) axis1;
			double min1 = ((ValueAxis) axis1).getMin();
			double max1 = ((ValueAxis) axis1).getMax();

			double min2 = ((ValueAxis) axis2).getMin();
			double max2 = ((ValueAxis) axis2).getMax();

			if (min1 < min2) {
				res.setMin(min1);
			} else {
				res.setMin(min2);
			}

			if (max1 > max2) {
				res.setMax(max1);
			} else {
				res.setMax(max2);
			}

			return res;

		} else if ((axis1 instanceof CategoricalAxis)
				&& (axis2 instanceof CategoricalAxis)) {
			if (((CategoricalAxis) axis1).getTickCount() == ((CategoricalAxis) axis2)
					.getTickCount()) {
				return axis1;
			} else {
				throw new AxisNotJoinableException();
			}
		} else {
			throw new AxisNotJoinableException();
		}
	}

}
