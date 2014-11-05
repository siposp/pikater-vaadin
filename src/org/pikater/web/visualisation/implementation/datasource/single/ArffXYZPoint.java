package org.pikater.web.visualisation.implementation.datasource.single;

/**
 * Class implementing a point for {@link Double} triplet. 
 * 
 * @author siposp
 *
 */
public class ArffXYZPoint extends XYZItem<Double> {

	/**
	 * Constructor of a new point
	 * @param x value X of the point
	 * @param y value Y of the point
	 * @param z value Z of the point
	 */
	public ArffXYZPoint(Double x, Double y, Double z) {
		super(x, y, z);
	}
}
