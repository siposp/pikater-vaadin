package org.pikater.web.vaadin.gui.server.components.forms.fields;

import java.util.Collection;

import org.pikater.web.vaadin.gui.server.components.forms.validators.NumberRangeValidator;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

/**
 * A center for the whole application to create fields
 * that are inteded to be used in forms, especially
 * {@link CustomFormLayout}.
 * 
 * @author SkyCrawl
 */
public class FormFieldFactory {
	//-------------------------------------------------------------------
	// GENERAL FIELD GENERATION

	public static TextField createTextField(String caption, String inputPrompt, String value, boolean required, boolean readOnly) {
		final TextField result = new TextField(caption);
		prepareForACustomForm(result, value, inputPrompt, required, readOnly);
		return result;
	}

	public static PasswordField createPasswordField(String caption, String value, boolean required, boolean readOnly) {
		// TODO: implement proper constraint eventually
		// addValidator(new StringLengthValidator("Five to twenty characters are required.", 5, 20, false));

		PasswordField result = new PasswordField(caption);
		prepareForACustomForm(result, value, "Enter password", required, readOnly);
		return result;
	}

	public static TextArea createTextArea(String caption, String inputPrompt, String value, boolean required, boolean readOnly) {
		final TextArea result = new TextArea(caption);
		result.setWordwrap(true);
		prepareForACustomForm(result, value, inputPrompt, required, readOnly);
		return result;
	}

	public static ComboBox createComboBox(String caption, Collection<?> options, Object defaultOption, boolean required, boolean readonly) {
		ComboBox result = new ComboBox(caption, options);
		result.setNewItemsAllowed(false);
		result.setImmediate(true);
		result.setTextInputAllowed(false);
		result.setNullSelectionAllowed(false);
		result.setRequired(required);
		if (defaultOption != null) {
			result.select(defaultOption);
		}
		result.setReadOnly(readonly);
		return result;
	}

	public static CustomFormCheckBox createCheckBox(String componentCaption, String checkBoxCaption, boolean initialState, boolean readOnly) {
		return new CustomFormCheckBox(componentCaption, checkBoxCaption, initialState, readOnly);
	}

	public static OptionGroup createOptionGroup(String caption, boolean required, boolean readOnly) {
		OptionGroup result = new OptionGroup(caption);
		result.setImmediate(true);
		result.setRequired(required);
		result.setReadOnly(readOnly);
		return result;
	}

	//-------------------------------------------------------------------
	// SPECIFIC FIELD GENERATION

	public static TextField getLoginField(String loginValue, boolean required, boolean readOnly) {
		// TODO: eventually add a special validator too
		return createTextField("Login:", "Enter a login", loginValue, required, readOnly);
	}

	public static TextField getEmailField(String emailValue, boolean required, boolean readOnly) {
		final TextField result = createTextField("Email:", "Enter an email", emailValue, required, readOnly);
		result.addValidator(new EmailValidator("Invalid email address."));
		return result;
	}

	/**
	 * A generic method to create a range-validated number (Integer, Float or Double) field.
	 */
	public static <N extends Number> TextField getNumericField(String caption, N value, N min, N max, boolean required, boolean readOnly) {
		if (value == null) {
			throw new NullPointerException();
		} else {
			final TextField result = createTextField(caption, "Enter a number", String.valueOf(value), required, readOnly);
			result.addValidator(new Validator() {
				private static final long serialVersionUID = 8536719332070293601L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					// validate required field - it has to non-empty
					if (result.isRequired() && ((value == null) || ((String) value).isEmpty())) {
						throw new InvalidValueException(result.getRequiredError());
					}
				}
			});
			result.addValidator(new NumberRangeValidator<N>(value.getClass(), min, max));
			return result;
		}
	}

	//-------------------------------------------------------------------
	// PRIVATE INTERFACE - SPECIAL FIELD BEHAVIOUR

	private static void prepareForACustomForm(final AbstractTextField field, String value, String inputPrompt, boolean required, boolean readOnly) {
		field.setValue(value == null ? "" : value);
		field.setInputPrompt(inputPrompt);
		field.setReadOnly(readOnly);
		field.setRequired(required);
		field.setRequiredError("This field is required.");
		if (!readOnly) {
			field.setValidationVisible(true);
			field.setTextChangeTimeout(500); // timeout to validate the field after text change event
			field.addTextChangeListener(new FieldEvents.TextChangeListener() {
				private static final long serialVersionUID = 1895549466379801259L;

				@Override
				public void textChange(TextChangeEvent event) {
					// necessary for auto form update to work:
					int position = field.getCursorPosition();
					field.setValue(event.getText()); // triggers a value change event which triggers validation
					field.setCursorPosition(position);
				}
			});
		}
	}
}
