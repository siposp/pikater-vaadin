package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent;

import java.util.HashMap;
import java.util.Map;

import org.pikater.core.ontology.subtrees.agentInfo.AgentInfo;
import org.pikater.shared.experiment.universalformat.UniversalComputationDescription;
import org.pikater.shared.experiment.universalformat.UniversalConnector;
import org.pikater.shared.experiment.universalformat.UniversalElement;
import org.pikater.shared.experiment.universalformat.UniversalGui;
import org.pikater.shared.experiment.universalformat.UniversalOntology;
import org.pikater.shared.experiment.webformat.BoxInfo;
import org.pikater.shared.experiment.webformat.BoxType;
import org.pikater.shared.experiment.webformat.ExperimentGraph;
import org.pikater.shared.logging.PikaterLogger;
import org.pikater.shared.util.SimpleIDGenerator;
import org.pikater.web.config.AgentInfoCollection;
import org.pikater.web.config.ServerConfigurationInterface;
import org.pikater.web.vaadin.gui.client.kineticcomponent.KineticComponentClientRpc;
import org.pikater.web.vaadin.gui.client.kineticcomponent.KineticComponentServerRpc;
import org.pikater.web.vaadin.gui.client.kineticcomponent.KineticComponentState;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.CustomTabSheetTabComponent;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor.ExpEditorToolbox;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.toolboxes.BoxOptionsToolbox;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.AbstractGraphItemShared.RegistrationOperation;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.BoxGraphItemShared;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.EdgeGraphItemShared;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractComponent;

@JavaScript(value = "kinetic-v4.7.3-dev.js")
public class KineticComponent extends AbstractComponent
{
	private static final long serialVersionUID = -539901377528727478L;
	
	//---------------------------------------------------------------
	// GUI RELATED FIELDS
	
	/**
	 * Constant reference to the parent editor component.
	 */
	private final ExpEditor parentEditor;
	
	/**
	 * Reference to the experiment editor tab linked to this content component.
	 */
	private CustomTabSheetTabComponent parentTab;
	
	/*
	 * Dynamic information from the client side - absolute left corner position of the Kinetic stage.
	 */
	private int absoluteLeft;
	private int absoluteTop;
	
	//---------------------------------------------------------------
	// EXPERIMENT RELATED FIELDS
	
	/**
	 * ID generator for boxes.
	 */
	private final SimpleIDGenerator boxIDGenerator;
	
	/**
	 * The dynamic mapping between boxes and agent information. Only a portion of agent information
	 * is sent to the client (+ some added value), wrapped in BoxInfo instance.
	 * This field is the base for all format conversions and some other commands.
	 */
	private final Map<String, AgentInfo> boxIDToAgentInfo;
	
	/**
	 * Used for saving experiments. The server has to issue an asynchronous command to the
	 * client and wait for an answer. The answer is stored in this field.
	 */
	private ExperimentGraph graphExportedFromClient;
	
	//---------------------------------------------------------------
	// OTHER PROGRAMMATIC FIELDS
	
	private final KineticComponentServerRpc serverRPC;
	
	private boolean bindOptionsManagerWithSelectionChanges;
	
	//---------------------------------------------------------------
	// CONSTRUCTOR
	
