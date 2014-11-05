package org.pikater.web.vaadin.gui.client.kineticengine;

import java.util.Stack;

import org.pikater.web.vaadin.gui.client.components.kineticcomponent.KineticComponentWidget;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.base.BiDiOperation;

/**
 * History manager for our kinetic canvas and experiment editor. Allows
 * undoing and redoing individual changes at will.
 * 
 * @author SkyCrawl
 */
public class KineticUndoRedoManager {
	private final KineticComponentWidget widget;
	private final Stack<BiDiOperation> undoStack;
	private final Stack<BiDiOperation> redoStack;

	public KineticUndoRedoManager(KineticComponentWidget widget) {
		this.widget = widget;
		this.undoStack = new Stack<BiDiOperation>();
		this.redoStack = new Stack<BiDiOperation>();
	}

	/**
	 * Load history from a backup.
	 */
	public void loadHistory(KineticUndoRedoManager history) {
		if (history != null) {
			if (!undoStack.isEmpty() || !redoStack.isEmpty()) {
				throw new IllegalStateException("Can not load history if there is already an alternative newer version.");
			} else {
				this.undoStack.addAll(history.undoStack);
				this.redoStack.addAll(history.redoStack);

				// notifyExperimentModifiedIf(!undoStack.isEmpty());
			}
		}
	}

	/**
	 * Delete history up until this moment.
	 */
	public void clear() {
		undoStack.clear();
		redoStack.clear();

		// alterStateIf(undoStack.isEmpty());
	}

	/**
	 * Register a new change and put it on top of the change history.
	 */
	public void push(BiDiOperation operation) {
		// alterStateIf(undoStack.isEmpty());

		undoStack.push(operation);
		redoStack.clear();
	}

	/**
	 * Undo the latest change.
	 */
	public void undo() {
		if (!undoStack.isEmpty()) {
			BiDiOperation op = undoStack.pop();
			redoStack.push(op);
			op.undo();

			// alterStateIf(undoStack.isEmpty());
		}
	}

	/**
	 * Comes in handy for incomplete operation that were cancelled 
	 * in the process.
	 */
	public void undoAndDiscard() {
		if (!undoStack.isEmpty()) {
			undoStack.pop().undo();

			// alterStateIf(undoStack.isEmpty());
		}
	}

	/**
	 * Redo the latest change undid with {@link #undo()}.
	 */
	public void redo() {
		// alterStateIf(undoStack.isEmpty());

		if (!redoStack.isEmpty()) {
			BiDiOperation op = redoStack.pop();
			undoStack.push(op);
			op.redo();
		}
	}

	//---------------------------------------------------------------
	// INTERFACE FOR SHARING THE "MODIFIED" STATUS WITH THE SERVER

	/*
	 * TODO: editing options makes this incomplete... we have to merge all changes
	 * to be registered either on the server or the client.
	 */

	@SuppressWarnings("unused")
	private void notifyExperimentModifiedIf(boolean condition) {
		if (condition) {
			widget.command_setExperimentModified(true);
		}
	}

	@SuppressWarnings("unused")
	private void notifyExperimentNotModifiedIf(boolean condition) {
		if (condition) {
			widget.command_setExperimentModified(false);
		}
	}
}
