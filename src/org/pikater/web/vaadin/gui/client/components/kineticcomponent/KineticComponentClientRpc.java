package org.pikater.web.vaadin.gui.client.components.kineticcomponent;

import org.pikater.web.experiment.client.BoxInfoClient;
import org.pikater.web.experiment.client.ExperimentGraphClient;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.IKineticComponent;

import com.vaadin.shared.communication.ClientRpc;

/** 
 * Defines server to client commands for {@link KineticComponent}.
 * 
 * @author SkyCrawl
 */
public interface KineticComponentClientRpc extends ClientRpc, IKineticComponent {
	void receiveExperimentToLoad(ExperimentGraphClient experiment);

	void createBox(BoxInfoClient info);
}