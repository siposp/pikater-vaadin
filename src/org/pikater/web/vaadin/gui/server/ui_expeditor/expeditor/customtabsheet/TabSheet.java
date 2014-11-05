package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.customtabsheet;

import java.util.HashMap;
import java.util.Map;

import org.pikater.web.sharedresources.ThemeResources;
import org.pikater.web.vaadin.gui.server.components.iconbutton.IconButton;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.layouts.borderlayout.AutoVerticalBorderLayout;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Border;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Row;
import org.pikater.web.vaadin.gui.shared.borderlayout.Dimension;
import org.pikater.web.vaadin.gui.shared.borderlayout.Dimension.DimensionMode;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

/**
 * <p>
 * Vaadin's {@link com.vaadin.ui.TabSheet} seemed a bit unpredictable when
 * dealing with client-side content like our {@link KineticComponent}, causing
 * several headaches. This class aims to provide the same basic functionality
 * without the mysterious inner mechanics of Vaadin's tab sheet component.
 * </p>
 * 
 * <p>
 * Warning: this class is not thread-safe. If a single user works with it, there
 * should be no problems but beware updating it with a background task for
 * instance.
 * </p>
 * 
 * @author SkyCrawl
 */
@StyleSheet("tabSheet.css")
public class TabSheet extends CustomComponent {
	private static final long serialVersionUID = -4571956367761332160L;

	/*
	 * GUI components.
	 */

	/**
	 * Master layout of this component.
	 */
	private final AutoVerticalBorderLayout innerLayout;

	/**
	 * North component of {@link #innerLayout}.
	 */
	private final HorizontalLayout tabBar;

	/**
	 * Inner component of {@link #tabBar} that is currently selected.
	 */
	private TabSheetTabComponent selectedTabComponent;

	/**
	 * Displayed in the content area when the {@link #tabBar} is empty.
	 */
	private final Panel defaultContent;

	/*
	 * Programmatic variables.
	 */

	private final ITabSheetContext context;
	private final Map<TabSheetTabComponent, Panel> tabToContentContainer;

	public TabSheet(ITabSheetContext context) {
		super();

		/*
		 * Inner component init.
		 */

		this.tabBar = new HorizontalLayout();
		this.tabBar.setStyleName("custom-tabsheet-tabs");
		this.tabBar.setSpacing(true);

		/*
		 * Layout init.
		 */

		// basic layout setup - this layout will not be changed in any way
		// during its lifetime so no need to store it in a field
		this.innerLayout = new AutoVerticalBorderLayout();
		this.innerLayout.setSizeFull();
		this.innerLayout.setStyleName("custom-tabsheet");
		this.innerLayout.setBorderSpacing(0);
		this.innerLayout.setRowInvisible(Row.CENTER, Row.SOUTH);
		this.innerLayout.setComponent(Border.NORTH, this.tabBar);
		this.innerLayout.setRowHeight(Row.SOUTH, new Dimension(
				DimensionMode.MAX));
		this.innerLayout.setFixedLayout(new Dimension(DimensionMode.AUTO),
				new Dimension(DimensionMode.AUTO), new Dimension(
						DimensionMode.AUTO));

		// this component's basic setup
		setCompositionRoot(innerLayout);
		setSizeFull();

		/*
		 * TabSheet init.
		 */

		this.defaultContent = createContentContainer(null);
		this.defaultContent.setEnabled(false);
		// note: don't violate the call order with the following:
		doAddTab(createAddTabComponent());

		/*
		 * Miscellaneous init.
		 */

		this.selectedTabComponent = null;
		this.tabToContentContainer = new HashMap<TabSheetTabComponent, Panel>();
		this.context = context;
	}

	// ---------------------------------------------------------------
	// PUBLIC INTERFACE

	/**
	 * Creates a new tab with the given components.
	 * 
	 * @param tabComponent
	 *            the tab component appended to {@link #tabBar}
	 * @param contentComponent
	 *            the content component to be displayed in the content area
	 */
	public void addTab(TabSheetTabComponent tabComponent,
			AbstractComponent contentComponent) {
		prepareTabComponent(tabComponent);
		doAddTab(tabComponent);
		tabToContentContainer.put(tabComponent,
				createContentContainer(contentComponent));
		setSelectedTab(tabComponent);
	}