	public KineticComponent(final ExpEditor parentEditor)
	{
		super();
		setSizeFull();
		
		/*
		 * Init.
		 */
		
		this.parentEditor = parentEditor;
		
		this.absoluteLeft = 0;
		this.absoluteTop = 0;
		
		this.boxIDGenerator = new SimpleIDGenerator();
		this.boxIDToAgentInfo = new HashMap<String, AgentInfo>();
		this.graphExportedFromClient = null;
		
		/*
		 * Register actions to do on client commands.
		 */
		
		this.serverRPC = new KineticComponentServerRpc()
		{
			private static final long serialVersionUID = -2769231541745495584L;
			
			@Override
			public void command_setExperimentModified(boolean modified)
			{
				getState().serverThinksThatSchemaIsModified = modified;
				parentTab.setTabContentModified(modified);
			}

			@Override
			public void command_onLoadCallback(int absoluteX, int absoluteY)
			{
				KineticComponent.this.absoluteLeft = absoluteX;
				KineticComponent.this.absoluteTop = absoluteY;
				
				// MyNotifications.showInfo(null, "On load callback");
			}
			
			@Override
			public void command_alterClickMode(ClickMode newClickMode)
			{
				getState().clickMode = newClickMode;
				KineticComponent.this.parentEditor.getToolbar().onClickModeAlteredOnClient(newClickMode);
			}
			
			@Override
			public void command_boxSetChange(RegistrationOperation opKind, BoxGraphItemShared[] boxes)
			{
				// TODO Auto-generated method stub
				// MyNotifications.showInfo(null, "Boxes");
			}

			@Override
			public void command_edgeSetChange(RegistrationOperation opKind, EdgeGraphItemShared[] edges)
			{
				// TODO Auto-generated method stub
				// MyNotifications.showInfo(null, "Edges");
			}
			
			@Override
			public void command_selectionChange(String[] selectedBoxesIDs)
			{
				if(bindOptionsManagerWithSelectionChanges)
				{
					// convert to agent information array
					AgentInfo[] selectedBoxesInformation = new AgentInfo[selectedBoxesIDs.length];
					for(int i = 0; i < selectedBoxesIDs.length; i++)
					{
						if(boxIDToAgentInfo.containsKey(selectedBoxesIDs[i]))
						{
							selectedBoxesInformation[i] = boxIDToAgentInfo.get(selectedBoxesIDs[i]);
						}
						else
						{
							throw new IllegalStateException(String.format("Kinetic state out of sync. "
									+ "No agent info was found for box ID '%s'.", selectedBoxesIDs[i]));
						}
					}
					
					BoxOptionsToolbox toolbox = (BoxOptionsToolbox) parentEditor.getToolbox(ExpEditorToolbox.METHOD_OPTION_MANAGER);
					
					// display the toolbox if there's a reason to
					if(selectedBoxesInformation.length > 0)
					{
						parentEditor.openToolbox(ExpEditorToolbox.METHOD_OPTION_MANAGER);
					}
					
					// and give the box options toolbox information to display
					toolbox.setContentFromSelectedBoxes(selectedBoxesInformation);
				}
			}

			@Override
			public void response_sendExperimentToSave(ExperimentGraph experiment)
			{
				graphExportedFromClient = experiment;
			}
		};
		registerRpc(this.serverRPC);
		
		this.bindOptionsManagerWithSelectionChanges = areSelectionChangesBoundWithOptionsManagerByDefault();
	}
	
	//---------------------------------------------------------------
	// INHERITED INTERFACE
	
	@Override
	public KineticComponentState getState()
	{
		return (KineticComponentState) super.getState();
	}
	
	//---------------------------------------------------------------
	// CLIENT RPC RELATED INTERFACE
	
	public void createBox(AgentInfo info, int absX, int absY)
	{
		getClientRPC().command_createBox(createBoxInfo(info, absX - absoluteLeft, absY - absoluteTop));
	}
	
	public void reloadVisualStyle()
	{
		getClientRPC().request_reloadVisualStyle();
	}
	
	public void importExperiment(UniversalComputationDescription uniFormat)
	{
		resetEnvironment();
		
		try
		{
			getClientRPC().command_receiveExperimentToLoad(uniToWeb(uniFormat));
		}
		catch (ConversionException e)
		{
			PikaterLogger.logThrowable("", e);
			MyNotifications.showError(null, "Could not import experiment. Contact the administrators.");
		}
	}
	
	public UniversalComputationDescription exportExperiment()
	{
		// send command to the client
		getClientRPC().request_sendExperimentToSave();
		
		// wait for an answer
		final int maxWaitTime = 30000; // 30 seconds
		final int waitTimePerIteration = 1000; // 1 second
		int timeWaited = 0;
		while(graphExportedFromClient == null)
		{
			try
			{
				Thread.sleep(waitTimePerIteration); 
			}
			catch (InterruptedException e)
			{
				PikaterLogger.logThrowable("Thread was interrupted while waiting for client answer. Perhaps a request timeout?", e);
				return null;
			}
			
			timeWaited += waitTimePerIteration;
			if(timeWaited == maxWaitTime)
			{
				return null;
			}
		}
		
		// and finally, try to return what is promised
		try
		{
			return webToUni(graphExportedFromClient);
		}
		catch (ConversionException e)
		{
			PikaterLogger.logThrowable("Could not convert to universal format because of the error below.", e);
			return null;
		}
		finally
		{
			graphExportedFromClient = null;
		}
	}
	
	//---------------------------------------------------------------
	// MISCELLANEOUS PUBLIC INTERFACE
	
	public boolean areSelectionChangesBoundWithOptionsManager()
	{
		return bindOptionsManagerWithSelectionChanges;
	}
	
	public static boolean areSelectionChangesBoundWithOptionsManagerByDefault()
	{
		return true;
	}

	public void setBindOptionsManagerWithSelectionChanges(boolean bindOptionsManagerWithSelectionChanges)
	{
		this.bindOptionsManagerWithSelectionChanges = bindOptionsManagerWithSelectionChanges;
	}
	
	public void setParentTab(CustomTabSheetTabComponent parentTab)
	{
		this.parentTab = parentTab;
	}
	
	public boolean isContentModified()
	{
		return getState().serverThinksThatSchemaIsModified;
	}
	
