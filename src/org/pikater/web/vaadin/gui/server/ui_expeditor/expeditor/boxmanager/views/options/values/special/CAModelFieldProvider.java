package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.special;

import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.server.components.dbviews.pickers.ModelWizardPicker;
import org.pikater.web.vaadin.gui.server.components.dbviews.pickers.ModelWizardPickerOutput;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.values.templated.IntegerValueProvider;
import org.vaadin.actionbuttontextfield.ActionButtonTextField;
import org.vaadin.actionbuttontextfield.widgetset.client.ActionButtonType;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.TextField;

import de.steinwedel.messagebox.MessageBox;

/**
 * Adds a special computation agent model ID lookup to a 
 * field inherited from parent.
 * 
 * @author SkyCrawl
 * @see {@link IntegerValueProvider} 
 */
public class CAModelFieldProvider extends IntegerValueProvider {
	private final String agentClassSimpleName;

	public CAModelFieldProvider(String agentClassSimpleName) {
		this.agentClassSimpleName = agentClassSimpleName;
	}

	@Override
	protected void doGenerateFields(Value value) {
		/*
		 * Generates an appropriate integer field.
		 */
		super.doGenerateFields(value);

		/*
		 * Get the text field.
		 */
		final TextField tf_value = (TextField) getGeneratedFields().entrySet().iterator().next().getValue();

		/*
		 * Add action button to it.
		 */
		ActionButtonTextField tf_value_extension = ActionButtonTextField.extend(tf_value);
		tf_value_extension.getState().type = ActionButtonType.ACTION_SEARCH;
		tf_value_extension.addClickListener(new ActionButtonTextField.ClickListener() {
			@Override
			public void buttonClick(ActionButtonTextField.ClickEvent clickEvent) {
				MessageBox mb = GeneralDialogs.wizardDialog("Search for model", new ModelWizardPicker(UserAuth.getUserEntity(VaadinSession.getCurrent()), agentClassSimpleName),
						new GeneralDialogs.IDialogResultHandler() {
							@Override
							public boolean handleResult(Object[] args) {
								ModelWizardPickerOutput output = (ModelWizardPickerOutput) args[0];
								tf_value.setValue(String.valueOf(output.getResult().getCreatedModel().getId()));
								return true; // close dialog
							}
						});
				mb.setWidth("800px");
				mb.setHeight("600px");
			}
		});
	}
}