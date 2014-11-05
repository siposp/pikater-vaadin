package org.pikater.web.visualisation.implementation.datasource.single;

/**
 * Class implementing a point for a triplet. The triplet consists of values of the same type
 * defined using type parameter T.
 * 
 * @author siposp
 *
 * @param <T> type of objects forming a triplet
 */
public class XYZItem<T> {
	private T x;
	private T y;
	private T z;

	public XYZItem(T x, T y, T z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public T getX() {
		return x;
	}

	public void setX(T x) {
		this.x = x;
	}

	public T getY() {
		return y;
	}

	public void setY(T y) {
		this.y = y;
	}

	public T getZ() {
		return z;
	}

	public void setZ(T z) {
		this.z = z;
	}
}