	//---------------------------------------------------------------
	// MISCELLANEOUS PRIVATE INTERFACE
	
	private KineticComponentClientRpc getClientRPC()
	{
		return getRpcProxy(KineticComponentClientRpc.class);
	}
	
	private BoxInfo createBoxInfo(AgentInfo info, int relX, int relY)
	{
		BoxType type = BoxType.fromAgentInfo(info);
		String newBoxID = String.valueOf(boxIDGenerator.getAndIncrement());
		boxIDToAgentInfo.put(newBoxID, info);
		return new BoxInfo(
				newBoxID,
				type.name(),
				info.getName(),
				relX,
				relY,
				type.toPictureURL()
		);
	}
	
	private void resetEnvironment()
	{
		getClientRPC().command_resetKineticEnvironment();
		serverRPC.command_setExperimentModified(false);
		boxIDToAgentInfo.clear();
	}
	
	//---------------------------------------------------------------
	// FORMAT CONVERSIONS
	
	private UniversalComputationDescription webToUni(ExperimentGraph webFormat) throws ConversionException
	{
		// first some checks
		AgentInfoCollection agentInfoProvider = ServerConfigurationInterface.getKnownAgents();
		if(webFormat == null)
		{
			throw new ConversionException(new NullPointerException("The argument web format is null."));
		}
		else if(agentInfoProvider == null)
		{
			throw new ConversionException(new NullPointerException("Agent information has not yet been received from pikater."));
		}
		
		UniversalComputationDescription result = new UniversalComputationDescription();
		for(BoxInfo boxInfo : webFormat.leafBoxes.values())
		{
			AgentInfo agentInfo = boxIDToAgentInfo.get(boxInfo.boxID);
			if(agentInfo == null)
			{
				throw new ConversionException(new IllegalStateException(String.format(
						"No agent info was found for box ID '%s'. Double check for box leaks.", boxInfo.boxID)));
			}
			
			UniversalOntology ontologyInfo = new UniversalOntology();
			try
			{
				ontologyInfo.setType(Class.forName(agentInfo.getOntologyClassName()));
			}
			catch (ClassNotFoundException e)
			{
				throw new ConversionException(new IllegalStateException(
						"Could not convert '%s' to a class instance. Has the referenced class moved or been deleted?", e));
			}
			
			// TODO: wrapper boxes, errors, options...
			
			UniversalElement elem = new UniversalElement();
			elem.setGUIInfo(new UniversalGui(boxInfo.initialX, boxInfo.initialY));
			elem.setOntologyInfo(ontologyInfo);
			
			result.addElement(elem);
		}
		return result;
	}
	
	private ExperimentGraph uniToWeb(UniversalComputationDescription uniFormat) throws ConversionException
	{
		// first some checks
		AgentInfoCollection agentInfoProvider = ServerConfigurationInterface.getKnownAgents();
		if(uniFormat == null)
		{
			throw new ConversionException(new NullPointerException("The argument universal format is null."));
		}
		else if(agentInfoProvider == null)
		{
			throw new ConversionException(new NullPointerException("Agent information has not yet been received from pikater."));
		}
		
		// and then onto the conversion
		if(uniFormat.isGUICompatible())
		{
			ExperimentGraph webFormat = new ExperimentGraph();

			// first convert all boxes
			Map<UniversalElement, String> uniBoxToWebBoxID = new HashMap<UniversalElement, String>();
			for(UniversalElement element : uniFormat.getAllElements())
			{
				AgentInfo agentInfo =  agentInfoProvider.getByOntologyClass(element.getOntologyInfo().getType());
				if(agentInfo == null)
				{
					throw new ConversionException(new IllegalStateException(String.format(
							"No agent info instance was found for ontology '%s'.", element.getOntologyInfo().getType().getName())));
				}
				else
				{
					BoxInfo info = createBoxInfo(agentInfo, element.getGUIInfo().x, element.getGUIInfo().y);
					String convertedBoxID = webFormat.addLeafBoxAndReturnID(info);
					uniBoxToWebBoxID.put(element, convertedBoxID);
				}
			}
			
			// then convert all edges
			for(UniversalElement element : uniFormat.getAllElements())
			{
				for(UniversalConnector edge : element.getOntologyInfo().getInputSlots())
				{
					webFormat.connect(
							uniBoxToWebBoxID.get(edge.getFromElement()),
							uniBoxToWebBoxID.get(element)
					);
				}
			}
			
			// TODO: wrapper boxes, options & stuff
			
			return webFormat;
		}
		else
		{
			throw new ConversionException(new IllegalArgumentException(String.format(
					"The universal format below is not fully compatible with the GUI (web) format.\n%s", uniFormat.toXML())));
		}
	}
}