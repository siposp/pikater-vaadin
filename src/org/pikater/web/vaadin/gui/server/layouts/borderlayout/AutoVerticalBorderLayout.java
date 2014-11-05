package org.pikater.web.vaadin.gui.server.layouts.borderlayout;

import org.pikater.web.vaadin.gui.client.extensions.AutoVerticalBorderLayoutExtensionClientRpc;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Border;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Column;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Row;
import org.pikater.web.vaadin.gui.shared.borderlayout.Dimension;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

/**
 * <p>Defines 5 areas to put arbitrary components inside. Looks like this:</br>
 *  ___________________________________</br>
 * |_________________N_________________|</br>
 * |__W__|___________C__________|___E__|</br>
 * |_________________S_________________|</br>
 * </p>
 * 
 * <p>Shortcuts stand for "north", "west", "center", "east" and "south".</p>
 * 
 * @author SkyCrawl
 */
@StyleSheet("autoVerticalBorderLayout.css")
public class AutoVerticalBorderLayout extends CustomLayout implements AutoVerticalBorderLayoutExtensionClientRpc {
	private static final long serialVersionUID = 2971241997257911489L;

	private final AutoVerticalBorderLayoutExtension extension;

	public AutoVerticalBorderLayout() {
		super("autoVerticalBorderLayout");

		this.extension = new AutoVerticalBorderLayoutExtension();
		this.extension.extend(this);
	}

	/**
	 * Set content to the given area.
	 */
	public void setComponent(Border border, AbstractComponent component) {
		if (component == null) {
			throw new NullPointerException("The new component can not be null.");
		}
		addComponent(component, border.name());
	}

	/**
	 * Get content of the given area.
	 */
	public Component getComponent(Border border) {
		return getComponent(border.name());
	}

	//------------------------------------------------------------------
	// CLIENT COMMANDS FORWARDED

	@Override
	public void setRowHeight(Row row, Dimension dimension) {
		extension.getClientRPC().setRowHeight(row, dimension);
	}

	@Override
	public void setColumnWidth(Column designatedColumn, Dimension dimension) {
		extension.getClientRPC().setColumnWidth(designatedColumn, dimension);
	}

	@Override
	public void addRowStyleName(Row row, String styleName) {
		extension.getClientRPC().addRowStyleName(row, styleName);
	}

	@Override
	public void removeRowStyleName(Row row, String styleName) {
		extension.getClientRPC().removeRowStyleName(row, styleName);
	}

	@Override
	public void addColumnStyleName(Column column, String styleName) {
		extension.getClientRPC().addColumnStyleName(column, styleName);
	}

	@Override
	public void removeColumnStyleName(Column column, String styleName) {
		extension.getClientRPC().removeColumnStyleName(column, styleName);
	}

	@Override
	public void setRowInvisible(Row affectedRow, Row rowToTakeUpTheAffectedRowsSpace) {
		extension.getClientRPC().setRowInvisible(affectedRow, rowToTakeUpTheAffectedRowsSpace);
	}

	@Override
	public void setRowVisible(Row affectedRow) {
		extension.getClientRPC().setRowVisible(affectedRow);
	}

	@Override
	public void setColumnInvisible(Column affectedColumn, Column columnToTakeUpTheAffectedColumnsSpace) {
		extension.getClientRPC().setColumnInvisible(affectedColumn, columnToTakeUpTheAffectedColumnsSpace);
	}

	@Override
	public void setColumnVisible(Column affectedColumn) {
		extension.getClientRPC().setColumnVisible(affectedColumn);
	}

	@Override
	public void setBorderSpacing(int pixels) {
		extension.getClientRPC().setBorderSpacing(pixels);
	}

	@Override
	public void setCellSpacing(int pixels) {
		extension.getClientRPC().setCellSpacing(pixels);
	}

	@Override
	public void setCellPadding(int pixels) {
		extension.getClientRPC().setCellPadding(pixels);
	}

	@Override
	public void setFixedLayout(Dimension westDimension, Dimension centerDimension, Dimension eastDimension) {
		extension.getClientRPC().setFixedLayout(westDimension, centerDimension, eastDimension);
	}
}
