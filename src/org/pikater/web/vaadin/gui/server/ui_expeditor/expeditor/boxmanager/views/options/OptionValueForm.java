package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.base.ValueType;
import org.pikater.core.ontology.subtrees.newoption.restrictions.TypeRestriction;
import org.pikater.core.ontology.subtrees.newoption.values.BooleanValue;
import org.pikater.core.ontology.subtrees.newoption.values.DoubleValue;
import org.pikater.core.ontology.subtrees.newoption.values.FloatValue;
import org.pikater.core.ontology.subtrees.newoption.values.IntegerValue;
import org.pikater.core.ontology.subtrees.newoption.values.NullValue;
import org.pikater.core.ontology.subtrees.newoption.values.QuestionMarkRange;
import org.pikater.core.ontology.subtrees.newoption.values.QuestionMarkSet;
import org.pikater.core.ontology.subtrees.newoption.values.StringValue;
import org.pikater.core.ontology.subtrees.newoption.values.interfaces.IValidatedValueData;
import org.pikater.core.ontology.subtrees.newoption.values.interfaces.IValueData;
import org.pikater.shared.util.collections.BidiMap;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.experiment.server.BoxType;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.BoxManagerOptionView;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.AbstractFieldProviderForValue;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.special.CAModelFieldProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.special.FileInputFieldProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.BooleanValueProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.DoubleValueProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.FloatValueProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.IntegerValueProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.QMRValueProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.QMSValueProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.StringValueProvider;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;

/**
 * <p>
 * Master component of {@link BoxManagerOptionView}.
 * </p>
 * 
 * <p>
 * Makes a best effort at displaying the given arguments, even if not valid.
 * That way, the source box can still be usable in some situations.
 * </p>
 */
public class OptionValueForm extends CustomFormLayout {
	private static final long serialVersionUID = 2200291325058461983L;

	/**
	 * Data source for this component. Provides information about currently
	 * viewed data structures.
	 */
	private final IOptionViewDataSource dataSource;

	/**
	 * An object that constructs value fields for the given value and provides
	 * some other useful interface.
	 */
	private AbstractFieldProviderForValue typeSpecificFieldProvider;

	/*
	 * Programmatic variables.
	 */
	private final BidiMap<ValueType, String> typeToDisplayString;

