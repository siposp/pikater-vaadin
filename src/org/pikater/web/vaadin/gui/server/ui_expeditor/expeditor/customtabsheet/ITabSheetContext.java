package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.customtabsheet;

/**
 * Interface implemented by parent components of {@link TabSheet}.
 * 
 * @author SkyCrawl
 */
public interface ITabSheetContext {
	void addEmptyTab();

	void onTabSelectionChange();
}