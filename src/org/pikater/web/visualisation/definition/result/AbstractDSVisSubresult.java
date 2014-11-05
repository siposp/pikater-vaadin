package org.pikater.web.visualisation.definition.result;

import java.io.File;

import org.pikater.shared.util.IOUtils;
import org.pikater.web.ImageType;

/**
 * Base class to wrap the generated visualization images and provide some
 * additional interface.
 * 
 * @author SkyCrawl
 *
 * @param <I> The type to index subresults with.
 */
public abstract class AbstractDSVisSubresult<I> {
	/**
	 * The type of the generated image.
	 */
	private final ImageType imageType;

	/**
	 * A handle to the generated image's file.
	 */
	private final File imageFile;

	public AbstractDSVisSubresult(ImageType imageType) {
		this.imageType = imageType;
		this.imageFile = IOUtils.createTemporaryFile("visualization", imageType.toFileExtension());
	}

	public ImageType getImageType() {
		return imageType;
	}

	public File getFile() {
		return imageFile;
	}

	/**
	 * Deletes and clears the generated subresult. 
	 */
	public void destroy() {
		imageFile.delete();
	}

	//----------------------------------------------------
	// SOME REQUIRED ABSTRACT INTERFACE

	/**
	 * Gets the Y index associated to this subresult.
	 */
	public abstract I toLeftIndex();

	/**
	 * Gets the X index associated to this subresult.
	 */
	public abstract I toTopIndex();
}
