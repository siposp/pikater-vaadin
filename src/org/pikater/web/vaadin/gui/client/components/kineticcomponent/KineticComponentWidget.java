package org.pikater.web.vaadin.gui.client.components.kineticcomponent;

import net.edzard.kinetic.Stage;

import org.pikater.web.experiment.client.BoxInfoClient;
import org.pikater.web.experiment.client.ExperimentGraphClient;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTKeyboardManager;
import org.pikater.web.vaadin.gui.client.kineticengine.IKineticEngineContext;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.GraphItemCreator;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticUndoRedoManager;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.DeleteSelectedBoxesOperation;
import org.pikater.web.vaadin.gui.client.kineticengine.GraphItemCreator.GraphItemRegistration;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.AbstractGraphItemShared.RegistrationOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.BoxGraphItemShared;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.EdgeGraphItemShared;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FocusPanel;

/** 
 * @author SkyCrawl
 */
public class KineticComponentWidget extends FocusPanel implements KineticComponentClientRpc, KineticComponentServerRpc, IKineticEngineContext {
	private static final long serialVersionUID = 946534795907059986L;

	// ------------------------------------------------------
	// PROGRAMMATIC FIELDS

	/**
	 * Reference to the client connector communicating with the server.	
	 */
	private final KineticComponentConnector connector;

	// ------------------------------------------------------
	// EXPERIMENT RELATED FIELDS

	private KineticState state;

	// --------------------------------------------------------
	// CONSTRUCTOR

