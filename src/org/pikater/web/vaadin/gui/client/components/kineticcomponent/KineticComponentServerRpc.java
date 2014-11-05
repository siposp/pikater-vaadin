package org.pikater.web.vaadin.gui.client.components.kineticcomponent;

import org.pikater.web.vaadin.gui.client.kineticengine.KineticUndoRedoManager;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.AbstractGraphItemShared.RegistrationOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.BoxGraphItemShared;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.EdgeGraphItemShared;

import com.vaadin.shared.communication.ServerRpc;

/** 
 * Defines client to server commands for {@link KineticComponent}.
 * 
 * @author SkyCrawl
 */
public interface KineticComponentServerRpc extends ServerRpc {
	/*
	 * General commands.
	 */

	/**
	 * Runtime absolute position of this component's master HTML element.
	 * Useful when trying to determine where exactly into the client
	 * kinetic canvas we should paint the dragged & dropped boxes.
	 */
	void command_onLoadCallback(int absoluteX, int absoluteY);

	/**
	 * @deprecated Currently unsupported. 
	 * @see {@link KineticUndoRedoManager}
	 */
	@Deprecated
	void command_setExperimentModified(boolean modified);

	/**
	 * Command to programmatically change {@link ClickMode click mode}
	 * of the current {@link KineticComponent}.
	 */
	void command_alterClickMode(ClickMode newClickMode);

	/*
	 * Experiment graph related commands. Quite self explanatory.
	 */

	void command_boxSetChange(RegistrationOperation opKind, BoxGraphItemShared[] boxes);

	void command_edgeSetChange(RegistrationOperation opKind, EdgeGraphItemShared[] edges);

	void command_boxPositionsChanged(BoxGraphItemShared[] boxes);

	void command_selectionChange(Integer[] selectedBoxIDs);
}
