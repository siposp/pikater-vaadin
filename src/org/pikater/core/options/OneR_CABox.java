package org.pikater.core.options;

import org.pikater.core.agents.experiment.computing.Agent_WekaOneRCA;
import org.pikater.core.ontology.subtrees.agentInfo.AgentInfo;
import org.pikater.core.ontology.subtrees.batchDescription.ComputingAgent;
import org.pikater.core.ontology.subtrees.newOption.base.NewOption;
import org.pikater.core.ontology.subtrees.newOption.restrictions.RangeRestriction;
import org.pikater.core.ontology.subtrees.newOption.values.IntegerValue;

public class OneR_CABox {
	

	public static AgentInfo get() {

		/**
		# Specify the minimum number of objects in a bucket (default: 6).
		$ B int 1 1 r 1 100
		**/
		NewOption optionB = new NewOption("B", new IntegerValue(6), new RangeRestriction(
				new IntegerValue(1),
				new IntegerValue(100))
		);
		optionB.setDescription("Specify the minimum number of objects in a bucket");
		

		AgentInfo agentInfo = new AgentInfo();
		agentInfo.setAgentClass(Agent_WekaOneRCA.class);
		agentInfo.setOntologyClass(ComputingAgent.class);
	
		agentInfo.setName("OneR");
		agentInfo.setDescription("One R Method");

		agentInfo.addOption(optionB);
		

		// Slots Definition
		agentInfo.setInputSlots(AAA_SlotHelper.getCAInputSlots());
		agentInfo.setOutputSlots(AAA_SlotHelper.getCAOutputSlots());

		return agentInfo;
	}

}
