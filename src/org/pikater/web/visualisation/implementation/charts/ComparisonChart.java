package org.pikater.web.visualisation.implementation.charts;

import java.awt.Color;

import org.pikater.web.visualisation.implementation.charts.axis.Axis;
import org.pikater.web.visualisation.implementation.charts.coloring.Colorer;
import org.pikater.web.visualisation.implementation.renderer.RendererInterface;

/**
 * <p>
 * This class represents additional functionality, that make possible to compare
 * data entries of two individual datasets.
 * </p>
 * <p>
 * In this case, we use terminology, as follows:
 * <ul>
 * <li>Primary dataset - dataset that is represented using circles as for
 * visualisation of single dataset</li>
 * <li>Secondary dataset - dataset that is represented using squares to
 * distinguish between the dataset</li>
 * </ul>
 * </p>
 * 
 * @author siposp
 * 
 */
public class ComparisonChart extends SingleChart {

	/**
	 * Constructor to create a new chart
	 * 
	 * @param width
	 *            of the chart in pixels
	 * @param height
	 *            of the chart in pixels
	 * @param renderer
	 *            {@link RendererInterface} used to draw the chart
	 * @param horizontalAxis
	 *            {@link Axis} representing axis X
	 * @param verticalAxis
	 *            {@link Axis} representing axis Y
	 */
	public ComparisonChart(int width, int height, RendererInterface renderer,
			Axis horizontalAxis, Axis verticalAxis) {
		super(width, height, 0, 0, renderer, horizontalAxis, verticalAxis);
	}

	/**
	 * Draws a square with specified parameters
	 * 
	 * @param x
	 *            coordinate in chart space
	 * @param y
	 *            coordinate in chart space
	 * @param z
	 *            value of the point
	 * @param pointWidth
	 *            width of square in pixels
	 * @param colorer
	 *            used to genereate the point's color based on its value
	 */
	public void renderSquarePoint(double x, double y, double z, int pointWidth,
			Colorer colorer) {
		renderSquarePoint(x, y, z, pointWidth, colorer.getColor(z));
	}

	/**
	 * Draws a square with specified parameters, that represents a data entry
	 * for the secondary datasets.
	 * 
	 * @param x
	 *            coordinate in chart space
	 * @param y
	 *            coordinate in chart space
	 * @param z
	 *            value of the point
	 * @param pointWidth
	 *            width of square in pixels
	 * @param color
	 *            the point is filled with
	 */
	public void renderSquarePoint(double x, double y, double z, int pointWidth,
			Color color) {
		int cx = offsetx
				+ yLabelWidth
				+ (int) (this.xLeftMargin + horizontalAxis.getValuePos(x)
						* this.getHorizontalAxisWidth());
		int cy = offsety
				+ yTopMargin
				+ getVerticalAxisHeight()
				+ yBottomMargin
				+ (int) (-yBottomMargin - verticalAxis.getValuePos(y)
						* this.getVerticalAxisHeight());
		renderer.drawRectangle(cx - pointWidth / 2, cy - pointWidth / 2,
				pointWidth, pointWidth, color, Color.BLACK, 1);
	}
}
