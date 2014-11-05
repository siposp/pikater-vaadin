package org.pikater.web.vaadin.gui.server.components.dbviews;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.users.UsersTableDBRow;
import org.pikater.shared.database.views.tableview.users.UsersTableDBView;
import org.pikater.shared.database.views.tableview.users.UsersTableDBView.Column;
import org.pikater.shared.util.Mailing;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.AbstractDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;

import com.vaadin.ui.AbstractComponent;

/**
 * Configuration for {@link DBTable DB tables} working with users.
 * 
 * @author SkyCrawl
 */
public class UsersDBViewRoot extends AbstractDBViewRoot<UsersTableDBView> {
	public UsersDBViewRoot(UsersTableDBView view) {
		super(view);
	}

	@Override
	public int getColumnSize(ITableColumn column) {
		UsersTableDBView.Column specificColumn = (UsersTableDBView.Column) column;
		switch (specificColumn) {
		case LOGIN:
			return 125;
		case EMAIL:
			return 200;
		case REGISTERED:
			return 100;
		case STATUS:
			return 100;
		case MAX_PRIORITY:
			return 100;
		case ADMIN:
			return 75;

		case RESET_PSWD:
			return 100;

		default:
			throw new IllegalStateException("Unknown state: " + specificColumn.name());
		}
	}

	@Override
	public ITableColumn getExpandColumn() {
		return UsersTableDBView.Column.EMAIL;
	}

	@Override
	public void onCellCreate(ITableColumn column, AbstractDBViewValue<?> value, AbstractComponent component) {
		UsersTableDBView.Column specificColumn = (UsersTableDBView.Column) column;
		if (specificColumn == Column.MAX_PRIORITY) {
			component.setDescription("The higher, the better.");
		} else if (specificColumn == Column.RESET_PSWD) {
			value.setOnCommitCallback(new AbstractDBViewValue.IOnValueCommitted() {
				/*
				 * This action is called after the new password is committed to DB.
				 */
				@Override
				public void onCommitted(AbstractTableRowDBView row, AbstractDBViewValue<?> value) {
					UsersTableDBRow specificRow = (UsersTableDBRow) row;

					StringBuilder sb = new StringBuilder();
					sb.append("Your password has been reset. The new password is:");
					sb.append("\r\n");
					sb.append(specificRow.getNewPlainTextPassword());

					try {
						Mailing.sendEmail(specificRow.getUser().getEmail(), "Password reset", sb.toString());
					} catch (Exception e) {
						// SMTP server should take responsibility for delivering emails, no custom handling needed
						PikaterWebLogger.logThrowable(String.format("Could not send a password reset notification to '%s':", specificRow.getUser().getEmail()), e);
					}
				}
			});
		}
	}

	@Override
	public void approveAction(final ITableColumn column, final AbstractTableRowDBView row, final Runnable action) {
		final UsersTableDBRow specificRow = (UsersTableDBRow) row;

		UsersTableDBView.Column specificColumn = (UsersTableDBView.Column) column;
		if (specificColumn == UsersTableDBView.Column.RESET_PSWD) {
			GeneralDialogs.confirm(null, String.format("Really reset password for user '%s'?", specificRow.getUser().getLogin()), new GeneralDialogs.IDialogResultHandler() {
				@Override
				public boolean handleResult(Object[] args) {
					// action is approved, perform it:
					action.run();
					return true;
				}
			});
		} else {
			throw new IllegalStateException(String.format("Action '%s' has to be approved before being executed", specificColumn.name()));
		}
	}
}
