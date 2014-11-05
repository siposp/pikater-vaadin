package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.values.FloatValue;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.OptionValueForm;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.AbstractFieldProviderForValue;

/**
 * Class providing {@link FloatValue}-backed fields to {@link OptionValueForm}.
 * 
 * @author SkyCrawl
 */
public class FloatValueProvider extends AbstractFieldProviderForValue {
	@Override
	protected void doGenerateFields(final Value value) {
		IFieldContext<Float> context = getFieldContextFrom(value);
		addField("value", createNumericField("Value:", context, new IOnValueChange<Float>() {
			@Override
			public void valueChanged(Float number) {
				value.setCurrentValue(new FloatValue(number));
			}
		}));
	}
}