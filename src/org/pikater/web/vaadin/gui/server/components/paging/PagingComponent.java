package org.pikater.web.vaadin.gui.server.components.paging;

import java.util.Arrays;

import org.pikater.web.vaadin.gui.server.components.anchor.Anchor;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * Component providing paging navigation and setting page sizes.
 * 
 * @author SkyCrawl
 */
@StyleSheet("pagingComponent.css")
public class PagingComponent extends HorizontalLayout {
	private static final long serialVersionUID = 5946124883390545518L;

	/*
	 * Programmatic variables.
	 */
	private final IPagedComponent context;

	/*
	 * Inner components.
	 */
	private final ComboBox cb_pageLength;
	private final PagePicker pagePicker;

	public PagingComponent(final IPagedComponent context) {
		super();
		setSpacing(true);
		setStyleName("pagingComponent");

		this.context = context;

		Label label_pageLength = new Label("Items per page:");
		label_pageLength.setSizeUndefined();

		this.cb_pageLength = new ComboBox(null, Arrays.asList(15, 30, 50, 75,
				100));
		this.cb_pageLength.setSizeUndefined();
		this.cb_pageLength.setNullSelectionAllowed(false);
		this.cb_pageLength.setNewItemsAllowed(false);
		this.cb_pageLength.setTextInputAllowed(false);
		this.cb_pageLength.setValue(15); // default page length
		this.cb_pageLength.setImmediate(true);
		this.cb_pageLength
				.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 9139815620172779594L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						context.onPageSizeChanged((Integer) event.getProperty()
								.getValue());
					}
				});

		this.pagePicker = new PagePicker();

		addComponent(label_pageLength);
		setComponentAlignment(label_pageLength, Alignment.MIDDLE_LEFT);
		addComponent(cb_pageLength);
		setComponentAlignment(cb_pageLength, Alignment.MIDDLE_LEFT);
		addComponent(pagePicker);
		setComponentAlignment(pagePicker, Alignment.MIDDLE_RIGHT);

		setExpandRatio(label_pageLength, 0);
		setExpandRatio(cb_pageLength, 0);
		setExpandRatio(pagePicker, 1);
	}

	public int getPageSize() {
		return (Integer) cb_pageLength.getValue();
	}

	public int getCurrentlySelectedPage() {
		return pagePicker.getCurrentlySelectedPage();
	}

	/**
	 * Look at the currently selected page and page size and determine the
	 * current overall offset of this first visible item.
	 */
	public int getOverallOffset() {
		return (getCurrentlySelectedPage() - 1) * getPageSize();
	}

	/**
	 * Sets currently viewed page to the given page.
	 * 
	 * @param spawnEvents
	 *            if true, calls {@link IPagedComponent#onPageChanged(int)}.
	 */
	public void setPage(int page, boolean spawnEvents) {
		pagePicker.setPage(page, spawnEvents); // simply forward
	}

	public void updatePageCount(int itemCountAcrossAllPages) {
		double itemCount = itemCountAcrossAllPages;
		double pageSize = getPageSize();
		int pagesNeeded = (int) Math.ceil(itemCount / pageSize); // this needs
																	// to be a
																	// double
																	// division,
																	// not an
																	// integer
																	// division
		pagePicker.setPageCount(pagesNeeded == 0 ? 1 : pagesNeeded);
	}

	// -------------------------------------------------------------
	// INNER TYPES

	public interface IPagedComponent {
		/**
		 * Callback for when user selects another page. When this method
		 * finishes, {@link #getAllItemsCount} is called to update the paging
		 * component further.</br> <font color="red">RED ALERT: </font> do not
		 * EVER update paging in this method. The component updates itself as
		 * needed.
		 */
		void onPageChanged(int page);

		/**
		 * Callback for when user sets a different page size. Paging is not
		 * updated by default, you must do it yourselves if you wish to react
		 * upon this event.
		 */
		void onPageSizeChanged(int itemsPerPage);
	}

	/**
	 * Inner component providing page navigation - direct links for first, last,
	 * next and previous pages + direct page selection combo box.
	 * 
	 * @author SkyCrawl
	 */
	private class PagePicker extends HorizontalLayout {
		private static final long serialVersionUID = -595408596224035721L;

		private final Anchor page_previous;
		private final ComboBox cb_selectedPage;
		private final Label label_lastPage;
		private final Anchor page_next;

		public PagePicker() {
			super();
			setSizeUndefined();
			setSpacing(true);

			Anchor page_first = new Anchor("<< First",
					new MouseEvents.ClickListener() {
						private static final long serialVersionUID = -3912177860445526917L;

						@Override
						public void click(ClickEvent event) {
							setPage(1, true);
						}
					});
			page_previous = new Anchor("< Previous",
					new MouseEvents.ClickListener() {
						private static final long serialVersionUID = -5064772189847933893L;

						@Override
						public void click(ClickEvent event) {
							if (!isFirstPageSelected()) {
								setPage(getCurrentlySelectedPage() - 1, true);
							}
						}
					});

			Label label_selectedPage = new Label("Page:");

			this.cb_selectedPage = new ComboBox();
			this.cb_selectedPage.setSizeUndefined();
			this.cb_selectedPage.setNullSelectionAllowed(false);
			this.cb_selectedPage.setTextInputAllowed(false);
			this.cb_selectedPage.setScrollToSelectedItem(true);
			this.cb_selectedPage.setImmediate(true);
			this.cb_selectedPage.setData(false); // disable events until init is
													// more or less complete
			this.cb_selectedPage
					.addValueChangeListener(new Property.ValueChangeListener() {
						private static final long serialVersionUID = 1709427468483026288L;

						@Override
						public void valueChange(ValueChangeEvent event) {
							if ((Boolean) cb_selectedPage.getData()) {
								Integer selectedPage = (Integer) event
										.getProperty().getValue();
								if (selectedPage != null) // just a safety
															// check... selected
															// page should not
															// be null
								{
									// just a safety check... should never
									// happen though
									if ((selectedPage < 1)
											|| (selectedPage > getLastPage())) {
										throw new IndexOutOfBoundsException();
									} else {
										// select the new page and update visual
										// style
										setPage(selectedPage, false);

										// notify the owner
										context.onPageChanged(selectedPage);
									}
								}
							}
						}
					});
			this.cb_selectedPage.addItem(1);
			this.cb_selectedPage.select(1);
			this.cb_selectedPage.setData(true); // enable events when init is
												// more or less complete

			this.label_lastPage = new Label();
			page_next = new Anchor("Next >", new MouseEvents.ClickListener() {
				private static final long serialVersionUID = -7651055555368147669L;

				@Override
				public void click(ClickEvent event) {
					if (!isLastPageSelected()) {
						setPage(getCurrentlySelectedPage() + 1, true);
					}
				}
			});
			this.label_lastPage.setValue(String.valueOf(1));
			Anchor page_last = new Anchor("Last >>",
					new MouseEvents.ClickListener() {
						private static final long serialVersionUID = -3144174540771713139L;

						@Override
						public void click(ClickEvent event) {
							setPage(getLastPage(), true);
						}
					});

			afterChecks();

			PagePicker.this
					.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
			addComponent(page_first);
			addComponent(page_previous);
			addComponent(label_selectedPage);
			addComponent(cb_selectedPage);
			addComponent(new Label("of"));
			addComponent(label_lastPage);
			addComponent(page_next);
			addComponent(page_last);
		}

		public int getLastPage() {
			return cb_selectedPage.size();
		}

		public int getCurrentlySelectedPage() {
			return (Integer) cb_selectedPage.getValue();
		}

		/**
		 * Sets currently viewed page to the given page. Needs to be used AFTER
		 * {@link #setPageCount(int)}.
		 * 
		 * @param spawnEvents
		 *            if true, calls {@link IPagedComponent#onPageChanged(int)}.
		 */
		public void setPage(int page, boolean spawnEvents) {
			// just a safety check... should never happen though
			if ((page < 1) || (page > getLastPage())) {
				throw new IndexOutOfBoundsException();
			} else if (spawnEvents) {
				cb_selectedPage.setValue(page);
			} else {
				cb_selectedPage.select(page);
				afterChecks();
			}
		}

		/**
		 * Updates inner components with a new page count, without spawning any
		 * events. Also updates the selected page if necessary but does NOT
		 * reset it to the first page.
		 */
		public void setPageCount(final int pageCount) {
			if (pageCount < 1) {
				throw new IllegalArgumentException(
						"Page count must be non-zero integer positive number.");
			} else {
				label_lastPage.setValue(String.valueOf(pageCount));
				updateWithoutSpawningEvents(new Runnable() {
					@Override
					public void run() {
						int currentlySelectedPage = getCurrentlySelectedPage();

						// TODO: make this more efficient - only add/remove
						// what's needed to be added/removed
						cb_selectedPage.removeAllItems();
						for (int i = 1; i <= pageCount; i++) {
							cb_selectedPage.addItem(i);
						}
						cb_selectedPage
								.select(currentlySelectedPage > getLastPage() ? getLastPage()
										: currentlySelectedPage);
					}
				});
				afterChecks();
			}
		}

		// ---------------------------------------------------
		// PRIVATE INTERFACE

		/**
		 * Provides a way to make changes to the inner navigation components
		 * without triggering any attached event handlers.
		 */
		private void updateWithoutSpawningEvents(Runnable updateAction) {
			/*
			 * Manipulating with the combobox's items triggers value change
			 * event, which in turn calls {@link #setSelectedPage(int page)}.
			 * Make sure that the event 'knows' not to call it and set the
			 * application data field to 'false'.
			 */
			cb_selectedPage.setData(false);
			updateAction.run();
			cb_selectedPage.setData(true);
		}

		/**
		 * Some post-processing of inner components when changes occur.
		 */
		private void afterChecks() {
			page_previous.setEnabled(!isFirstPageSelected());
			page_next.setEnabled(!isLastPageSelected());
		}

		private boolean isFirstPageSelected() {
			return getCurrentlySelectedPage() == 1;
		}

		private boolean isLastPageSelected() {
			return getCurrentlySelectedPage() == getLastPage();
		}
	}
}
