package org.pikater.web.visualisation;

import org.pikater.web.visualisation.definition.AttrMapping;

/** 
 * @author SkyCrawl
 */
public class DatasetVisualizationValidation {
	/**
	 * Checks whether the given attribute mapping is valid for image
	 * generation. Relates to single dataset visualization.
	 */
	public static boolean isCompatible(AttrMapping attributes) {
		return true;
	}

	/**
	 * Checks whether the given attribute mappings are valid for image
	 * generation. Relates to dataset comparison. 
	 */
	public static boolean areCompatible(AttrMapping attributes1, AttrMapping attributes2) {
		return attributes1.getAttrX().isVisuallyCompatible(attributes2.getAttrX()) && attributes1.getAttrY().isVisuallyCompatible(attributes2.getAttrY())
				&& attributes1.getAttrTarget().isVisuallyCompatible(attributes2.getAttrTarget());
	}
}
