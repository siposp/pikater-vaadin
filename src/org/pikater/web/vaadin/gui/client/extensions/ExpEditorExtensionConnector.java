package org.pikater.web.vaadin.gui.client.extensions;

import org.pikater.web.vaadin.gui.client.components.kineticcomponent.KineticComponentConnector;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditorExtension;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

/** 
 * @author SkyCrawl 
 */
@Connect(ExpEditorExtension.class)
public class ExpEditorExtensionConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = 6766120104518020715L;

	// private final ExpEditorExtensionServerRpc serverRPC = RpcProxy.create(ExpEditorExtensionServerRpc.class, this);

	public ExpEditorExtensionConnector() {
		registerRpc(ExpEditorExtensionClientRpc.class, new ExpEditorExtensionClientRpc() {
			private static final long serialVersionUID = 560120982576334694L;

			@Override
			public void command_cacheBoxPictures(final String[] pictureURLs) {
				// built-in image widget does exactly what we want, we only need to create an instance
				final Image[] imageWidgets = new Image[pictureURLs.length];
				for (int i = 0; i < pictureURLs.length; i++) {
					imageWidgets[i] = new Image(pictureURLs[i]);
				}
			}

			@Override
			public void command_resizeSelectedKineticComponent() {
				resizeSelectedKineticComponent();
			}
		});
	}

	//------------------------------------------------------------
	// INHERITED INTERFACE

	@Override
	protected void extend(ServerConnector target) {
		// then prevent accidental loss of work
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			public void onWindowClosing(Window.ClosingEvent closingEvent) {
				/*
				 * TODO: currently unsupported feature
				if(getState().modifiedTabsCount > 0)
				{
					closingEvent.setMessage("There's unsaved content in the editor. Really leave?");
				}
				*/

				closingEvent.setMessage("Really leave? Unsaved content will be lost.");
			}
		});

		// and finally, handle resize events
		Window.addResizeHandler(new ResizeHandler() {
			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					resizeSelectedKineticComponent();
				}
			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250); // only actually resize when the window resize ends
			}
		});
	}

	@Override
	public ExpEditorExtensionSharedState getState() {
		return (ExpEditorExtensionSharedState) super.getState();
	}

	//------------------------------------------------------------
	// THE ACTUAL RESIZE RELATED INTERFACE

	private KineticComponentConnector getKineticConnectorByID(String id) {
		return (KineticComponentConnector) getConnection().getConnector(id, 0);
	}

	private void resizeSelectedKineticComponent() {
		if (getState().currentlySelectedKineticConnectorID != null) {
			getKineticConnectorByID(getState().currentlySelectedKineticConnectorID).getWidget().doResize();
		}
	}
}