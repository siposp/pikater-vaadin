package org.pikater.web.vaadin.gui.server.ui_default.indexpage;

import org.pikater.web.vaadin.CustomConfiguredUI;
import org.pikater.web.vaadin.gui.server.layouts.borderlayout.AutoVerticalBorderLayout;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider.DefaultFeature;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider.IWebFeature;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Border;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Column;
import org.pikater.web.vaadin.gui.shared.borderlayout.BorderLayoutUtil.Row;
import org.pikater.web.vaadin.gui.shared.borderlayout.Dimension.DimensionMode;
import org.pikater.web.vaadin.gui.shared.borderlayout.Dimension;
import org.pikater.web.vaadin.gui.shared.borderlayout.Dimension.DimensionUnit;

import com.vaadin.annotations.StyleSheet;

/**
 * Master component for {@link DefaultUI index page UI}.
 * 
 * @author SkyCrawl
 */
@StyleSheet("indexPage.css")
public class IndexPage extends AutoVerticalBorderLayout {
	private static final long serialVersionUID = -5286072324040828820L;

	private final ContentArea contentArea;

	public IndexPage() {
		/*
		 * A new tab was created or a browser refresh occured.  
		 */
		super();

		this.contentArea = new ContentArea();

		setBorderSpacing(0);
		setCellPadding(0);
		setCellSpacing(0);
		setComponent(Border.NORTH, new BannerArea());
		setComponent(Border.WEST, new LeftMenu(this));
		setComponent(Border.CENTER, contentArea);
		setRowInvisible(Row.SOUTH, Row.CENTER);
		setRowHeight(Row.CENTER, new Dimension(DimensionMode.MAX));
		setColumnInvisible(Column.EAST, Column.CENTER);
		setFixedLayout(new Dimension(200, DimensionUnit.PX), new Dimension(DimensionMode.AUTO), new Dimension(DimensionMode.AUTO));

		/*
		 * Display the content depicted by URL fragment or display default content if no fragment is defined.
		 */
		if (CustomConfiguredUI.isURIFragmentDefined()) {
			// try to preserve the original content being displayed
			IWebFeature featureFromRequest = ContentProvider.getFeatureFromNavigatorName(CustomConfiguredUI.getURIFragment());
			if (featureFromRequest == null) {
				// default to our basic content
				openContent(getDefaultContent());
			} else {
				// original content successfully preserved
				openContent(featureFromRequest);
			}
		} else {
			// default to our basic content
			openContent(getDefaultContent());
		}
	}

	/**
	 * Index page's content can only be a predefined component
	 * bound with a certain feature in {@link ContentProvider}.
	 */
	public void openContent(IWebFeature feature) {
		this.contentArea.setContentView(feature);
	}

	private IWebFeature getDefaultContent() {
		return DefaultFeature.WELCOME;
		// return DefaultFeature.TEST;
	}
}
