package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.restrictions.RangeRestriction;
import org.pikater.core.ontology.subtrees.newoption.restrictions.SetRestriction;
import org.pikater.core.ontology.subtrees.newoption.values.IntegerValue;
import org.pikater.core.ontology.subtrees.newoption.values.interfaces.IValueData;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.forms.validators.NumberConstant;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.OptionValueForm;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

/**
 * Class wrapping all generic methods providing fields to
 * {@link OptionValueForm}. Designed to be subclassed to provide templated field
 * groups.
 * 
 * @author SkyCrawl
 */
public abstract class AbstractFieldProviderForValue {
	/**
	 * All generated fields.
	 */
	private final Map<String, AbstractField<? extends Object>> generatedFields;

	public AbstractFieldProviderForValue() {
		this.generatedFields = new LinkedHashMap<String, AbstractField<? extends Object>>(); // LinkedHashMap
																								// keeps
																								// insertion
																								// order
	}

	/**
	 * The main entry-point method.
	 */
	public void generateFields(Value value) {
		generatedFields.clear();
		doGenerateFields(value);
	}

	/**
	 * Pick up the result of {@link #generatedFields(Value)}.
	 */
	public Map<String, AbstractField<? extends Object>> getGeneratedFields() {
		return generatedFields;
	}

	/**
	 * Custom method that actually generates anything. Use this class's static
	 * methods to generate fields and then register them with
	 * {@link #addField(String, AbstractField)}.
	 */
	protected abstract void doGenerateFields(Value value);

	protected void addField(String notificationDescription,
			AbstractField<? extends Object> field) {
		generatedFields.put(notificationDescription, field);
	}

	// --------------------------------------------------------------------------
	// UTILITY METHODS TO GENERATE FIELDS

	@SuppressWarnings("unchecked")
	protected static <N extends Number & Comparable<? super N>> AbstractField<? extends Object> createNumericField(
			String caption, final IFieldContext<N> context,
			final IOnValueChange<N> valueChangeHandler) {
		// create & bind with the value type
		if (context.getSetRestriction() != null) {
			return createEnumeratedField(context.getCurrentValue(),
					(List<N>) getSortedEnumerationForValue(context
							.getSetRestriction().getValues()), caption,
					valueChangeHandler);
		} else // whether range restriction is defined or not
		{
			N min = null, max = null;
			if (context.getRangeRestriction() != null) {
				if (context.getRangeRestriction().getMinValue() != null) {
					min = (N) context.getRangeRestriction().getMinValue()
							.hackValue();
				}
				if (context.getRangeRestriction().getMaxValue() != null) {
					max = (N) context.getRangeRestriction().getMaxValue()
							.hackValue();
				}
			}
			TextField tf_value = FormFieldFactory.getNumericField(caption,
					context.getCurrentValue(), min, max, true, false);
			tf_value.setWidth("100%");
			tf_value.addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 3736100120428402858L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					NumberConstant numberParser = NumberConstant
							.fromNumberClass(context.getCurrentValue()
									.getClass());
					Number parsedNumber;
					try {
						parsedNumber = numberParser.parse((String) event
								.getProperty().getValue());
					} catch (Exception t) {
						// if the field's content can not be parsed, do nothing
						// (validation error will be displayed)
						return;
					}
					valueChangeHandler.valueChanged((N) parsedNumber);
				}
			});
			return tf_value;
		}
	}

	@SuppressWarnings("unchecked")
	protected static <O extends Object> AbstractField<? extends Object> createEnumeratedField(
			O currentValue, List<O> options, String caption,
			final IOnValueChange<O> valueChangeHandler) {
		final ComboBox cb_value = FormFieldFactory.createComboBox(caption,
				options, currentValue, true, false);
		cb_value.setWidth("100%");
		cb_value.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -3938305148585892660L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				valueChangeHandler.valueChanged((O) event.getProperty()
						.getValue());
			}
		});
		return cb_value;
	}

	protected static AbstractField<? extends Object> createTextField(
			String caption, final IFieldContext<String> context,
			final IOnValueChange<String> valueChangeHandler) {
		if (context.getSetRestriction() != null) {
			List<String> options = getSortedEnumerationForValue(context
					.getSetRestriction().getValues());
			return createEnumeratedField(context.getCurrentValue(), options,
					"Value:", valueChangeHandler);
		} else {
			if (context.getRangeRestriction() != null) {
				// TODO: range restriction
				throw new IllegalStateException(
						"Ranged field is not yet implemented for strings.");
			} else {
				TextField tf_value = FormFieldFactory.createTextField(caption,
						null, context.getCurrentValue(), true, false);
				tf_value.setWidth("100%");
				tf_value.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = -6565459293274644984L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						valueChangeHandler.valueChanged((String) event
								.getProperty().getValue());
					}
				});
				return tf_value;
			}
		}
	}

	protected static AbstractField<? extends Object> createAttemptsField(
			final int defaultCountOfValuesToTry,
			final IOnValueChange<Integer> valueChangeHandler) {
		return createNumericField("Attempts:", new IFieldContext<Integer>() {
			@Override
			public Integer getCurrentValue() {
				return defaultCountOfValuesToTry;
			}

			@Override
			public RangeRestriction getRangeRestriction() {
				// let agents handle corner cases, this implementation strives
				// for a high level of abstraction
				return new RangeRestriction(new IntegerValue(1), null);
			}

			@Override
			public SetRestriction getSetRestriction() {
				return null;
			}
		}, valueChangeHandler);
	}

	// --------------------------------------------------------------------------
	// SPECIAL TYPES

	protected interface IFieldContext<O extends Object> {
		O getCurrentValue();

		SetRestriction getSetRestriction();

		RangeRestriction getRangeRestriction();
	}

	protected interface IOnValueChange<O extends Object> {
		void valueChanged(O object);
	}

	// --------------------------------------------------------------------------
	// UTILITY METHODS TO CREATE CONTEXT OBJECTS USED IN OTHER METHODS OF THIS
	// CLASS

	protected static <O extends Object> IFieldContext<O> getFieldContextFrom(
			final O currentValue, final Value value) {
		return new IFieldContext<O>() {
			@Override
			public O getCurrentValue() {
				return currentValue;
			}

			@Override
			public RangeRestriction getRangeRestriction() {
				return value.getType().getRangeRestriction();
			}

			@Override
			public SetRestriction getSetRestriction() {
				return value.getType().getSetRestriction();
			}
		};
	}

	@SuppressWarnings("unchecked")
	protected static <O extends Object> IFieldContext<O> getFieldContextFrom(
			final Value value) {
		return getFieldContextFrom((O) value.getCurrentValue().hackValue(),
				value);
	}

	// --------------------------------------------------------------------------
	// VARIOUS OTHER UTILITY METHODS

	@SuppressWarnings("unchecked")
	protected static <O extends Object> List<O> getUnsortedEnumerationForValue(
			List<IValueData> values) {
		List<O> options = new ArrayList<O>();
		for (IValueData possibleValue : values) {
			options.add((O) possibleValue.hackValue());
		}
		return options;
	}

	@SuppressWarnings("unchecked")
	protected static <O extends Object & Comparable<? super O>> List<O> getSortedEnumerationForValue(
			List<IValueData> values) {
		List<O> options = (List<O>) getUnsortedEnumerationForValue(values);
		Collections.sort(options);
		return options;
	}
}
