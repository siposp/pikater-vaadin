package org.pikater.core.options.recommend;

import org.pikater.core.agents.experiment.recommend.Agent_NMTopRecommender;
import org.pikater.core.ontology.subtrees.agentInfo.AgentInfo;
import org.pikater.core.ontology.subtrees.batchDescription.Recommend;
import org.pikater.core.options.AAA_SlotHelper;

public class NMTopRecommender_Box {

	public static AgentInfo get() {

		AgentInfo agentInfo = new AgentInfo();
		agentInfo.importAgentClass(Agent_NMTopRecommender.class);
		agentInfo.importOntologyClass(Recommend.class);
	
		agentInfo.setName("NMTop Recommend");
		agentInfo.setDescription("NMTop Recommend");

		//Slot Definition
		agentInfo.setOutputSlots(AAA_SlotHelper.getRecommendOutputSlots());

		return agentInfo;
	}

}