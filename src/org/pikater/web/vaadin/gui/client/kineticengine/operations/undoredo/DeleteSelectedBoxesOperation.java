package org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo;

import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.ItemRegistrationModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.SelectionModule;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.SelectionModule.SelectionOperation;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.base.BiDiOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.AbstractGraphItemShared.RegistrationOperation;

/**
 * Undoable operation that deletes all selected boxes and related
 * edges.
 * 
 * @author SkyCrawl
 */
public class DeleteSelectedBoxesOperation extends BiDiOperation {
	private final SelectionModule selectionModule;
	private final ItemRegistrationModule itemRegistrationModule;
	private final BoxGraphItemClient[] originalSelectedBoxes;
	private final EdgeGraphItemClient[] allRelatedEdges; // selected and "in-between"

	public DeleteSelectedBoxesOperation(KineticEngine kineticEngine) {
		super(kineticEngine);

		this.selectionModule = (SelectionModule) kineticEngine.getModule(SelectionModule.moduleID);
		this.itemRegistrationModule = (ItemRegistrationModule) kineticEngine.getModule(ItemRegistrationModule.moduleID);
		this.originalSelectedBoxes = this.selectionModule.getSelectedBoxes();
		this.allRelatedEdges = this.selectionModule.getAllRelatedEdges();
	}

	@Override
	public void undo() {
		itemRegistrationModule.doOperation(RegistrationOperation.REGISTER, false, true, originalSelectedBoxes); // first add boxes
		itemRegistrationModule.doOperation(RegistrationOperation.REGISTER, false, true, allRelatedEdges); // then add edges
		selectionModule.doSelectionRelatedOperation(SelectionOperation.SELECTION, true, true, originalSelectedBoxes); // and finally, select everything
	}

	@Override
	public void redo() {
		selectionModule.doSelectionRelatedOperation(SelectionOperation.DESELECTION, false, true, originalSelectedBoxes); // first deselect everything
		itemRegistrationModule.doOperation(RegistrationOperation.UNREGISTER, false, true, allRelatedEdges); // then remove edges
		itemRegistrationModule.doOperation(RegistrationOperation.UNREGISTER, true, true, originalSelectedBoxes); // and finally, remove boxes
	}
}