package ontology.messages;

import jade.content.onto.*;
import jade.content.schema.*;
import java.util.*;


public class MessagesOntology extends Ontology {

	  
	  //  A symbolic constant, containing the name of this ontology.
	  
	  public static final String NAME = "messages-ontology";

	  // VOCABULARY
	  // Concepts

	  public static final String TASK = "TASK";
	  public static final String TASK_ID = "id";
	  public static final String TASK_COMPUTATION_ID = "computation_id";
	  public static final String TASK_PROBLEM_ID = "problem_id";
	  public static final String TASK_OPTIONS = "options";
	  public static final String TASK_RESULT = "result";
	  
	  public static final String COMPUTATION = "COMPUTATION";
	  public static final String COMPUTATION_ID = "id";
	  public static final String COMPUTATION_PROBLEM_ID = "problem_id";
	  public static final String COMPUTATION_AGENT = "agent";
	  public static final String COMPUTATION_DATA_FILE_NAME = "data_file_name";
	  
	  public static final String PROBLEM = "PROBLEM";
	  public static final String PROBLEM_ID = "id";
	  public static final String PROBLEM_AGENTS = "agents";
	  public static final String PROBLEM_FILE_NAMES = "file_names";

	  public static final String EVALUATION = "EVALUATION";
	  public static final String EVALUATION_ERROR_RATE = "error_rate";
	  public static final String EVALUATION_PCT_INCORRECT = "pct_incorrect";
	  
	  public static final String OPTION = "OPTION";
	  public static final String OPTION_MUTABLE = "mutable";
	  public static final String OPTION_RANGE = "range";
	  public static final String OPTION_SET = "set";
	  public static final String OPTION_IS_A_SET = "is_a_set";
	  public static final String OPTIONS_NUM_ARGS = "number_of_args";
	  public static final String OPTION_DATA_TYPE = "data_type";
	  public static final String OPTION_WEKA_DESTRIPTION = "description";
	  public static final String OPTION_WEKA_NAME = "name";
	  public static final String OPTION_WEKA_SYNOPSIS = "synopsis";
	  public static final String OPTION_VALUE = "value";
	  

	  public static final String AGENT = "AGENT";
	  public static final String AGENT_NAME = "name";
	  public static final String AGENT_OPTIONS = "options";
	  	  
	  public static final String INTERVAL = "INTERVAL";
	  public static final String INTERVAL_MIN = "min";
	  public static final String INTERVAL_MAX = "max";
	  
	  // Predicates

	  // Actions
	  public static final String COMPUTE = "COMPUTE";
	  public static final String COMPUTE_COMPUTATION = "computation";
	  
	  public static final String EXECUTE = "EXECUTE";
	  public static final String EXECUTE_TASK = "task";

	  
	  
	  public static final String SOLVE = "SOLVE";
	  public static final String SOLVE_PROBLEM = "problem";
	  
	  public static final String GET_OPTIONS = "GET-OPTIONS";

	  // public static final String SEND_OPTIONS = "SEND-OPTIONS";
	  // public static final String SEND_OPTIONS_OPTIONS = "options";
	  
	  
	  private static Ontology theInstance = new MessagesOntology();
		
	  /**
	     This method grants access to the unique instance of the
	     ontology.
	     @return An <code>Ontology</code> object, containing the concepts
	     of the ontology.
	  */
	   public static Ontology getInstance() {
			return theInstance;
	   }
		
