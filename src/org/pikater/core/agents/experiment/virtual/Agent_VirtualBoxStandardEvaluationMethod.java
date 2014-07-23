package org.pikater.core.agents.experiment.virtual;

import org.pikater.core.ontology.subtrees.agentInfo.AgentInfo;
import org.pikater.core.options.StandardEvaluationMethod_VirtualBox;

public class Agent_VirtualBoxStandardEvaluationMethod extends Agent_VirtualBoxProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3074428585945051239L;

	@Override
	protected AgentInfo getAgentInfo() {

		return StandardEvaluationMethod_VirtualBox.get();
	}

}