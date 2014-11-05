package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

import java.util.Collection;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.base.values.BooleanDBViewValue;
import org.pikater.shared.database.views.base.values.NamedActionDBViewValue;
import org.pikater.shared.database.views.base.values.RepresentativeDBViewValue;
import org.pikater.shared.database.views.base.values.StringDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableDBView;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.AbstractDBViewRoot;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

/**
 * Low-level {@link DBTable} view provider. Asks database for the currently
 * viewed data and then passes them to the table.
 * 
 * @author SkyCrawl
 */
public class DBTableContainer implements Container.Sortable, ICommitable {
	private static final long serialVersionUID = 9197656251784900256L;

	private final DBTable parentTable;
	private final DBTableContainerItems currentlyViewedRows;

	public DBTableContainer(DBTable parentTable) {
		this.parentTable = parentTable;
		this.currentlyViewedRows = new DBTableContainerItems();
	}

	// -----------------------------------------------------------
	// FIRST INHERITED CONTAINER-SPECIFIC REQUIRED INTERFACE

	@Override
	public Collection<ITableColumn> getContainerPropertyIds() {
		return viewRoot.getUnderlyingDBView().getAllColumns();
	}

	@Override
	public Class<? extends Object> getType(Object propertyId) {
		return getPresentationType(this, (ITableColumn) propertyId);
	}

	/**
	 * This method is called whenever table refresh occurs. So, we have to
	 * rebuild the internal index from scratch.
	 */
	@Override
	public Collection<?> getItemIds() {
		currentlyViewedRows.loadRows(this, viewRoot.getUnderlyingDBView()
				.queryRows(parentTable.getQuery()));
		return currentlyViewedRows.getRowIDs();
	}

	@Override
	public int size() {
		return currentlyViewedRows.getRowIDs().size();
	}

	@Override
	public boolean containsId(Object itemId) {
		return currentlyViewedRows.getRowIDs().contains(itemId);
	}

	@Override
	public DBTableItem getItem(Object itemId) {
		return currentlyViewedRows.getRow(itemId);
	}

	@Override
	public Property<? extends Object> getContainerProperty(Object itemId,
			Object propertyId) {
		return getItem(itemId).getItemProperty(propertyId);
	}

	// -----------------------------------------------------------
	// THEN CONTAINER-SPECIFIC INHERITED OPTIONAL INTERFACE

	/*
	 * If you want these methods, override them in a child implementation.
	 */

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	// ------------------------------------------------------------------------------------------------
	// BANNED INHERITED INTERFACE

	/*
	 * We won't be adding or removing table columns from the GUI.
	 */

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	// -----------------------------------------------------------
	// CONTAINER-SPECIFIC SORTABLE REQUIRED INTERFACE

	/*
	 * Although these methods are not really used by our tables (we have support
	 * for native sorting by queries), they are set to be "sortable" and thus
	 * require having a sortable container at their disposal.
	 */

	public Collection<?> getSortableContainerPropertyIds() {
		return viewRoot.getUnderlyingDBView().getSortableColumns();
	}

	public Object firstItemId() {
		return currentlyViewedRows.getFirstItemID();
	}

	public Object nextItemId(Object itemId) {
		return currentlyViewedRows.getNextItemID(itemId);
	}

	public Object prevItemId(Object itemId) {
		return currentlyViewedRows.getPrevItemID(itemId);
	}

	public Object lastItemId() {
		return currentlyViewedRows.getLastItemID();
	}

	public boolean isFirstId(Object itemId) {
		return currentlyViewedRows.isFirstID(itemId);
	}

	public boolean isLastId(Object itemId) {
		return currentlyViewedRows.isLastID(itemId);
	}

	public void sort(Object[] propertyId, boolean[] ascending) {
		/*
		 * This is executed AFTER rebuilding the row cache and we need the
		 * sorting to be native (database query) => do nothing in here.
		 */
	}

	// -----------------------------------------------------------
	// CONTAINER-SPECIFIC SORTABLE OPTIONAL INTERFACE

