package org.pikater.web.sharedresources;

import org.pikater.web.servlets.DynamicDownloadServlet;

/**
 * Common interface for all shared resources of the web application. Resources are registered
 * with {@link ResourceRegistrar} and can be:
 * <ul>
 * <li> streamed outside the application with {@link DynamicDownloadServlet},
 * <li> shared internally between threads/components with {@link ResourceRegistrar}.
 * </ul>
 * 
 * @author SkyCrawl
 */
public interface IRegistrarResource {
	/**
	 * Gets the resource's expiration time (event-based).
	 */
	ResourceExpiration getLifeSpan();
}
