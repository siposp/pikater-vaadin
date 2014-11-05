package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.users;

import org.pikater.shared.database.views.tableview.users.UsersTableDBView;
import org.pikater.web.vaadin.gui.server.components.dbviews.UsersDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTableLayout;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.IContentComponent;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * View implementing the administrator user feature.
 * 
 * @author SkyCrawl
 * 
 * @see {@link DefaultUI}
 * @see {@link ContentProvider}
 */
public class UsersView extends DBTableLayout implements IContentComponent {
	private static final long serialVersionUID = -6440037882833424701L;

	public UsersView() {
		super();
		setSizeUndefined();
		setWidth("100%");
	}

	//----------------------------------------------------
	// VIEW INTERFACE

	@Override
	public void enter(ViewChangeEvent event) {
		setView(new UsersDBViewRoot(new UsersTableDBView())); // required to be executed after initializing DB view
	}

	@Override
	public boolean isReadyToClose() {
		return true;
	}

	@Override
	public String getCloseMessage() {
		return null;
	}

	@Override
	public void beforeClose() {
	}
}