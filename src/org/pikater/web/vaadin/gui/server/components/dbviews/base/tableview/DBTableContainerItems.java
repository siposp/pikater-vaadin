package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.pikater.shared.database.views.base.query.QueryResult;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.util.SimpleIDGenerator;

/**
 * The lowest-level implementation of a view provider for {@link DBTable}. Works
 * directly with the database.
 * 
 * @author SkyCrawl
 */
public class DBTableContainerItems implements ICommitable {
	/**
	 * Vaadin tables work with IDs, so we have to have all rows mapped.
	 * 
	 * @see {@link #getRowIDs()}
	 * @see {@link #getRow(Object)}
	 */
	private final Map<Integer, DBTableItem> currentlyViewedRows;

	/**
	 * You never know when it might come in handy.
	 * 
	 * @see {@link #getAllItemsCount()}
	 */
	private QueryResult lastQueryResult;

	public DBTableContainerItems() {
		this.currentlyViewedRows = new HashMap<Integer, DBTableItem>();
	}

	@Override
	public void commitToDB() {
		for (DBTableItem row : currentlyViewedRows.values()) {
			row.commitToDB();
		}
	}

	// -----------------------------------------------
	// MAIN CONTAINER INTERFACE - SHOULD BE SELF EXPLANATORY

	/**
	 * Look at the table, define a query with its help, ask database for the new
	 * rows, store them and wait until the table picks them up.
	 */
	public void loadRows(DBTableContainer container, QueryResult queryResult) {
		// first, clear the previous rows
		this.currentlyViewedRows.clear();

		// then generate the new ones
		SimpleIDGenerator ids = new SimpleIDGenerator();
		for (AbstractTableRowDBView row : queryResult.getConstrainedResults()) {
			this.currentlyViewedRows.put(ids.getAndIncrement(),
					new DBTableItem(container, row));
		}

		// and finally:
		lastQueryResult = queryResult;
	}

	public Set<Integer> getRowIDs() {
		return this.currentlyViewedRows.keySet();
	}

	public DBTableItem getRow(Object rowID) {
		return this.currentlyViewedRows.get(rowID);
	}

	public int getAllItemsCount() {
		return lastQueryResult.getAllResultsCount();
	}

	// -----------------------------------------------
	// SORT INTERFACE - REFER TO THE CONTAINER.SORTABLE INTERFACE JAVADOC WHEN
	// IMPLEMENTING

	/*
	 * Although these methods are not really used by our tables (we have support
	 * for native sorting by queries), they are set to be "sortable" and thus
	 * require having a sortable container at their disposal.
	 */

	public Object getFirstItemID() {
		if (!currentlyViewedRows.isEmpty()) {
			return SimpleIDGenerator.getFirstID();
		} else {
			return null;
		}
	}

	public Object getNextItemID(Object itemId) {
		if (isLastID(itemId)) {
			return null;
		} else {
			Integer integer = (Integer) itemId;
			return integer + 1;
		}
	}

	public Object getPrevItemID(Object itemId) {
		if (isFirstID(itemId)) {
			return null;
		} else {
			Integer integer = (Integer) itemId;
			return integer - 1;
		}
	}

	public Object getLastItemID() {
		if (!currentlyViewedRows.isEmpty()) {
			return currentlyViewedRows.size() - 1;
		} else {
			return null;
		}
	}

	public boolean isFirstID(Object itemId) {
		if (itemId == null) {
			return false;
		} else {
			return itemId.equals(SimpleIDGenerator.getFirstID());
		}
	}

	public boolean isLastID(Object itemId) {
		if (itemId == null) {
			return false;
		} else {
			return itemId.equals(getLastItemID());
		}
	}
}
