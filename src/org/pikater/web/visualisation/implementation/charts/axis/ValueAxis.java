package org.pikater.web.visualisation.implementation.charts.axis;

/**
 * Class representing an axis for numerical values.
 * 
 * @author siposp
 * 
 */
public class ValueAxis extends Axis {
	protected double min;
	protected double max;

	public ValueAxis(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public ValueAxis() {
		this.min = 0.0;
		this.max = 10.0;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	@Override
	public double getValuePos(double value) {
		return (value - min) / (max - min);
	}

	int tickCount = 11;

	public void setTickCount(int ticks) {
		this.tickCount = ticks;
	}

	@Override
	public int getTickCount() {
		return this.tickCount;
	}

	@Override
	public String getTickString(int tick) {
		if (tick == 0)
			return String.format("%1$,.2f", min);
		if (tick == tickCount)
			return String.format("%1$,.2f", max);
		double inc = (max - min) / (tickCount - 1);
		return String.format("%1$,.2f", min + tick * inc);
	}

	@Override
	public double getTickPosition(int tick) {
		double inc = (max - min) / (getTickCount() - 1);
		double tickVal = min + tick * inc;
		return getValuePos(tickVal);
	}

}
