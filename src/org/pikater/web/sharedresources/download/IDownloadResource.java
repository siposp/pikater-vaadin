package org.pikater.web.sharedresources.download;

import java.io.InputStream;

import org.pikater.web.HttpContentType;
import org.pikater.web.servlets.DynamicDownloadServlet;
import org.pikater.web.sharedresources.IRegistrarResource;

/**
 * An interface which every downloadable resource ({@link DynamicDownloadServlet}
 * must implement. Nothing else is strictly required.
 * 
 * @author SkyCrawl
 */
public interface IDownloadResource extends IRegistrarResource {
	/**
	 * Returns the future filename of this resource on the client's machine.
	 * Needed for browsers.
	 */
	String getFilename();

	/**
	 * Returns the <a href="http://en.wikipedia.org/wiki/Internet_media_type">mime type</a>
	 * of this resource. Needed for browsers.
	 * @see {@link HttpContentType}
	 */
	String getMimeType();

	/**
	 * Returns the size of the resource. Needed for browsers.
	 */
	long getSize();

	/**
	 * Returns a binary stream of the resource.
	 * @throws Exception
	 */
	InputStream getStream() throws Exception;
}
