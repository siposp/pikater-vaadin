package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.users;

import org.pikater.web.vaadin.gui.server.components.forms.UserProfileForm;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.IContentComponent;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;

/**
 * View implementing the user "profile" feature.
 * 
 * @author SkyCrawl
 * 
 * @see {@link DefaultUI}
 * @see {@link ContentProvider}
 */
public class UserProfileView extends CustomComponent implements IContentComponent {
	private static final long serialVersionUID = -5751678204210363235L;

	private final UserProfileForm innerForm;

	public UserProfileView() {
		super();

		this.innerForm = new UserProfileForm();
		this.innerForm.setSizeUndefined();

		setCompositionRoot(this.innerForm);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		innerForm.enter(event);
	}

	@Override
	public boolean isReadyToClose() {
		return !innerForm.isFormUpdated();
	}

	@Override
	public String getCloseMessage() {
		return "Changes were not stored yet. Discard them and continue?";
	}

	@Override
	public void beforeClose() {
	}
}