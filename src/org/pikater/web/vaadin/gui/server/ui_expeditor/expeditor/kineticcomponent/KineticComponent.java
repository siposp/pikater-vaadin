package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent;

import org.pikater.core.ontology.subtrees.agentinfo.AgentInfo;
import org.pikater.shared.database.jpa.JPABatch;
import org.pikater.shared.experiment.UniversalComputationDescription;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.experiment.server.BoxInfoServer;
import org.pikater.web.experiment.server.ExperimentGraphServer;
import org.pikater.web.experiment.server.ExperimentGraphValidator;
import org.pikater.web.vaadin.gui.client.components.kineticcomponent.KineticComponentClientRpc;
import org.pikater.web.vaadin.gui.client.components.kineticcomponent.KineticComponentServerRpc;
import org.pikater.web.vaadin.gui.client.components.kineticcomponent.KineticComponentState;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.CustomTabSheetTabComponent;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor.ExpEditorToolbox;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.BoxManagerToolbox;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.customtabsheet.TabSheet;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.IKineticComponent;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.AbstractGraphItemShared.RegistrationOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.BoxGraphItemShared;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.EdgeGraphItemShared;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractComponent;

/**
 * Content component for {@link ExpEditor experiment editor's} {@link TabSheet tab
 * sheet} tabs. Contains kinetic canvas (in the client implementation) used to
 * draw experiments. Also manages experiment data structure.
 * 
 * @author SkyCrawl
 */
@JavaScript(value = "kinetic-v4.7.3-dev.js")
public class KineticComponent extends AbstractComponent implements IKineticComponent {
	private static final long serialVersionUID = -539901377528727478L;

	//---------------------------------------------------------------
	// GUI COMPONENTS TO KEEP TRACK OF

	/**
	 * Constant reference to the parent editor component.
	 */
	private final ExpEditor parentEditor;

	/**
	 * Reference to the tab containing this component.
	 */
	private CustomTabSheetTabComponent parentTab;

	//---------------------------------------------------------------
	// EXPERIMENT RELATED FIELDS

	/**
	 * The dynamic mapping of box IDs to box information providers. This field is
	 * the base for all format conversions and some other commands.
	 */
	private ExperimentGraphServer experimentGraph;

	/**
	 * Reference to experiment last used in the {@link #importExperiment(UniversalComputationDescription)}
	 * method.
	 */
	private Integer previouslyLoadedExperimentID;

	//---------------------------------------------------------------
	// OTHER PROGRAMMATIC FIELDS

	/*
	 * Dynamic information from the client side - absolute left corner position of the Kinetic stage.
	 */
	private int absoluteLeft;
	private int absoluteTop;

	//---------------------------------------------------------------
	// CONSTRUCTOR

