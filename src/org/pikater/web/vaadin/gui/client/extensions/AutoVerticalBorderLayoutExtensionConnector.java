package org.pikater.web.vaadin.gui.client.extensions;

import java.util.HashMap;
import java.util.Map;

import org.pikater.web.vaadin.gui.server.layouts.borderlayout.AutoVerticalBorderLayout;
import org.pikater.web.vaadin.gui.server.layouts.borderlayout.AutoVerticalBorderLayoutExtension;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Column;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Row;
import org.pikater.web.vaadin.gui.shared.borderlayout.Dimension;
import org.pikater.web.vaadin.gui.shared.borderlayout.Dimension.DimensionMode;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.customlayout.CustomLayoutConnector;
import com.vaadin.shared.ui.Connect;

/** 
 * @author SkyCrawl
 * @see {@link AutoVerticalBorderLayout}
 */
@Connect(AutoVerticalBorderLayoutExtension.class)
public class AutoVerticalBorderLayoutExtensionConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = 6766120104518020715L;

	/*
	 * Keep these static definitions in sync with "WebContent/VAADIN/themes/pikater/layouts/autoVerticalBorderLayout.html". 
	 */
	private static final String firstRowStyleName = "avbl-firstRow";
	private static final String secondRowStyleName = "avbl-secondRow";
	private static final String thirdRowStyleName = "avbl-thirdRow";
	private static final String firstColumnStyleName = "avbl-firstColumn";
	private static final String secondColumnStyleName = "avbl-secondColumn";
	private static final String thirdColumnStyleName = "avbl-thirdColumn";

	/*
	 * Reference holders for important inner layout elements. 
	 */
	private Element encapsulatingTable = null;

	private Element coll1 = null;
	private Element coll2 = null;
	private Element coll3 = null;

	private Element row1 = null;
	private Element row2 = null;
	private Element row3 = null;

	private Element cell1 = null;
	private Element cell2 = null;
	private Element cell3 = null;

	/*
	 * Some helping variables.
	 */
	private final Map<Row, ItemInfo<Row>> rowInfo;
	private final Map<Column, ItemInfo<Column>> columnInfo;

	/**
	 * Constructor.
	 */
	public AutoVerticalBorderLayoutExtensionConnector() {
		this.rowInfo = new HashMap<Row, ItemInfo<Row>>();
		for (Row row : Row.values()) {
			this.rowInfo.put(row, new ItemInfo<Row>());
		}
		this.columnInfo = new HashMap<Column, ItemInfo<Column>>();
		for (Column column : Column.values()) {
			this.columnInfo.put(column, new ItemInfo<Column>());
		}

		registerRpc(AutoVerticalBorderLayoutExtensionClientRpc.class, new AutoVerticalBorderLayoutExtensionClientRpc() {
			private static final long serialVersionUID = 8760526187099873218L;

			/*
			 * GENERAL IMPORTANT NOTE:
			 * The in-built GWT style builder uses the "camelCase" format (no hyphens, underscores, spaces etc) so
			 * when using the "style.setProperty()" methods, make sure the property names are in that format.
			 * For example: CSS property "border-spacing" builds into "borderSpacing" in "camelCase".
			 */

			@Override
			public void setRowHeight(final Row designatedRow, final Dimension dimension) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						if (dimension.isSetToMax()) {
							// set everything else to auto prior to setting the desired element to max
							for (Row row : Row.values()) {
								if (row != designatedRow) {
									getElementByRow(row).getStyle().setProperty("height", new Dimension(DimensionMode.AUTO).cssDimensionString);
								}
							}
						}
						getElementByRow(designatedRow).getStyle().setProperty("height", dimension.cssDimensionString);
					}
				});
			}

			@Override
			public void setColumnWidth(final Column designatedColumn, final Dimension dimension) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						getElementByColumn(designatedColumn).getStyle().setProperty("width", dimension.cssDimensionString);
					}
				});
			}

			@Override
			public void addRowStyleName(final Row row, final String styleName) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						getElementByRow(row).addClassName(styleName);
					}
				});
			}

			@Override
			public void removeRowStyleName(final Row row, final String styleName) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						getElementByRow(row).removeClassName(styleName);
					}
				});
			}

			@Override
			public void addColumnStyleName(final Column column, final String styleName) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						getElementByColumn(column).addClassName(styleName);
					}
				});
			}

			@Override
			public void removeColumnStyleName(final Column column, final String styleName) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						getElementByColumn(column).removeClassName(styleName);
					}
				});
			}

			@Override
			public void setRowInvisible(final Row affectedRow, final Row rowToTakeUpTheAffectedRowsSpace) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						if (affectedRow != rowToTakeUpTheAffectedRowsSpace) {
							ItemInfo<Row> affectedRowInfo = rowInfo.get(affectedRow);
							ItemInfo<Row> spacerInfo = rowInfo.get(rowToTakeUpTheAffectedRowsSpace);
							if (affectedRowInfo.visible) {
								affectedRowInfo.spaceTakerForThisItem = rowToTakeUpTheAffectedRowsSpace;
								affectedRowInfo.visible = false;
								spacerInfo.span++;

								setVisibility(getElementByRow(affectedRow), false);
								getElementByRow(rowToTakeUpTheAffectedRowsSpace).setAttribute("rowspan", String.valueOf(spacerInfo.span));
							}
							// else do nothing
						} else {
							throw new IllegalArgumentException("Row to take up the available space must be distinct from " + "the row being made invisible.");
						}
					}
				});
			}

			@Override
			public void setRowVisible(final Row affectedRow) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						ItemInfo<Row> affectedRowInfo = rowInfo.get(affectedRow);
						ItemInfo<Row> spacerInfo = rowInfo.get(affectedRowInfo.spaceTakerForThisItem);
						if (!affectedRowInfo.visible) {
							spacerInfo.span--;
							getElementByRow(affectedRowInfo.spaceTakerForThisItem).setAttribute("rowspan", String.valueOf(spacerInfo.span));

							affectedRowInfo.spaceTakerForThisItem = null;
							affectedRowInfo.visible = true;

							setVisibility(getElementByRow(affectedRow), true);
						}
						// else do nothing
					}
				});
			}

			@Override
			public void setColumnInvisible(final Column affectedColumn, final Column columnToTakeUpTheAffectedColumnsSpace) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						if (affectedColumn != columnToTakeUpTheAffectedColumnsSpace) {
							ItemInfo<Column> affectedColumnInfo = columnInfo.get(affectedColumn);
							ItemInfo<Column> spacerInfo = columnInfo.get(columnToTakeUpTheAffectedColumnsSpace);
							if (affectedColumnInfo.visible) {
								affectedColumnInfo.spaceTakerForThisItem = columnToTakeUpTheAffectedColumnsSpace;
								affectedColumnInfo.visible = false;
								spacerInfo.span++;

								setVisibility(getElementByColumn(affectedColumn), false);
								getElementByColumn(columnToTakeUpTheAffectedColumnsSpace).setAttribute("colspan", String.valueOf(spacerInfo.span));
							}
							// else do nothing
						} else {
							throw new IllegalArgumentException("Column to take up the available space must be distinct from " + "the column being made invisible.");
						}
					}
				});
			}

			@Override
			public void setColumnVisible(final Column affectedColumn) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						ItemInfo<Column> affectedColumnInfo = columnInfo.get(affectedColumn);
						ItemInfo<Column> spacerInfo = columnInfo.get(affectedColumnInfo.spaceTakerForThisItem);
						if (!affectedColumnInfo.visible) {
							spacerInfo.span--;
							getElementByColumn(affectedColumnInfo.spaceTakerForThisItem).setAttribute("colspan", String.valueOf(spacerInfo.span));

							affectedColumnInfo.spaceTakerForThisItem = null;
							affectedColumnInfo.visible = true;

							setVisibility(getElementByColumn(affectedColumn), true);
						}
						// else do nothing
					}
				});

			}

			@Override
			public void setBorderSpacing(final int pixels) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						// encapsulatingTable.getStyle().setProperty("border-spacing", String.valueOf(pixels) + "px"); // hyphen format - GWT complains
						encapsulatingTable.getStyle().setProperty("borderSpacing", String.valueOf(pixels) + "px"); // camelCase format
					}
				});
			}

			@Override
			public void setCellSpacing(final int pixels) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						encapsulatingTable.setAttribute("cellspacing", String.valueOf(pixels) + "px");
					}
				});
			}

			@Override
			public void setCellPadding(final int pixels) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						encapsulatingTable.setAttribute("cellpadding", String.valueOf(pixels) + "px");
					}
				});
			}

			@Override
			public void setFixedLayout(final Dimension westDimension, final Dimension centerDimension, final Dimension eastDimension) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						encapsulatingTable.getStyle().setTableLayout(TableLayout.FIXED);
						coll1.getStyle().setProperty("width", westDimension.cssDimensionString);
						coll2.getStyle().setProperty("width", centerDimension.cssDimensionString);
						coll3.getStyle().setProperty("width", eastDimension.cssDimensionString);
					}
				});
			}
		});
	}

	//---------------------------------------------------------------
	// INITIALIZATION CODE

	@Override
	protected void extend(ServerConnector target) {
		final Widget extendedWidget = ((CustomLayoutConnector) target).getWidget();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				// first crawl the structure and get references to elements of interest
				encapsulatingTable = extendedWidget.getElement().getFirstChildElement();
				checkElement(encapsulatingTable, null);

				Element colGroup = encapsulatingTable.getFirstChildElement();
				checkElement(colGroup, null);

				coll1 = colGroup.getFirstChildElement();
				checkElement(coll1, null);
				coll2 = coll1.getNextSiblingElement();
				checkElement(coll2, null);
				coll3 = coll2.getNextSiblingElement();
				checkElement(coll3, null);

				row1 = encapsulatingTable.getFirstChildElement().getNextSiblingElement().getFirstChildElement();
				checkElement(row1, firstRowStyleName);
				row2 = row1.getNextSiblingElement();
				checkElement(row2, secondRowStyleName);
				row3 = row2.getNextSiblingElement();
				checkElement(row3, thirdRowStyleName);

				cell1 = row2.getFirstChildElement();
				checkElement(cell1, firstColumnStyleName);
				cell2 = cell1.getNextSiblingElement();
				checkElement(cell2, secondColumnStyleName);
				cell3 = cell2.getNextSiblingElement();
				checkElement(cell3, thirdColumnStyleName);
			}
		});
	}

	private void checkElement(Element elem, String styleName) {
		if (elem == null) {
			throw new NullPointerException("Could not find the element with style name '" + styleName + "'");
		} else if ((styleName != null) && !elem.getClassName().equals(styleName)) {
			throw new IllegalStateException("Malformed template HTML file for auto vertical border layout. Unknown class name found: " + "'" + elem.getClassName() + "'. Expected: '" + styleName
					+ "'.");
		}
	}

	//---------------------------------------------------------------
	// PRIVATE TYPES

	private class ItemInfo<T> {
		public boolean visible;
		public int span;
		public T spaceTakerForThisItem;

		public ItemInfo() {
			this.visible = true;
			this.span = 1;
			this.spaceTakerForThisItem = null;
		}
	}

	//---------------------------------------------------------------
	// MISCELLANEOUS

	private Element getElementByRow(Row row) {
		switch (row) {
		case NORTH:
			return row1;
		case CENTER:
			return row2;
		case SOUTH:
			return row3;
		default:
			throw new IllegalStateException("Unknown row: '" + row.name() + "'");
		}
	}

	private Element getElementByColumn(Column column) {
		switch (column) {
		case WEST:
			return cell1;
		case CENTER:
			return cell2;
		case EAST:
			return cell3;
		default:
			throw new IllegalStateException("Unknown column: '" + column.name() + "'");
		}
	}

	private void setVisibility(Element elem, boolean visible) {
		if (visible) {
			elem.getStyle().clearProperty("display");
		} else {
			elem.getStyle().setDisplay(Display.NONE);
		}
	}
}