	public Object addItemAfter(Object previousItemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public Item addItemAfter(Object previousItemId, Object newItemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	// -----------------------------------------------------------
	// VIEW TYPE TO PRESENTATION TYPE BINDING METHODS

	/**
	 * Return content class for the given column/cell. The table is homogenous
	 * and descendants of {@link AbstractTableDBView} directly require/imply it.
	 */
	public static Class<? extends Object> getPresentationType(
			DBTableContainer container, ITableColumn column) {
		switch (column.getColumnType()) {
			case BOOLEAN:
				return CheckBox.class;

			case STRING:
				return TextField.class;

			case REPRESENTATIVE:
				return ComboBox.class;

			case NAMED_ACTION:
				return Button.class;

			default:
				throw new IllegalStateException("Unknown state: "
						+ column.getColumnType().name());
		}
	}

	/**
	 * Vaadin tables work with "properties", instead of working directly with
	 * values. We have created our own, with a little extra nice features.
	 */
	public static Property<? extends Object> getProperty(
			DBTableContainer container, ITableColumn column,
			AbstractTableRowDBView row,
			AbstractDBViewValue<? extends Object> value) {
		switch (column.getColumnType()) {
			case BOOLEAN:
				DBTableItemPropertyCheck newProperty1 = new DBTableItemPropertyCheck(
						container.getParentTable(), row,
						(BooleanDBViewValue) value);
				container.getViewRoot().onCellCreate(column, value,
						newProperty1.getValue());
				return newProperty1;

			case STRING:
				DBTableItemPropertyText newProperty2 = new DBTableItemPropertyText(
						container.getParentTable(), row,
						(StringDBViewValue) value);
				container.getViewRoot().onCellCreate(column, value,
						newProperty2.getValue());
				return newProperty2;

			case REPRESENTATIVE:
				DBTableItemPropertyCombo newProperty3 = new DBTableItemPropertyCombo(
						container.getParentTable(), row,
						(RepresentativeDBViewValue) value);
				container.getViewRoot().onCellCreate(column, value,
						newProperty3.getValue());
				return newProperty3;

			case NAMED_ACTION:
				DBTableItemPropertyAction newProperty4 = new DBTableItemPropertyAction(
						container, column, row, (NamedActionDBViewValue) value);
				container.getViewRoot().onCellCreate(column, value,
						newProperty4.getValue());
				return newProperty4;

			default:
				throw new IllegalStateException("Unknown state: "
						+ column.getColumnType().name());
		}
	}

	// -----------------------------------------------------------
	// SPECIAL DATABASE INTERFACE

	@Override
	public void commitToDB() {
		currentlyViewedRows.commitToDB();
	}

	// -----------------------------------------------------------
	// AND FINALLY, SOME ADDED VALUE

	/**
	 * A special wrapper class bound to a certain view type providing display
	 * configuration for the table - column widths etc.
	 */
	private AbstractDBViewRoot<? extends AbstractTableDBView> viewRoot;

	public AbstractDBViewRoot<? extends AbstractTableDBView> getViewRoot() {
		return viewRoot;
	}

	public void setViewRoot(
			AbstractDBViewRoot<? extends AbstractTableDBView> viewRoot) {
		this.viewRoot = viewRoot;
	}

	public DBTable getParentTable() {
		return parentTable;
	}

	/**
	 * Return the total number of rows for the currently viewed database table.
	 * This is needed for table paging to work.
	 */
	public int getUnconstrainedQueryResultsCount() {
		return currentlyViewedRows.getAllItemsCount();
	}

	/*
	 * NOT NEEDED OR USED AT THE MOMENT protected void
	 * batchSetValues(Set<Integer> ids, Header header, String newValue) { //
	 * TODO: finish if(header.supportsBatchSet()) { for(Integer itemID : ids) {
	 * //getItem(itemID).serverInfoProperties.setValueForProperty(header,
	 * newValue); } } else { throw new UnsupportedOperationException(); } }
	 */
}
