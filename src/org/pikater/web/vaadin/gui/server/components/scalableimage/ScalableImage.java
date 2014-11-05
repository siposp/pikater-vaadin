package org.pikater.web.vaadin.gui.server.components.scalableimage;

import org.pikater.web.vaadin.gui.client.extensions.ScalableImageExtensionClientRpc;

import com.vaadin.ui.CustomLayout;

/**
 * A CSS implementation of a scaled image component. Use
 * {@link #setScaleRatio(double)}.
 * 
 * @author SkyCrawl
 */
public class ScalableImage extends CustomLayout implements ScalableImageExtensionClientRpc {
	private static final long serialVersionUID = -5047809273477362373L;

	private final ScalableImageExtension extension;

	public ScalableImage(String imageURL, int imageWidth, int imageHeight) {
		this(null, imageURL, imageWidth, imageHeight);
	}

	public ScalableImage(String caption, String imageURL, int imageWidth, int imageHeight) {
		super("scalableImageLayout");
		setCaption(caption);

		this.extension = new ScalableImageExtension(this);
		setImage(imageURL, imageWidth, imageHeight);
	}

	@Override
	public void setImage(String imageURL, int imageWidth, int imageHeight) {
		extension.getClientRPC().setImage(imageURL, imageWidth, imageHeight);
	}

	public void setScaleRatio(double scaleRatio) {
		extension.getClientRPC().setScaleRatio(scaleRatio);
	}
}