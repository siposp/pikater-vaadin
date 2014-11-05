package org.pikater.web.sharedresources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.pikater.shared.quartz.PikaterJobScheduler;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.quartzjobs.ResourceExpirationJob;
import org.pikater.web.servlets.DynamicDownloadServlet;
import org.pikater.web.sharedresources.download.IDownloadResource;
import org.pikater.web.vaadin.UserSession;

import com.vaadin.server.VaadinSession;

/**
 * An implementation of sharing "resources" (technically anything, even class instances)
 * between threads or server-client pairs. Resources used are required to be "accessible"
 * from {@link UserSession user session} (and thus tied to it), unless they have an explicit
 * expiration time set.</br>
 * This restriction refers to the {@link #registerResource(VaadinSession, IRegistrarResource)}
 * method.
 * 
 * @author SkyCrawl
 */
public class ResourceRegistrar {
	/**
	 * Dedicated synchronization object.
	 */
	private static final Object LOCK_OBJECT = new Object();

	/**
	 * Collection of registered resources mapped by an ID.
	 */
	private static final Map<UUID, IRegistrarResource> uuidToResource = new HashMap<UUID, IRegistrarResource>();

	//-------------------------------------------------------------
	// GENERAL RESOURCE ROUTINES

	/**
	 * This is the base method for creating download URLs. First, a resource mapping needs
	 * to be created, resource ID returned (which is what this method returns) and then
	 * a download URL may be constructed with {@link #getDownloadURL(UUID)}.
	 */
	public static UUID registerResource(VaadinSession session, IRegistrarResource resource) throws ResourceException {
		/*
		 * Don't forget to keep the invariant mentioned in the class's Javadoc.
		 */

		if (resource == null) {
			throw new ResourceException("Given resource can not be null.");
		} else {
			synchronized (LOCK_OBJECT) {
				UUID newUUID = getNextUIID();
				uuidToResource.put(newUUID, resource);
				switch (resource.getLifeSpan()) {
				case ON_FIRST_PICKUP:
					try {
						// resource will expire on its own if not picked up
						PikaterJobScheduler.getJobScheduler().defineJob(ResourceExpirationJob.class, new Object[] { newUUID, resource });
					} catch (Exception e) {
						/*
						 * Send a runtime exception that will be caught by the default error handler on Vaadin UI,
						 * logged and client will see a notification of an error with 500 status code (internal
						 * server error).
						 */
						throw new ResourceException("Could not issue a resource expiration job.", e);
					}
					break;

				case ON_SESSION_END:
					// resource will be expired manually, when session ends - only remember it for now
					UserSession.rememberSharedResource(VaadinSession.getCurrent(), newUUID);
					break;

				default:
					throw new ResourceException("Unknown state: " + resource.getLifeSpan().name());
				}
				return newUUID;
			}
		}
	}

	/**
	 * Returns whether a resource is mapped to the given ID. 
	 */
	public static boolean isResourceRegistered(UUID resourceID) {
		synchronized (LOCK_OBJECT) {
			return uuidToResource.containsKey(resourceID);
		}
	}

	/**
	 * Gets the resource associated with the given ID. Mind that resource may expire
	 * on first pickup and using this method may expire the returned resource.
	 */
	public static IRegistrarResource getResource(UUID resourceID) {
		synchronized (LOCK_OBJECT) {
			return getResource(resourceID, true);
		}
	}

	/**
	 * Translates the given resource ID to a token (String) so it can be passed (for instance) to another UI
	 * as an URL parameter.
	 */
	public static String fromResourceID(UUID resourceID) {
		return resourceID.toString();
	}

	/**
	 * Translates the given token constructed with the {@link #fromResourceID(UUID)} method
	 * into a resource ID.
	 */
	public static UUID toResourceID(String resourceToken) throws ResourceException {
		if (resourceToken == null) {
			throw new ResourceException("Can not construct resource ID from a null string.");
		} else {
			try {
				return UUID.fromString(resourceToken);
			} catch (IllegalArgumentException e) {
				throw new ResourceException(e);
			}
		}
	}

	/**
	 * Handles exceptions thrown from methods of this class. If the exception is
	 * an unknown error case, this method will throw a {@link RuntimeException}
	 * and output {@link HttpServletResponse#SC_NOT_FOUND} error status.
	 * 
	 * {@link RuntimeException RuntimeExceptions} will be caught by the default
	 * UI error handler and a visible notification will be displayed to user.
	 */
	public static void handleError(Exception e, HttpServletResponse resp) {
		if (e instanceof ResourceException) {
			/*
			 * Covers known cases that do not need to be logged. 
			 */

			try {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		} else {
			/*
			 * Covers unknown cases that need to be logged.
			 */

			PikaterWebLogger.logThrowable("An unexpected problem was found:", e);
			try {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				throw new RuntimeException(e);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	//-------------------------------------------------------------
	// DOWNLOADABLE RESOURCE ROUTINES

	/**
	 * Creates a download URL for the given resource ID. The associated resource
	 * must be an instance of {@link IDownloadResource}. The returned URL points to
	 * {@link DynamicDownloadServlet}.
	 */
	public static String getDownloadURL(UUID resourceID) throws ResourceException {
		synchronized (LOCK_OBJECT) {
			IRegistrarResource resource = null;
			try {
				resource = getResource(resourceID, false);
			} catch (IllegalStateException e) {
				throw new ResourceException("Resource not found.");
			}

			if (resource instanceof IDownloadResource) {
				return String.format("./download?t=%s", fromResourceID(resourceID));
			} else {
				throw new ResourceException("Resource associated with the given ID is not downloadable.");
			}
		}
	}

	//-------------------------------------------------------------
	// RESOURCE EXPIRATION INTERFACE

	/**
	 * Expires a resource that was to be expired on first pickup.
	 */
	public static void expireOnFirstPickupResource(UUID resourceID, IRegistrarResource resource) {
		synchronized (LOCK_OBJECT) {
			if (uuidToResource.containsKey(resourceID) && uuidToResource.get(resourceID).equals(resource)) {
				resourceExpired(resourceID);
			}
		}
	}

	/**
	 * Method to be used when a resource expires.
	 */
	public static void expireSessionResources(VaadinSession session) {
		synchronized (LOCK_OBJECT) {
			for (UUID resourceID : UserSession.getSharedResources(session)) {
				if (uuidToResource.containsKey(resourceID) && uuidToResource.get(resourceID).getLifeSpan() == ResourceExpiration.ON_SESSION_END) {
					resourceExpired(resourceID);
				}
			}
		}
	}

	private static void resourceExpired(UUID resourceID) {
		uuidToResource.remove(resourceID);
	}

	//-----------------------------------------------------------------------------
	// PRIVATE INTERFACE

	private static IRegistrarResource getResource(UUID resourceID, boolean deleteIfFound) throws IllegalStateException {
		if (uuidToResource.containsKey(resourceID)) {
			IRegistrarResource resource = uuidToResource.get(resourceID);
			if ((resource.getLifeSpan() == ResourceExpiration.ON_FIRST_PICKUP) && deleteIfFound) {
				resourceExpired(resourceID);
			}
			return resource;
		} else {
			throw new IllegalStateException("No resource found for the given ID.");
		}
	}

	private static UUID getNextUIID() {
		UUID newUUID = null;
		boolean continueGenerating = true;
		while (continueGenerating) {
			newUUID = UUID.randomUUID();
			continueGenerating = uuidToResource.containsKey(newUUID);
		}
		return newUUID;
	}
}
