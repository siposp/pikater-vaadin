package org.pikater.web.vaadin.gui.server.components.popups.dialogs;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;

import org.pikater.web.PikaterWebLogger;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;
import de.steinwedel.messagebox.ResourceFactory;

/**
 * Defines specific dialogs used to track progress of something.
 * 
 * @author SkyCrawl
 */
public class ProgressDialog extends DialogCommons {
	/**
	 * The main method of this class, showing the given progress dialog.
	 */
	public static ProgressDialogContext show(String title,
			final IProgressDialogTaskHandler progressDialogEvents) {
		ProgressBar progress = new ProgressBar(0);
		progress.setSizeFull();

		Label lbl_percentage = new Label() {
			private static final long serialVersionUID = 4092396095417883900L;

			public void setValue(String newStringValue) {
				super.setValue(newStringValue + " %");
			}
		};
		lbl_percentage.setValue("0");
		lbl_percentage.setSizeUndefined();

		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSizeFull();
		hLayout.setSpacing(true);
		hLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		hLayout.addComponent(lbl_percentage);
		hLayout.addComponent(progress);
		hLayout.setExpandRatio(progress, 1);

		MyMessageBoxListener listener = new MyMessageBoxListener(null) {
			/*
			 * IMPORTANT: the 'null' argument can easily cause errors if
			 * modifications are made to the enclosing class.
			 */

			@Override
			protected boolean allowOKHandle() {
				return true;
			}

			@Override
			protected void addArgs(List<Object> arguments) {
			}

			@Override
			protected void handleClose() {
				try {
					progressDialogEvents.abortTask();
				} catch (Exception t) {
					PikaterWebLogger.logThrowable(
							"Could not abort underlying task.", t);
				} finally {
					super.handleClose(); // automatically and always close the
											// dialog
				}
			}
		};
		MessageBox mb = MessageBox.showCustomized(Icon.NONE,
				title == null ? "Progress" : title, hLayout, listener,
				ButtonId.ABORT);
		listener.setParentBox(mb); // don't forget this!
		mb.getWindow().setWidth("250px");
		mb.getWindow().setStyleName("progressDialog"); // stretch the component
														// area to maximum
														// width...
		setupGeneralDialog(mb, false);
		return new ProgressDialogContext(mb, progress, lbl_percentage,
				progressDialogEvents);
	}

	// -------------------------------------------------------------------------
	// PUBLIC TYPES - PROGRESS BAR DIALOG

	/**
	 * Classes implementing this object are supposed to manage the underlying
	 * tracked task.
	 * 
	 * @author SkyCrawl
	 */
	public static interface IProgressDialogTaskHandler {
		/**
		 * Signal used to start the underlying task.
		 * <ul>
		 * <li>Dialog is never created if an exception is thrown so you don't
		 * have to close it manually in such cases.
		 * </ul>
		 * 
		 * @param contextForTask
		 *            provides progress and status callbacks
		 * @throws Throwable
		 */
		void startTask(IProgressDialogResultHandler contextForTask)
				throws Exception;

		/**
		 * Signal used to abort the underlying task.
		 * <ul>
		 * <li>Dialog is closed automatically.
		 * <li>Feel free to throw exceptions.
		 * </ul>
		 */
		void abortTask();

		/**
		 * Called when the underlying task is finished.
		 * <ul>
		 * <li>You only need to use the task's result to generate new GUI -
		 * everything else is taken care of for you.
		 * <li>Dialog is closed automatically.
		 * <li>Feel free to throw exceptions.
		 * </ul>
		 * <font color="red">This method is only called FROM THE TASK and as
		 * such, notifications will not work in it.</font>
		 * 
		 * @param result
		 *            can be any custom-defined class implementing the interface
		 */
		void onTaskFinish(IProgressDialogTaskResult result);
	}

	/**
	 * Classes implementing this object are supposed to handle the underlying
	 * tracked task's progress, whether successful or not.
	 * 
	 * @author SkyCrawl
	 */
	public static interface IProgressDialogResultHandler {
		/**
		 * @param percentage
		 *            value must be between 0.0 and 1.0
		 */
		void updateProgress(float percentage);

		/**
		 * Call this method when the underlying task fails to notify the dialog.
		 */
		void failed();

		/**
		 * Call this method when the underlying task finishes to notify the
		 * dialog.
		 */
		void finished(IProgressDialogTaskResult result);
	}

	/**
	 * A base interface for objects that hold the underlying task's results.
	 * 
	 * @author SkyCrawl
	 */
	public static interface IProgressDialogTaskResult {
	}

