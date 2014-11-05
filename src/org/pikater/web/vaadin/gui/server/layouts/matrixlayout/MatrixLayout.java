package org.pikater.web.vaadin.gui.server.layouts.matrixlayout;

import org.pikater.web.vaadin.gui.server.layouts.flowlayout.HorizontalFlowLayout;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.IFlowLayoutStyleProvider;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.VerticalFlowLayout;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.thumbnail.Thumbnail;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Views items indexed by a "left" and "top" index. Items viewed may be
 * filtered by both indexes.
 * 
 * @author SkyCrawl
 *
 * @param <I> The index type of the matrix.
 * @param <R> The item type of the matrix.
 */
@StyleSheet("matrixLayout.css")
public class MatrixLayout<I extends Object, R extends Thumbnail> extends VerticalLayout {
	private static final long serialVersionUID = -4102353757340619486L;

	/*
	 * Some static/configuration variables.
	 */

	private static final String HEADER_WIDTH = "150px";
	private static final String HEADER_HEIGHT = "30px"; // synchronize this with CSS (header component's line height)

	/*
	 * Data source related variables.
	 */

	private final IMatrixDataSource<I, ? extends Component> dataSource;

	/*
	 * Components to keep track of.
	 */

	private final HorizontalFlowLayout content;
	private HeaderComponent selectedHeader_leftIndex;
	private HeaderComponent selectedHeader_topIndex;

