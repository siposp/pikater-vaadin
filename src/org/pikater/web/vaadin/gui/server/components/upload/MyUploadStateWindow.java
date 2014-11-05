package org.pikater.web.vaadin.gui.server.components.upload;

import java.util.ArrayList;
import java.util.List;

import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStatePanel;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

/**
 * Our own, a more centralized, expansion of the original multi-file
 * upload Vaadin add-on.
 * 
 * @author SkyCrawl
 */
public class MyUploadStateWindow extends UploadStateWindow {
	private static final long serialVersionUID = -1630660423984992697L;

	private final List<UploadStatePanel> panels;
	private final Object lock_object;

	// TODO: edit and attach a network activity graph to display upload speed history?

	public MyUploadStateWindow() {
		super();
		this.panels = new ArrayList<UploadStatePanel>();
		this.lock_object = new Object();
	}

	@Override
	public void addPanel(UploadStatePanel panel) {
		synchronized (lock_object) {
			super.addPanel(panel);
			this.panels.add(panel);
		}
	}

	@Override
	public void removePanel(UploadStatePanel panel) {
		synchronized (lock_object) {
			super.removePanel(panel);
			this.panels.remove(panel);
		}
	}

	/**
	 * Note: on any UI. 
	 */
	public boolean isAFileBeingUploaded() {
		/*
		 * Works across all instances in all UIs, if panels are always
		 * added.
		 */
		synchronized (lock_object) {
			for (UploadStatePanel panel : panels) {
				if (panel.hasUploadInProgress()) {
					return true;
				}
			}
			return false;
		}
	}
}
