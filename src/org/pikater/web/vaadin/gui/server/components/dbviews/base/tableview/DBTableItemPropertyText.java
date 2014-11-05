package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

import org.pikater.shared.database.views.base.values.StringDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

/**
 * @author SkyCrawl
 * @see {@link DBTableContainer#getProperty(DBTableContainer, ITableColumn, AbstractTableRowDBView, org.pikater.shared.database.views.base.values.AbstractDBViewValue)
 *      DBTableContainer#getProperty()}
 */
public class DBTableItemPropertyText implements Property<TextField> {
	private static final long serialVersionUID = -1889090215704801361L;

	private final TextField textField;
	private final boolean readOnly;

	public DBTableItemPropertyText(final DBTable parentTable,
			final AbstractTableRowDBView row,
			final StringDBViewValue valueWrapper) {
		this.readOnly = valueWrapper.isReadOnly();

		this.textField = new TextField(null, valueWrapper.getValue());
		this.textField.setWidth("100%");
		this.textField.setImmediate(true);
		this.textField.setTextChangeTimeout(500); // necessary to avoid too big
													// DB traffic
		this.textField.setReadOnly(isReadOnly());
		if (!isReadOnly()) {
			this.textField.addValueChangeListener(new ValueChangeListener() // TODO:
																			// use
																			// a
																			// textchangelistener
																			// instead?
					{
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
	public TextField getValue() {
		return textField;
	}

	@Override
	public void setValue(TextField newValue)
			throws com.vaadin.data.Property.ReadOnlyException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends TextField> getType() {
		return TextField.class;
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