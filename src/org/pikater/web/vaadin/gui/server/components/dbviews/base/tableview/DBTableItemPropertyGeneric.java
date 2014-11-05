package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;

import com.vaadin.data.Property;

/**
 * @author SkyCrawl
 * @see {@link DBTableContainer#getProperty(DBTableContainer, ITableColumn, AbstractTableRowDBView, org.pikater.shared.database.views.base.values.AbstractDBViewValue)
 *      DBTableContainer#getProperty()}
 */
public class DBTableItemPropertyGeneric<T extends Object> implements
		Property<T> {
	private static final long serialVersionUID = -2142093421893500620L;

	private final DBTableContainer container;
	private final AbstractTableRowDBView row;
	private final ITableColumn column;
	private final AbstractDBViewValue<T> valueWrapper;

	public DBTableItemPropertyGeneric(DBTableContainer container,
			AbstractTableRowDBView row, ITableColumn column,
			AbstractDBViewValue<T> valueWrapper) {
		this.container = container;
		this.row = row;
		this.column = column;
		this.valueWrapper = valueWrapper;

		if (getValue() != null) {
			// compatibility check
			Class<?> valueTypeClass = getValue().getClass();
			Class<?> viewBindingTypeClass = getType();
			if (!valueTypeClass.equals(viewBindingTypeClass)) {
				throw new ClassCastException(
						String.format(
								"Incorrect type binding detected. '%s' suggests '%s' but '%s' returns '%s'",
								column.getClass().getName(),
								viewBindingTypeClass.getName(), valueWrapper
										.getClass().getName(), valueTypeClass
										.getName()));
			}
		}
	}

	@Override
	public T getValue() {
		return (T) valueWrapper.getValue();
	}

	@Override
	public void setValue(T newValue)
			throws ReadOnlyException {
		valueWrapper.setValue(newValue);
		if (container.getParentTable().isImmediate()) {
			valueWrapper.commit(row);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getType() {
		return (Class<T>) container.getType(column);
	}

	@Override
	public boolean isReadOnly() {
		return valueWrapper.isReadOnly();
	}

	@Override
	public void setReadOnly(boolean newStatus) {
		throw new UnsupportedOperationException();
	}
}