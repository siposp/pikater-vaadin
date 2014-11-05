package org.pikater.web.sharedresources.download;

import java.io.File;

import org.pikater.web.sharedresources.ResourceExpiration;

/**
 * A file-backed downloadable image resource with some extra information.
 * 
 * @author SkyCrawl
 */
public class ImageDownloadResource extends FileDownloadResource {
	private final int imageWidth;
	private final int imageHeight;

	public ImageDownloadResource(File file, ResourceExpiration expiration, String mimeType, int imageWidth, int imageHeight) {
		super(file, expiration, mimeType);

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}
}