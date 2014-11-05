package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views;

import org.pikater.web.experiment.server.BoxType;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.BoxManagerToolbox;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.IContextForViews;
import org.vaadin.jouni.dom.Dom;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Base class for all {@link BoxManagerToolbox box manager toolbox}
 * views/subviews.
 * 
 * @author SkyCrawl
 * 
 * @param <S>
 *            Data source object type for this view.
 */
public abstract class AbstractBoxManagerView<S extends Object> extends
		VerticalLayout {
	private static final long serialVersionUID = 2129962006939216700L;

	/**
	 * Context providing all necessary information background information.
	 */
	private final IContextForViews context;

	/**
	 * Component displaying the box's identification - category and name.
	 */
	private final Label boxIdentificationLabel;

	/**
	 * Extension providing server-side CSS styling interface to
	 * {@link #boxIdentificationLabel}.
	 */
	private final Dom boxIdentificationStyler;

	/**
	 * Data source for this view.
	 */
	private S currentSource;

	public AbstractBoxManagerView(IContextForViews context) {
		super();

		this.context = context;

		this.boxIdentificationLabel = new Label() {
			private static final long serialVersionUID = -3028878769001633631L;

			@Override
			public void setValue(String newStringValue) {
				super.setValue("Box: " + newStringValue);
			}
		};
		this.boxIdentificationLabel.setSizeUndefined();
		this.boxIdentificationLabel.setStyleName("emphasizedLabel");
		this.boxIdentificationStyler = new Dom(this.boxIdentificationLabel);

		this.currentSource = null;
	}

	/**
	 * A special method to override the box identification text.
	 */
	public void setBoxIdentification(String label) {
		boxIdentificationLabel.setValue(label);
	}

	/**
	 * Comes in handy when the data source for this view gets changed.
	 */
	public void refreshBoxIdentification() {
		boxIdentificationLabel.setValue(String.format(
				"%s@%s",
				BoxType.fromAgentInfo(
						context.getCurrentBoxDataSource().getAssociatedAgent())
						.name(), context.getCurrentBoxDataSource()
						.getAssociatedAgent().getName()));
	}

	/**
	 * Set data source for view. Handles the whole process, nothing else is
	 * needed.
	 */
	public void setContentFrom(S source) {
		if (currentSource != source) {
			try {
				if (source == null) {
					throw new IllegalArgumentException("Null.");
				} else {
					validateSource(source);
				}
			} catch (IllegalArgumentException e) {
				MyNotifications.showError("Can not load the resource",
						e.getMessage());
				return;
			}

			currentSource = source;
			refreshBoxIdentification();
			refreshContent();
		}
	}

	// -------------------------------------------------
	// ABSTRACT INTERFACE

	/**
	 * Throws an exception if something is not right with the given data source.
	 * 
	 * @throws IllegalArgumentException
	 */
	protected abstract void validateSource(S source)
			throws IllegalArgumentException;

	/**
	 * Called when this view's data source is changed so as to refresh the
	 * displayed content for another object.
	 */
	public abstract void refreshContent();

	// -------------------------------------------------
	// PROTECTED INTERFACE

	protected IContextForViews getContext() {
		return context;
	}

	protected Label getBoxIdentificatinComponent() {
		return boxIdentificationLabel;
	}

	protected Dom getBoxIdentificationStyler() {
		return boxIdentificationStyler;
	}

	/**
	 * Gets the data source for this view.
	 */
	protected S getCurrentSource() {
		return currentSource;
	}
}
