package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.values.BooleanValue;
import org.pikater.web.vaadin.gui.server.components.forms.fields.CustomFormCheckBox;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.OptionValueForm;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.AbstractFieldProviderForValue;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

/**
 * Class providing {@link BooleanValue}-backed fields to {@link OptionValueForm}.
 * 
 * @author SkyCrawl
 */
public class BooleanValueProvider extends AbstractFieldProviderForValue {
	@Override
	protected void doGenerateFields(final Value value) {
		CustomFormCheckBox chb_value = FormFieldFactory.createCheckBox("Value:", "", (Boolean) value.getCurrentValue().hackValue(), false);
		chb_value.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -2288402102482210410L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				value.setCurrentValue(new BooleanValue((Boolean) event.getProperty().getValue()));
			}
		});
		addField("value", chb_value);
	}
}