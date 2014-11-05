package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent;

import org.pikater.core.ontology.subtrees.agentinfo.AgentInfo;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.DragAndDropWrapper;

public class KineticDnDWrapper extends DragAndDropWrapper {
	private static final long serialVersionUID = 5184871976150233156L;

	public KineticDnDWrapper(final KineticComponent kineticComponent) {
		super(kineticComponent);
		setSizeFull();

		setDropHandler(new DropHandler() {
			private static final long serialVersionUID = 3609742031571169442L;

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

			@Override
			public void drop(DragAndDropEvent event) {
				// we are about to issue creation of a new box in the schema editor - some prerequisites:
				WrapperTargetDetails details = (WrapperTargetDetails) event.getTargetDetails();
				WrapperTransferable transferable = (WrapperTransferable) event.getTransferable();
				AgentInfo agentInfo = (AgentInfo) ((AbstractComponent) transferable.getDraggedComponent()).getData();

				// issue the creation command to the client:
				kineticComponent.createBox(agentInfo, details.getMouseEvent().getClientX(), details.getMouseEvent().getClientY(), true);
			}
		});
	}

	public KineticComponent getWrappedComponent() {
		return (KineticComponent) getCompositionRoot();
	}
}