	public OptionValueForm(IOptionViewDataSource dataSource) {
		super(null);
		this.dataSource = dataSource;
		this.typeSpecificFieldProvider = null;
		this.typeToDisplayString = new BidiMap<ValueType, String>();

		setupFields();
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public IOnSubmit getSubmitAction() {
		return null;
	}

	// ---------------------------------------------------------------------------------------
	// ROOT METHOD TO CREATE THE FORM - CREATES A TYPE FIELD AND CALLS OTHER
	// METHODS OF THIS CLASS

	private void setupFields() {
		// go through allowed types, validate, register, sort & transform into
		// strings
		List<String> typeOptions = new ArrayList<String>();
		for (ValueType type : dataSource.getAllowedTypes().getTypes()) {
			/*
			 * IMPORTANT: this is assumed to check various things which are
			 * needed both here and in other methods of this class.
			 */
			if (type.isValid()) {
				String typeStr = null;
				try {
					typeStr = type.getDefaultValue().toDisplayName();
				} catch (Exception t) {
					PikaterWebLogger
							.logThrowable(
									String.format(
											"Could not transform the '%s' value type to display string.",
											type.getDefaultValue().getClass()),
									t);
					continue;
				}

				if (type.isRangeRestrictionDefined()) {
					typeStr = typeStr + "[R]";
				} else if (type.isSetRestrictionDefined()) {
					typeStr = typeStr + "[S]";
				}

				if (typeToDisplayString.containsValue(typeStr)) {
					PikaterWebLogger.logThrowable("Duplicate type detected.",
							new IllegalStateException());
				} else {
					this.typeToDisplayString.put(type, typeStr);
					typeOptions.add(typeStr);
				}
			}
		}
		Collections.sort(typeOptions, new Comparator<String>() {
			private final String nullDisplayName = new NullValue()
					.toDisplayName();

			@Override
			public int compare(String o1, String o2) {
				if (o1.equals(nullDisplayName)) {
					return -1;
				} else if (o2.equals(nullDisplayName)) {
					return 1;
				} else {
					return o1.compareTo(o2);
				}
			}
		});

		// display a warning to the user if there was a problem with the allowed
		// types
		if (typeOptions.isEmpty()) {
			MyNotifications.showError("No types defined or all invalid",
					"Please, contact the admins.");
		} else if (typeOptions.size() != dataSource.getAllowedTypes()
				.getTypes().size()) // a type is missing
		{
			MyNotifications.showWarning("Some types not valid",
					"If this is a bug, contact admins.");
		} else {
			String selectedType;
			if ((dataSource.getValue().getType() != null)
					&& typeToDisplayString.containsKey(dataSource.getValue()
							.getType())) {
				// value has defined type and it is a known type in restrictions
				selectedType = typeToDisplayString.getValue(dataSource
						.getValue().getType());
				if (dataSource.getValue().getCurrentValue() == null) {
					dataSource.getValue().setCurrentValue(
							dataSource.getValue().getType().getDefaultValue()
									.clone());
				}
			} else // auto-correct accidental invalid type binding
			{
				selectedType = typeOptions.get(0);
				dataSource.getValue().setType(
						typeToDisplayString.getKey(selectedType));
				dataSource.getValue().setCurrentValue(
						typeToDisplayString.getKey(selectedType)
								.getDefaultValue().clone());
			}
			// at this point, current value needs to be set (non-null) for the
			// 'value' argument

			// create type combobox and set it up
			ComboBox cb_type = FormFieldFactory.createComboBox("Type:",
					typeOptions, selectedType, true, false);
			cb_type.setSizeFull();
			cb_type.addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 3736100120428402858L;

				/**
				 * Reloads type specific forms fields.
				 */
				@Override
				public void valueChange(ValueChangeEvent event) {
					// first change type and reset value to default
					ValueType newlySelectedType = typeToDisplayString
							.getKey((String) event.getProperty().getValue());
					dataSource.getValue().setType(newlySelectedType);
					dataSource.getValue().setCurrentValue(
							newlySelectedType.getDefaultValue().clone());

					// and then recreate type specific fields
					recreateTypeSpecificFields(dataSource.getValue());
				}
			});
			addField("type", cb_type);

			// create and setup other fields
			recreateTypeSpecificFields(dataSource.getValue());

			// add a special button to reset value to default (default value of
			// the currently selected type)
			attachToButtonInterface(new Button("Reset value",
					new Button.ClickListener() {
						private static final long serialVersionUID = 838180535195566779L;

						@Override
						public void buttonClick(ClickEvent event) {
							// first reset the value
							dataSource.getValue().setCurrentValue(
									dataSource.getValue().getType()
											.getDefaultValue().clone());

							// and then recreate type specific fields
							recreateTypeSpecificFields(dataSource.getValue());
						}
					}));
		}
	}

	// ---------------------------------------------------------------------------------------
	// SECOND-LEVEL METHOD TO CREATE THE FORM - CREATES FIELDS IN TYPE-SPECIFIC
	// MANNER

	/**
	 * The {@link #setupFields(Value, TypeRestriction)} method ensures that the
	 * {@link Value#getCurrentValue()} doesn't return null for the argument and
	 * thus, this method requires it.</br> IMPORTANT: this method is required
	 * to:
	 * <ul>
	 * <li>Set the appropriate child of {@link AbstractFieldProviderForValue} to
	 * the {@link #typeSpecificFieldProvider} field.
	 * <li>Register all the fields provided by
	 * {@link #typeSpecificFieldProvider} into the form.
	 * </ul>
	 */
	private void recreateTypeSpecificFields(Value value) {
		// first get rid of the old fields, if any are defined
		if (typeSpecificFieldProvider != null) {
			for (AbstractField<? extends Object> field : typeSpecificFieldProvider
					.getGeneratedFields().values()) {
				removeField(field);
			}
		}

		// then validate the value if need be
		if (value.getCurrentValue() instanceof IValidatedValueData) // currently
																	// only QMR
																	// & QMS
		{
			if (!((IValidatedValueData) value.getCurrentValue()).isValid()) {
				PikaterWebLogger.logThrowable("", new IllegalStateException(
						"Invalid value received."));
				MyNotifications.showWarning(null, "Invalid value received.");
				return;
			}
		}

		// then get the appropriate field provider according to the given value
		// type
		Class<? extends IValueData> typeClass = value.getCurrentValue()
				.getClass();
		if (typeClass.equals(BooleanValue.class)) {
			typeSpecificFieldProvider = new BooleanValueProvider();
		} else if (typeClass.equals(StringValue.class)) {
			// first special treatment and then default
			if ((dataSource.getBox().getBoxType() == BoxType.INPUT)
					&& dataSource.getBox().getAssociatedAgent().getName()
							.equals("FileInput")
					&& dataSource.getOption().getName().equals("fileURI")) {
				typeSpecificFieldProvider = new FileInputFieldProvider();
			} else {
				typeSpecificFieldProvider = new StringValueProvider();
			}
		} else if (typeClass.equals(IntegerValue.class)) {
			// first special treatment and then default
			if ((dataSource.getBox().getBoxType() == BoxType.COMPUTE)
					&& dataSource.getOption().getName().equals("model")) {
				typeSpecificFieldProvider = new CAModelFieldProvider(dataSource
						.getBox().getAssociatedAgent().getAgentClassName());
			} else {
				typeSpecificFieldProvider = new IntegerValueProvider();
			}
		} else if (typeClass.equals(FloatValue.class)) {
			typeSpecificFieldProvider = new FloatValueProvider();
		} else if (typeClass.equals(DoubleValue.class)) {
			typeSpecificFieldProvider = new DoubleValueProvider();
		} else if (typeClass.equals(QuestionMarkRange.class)) {
			typeSpecificFieldProvider = new QMRValueProvider();
		} else if (typeClass.equals(QuestionMarkSet.class)) {
			typeSpecificFieldProvider = new QMSValueProvider();
		} else if (typeClass.equals(NullValue.class)) {
			// these values can not be set and hence no field is created
			// whatsoever
			return;
		} else {
			PikaterWebLogger.logThrowable(
					"",
					new IllegalStateException(String.format(
							"Unimplemented value type used: '%s'.",
							typeClass.getName())));
			MyNotifications.showWarning("Unsupported option type",
					typeClass.getSimpleName());
			return;
		}

		// and finally, generate fields and register them
		typeSpecificFieldProvider.generateFields(value);
		for (Entry<String, AbstractField<? extends Object>> entry : typeSpecificFieldProvider
				.getGeneratedFields().entrySet()) {
			addField(entry.getKey(), entry.getValue());
		}
	}
}
