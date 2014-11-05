package org.pikater.web.experiment.server;

import java.util.LinkedHashSet;
import java.util.Set;

import org.pikater.core.CoreConstant;
import org.pikater.core.CoreConstant.SlotDirection;
import org.pikater.core.ontology.subtrees.agentinfo.Slot;
import org.pikater.core.ontology.subtrees.newoption.base.NewOption;

/**
 * As implied by the name, a class created for the sole purpose of validating experiments
 * before being queued for execution.
 * 
 * @author SkyCrawl
 */
public class ExperimentGraphValidator {
	/**
	 * The experiment to validate.
	 */
	private final ExperimentGraphServer experimentGraph;

	/**
	 * Collection storing all found problems as strings.
	 */
	private final Set<String> problems;

	public ExperimentGraphValidator(ExperimentGraphServer experimentGraph) {
		this.experimentGraph = experimentGraph;
		this.problems = new LinkedHashSet<String>();
	}

	//-----------------------------------------------------------
	// BASIC PUBLIC INTERFACE

	public boolean aProblemWasFound() {
		return !this.problems.isEmpty();
	}

	public Set<String> getProblems() {
		return problems;
	}

	/**
	 * Clears all previously found problems to (potentially) trigger validation again.
	 */
	public void clear() {
		this.problems.clear();
	}

	//-----------------------------------------------------------
	// BASIC PRIVATE INTERFACE

	private void registerValidationProblem(String problem) {
		this.problems.add(problem);
	}

	// ------------------------------------------------------------------
	// VALIDATION INTERFACE

	/**
	 * Triggers the validation. Results can be inspected with the {@link #getProblems()} method.
	 */
	public void validate() {
		// clean the instance just in case so that old problems are not effective anymore
		clear();

		// first check boxes
		boolean inputBoxIncluded = false;
		for (BoxInfoServer box : experimentGraph) {
			if (box.isRegistered()) { // only inspect registered boxes
				/*
				 * A check whether the box is approved is needed. Currently, this is done
				 * in core system and only approved agents are sent to web.
				 */
				if (!experimentGraph.getSlotConnections().hasASlotConnected(box)) {
					// the box is not isolated but has no valid slot connections defined
					registerValidationProblem(String.format("At least one of '%s' boxes has no slot connections defined. Isolated boxes are not allowed.", box.getAssociatedAgent().getName()));
				} else {
					// this is only basic validation, specific option validation may be done in the switch below
					for (NewOption option : box.getAssociatedAgent().getOptions()) {
						if (!option.isValid(true)) {
							// the box is not isolated but has no valid slot connections defined
							registerValidationProblem(String.format("At least one of '%s' boxes has invalid value in the '%s' option.", box.getAssociatedAgent().getName(), option.getName()));
						}
					}
					switch (box.getBoxType()) {
					case INPUT:
						inputBoxIncluded = true;
						if (box.getAssociatedAgent().getName().equals("FileInput")) {
							NewOption optionToCheck = box.getAssociatedAgent().getOptions().fetchOptionByName("fileURI");
							String value = (String) optionToCheck.toSingleValue().getCurrentValue().hackValue();
							if (value.equals("inputFile.ARFF")) {
								registerValidationProblem("At least one of 'FileInput' boxes has the invalid default value set in the 'fileURI' option.");
							}
						}
						validateBoxSlot(box, SlotDirection.OUTPUT, CoreConstant.SlotContent.FILE_DATA.getSlotName()); // potentially duplicate check but it is needed
						break;
					case CHOOSE:
						break;
					case COMPOSITE:
						break;
					case COMPUTE:
						validateBoxSlot(box, SlotDirection.INPUT, CoreConstant.SlotContent.TRAINING_DATA.getSlotName()); // potentially duplicate check but it is needed
						validateBoxSlot(box, SlotDirection.INPUT, CoreConstant.SlotContent.TESTING_DATA.getSlotName()); // potentially duplicate check but it is needed
						break;
					case MISC:
						break;
					case OPTION:
						break;
					case OUTPUT:
						validateBoxSlot(box, SlotDirection.INPUT, CoreConstant.SlotContent.FILE_DATA.getSlotName()); // potentially duplicate check but it is needed
						break;
					case PROCESS_DATA:
						break;
					case SEARCH:
						break;
					default:
						throw new IllegalStateException("Unknown state: " + box.getBoxType().name());
					}
				}
			}
		}

		if (!inputBoxIncluded) {
			registerValidationProblem("The experiment has no registered input boxes. This error may be the result of previous errors.");
		}

		// then check edges
		/*
		for(BoxInfoServer boxFrom : experimentGraph) {
			for(BoxInfoServer boxTo : experimentGraph.getFromNeighbours(boxFrom.getID())) {
				if(boxFrom.isRegistered() != boxTo.isRegistered()) {
					registerValidationProblem(String.format("At least one of '%s' boxes doesn't have the '%s' slot connected."));
				}
			}
		}
		*/
	}

	private void validateBoxSlot(BoxInfoServer box, SlotDirection direction, String slotName) {
		Slot slotToCheck;
		if (direction == SlotDirection.INPUT) {
			slotToCheck = box.getAssociatedAgent().fetchInputSlotByName(slotName);
		} else {
			slotToCheck = box.getAssociatedAgent().fetchOutputSlotByName(slotName);
		}
		if (!experimentGraph.getSlotConnections().isSlotConnectedToAValidEndpoint(slotToCheck)) {
			registerValidationProblem(String.format("At least one of '%s' boxes doesn't have the '%s' slot connected.", box.getAssociatedAgent().getName(), slotName));
		}
	}
}