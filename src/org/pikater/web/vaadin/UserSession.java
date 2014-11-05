package org.pikater.web.vaadin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.vaadin.server.VaadinSession;

/**
 * A utility class that manages all attributes the application stores in user
 * {@link VaadinSession sessions}. Do note, however, that it is not a
 * "session implementation". On the contrary, the session in question is always
 * required as a method argument.
 * 
 * @author SkyCrawl
 */
public class UserSession {
	private static final String KEY_USER_ID = "k_uid";
	private static final String KEY_USER_UPLOADS = "k_uu";
	private static final String KEY_SHARED_RESOURCES = "k_sr";

	// ----------------------------------------------------------
	// PRIVATE INTERFACE (INFRASTRUCTURE)

	private static void setAttribute(VaadinSession session, String key,
			Object value) {
		session.setAttribute(key, value);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Object> T getAttribute(VaadinSession session,
			String key) {
		return (T) session.getAttribute(key);
	}

	// ----------------------------------------------------------
	// INTERFACE MANIPULATING WITH INDIVIDUAL ATTRIBUTES

	public static Integer getUserID(VaadinSession session) {
		return getAttribute(session, UserSession.KEY_USER_ID);
	}

	public static void storeUserID(VaadinSession session, Integer userID) {
		setAttribute(session, UserSession.KEY_USER_ID, userID);
	}

	public static UserUploads getUserUploadManager(VaadinSession session) {
		return getAttribute(session, UserSession.KEY_USER_UPLOADS);
	}

	public static void storeUserUploadManager(VaadinSession session,
			UserUploads manager) {
		setAttribute(session, UserSession.KEY_USER_UPLOADS, new UserUploads());
	}

	/**
	 * <p>
	 * All shared resources registered with {@link ResourceRegistrar} and
	 * {@link ResourceExpiration#ON_SESSION_END} are also required to be stored
	 * in the user's session for eventual cleanup.
	 * </p>
	 * 
	 * <p>
	 * Other resources may be stored too.
	 * </p>
	 * 
	 * @param session
	 *            user's session
	 * @param resourceID
	 *            the identifier assigned to the given resource by
	 *            {@link ResourceRegistrar}
	 */
	public static void rememberSharedResource(VaadinSession session,
			UUID resourceID) {
		Set<UUID> sharedResources = getSharedResources(session);
		if (sharedResources == null) {
			sharedResources = new HashSet<UUID>();
		}
		if (sharedResources.add(resourceID)) {
			session.setAttribute(KEY_SHARED_RESOURCES, sharedResources);
		}
	}

	/**
	 * Returns all identifiers stored by
	 * {@link #rememberSharedResource(VaadinSession, UUID)} method.
	 * 
	 * @param session
	 *            user's session
	 */
	public static Set<UUID> getSharedResources(VaadinSession session) {
		return getAttribute(session, KEY_SHARED_RESOURCES);
	}
}
