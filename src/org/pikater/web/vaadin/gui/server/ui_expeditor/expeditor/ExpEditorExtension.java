package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor;

import java.util.HashSet;
import java.util.Set;

import org.pikater.web.vaadin.gui.client.extensions.ExpEditorExtensionClientRpc;
import org.pikater.web.vaadin.gui.client.extensions.ExpEditorExtensionServerRpc;
import org.pikater.web.vaadin.gui.client.extensions.ExpEditorExtensionSharedState;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;

import com.vaadin.server.AbstractExtension;

/**
 * Extension implementing some add-on supporting features to
 * {@link ExpEditor experiment editor}.
 * 
 * @author SkyCrawl
 */
public class ExpEditorExtension extends AbstractExtension {
	private static final long serialVersionUID = 8278201529558658998L;

	private final Set<KineticComponent> modifiedContent;

	public ExpEditorExtension() {
		this.modifiedContent = new HashSet<KineticComponent>();
		registerRpc(new ExpEditorExtensionServerRpc() {
			private static final long serialVersionUID = 216446786216335413L;
		});
	}

	@Override
	public ExpEditorExtensionSharedState getState() {
		return (ExpEditorExtensionSharedState) super.getState();
	}

	/**
	 * Exposing the inherited API.
	 */
	public void extend(ExpEditor expEditor) {
		super.extend(expEditor);
	}

	/**
	 * Get handle to client commands.
	 */
	public ExpEditorExtensionClientRpc getClientRPC() {
		return getRpcProxy(ExpEditorExtensionClientRpc.class);
	}

	/**
	 * @deprecated Not supported at the moment.
	 */
	@Deprecated
	public void setKineticContentModified(KineticComponent content, boolean modified) {
		if (modified ? modifiedContent.add(content) : modifiedContent.remove(content)) {
			// modified content size changed
			getState().modifiedTabsCount = modifiedContent.size();
		}
	}
}
