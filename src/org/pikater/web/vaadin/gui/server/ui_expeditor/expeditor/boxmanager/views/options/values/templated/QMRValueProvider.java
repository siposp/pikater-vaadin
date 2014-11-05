package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.values.DoubleValue;
import org.pikater.core.ontology.subtrees.newoption.values.FloatValue;
import org.pikater.core.ontology.subtrees.newoption.values.IntegerValue;
import org.pikater.core.ontology.subtrees.newoption.values.QuestionMarkRange;
import org.pikater.core.ontology.subtrees.newoption.values.StringValue;
import org.pikater.core.ontology.subtrees.newoption.values.interfaces.IComparableValueData;
import org.pikater.shared.util.Interval;
import org.pikater.web.vaadin.gui.server.components.forms.validators.NumberRangeValidator;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.OptionValueForm;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.AbstractFieldProviderForValue;

import com.vaadin.data.Validator;
import com.vaadin.ui.AbstractField;

/**
 * Class providing {@link QuestionMarkRange}-backed fields to {@link OptionValueForm}.
 * 
 * @author SkyCrawl
 */
public class QMRValueProvider extends AbstractFieldProviderForValue {
	@Override
	protected void doGenerateFields(final Value value) {
		// first cast value
		final QuestionMarkRange currentValue = (QuestionMarkRange) value.getCurrentValue();

		// then create the attempts field
		addField("attempts", createAttemptsField(currentValue.getCountOfValuesToTry(), new IOnValueChange<Integer>() {
			@Override
			public void valueChanged(Integer newValue) {
				currentValue.setCountOfValuesToTry(newValue);
			}
		}));

		// create the range fields
		AbstractField<? extends Object> minField = null;
		AbstractField<? extends Object> maxField = null;
		Class<? extends IComparableValueData> typeClass = currentValue.getUserDefinedRestriction().fetchRangeType();
		if (typeClass.equals(StringValue.class)) {
			if (currentValue.getUserDefinedRestriction().getMinValue() != null) {
				IFieldContext<String> context = getFieldContextFrom((String) currentValue.getUserDefinedRestriction().getMinValue().hackValue(), value);
				minField = createTextField("Minimum:", context, new IOnValueChange<String>() {
					@Override
					public void valueChanged(String newValue) {
						currentValue.getUserDefinedRestriction().setMinValue(new StringValue(newValue));
					}
				});
			}
			if (currentValue.getUserDefinedRestriction().getMaxValue() != null) {
				IFieldContext<String> context = getFieldContextFrom((String) currentValue.getUserDefinedRestriction().getMaxValue().hackValue(), value);
				maxField = createTextField("Maximum:", context, new IOnValueChange<String>() {
					@Override
					public void valueChanged(String newValue) {
						currentValue.getUserDefinedRestriction().setMaxValue(new StringValue(newValue));
					}
				});
			}

			// TODO: cross bind string validators (see example below)
		} else {
			if (currentValue.getUserDefinedRestriction().getMinValue() != null) {
				minField = createNumericRangeField("Minimum:", typeClass, value, currentValue.getUserDefinedRestriction().getMinValue(), new IOnValueChange<IComparableValueData>() {
					@Override
					public void valueChanged(IComparableValueData newValue) {
						currentValue.getUserDefinedRestriction().setMinValue(newValue);
					}
				});
			}
			if (currentValue.getUserDefinedRestriction().getMaxValue() != null) {
				maxField = createNumericRangeField("Maximum:", typeClass, value, currentValue.getUserDefinedRestriction().getMaxValue(), new IOnValueChange<IComparableValueData>() {
					@Override
					public void valueChanged(IComparableValueData newValue) {
						currentValue.getUserDefinedRestriction().setMaxValue(newValue);
					}
				});
			}

			// then cross bind validators for the value of other field, if need be
			if ((minField != null) && (maxField != null)) {
				crossBindNumberRangeValidators(typeClass, minField, maxField);
			}
		}

		// and finally, register
		addField("minimum", minField);
		addField("maximum", maxField);
	}

	private AbstractField<? extends Object> createNumericRangeField(String caption, Class<? extends IComparableValueData> typeClass, final Value value, final IComparableValueData rangeValue,
			final IOnValueChange<IComparableValueData> valueChangeHandler) {
		if (typeClass.equals(IntegerValue.class)) {
			return createNumericField(caption, getFieldContextFrom((Integer) rangeValue.hackValue(), value), new IOnValueChange<Integer>() {
				@Override
				public void valueChanged(Integer newValue) {
					valueChangeHandler.valueChanged(new IntegerValue(newValue));
				}
			});
		} else if (typeClass.equals(FloatValue.class)) {
			return createNumericField(caption, getFieldContextFrom((Float) rangeValue.hackValue(), value), new IOnValueChange<Float>() {
				@Override
				public void valueChanged(Float newValue) {
					valueChangeHandler.valueChanged(new FloatValue(newValue));
				}
			});
		} else if (typeClass.equals(DoubleValue.class)) {
			return createNumericField(caption, getFieldContextFrom((Double) rangeValue.hackValue(), value), new IOnValueChange<Double>() {
				@Override
				public void valueChanged(Double newValue) {
					valueChangeHandler.valueChanged(new DoubleValue(newValue));
				}
			});
		} else {
			throw new IllegalStateException(String.format("Unknown '%s' child: '%s'", IComparableValueData.class.getSimpleName(), typeClass.getName()));
		}
	}

	private void crossBindNumberRangeValidators(Class<? extends IComparableValueData> typeClass, final AbstractField<? extends Object> minField, final AbstractField<? extends Object> maxField) {
		final NumberRangeValidator<Number> minFieldValidator = getNumberRangeValidator(minField);
		final NumberRangeValidator<Number> maxFieldValidator = getNumberRangeValidator(maxField);

		minFieldValidator.addBoundsProvider(new Interval<Number>(null, maxFieldValidator.parse((String) maxField.getValue())));
		maxFieldValidator.addBoundsProvider(new Interval<Number>(minFieldValidator.parse((String) minField.getValue()), null));
	}

	@SuppressWarnings("unchecked")
	private NumberRangeValidator<Number> getNumberRangeValidator(AbstractField<? extends Object> field) {
		for (Validator validator : field.getValidators()) {
			if (validator instanceof NumberRangeValidator) {
				return (NumberRangeValidator<Number>) validator;
			}
		}
		throw new IllegalStateException("Number validator not found.");
	}
}