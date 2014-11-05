package org.pikater.web.vaadin.gui.server.components.popups.dialogs;

import org.pikater.shared.database.jpa.JPAUser;
import org.pikater.shared.database.jpa.daos.DAOs;
import org.pikater.web.vaadin.gui.server.components.forms.CreateAccountForm;
import org.pikater.web.vaadin.gui.server.components.forms.LoginForm;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;

/**
 * A utility class to implement specialized dialogs for a particular use.
 * 
 * @author SkyCrawl
 */
public class SpecialDialogs extends DialogCommons {
	public static MessageBox loginDialog(
			final IDialogResultHandler resultHandler) {
		LoginForm loginForm = new LoginForm();
		MyComponentMessageBoxListenerWithExternalResultHandler<LoginForm> listener = new MyComponentMessageBoxListenerWithExternalResultHandler<LoginForm>(
				loginForm, resultHandler) {
			@Override
			protected boolean handleCustomButton(ButtonId button) {
				if (button == ButtonId.CUSTOM_1) {
					GeneralDialogs.componentDialog("Create a new account",
							new CreateAccountForm() {
								private static final long serialVersionUID = 7554808434070423018L;

								@Override
								public boolean handleResult(Object[] args) {
									DAOs.userDAO.storeEntity(JPAUser
											.createAccountForGUI(
													(String) args[0],
													(String) args[1],
													(String) args[2]));
									return true;
								}
							});
					return true;
				}
				return false;
			}
		};
		MessageBox mb = MessageBox.showCustomized(Icon.NONE,
				"Please, authenticate yourself", loginForm, listener,
				ButtonId.OK, ButtonId.CUSTOM_1);
		listener.setParentBox(mb); // don't forget this!
		setupGeneralDialog(mb, false);
		bindActionsToKeyboard(mb, mb.getButton(ButtonId.OK), false);
		mb.getButton(ButtonId.CUSTOM_1).setCaption("Create account");
		return mb;
	}
}