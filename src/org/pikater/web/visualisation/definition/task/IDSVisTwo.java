package org.pikater.web.visualisation.definition.task;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.visualisation.definition.AttrComparisons;

/**
 * <p>An interface helping to maintain application consistency when it comes
 * triggering/handling dataset comparison.</p>
 * 
 * <p>Anything that aspires to execute dataset comparison should
 * implement this interface.</p>
 * 
 * @author SkyCrawl
 */
public interface IDSVisTwo {
	/**
	 * Triggers dataset comparison with the given parameters.
	 * @throws Exception
	 */
	void visualizeDatasetComparison(JPADataSetLO dataset1, JPADataSetLO dataset2, AttrComparisons comparisonList) throws Exception;
}
