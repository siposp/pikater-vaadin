package org.pikater.core.options.xmlGenerators;

import org.pikater.core.agents.experiment.computing.Agent_WekaCA;
import org.pikater.core.ontology.agentInfo.AgentInfo;
import org.pikater.core.ontology.description.ComputingAgent;

public class NBTree_CABox {
	
	public static AgentInfo get() {

		AgentInfo agentInfo = new AgentInfo();
		agentInfo.setAgentClass(Agent_WekaCA.class.getName());
		agentInfo.setOntologyClass(ComputingAgent.class.getName());
	
		agentInfo.setName("NBTree");
		agentInfo.setPicture("picture3.jpg");
		agentInfo.setDescription("NBTree Method");


		// Slots Definition
		agentInfo.setInputSlots(AAA_SlotHelper.getCAInputSlots());
		agentInfo.setOutputSlots(AAA_SlotHelper.getCAOutputSlots());

		return agentInfo;
	}
}
