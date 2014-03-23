package pikater.utility.pikaterDatabase.tables.experiments;

import java.util.ArrayList;

import pikater.utility.boxTypes.Box;
import pikater.utility.pikaterDatabase.tables.experiments.parameters.UniversalParameter;
import pikater.utility.pikaterDatabase.tables.experiments.slots.ParameterConsumerSlot;
import pikater.utility.pikaterDatabase.tables.experiments.slots.UniversalSlot;

public class UniversalDataConsumer extends UniversalDataFlow {

	private ArrayList<UniversalSlot> inputSlots = new ArrayList<UniversalSlot>();


	public UniversalSlot getInputSlotByName(String name) {
		
		for (UniversalSlot slotI : this.inputSlots) {
			if (slotI.getSlotName().equals(name)) {
				return slotI;
			}
		}
		return null;
	}

	public void setBoxDescription(Box boxDescription) {
		super.boxDescription = boxDescription;

		//TODO: better to create copy of slots and parameters
		for ( UniversalParameter parameterI : boxDescription.getParameters() ) {
			
			ParameterConsumerSlot paramSlot = new ParameterConsumerSlot();
			paramSlot.setParameter(parameterI);
			inputSlots.add(paramSlot);
		}

		for ( UniversalSlot slotI : boxDescription.getInputSlots() ) {
			inputSlots.add(slotI);
		}
		
		
	}

	
	public ArrayList<UniversalSlot> getInputSlots() {
		return inputSlots;
	}
	public void setInputSlots(ArrayList<UniversalSlot> inputSlots) {
		this.inputSlots = inputSlots;
	}
	
}