	/**
	 * The object connecting specific dialog handlers with the GUI and making
	 * all of this to run.
	 * 
	 * @author SkyCrawl
	 */
	public static class ProgressDialogContext implements
			IProgressDialogResultHandler {
		private static final int POLL_INTERVAL = 500;

		/**
		 * Reference to the current UI. Needs to be kept so that polling can be
		 * easily disabled as a call from the underlying task.
		 */
		private final UI currentUI;

		/*
		 * Components to update.
		 */
		private final MessageBox box;
		private final ProgressBar progress;
		private final Label lbl_percentage;

		/**
		 * Case-dependent events to call.
		 */
		private final IProgressDialogTaskHandler taskHandler;

		public ProgressDialogContext(MessageBox box, ProgressBar progress,
				Label lbl_percentage, IProgressDialogTaskHandler taskHandler) {
			this.currentUI = UI.getCurrent();
			this.box = box;
			this.progress = progress;
			this.lbl_percentage = lbl_percentage;
			this.taskHandler = taskHandler;

			if (this.currentUI.getPollInterval() == -1) // polling is not set
														// yet
			{
				/*
				 * Start polling the UI for changes (from client). The task,
				 * when started, is going to be updating the GUI but since it is
				 * a thread completely separate from Vaadin, the changes will
				 * not be automatically pushed to the client.
				 */
				this.currentUI.setPollInterval(POLL_INTERVAL);

				// this is not currently implemented (in Vaadin version 7.1.4):
				// this.currentUI.addPollListener();

				/*
				 * TODO: when the project is updated to use a never Vaadin
				 * version (2.0+), definitely implement a poll listener to
				 * display notifications about the task's status, namely the
				 * FAILED status.
				 */
			} else {
				throw new IllegalStateException("Polling was already enabled.");
			}

			/*
			 * Start the underlying task.
			 */
			try {
				taskHandler.startTask(this);
			} catch (Exception e) {
				/*
				 * We need the dialog to not be created (so as not to close it
				 * just afterwards). As such, silence the IDE using a
				 * RuntimeException, which will be caught later on and a
				 * notification will be displayed.
				 */
				throw new RuntimeException(e);
			}
		}

		@Override
		public void updateProgress(final float percentage) {
			executeInLock(new Runnable() {
				@Override
				public void run() {
					// updates component state on the server side - will be
					// propagated to client when a poll request arrives
					progress.setValue(percentage);
					lbl_percentage.setValue(String.valueOf(Math
							.round(percentage * 100)));
				}
			});
		}

		@Override
		public void finished(final IProgressDialogTaskResult result) {
			executeInLock(new Runnable() {
				@Override
				public void run() {
					box.getButton(ButtonId.ABORT).setCaption(
							"Finished - click to open result");
					box.getButton(ButtonId.ABORT).setIcon(
							new ResourceFactory().getIcon(ButtonId.YES));
					box.getButton(ButtonId.ABORT).addClickListener(
							new Button.ClickListener() {
								private static final long serialVersionUID = 5823278457271773907L;

								@Override
								public void buttonClick(ClickEvent event) {
									try {
										taskHandler.onTaskFinish(result); // simply
																			// forward
									} catch (Exception t) {
										PikaterWebLogger
												.logThrowable(
														"Something went wrong in progress dialog's onFinish event.",
														t);
										MyNotifications.showApplicationError();
									} finally {
										cleanup();
									}
								}
							});
				}
			});
		}

		@Override
		public void failed() {
			executeInLock(new Runnable() {
				@Override
				public void run() {
					box.getButton(ButtonId.ABORT).setCaption(
							"Failed - click to close");
					box.getButton(ButtonId.ABORT).addClickListener(
							new Button.ClickListener() {
								private static final long serialVersionUID = 5823278457271773907L;

								@Override
								public void buttonClick(ClickEvent event) {
									cleanup();
								}
							});
				}
			});
		}

		/**
		 * Vaadin doesn't like when components are updated while response with
		 * previous updates is being written. Use this method to update
		 * components from background threads to avoid accidental concurrent
		 * access issues.
		 */
		private void executeInLock(Runnable command) {
			Lock lock = currentUI.getSession().getLockInstance();
			try {
				lock.lock();

				command.run();
			} finally {
				lock.unlock();
			}
		}

		private void cleanup() {
			try {
				// let the UI have a chance to poll for the changes made by task
				// finish or task fail event
				Thread.sleep(POLL_INTERVAL * 2);
			} catch (InterruptedException e) {
				/*
				 * This is not really serious as any client to server request
				 * will download the GUI changes made, but still the user may
				 * have to trigger such request manually and it might now be
				 * always possible.
				 */
				PikaterWebLogger
						.log(Level.WARNING,
								"UI polling will be disabled a bit earlier than expected - thread was interrupted.");
			} finally {
				// and then disable polling, whatever happens
				this.currentUI.setPollInterval(-1);
				this.box.close();
			}
		}
	}
}
