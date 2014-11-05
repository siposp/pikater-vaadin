package org.pikater.web.vaadin.gui.server.ui_default.indexpage;

import org.pikater.shared.util.ReflectionUtils;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider.IWebFeature;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.IContentComponent;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

/**
 * Defines content area of the {@link DefaultUI index page}.
 * 
 * @author SkyCrawl
 */
public class ContentArea extends Panel {
	private static final long serialVersionUID = 7642456908975377869L;

	private final Navigator navigator;

	public ContentArea() {
		super();
		setSizeFull();
		setStyleName("contentArea");

		this.navigator = new Navigator(UI.getCurrent(), this);
		this.navigator.addViewChangeListener(new ViewChangeListener() {
			private static final long serialVersionUID = -6954284010979732570L;

			private boolean result;

			/**
			 * Called after {@link Navigator#navigateTo(String)}, e.g. after
			 * {@link ContentArea#setContent(Component content)} or
			 * {@link ContentArea#setContent(IWebFeature feature)} method.
			 */
			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				result = false; // block the change by default
				if ((event.getOldView() != null) && (event.getOldView() instanceof IContentComponent)) {
					final IContentComponent currentView = (IContentComponent) event.getOldView();
					if (!currentView.isReadyToClose()) {
						GeneralDialogs.confirm("Navigate away?", currentView.getCloseMessage(), new GeneralDialogs.IDialogResultHandler() {
							@Override
							public boolean handleResult(Object[] args) {
								currentView.beforeClose();
								result = true;
								return true; // close the dialog
							}
						});
					} else {
						result = true;
					}
				} else {
					result = true;
				}
				return result;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
			}
		});

		/*
		 * Register all available views so that navigator will always know what view to create.
		 */
		for (Class<? extends IWebFeature> featureSetClass : ReflectionUtils.getSubtypesFromPackage(ContentProvider.class.getPackage(), IWebFeature.class)) {
			registerAllViewsFromFeatureSet(featureSetClass);
		}
	}

	/**
	 * @deprecated Use {@link #setContentView(IWebFeature)} instead.
	 */
	@Deprecated
	@Override
	public void setContent(Component content) {
		super.setContent(content);
	}

	//---------------------------------------------------------------
	// PUBLIC INTERFACE

	/**
	 * The main routine for setting the content component.
	 */
	public void setContentView(IWebFeature feature) {
		if (feature.accessAllowed(VaadinSession.getCurrent())) {
			navigator.navigateTo(feature.toNavigatorName());
		} else {
			GeneralDialogs.error("Access denied", "Contact the administrators.");
		}
	}

	//---------------------------------------------------------------
	// PRIVATE INTERFACE

	private void registerAllViewsFromFeatureSet(Class<? extends IWebFeature> clazz) {
		for (IWebFeature feature : clazz.getEnumConstants()) {
			if (feature.accessAllowed(VaadinSession.getCurrent()) && (feature.toComponentClass() != null)) {
				navigator.addView(feature.toNavigatorName(), feature.toComponentClass());
			}
		}
	}
}
