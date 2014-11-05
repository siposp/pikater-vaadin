package org.pikater.web.visualisation.implementation.charts;

import java.awt.Color;

import org.pikater.web.visualisation.implementation.charts.axis.Axis;
import org.pikater.web.visualisation.implementation.charts.axis.CategoricalAxis;
import org.pikater.web.visualisation.implementation.charts.coloring.Colorer;
import org.pikater.web.visualisation.implementation.renderer.RendererInterface;
import org.pikater.web.visualisation.implementation.renderer.RendererInterface.TextAlignment;

/**
 * <p>
 * Abstract class representing some general functionality for a basic chart.
 * This chart may be used as base for chart, that displays multiple combinations
 * of dataset attributes in one chart. For this reason it uses the basic
 * functions to display the points and the background, but omitting function to
 * draw e.g. captions.
 * </p>
 * <p>
 * Dataset entities are represented by their [x,y] coordinates in the chart's
 * plain and their values z , that is used by some {@link Colorer} object to
 * generate points' color.
 * </p>
 * 
 * @see org.pikater.web.visualisation.implementation.charts.SingleChart
 * 
 * @author siposp
 * 
 */
public abstract class Chart {

	protected Axis horizontalAxis;
	protected Axis verticalAxis;
	protected int offsetx;
	protected int offsety;

	protected int xLabelHeight = 60;
	protected int chartHeight;
	protected int chartWidth;
	protected int xLeftMargin = 10;
	protected int xRightMargin = 10;
	protected int yBottomMargin = 10;
	protected int yTopMargin = 10;
	protected int yLabelWidth = 40;
	protected int tickSize = 10;

	private Color backGroundColor = Color.GRAY;
	boolean tickLabelVisible = true;
	Colorer zcolorer;
	protected RendererInterface renderer;

	public Chart(int width, int height, int offsetx, int offsety,
			RendererInterface renderer, Axis horizontalAxis, Axis verticalAxis) {
		setRenderer(renderer);
		this.horizontalAxis = horizontalAxis;
		this.verticalAxis = verticalAxis;
		this.offsetx = offsetx;
		this.offsety = offsety;
		this.chartWidth = width;
		this.chartHeight = height;
	}

	/**
	 * Sets all margins of the chart - left, right, top, bottom
	 * 
	 * @param margin
	 *            the value to be set
	 */
	public void setMargin(int margin) {
		this.setXLeftMargin(margin);
		this.setXRightMargin(margin);
		this.setYTopMargin(margin);
		this.setYBottomMargin(margin);
	}

	/**
	 * Sets the size of both labels - for X and Y axis. Choice of the value may
	 * depend on used {@link RendererInterface}.
	 * 
	 * @param labelSize
	 *            new size of labels in points
	 */
	public void setLabelSize(int labelSize) {
		this.setXLabelHeight(labelSize);
		this.setYLabelWidth(labelSize);
	}

	protected void setRenderer(RendererInterface renderer) {
		this.renderer = renderer;
	}

	public int getXLabelHeight() {
		return xLabelHeight;
	}

	public void setXLabelHeight(int xLabelHeight) {
		this.xLabelHeight = xLabelHeight;
	}

	public int getAreaHeight() {
		return chartHeight - xLabelHeight;
	}

	public int getXLeftMargin() {
		return xLeftMargin;
	}

	public void setXLeftMargin(int xLeftMargin) {
		this.xLeftMargin = xLeftMargin;
	}

	public int getXRightMargin() {
		return xRightMargin;
	}

	public void setXRightMargin(int xRightMargin) {
		this.xRightMargin = xRightMargin;
	}

	public int getYBottomMargin() {
		return yBottomMargin;
	}

	public void setYBottomMargin(int yBottomMargin) {
		this.yBottomMargin = yBottomMargin;
	}

	public int getYTopMargin() {
		return yTopMargin;
	}

	public void setYTopMargin(int yTopMargin) {
		this.yTopMargin = yTopMargin;
	}

	public int getYLabelWidth() {
		return yLabelWidth;
	}

	public void setYLabelWidth(int yLabelWidth) {
		this.yLabelWidth = yLabelWidth;
	}

	public int getAreaWidth() {
		return chartWidth - yLabelWidth;
	}

	public int getVerticalAxisHeight() {
		return getAreaHeight() - yBottomMargin - yTopMargin;
	}

	public int getHorizontalAxisWidth() {
		return getAreaWidth() - xLeftMargin - xRightMargin;
	}

	public int getChartHeight() {
		return chartHeight;
	}

	public void setChartHeight(int chartHeight) {
		this.chartHeight = chartHeight;
	}

	public int getChartWidth() {
		return chartWidth;
	}

	public void setChartWidth(int chartWidth) {
		this.chartWidth = chartWidth;
	}

	public int getTickSize() {
		return tickSize;
	}

	public void setTickSize(int tickSize) {
		this.tickSize = tickSize;
	}

	public Color getBackGroundColor() {
		return backGroundColor;
	}

	public void setBackGroundColor(Color backGroundColor) {
		this.backGroundColor = backGroundColor;
	}

	public boolean isTickLabelVisible() {
		return tickLabelVisible;
	}

	public void setTickLabelVisible(boolean tickLabelVisible) {
		this.tickLabelVisible = tickLabelVisible;
	}

	public Colorer getZcolorer() {
		return zcolorer;
	}

	public void setZcolorer(Colorer zcolorer) {
		this.zcolorer = zcolorer;
	}

	public void setHorizontalAxis(Axis axis) {
		this.horizontalAxis = axis;
	}

	public void setVerticalAxis(Axis axis) {
		this.verticalAxis = axis;
	}

	public Axis getHorizontalAxis() {
		return this.horizontalAxis;
	}

