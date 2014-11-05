package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager;

/**
 * Types of views/subviews defined in {@link BoxManagerToolbox}. Each
 * is required to display the box name and type.
 *  
 * @author SkyCrawl
 */
public enum BoxManagerView {
	/**
	 * Displays box options, slots and whether they are connected.
	 * Provides a button to switch to "editing mode".
	 */
	OVERVIEW,

	/**
	 * Subview of overview, built especially for editing options.
	 */
	OPTIONVIEW,

	/**
	 * Subview of overview, built especially for editing slots.
	 */
	SLOTVIEW
}