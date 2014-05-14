package org.pikater.web.vaadin.gui.client;

import org.pikater.web.vaadin.gui.client.managers.GWTKeyboardManager;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Event;

public class AppEntryPoint implements EntryPoint
{
	@Override
	public void onModuleLoad()
	{
		/*
		 * NOTES:
		 * - Don't use the "GWT.setUncaughtExceptionHandler(handler);" with the GWT logger. These uncaught exceptions
		 * are native and contain useless (javascript related) information for a Java/GWT programmer.  
		 */
		
		/*
		 * Creates a special JS namespace, injects it into the window object and
		 * exports some JSNI functions that can be called from the server.
		 */
		// JSNI_SharedConfig.exportStaticMethods();

		/*
		 * Adds a keyboard listener that keeps track of what keys are currently
		 * pressed. The underlying code is called after an event is triggered
		 * and even before the browser processes it which allows for cancelling
		 * events.
		 */
		Event.addNativePreviewHandler(GWTKeyboardManager.getNativePreviewHandler());
	}
}