package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

import org.pikater.shared.database.views.base.values.BooleanDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;

/**
 * @author SkyCrawl
 * @see {@link DBTableContainer#getProperty(DBTableContainer, ITableColumn, AbstractTableRowDBView, org.pikater.shared.database.views.base.values.AbstractDBViewValue)
 *      DBTableContainer#getProperty()}
 */
public class DBTableItemPropertyCheck implements Property<CheckBox> {
	private static final long serialVersionUID = 8460699686386766346L;

	private final CheckBox checkBox;
	private final boolean readOnly;

	public DBTableItemPropertyCheck(final DBTable parentTable,
			final AbstractTableRowDBView row,
			final BooleanDBViewValue valueWrapper) {
		this.readOnly = valueWrapper.isReadOnly();

		this.checkBox = new CheckBox(null, valueWrapper.getValue());
		this.checkBox.setImmediate(true);
		this.checkBox.setReadOnly(isReadOnly());
		if (!isReadOnly()) {
			this.checkBox.addValueChangeListener(new ValueChangeListener() {
				private static final long serialVersionUID = -7967404862064544415L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					valueWrapper.setValue((Boolean) event.getProperty()
							.getValue());
					if (parentTable.isImmediate()) {
						valueWrapper.commit(row);
					}
				}
			});
		}
	}

	@Override
	public CheckBox getValue() {
		return checkBox;
	}

	@Override
	public void setValue(CheckBox newValue) throws ReadOnlyException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends CheckBox> getType() {
		return CheckBox.class;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly(boolean newStatus) {
		throw new UnsupportedOperationException();
	}
}