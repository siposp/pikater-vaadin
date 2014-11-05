package org.pikater.web.vaadin.gui.client.extensions;

import com.vaadin.shared.communication.ServerRpc;

/** 
 * @author SkyCrawl 
 */
public interface UniversalUIExtensionServerRpc extends ServerRpc {
	void logWarning(String message);

	void logThrowable(String message, String throwableStackTrace);

	/**
	 * This method should only be used to "inform" the server of
	 * an uncaught native client exception. There is no argument because:
	 * <ul>
	 * <li> It's not easy to serialize native javascript exceptions.
	 * <li> It's even pointless to do so because they contain references
	 * to obfuscated GWT javascript code and are completely useless.
	 * </ul> 
	 */
	void logUncaughtNativeClientException();
}