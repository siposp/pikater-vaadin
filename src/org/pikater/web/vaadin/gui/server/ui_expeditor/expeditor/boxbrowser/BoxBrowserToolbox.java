package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxbrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pikater.core.ontology.subtrees.agentinfo.AgentInfo;
import org.pikater.web.config.KnownCoreAgents;
import org.pikater.web.experiment.server.BoxType;
import org.pikater.web.vaadin.gui.server.components.toolbox.Toolbox;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.VerticalLayout;

/**
 * Toolbox displayed on the left of {@link ExpEditor}, containing
 * all available experiment editor boxes.
 * 
 * @author SkyCrawl
 */
@StyleSheet("boxBrowserToolbox.css")
public class BoxBrowserToolbox extends Toolbox {
	private static final long serialVersionUID = -1349487568799323284L;

	public BoxBrowserToolbox(String caption, KnownCoreAgents agentInfoProvider, ClickListener minimizeAction) {
		super(caption, minimizeAction);
		setSizeFull();

		Accordion content = new Accordion();
		content.setStyleName("boxBrowserToolbox-accordion");
		for (BoxType type : BoxType.values()) {
			VerticalLayout vLayout = new VerticalLayout();
			vLayout.setSizeFull();
			vLayout.setStyleName("boxBrowserToolbox-accordion-content");

			Map<String, DragAndDropWrapper> boxNameToComponentMapping = new HashMap<String, DragAndDropWrapper>();
			for (AgentInfo agentInfo : agentInfoProvider.getAllByType(type)) {
				Label lbl = new Label(agentInfo.getName());
				lbl.setSizeUndefined();
				lbl.setData(agentInfo);

				DragAndDropWrapper dndWrapper = new DragAndDropWrapper(lbl);
				dndWrapper.setSizeFull();
				dndWrapper.setStyleName("boxBrowserToolbox-accordion-content-draggable");
				dndWrapper.setDescription(agentInfo.getDescription());
				dndWrapper.setDragStartMode(DragStartMode.COMPONENT);
				dndWrapper.setData(agentInfo);

				boxNameToComponentMapping.put(agentInfo.getName(), dndWrapper);
			}

			// sort box names and add the component representing them in the sorted order
			List<String> sortedBoxNames = new ArrayList<String>(boxNameToComponentMapping.keySet());
			Collections.sort(sortedBoxNames);
			for (String boxName : sortedBoxNames) {
				vLayout.addComponent(boxNameToComponentMapping.get(boxName));
			}

			// Tab newTab = content.addTab(vLayout, type.name());
			content.addTab(vLayout, String.format("%s (%d)", type.name(), vLayout.getComponentCount()));
		}

		setToolboxContent(content);
	}
}
