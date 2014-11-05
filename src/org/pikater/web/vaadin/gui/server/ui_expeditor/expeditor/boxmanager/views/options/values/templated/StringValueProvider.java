package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.values.StringValue;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.OptionValueForm;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.AbstractFieldProviderForValue;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.special.FileInputFieldProvider;

/**
 * Class providing {@link StringValue}-backed fields to {@link OptionValueForm}.
 * 
 * @author SkyCrawl
 */
public class StringValueProvider extends AbstractFieldProviderForValue {
	/**
	 * IMPORTANT: {@link FileInputFieldProvider} requires a single text field
	 * to be generated in this method.
	 */
	@Override
	protected void doGenerateFields(final Value value) {
		IFieldContext<String> context = getFieldContextFrom(value);
		addField("value", createTextField("Value:", context, new IOnValueChange<String>() {
			@Override
			public void valueChanged(String newValue) {
				value.setCurrentValue(new StringValue(newValue));
			}
		}));
	}
}