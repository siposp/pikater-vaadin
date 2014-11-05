package net.edzard.kinetic;

import java.io.Serializable;

/**
 * A data quadruple.
 * Used for, e.g. rectangles or bounding boxes.
 * @author Ed
 */
public class Box2d implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The left side value */
	public Double left;
	
	/** The top side value */
	public Double top;
	
	/** The right side value */
	public Double right;
	
	/** The bottom side value */
	public Double bottom;
	
	/**
	 * Standard Ctor.
	 * Initializes all fields with {@link Double#NaN}.
	 */
	public Box2d() {
		this.left = this.top = this.right = this.bottom = Double.NaN;
	}
	
	/**
	 * Parametrized Ctor.
	 * @param left Value for the left side
	 * @param top Value for the top side
	 * @param right Value for the right side
	 * @param bottom Value for the bottom side
	 */
	public Box2d(double left, double top, double right, double bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	/**
	 * Creates a box given by its position and size.
	 * @param position
	 * @param size Size as width and height. 
	 */
	public Box2d(Vector2d position, Vector2d size) {
		this.left = position.x;
		this.top = position.y;
		this.right = left + size.x;
		this.bottom = top + size.y;
	}
	
	public final double getWidth() {
		return this.right - this.left;
	}
	
	public final double getHeight() {
		return this.bottom - this.top;
	}
	
	public final Vector2d getSize() {
	    return new Vector2d(this.getWidth(), this.getHeight());
	}
	
	public final Vector2d getPosition() {
	    return new Vector2d(this.left, this.top);
	}

	/**
	 * Check if a point is within the bounds of the box.
	 * Takes unset boundaries (fields assigned with {@link Double#NaN}) into account. 
	 * @param point The point to evaluate
	 * @return True, if the given point is within or on the boundaries of the box. False, otherwise.
	 */
	public final boolean isInside(Vector2d point)
	{
		return !left.equals(Double.NaN) && (left <= point.x) &&
			!top.equals(Double.NaN) && (top <= point.y) &&
			!right.equals(Double.NaN) && (right >= point.x) &&
			!bottom.equals(Double.NaN) && (bottom >= point.y);
	}
	
	/**
	 * Whether this box shares at least a small area with the given box.
	 * @param otherBox
	 * @return
	 * @see answer #2: http://stackoverflow.com/questions/2752349/fast-rectangle-to-rectangle-intersection
	 */
	public final boolean intersects(Box2d otherBox)
	{
		return (this.left <= otherBox.right) && // this.left <= other.right
				(otherBox.left <= this.right) && // other.left <= this.right
				(this.top <= otherBox.bottom) && // this.top <= other.bottom
				(otherBox.top <= this.bottom); // other.top <= this.bottom
	}
	
	/**
	 * Corrects the box by some offset value.
	 * @param offset The offset value to use for correction
	 */
	public final void correctOffset(Vector2d offset) {
		this.left -= offset.x;
		this.top -= offset.y;
		this.right -= offset.x;
		this.bottom -= offset.y;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[(");
		sb.append(left).append(",").append(top).append(")").append(" x (").append(right).append(",").append(bottom).append(")]");
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bottom);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(left);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(right);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(top);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Box2d other = (Box2d) obj;
		if (Double.doubleToLongBits(bottom) != Double
				.doubleToLongBits(other.bottom))
			return false;
		if (Double.doubleToLongBits(left) != Double
				.doubleToLongBits(other.left))
			return false;
		if (Double.doubleToLongBits(right) != Double
				.doubleToLongBits(other.right))
			return false;
		if (Double.doubleToLongBits(top) != Double.doubleToLongBits(other.top))
			return false;
		return true;
	}
}