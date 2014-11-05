package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.pikater.core.ontology.subtrees.agentinfo.Slot;
import org.pikater.core.ontology.subtrees.newoption.base.NewOption;
import org.pikater.web.experiment.server.BoxInfoServer;
import org.pikater.web.experiment.server.BoxSlot;
import org.pikater.web.vaadin.gui.server.StyleBuilder;
import org.pikater.web.vaadin.gui.server.components.led.LedIndicator;
import org.pikater.web.vaadin.gui.server.components.led.LedIndicator.LedIndicatorTheme;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.HorizontalFlowLayout;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.IFlowLayoutStyleProvider;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.VerticalFlowLayout;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.BoxHighlightExtension;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.BoxManagerView;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.IContextForViews;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Subview of overview, built especially for editing slots.
 * 
 * @see {@link BoxManagerView#OVERVIEW}
 */
public class BoxManagerOverview extends AbstractBoxManagerView<BoxInfoServer> {
	private static final long serialVersionUID = 2987699648757179970L;

	private final VerticalFlowLayout innerLayout;

	public BoxManagerOverview(IContextForViews context) {
		super(context);
		setStyleName("boxManagerOverview");
		getBoxIdentificatinComponent().addStyleName("viewItem");

		this.innerLayout = new VerticalFlowLayout(null);
		this.innerLayout.setSizeFull();

		addComponent(getBoxIdentificatinComponent());
		addComponent(this.innerLayout);
		setExpandRatio(this.innerLayout, 1);
	}

	@Override
	protected void validateSource(BoxInfoServer source)
			throws IllegalArgumentException {
		// accept all instances
	}

	@Override
	public void refreshContent() {
		innerLayout.removeAllComponents();
		refreshOptions();
		refreshSlots();
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.innerLayout.setEnabled(enabled);
	}

	// ---------------------------------------------------------------------------
	// MAIN REFRESH ROUTINES

	private void refreshOptions() {
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setCaption("Options:");
		vLayout.setSpacing(true);
		vLayout.addStyleName("viewItem");
		for (final NewOption option : getCurrentSource().getAssociatedAgent()
				.getOptions()) {
			vLayout.addComponent(createLabeledRow(option.getName(), option
					.getDescription(), new Button("Edit",
					new Button.ClickListener() {
						private static final long serialVersionUID = 4258994618274439965L;

						@Override
						public void buttonClick(ClickEvent event) {
							BoxManagerOptionView optionView = (BoxManagerOptionView) getContext()
									.getView(BoxManagerView.OPTIONVIEW);
							optionView.setContentFrom(option);
							getContext().setView(optionView);
						}
					})));
		}
		correctEmptyLayout(vLayout);
		this.innerLayout.addComponent(vLayout);
	}

	private void refreshSlots() {
		this.innerLayout.addComponent(getSlotLayout("Input slots:",
				getCurrentSource().getAssociatedAgent().getInputSlots()));
		this.innerLayout.addComponent(getSlotLayout("Output slots:",
				getCurrentSource().getAssociatedAgent().getOutputSlots()));
	}

	// ---------------------------------------------------------------------------
	// SECONDARY REFRESH ROUTINES

	private VerticalLayout getSlotLayout(String caption, List<Slot> source) {
		VerticalLayout vLayout_slots = new VerticalLayout();
		vLayout_slots.setCaption(caption);
		vLayout_slots.setSpacing(true);
		vLayout_slots.addStyleName("viewItem");
		for (final Slot slot : source) {
			vLayout_slots.addComponent(createSlotRow(slot, new Button("Edit",
					new Button.ClickListener() {
						private static final long serialVersionUID = -4121772417813177161L;

						@Override
						public void buttonClick(ClickEvent event) {
							BoxManagerSlotView slotView = (BoxManagerSlotView) getContext()
									.getView(BoxManagerView.SLOTVIEW);
							slotView.setContentFrom(slot);
							getContext().setView(slotView);
						}
					})));
		}
		correctEmptyLayout(vLayout_slots);
		return vLayout_slots;
	}

	private Component createSlotRow(final Slot slot, Button actionButton) {
		Set<BoxSlot> connectedEndpoints = getContext().getCurrentComponent()
				.getExperimentGraph().getSlotConnections()
				.getConnectedValidEndpoints(slot);
		boolean isSlotConnected = !connectedEndpoints.isEmpty();
		LedIndicator ledComponent = new LedIndicator(
				getIndicatorTheme(isSlotConnected));
		ledComponent.setDescription(isSlotConnected ? "Slot IS connected."
				: "Slot is NOT connected.");

		HorizontalFlowLayout fLayout = (HorizontalFlowLayout) createLabeledRow(
				slot.getName(), slot.getDescription(), actionButton);
		fLayout.addComponentAsFirst(ledComponent);
		if (isSlotConnected) {
			new BoxHighlightExtension(
					// mouse over and mouse out events
					getContext().getCurrentComponent().getConnectorId(),
					endpointSetToIDArray(connectedEndpoints)).extend(fLayout);
		}
		return fLayout;
	}

	private Integer[] endpointSetToIDArray(Set<BoxSlot> list) {
		List<Integer> result = new ArrayList<Integer>();
		for (BoxSlot endPoint : list) {
			result.add(endPoint.getParentBox().getID());
		}
		return result.toArray(new Integer[0]);
	}

	private LedIndicatorTheme getIndicatorTheme(boolean slotHasAValidConnection) {
		return slotHasAValidConnection ? LedIndicatorTheme.GREEN
				: LedIndicatorTheme.RED;
	}

	// ---------------------------------------------------------------------------
	// SOME UI CONSTRUCTION METHODS

	public static Component createLabeledRow(String label, String description,
			Button actionButton) {
		HorizontalFlowLayout fLayout = new HorizontalFlowLayout(
				new IFlowLayoutStyleProvider() {
					@Override
					public void setStylesForInnerComponent(Component c,
							StyleBuilder builder) {
						if (c instanceof LedIndicator) {
							// it is assumed here that this method is used to
							// create slot rows too
							builder.setProperty("display", "table"); // vertically
																		// centers
						} else if (c instanceof Label) {
							builder.setProperty("padding-left", "10px");
						}
					}
				});
		fLayout.setStyleName("listItem"); // size is determined in CSS

		Label lbl_option = new Label(label);
		lbl_option.setSizeUndefined();
		lbl_option.setDescription(description);

		fLayout.addComponent(lbl_option);
		if (actionButton != null) {
			fLayout.addComponentToRight(actionButton);
		}
		return fLayout;
	}

	public static void correctEmptyLayout(AbstractOrderedLayout layout) {
		if (layout.getComponentCount() == 0) // no slots are defined
		{
			layout.addComponent(createLabeledRow("None", null, null));
		}
	}
}