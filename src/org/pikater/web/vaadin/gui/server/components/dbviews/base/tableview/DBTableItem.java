package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * An item for our {@link DBTable database tables} representing and
 * encapsulating a single row. The row is defined by {@link #rowView}.
 * 
 * @author SkyCrawl
 */
public class DBTableItem implements Item, ICommitable {
	private static final long serialVersionUID = -8884535836440270245L;

	private final DBTableContainer container;
	private final AbstractTableRowDBView rowView;
	private final Map<ITableColumn, Property<? extends Object>> columnToValue;

	public DBTableItem(DBTableContainer container,
			AbstractTableRowDBView rowView) {
		this.container = container;
		this.rowView = rowView;

		this.columnToValue = new HashMap<ITableColumn, Property<? extends Object>>();
		for (ITableColumn column : getItemPropertyIds()) {
			this.columnToValue.put(column,
					DBTableContainer.getProperty(container, column, rowView,
							rowView.getValueWrapper(column)));
		}
	}

	@Override
	public void commitToDB() {
		rowView.commit();
	}

	@Override
	public Collection<ITableColumn> getItemPropertyIds() {
		/*
		 * By default, all rows have a value for every table column since our
		 * tables are homogenous.
		 */
		return container.getContainerPropertyIds();
	}

	@Override
	public Property<? extends Object> getItemProperty(Object id) {
		return columnToValue.get(id);
	}

	public AbstractTableRowDBView getRowView() {
		return rowView;
	}

	// ------------------------------------------------------------------------------------------------
	// OPTIONAL INHERITED INTERFACE

	/*
	 * We won't be adding or removing table columns from the GUI.
	 */

	@SuppressWarnings("rawtypes")
	@Override
	public boolean addItemProperty(Object id, Property property)
			throws UnsupportedOperationException {
		return false;
	}

	@Override
	public boolean removeItemProperty(Object id)
			throws UnsupportedOperationException {
		return false;
	}
}