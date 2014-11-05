package org.pikater.web.vaadin.gui.server.components.popups;

import org.pikater.web.sharedresources.ResourceRegistrar;
import org.pikater.web.sharedresources.ThemeResources;
import org.pikater.web.vaadin.CustomConfiguredUI;
import org.vaadin.alump.fancylayouts.FancyNotification;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.UI;

/**
 * <p>This class define routines for displaying various kinds of notifications
 * in OS X style.</br>
 * Note that this class can not be used outside of Vaadin requests (threads
 * processing them). The methods defined here work on a so called thread-local
 * pattern.</p>
 * 
 * <p>TODO: is there a decent way to improve this? To get an instance of underlying
 * UI based on something other than currently running thread? Maybe
 * {@link ResourceRegistrar}?</p>
 * 
 * @author SkyCrawl
 */
public class MyNotifications {
	//----------------------------------------------------------------
	// GENERAL USE NOTIFICATIONS

	public static void showSuccess(String title, String description) {
		getCurrentNotificationsManager().showNotification(null, title, description, ThemeResources.img_notificationSuccessIcon);
	}

	public static void showInfo(String title, String description) {
		getCurrentNotificationsManager().showNotification(null, title, description, ThemeResources.img_notificationInfoIcon);
	}

	public static void showWarning(String title, String description) {
		getCurrentNotificationsManager().showNotification(null, title, description, ThemeResources.img_notificationWarnIcon);
	}

	public static void showError(String title, String description) {
		getCurrentNotificationsManager().showNotification(null, title, description, ThemeResources.img_notificationErrorIcon);
	}

	//----------------------------------------------------------------
	// SPECIFIC USE NOTIFICATIONS

	public static void showApplicationError() {
		showApplicationError("Please contact the administrators.");
	}

	public static void showApplicationError(String message) {
		getCurrentNotificationsManager().showNotification(null, "Application error", message, ThemeResources.img_notificationErrorIcon);
	}

	//----------------------------------------------------------------
	// PRIVATE INTERFACE

	private static MyFancyNotifications getCurrentNotificationsManager() {
		/*
		 * This is one of the reasons why notifications need to operate
		 * on a thread-local pattern - they require to have a special
		 * component attached to a UI. 
		 */
		return ((CustomConfiguredUI) UI.getCurrent()).getNotificationsComponent();
	}

	@SuppressWarnings("unused")
	private static void showHTMLNotification(String title, String message, String styleName) {
		FancyNotification notif = new FancyNotification(null, "Hello <span style=\"text-decoration: underline;\">World</span>", "Lorem <span style=\"font-size: 80%;\">ipsum.</span>");
		notif.getTitleLabel().setContentMode(ContentMode.HTML);
		notif.getDescriptionLabel().setContentMode(ContentMode.HTML);
		getCurrentNotificationsManager().showNotification(notif);
	}
}