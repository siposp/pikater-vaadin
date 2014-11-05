package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager;

import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;

/**
 * Context interface required to be implemented {@link BoxManagerToolbox}
 * box manager toolbox's parent components - at this moment only
 * {@link KineticComponent}.
 * 
 * @author SkyCrawl
 */
public interface IBoxManagerToolboxContext {
	KineticComponent getCurrentComponent();
}