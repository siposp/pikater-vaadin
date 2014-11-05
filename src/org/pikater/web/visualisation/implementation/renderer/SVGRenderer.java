package org.pikater.web.visualisation.implementation.renderer;

import java.awt.Color;
import java.io.PrintStream;

/**
 * <p>Renderer class, that can be used to render graphics to Scalable Vector Graphics (SVG) format.</p>
 * <p>This class can be used for rendering into streams, that results in low memory footprint. However, please consider
 * that rendering large charts can result in huge output SVG files.</p>
 *
 * @author siposp
 */
public class SVGRenderer implements RendererInterface {

	private static int DEFAULT_FONT_SIZE = 12;

	private static String startDocument = "<?xml version=\"1.0\"?>\n" + "<svg width=\"{width}\" height=\"{height}\" " + "xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
	private static String endDocument = "</svg>";
	private static String circleString = "<circle cx=\"{cx}\" cy=\"{cy}\" r=\"{r}\" style=\"{strokeCSS}{strokeWidthCSS}{fillCSS}\"/>";
	private static String textString = "<text text-anchor=\"{align}\" x=\"{x}\" y=\"{y}\" style=\"{color}{size}\">{text}</text>";
	private static String lineString = "<line x1=\"{x1}\" y1=\"{y1}\" x2=\"{x2}\" y2=\"{y2}\" style=\"{strokeCSS}{strokeWidthCSS}\" />";
	private static String rectangleString = "<rect x=\"{x}\" y=\"{y}\" width=\"{width}\" height=\"{height}\" style=\"fill:rgb({fillR},{fillG},{fillB});stroke-width:{strokeWidth};stroke:rgb({strokeR},{strokeG},{strokeB});\"/>";

	private PrintStream out;
	private int width;
	private int height;

	public SVGRenderer(PrintStream outputStream, int width, int height) {
		this.out = outputStream;
		this.width = width;
		this.height = height;
	}

	@Override
	public void drawCircle(int x, int y, int radius) {
		this.drawCircle(x, y, radius, Color.BLACK, Color.GRAY, 1);
	}

	@Override
	public void drawCircle(int x, int y, int radius, Color stroke, Color fill, int strokeWidth) {
		out.println(circleString.replace("{cx}", "" + x).replace("{cy}", "" + y).replace("{r}", "" + radius).replace("{strokeWidthCSS}", String.format("stroke-width:%1$d;", strokeWidth))
				.replace("{strokeCSS}", String.format("stroke:rgb(%1$d,%2$d,%3$d);", stroke.getRed(), stroke.getGreen(), stroke.getBlue()))
				.replace("{fillCSS}", String.format("fill:rgb(%1$d,%2$d,%3$d);", fill.getRed(), fill.getGreen(), fill.getBlue())));

	}

	@Override
	public void drawText(String text, int x, int y, RendererInterface.TextAlignment textAlignment, Color color) {
		String outText = textString.replace("{x}", "" + x).replace("{y}", "" + y).replace("{size}", "")
				.replace("{color}", String.format("fill:rgb(%1$d,%2$d,%3$d);", color.getRed(), color.getGreen(), color.getBlue())).replace("{text}", text);

		switch (textAlignment) {
		case Left:
			outText = outText.replace("{align}", "start");
			break;
		case Center:
			outText = outText.replace("{align}", "middle");
			break;
		case Right:
			outText = outText.replace("{align}", "end");
			break;
		default:
			break;
		}
		out.println(outText);
	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree) {
		drawText(text, x, y, textAlignment, color, rotationDegree, x, y, DEFAULT_FONT_SIZE);
	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int size) {
		drawText(text, x, y, textAlignment, color, rotationDegree, x, y, size);
	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int rotationX, int rotationY) {
		drawText(text, x, y, textAlignment, color, rotationDegree, rotationX, rotationY, DEFAULT_FONT_SIZE);
	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int rotationX, int rotationY, int size) {
		String outText = String.format("<g transform=\"rotate(%1$d %2$d %3$d)\">", rotationDegree, rotationX, rotationY)
				+ textString.replace("{x}", "" + x).replace("{y}", "" + y).replace("{color}", String.format("fill:rgb(%1$d,%2$d,%3$d);", color.getRed(), color.getGreen(), color.getBlue()))
						.replace("{size}", "font-size:" + size + "px;").replace("{text}", text);

		switch (textAlignment) {
		case Left:
			outText = outText.replace("{align}", "start");
			break;
		case Center:
			outText = outText.replace("{align}", "middle");
			break;
		case Right:
			outText = outText.replace("{align}", "end");
			break;
		default:
			break;
		}
		outText = outText + "</g>";
		out.println(outText);

	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2, Color stroke, int strokeWidth) {
		out.println(lineString.replace("{x1}", "" + x1).replace("{y1}", "" + y1).replace("{x2}", "" + x2).replace("{y2}", "" + y2)
				.replace("{strokeWidthCSS}", String.format("stroke-width:%1$d;", strokeWidth))
				.replace("{strokeCSS}", String.format("stroke:rgb(%1$d,%2$d,%3$d);", stroke.getRed(), stroke.getGreen(), stroke.getBlue())));

	}

	@Override
	public void drawRectangle(int x, int y, int width, int height, Color fillColor, Color strokeColor, int strokeWidth) {
		out.println(rectangleString.replace("{x}", "" + x).replace("{y}", "" + y).replace("{width}", "" + width).replace("{height}", "" + height)
				.replace("{fillR}", String.format("%1$d", fillColor.getRed())).replace("{fillG}", String.format("%1$d", fillColor.getGreen()))
				.replace("{fillB}", String.format("%1$d", fillColor.getBlue())).replace("{strokeWidth}", "" + strokeWidth).replace("{strokeR}", String.format("%1$d", strokeColor.getRed()))
				.replace("{strokeG}", String.format("%1$d", strokeColor.getGreen())).replace("{strokeB}", String.format("%1$d", strokeColor.getBlue()))

		);
	}

	@Override
	public void begin() {
		out.println(startDocument.replace("{width}", "" + this.width).replace("{height}", "" + this.height));
	}

	@Override
	public void end() {
		out.print(endDocument);
		out.flush();
	}

}