	public KineticComponentWidget(KineticComponentConnector connector) {
		super();

		/*
		 * Do the rest.
		 */

		this.connector = connector;
		this.state = null;

		// handlers to register keys being pushed down and released when the editor has focus
		addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_BACKSPACE:
					getEngine().pushToHistory(new DeleteSelectedBoxesOperation(getEngine()));
					event.preventDefault();
					event.stopPropagation();
					break;
				case 90: // Z
					if (GWTKeyboardManager.isControlKeyDown()) {
						getHistoryManager().undo();
						event.preventDefault();
						event.stopPropagation();
					}
					break;
				case 89: // Y
					if (GWTKeyboardManager.isControlKeyDown()) {
						getHistoryManager().redo();
						event.preventDefault();
						event.stopPropagation();
					}
					break;
				case 87: // W
					if (GWTKeyboardManager.isAltKeyDown()) {
						// the click mode will really be changed on the server...
						command_alterClickMode(getClickMode().getOther());
						event.preventDefault();
						event.stopPropagation();
					}
					break;
				default:
					// GWTLogger.logWarning("KeyCode down: " + event.getNativeEvent().getKeyCode());
					break;
				}
			}
		});
		addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				setFocus(true); // there is no cross-browser support for "isFocused" method so just set focus anyway :)
			}
		});

		/*
		 * MY PRIVATE PLAYTHING, NEVER FINISHED: set action modifier key ("CMD" for Mac)
		 * resource for this: http://stackoverflow.com/questions/3902635/how-does-one-capture-a-macs-command-key-via-javascript
		switch (JavascriptEntryPoint.getUnderlyingOS())
		{
			case MAC_OS: // meta key
				switch ()
				this.actionModifierKey = 224;
				break;
			default: // control key
				this.actionModifierKey = KeyCodes.KEY_CTRL;
				break;
		}
		*/
	}

	public KineticState getState() {
		return state;
	}

	public void initState(final KineticState backup) {
		if (backup != null) {
			state = backup;
			state.setParentWidget(KineticComponentWidget.this);
		} else {
			state = new KineticState(KineticComponentWidget.this);
		}
		doResize();
	}

	public void doResize() {
		// when the GWT event loop finishes and the component is fully read and its information published
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				/*
				 * Only use this to expand... if one of the new dimensions is smaller than it was, let
				 * the parent widget shrink. 
				 */

				// determine the new size
				Stage underlyingStage = (Stage) getEngine().getContainer(EngineComponent.STAGE);
				int newWidth = Math.max(getElement().getOffsetWidth(), (int) underlyingStage.getWidth());
				int newHeight = Math.max(getElement().getOffsetHeight(), (int) underlyingStage.getHeight());
				if ((newWidth > underlyingStage.getWidth()) || (newHeight > underlyingStage.getHeight())) {
					// actually resize
					getEngine().resize(newWidth, newHeight);
				}

				/*
				 * Send information about absolute position to the server so that it can compute relative
				 * mouse positions correctly.
				 */
				command_onLoadCallback(getAbsoluteLeft(), getAbsoluteTop());
			}
		});
	}

	private KineticComponentServerRpc getServerRPC() {
		return connector.serverRPC;
	}

	// *****************************************************************************************************
	// COMMANDS FROM SERVER

	/*
	 * IMPORTANT: don't call other commands from inside commands. Because we use
	 * scheduler, the actions are simply enqueued and hence any action called
	 * from within will actually be performed AFTER the current one finishes, which
	 * may already be in another "context" and "state". 
	 */

	@Override
	public void highlightBoxes(final Integer[] boxIDs) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				getEngine().highlightUntilNextRepaint(boxIDs);
			}
		});
	}

	@Override
	public void cancelBoxHighlight() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				getEngine().draw(EngineComponent.LAYER_BOXES);
			}
		});
	}

	@Override
	public void cancelSelection() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				getEngine().cancelSelection();
			}
		});
	}

	@Override
	public void resetEnvironment() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				getEngine().destroyGraphAndClearStage();
				getHistoryManager().clear();
			}
		});
	}

	@Override
	public void receiveExperimentToLoad(final ExperimentGraphClient experiment) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (experiment != null) {
					getEngine().setExperiment(experiment);
				} else {
					resetEnvironment(); // in this case we don't mind calling other command
				}
			}
		});
	}

	@Override
	public void createBox(final BoxInfoClient info) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				getGraphItemCreator().createBox(GraphItemRegistration.AUTOMATIC, info);
			}
		});
	}

	// *****************************************************************************************************
	// COMMANDS TO SERVER - SIMPLE FORWARDING

	@Override
	public void command_setExperimentModified(boolean modified) {
		getServerRPC().command_setExperimentModified(modified);
	}

	@Override
	public void command_onLoadCallback(int absoluteX, int absoluteY) {
		getServerRPC().command_onLoadCallback(absoluteX, absoluteY);
	}

	@Override
	public void command_alterClickMode(ClickMode newClickMode) {
		getServerRPC().command_alterClickMode(newClickMode);
	}

	@Override
	public void command_selectionChange(Integer[] selectedBoxesAgentIDs) {
		getServerRPC().command_selectionChange(selectedBoxesAgentIDs);
	}

	@Override
	public void command_boxSetChange(RegistrationOperation opKind, BoxGraphItemShared[] boxes) {
		getServerRPC().command_boxSetChange(opKind, boxes);
	}

	@Override
	public void command_edgeSetChange(RegistrationOperation opKind, EdgeGraphItemShared[] edges) {
		getServerRPC().command_edgeSetChange(opKind, edges);
	}

	@Override
	public void command_boxPositionsChanged(BoxGraphItemShared[] boxes) {
		getServerRPC().command_boxPositionsChanged(boxes);
	}

	// *****************************************************************************************************
	// KINETIC CONTEXT INTERFACE

	@Override
	public Element getStageDOMElement() {
		return getElement();
	}

	@Override
	public KineticEngine getEngine() {
		return state.getEngine();
	}

	@Override
	public KineticUndoRedoManager getHistoryManager() {
		return state.getHistoryManager();
	}

	@Override
	public GraphItemCreator getGraphItemCreator() {
		return state.getGraphItemCreator();
	}

	@Override
	public ClickMode getClickMode() {
		return connector.getState().clickMode;
	}
}