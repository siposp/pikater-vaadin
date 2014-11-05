package org.pikater.web.vaadin.gui.server.layouts.borderlayout;

import org.pikater.web.vaadin.gui.client.extensions.AutoVerticalBorderLayoutExtensionClientRpc;
import org.pikater.web.vaadin.gui.client.extensions.AutoVerticalBorderLayoutExtensionServerRpc;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.CustomLayout;

/**
 * Extension for {@link AutoVerticalBorderLayout} to make it work - all
 * of its features are really implemented on the client.
 * 
 * @author SkyCrawl
 */
public class AutoVerticalBorderLayoutExtension extends AbstractExtension {
	private static final long serialVersionUID = 8278201529558658998L;

	public AutoVerticalBorderLayoutExtension() {
		registerRpc(new AutoVerticalBorderLayoutExtensionServerRpc() {
			private static final long serialVersionUID = -2652556748569844059L;
		});
	}

	public AutoVerticalBorderLayoutExtensionClientRpc getClientRPC() {
		return getRpcProxy(AutoVerticalBorderLayoutExtensionClientRpc.class);
	}

	/**
	 * Exposing the inherited API.
	 */
	public void extend(CustomLayout layout) {
		super.extend(layout);
	}
}
