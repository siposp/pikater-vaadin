package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views;

import org.pikater.core.ontology.subtrees.newoption.base.NewOption;
import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.restrictions.TypeRestriction;
import org.pikater.web.experiment.server.BoxInfoServer;
import org.pikater.web.vaadin.gui.server.components.anchor.Anchor;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.BoxManagerView;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.IContextForViews;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.IOptionViewDataSource;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options.OptionValueForm;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * Subview of overview, built especially for editing slots.
 * 
 * @see {@link BoxManagerView#OPTIONVIEW}
 */
public class BoxManagerOptionView extends AbstractBoxManagerView<NewOption> {
	private static final long serialVersionUID = 5778436487377608808L;

	private final IContextForViews context;
	private final Anchor link_optionName;
	private OptionValueForm currentForm;

	public BoxManagerOptionView(IContextForViews context) {
		super(context);
		setStyleName("boxManagerOptionView");

		Label lbl_valueType = new Label("Option:");
		lbl_valueType.setStyleName("emphasizedLabel");

		this.link_optionName = new Anchor("", "");
		this.link_optionName.addStyleName("emphasizedLabel");

		HorizontalLayout hl_optionIdentification = new HorizontalLayout();
		hl_optionIdentification.setStyleName("viewItem");
		hl_optionIdentification.setSpacing(true);
		hl_optionIdentification.addComponent(lbl_valueType);
		hl_optionIdentification.addComponent(this.link_optionName);

		this.context = context;
		this.currentForm = null;

		addComponent(getBoxIdentificatinComponent());
		addComponent(hl_optionIdentification);
	}

	@Override
	protected void validateSource(NewOption source)
			throws IllegalArgumentException {
		if (!source.isSingleValue()) {
			throw new IllegalArgumentException("Multi-value options.");
		}
	}

	@Override
	public void refreshContent() {
		link_optionName.setValue(getCurrentSource().getName());
		link_optionName.setDescription(getCurrentSource().getDescription());

		if (currentForm != null) {
			removeComponent(currentForm);
		}
		currentForm = new OptionValueForm(new IOptionViewDataSource() {
			private final Value currentValue = getCurrentSource()
					.toSingleValue();
			private final TypeRestriction allowedTypes = getCurrentSource()
					.fetchValueRestrictionForIndex(0);

			@Override
			public BoxInfoServer getBox() {
				return context.getCurrentBoxDataSource();
			}

			@Override
			public NewOption getOption() {
				return getCurrentSource();
			}

			@Override
			public Value getValue() {
				return currentValue;
			}

			@Override
			public TypeRestriction getAllowedTypes() {
				return allowedTypes;
			}
		});
		currentForm.setCaption("Value information:");
		currentForm.setSizeFull();
		currentForm.attachToButtonInterface(new Button("Back to overview",
				new Button.ClickListener() {
					private static final long serialVersionUID = 8283308445389621644L;

					@Override
					public void buttonClick(ClickEvent event) {
						getContext().resetView();
					}
				}));
		addComponent(currentForm);
	}
}