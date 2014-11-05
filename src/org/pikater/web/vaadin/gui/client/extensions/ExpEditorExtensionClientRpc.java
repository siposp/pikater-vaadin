package org.pikater.web.vaadin.gui.client.extensions;

import com.vaadin.shared.communication.ClientRpc;

/** 
 * @author SkyCrawl 
 */
public interface ExpEditorExtensionClientRpc extends ClientRpc {
	/**
	 * These pictures will not be used directly in Vaadin but rather in the Kinetic environment
	 * on the client. When drawing boxes in the Kinetic stage, images need to be already loaded
	 * on the client (downloaded and ready), otherwise Kinetic won't draw them correctly.
	 * That is what this method aims to do.
	 */
	void command_cacheBoxPictures(String[] pictureURLs);

	/**
	 * Called on events that might change the kinetic component's parent's size.
	 * If this happens, the underlying kinetic {@link net.edzard.kinetic.Stage}
	 * needs to be resized manually as well.
	 */
	void command_resizeSelectedKineticComponent();
}
