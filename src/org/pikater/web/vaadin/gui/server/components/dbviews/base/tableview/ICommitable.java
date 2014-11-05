package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

/**
 * @author SkyCrawl
 */
public interface ICommitable {
	/**
	 * Commits changes to database.
	 */
	void commitToDB();
}