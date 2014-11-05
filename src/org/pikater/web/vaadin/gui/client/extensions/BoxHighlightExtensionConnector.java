package org.pikater.web.vaadin.gui.client.extensions;

import org.pikater.web.vaadin.gui.client.components.kineticcomponent.KineticComponentConnector;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient.VisualStyle;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.BoxHighlightExtension;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.visualstyle.KineticBoxSettings;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

/**  
 * @author SkyCrawl
 * @see {@link BoxHighlightExtension}
 */
@Connect(BoxHighlightExtension.class)
public class BoxHighlightExtensionConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = -699537664178182240L;

	@Override
	protected void extend(ServerConnector target) {
		/*
		 * Extends the given component/connector with a mouseover/mouseout
		 * handler that turns on/off highlighting of the given boxes of
		 * the given instance of client kinetic canvas.
		 * 
		 * This is all required not to be implemented on the server because
		 * of performance penalties and impacts. As such, this extension
		 * makes a VERY nice workaround.
		 */

		final Widget extendedWidget = ((ComponentConnector) target).getWidget();
		extendedWidget.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				extendedWidget.getElement().getStyle().setBackgroundColor(KineticBoxSettings.getColor(VisualStyle.HIGHLIGHTED_SLOT).toString());
				getKineticConnectorByID(getState().kineticConnectorID).getWidget().highlightBoxes(getState().boxIDs);
			}
		}, MouseOverEvent.getType());
		extendedWidget.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				extendedWidget.getElement().getStyle().clearBackgroundColor();
				getKineticConnectorByID(getState().kineticConnectorID).getWidget().cancelBoxHighlight();
			}
		}, MouseOutEvent.getType());
	}

	@Override
	public BoxHighlightExtensionSharedState getState() {
		return (BoxHighlightExtensionSharedState) super.getState();
	}

	private KineticComponentConnector getKineticConnectorByID(String id) {
		return (KineticComponentConnector) getConnection().getConnector(id, 0);
	}
}