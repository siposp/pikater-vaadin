package org.pikater.web.vaadin.gui.client.extensions;

import com.vaadin.shared.communication.ClientRpc;

/** 
 * @author SkyCrawl 
 */
public interface ScalableImageExtensionClientRpc extends ClientRpc {
	/**
	 * Sets the image to be contained and its original size. This needs to be called.
	 */
	void setImage(String imageURL, int imageWidth, int imageHeight);

	/**
	 * Sets the underlying image's size. 
	 * @param scaleRatio Percentage of the image's original size. If set to "1", sets
	 * the image to original size.  
	 */
	void setScaleRatio(double scaleRatio);
}
