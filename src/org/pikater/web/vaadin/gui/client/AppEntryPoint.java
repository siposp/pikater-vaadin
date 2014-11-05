package org.pikater.web.vaadin.gui.client;

import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTKeyboardManager;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTLogger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Event;

/**
 * Basic client setup, called once when the client engine
 * initialized the client application in browser.
 * 
 * @author SkyCrawl
 */
public class AppEntryPoint implements EntryPoint {
	@Override
	public void onModuleLoad() {
		/*
		 * Adds a keyboard listener that keeps track of what keys are currently ressed.
		 */
		Event.addNativePreviewHandler(GWTKeyboardManager.getNativePreviewHandler());

		/*
		 * Send uncaught exceptions to the server:  
		 */
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				GWTLogger.logThrowable("Uncaught exception from client:", e);

				/*
				if((e instanceof UmbrellaException) || (e instanceof JavaScriptException)) {
					// native exceptions usually contain no useful information => send a generic "warning" instead:
					GWTLogger.logUncaughtNativeClientException();
				}
				else {
					GWTLogger.logThrowable("Uncaught exception from client:", e);
				}
				*/
			}
		});
	}
}