package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.values.DoubleValue;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.OptionValueForm;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.AbstractFieldProviderForValue;

/**
 * Class providing {@link DoubleValue}-backed fields to {@link OptionValueForm}.
 * 
 * @author SkyCrawl
 */
public class DoubleValueProvider extends AbstractFieldProviderForValue {
	@Override
	protected void doGenerateFields(final Value value) {
		IFieldContext<Double> context = getFieldContextFrom(value);
		addField("value", createNumericField("Value:", context, new IOnValueChange<Double>() {
			@Override
			public void valueChanged(Double number) {
				value.setCurrentValue(new DoubleValue(number));
			}
		}));
	}
}