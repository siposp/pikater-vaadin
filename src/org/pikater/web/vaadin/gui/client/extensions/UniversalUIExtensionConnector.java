package org.pikater.web.vaadin.gui.client.extensions;

import org.pikater.web.vaadin.UniversalUIExtension;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTLogger;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

/** 
 * @author SkyCrawl 
 */
@Connect(UniversalUIExtension.class)
public class UniversalUIExtensionConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = 6766120104518020715L;

	private final UniversalUIExtensionServerRpc serverRPC = RpcProxy.create(UniversalUIExtensionServerRpc.class, this);

	public UniversalUIExtensionConnector() {
		/*
		registerRpc(ClientLoggingExtensionClientRpc.class, new ClientLoggingExtensionClientRpc()
		{
			private static final long serialVersionUID = 5749987507481194601L;
		});
		*/
	}

	@Override
	protected void extend(ServerConnector target) {
		if (!GWTLogger.isLoggerSet()) {
			GWTLogger.setRemoteLogger(serverRPC);
		}
	}
}