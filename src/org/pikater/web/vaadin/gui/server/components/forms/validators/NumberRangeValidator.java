package org.pikater.web.vaadin.gui.server.components.forms.validators;

import java.util.Collection;
import java.util.HashSet;

import org.pikater.shared.util.Interval;

import com.vaadin.data.Validator;

/**
 * Common range validator for numbers. Any number of ranges can
 * be added and validation will validate against the highest
 * minimum and lowest maximum found.
 * 
 * @author SkyCrawl
 *
 * @param <N> The number type. Integer, Float or Double.
 */
public class NumberRangeValidator<N extends Number> implements Validator {
	private static final long serialVersionUID = 3351367465515564187L;

	private final NumberConstant thisNumber;
	private final Collection<Interval<N>> boundsProviders;

	public NumberRangeValidator(Class<? extends Number> numberClass, final N min, final N max) {
		this(numberClass, new Interval<N>(min, max));
	}

	public NumberRangeValidator(Class<? extends Number> numberClass, Interval<N> boundsProvider) {
		this.thisNumber = NumberConstant.fromNumberClass(numberClass);
		this.boundsProviders = new HashSet<Interval<N>>();
		addBoundsProvider(boundsProvider);
	}

	/**
	 * Add a range to validate against.
	 */
	public void addBoundsProvider(Interval<N> boundsProvider) {
		this.boundsProviders.add(boundsProvider);
	}

	/**
	 * Parse the given value into the validator's associated number
	 * type.
	 * @throws InvalidValueException
	 */
	@SuppressWarnings("unchecked")
	public N parse(String value) throws InvalidValueException {
		try {
			return (N) thisNumber.parse(value);
		} catch (Exception t) {
			throw new InvalidValueException("Not " + thisNumber.errorDisplayString);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void validate(Object value) throws InvalidValueException {
		N min = (N) this.thisNumber.absoluteMin;
		N max = (N) this.thisNumber.absoluteMax;
		for (Interval<N> interval : boundsProviders) {
			if ((interval.getMin() != null) && (interval.getMin().doubleValue() > min.doubleValue())) {
				min = (N) interval.getMin();
			}
			if ((interval.getMax() != null) && (interval.getMax().doubleValue() < max.doubleValue())) {
				max = (N) interval.getMax();
			}
		}

		N num = parse((String) value);
		if ((min.doubleValue() > num.doubleValue()) || (num.doubleValue() > max.doubleValue())) {
			throw new InvalidValueException("Not " + thisNumber.errorDisplayString + " between " + min.toString() + " and " + max.toString());
		}
	}
}
