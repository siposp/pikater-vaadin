import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.jdom.JDOMException;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import ontology.messages.*;


public class Agent_GUI_config_file extends Agent_GUI{

	private String path = System.getProperty("user.dir")+System.getProperty("file.separator");
	private String configFileName;
	
	
	@Override
	protected void displayOptions(Problem problem, int performative) {
		String msg = "Failed";
		if (performative == ACLMessage.INFORM){ 
			msg = "OK";
		}
		System.out.println("Agent :"+getName()+": Displaying the options ;) "+msg);
	} //  end displayOptions

	@Override
	protected void displayResult(ACLMessage inform) {
		System.out.println("Agent :"+getName()+": Displaying the results ;)");
	}
	@Override
	protected void DisplayWrongOption(int problemGuiId, String agentName, String optionName, String errorMessage){
		System.out.println("Agent :"+getName()+" "+problemGuiId+" "+agentName+" "+optionName+" "+errorMessage);
	}
	
	@Override
	protected void allOptionsReceived(int problem_id) {
		sendProblem(problem_id);
	}
	
	@Override
	protected String getAgentType() {
		return "GUI config file";
	}

	@Override
	protected void mySetup() {
		setDefault_number_of_values_to_try(4);
		
		doWait(1000);
		
		System.out.println("Agent types: "+offerAgentTypes());
		
		configFileName = getConfigFileName();
		try {
			getProblemsFromXMLFile(configFileName);
		}
		// indicates a well-formedness error
        catch (JDOMException e) { 
          System.out.println(configFileName + " is not well-formed. "+e.getMessage());
        }  
        catch (IOException e) { 
          System.out.print("Could not check " + configFileName);
          System.out.println(" because " + e.getMessage());
        }
		// */        
        
		/*
        // test:
        int newId = createNewProblem("1000");
        try {
			addAgentToProblemWekaStyle(newId, null, "MultilayerPerceptron", "-L 0.2 -D -M ? -H ?,?".split(" "));
		} catch (FailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         addDatasetToProblem(newId, "iris.arff", "iris.arff");
 		// getAgentOptions("mp1"); 
        // */
	
		
	}	// end mySetup

	@Override
  	protected void displayPartialResult(ACLMessage inform) {
		System.out.println("Partial results");
	} 

	private String getConfigFileName(){
		return (String)getArguments()[0];
	}
	
}
