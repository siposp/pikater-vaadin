package org.pikater.web.vaadin.gui.server.ui_default.indexpage;

import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.sharedresources.ThemeResources;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Defines banner area (top strip) of the {@link DefaultUI index page}.
 * Beside displaying banner in the background, this component also
 * defines authentication interface that:
 * <ul>
 * <li> displays the currently authenticated account,
 * <li> provides a logout button.
 * </ul>
 * 
 * @author SkyCrawl
 */
public class BannerArea extends HorizontalLayout {
	private static final long serialVersionUID = 8822379309622158669L;

	private final Label lbl_loggedInAs;
	private final Label lbl_accountName;

	public BannerArea() {
		super();
		setSizeFull();
		setStyleName("bannerArea");

		Image checkIcon = new Image(null, ThemeResources.img_checkIcon16);
		this.lbl_loggedInAs = new Label("Logged in as:");
		this.lbl_accountName = new Label();
		this.lbl_accountName.setStyleName("label-accountName");

		HorizontalLayout loggedInAs = new HorizontalLayout();
		loggedInAs.setSpacing(true);
		loggedInAs.addComponent(checkIcon);
		loggedInAs.addComponent(lbl_loggedInAs);
		loggedInAs.addComponent(lbl_accountName);
		loggedInAs.setComponentAlignment(checkIcon, Alignment.MIDDLE_CENTER);
		loggedInAs.setComponentAlignment(lbl_loggedInAs, Alignment.MIDDLE_CENTER);
		loggedInAs.setComponentAlignment(lbl_accountName, Alignment.MIDDLE_CENTER);

		Button btn_logout = new Button("Logout", new Button.ClickListener() {
			private static final long serialVersionUID = 1401413857588241059L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				UserAuth.logout(VaadinSession.getCurrent());
				getUI().getPage().reload();
			}
		});

		VerticalLayout loginStatusComponent = new VerticalLayout();
		loginStatusComponent.setSizeUndefined();
		loginStatusComponent.setSpacing(true);
		loginStatusComponent.setStyleName("bannerArea-authinfo");
		loginStatusComponent.addComponent(loggedInAs);
		loginStatusComponent.addComponent(btn_logout);
		loginStatusComponent.setComponentAlignment(loggedInAs, Alignment.MIDDLE_LEFT);
		loginStatusComponent.setComponentAlignment(btn_logout, Alignment.MIDDLE_RIGHT);

		Image banner = new Image(null, ThemeResources.img_banner);
		banner.setStyleName("bannerArea-image");

		addComponent(banner);
		addComponent(loginStatusComponent);
		setComponentAlignment(banner, Alignment.MIDDLE_CENTER);
		setComponentAlignment(loginStatusComponent, Alignment.MIDDLE_RIGHT);
		setExpandRatio(banner, 1);
	}

	@Override
	public void attach() {
		super.attach();

		if (WebAppConfiguration.avoidUsingDBForNow()) {
			lbl_accountName.setValue("some_account");
		} else {
			lbl_accountName.setValue(UserAuth.getUserEntity(getSession()).getLogin());
		}
	}
}