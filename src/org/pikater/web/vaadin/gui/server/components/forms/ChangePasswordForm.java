package org.pikater.web.vaadin.gui.server.components.forms;

import java.util.List;

import org.pikater.shared.database.jpa.JPAUser;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogComponent;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.PasswordField;

/**
 * A form to for users to change their passwods with.
 * 
 * @author SkyCrawl
 */
public abstract class ChangePasswordForm extends CustomFormLayout implements IDialogComponent {
	private static final long serialVersionUID = 5179296189317359241L;

	private final PasswordField pf_currentPassword;
	private final PasswordField pf_newPassword;
	private final PasswordField pf_newPasswordAgain;

	public ChangePasswordForm() {
		super(null);

		this.pf_currentPassword = FormFieldFactory.createPasswordField("Current password:", null, true, false);
		this.pf_newPassword = FormFieldFactory.createPasswordField("New password:", null, true, false);
		this.pf_newPasswordAgain = FormFieldFactory.createPasswordField("New password again:", null, true, false);

		addField("current password", pf_currentPassword);
		addField("new password", pf_newPassword);
		addField("new password again", pf_newPasswordAgain);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public IOnSubmit getSubmitAction() {
		return null;
	}

	@Override
	public boolean isFormValidAndUpdated() {
		// TODO: looks like each field needs to be validated in a special manner... displaying a notification of what's wrong (unless everything's visible)
		JPAUser currentUser = UserAuth.getUserEntity(VaadinSession.getCurrent());
		boolean validated = super.isFormValidAndUpdated();
		if (!validated) {
			return false; // a notification of some sort is assumed to have been shown
		} else if (!pf_currentPassword.getValue().equals(currentUser.getPassword())) {
			MyNotifications.showError(null, "Current password is not correct.");
			return false;
		} else if (!pf_newPassword.getValue().equals(pf_newPasswordAgain.getValue())) {
			MyNotifications.showError(null, "New passwords don't match.");
			return false;
		} else {
			return true;
		}
	}

	public String getChangedPassword() {
		return pf_newPassword.getValue();
	}

	//--------------------------------------------------------------------
	// METHODS DEFINING THIS FORM'S BEHAVIOUR AS A PART OF A DIALOG

	@Override
	public void addArgs(List<Object> arguments) {
		arguments.add(getChangedPassword());
	}
}