package org.pikater.web.visualisation.implementation.renderer;

import java.awt.Color;

/**
 * Interface representing functions necessary to draw the charts for dataset visualisation.
 * @author siposp
 *
 */
public interface RendererInterface {

	public enum TextAlignment {
		Left, Center, Right
	};

	/**
	 * Does all the preprocessing needed by the renderer.
	 */
	public void begin();

	/**
	 * Does all the postprocessing needed by the renderer.
	 */
	public void end();

	/**
	 * Draws a circle with center point at [x,y]. 
	 * @param x coordinate of the center point
	 * @param y coordinate of the center point
	 * @param radius of the circle
	 */
	public void drawCircle(int x, int y, int radius);

	/**
	 * Draws a circle with center point at [x,y], filled with given color and with stroke in specified color and width.
	 * @param x coordinate of the center point
	 * @param y coordinate of the center point
	 * @param radius of the circle
	 * @param stroke color of the stroke
	 * @param fill color of the filled circle
	 * @param strokeWidth width of the stroke 
	 */
	public void drawCircle(int x, int y, int radius, Color stroke, Color fill, int strokeWidth);

	/**
	 * Draws a text to the position [x,y] in given color with specified text alignment
	 * @param text {@link String} object containing the text
	 * @param x coordinate of the text
	 * @param y coordiante of the text
	 * @param textAlignment of the text
	 * @param color of the text
	 */
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color);

	/**
	 * Draws a text to the position [x,y] in given color with specified text alignment, rotated by certain degree.
	 * @param text {@link String} object containing the text
	 * @param x coordinate of the text
	 * @param y coordiante of the text
	 * @param textAlignment of the text
	 * @param color of the text
	 * @param rotationDegree of the rotated text
	 */
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree);

	/**
	 * Draws a text to the position [x,y] in given color with specified text alignment, rotated by certain degree. The text also has size specified.
	 * @param text {@link String} object containing the text
	 * @param x coordinate of the text
	 * @param y coordiante of the text
	 * @param textAlignment of the text
	 * @param color of the text
	 * @param rotationDegree of the rotated text
	 * @param size of text
	 */
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int size);

	/**
	 * Draws a text to the position [x,y] in given color with specified text alignment, rotated by certain degree. Text is rotated around a certain point.
	 * @param text {@link String} object containing the text
	 * @param x coordinate of the text
	 * @param y coordiante of the text
	 * @param textAlignment of the text
	 * @param color of the text
	 * @param rotationDegree of the rotated text
	 * @param rotationX X coordinate of center of rotation
	 * @param rotationY Y coordinate of center of rotation
	 */
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int rotationX, int rotationY);

	/**
	 * Draws a text to the position [x,y] in given color with specified text alignment, rotated by certain degree. Text has also size specified and is rotated around a certain point.
	 * @param text {@link String} object containing the text
	 * @param x coordinate of the text
	 * @param y coordiante of the text
	 * @param textAlignment of the text
	 * @param color of the text
	 * @param rotationDegree of the rotated text
	 * @param rotationX X coordinate of center of rotation
	 * @param rotationY Y coordinate of center of rotation
	 * @param size of text
	 */
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int rotationX, int rotationY, int size);

	/**
	 * Draws a line from position [x1,y1] to position [x2,y2]. The line's color and width is also set.   
	 * @param x1 X coordinate of the starting point
	 * @param y1 Y coordinate of the starting point
	 * @param x2 X coordinate of the ending point
	 * @param y2 Y coordinate of the ending point
	 * @param color of line
	 * @param strokeWidth width of line
	 */
	public void drawLine(int x1, int y1, int x2, int y2, Color color, int strokeWidth);

	/**
	 * Draws a rectangle with top-left corner at [x,y] position. Rectangle has specified width and height, is filled with certain color and the line around the rectangle has its own color and thickness. 
	 * @param x coordinate of top-left corner
	 * @param y coordinate of top-left corner
	 * @param width of the rectangle
	 * @param height of the rectangle
	 * @param fillColor color rectangle is filled with
	 * @param strokeColor color of line around rectangle
	 * @param strokeWidth thickness of line around the rectangle
	 */
	public void drawRectangle(int x, int y, int width, int height, Color fillColor, Color strokeColor, int strokeWidth);
}
