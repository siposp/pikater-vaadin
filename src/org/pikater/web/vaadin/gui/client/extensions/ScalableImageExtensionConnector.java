package org.pikater.web.vaadin.gui.client.extensions;

import org.pikater.web.vaadin.gui.server.components.scalableimage.ScalableImageExtension;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.customlayout.CustomLayoutConnector;
import com.vaadin.shared.ui.Connect;

/** 
 * @author SkyCrawl 
 */
@Connect(ScalableImageExtension.class)
public class ScalableImageExtensionConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = 6766120104518020715L;

	// private final ScalableImageExtensionServerRpc serverRPC = RpcProxy.create(ScalableImageExtensionServerRpc.class, this);

	private Element imgTag;
	private int originalImgWidth;
	private int originalImgHeight;

	public ScalableImageExtensionConnector() {
		registerRpc(ScalableImageExtensionClientRpc.class, new ScalableImageExtensionClientRpc() {
			private static final long serialVersionUID = 5749987507481194601L;

			@Override
			public void setScaleRatio(final double scaleRatio) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						imgTag.getStyle().setWidth(originalImgWidth * scaleRatio, Unit.PX);
						imgTag.getStyle().setHeight(originalImgHeight * scaleRatio, Unit.PX);
					}
				});
			}

			@Override
			public void setImage(final String imageURL, final int imageWidth, final int imageHeight) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						imgTag.setAttribute("src", imageURL);
						originalImgWidth = imageWidth;
						originalImgHeight = imageHeight;
					}
				});
			}
		});
	}

	@Override
	protected void extend(ServerConnector target) {
		final Widget extendedWidget = ((CustomLayoutConnector) target).getWidget();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				Element containingDiv = extendedWidget.getElement().getFirstChildElement();
				Element containingTd = containingDiv.getFirstChildElement().getFirstChildElement().getFirstChildElement().getFirstChildElement();
				imgTag = containingTd.getFirstChildElement();
			}
		});
	}
}