package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views;

import org.pikater.core.ontology.subtrees.agentinfo.Slot;
import org.pikater.web.experiment.server.BoxSlot;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient.VisualStyle;
import org.pikater.web.vaadin.gui.server.StyleBuilder;
import org.pikater.web.vaadin.gui.server.components.anchor.Anchor;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.HorizontalFlowLayout;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.IFlowLayoutStyleProvider;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.BoxHighlightExtension;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.BoxManagerView;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.IContextForViews;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.visualstyle.KineticBoxSettings;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Subview of overview, built especially for editing slots.
 * 
 * @see {@link BoxManagerView#SLOTVIEW}
 */
public class BoxManagerSlotView extends AbstractBoxManagerView<Slot> {
	private static final long serialVersionUID = 7575836463215779077L;

	private final Anchor link_slotName;
	private final Label lbl_slotType;
	private VerticalLayout vLayout_slots;

	public BoxManagerSlotView(IContextForViews context) {
		super(context);
		setStyleName("boxManagerSlotView");

		Label lbl_slotLabel = new Label("Slot:");
		lbl_slotLabel.setStyleName("emphasizedLabel");

		this.link_slotName = new Anchor("", "");
		this.link_slotName.addStyleName("emphasizedLabel");

		this.lbl_slotType = new Label();
		this.lbl_slotType.setStyleName("emphasizedLabel");

		HorizontalLayout hl_slotIdentification = new HorizontalLayout();
		hl_slotIdentification.setStyleName("viewItem");
		hl_slotIdentification.setSpacing(true);
		hl_slotIdentification.addComponent(lbl_slotLabel);
		hl_slotIdentification.addComponent(this.link_slotName);
		hl_slotIdentification.addComponent(this.lbl_slotType);

		this.vLayout_slots = new VerticalLayout();

		HorizontalLayout hLayout_buttons = new HorizontalLayout();
		hLayout_buttons.setSpacing(true);
		hLayout_buttons.addComponent(new Button("Back to overview",
				new Button.ClickListener() {
					private static final long serialVersionUID = -2304908022626669874L;

					@Override
					public void buttonClick(ClickEvent event) {
						getContext().getView(BoxManagerView.OVERVIEW)
								.refreshContent(); // to update connected / not
													// connected state of slots
						getContext().resetView();
					}
				}));

		addComponent(getBoxIdentificatinComponent());
		addComponent(hl_slotIdentification);
		addComponent(this.vLayout_slots);
		addComponent(hLayout_buttons);
	}

	@Override
	protected void validateSource(Slot source) throws IllegalArgumentException {
		// accept all instances
	}

	@Override
	public void refreshContent() {
		BoxSlot currentEndpoint = new BoxSlot(getContext()
				.getCurrentBoxDataSource(), getCurrentSource());

		this.link_slotName.setValue(currentEndpoint.getChildSlot().getName());
		this.link_slotName.setDescription(currentEndpoint.getChildSlot()
				.getDescription());
		this.lbl_slotType.setValue(String.format("(%s)", currentEndpoint
				.getChildSlotsType().name()));

		VerticalLayout vLayout_slots_new = new VerticalLayout();
		vLayout_slots_new.setStyleName("viewItem");
		vLayout_slots_new.setCaption(String.format("Connected %s slots:",
				currentEndpoint.getChildSlotsType().getOther().name()));
		vLayout_slots_new.setSpacing(true);
		for (final BoxSlot endpoint : getContext().getCurrentComponent()
				.getExperimentGraph().getSlotConnections()
				.getCandidateEndpoints(currentEndpoint)) {
			HorizontalFlowLayout connectionRow = new HorizontalFlowLayout(
					new IFlowLayoutStyleProvider() {
						@Override
						public void setStylesForInnerComponent(Component c,
								StyleBuilder builder) {
							builder.setProperty("margin-left", "5px");
							if (c instanceof CheckBox) {
								builder.setProperty("margin-top", "-1px");
							}
						}
					});
			connectionRow.setStyleName("listItem"); // size is determined in CSS

			new BoxHighlightExtension(
					// mouse over and mouse out events
					getContext().getCurrentComponent().getConnectorId(),
					new Integer[] { endpoint.getParentBox().getID() })
					.extend(connectionRow);

			CheckBox chb_endpoint = new CheckBox(null, getContext()
					.getCurrentComponent()
					.getExperimentGraph()
					.getSlotConnections()
					.areSlotsConnected(getCurrentSource(),
							endpoint.getChildSlot()));
			chb_endpoint.setImmediate(true);
			chb_endpoint
					.addValueChangeListener(new Property.ValueChangeListener() {
						private static final long serialVersionUID = -3307902234166001492L;

						@Override
						public void valueChange(ValueChangeEvent event) {
							if ((Boolean) event.getProperty().getValue()) {
								getContext()
										.getCurrentComponent()
										.getExperimentGraph()
										.getSlotConnections()
										.connect(
												new BoxSlot(
														getContext()
																.getCurrentBoxDataSource(),
														getCurrentSource()),
												endpoint);
							} else {
								getContext()
										.getCurrentComponent()
										.getExperimentGraph()
										.getSlotConnections()
										.disconnect(getCurrentSource(),
												endpoint.getChildSlot());
							}
						}
					});
			Label lbl_caption = new Label(String.format("%s@%s", endpoint
					.getParentBox().getAssociatedAgent().getName(), endpoint
					.getChildSlot().getName()));
			lbl_caption.setSizeUndefined();

			connectionRow.addComponent(chb_endpoint);
			connectionRow.addComponent(lbl_caption);
			vLayout_slots_new.addComponent(connectionRow);
		}
		if (vLayout_slots_new.getComponentCount() > 0) {
			getBoxIdentificationStyler().getStyle().setProperty(
					"background-color",
					KineticBoxSettings.getColor(VisualStyle.SELECTED)
							.toString());
		} else {
			getBoxIdentificationStyler().getStyle().setProperty(
					"background-color", "transparent");
			BoxManagerOverview.correctEmptyLayout(vLayout_slots_new);
		}
		replaceComponent(this.vLayout_slots, vLayout_slots_new);
		this.vLayout_slots = vLayout_slots_new;
	}
}