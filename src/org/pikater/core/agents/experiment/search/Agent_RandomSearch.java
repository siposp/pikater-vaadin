package org.pikater.core.agents.experiment.search;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.pikater.core.ontology.subtrees.agentInfo.AgentInfo;
import org.pikater.core.ontology.subtrees.newOption.NewOption;
import org.pikater.core.ontology.subtrees.newOption.Value;
import org.pikater.core.ontology.subtrees.newOption.value.FloatValue;
import org.pikater.core.ontology.subtrees.newOption.value.IntegerValue;
import org.pikater.core.ontology.subtrees.search.SearchSolution;
import org.pikater.core.ontology.subtrees.search.searchItems.SearchItem;
import org.pikater.core.options.RandomSearch_SearchBox;

public class Agent_RandomSearch extends Agent_Search {

	private static final long serialVersionUID = 2777277001533605329L;

	private int number_of_tries = 0;
	private float error_rate = 1;
	
	private int maximum_tries;
	private float final_error_rate;
	protected Random rnd_gen = new Random(1);

	@Override
	protected String getAgentType() {
		return "RandomSearch";
	}

	@Override
	protected AgentInfo getAgentInfo() {
		return RandomSearch_SearchBox.get();
	}

	@Override
	protected boolean finished() {
		if (number_of_tries >= maximum_tries) {
			return true;
		}

		if (error_rate <= final_error_rate) {
			return true;
		}
		return false;
	}

	@Override
	protected void loadSearchOptions(){
		
		List<NewOption> search_options = getSearch_options();
		// find maximum tries in Options

		final_error_rate = (float) 0.01;
		maximum_tries= 10;
		
		for (NewOption next : search_options) {
			
			Value valueI = next.convertToSingleValue();
			
			if (next.getName().equals("E")){
				FloatValue value = (FloatValue) valueI.getValue();
				final_error_rate = value.getValue();
			}
			if (next.getName().equals("M")){
				IntegerValue value = (IntegerValue) valueI.getValue();
				maximum_tries = value.getValue();
			}
		}
		// query_block_size = 1;
		query_block_size = maximum_tries;
		System.out.println(getLocalName()+" parameters are: ");
		System.out.println("   final_error_rate: " + final_error_rate);
		System.out.println("   maximum_tries: " + maximum_tries);		
	}

	@Override
	protected void updateFinished(float[][] evaluations) {
		if (evaluations == null){
			error_rate = 1;
		}
		else{
			float best_err = evaluations[0][0];
			for(int i = 0; i < evaluations.length; i++){
				if(evaluations[i][0]<best_err)
					best_err = evaluations[i][0];
			}
			error_rate = best_err;//((Evaluation)(evaluations.get(0))).getError_rate();			
		}
	}
	
	private SearchSolution genRandomSolution(){
		// go through the solutions Vector, generate random values
		List<String> new_solution = new ArrayList<String>();
		
		for (SearchItem si : getSchema() ) {
			new_solution.add(si.randomValue(rnd_gen));
		}
		SearchSolution sol = new SearchSolution();
		sol.setValues(new_solution);
		return sol;
	}
		
	@Override
	protected List<SearchSolution> generateNewSolutions(List<SearchSolution> solutions, float[][] evaluations) {
		number_of_tries+=query_block_size;
		
		List<SearchSolution> solutions_list = new ArrayList<SearchSolution>();
		//generate sequence of random solutions
		for(int i = 0; i < query_block_size; i++){
			solutions_list.add(genRandomSolution());
		}
		return solutions_list;
	}

}