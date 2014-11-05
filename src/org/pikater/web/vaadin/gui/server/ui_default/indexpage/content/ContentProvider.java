package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content;

import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.vaadin.CustomConfiguredUI;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.IndexPage;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.LeftMenu;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.agents.AgentsView;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.agents.UserAgentsView;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.batches.BatchesView;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.batches.UserBatchesView;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets.DatasetsView;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets.UserDatasetsView;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.test.TestView;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.users.UserProfileView;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.users.UsersView;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinSession;

/**
 * All features (components intended to be displayed in the content
 * area of {@link DefaultUI}) should be registered here.
 * 
 * @author SkyCrawl
 */
public class ContentProvider {
	/**
	 * Interface required for each feature.
	 * 
	 * @author SkyCrawl
	 */
	public interface IWebFeature {
		/**
		 * Whether the currently authenticated user (or a guest user)
		 * has access to this feature.
		 */
		boolean accessAllowed(VaadinSession session);

		/**
		 * Gets the menu caption for this feature.
		 * @see {@link LeftMenu}
		 */
		String toMenuCaption();

		/**
		 * Gets the URL fragment used to represent this feature.
		 * @see {@link Navigator}
		 * @see {@link IndexPage}
		 */
		String toNavigatorName();

		/**
		 * Gets the class of the component implementing this feature.
		 */
		Class<? extends IContentComponent> toComponentClass();
	}

	/**
	 * Common enumeration for all "miscellaneous" features.
	 * 
	 * @author SkyCrawl
	 */
	public enum DefaultFeature implements IWebFeature {
		// IMPORTANT: always bind the default content with empty navigator name.
		WELCOME("", WelcomeView.class), TEST("test", TestView.class);

		private final Class<? extends IContentComponent> mappedComponent;
		private final String navigatorName;

		private DefaultFeature(String navigatorName, Class<? extends IContentComponent> mappedComponent) {
			this.navigatorName = navigatorName;
			this.mappedComponent = mappedComponent;
		}

		@Override
		public boolean accessAllowed(VaadinSession session) {
			switch (this) {
			case TEST:
				return CustomConfiguredUI.isDebugModeActive();
			case WELCOME:
				return true; // this feature set is for everyone
			default:
				throw new IllegalStateException("Unknown state: " + name());
			}
		}

		@Override
		public String toMenuCaption() {
			return null;
		}

		@Override
		public String toNavigatorName() {
			return navigatorName;
		}

		@Override
		public Class<? extends IContentComponent> toComponentClass() {
			return mappedComponent;
		}
	}

	/**
	 * Common enumeration for all administrator features.
	 * 
	 * @author SkyCrawl
	 */
	public enum AdminFeature implements IWebFeature {
		// IMPORTANT: all navigator names should start with "admin". See {@link #getFeatureFromNavigatorName} below.
		VIEW_USERS("adminAllUsers", UsersView.class), VIEW_DATASETS("adminAllDatasets", DatasetsView.class), VIEW_METHODS("adminAllUserAgents", AgentsView.class), VIEW_EXPERIMENTS(
				"adminAllExperiments", BatchesView.class);

		private final Class<? extends IContentComponent> mappedComponent;
		private final String navigatorName;

		private AdminFeature(String navigatorName, Class<? extends IContentComponent> mappedComponent) {
			this.navigatorName = navigatorName;
			this.mappedComponent = mappedComponent;
		}

		@Override
		public String toMenuCaption() {
			switch (this) {
			case VIEW_USERS:
				return "All users";
			case VIEW_DATASETS:
				return "All datasets";
			case VIEW_METHODS:
				return "All user agents";
			case VIEW_EXPERIMENTS:
				return "All experiments";
			default:
				throw new IllegalStateException("Unknown state: " + name());
			}
		}

		@Override
		public boolean accessAllowed(VaadinSession session) {
			return WebAppConfiguration.avoidUsingDBForNow() ? true : UserAuth.getUserEntity(session).isAdmin(); // only allowed for admins
		}

		@Override
		public String toNavigatorName() {
			return navigatorName;
		}

		@Override
		public Class<? extends IContentComponent> toComponentClass() {
			return mappedComponent;
		}
	}

	/**
	 * Common enumeration for all user features.
	 * 
	 * @author SkyCrawl
	 */
	public enum UserFeature implements IWebFeature {
		// IMPORTANT: all navigator names should start with "user". See {@link #getFeatureFromNavigatorName} below.
		VIEW_PROFILE("userProfile", UserProfileView.class), VIEW_DATASETS("userDatasets", UserDatasetsView.class), VIEW_METHODS("userAgents", UserAgentsView.class), VIEW_EXPERIMENTS(
				"userExperiments", UserBatchesView.class);

		private final Class<? extends IContentComponent> mappedComponent;
		private final String navigatorName;

		private UserFeature(String navigatorName, Class<? extends IContentComponent> mappedComponent) {
			this.navigatorName = navigatorName;
			this.mappedComponent = mappedComponent;
		}

		@Override
		public boolean accessAllowed(VaadinSession session) {
			if (WebAppConfiguration.avoidUsingDBForNow()) {
				return true;
			} else {
				return UserAuth.isUserAuthenticated(session); // only allowed for authenticated users
			}
		}

		@Override
		public String toMenuCaption() {
			switch (this) {
			case VIEW_PROFILE:
				return "View & edit profile";
			case VIEW_DATASETS:
				return "My datasets";
			case VIEW_METHODS:
				return "My agents";
			case VIEW_EXPERIMENTS:
				return "My experiments";
			default:
				throw new IllegalStateException("Unknown state: " + name());
			}
		}

		@Override
		public String toNavigatorName() {
			return navigatorName;
		}

		@Override
		public Class<? extends IContentComponent> toComponentClass() {
			return mappedComponent;
		}
	}

	/**
	 * In other words, gets the feature represented by the given URL fragment. 
	 */
	public static IWebFeature getFeatureFromNavigatorName(String navigatorName) {
		if (navigatorName == null) {
			return null;
		} else if (navigatorName.startsWith("admin")) {
			return getFeatureFromNavigatorName(navigatorName, AdminFeature.values());
		} else if (navigatorName.startsWith("user")) {
			return getFeatureFromNavigatorName(navigatorName, UserFeature.values());
		} else {
			return getFeatureFromNavigatorName(navigatorName, DefaultFeature.values());
		}
	}

	private static IWebFeature getFeatureFromNavigatorName(String navigatorName, IWebFeature[] features) {
		for (IWebFeature feature : features) {
			if (feature.toNavigatorName().equalsIgnoreCase(navigatorName)) {
				return feature;
			}
		}
		return null;
	}
}
