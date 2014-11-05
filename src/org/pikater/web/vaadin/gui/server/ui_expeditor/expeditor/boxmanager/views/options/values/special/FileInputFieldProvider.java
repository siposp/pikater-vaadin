package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.special;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.shared.database.views.tableview.datasets.DataSetTableDBRow;
import org.pikater.web.vaadin.gui.server.components.dbviews.pickers.DatasetTablePicker;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.StringValueProvider;
import org.vaadin.actionbuttontextfield.ActionButtonTextField;
import org.vaadin.actionbuttontextfield.widgetset.client.ActionButtonType;

import com.vaadin.ui.TextField;

/**
 * Adds a special dataset filename lookup to a 
 * field inherited from parent.
 * 
 * @author SkyCrawl
 * @see {@link StringValueProvider} 
 */
public class FileInputFieldProvider extends StringValueProvider {
	@Override
	protected void doGenerateFields(Value value) {
		/*
		 * Generates an appropriate text field.
		 */
		super.doGenerateFields(value);

		/*
		 * Get the text field.
		 */
		final TextField tf_value = (TextField) getGeneratedFields().entrySet().iterator().next().getValue();

		/*
		 * Add action button it, don't disable user editing (some datasets may be hard to find and user input may be handy). 
		 */
		ActionButtonTextField tf_value_extension = ActionButtonTextField.extend(tf_value);
		tf_value_extension.getState().type = ActionButtonType.ACTION_SEARCH;
		tf_value_extension.addClickListener(new ActionButtonTextField.ClickListener() {
			@Override
			public void buttonClick(ActionButtonTextField.ClickEvent clickEvent) {
				GeneralDialogs.componentDialog("Search for input dataset", new DatasetTablePicker("Select a row and click 'Ok':"), new GeneralDialogs.IDialogResultHandler() {
					@Override
					public boolean handleResult(Object[] args) {
						DataSetTableDBRow row = (DataSetTableDBRow) args[0];
						tf_value.setValue(row.getDataset().getFileName());
						return true; // close dialog
					}
				});
			}
		});
	}
}