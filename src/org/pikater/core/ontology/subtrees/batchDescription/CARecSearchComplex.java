package org.pikater.core.ontology.subtrees.batchDescription;

import java.util.List;
import java.util.ArrayList;

import org.pikater.core.CoreConstants;
import org.pikater.core.ontology.subtrees.newOption.NewOptions;
import org.pikater.core.ontology.subtrees.newOption.base.NewOption;




/**
 * Created by Martin Pilat on 28.12.13.
 */
public class CARecSearchComplex extends DataProcessing implements IComputingAgent, IDataProvider {

	private static final long serialVersionUID = -913470799010962236L;

	private List<NewOption> options;
    private List<ErrorSourceDescription> errors;

    private Search search;
    private Recommend recommender;
    private IComputingAgent computingAgent;

    public CARecSearchComplex() {
    	
    	this.options = new ArrayList<NewOption>();
    	this.errors = new ArrayList<ErrorSourceDescription>();
    }
    
    public List<ErrorSourceDescription> getErrors() {
        return errors;
    }
    public void setErrors(List<ErrorSourceDescription> errors) {
        this.errors = errors;
    }

    public IComputingAgent getComputingAgent() {
        return computingAgent;
    }
    public void setComputingAgent(IComputingAgent computingAgent) {
        this.computingAgent = computingAgent;
    }
    
    public Search getSearch() {
        return search;
    }
    public void setSearch(Search search) {
        this.search = search;
    }

    public Recommend getRecommender() {
        return recommender;
    }
    public void setRecommender(Recommend recommender) {
        this.recommender = recommender;
    }

    public List<NewOption> getOptions() {
        return options;
    }
    public void setOptions(List<NewOption> options) {
        this.options = options;
    }
    public void addOption(NewOption option) {
    	if (option == null) {
    		throw new NullPointerException("Argument option can't be null");
    	}
        this.options.add(option);
    }
	
	@Override
	public List<NewOption> exportAllOptions() {
		return this.options;
	}
	@Override
	public void importAllOptions(List<NewOption> options) {
		this.options = options;	
	}
	
	@Override
	public List<ErrorSourceDescription> exportAllErrors() {
		return this.errors;
	}
	@Override
	public void importAllErrors(List<ErrorSourceDescription> errors) {
		this.errors = errors;
	}
	
	@Override
	public List<DataSourceDescription> exportAllDataSourceDescriptions() {
		
		DataSourceDescription searchSlot =
				new DataSourceDescription();
		searchSlot.setInputType(
				CoreConstants.SLOT_SEARCH);
		searchSlot.setOutputType(
				CoreConstants.SLOT_SEARCH);
		searchSlot.setDataProvider(search);
		
		DataSourceDescription recommenderSlot =
				new DataSourceDescription();
		recommenderSlot.setInputType(
				CoreConstants.SLOT_RECOMMEND);
		recommenderSlot.setOutputType(
				CoreConstants.SLOT_RECOMMEND);
		recommenderSlot.setDataProvider(recommender);
		
		DataSourceDescription computingAgentSlot =
				new DataSourceDescription();
		computingAgentSlot.setInputType(
				CoreConstants.SLOT_COMPUTATION_AGENT);
		computingAgentSlot.setOutputType(
				CoreConstants.SLOT_COMPUTATION_AGENT);
		computingAgentSlot.setDataProvider((IDataProvider) computingAgent);

		List<DataSourceDescription> slots = new ArrayList<DataSourceDescription>();
		if (search != null) {
			slots.add(searchSlot);
		}
		if (recommender != null) {
			slots.add(recommenderSlot);
		}
		if (computingAgent != null) {
			slots.add(computingAgentSlot);
		}
		return slots;
	}
	
	@Override
	public void importAllDataSourceDescriptions(List<DataSourceDescription> dataSourceDescriptions) {
		
		for (DataSourceDescription slotI : dataSourceDescriptions) {
			
			if (slotI.getInputType().equals(CoreConstants.SLOT_SEARCH)) {
				search = (Search) slotI.getDataProvider();
			}
			if (slotI.getInputType().equals(CoreConstants.SLOT_RECOMMEND)) {
				recommender = (Recommend) slotI.getDataProvider();
			}
			if (slotI.getInputType().equals(CoreConstants.SLOT_COMPUTATION_AGENT)) {
				computingAgent = (IComputingAgent) slotI.getDataProvider();
			}
		}
		
	}

	public CARecSearchComplex clone() {
		
		CARecSearchComplex complex = new CARecSearchComplex();
		complex.setId(this.getId());
		NewOptions optionsOnt = new NewOptions(this.options);
		complex.setOptions(optionsOnt.clone().getOptions());
		ErrorDescriptions errorsOnt = new ErrorDescriptions(this.errors);
		complex.setErrors(errorsOnt.clone().getErrors());
		
		if (this.search != null) {
			complex.setSearch(this.search.clone());
		}
		if (this.recommender != null) {
			complex.setRecommender(this.recommender.clone());
		}
		if (this.computingAgent != null) {
			complex.setComputingAgent(this.computingAgent.clone());
		}
		return complex;
	}
	
}