	/**
	 * Constructor.</br>
	 * Remember to also use {@link #setParentTab(CustomTabSheetTabComponent)}
	 * for each instance of this component.
	 */
	public KineticComponent(final ExpEditor parentEditor) {
		super();
		setSizeFull();
		setPrimaryStyleName("kineticComponent");

		/*
		 * Init.
		 */

		this.parentEditor = parentEditor;
		this.parentTab = null;

		this.experimentGraph = new ExperimentGraphServer();
		this.previouslyLoadedExperimentID = null;

		this.absoluteLeft = 0;
		this.absoluteTop = 0;

		/*
		 * Register handlers for client commands.
		 */

		registerRpc(new KineticComponentServerRpc() {
			private static final long serialVersionUID = -2769231541745495584L;

			/**
			 * Currently unsupported.
			 */
			@Deprecated
			@Override
			public void command_setExperimentModified(boolean modified) {
				/*
				getState().serverThinksThatSchemaIsModified = modified;
				parentTab.setTabContentModified(modified);
				parentEditor.getExtension().setKineticContentModified(KineticComponent.this, modified);
				
				MyNotifications.showInfo("Modification note", String.valueOf(modified));
				*/
			}

			@Override
			public void command_onLoadCallback(int absoluteX, int absoluteY) {
				KineticComponent.this.absoluteLeft = absoluteX;
				KineticComponent.this.absoluteTop = absoluteY;
			}

			@Override
			public void command_alterClickMode(ClickMode newClickMode) {
				getState().clickMode = newClickMode;
				KineticComponent.this.parentEditor.getToolbar().onClickModeAlteredOnClient(newClickMode);
			}

			/**
			 * This is assumed to only be invoked when the user creates a box (no edges are thus
			 * yet attached).
			 */
			@Override
			public void command_boxSetChange(RegistrationOperation opKind, BoxGraphItemShared[] boxes) {
				// get the toolbox
				final BoxManagerToolbox boxManager = (BoxManagerToolbox) parentEditor.getToolbox(ExpEditorToolbox.BOX_MANAGER);

				// register/unregister the boxes on the server, update box manager if need be
				for (BoxGraphItemShared boxShared : boxes) {
					BoxInfoServer boxServer = experimentGraph.getBox(boxShared.getID());
					boxServer.setRegistered(opKind == RegistrationOperation.REGISTER);
					if (!getState().boxManagerBoundWithSelection && (opKind == RegistrationOperation.UNREGISTER) && (boxServer.getID().equals(boxManager.getCurrentBoxDataSource().getID()))) {
						boxManager.setContentFromSelectedBoxes(new BoxInfoServer[0]);
					}
				}
			}

			/**
			 * This is assumed to only be invoked when the user creates an edge. If it is unregistered,
			 * boxes should not be affected because {@link #command_boxSetChange()}.
			 */
			@Override
			public void command_edgeSetChange(RegistrationOperation opKind, EdgeGraphItemShared[] edges) {
				// get the toolbox's current data source
				final BoxManagerToolbox boxManager = (BoxManagerToolbox) parentEditor.getToolbox(ExpEditorToolbox.BOX_MANAGER);

				boolean boxManagerNeedsToUpdate = false;
				for (EdgeGraphItemShared edge : edges) {
					interboxConnectionAction(opKind, edge);

					if ((boxManager.getCurrentBoxDataSource() != null) && 
							((edge.fromBoxID.equals(boxManager.getCurrentBoxDataSource().getID())) || (edge.toBoxID.equals(boxManager.getCurrentBoxDataSource().getID())))) {
							boxManagerNeedsToUpdate = true;
					}
				}
				if (boxManagerNeedsToUpdate) {
					boxManager.getCurrentView().refreshContent();
				}
			}

			@Override
			public void command_selectionChange(Integer[] selectedBoxesIDs) {
				if (getState().boxManagerBoundWithSelection) {
					// convert to agent information array
					BoxInfoServer[] selectedBoxesInformation = new BoxInfoServer[selectedBoxesIDs.length];
					for (int i = 0; i < selectedBoxesIDs.length; i++) {
						if (experimentGraph.containsBox(selectedBoxesIDs[i])) {
							selectedBoxesInformation[i] = experimentGraph.getBox(selectedBoxesIDs[i]);
						} else {
							throw new IllegalStateException(String.format("Kinetic state out of sync. " + "No agent info was found for box ID '%d'.", selectedBoxesIDs[i]));
						}
					}

					// get the toolbox
					BoxManagerToolbox toolbox = (BoxManagerToolbox) parentEditor.getToolbox(ExpEditorToolbox.BOX_MANAGER);

					// set the new content to it and display the toolbox if needed
					if (toolbox.setContentFromSelectedBoxes(selectedBoxesInformation)) {
						parentEditor.openToolbox(ExpEditorToolbox.BOX_MANAGER);
					}
				}
			}

			@Override
			public void command_boxPositionsChanged(BoxGraphItemShared[] boxes) {
				for (BoxGraphItemShared box_shared : boxes) {
					BoxInfoServer box_server = experimentGraph.getBox(box_shared.getID());
					box_server.setPosX(box_shared.getPosX());
					box_server.setPosY(box_shared.getPosY());
				}
			}

			//-------------------------------------------------------------
			// SOME CONVENIENCE PRIVATE INTERFACE

			/**
			 * Does all kinds of checks and throws exceptions.
			 */
			private void interboxConnectionAction(RegistrationOperation opKind, EdgeGraphItemShared edge) {
				if (opKind == RegistrationOperation.REGISTER) {
					experimentGraph.connect(edge.fromBoxID, edge.toBoxID);
				} else {
					experimentGraph.disconnect(edge.fromBoxID, edge.toBoxID);
				}
			}
		});
	}

	//---------------------------------------------------------------
	// INHERITED INTERFACE

	@Override
	public KineticComponentState getState() {
		return (KineticComponentState) super.getState();
	}

	//---------------------------------------------------------------
	// BOTH SERVER AND CLIENT KINETIC COMPONENT INTERFACE

	/*
	 * Simply forward calls to client.
	 */

	@Override
	public void highlightBoxes(Integer[] boxIDs) {
		getClientRPC().highlightBoxes(boxIDs);
	}

	@Override
	public void cancelBoxHighlight() {
		getClientRPC().cancelBoxHighlight();
	}

	@Override
	public void cancelSelection() {
		getClientRPC().cancelSelection();
	}

	@Override
	public void resetEnvironment() {
		/*
		 * Server side reset could be done in a response call from the client but
		 * then we would have to wait for it before the current thread proceeds.
		 * As such, NEVER reset server-side state from the client :).
		 */

		// client side reset
		getClientRPC().resetEnvironment();

		// server side reset
		experimentGraph.clear();
		experimentGraph = null;
	}

	//---------------------------------------------------------------
	// EXPERIMENT IMPORT/EXPORT RELATED ROUTINES/TYPES

	/**
	 * Used BEFORE saving experiments to database.</br> 
	 */
	public static interface IOnExperimentExported {
		/**
		 * Handle the exported experiment in this method.
		 * @param experimentSavedCallback callback for when the experiment is successfully saved to database
		 */
		void handleExperiment(UniversalComputationDescription exportedExperiment, IOnExperimentSaved experimentSavedCallback);
	}