	public Axis getVerticalAxis() {
		return this.verticalAxis;
	}

	public abstract void startChart();

	public abstract void finishChart();

	/**
	 * Draws the background by rendering the area with axes and ticks marking
	 * some special values.
	 */
	protected void renderBackground() {
		// drawing the background
		renderer.drawRectangle(offsetx + yLabelWidth, offsety, getAreaWidth(),
				getAreaHeight(), getBackGroundColor(), Color.BLACK, 0);
		// drawing the vertical axis
		renderer.drawLine(offsetx + yLabelWidth, offsety,
				offsetx + yLabelWidth, offsety + getAreaHeight(),
				verticalAxis.getAxisColor(), 1);
		// and the ticks
		for (int i = 0; i < verticalAxis.getTickCount(); i++) {
			int y = offsety
					+ yTopMargin
					+ getVerticalAxisHeight()
					- (int) (verticalAxis.getTickPosition(i) * getVerticalAxisHeight());
			renderer.drawLine(offsetx + yLabelWidth - tickSize, y, offsetx
					+ yLabelWidth, y, verticalAxis.getAxisColor(), 1);

			if (tickLabelVisible) {
				if (verticalAxis instanceof CategoricalAxis) {
					if (i % 2 == 0) {
						renderer.drawText(verticalAxis.getTickString(i),
								offsetx + yLabelWidth - tickSize - 2, y + 5,
								TextAlignment.Center,
								verticalAxis.getAxisColor(), -90);
					} else {
						renderer.drawText(verticalAxis.getTickString(i),
								offsetx + yLabelWidth - tickSize - 18, y + 5,
								TextAlignment.Center,
								verticalAxis.getAxisColor(), -90);
					}
				} else {
					renderer.drawText(verticalAxis.getTickString(i), offsetx
							+ yLabelWidth - tickSize - 2, y + 5,
							TextAlignment.Right, verticalAxis.getAxisColor());
				}
			}
		}

		// drawing the horizontal axis
		renderer.drawLine(offsetx + yLabelWidth, offsety + getAreaHeight(),
				offsetx + getChartWidth(), offsety + getAreaHeight(),
				horizontalAxis.getAxisColor(), 1);
		// and the ticks
		for (int i = 0; i < horizontalAxis.getTickCount(); i++) {
			int x = offsetx
					+ yLabelWidth
					+ xLeftMargin
					+ (int) (horizontalAxis.getTickPosition(i) * getHorizontalAxisWidth());
			renderer.drawLine(x, offsety + getAreaHeight(), x, offsety
					+ getAreaHeight() + tickSize,
					horizontalAxis.getAxisColor(), 1);
			if (tickLabelVisible) {
				// if we have categories write them in two rows
				if (horizontalAxis instanceof CategoricalAxis) {
					if (i % 2 == 0) {
						renderer.drawText(horizontalAxis.getTickString(i), x,
								offsety + getAreaHeight() + tickSize + 15,
								TextAlignment.Center,
								horizontalAxis.getAxisColor());
					} else {
						renderer.drawText(horizontalAxis.getTickString(i), x,
								offsety + getAreaHeight() + tickSize + 31,
								TextAlignment.Center,
								horizontalAxis.getAxisColor());
					}
				} else {
					renderer.drawText(horizontalAxis.getTickString(i), x,
							offsety + getAreaHeight() + tickSize + 15,
							TextAlignment.Center, horizontalAxis.getAxisColor());
				}
			}
		}

	}

	/**
	 * Draws a circle for some dataset entry. To choose the color of the point
	 * {@link Colorer} set for this chart is used. You can change the colorer by
	 * calling {@link Chart#setZcolorer(Colorer)}
	 * 
	 * @param x
	 *            coordinate in chart space
	 * @param y
	 *            coordinate in chart space
	 * @param z
	 *            value of the point
	 * @param radius
	 *            of the point in pixels
	 */
	public void renderPoint(double x, double y, double z, int radius) {
		renderPoint(x, y, z, radius, this.zcolorer.getColor(z));
	}

	/**
	 * Draws a circle for some dataset entry.
	 * 
	 * @param x
	 *            coordinate in chart space
	 * @param y
	 *            coordinate in chart space
	 * @param z
	 *            value of the point
	 * @param radius
	 *            radius of the point in pixels
	 * @param colorer
	 *            {@link Colorer} used to retrieve the points color, based on
	 *            its value
	 */
	public void renderPoint(double x, double y, double z, int radius,
			Colorer colorer) {
		renderPoint(x, y, z, radius, colorer.getColor(z));
	}

	/**
	 * Draws a circle for some dataset entry.
	 * 
	 * @param x
	 *            coordinate in chart space
	 * @param y
	 *            coordinate in chart space
	 * @param z
	 *            value of the point
	 * @param radius
	 *            radius of the point in pixels
	 * @param color
	 *            {@link Color} point is filled with
	 */
	public void renderPoint(double x, double y, double z, int radius,
			Color color) {
		renderer.drawCircle(
				this.offsetx
						+ yLabelWidth
						+ (int) (this.xLeftMargin + horizontalAxis
								.getValuePos(x) * this.getHorizontalAxisWidth()),
				this.offsety
						+ this.yTopMargin
						+ getVerticalAxisHeight()
						+ yBottomMargin
						+ (int) (-yBottomMargin - verticalAxis.getValuePos(y)
								* this.getVerticalAxisHeight()), radius,
				Color.BLACK, color, 1);
	}

	/**
	 * It calls the {@link RendererInterface#begin()} function to initiate
	 * rendering.
	 */
	public void beginRendering() {
		this.renderer.begin();
	}

	/**
	 * It calls the {@link RendererInterface#end()} function to initiate
	 * rendering.
	 */
	public void endRendering() {
		this.renderer.end();
	}

}