	  /**
	   * Constructor
	   */
	  private MessagesOntology() {
	    //__CLDC_UNSUPPORTED__BEGIN
	  	super(NAME, BasicOntology.getInstance());


	    try {
			add(new ConceptSchema(TASK), Task.class);
			add(new ConceptSchema(COMPUTATION), Computation.class);
			add(new ConceptSchema(OPTION), Option.class);			
			add(new ConceptSchema(INTERVAL), Interval.class);
			add(new ConceptSchema(AGENT), Agent.class);
			add(new ConceptSchema(PROBLEM), Problem.class);
			add(new ConceptSchema(EVALUATION), Evaluation.class);
			
			add(new AgentActionSchema(COMPUTE), Compute.class);
			add(new AgentActionSchema(GET_OPTIONS), GetOptions.class);
			add(new AgentActionSchema(EXECUTE), Execute.class);
			add(new AgentActionSchema(SOLVE), Solve.class);
			
			// add(new AgentActionSchema(SEND_OPTIONS), SendOptions.class);
			
			
	    	ConceptSchema cs = (ConceptSchema)getSchema(COMPUTATION);
	    	cs.add(COMPUTATION_ID, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(COMPUTATION_PROBLEM_ID, (PrimitiveSchema)getSchema(BasicOntology.STRING));
			cs.add(COMPUTATION_AGENT, (ConceptSchema)getSchema(AGENT));
			cs.add(COMPUTATION_DATA_FILE_NAME, (PrimitiveSchema)getSchema(BasicOntology.STRING));

			cs = (ConceptSchema)getSchema(PROBLEM);
			cs.add(PROBLEM_ID, (PrimitiveSchema)getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
	    	cs.add(PROBLEM_AGENTS, (ConceptSchema)getSchema(AGENT), 1, ObjectSchema.UNLIMITED);
	    	cs.add(PROBLEM_FILE_NAMES, (PrimitiveSchema)getSchema(BasicOntology.STRING), 1, ObjectSchema.UNLIMITED);

			cs = (ConceptSchema)getSchema(TASK);
			cs.add(TASK_ID, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(TASK_COMPUTATION_ID, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(TASK_PROBLEM_ID, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(TASK_OPTIONS, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(TASK_RESULT, (ConceptSchema)getSchema(EVALUATION), ObjectSchema.OPTIONAL);
	    	
	    	cs = (ConceptSchema)getSchema(INTERVAL);
	    	cs.add(INTERVAL_MIN, (PrimitiveSchema)getSchema(BasicOntology.FLOAT));
	    	cs.add(INTERVAL_MAX, (PrimitiveSchema)getSchema(BasicOntology.FLOAT));	
	    	
	    	cs = (ConceptSchema)getSchema(OPTION);
	    	cs.add(OPTION_MUTABLE, (PrimitiveSchema)getSchema(BasicOntology.BOOLEAN));
	    	cs.add(OPTION_RANGE, (ConceptSchema)getSchema(INTERVAL));
	    	// cs.add(OPTION_SET, (PrimitiveSchema)getSchema(BasicOntology.STRING), 1, ObjectSchema.UNLIMITED);
	    	cs.add(OPTION_IS_A_SET, (PrimitiveSchema)getSchema(BasicOntology.BOOLEAN));  	
	    	cs.add(OPTIONS_NUM_ARGS, (ConceptSchema)getSchema(INTERVAL));
	    	cs.add(OPTION_DATA_TYPE, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(OPTION_WEKA_DESTRIPTION, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(OPTION_WEKA_NAME, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(OPTION_WEKA_SYNOPSIS, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(OPTION_VALUE, (PrimitiveSchema)getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
	    	
	    	cs = (ConceptSchema)getSchema(EVALUATION);
	    	cs.add(EVALUATION_ERROR_RATE, (PrimitiveSchema)getSchema(BasicOntology.FLOAT));
	    	cs.add(EVALUATION_PCT_INCORRECT, (PrimitiveSchema)getSchema(BasicOntology.FLOAT));
    	
	    	cs = (ConceptSchema)getSchema(AGENT);
	    	cs.add(AGENT_NAME, (PrimitiveSchema)getSchema(BasicOntology.STRING));
	    	cs.add(AGENT_OPTIONS, (ConceptSchema)getSchema(OPTION), 1, ObjectSchema.UNLIMITED);
	    	
	    	
			AgentActionSchema as = (AgentActionSchema)getSchema(COMPUTE);
			as.add(COMPUTE_COMPUTATION, (ConceptSchema)getSchema(COMPUTATION));

			as = (AgentActionSchema)getSchema(SOLVE);
	    	as.add(SOLVE_PROBLEM, (ConceptSchema)getSchema(PROBLEM));

			as = (AgentActionSchema)getSchema(GET_OPTIONS);
			
			as = (AgentActionSchema)getSchema(EXECUTE);
	    	as.add(EXECUTE_TASK, (ConceptSchema)getSchema(TASK));

			//as = (AgentActionSchema)getSchema(SEND_OPTIONS);
			//as.add(SEND_OPTIONS_OPTIONS, (ConceptSchema)getSchema(OPTION), 1, ObjectSchema.UNLIMITED);
			
	    }
	    catch(OntologyException oe) {
	      oe.printStackTrace();
	    }
	  }
	  

}

	  