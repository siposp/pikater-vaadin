package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.agents;

import org.pikater.shared.database.views.tableview.externalagents.ExternalAgentTableDBView;
import org.pikater.web.vaadin.gui.server.components.dbviews.AgentsDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTableLayout;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.IContentComponent;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * View implementing the administrator agent feature.
 * 
 * @author SkyCrawl
 * 
 * @see {@link DefaultUI}
 * @see {@link ContentProvider}
 */
public class AgentsView extends DBTableLayout implements IContentComponent {
	private static final long serialVersionUID = -4608720645535772140L;

	public AgentsView() {
		super();
		setSizeUndefined();
		setWidth("100%");
	}

	//----------------------------------------------------
	// VIEW INTERFACE

	@Override
	public void enter(ViewChangeEvent event) {
		setView(new AgentsDBViewRoot(new ExternalAgentTableDBView())); // required to be executed after initializing DB view
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