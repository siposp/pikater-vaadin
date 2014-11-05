package org.pikater.web.vaadin.gui.server.components.dbviews.base;

import java.util.List;

import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTable;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTableLayout;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogResultPreparer;

import com.vaadin.ui.Label;

/**
 * <p>A special version of {@link DBTable} that is designed
 * to be used as a "row picker". By the user selection
 * a row, it is "picked" for some further processing.</p>
 * 
 * <p>Especially customized to be used with dialogs.</p>
 * 
 * @author SkyCrawl
 */
public class TableRowPicker extends DBTableLayout implements IDialogResultPreparer {
	private static final long serialVersionUID = 9055067769093710286L;

	public TableRowPicker(String caption) {
		setSizeUndefined();
		setReadOnly(true);
		getTable().setMultiSelect(false);
		addComponentAsFirst(new Label(caption));
	}

	@Override
	public boolean isResultReadyToBeHandled() {
		return getTable().isARowSelected();
	}

	@Override
	public void addArgs(List<Object> arguments) {
		arguments.add(getTable().getViewsOfSelectedRows()[0]);
	}
}