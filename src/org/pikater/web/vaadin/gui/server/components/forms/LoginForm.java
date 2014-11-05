package org.pikater.web.vaadin.gui.server.components.forms;

import java.util.List;

import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogResultPreparer;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * A form for users to authenticate with.
 * 
 * @author SkyCrawl
 */
public class LoginForm extends CustomFormLayout implements IDialogResultPreparer {
	private static final long serialVersionUID = -2356468027629344476L;

	private final TextField loginField;
	private final PasswordField passwordField;

	public LoginForm() {
		super(null);

		/*
		this.loginField = FormFieldFactory.getLoginField("sj", true, false);
		this.passwordField = FormFieldFactory.getGeneralPasswordField("Password:", "123", true, false);
		*/

		this.loginField = FormFieldFactory.getLoginField("", true, false);
		this.passwordField = FormFieldFactory.createPasswordField("Password:", "", true, false);

		addField("login", loginField);
		addField("password", passwordField);
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

	//--------------------------------------------------------------------
	// METHODS DEFINING THIS FORM'S BEHAVIOUR AS A PART OF A DIALOG

	@Override
	public void addArgs(List<Object> arguments) {
		arguments.add(getLogin());
		arguments.add(getPassword());
	}
}