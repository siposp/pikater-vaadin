package org.pikater.web.vaadin.gui.server.components.forms.fields;

import java.util.Collection;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.OptionGroup;

/**
 * <p>Custom checkbox implementation based on {@link OptionGroup} rather
 * than {@link CheckBox} so that it works with Vaadin's {@link FormLayout}
 * properly. If the latter is used, captions are displayed to the right
 * of the checkbox in the form - we want it to be aligned with the rest of
 * the form, on the left side.</p>
 * 
 * <p>At the present time, it only supports single select mode,
 * but can easily be made to support multi-select mode too.</p>
 * 
 * @author SkyCrawl
 */
public class CustomFormCheckBox extends OptionGroup {
	private static final long serialVersionUID = -6320729319786554512L;

	private final Object itemID;

	public CustomFormCheckBox(String componentCaption, String checkBoxCaption, boolean initialState, boolean readOnly) {
		super(componentCaption);
		setImmediate(true);
		setMultiSelect(true);

		this.itemID = checkBoxCaption;
		addItem(this.itemID);
		if (initialState) {
			select(this.itemID);
		}

		setReadOnly(readOnly);
	}

	/**
	 * The provided listener should accept boolean properties.
	 */
	@Override
	public void addValueChangeListener(final ValueChangeListener listener) {
		super.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -9171250614559884524L;

			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				/*
				 * OptionGroup's value change listener somewhat differs from the CheckBox's.
				 * Let's seemingly translate value of the first into a value of the second :). 
				 */
				@SuppressWarnings("unchecked")
				final Collection<Object> selectedValues = (Collection<Object>) event.getProperty().getValue();
				listener.valueChange(new com.vaadin.data.Property.ValueChangeEvent() {
					private static final long serialVersionUID = -2648483124690298644L;

					@Override
					public com.vaadin.data.Property<Boolean> getProperty() {
						return new ObjectProperty<Boolean>(!selectedValues.isEmpty());
					}
				});
			}
		});
	}

	public boolean isSelected() {
		return isSelected(itemID);
	}
}