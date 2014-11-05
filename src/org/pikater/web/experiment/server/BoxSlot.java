package org.pikater.web.experiment.server;

import org.pikater.core.CoreConstant.SlotDirection;
import org.pikater.core.ontology.subtrees.agentinfo.Slot;

/**
 * A wrapper class for (oriented) experiment graph endpoint - a box, a slot and
 * a direction (INPUT/OUTPUT).
 * 
 * @author SkyCrawl
 */
public class BoxSlot {
	private final BoxInfoServer parentBox;
	private final Slot childSlot;
	private final SlotDirection childSlotsType;

	public BoxSlot(BoxInfoServer parentBox, Slot childSlot) {
		this.parentBox = parentBox;
		if (parentBox.getAssociatedAgent().getInputSlots().contains(childSlot)) {
			this.childSlotsType = SlotDirection.INPUT;
		} else if (parentBox.getAssociatedAgent().getOutputSlots().contains(childSlot)) {
			this.childSlotsType = SlotDirection.OUTPUT;
		} else {
			this.childSlotsType = null;
		}
		this.childSlot = childSlot;
	}

	public BoxInfoServer getParentBox() {
		return parentBox;
	}

	public Slot getChildSlot() {
		return childSlot;
	}

	public SlotDirection getChildSlotsType() {
		return childSlotsType;
	}

	public boolean isFromEndpoint() {
		return childSlotsType == SlotDirection.OUTPUT;
	}

	public boolean isValid() {
		if ((childSlotsType == null) || (parentBox == null) || (childSlot == null)) {
			return false;
		}
		// parent-child relationship between the box and slot is ensured by the constructor and slot type null check above
		return true;
	}

	//---------------------------------------------------------
	// COMPARISON INTERFACE - GENERATED WITH ECLIPSE

	/*
	 * NOT NEEDED AFTER ALL BUT KEPT FOR NEEDY TIMES
	 * 1) Equals comparisons were removed because they're unwanted and could
	 * potentially cause bugs. Instance comparison is sufficient for us 
	 * in this case.
	 * 2) Since we use validation as null checks before using instances
	 * of this class in collections, we don't have to do check again
	 * in here.
	 */

	/*
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoxSlot other = (BoxSlot) obj;
		if ((childSlot != other.childSlot) ||  (childSlotsType != other.childSlotsType) || (parentBox != other.parentBox))
		{
			return false;
		}
		return true;
	}
	*/
}