package org.pikater.web.vaadin.gui.server.components.dbviews;

import org.pikater.shared.database.views.base.ITableColumn;
import org.pikater.shared.database.views.base.values.AbstractDBViewValue;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.datasets.metadata.CategoricalMetaDataTableDBView;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.AbstractDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;

import com.vaadin.ui.AbstractComponent;

/**
 * Configuration for {@link DBTable DB tables} working with categorical metadata.
 * 
 * @author SkyCrawl
 */
public class CategoricalMetadataDBViewRoot extends AbstractDBViewRoot<CategoricalMetaDataTableDBView> {
	public CategoricalMetadataDBViewRoot(CategoricalMetaDataTableDBView view) {
		super(view);
	}

	@Override
	public int getColumnSize(ITableColumn column) {
		CategoricalMetaDataTableDBView.Column specificColumn = (CategoricalMetaDataTableDBView.Column) column;
		switch (specificColumn) {
		case NAME:
			return 150;
		case IS_TARGET:
			return 75;
		case CATEGORY_COUNT:
			return 100;
		case RATIO_OF_MISSING_VALUES:
			return 125;
		case CLASS_ENTROPY:
			return 75;
		case ENTROPY:
			return 115;
		default:
			throw new IllegalStateException(String.format("No sizing information found for column '%s'", specificColumn.name()));
		}
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