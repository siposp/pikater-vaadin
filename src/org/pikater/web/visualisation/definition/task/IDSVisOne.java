package org.pikater.web.visualisation.definition.task;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.shared.database.jpa.JPADataSetLO;

/**
 * <p>An interface helping to maintain application consistency when it comes
 * triggering/handling single dataset visualization.</p>
 * 
 * <p>Anything that aspires to execute single dataset visualization should
 * implement this interface.</p>
 * 
 * @author SkyCrawl
 */
public interface IDSVisOne {
	/**
	 * Triggers single dataset visualization with the given parameters.
	 * @throws Exception
	 */
	void visualizeDataset(JPADataSetLO dataset, JPAAttributeMetaData[] attrs, JPAAttributeMetaData attrTarget) throws Exception;
}
