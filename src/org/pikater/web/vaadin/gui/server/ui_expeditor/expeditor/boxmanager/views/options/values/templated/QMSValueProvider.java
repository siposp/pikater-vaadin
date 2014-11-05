package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated;

import java.util.Collection;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.values.QuestionMarkSet;
import org.pikater.core.ontology.subtrees.newoption.values.interfaces.IValueData;
import org.pikater.shared.util.collections.BidiMap;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.OptionValueForm;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.AbstractFieldProviderForValue;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.OptionGroup;

/**
 * Class providing {@link QuestionMarkSet}-backed fields to {@link OptionValueForm}.
 * 
 * @author SkyCrawl
 */
public class QMSValueProvider extends AbstractFieldProviderForValue {
	@Override
	protected void doGenerateFields(Value value) {
		// first cast value
		final QuestionMarkSet currentValue = (QuestionMarkSet) value.getCurrentValue();

		// then create the attempts field
		addField("attempts", createAttemptsField(currentValue.getCountOfValuesToTry(), new IOnValueChange<Integer>() {
			@Override
			public void valueChanged(Integer newValue) {
				currentValue.setCountOfValuesToTry(newValue);
			}
		}));

		// and finally, create list select for the set
		final BidiMap<Object, IValueData> itemIDToValue = new BidiMap<Object, IValueData>();
		OptionGroup setFilter = FormFieldFactory.createOptionGroup("Values:", false, false);
		setFilter.setMultiSelect(true);
		for (IValueData setValue : value.getType().getSetRestriction().getValues()) {
			Object itemID = setFilter.addItem();
			setFilter.setItemCaption(itemID, setValue.hackValue().toString());
			itemIDToValue.put(itemID, setValue);
		}
		for (IValueData setValue : currentValue.getUserDefinedRestriction().getValues()) {
			setFilter.select(itemIDToValue.getKey(setValue));
		}
		setFilter.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -7264483694504651328L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				@SuppressWarnings("unchecked")
				Collection<Object> selectedValues = (Collection<Object>) event.getProperty().getValue();

				currentValue.getUserDefinedRestriction().getValues().clear();
				for (Object itemID : selectedValues) {
					currentValue.getUserDefinedRestriction().getValues().add(itemIDToValue.getValue(itemID));
				}
			}
		});
		addField("values", setFilter);
	}
}