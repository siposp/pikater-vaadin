package org.pikater.web.visualisation.implementation.charts.axis;

/**
 * Class representing an axis for categorical values.
 * 
 * @author siposp
 * 
 */
public class CategoricalAxis extends ValueAxis {

	String[] values;

	public CategoricalAxis(String[] values) {
		this.min = 0.0;
		this.max = values.length - 1;
		this.values = values;
	}

	@Override
	public int getTickCount() {
		return values.length;
	}

	@Override
	public String getTickString(int tick) {
		return values[tick];
	}

}
