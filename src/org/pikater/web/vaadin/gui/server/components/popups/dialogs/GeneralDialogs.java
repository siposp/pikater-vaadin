package org.pikater.web.vaadin.gui.server.components.popups.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.pikater.web.vaadin.gui.server.components.wizards.WizardForDialog;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;

/**
 * Defines many non-specific dialogs for general use.
 * 
 * @author SkyCrawl
 */
public class GeneralDialogs extends DialogCommons {
	/**
	 * A simple notification message box that can closed right away.
	 */
	public static MessageBox info(String title, String message) {
		MessageBox mb = MessageBox
				.showPlain(Icon.INFO, title == null ? "Notification" : title,
						message, ButtonId.CLOSE);
		setupGeneralDialog(mb, true);
		bindActionsToKeyboard(mb, mb.getButton(ButtonId.CLOSE), true);
		return mb;
	}

	/**
	 * A simple warning message box that can closed right away.
	 */
	public static MessageBox warning(String title, String message) {
		MessageBox mb = MessageBox.showPlain(Icon.WARN,
				title == null ? "Warning" : title, message, ButtonId.CLOSE);
		setupGeneralDialog(mb, true);
		bindActionsToKeyboard(mb, mb.getButton(ButtonId.CLOSE), true);
		return mb;
	}

	/**
	 * A simple error message box that can closed right away.
	 */
	public static MessageBox error(String title, String message) {
		MessageBox mb = MessageBox.showPlain(Icon.ERROR,
				title == null ? "Error" : title, message, ButtonId.OK);
		setupGeneralDialog(mb, true);
		bindActionsToKeyboard(mb, mb.getButton(ButtonId.OK), true);
		return mb;
	}

	/**
	 * A simple confirmation message box that can closed right away.
	 */
	public static MessageBox confirm(String title, String message,
			IDialogResultHandler resultHandler) {
		MyMessageBoxListener listener = MyMessageBoxListener
				.getDefault(resultHandler);
		MessageBox mb = MessageBox.showPlain(Icon.QUESTION,
				title == null ? "Confirm" : title, message, listener,
				ButtonId.YES, ButtonId.NO);
		listener.setParentBox(mb); // don't forget this!
		setupGeneralDialog(mb, true);
		bindActionsToKeyboard(mb, mb.getButton(ButtonId.YES), true);
		return mb;
	}

	/**
	 * A text-prompt in the form of a dialog.
	 * 
	 * @param inputLabel
	 *            what do we want the user to enter
	 */
	public static MessageBox textPrompt(String title, String inputLabel,
			final IDialogResultHandler resultHandler) {
		final TextField tf = new TextField();
		tf.setInputPrompt("Enter value");
		MyMessageBoxListener listener = new MyMessageBoxListener(resultHandler) {
			@Override
			protected boolean allowOKHandle() {
				return true;
			}

			@Override
			protected void addArgs(List<Object> arguments) {
				arguments.add(tf.getValue());
			}
		};
		MessageBox mb = MessageBox.showCustomized(Icon.QUESTION,
				title == null ? "Text prompt" : title, tf, listener,
				ButtonId.OK);
		listener.setParentBox(mb); // don't forget this!
		setupGeneralDialog(mb, false);
		bindActionsToKeyboard(mb, mb.getButton(ButtonId.OK), true);
		return mb;
	}

	/**
	 * A custom component, wrapped in a dialog.
	 */
	public static MessageBox componentDialog(String title,
			IDialogComponent content) {
		MyComponentMessageBoxListener<IDialogComponent> listener = new MyComponentMessageBoxListener<IDialogComponent>(
				content);
		MessageBox mb = MessageBox.showCustomized(Icon.NONE,
				title != null ? title : "", content, listener, ButtonId.OK,
				ButtonId.CANCEL);
		listener.setParentBox(mb); // don't forget this!
		setupGeneralDialog(mb, false);
		bindActionsToKeyboard(mb, mb.getButton(ButtonId.OK), true);
		return mb;
	}

	/**
	 * A custom component, wrapped in a dialog.
	 */
	public static MessageBox componentDialog(String title,
			IDialogResultPreparer content, IDialogResultHandler resultHandler) {
		MyComponentMessageBoxListenerWithExternalResultHandler<IDialogResultPreparer> listener = new MyComponentMessageBoxListenerWithExternalResultHandler<IDialogResultPreparer>(
				content, resultHandler);
		MessageBox mb = MessageBox.showCustomized(Icon.NONE,
				title != null ? title : "", content, listener, ButtonId.OK,
				ButtonId.CANCEL);
		listener.setParentBox(mb); // don't forget this!
		setupGeneralDialog(mb, false);
		bindActionsToKeyboard(mb, mb.getButton(ButtonId.OK), true);
		return mb;
	}

	/**
	 * A custom wizard component, wrapped in a dialog.
	 */
	public static <C extends WizardForDialog<?> & IDialogComponent> MessageBox wizardDialog(
			String title, final C content) {
		final MessageBox mb = MessageBox.showCustomized(Icon.NONE,
				title != null ? title : "", content);
		setupWizardDialog(mb, content);
		bindActionsToKeyboard(mb, null, true);
		content.getCancelButton().addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 928076878738923314L;

			@Override
			public void buttonClick(ClickEvent event) {
				mb.close();
			}
		});
		content.getFinishButton().addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 928076878738923314L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (content.isResultReadyToBeHandled()) {
					List<Object> arguments = new ArrayList<Object>();
					content.addArgs(arguments);
					content.handleResult(arguments.toArray());
					mb.close();
				}
			}
		});
		content.setSizeFull();
		content.getParent().setSizeFull();
		content.getParent().getParent().setSizeFull();
		return mb;
	}

	/**
	 * A custom wizard component, wrapped in a dialog.
	 */
	public static <C extends WizardForDialog<?> & IDialogResultPreparer> MessageBox wizardDialog(
			String title, final C content,
			final IDialogResultHandler resultHandler) {
		final MessageBox mb = MessageBox.showCustomized(Icon.NONE,
				title != null ? title : "", content);
		setupWizardDialog(mb, content);
		bindActionsToKeyboard(mb, null, true);
		content.getCancelButton().addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 928076878738923314L;

			@Override
			public void buttonClick(ClickEvent event) {
				mb.close();
			}
		});
		content.getFinishButton().addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 928076878738923314L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (content.isResultReadyToBeHandled()) {
					List<Object> arguments = new ArrayList<Object>();
					content.addArgs(arguments);
					resultHandler.handleResult(arguments.toArray());
					mb.close();
				}
			}
		});
		content.setSizeFull();
		content.getParent().setSizeFull();
		content.getParent().getParent().setSizeFull();
		return mb;
	}
}
