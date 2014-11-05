package org.pikater.web.vaadin.gui.server.components.imageviewer;

import org.pikater.web.vaadin.gui.server.StyleBuilder;
import org.pikater.web.vaadin.gui.server.components.scalableimage.ScalableImage;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.HorizontalFlowLayout;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.IFlowLayoutStyleProvider;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;

/**
 * An image viewer for large images capable of zooming in and out.
 * 
 * @author SkyCrawl
 */
@StyleSheet("imageViewer.css")
public class ImageViewer extends VerticalLayout {
	private static final long serialVersionUID = 1080049714636026620L;

	private final ScalableImage scaler;

	public ImageViewer(String imageURL, int imageWidth, int imageHeight) {
		super();
		setPrimaryStyleName("imageViewer");

		Label lbl_zoom = new Label("Zoom level (PCT):");
		lbl_zoom.setSizeUndefined();

		Slider slider = new Slider(0, 2, 2);
		slider.setWidth("200px");
		slider.setImmediate(true);
		slider.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 6756349322188831368L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				scaler.setScaleRatio((Double) event.getProperty().getValue());
			}
		});

		HorizontalFlowLayout toolbar = new HorizontalFlowLayout(new IFlowLayoutStyleProvider() {
			@Override
			public void setStylesForInnerComponent(Component c, StyleBuilder builder) {
				builder.setProperty("margin-left", "10px");
			}
		});
		toolbar.setStyleName("imageViewer-toolbar");
		toolbar.setSizeUndefined();
		toolbar.setWidth("100%");
		toolbar.addComponent(lbl_zoom);
		toolbar.addComponent(slider);

		this.scaler = new ScalableImage(imageURL, imageWidth, imageHeight);
		this.scaler.setSizeFull();
		slider.setValue(new Double(1)); // no zoom level by default

		addComponent(toolbar);
		addComponent(this.scaler);
		setExpandRatio(this.scaler, 1);
	}
}