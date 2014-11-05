package org.pikater.web.visualisation.implementation.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * <p>Renderer class, that can be used to render graphics to {@link BufferedImage}.</p>
 * <p>Functions of this class draw to an already prepared {@link BufferedImage} object. The main
 * advantage of the approach is, that the size of final image doesn't depend on the amount of data 
 * being drawn in the chart. However, huge bitmap images can occupy a significant amount of memory.</p>
 * 
 * @author siposp
 */
public class ImageRenderer implements RendererInterface {

	private static int DEFAULT_FONT_SIZE = 14;

	private BufferedImage img;

	private Graphics2D g2 = null;

	public ImageRenderer(int width, int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	public ImageRenderer(BufferedImage img) {
		this.img = img;
	}

	public BufferedImage getImage() {
		return img;
	}

	@Override
	public void begin() {
		g2 = img.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
	}

	@Override
	public void end() {
		// this renderer doesn't need postprocessing
	}

	@Override
	public void drawCircle(int x, int y, int radius) {
		this.drawCircle(x, y, radius, Color.BLACK, Color.GRAY, 1);
	}

	@Override
	public void drawCircle(int x, int y, int radius, Color stroke, Color fill, int strokeWidth) {
		BasicStroke gStroke = new BasicStroke(strokeWidth);

		g2.setPaint(fill);
		g2.fillOval(x - radius - strokeWidth, y - radius - strokeWidth, (radius + strokeWidth) * 2, (radius + strokeWidth) * 2);

		g2.setColor(stroke);
		g2.setStroke(gStroke);
		g2.drawOval(x - radius - strokeWidth, y - radius - strokeWidth, (radius + strokeWidth) * 2, (radius + strokeWidth) * 2);

	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2, Color color, int strokeWidth) {
		BasicStroke gStroke = new BasicStroke(strokeWidth);
		g2.setStroke(gStroke);
		g2.setColor(color);
		g2.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color) {
		this.drawText(text, x, y, textAlignment, color, 0);
	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree) {
		this.drawText(text, x, y, textAlignment, color, rotationDegree, x, y, DEFAULT_FONT_SIZE);
	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int rotationX, int rotationY) {
		drawText(text, x, y, textAlignment, color, rotationDegree, x, y, DEFAULT_FONT_SIZE);

	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int size) {
		drawText(text, x, y, textAlignment, color, rotationDegree, x, y, size);
	}

	@Override
	public void drawText(String text, int x, int y, TextAlignment textAlignment, Color color, int rotationDegree, int rotationX, int rotationY, int size) {
		AffineTransform saved = g2.getTransform();

		Font f = new Font(null, Font.PLAIN, size);
		g2.setFont(f);

		FontMetrics fm = g2.getFontMetrics();

		AffineTransform affineTransform = new AffineTransform();

		affineTransform.rotate(Math.toRadians(rotationDegree), rotationX, rotationY);

		switch (textAlignment) {
		case Left:
			break;
		case Center:
			affineTransform.translate(-fm.stringWidth(text) / 2, 0);
			break;
		case Right:
			affineTransform.translate(-fm.stringWidth(text), 0);
			break;
		default:
			throw new IllegalStateException();
		}

		g2.setTransform(affineTransform);
		g2.setColor(color);

		g2.drawString(text, x, y);

		g2.setTransform(saved);
	}

	@Override
	public void drawRectangle(int x, int y, int width, int height, Color fillColor, Color strokeColor, int strokeWidth) {
		BasicStroke gStroke = new BasicStroke(strokeWidth);

		g2.setColor(fillColor);
		g2.fillRect(x, y, width, height);

		g2.setStroke(gStroke);
		g2.setColor(strokeColor);
		g2.drawRect(x, y, width, height);
	}

}
