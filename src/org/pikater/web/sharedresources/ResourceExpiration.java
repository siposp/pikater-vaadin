package org.pikater.web.sharedresources;

/**
 * Event-based resource expiration definitions.
 * 
 * @author SkyCrawl
 */
public enum ResourceExpiration {
	/**
	 * If this expiration value is assigned to a resource, it will expire
	 * either with the first {@link ResourceRegistrar#getResource(java.util.UUID)}
	 * call or, if the resource is downloadable, when it is first downloaded or
	 * when a download timeout is reached.
	 */
	ON_FIRST_PICKUP,

	/**
	 * This is the default. All resources expire when user logs out or his session
	 * expires.
	 */
	ON_SESSION_END
}