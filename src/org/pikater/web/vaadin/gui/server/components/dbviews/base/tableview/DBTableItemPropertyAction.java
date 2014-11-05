package org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.NamedActionDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

/**
 * @author SkyCrawl
 * @see {@link DBTableContainer#getProperty(DBTableContainer, ITableColumn, AbstractTableRowDBView, org.pikater.shared.database.views.base.values.AbstractDBViewValue)
 *      DBTableContainer#getProperty()}
 */
public class DBTableItemPropertyAction implements Property<Button> {
	private static final long serialVersionUID = -4326743856484935440L;

	private final Button btn;

	public DBTableItemPropertyAction(final DBTableContainer container,
			final ITableColumn column, final AbstractTableRowDBView row,
			final NamedActionDBViewValue valueWrapper) {
		this.btn = new Button(valueWrapper.getValue(),
				new Button.ClickListener() {
					private static final long serialVersionUID = 1829748841851811252L;

					@Override
					public void buttonClick(ClickEvent event) {
						container.getViewRoot().approveAction(column, row,
								new Runnable() {
									@Override
									public void run() {
										valueWrapper.executeAction();
										if (container.getParentTable()
												.isImmediate()) {
											valueWrapper.commit(row);
										}
										btn.setEnabled(valueWrapper.isEnabled());
									}
								});
					}
				});
		this.btn.setEnabled(valueWrapper.isEnabled());
	}

	@Override
	public Button getValue() {
		return btn;
	}

	@Override
	public void setValue(Button newValue)
			throws ReadOnlyException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends Button> getType() {
		return Button.class;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void setReadOnly(boolean newStatus) {
		throw new UnsupportedOperationException();
	}
}