	/**
	 * Used AFTER saving experiments to database to keep this component's inner state in sync.
	 */
	public static interface IOnExperimentSaved {
		/**
		 * Call this method when your experiment has successfully been saved to database to keep
		 * the component in consistent state.
		 * @param newBatchID the ID of the newly saved experiment
		 */
		void experimentSaved(JPABatch newExperimentEntity);
	}

	public void importExperiment(JPABatch experiment) {
		try {
			// first and foremost, reset current state (both server and client)
			resetEnvironment();

			// transform to universal format
			UniversalComputationDescription uniFormat = UniversalComputationDescription.fromXML(experiment.getXML());

			// transform universal format to server format and store the result
			experimentGraph = ExperimentGraphServer.fromUniversalFormat(parentEditor.getAgentInfoProvider(), uniFormat);

			// transform to client format and send it to the client
			getClientRPC().receiveExperimentToLoad(experimentGraph.toClientFormat());

			// finish updating server-side state
			previouslyLoadedExperimentID = experiment.getId();

			// and some final visual changes
			parentTab.setCaption(experiment.getName());
		} catch (ConversionException e) {
			PikaterWebLogger.logThrowable("", e);
			MyNotifications.showError("Could not import experiment", "Please, contact the administrators.");
		}
	}

	public void exportExperiment(boolean validationNeeded, IOnExperimentExported exportCallback) {
		try {
			/*
			 * Only export experiments that are valid (if validation needs to be checked).
			 */
			if (validationNeeded) {
				ExperimentGraphValidator validator = new ExperimentGraphValidator(experimentGraph);
				validator.validate();
				if (validator.aProblemWasFound()) {
					StringBuilder sb = new StringBuilder();
					for (String problem : validator.getProblems()) {
						if (validator.getProblems().size() > 1) {
							sb.append("- ");
						}
						sb.append(problem);
						sb.append("\n");
					}
					GeneralDialogs.error("Experiment not valid", sb.toString());
					return;
				}
			}

			/*
			 * The actual export code.
			 */
			UniversalComputationDescription result = experimentGraph.toUniversalFormat(parentEditor.getAgentInfoProvider());

			/*
			// test case - redirect the same experiment into a new tab and test the conversion cycle (don't save experiment for execution if you want to use this)
			JPABatch newBatch = new JPABatch("poliket", "bla bla", result.toXML(), null);
			parentEditor.loadExperimentIntoNewTab(newBatch);
			*/

			exportCallback.handleExperiment(result, new IOnExperimentSaved() {
				@Override
				public void experimentSaved(JPABatch newExperimentEntity) {
					if (newExperimentEntity.getId() == 0) {
						throw new IllegalStateException("The given new experiment has not been saved yet.");
					} else {
						previouslyLoadedExperimentID = newExperimentEntity.getId();
						parentTab.setCaption(newExperimentEntity.getName());
					}
				}
			});
		} catch (ConversionException e) {
			PikaterWebLogger.logThrowable("", e);
			MyNotifications.showError("Could not export experiment", "Please, contact the administrators.");
		}
	}

	//---------------------------------------------------------------
	// MISCELLANEOUS PUBLIC INTERFACE

	public ExperimentGraphServer getExperimentGraph() {
		return experimentGraph;
	}

	/**
	 * Database ID of the last loaded experiment for this canvas.
	 */
	public Integer getPreviouslyLoadedExperimentID() {
		return previouslyLoadedExperimentID;
	}

	/**
	 * This reference can not be passed via a constructor because
	 * of implementation issues. Make sure to do it sometime later.
	 */
	public void setParentTab(CustomTabSheetTabComponent parentTab) {
		this.parentTab = parentTab;
	}

	@Deprecated
	public boolean isContentModified() {
		return true; // TODO: until problems with the "modified" status are resolved, always return true
		// return getState().serverThinksThatSchemaIsModified;
	}

	/**
	 * Creates a new box for the given arguments and adds it to
	 * the kinetic canvas. In other words, handles the whole
	 * process.
	 */
	public BoxInfoServer createBox(AgentInfo info, int absX, int absY, boolean sendToClient) {
		BoxInfoServer result = null;
		try {
			result = experimentGraph.addBox(new BoxInfoServer(info, absX - absoluteLeft, absY - absoluteTop));
		} catch (CloneNotSupportedException e) {
			PikaterWebLogger.logThrowable("Could not create a new server box because:", e);
			return null;
		}

		if (sendToClient) {
			getClientRPC().createBox(result.toClientFormat());
		}

		return result;
	}

	//---------------------------------------------------------------
	// MISCELLANEOUS PRIVATE INTERFACE

	private KineticComponentClientRpc getClientRPC() {
		return getRpcProxy(KineticComponentClientRpc.class);
	}
}
