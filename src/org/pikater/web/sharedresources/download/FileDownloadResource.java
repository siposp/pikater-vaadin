package org.pikater.web.sharedresources.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.pikater.web.servlets.DynamicDownloadServlet;
import org.pikater.web.sharedresources.ResourceExpiration;

/**
 * A generic class wrapping files in downloadable resources.
 * 
 * @see {@link DynamicDownloadServlet}
 */
public class FileDownloadResource implements IDownloadResource {
	private final File file;
	private final ResourceExpiration expiration;
	private final String mimeType;

	public FileDownloadResource(File file, ResourceExpiration expiration, String mimeType) {
		this.file = file;
		this.expiration = expiration;
		this.mimeType = mimeType;
	}

	@Override
	public ResourceExpiration getLifeSpan() {
		return expiration;
	}

	@Override
	public String getFilename() {
		return file.getName();
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public long getSize() {
		return file.length();
	}

	@Override
	public InputStream getStream() throws Exception {
		return new FileInputStream(file);
	}
}