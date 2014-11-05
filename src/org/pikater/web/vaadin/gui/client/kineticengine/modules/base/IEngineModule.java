package org.pikater.web.vaadin.gui.client.kineticengine.modules.base;

import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient;

/**
 * Common interface for engine modules. Each module handles and fully
 * implements a particular more complex functionality related to
 * experiment editor's canvas.
 * 
 * @author SkyCrawl
 */
public interface IEngineModule {
	/**
	 * Return a unique ID specific to this module. 
	 */
	public String getModuleID();

	/**
	 * Should this module require others for its operation, this is
	 * the time to establish references to them. Use
	 * {@link KineticEngine#getModule(String)}.  
	 */
	public void createModuleCrossReferences();

	/**
	 * <p>Return an array of class names of graph items that this
	 * module uses for its operation (attaches event handlers to).</p>
	 * 
	 * <p>Example: <pre>return new String[] { GWTMisc.getSimpleName(BoxGraphItemClient.class) };</pre>
	 * 
	 */
	public String[] getGraphItemTypesToAttachHandlersTo();

	/**
	 * Attach your required event handlers to the given graph item,
	 * if needed. Consult {@link #getGraphItemTypesToAttachHandlersTo()}.
	 */
	public void attachHandlers(AbstractGraphItemClient<?> graphItem);
}