	public MatrixLayout(IMatrixDataSource<I, R> dataSource, IMatrixLayoutHeaderProvider<I> captionProvider, IFlowLayoutStyleProvider contentStyleProvider) {
		super();
		setPrimaryStyleName("matrixView");

		/*
		 * Setup data source variables.
		 */

		this.dataSource = dataSource;
		this.selectedHeader_leftIndex = null;
		this.selectedHeader_topIndex = null;

		/*
		 * Setup the UI.
		 */

		this.content = new HorizontalFlowLayout(contentStyleProvider);
		content.setSizeFull();
		content.addStyleName("main-subcomponent");
		content.addStyleName("content");

		// first row
		HorizontalFlowLayout fLayout_indexTop = new HorizontalFlowLayout(null); // TODO: use the style provider and don't use external CSS
		fLayout_indexTop.setSizeFull();
		fLayout_indexTop.addStyleName("topHeaders");
		fLayout_indexTop.addStyleName("main-subcomponent");
		for (I indexTop : dataSource.getTopIndexSet()) {
			final HeaderComponent header = new HeaderComponent(captionProvider.getCaptionComponent(indexTop));
			header.setAssociatedIndex(indexTop);
			header.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 6643479653093109482L;

				@Override
				public void click(ClickEvent event) {
					if (selectedHeader_topIndex != null) {
						if (selectedHeader_topIndex != header) {
							selectedHeader_topIndex.invertSelection();
							selectedHeader_topIndex = header;
							selectedHeader_topIndex.invertSelection();
						} else {
							selectedHeader_topIndex.invertSelection();
							selectedHeader_topIndex = null;
						}
					} else {
						selectedHeader_topIndex = header;
						selectedHeader_topIndex.invertSelection();
					}
					updateContent();
				}
			});
			fLayout_indexTop.addComponent(header);
		}

		final HeaderComponent header_reset = new HeaderComponent(new Label("RESET SELECTION"));
		header_reset.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -911534300593086566L;

			@Override
			public void click(ClickEvent event) {
				boolean somethingWasSelected = false;
				if (selectedHeader_leftIndex != null) {
					somethingWasSelected = true;
					selectedHeader_leftIndex.invertSelection();
					selectedHeader_leftIndex = null;
				}
				if (selectedHeader_topIndex != null) {
					somethingWasSelected = true;
					selectedHeader_topIndex.invertSelection();
					selectedHeader_topIndex = null;
				}
				if (somethingWasSelected) {
					updateContent();
				}
			}
		});

		HorizontalLayout firstRow = new HorizontalLayout();
		firstRow.setStyleName("firstRow");
		firstRow.setWidth("100%");
		firstRow.setSpacing(true);
		firstRow.addComponent(header_reset);
		firstRow.addComponent(fLayout_indexTop);
		firstRow.setExpandRatio(fLayout_indexTop, 1);

		// second row
		VerticalFlowLayout fLayout_indexLeft = new VerticalFlowLayout(null); // TODO: use the style provider and don't use external css
		fLayout_indexLeft.addStyleName("leftHeaders");
		fLayout_indexLeft.addStyleName("main-subcomponent");
		for (I indexLeft : dataSource.getLeftIndexSet()) {
			final HeaderComponent header = new HeaderComponent(captionProvider.getCaptionComponent(indexLeft));
			header.setAssociatedIndex(indexLeft);
			header.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 5086608785319580110L;

				@Override
				public void click(ClickEvent event) {
					if (selectedHeader_leftIndex != null) {
						if (selectedHeader_leftIndex != header) {
							selectedHeader_leftIndex.invertSelection();
							selectedHeader_leftIndex = header;
							selectedHeader_leftIndex.invertSelection();
						} else {
							selectedHeader_leftIndex.invertSelection();
							selectedHeader_leftIndex = null;
						}
					} else {
						selectedHeader_leftIndex = header;
						selectedHeader_leftIndex.invertSelection();
					}
					updateContent();
				}
			});
			fLayout_indexLeft.addComponent(header);
		}

		HorizontalLayout secondRow = new HorizontalLayout();
		secondRow.setSizeFull();
		secondRow.setStyleName("secondRow");
		secondRow.setSpacing(true);
		secondRow.addComponent(fLayout_indexLeft);
		secondRow.addComponent(content);
		secondRow.setExpandRatio(content, 1);

		// top-level setup
		addComponent(firstRow);
		addComponent(secondRow);
		setExpandRatio(secondRow, 1);
		updateContent();
	}

	//--------------------------------------------------------
	// PRIVATE INTERFACE

	/**
	 * <p>Updates the content area and displays items for the current
	 * selection.</p>
	 * 
	 * <p>For the sake of clarity, the method is divided into 3 parts.</p>
	 */
	private void updateContent() {
		content.removeAllComponents();
		if (selectedHeader_leftIndex != null) {
			updateContentFor(selectedHeader_leftIndex.getAssociatedIndex());
		} else {
			for (I leftIndex : dataSource.getLeftIndexSet()) {
				updateContentFor(leftIndex);
			}
		}
	}

	/**
	 * Subroutine of {@link #updateContent()}. Only use this method
	 * from within it.
	 */
	private void updateContentFor(I indexLeft) {
		if (selectedHeader_topIndex != null) {
			updateContentFor(indexLeft, selectedHeader_topIndex.getAssociatedIndex());
		} else {
			for (I indexTop : dataSource.getTopIndexSet()) {
				updateContentFor(indexLeft, indexTop);
			}
		}
	}

	/**
	 * Subroutine of {@link #updateContent()}. Only use this method
	 * from within it.
	 */
	private void updateContentFor(I indexLeft, I indexTop) {
		Component currentComponent = dataSource.getElement(indexLeft, indexTop);
		if (currentComponent != null) {
			content.addComponent(currentComponent);
		}
	}

	//--------------------------------------------------------
	// PRIVATE TYPES

	/**
	 * A component representing an index which is selected/deselected
	 * with a click.
	 * 
	 * @author SkyCrawl
	 */
	private class HeaderComponent extends Panel {
		private static final long serialVersionUID = 8986511648022361418L;

		private boolean selected;
		private I associatedIndex;

		public HeaderComponent(Label lbl_inner) {
			super();
			setWidth(HEADER_WIDTH);
			setStyleName("headerComponent");
			addStyleName("notSelected");

			this.selected = false;
			this.associatedIndex = null;

			lbl_inner.setSizeFull();
			lbl_inner.setHeight(HEADER_HEIGHT);
			setContent(lbl_inner);
		}

		public I getAssociatedIndex() {
			return associatedIndex;
		}

		public void setAssociatedIndex(I associatedIndex) {
			this.associatedIndex = associatedIndex;
		}

		public void invertSelection() {
			if (isSelected()) {
				removeStyleName("selected");
				addStyleName("notSelected");
			} else {
				removeStyleName("notSelected");
				addStyleName("selected");
			}
			selected = !selected;
		}

		public boolean isSelected() {
			return selected;
		}
	}
}