	public TabSheetTabComponent getSelectedTab() {
		return selectedTabComponent;
	}

	public void setSelectedTab(TabSheetTabComponent tabComponent) {
		if (selectedTabComponent != null) {
			selectedTabComponent.setSelected(false);
		}
		selectedTabComponent = tabComponent;
		tabComponent.setSelected(true);
		setSelectedContent(tabToContentContainer.get(tabComponent));
		context.onTabSelectionChange();
	}

	// ---------------------------------------------------------------
	// PRIVATE INTERFACE

	/**
	 * Only add tabs with this method. Handles some special cases.
	 */
	private void doAddTab(AbstractComponent newTab) {
		this.tabBar.addComponent(newTab, tabBar.getComponentCount() == 0 ? 0
				: tabBar.getComponentCount() - 1);
		this.tabBar.setComponentAlignment(newTab, Alignment.MIDDLE_CENTER);
	}

	/**
	 * Creates the component that, when clicked, creates and adds a new empty
	 * tab.
	 */
	private AbstractComponent createAddTabComponent() {
		IconButton addTabButton = new IconButton(ThemeResources.img_plusIcon16);
		addTabButton
				.addClickListener(new com.vaadin.event.MouseEvents.ClickListener() {
					private static final long serialVersionUID = -7054477583680936381L;

					@Override
					public void click(
							com.vaadin.event.MouseEvents.ClickEvent event) {
						context.addEmptyTab();
					}
				});
		addTabButton.setStyleName("custom-tabsheet-tabs-tab-add");
		return addTabButton;
	}

	/**
	 * Attaches required event handlers.
	 */
	private void prepareTabComponent(final TabSheetTabComponent tabComponent) {
		tabComponent
				.addClickListener(new com.vaadin.event.MouseEvents.ClickListener() {
					private static final long serialVersionUID = -3908357742920865682L;

					@Override
					public void click(
							com.vaadin.event.MouseEvents.ClickEvent event) {
						if (tabComponent != selectedTabComponent) {
							setSelectedTab(tabComponent);
						}
					}
				});
		tabComponent
				.addCloseHandler(new com.vaadin.event.MouseEvents.ClickListener() {
					private static final long serialVersionUID = -8426832013522667216L;

					@Override
					public void click(
							com.vaadin.event.MouseEvents.ClickEvent event) {
						if (tabComponent.canCloseTab()) // ask whether we can
														// close this tab
						{
							closeTab();
						} else // if not, give the user a chance to cancel the
								// action
						{
							GeneralDialogs.confirm("Really close this tab?",
									"The content will be lost, if unsaved.",
									new GeneralDialogs.IDialogResultHandler() {
										/*
										 * If confirmed:
										 */

										@Override
										public boolean handleResult(
												Object[] args) {
											closeTab(); // close the tab
											return true; // close the dialog
										}
									});
						}
					}

					private void closeTab() {
						// first handle the case if we remove the selected tab
						if (selectedTabComponent == tabComponent) {
							int currentPosition = tabBar
									.getComponentIndex(selectedTabComponent);
							if (currentPosition > 0) {
								setSelectedTab((TabSheetTabComponent) tabBar
										.getComponent(currentPosition - 1));
							} else if (currentPosition < tabBar
									.getComponentCount() - 2) // not the last
																// tab
							{
								setSelectedTab((TabSheetTabComponent) tabBar
										.getComponent(currentPosition + 1));
							} else // no tabs to select anymore
							{
								setSelectedContent(null);
							}
						}

						// and then remove everything related to this tab
						tabToContentContainer.remove(tabComponent);
						tabBar.removeComponent(tabComponent);
					}
				});
		tabComponent.setStyleName("custom-tabsheet-tabs-tab");
	}

	/**
	 * Creates container for content component of a tab.
	 */
	private Panel createContentContainer(AbstractComponent content) {
		Panel result = new Panel();
		result.setSizeFull();
		result.setStyleName("custom-tabsheet-content-container");
		if (content != null) {
			result.setContent(content);
		}
		return result;
	}

	/**
	 * Sets the selected tab's content component's container as the selected
	 * component. Only to be used from
	 * {@link #setSelectedTab(TabSheetTabComponent)}.
	 */
	private void setSelectedContent(Panel content) {
		innerLayout.setComponent(Border.SOUTH, content != null ? content
				: defaultContent);
	}
}
