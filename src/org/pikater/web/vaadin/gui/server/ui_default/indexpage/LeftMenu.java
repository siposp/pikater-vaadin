package org.pikater.web.vaadin.gui.server.ui_default.indexpage;

import java.util.HashMap;
import java.util.Map;

import org.pikater.shared.util.SimpleIDGenerator;
import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.vaadin.CustomConfiguredUI;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.CustomConfiguredUIServlet.PikaterUI;
import org.pikater.web.vaadin.gui.server.components.anchor.Anchor;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider.AdminFeature;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider.IWebFeature;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider.UserFeature;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

/**
 * Feature navigation menu within {@link IndexPage}, which
 * backtracks to {@link DefaultUI}.
 * 
 * @author SkyCrawl
 */
public class LeftMenu extends Panel {
	private static final long serialVersionUID = -3876199300842530858L;

	//----------------------------------------------------------
	// COMPONENT FIELDS

	/**
	 * The menu component. Constructed when this component is attached and first child
	 * of inner layout.
	 */
	private Tree menu;

	//----------------------------------------------------------
	// PROGRAMMATIC FIELDS

	private final SimpleIDGenerator idGenerator;
	private final Map<Integer, IWebFeature> menuItemIDToFeature;

	public LeftMenu(final IndexPage parent) {
		super();
		setStyleName("leftMenu-container");

		this.idGenerator = new SimpleIDGenerator();
		this.menuItemIDToFeature = new HashMap<Integer, IWebFeature>();

		// define the topmost component
		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.setStyleName("leftMenu");

		// define the menu component
		this.menu = new Tree();
		this.menu.setImmediate(true);
		this.menu.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 6026687725039562858L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Integer menuItemID = (Integer) event.getProperty().getValue();
				if (menuItemIDToFeature.containsKey(menuItemID)) {
					parent.openContent(menuItemIDToFeature.get(menuItemID));
				}
			}
		});

		// define additional menu item, that is not directly part of it (internally)
		Anchor anchor = new Anchor("Open experiment editor", String.format("window.open('%s', '_blank');", CustomConfiguredUI.getRedirectURLToUI(PikaterUI.EXP_EDITOR)));
		anchor.setStyleName("v-tree-node-caption");

		// add leaf components
		innerLayout.addComponent(this.menu);
		innerLayout.addComponent(anchor);

		// add topmost component
		setContent(innerLayout);
	}

	@Override
	public void attach() {
		super.attach();

		if (WebAppConfiguration.avoidUsingDBForNow()) {
			buildMenu(true);
		} else {
			buildMenu(UserAuth.getUserEntity(getSession()).isAdmin());
		}
	}

	private void buildMenu(boolean withAdminMenu) {
		if (withAdminMenu) {
			buildMenuCategory("Admin features", AdminFeature.class);
		}
		buildMenuCategory("User features", UserFeature.class);
	}

	private void buildMenuCategory(String caption, Class<? extends IWebFeature> featureSetClass) {
		Integer rootMenuID = addMenuItem(caption);
		for (IWebFeature feature : featureSetClass.getEnumConstants()) {
			Integer subMenuID = addMenuItem(feature.toMenuCaption());
			menu.setParent(subMenuID, rootMenuID);
			menu.setChildrenAllowed(subMenuID, false);

			// only items registered like this will have custom action called on click
			menuItemIDToFeature.put(subMenuID, feature);
		}
		menu.expandItem(rootMenuID);
	}

	private Integer addMenuItem(String caption) {
		Integer menuItemID = idGenerator.getAndIncrement();
		menu.addItem(menuItemID);
		menu.setItemCaption(menuItemID, caption);
		return menuItemID;
	}
}