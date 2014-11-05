package org.pikater.web.vaadin.gui.server.components.forms;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.util.collections.CustomOrderSet;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogResultPreparer;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;

/**
 * A form to set dataset visualization parameters with.
 * 
 * @author SkyCrawl
 */
public class DatasetVisualizationForm extends CustomFormLayout implements IDialogResultPreparer {
	private static final long serialVersionUID = -223143365023322543L;

	private final ComboBox field_attrTarget;
	private final OptionGroup field_attrsToCompare; // TODO: horizontal flow layout?

	public DatasetVisualizationForm(JPADataSetLO dataset) {
		super(null);

		List<JPAAttributeMetaData> targetAttrs = dataset.getTargetAttributes();
		this.field_attrTarget = FormFieldFactory.createComboBox("Target attribute:", targetAttrs, targetAttrs.get(targetAttrs.size() - 1), true, false);
		for (JPAAttributeMetaData attr : targetAttrs) {
			this.field_attrTarget.setItemCaption(attr, attr.getName());
		}

		this.field_attrsToCompare = FormFieldFactory.createOptionGroup("Attributes:", true, false);
		this.field_attrsToCompare.setMultiSelect(true);
		Set<JPAAttributeMetaData> customOrderAttributes = new CustomOrderSet<JPAAttributeMetaData>(dataset.getAttributeMetaData(), new Comparator<JPAAttributeMetaData>() {
			@Override
			public int compare(JPAAttributeMetaData o1, JPAAttributeMetaData o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (JPAAttributeMetaData attr : customOrderAttributes) {
			this.field_attrsToCompare.addItem(attr);
			this.field_attrsToCompare.setItemCaption(attr, attr.getName());
			this.field_attrsToCompare.select(attr);
		}

		addField("target", this.field_attrTarget);
		addField("attributes", this.field_attrsToCompare);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public IOnSubmit getSubmitAction() {
		return null;
	}

	@Override
	public void addArgs(List<Object> arguments) {
		arguments.add(getSelectedAttributes());
		arguments.add(getSelectedTargetAttribute());
	}

	public JPAAttributeMetaData getSelectedTargetAttribute() {
		return (JPAAttributeMetaData) field_attrTarget.getValue();
	}

	@SuppressWarnings("unchecked")
	public JPAAttributeMetaData[] getSelectedAttributes() {
		return ((Set<JPAAttributeMetaData>) field_attrsToCompare.getValue()).toArray(new JPAAttributeMetaData[0]);
	}
}