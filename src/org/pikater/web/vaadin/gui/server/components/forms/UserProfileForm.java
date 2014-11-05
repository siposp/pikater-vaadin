package org.pikater.web.vaadin.gui.server.components.forms;

import org.pikater.shared.database.jpa.JPAUser;
import org.pikater.shared.database.jpa.daos.DAOs;
import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * A form to display and change user profile.
 * 
 * @author SkyCrawl
 */
public class UserProfileForm extends CustomFormLayout {
	private static final long serialVersionUID = 1654056604776473039L;

	private final TextField loginField;
	private final PasswordField passwordField;
	private final TextField emailField;

	private final Button btn_changePassword;

	public UserProfileForm() {
		super("Save changes");

		this.loginField = FormFieldFactory.getLoginField(null, false, true); // not required since completely read-only
		this.passwordField = FormFieldFactory.createPasswordField("Password:", null, true, true);
		this.emailField = FormFieldFactory.getEmailField(null, true, false);

		this.btn_changePassword = new Button("Change password", new ClickListener() {
			private static final long serialVersionUID = -8548997603012122979L;

			@Override
			public void buttonClick(ClickEvent event) {
				GeneralDialogs.componentDialog("Change password", new ChangePasswordForm() {
					private static final long serialVersionUID = -6536658886477204213L;

					@Override
					public boolean handleResult(Object[] args) {
						setValueAndIgnoreReadOnly(passwordField, (String) args[0]);
						return true;
					}
				});
			}
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		JPAUser currentUser = UserAuth.getUserEntity(VaadinSession.getCurrent());

		setValueAndIgnoreReadOnly(loginField, currentUser.getLogin());
		setCommitted(loginField);
		setValueAndIgnoreReadOnly(passwordField, currentUser.getPassword());
		setCommitted(passwordField);
		emailField.setValue(currentUser.getEmail());
		setCommitted(emailField);

		addField("login", loginField);
		addField("password", passwordField);
		addField("email", emailField);

		attachToButtonInterface(btn_changePassword);
	}

	@Override
	public IOnSubmit getSubmitAction() {
		return new IOnSubmit() {
			@Override
			public boolean onSubmit() {
				JPAUser currentUser = UserAuth.getUserEntity(VaadinSession.getCurrent());
				if (!WebAppConfiguration.avoidUsingDBForNow()) {
					currentUser.setPassword(passwordField.getValue());
					currentUser.setEmail(emailField.getValue());
					DAOs.userDAO.updateEntity(currentUser);
				}

				MyNotifications.showSuccess(null, "Changes were successfully saved.");
				return true;
			}
		};
	}
}