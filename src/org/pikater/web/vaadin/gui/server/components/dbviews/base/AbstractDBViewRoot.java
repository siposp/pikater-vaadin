package org.pikater.web.vaadin.gui.server.components.dbviews.base;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableDBView;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;

import com.vaadin.ui.AbstractComponent;

/**
 * A wrapper for {@link AbstractTableDBView DB views} used in 
 * {@link DBTable DB tables}. Provides GUI configuration for
 * the view like column sizes etc.
 * 
 * @author SkyCrawl
 *
 * @param <T> The view type.
 */
public abstract class AbstractDBViewRoot<T extends AbstractTableDBView> {
	private final T underlyingDBView;
	private DBTable parentTable;

	public AbstractDBViewRoot(T underlyingDBView) {
		this.underlyingDBView = underlyingDBView;
	}

	public T getUnderlyingDBView() {
		return underlyingDBView;
	}

	public void setParentTable(DBTable parentTable) {
		this.parentTable = parentTable;
	}

	public DBTable getParentTable() {
		if (parentTable != null) {
			return parentTable;
		} else {
			throw new IllegalStateException("No table has been associated with this view. Check {@link DBTable}.");
		}
	}

	public abstract int getColumnSize(ITableColumn column);

	/**
	 * Gets the column to take up any remaining width of the table,
	 * after it has been distributed to columns as specified by
	 * {@link #getColumnSize(ITableColumn)}.
	 */
	public abstract ITableColumn getExpandColumn();

	/**
	 * Post-processing for table cells, after they have been initialized.
	 */
	public abstract void onCellCreate(ITableColumn column, AbstractDBViewValue<?> value, AbstractComponent component);

	/**
	 * This method is called after the user clicks a button within a cell 
	 * in {@link DBTable}. It is a top-level check whether the button's
	 * action handler may be executed for the given row. 
	 */
	public abstract void approveAction(ITableColumn column, AbstractTableRowDBView row, Runnable action);
}
