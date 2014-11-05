package org.pikater.web.vaadin.gui.server.components.forms;

import java.util.List;

import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogComponent;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * A form to create accounts with.
 * 
 * @author SkyCrawl
 */
public abstract class CreateAccountForm extends CustomFormLayout implements IDialogComponent {
	private static final long serialVersionUID = 1751781460173551178L;

	private final TextField loginField;
	private final PasswordField passwordField;
	private final TextField emailField;

	public CreateAccountForm() {
		super(null);

		this.loginField = FormFieldFactory.getLoginField(null, true, false);
		this.passwordField = FormFieldFactory.createPasswordField("Password:", null, true, false);
		this.emailField = FormFieldFactory.getEmailField(null, true, false);

		addField("login", this.loginField);
		addField("password", this.passwordField);
		addField("email", this.emailField);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public IOnSubmit getSubmitAction() {
		return null;
	}

	public String getLogin() {
		return loginField.getValue();
	}

	public String getPassword() {
		return passwordField.getValue();
	}

	public String getEmail() {
		return emailField.getValue();
	}

	//--------------------------------------------------------------------
	// METHODS DEFINING THIS FORM'S BEHAVIOUR AS A PART OF A DIALOG

	@Override
	public void addArgs(List<Object> arguments) {
		arguments.add(getLogin());
		arguments.add(getPassword());
		arguments.add(getEmail());
	}
}