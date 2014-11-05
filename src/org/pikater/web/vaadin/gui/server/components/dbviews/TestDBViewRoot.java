package org.pikater.web.vaadin.gui.server.components.dbviews;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.test.TestTableDBView;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.AbstractDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;

import com.vaadin.ui.AbstractComponent;

/**
 * Configuration for {@link DBTable DB tables} made for development
 * tests.
 * 
 * @author SkyCrawl
 */
public class TestDBViewRoot extends AbstractDBViewRoot<TestTableDBView> {
	public TestDBViewRoot(TestTableDBView underlyingDBView) {
		super(underlyingDBView);
	}

	@Override
	public int getColumnSize(ITableColumn column) {
		return 100;
	}

	@Override
	public ITableColumn getExpandColumn() {
		return null;
	}

	@Override
	public void onCellCreate(ITableColumn column, AbstractDBViewValue<?> value, AbstractComponent component) {
	}

	@Override
	public void approveAction(ITableColumn column, AbstractTableRowDBView row, Runnable action) {
	}
}