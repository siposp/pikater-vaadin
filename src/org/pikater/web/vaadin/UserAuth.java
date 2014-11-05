package org.pikater.web.vaadin;

import org.pikater.shared.database.jpa.JPAUser;
import org.pikater.shared.database.jpa.daos.DAOs;
import org.pikater.shared.database.jpa.daos.AbstractDAO.EmptyResultAction;
import org.pikater.web.config.WebAppConfiguration;

import com.vaadin.server.VaadinSession;

/**
 * <p>
 * A utility class that manages information about the currently logged-in user.
 * For that purpose, {@link UserSession} is used.
 * </p>
 * 
 * <p>
 * Do note, that this is not a "session implementation". On the contrary, the
 * session in question is always required as a method argument.
 * </p>
 * 
 * @author SkyCrawl
 */
public class UserAuth {
	public static Integer getUserID(VaadinSession session) {
		if (session == null) {
			throw new NullPointerException(
					"Session is null. Did you use the 'getSession()' method? If so, make sure you"
							+ "use it from the 'AbstractComponent.attach' method or just use 'VaadinSession.getCurrent()' instead.");
		} else {
			return UserSession.getUserID(session);
		}
	}

	public static JPAUser getUserEntity(VaadinSession session) {
		if (WebAppConfiguration.avoidUsingDBForNow()) {
			return JPAUser.getDummy();
		} else {
			return DAOs.userDAO.getByID(getUserID(session),
					EmptyResultAction.LOG_NULL);
		}
	}

	public static boolean isUserAuthenticated(VaadinSession session) {
		return getUserID(session) != null;
	}

	public static void login(VaadinSession session, int userID) {
		if (isUserAuthenticated(session)) {
			throw new IllegalStateException(
					"User is already authenticated in this session.");
		} else {
			UserSession.storeUserID(session, userID);
			UserSession.storeUserUploadManager(session, new UserUploads());
		}
	}

	public static void logout(VaadinSession session) {
		if (isUserAuthenticated(session)) {
			UserSession.storeUserUploadManager(session, null);
			UserSession.storeUserID(session, null);
		} else {
			throw new IllegalStateException("User is not authenticated.");
		}
	}
}
