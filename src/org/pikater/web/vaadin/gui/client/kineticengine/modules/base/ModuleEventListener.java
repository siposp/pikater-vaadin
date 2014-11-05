package org.pikater.web.vaadin.gui.client.kineticengine.modules.base;

import net.edzard.kinetic.event.IEventListener;
import net.edzard.kinetic.event.KineticEvent;

/**
 * <p>CONTEXT OF THE PROBLEM:</br>
 * Modules may attach event handlers to graph items. If more of them
 * attach a handler for the same event (say click event), each module
 * will process that event, in the same order their respective handlers
 * were attached. This may be a very undesirable situation.</p>
 * 
 * <p>SOLUTION OF THE PROBLEM:</br>
 * This special event wrapper of course. It encapsulates the native
 * event raised and prevents it from being handled further, once it
 * is set as "processed" ({@link KineticEvent#setProcessed()} or
 * {@link KineticEvent#setProcessed(String)}.</p> 
 * 
 * @author SkyCrawl
 */
public abstract class ModuleEventListener implements IEventListener {
	@Override
	public void handle(KineticEvent event) {
		if (!event.isProcessed()) {
			handleInner(event);
			/*
			if(JSNI_SharedConfig.isDebugModeActivated())
			{
				if(event.isProcessed()) {
					event.setProcessed(GWTMisc.getSimpleName(listener.getClass()));
				}
				else {
					// TODO
				}
			}
			*/
		}
	}

	/**
	 * This method is only called if the event has not been previously
	 * set as "processed". For more information, view Javadoc of 
	 * {@link ModuleEventListener}.
	 */
	protected abstract void handleInner(KineticEvent event);
}
