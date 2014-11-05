package org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo;

import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient.EndPoint;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.ItemRegistrationModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.SelectionModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.SelectionModule.SelectionOperation;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.base.BiDiOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.AbstractGraphItemShared.RegistrationOperation;

/**
 * Undoable operation that deletes edges from the graph
 * AND from the environment/canvas.
 * 
 * @author SkyCrawl
 */
public class DeleteEdgeOperation extends BiDiOperation {
	private final ItemRegistrationModule itemRegistrationModule;
	private final SelectionModule selectionModule;
	private final EdgeGraphItemClient edgeToRemove;
	private final boolean edgeSelectedOriginally;

	public DeleteEdgeOperation(KineticEngine kineticEngine, EdgeGraphItemClient edgeToRemove) {
		super(kineticEngine);

		this.itemRegistrationModule = (ItemRegistrationModule) kineticEngine.getModule(ItemRegistrationModule.moduleID);
		this.selectionModule = (SelectionModule) kineticEngine.getModule(SelectionModule.moduleID);
		this.edgeToRemove = edgeToRemove;
		this.edgeSelectedOriginally = edgeToRemove.isSelected();
	}

	@Override
	public void undo() {
		itemRegistrationModule.doOperation(RegistrationOperation.REGISTER, !edgeSelectedOriginally, true, edgeToRemove);
		if (edgeSelectedOriginally) {
			// select both endpoints (and thus the edge also)
			selectionModule.doSelectionRelatedOperation(SelectionOperation.SELECTION, true, true, edgeToRemove.getEndPoint(EndPoint.FROM), edgeToRemove.getEndPoint(EndPoint.TO));
		}
	}

	@Override
	public void redo() {
		if (edgeSelectedOriginally) {
			// deselect both endpoints first (and thus the edge also)
			selectionModule.doSelectionRelatedOperation(SelectionOperation.DESELECTION, false, true, edgeToRemove.getEndPoint(EndPoint.FROM), edgeToRemove.getEndPoint(EndPoint.TO));
		}
		itemRegistrationModule.doOperation(RegistrationOperation.UNREGISTER, true, true, edgeToRemove);
	}
}