package org.pikater.web.vaadin.gui.client.extensions;

import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;

import com.vaadin.shared.communication.SharedState;

/** 
 * @author SkyCrawl 
 */
public class ExpEditorExtensionSharedState extends SharedState {
	private static final long serialVersionUID = -7743696126483421775L;

	/**
	 * @deprecated Related feature is not supported at the moment.
	 * 
	 * <p>Updated everytime the count of modified experiments changes. Useful to
	 * handle close events and let the user confirm leaving unsaved content behind.</p>
	 */
	@Deprecated
	public int modifiedTabsCount;

	/**
	 * <p>Since {@link ExpEditor} is a tabbed component, we need to remember
	 * what tab is currently selected for some custom actions to work.</p>
	 * 
	 * <p>To be more precise, this is needed for client resize events and
	 * events that could affect the size of the currently selected
	 * {@link KineticComponent}.</p>
	 */
	public String currentlySelectedKineticConnectorID = null;
}