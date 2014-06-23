package org.pikater.core.agents.system.managerAgent;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

import org.pikater.core.agents.configuration.Arguments;
import org.pikater.core.agents.system.Agent_ManagerAgent;
import org.pikater.core.ontology.AgentManagementOntology;
import org.pikater.core.ontology.MessagesOntology;
import org.pikater.core.ontology.subtrees.management.CreateAgent;
import org.pikater.core.ontology.subtrees.management.KillAgent;
import org.pikater.core.ontology.subtrees.management.KillYourself;
import org.pikater.core.ontology.subtrees.management.LoadAgent;
import org.pikater.core.ontology.subtrees.management.SaveAgent;
import org.pikater.core.ontology.subtrees.task.ExecuteTask;

import java.io.*;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * User: Kuba
 * Date: 7.11.13
 * Time: 15:51
 */
public class ManagerAgentRequestResponder {
    private Agent_ManagerAgent managerAgent;

    
    public ManagerAgentRequestResponder(Agent_ManagerAgent managerAgent) {
        this.managerAgent = managerAgent;
    }

    public  Object toObject(byte[] bytes) throws IOException, ClassNotFoundException{
        Object object;

        object = new java.io.ObjectInputStream(new
                java.io.ByteArrayInputStream(bytes)).readObject();

        return object;
    }

    public ACLMessage respondToSaveAgent(ACLMessage request) throws OntologyException, Codec.CodecException, IOException, ClassNotFoundException {
        Action a = (Action) managerAgent.getContentManager().extractContent(request);
        SaveAgent sa = (SaveAgent) a.getAction();

        int userID = sa.getUserID();

        org.pikater.core.ontology.subtrees.management.Agent agent = sa.getAgent();

        String name = agent.getName(); // TODO - zajistit unikatni pro konkretniho uzivatele
        Timestamp currentTimestamp =
                new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());


        String filename = userID + "_" + name + "_"
                + currentTimestamp.toString().replace(":", "-").replace(" ", "_");


        // save serialized object to file
        byte [] object = sa.getAgent().getObject();
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(Agent_ManagerAgent.saveDirectoryPath + filename + ".model"));


        oos.writeObject(toObject(object));
        oos.flush();
        oos.close();
        managerAgent.log("Agent " + name + " saved to file" + filename + ".model");

        ACLMessage reply = request.createReply();
        reply.setContent(filename);
        reply.setPerformative(ACLMessage.INFORM);

        return reply;
    }

    public ACLMessage respondToCreateAgent(ACLMessage request) throws OntologyException, CodecException {
    	
        Action a = (Action) managerAgent.getContentManager().extractContent(request);
        CreateAgent createAgent = (CreateAgent) a.getAction();
        
        String agentName = createAgent.getName();
        String agentType = createAgent.getType();
        Arguments arguments = createAgent.getArguments();
        
        String agentNameCreated =
        		managerAgent._createAgent(agentType, agentName, arguments);

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(agentNameCreated);
        managerAgent.log("Agent " + agentNameCreated + " created.");

        return reply;
    }

	public ACLMessage respondToKillAgent(ACLMessage request) throws OntologyException, CodecException {
		
        Action a = (Action) managerAgent.getContentManager().extractContent(request);
        KillAgent killAgent = (KillAgent) a.getAction();

        String agentName = killAgent.getName();
        AID agentAID = new AID(agentName, false);
        
        managerAgent.log("Request to kill " + agentName + " agent");
        Ontology ontology = AgentManagementOntology.getInstance();
        Codec codec = managerAgent.getCodec();
        
        
        ACLMessage msgToAgent = request.createReply();
        msgToAgent.setPerformative(ACLMessage.REQUEST);
        msgToAgent.setLanguage(codec.getName());
        msgToAgent.setOntology(ontology.getName());
        
        try {
        	Action actionToAgent = new Action(agentAID, new KillYourself());
        	managerAgent.getContentManager().fillContent(msgToAgent, actionToAgent);
        	
		} catch (CodecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String replyText = null;
        try {
			ACLMessage replyFromAgent = FIPAService.doFipaRequestClient(managerAgent, msgToAgent, 10000);
			replyText = replyFromAgent.getContent();

		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		managerAgent.log("Reply from " + agentName + " : " + replyText);

		
		ACLMessage reply = request.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		return reply;
	}
	
    public ACLMessage respondToLoadAgent(ACLMessage request) throws OntologyException, Codec.CodecException, IOException, ClassNotFoundException, ControllerException {

        Action a = (Action) managerAgent.getContentManager().extractContent(request);
        LoadAgent la = (LoadAgent) a.getAction();
        ExecuteTask fa = la.getFirst_action();

        Agent newAgent;

        if (la.getObject() != null){
            newAgent = (Agent)toObject(la.getObject());
        }
        else {

            // read agent from file
            String filename = Agent_ManagerAgent.saveDirectoryPath
            		+ la.getFilename() + ".model";

            //Construct the ObjectInputStream object
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));

            newAgent = (Agent) inputStream.readObject();

            inputStream.close();
        }

        managerAgent.log("Resurrected agent : " + newAgent);
        // TODO kdyz se ozivuje 2x ten samej -> chyba


        if (newAgent != null){
            // get a container controller for creating new agents

            ContainerController container = managerAgent.getContainerController();
            AgentController controller = container.acceptNewAgent(la.getFilename(), newAgent);
            controller.start();

        }
        else {
            throw new ControllerException("Agent not created.");
        }

        managerAgent.log("Loaded agent:   " + la.getFilename());

        jade.lang.acl.ACLMessage reply;

        if (fa != null){
            // send message with fa action to the loaded agent
            Action ac = new Action();
            ac.setAction(fa);
            ac.setActor(request.getSender());

            Ontology ontology = MessagesOntology.getInstance();
            
            jade.lang.acl.ACLMessage first_message = new jade.lang.acl.ACLMessage(jade.lang.acl.ACLMessage.REQUEST);
            first_message.setLanguage(managerAgent.getCodec().getName());
            first_message.setOntology(ontology.getName());
            first_message.addReceiver(new AID(la.getFilename(), AID.ISLOCALNAME));
            first_message.clearAllReplyTo();
            first_message.addReplyTo(request.getSender());
            first_message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            first_message.setConversationId(request.getConversationId());

            managerAgent.getContentManager().fillContent(first_message, ac);
            managerAgent.send(first_message);
        }
        reply = request.createReply();
        reply.setContent("Agent "+newAgent.getLocalName()+" resurected.");
        reply.setPerformative(jade.lang.acl.ACLMessage.INFORM);


        return reply;
    }

}