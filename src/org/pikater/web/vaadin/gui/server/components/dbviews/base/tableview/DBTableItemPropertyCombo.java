package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.RepresentativeDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;

import com.vaadin.data.Property;
import com.vaadin.ui.ComboBox;

/**
 * @author SkyCrawl
 * @see {@link DBTableContainer#getProperty(DBTableContainer, ITableColumn, AbstractTableRowDBView, org.pikater.shared.database.views.base.values.AbstractDBViewValue)
 *      DBTableContainer#getProperty()}
 */
public class DBTableItemPropertyCombo implements Property<ComboBox> {
	private static final long serialVersionUID = -5919644785774086999L;

	private final ComboBox comboBox;
	private final boolean readOnly;

	public DBTableItemPropertyCombo(final DBTable parentTable,
			final AbstractTableRowDBView row,
			final RepresentativeDBViewValue valueWrapper) {
		this.readOnly = valueWrapper.isReadOnly();

		this.comboBox = new ComboBox(null, valueWrapper.getValues());
		this.comboBox.setWidth("100%");
		this.comboBox.setValue(valueWrapper.getValue());
		this.comboBox.setNullSelectionAllowed(false);
		this.comboBox.setTextInputAllowed(false);
		this.comboBox.setNewItemsAllowed(false);
		this.comboBox.setImmediate(true);
		this.comboBox.setReadOnly(isReadOnly());
		if (!isReadOnly()) {
			this.comboBox.addValueChangeListener(new ValueChangeListener() {
				private static final long serialVersionUID = -6175606221977226773L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					valueWrapper.setValue((String) event.getProperty()
							.getValue());
					if (parentTable.isImmediate()) {
						valueWrapper.commit(row);
					}
				}
			});
		}
	}

	@Override
	public ComboBox getValue() {
		return comboBox;
	}

	@Override
	public void setValue(ComboBox newValue)
			throws com.vaadin.data.Property.ReadOnlyException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends ComboBox> getType() {
		return ComboBox.class;
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