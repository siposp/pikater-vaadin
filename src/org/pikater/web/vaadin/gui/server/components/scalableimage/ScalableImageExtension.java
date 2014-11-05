package org.pikater.web.vaadin.gui.server.components.scalableimage;

import org.pikater.web.vaadin.gui.client.extensions.ScalableImageExtensionClientRpc;
import org.pikater.web.vaadin.gui.client.extensions.ScalableImageExtensionServerRpc;

import com.vaadin.server.AbstractExtension;

/**
 * Vaadin extension implementing the conduct of {@link ScalableImage}.
 * 
 * @author SkyCrawl
 */
public class ScalableImageExtension extends AbstractExtension {
	private static final long serialVersionUID = 8278201529558658998L;

	public ScalableImageExtension(ScalableImage scalableImageComponent) {
		registerRpc(new ScalableImageExtensionServerRpc() {
			private static final long serialVersionUID = 6572754997353185232L;
		});
		extend(scalableImageComponent);
	}

	public ScalableImageExtensionClientRpc getClientRPC() {
		return getRpcProxy(ScalableImageExtensionClientRpc.class);
	}
}