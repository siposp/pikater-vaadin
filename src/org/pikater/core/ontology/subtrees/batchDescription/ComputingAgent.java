package org.pikater.core.ontology.subtrees.batchDescription;


import java.util.ArrayList;
import java.util.List;

import org.pikater.core.ontology.subtrees.option.Option;
import org.pikater.shared.experiment.universalformat.UniversalComputationDescription;
import org.pikater.shared.experiment.universalformat.UniversalConnector;
import org.pikater.shared.experiment.universalformat.UniversalElement;
import org.pikater.shared.experiment.universalformat.UniversalOntology;


/**
 * Created by Martin Pilat on 28.12.13.
 */
public class ComputingAgent extends AbstractDataProcessing implements IDataProvider, IComputingAgent, IErrorProvider {

	private static final long serialVersionUID = 2127755171666013125L;

	private String agentType;
	private IModel model;
    private List<Option> options;

	private DataSourceDescription trainingData;
    private DataSourceDescription testingData;
    private DataSourceDescription validationData;

    public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public IModel getModel() {
		return model;
	}
	public void setModel(IModel model) {
		this.model = model;
	}    

    public DataSourceDescription getTrainingData() {
        return trainingData;
    }
    public void setTrainingData(DataSourceDescription trainingData) {
        this.trainingData = trainingData;
    }

    public DataSourceDescription getTestingData() {
        return testingData;
    }
    public void setTestingData(DataSourceDescription testingData) {
        this.testingData = testingData;
    }

    public DataSourceDescription getValidationData() {
        return validationData;
    }
    public void setValidationData(DataSourceDescription validationData) {
        this.validationData = validationData;
    }
    
    public List<Option> getOptions() {
    	if (this.options == null) {
    		return new ArrayList<Option>();
    	}
        return options;
    }
    public void setOptions(List<Option> options) {
        this.options = options;
    }
    public void addOption(Option option) {
    	if (this.options == null) {
    		this.options = new ArrayList<Option>();
    	}
        this.options.add(option);
    }


	@Override
	UniversalElement exportUniversalElement(
			UniversalComputationDescription uModel) {
			    
	    UniversalConnector universalTrainingData =
	    		trainingData.exportUniversalConnector(uModel);
	    universalTrainingData.setInputDataType("trainingData");

		UniversalOntology ontologyInfo = new UniversalOntology();
		ontologyInfo.setType(this.getClass());
		ontologyInfo.setOptions(options);
		ontologyInfo.addInputSlot(universalTrainingData);
		
		UniversalElement wrapper = new UniversalElement();
		wrapper.setOntologyInfo(ontologyInfo);
		uModel.addElement(wrapper);
		
		return wrapper;
	}

}

