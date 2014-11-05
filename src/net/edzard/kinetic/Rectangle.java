package net.edzard.kinetic;

/**
 * A rectangular shape.
 * @author Ed
 */
public class Rectangle extends Shape {
	
	/**
	 * Various points of interest.
	 */
	public static enum RectanglePoint
	{
		WESTCENTER,
		EASTCENTER,
		SOUTHCENTER,
		NORTHCENTER,
		CENTER;
		
		public RectanglePoint getInverted()
		{
			switch (this)
			{
				case WESTCENTER:
					return EASTCENTER;
				case EASTCENTER:
					return WESTCENTER;
				case SOUTHCENTER:
					return NORTHCENTER;
				case NORTHCENTER:
					return SOUTHCENTER;
				default:
					return CENTER;
			}
		}
		
		public static RectanglePoint getDirection(RectanglePoint direction, boolean invert)
		{
			if(invert)
			{
				return direction.getInverted();
			}
			else
			{
				return direction;
			}
		}
	}

	/** Protected default Ctor keeps GWT happy */ 
	protected Rectangle() {}
	
	/**
	 * Retrieve the rectangle shape corner radius.
	 * @return The corner radius
	 */
	public final native double getCornerRadius() /*-{
		return this.getCornerRadius();
	}-*/;
	
	/**
	 * Assign the rectangle shape corner radius.
	 * Yes, we have rounded corners. :-)
	 * @param radius A radius for the rounding of corners
	 */
	public final native void setCornerRadius(double radius) /*-{
		this.setCornerRadius(radius);
	}-*/;
	
	/**
	 * Does this rectangle intersect with another? 
	 * @param other The other rectangle
	 */
	public final boolean intersects(Box2d otherBox)
	{
		return getBox().intersects(otherBox);
	}
	
	/**
	 * Determines whether the supplied absolute position is within this rectangle.
	 * @param the position to check
	 * @return whether position is inside the rectangle
	 */
	public final boolean contains(Vector2d point)
	{
		Vector2d absolutePosition = getAbsolutePosition();
		Vector2d oppositeCorner = new Vector2d(absolutePosition.x + getWidth(), absolutePosition.y + getHeight());
		return (point.x >= absolutePosition.x) && (point.x <= oppositeCorner.x) &&
				(point.y >= absolutePosition.y) && (point.y <= oppositeCorner.y);
	}

	/**
	 * Convenience method for computing various absolute coordinates of a certain point on the rectangle. 
	 * @param the point
	 * @return the position
	 */
	public final Vector2d getAbsolutePointPosition(RectanglePoint point, Vector2d scale)
	{
		Vector2d absPos = getAbsolutePosition();
		switch (point)
		{
			case EASTCENTER:
				return new Vector2d(absPos.x, absPos.y + getScaledHeight(scale.y) / 2); 
			case WESTCENTER:
				return new Vector2d(absPos.x + getScaledWidth(scale.x), absPos.y + getScaledHeight(scale.y) / 2);
			case NORTHCENTER:
				return new Vector2d(absPos.x + getScaledWidth(scale.x) / 2, absPos.y);
			case SOUTHCENTER:
				return new Vector2d(absPos.x + getScaledWidth(scale.x) / 2, absPos.y + getScaledHeight(scale.y));
			case CENTER:
				return new Vector2d(absPos.x + getScaledWidth(scale.x) / 2, absPos.y + getScaledHeight(scale.y) / 2);
			default:
				 throw new IllegalStateException();
		}
	}
	
	public final Box2d getBox()
	{
		return new Box2d(getAbsolutePosition(), getSize());
	}
	
	private final double getScaledWidth(double scale)
	{
		return getWidth() * scale;
	}
	
	private final double getScaledHeight(double scale)
	{
		return getHeight() * scale;
	}
}
