package org.pikater.web.vaadin.gui.server.ui_default;

import org.pikater.web.vaadin.CustomConfiguredUI;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.IndexPage;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;

/**
 * Defines the "index page" of the web application.
 * 
 * @author SkyCrawl
 */
@Title("Pikatorium")
@Theme("pikater")
@Push(value = PushMode.AUTOMATIC)
public class DefaultUI extends CustomConfiguredUI {
	private static final long serialVersionUID = 1964653532060950402L;

	@Override
	protected void init(VaadinRequest request) {
		/*
		 * Don't forget to call this.
		 * IMPORTANT:
		 * 1) You shouldn't update the UI in this method. You only provide the content component
		 * when you're asked to in the {@link #displayChildContent()} method.
		 * 2) When {@link #displayChildContent()} is called, this method is still not finished.
		 * You shouldn't have any initializing code after the super.init() call.
		 */
		super.init(request);
	}

	@Override
	protected void displayChildContent() {
		/*
		 * Display index page if user is authenticated or make him authenticate first
		 * and then display it.
		 */
		if (UserAuth.isUserAuthenticated(VaadinSession.getCurrent())) {
			displayIndexPage();
		} else {
			forceUserToAuthenticate(new IAuthenticationSuccessful() {
				@Override
				public void onSuccessfulAuth() {
					displayIndexPage();
				}
			});
		}
	}

	private void displayIndexPage() {
		setPageCroppedAndHorizontallyCentered(true); // to make it look better

		// return new index page for each UI unless you want all browser tabs to be synchronized and display the same content
		setContent(new IndexPage());
	}
}