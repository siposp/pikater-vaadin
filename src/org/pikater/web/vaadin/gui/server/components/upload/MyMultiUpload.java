package org.pikater.web.vaadin.gui.server.components.upload;

import java.io.InputStream;

import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

/**
 * Our own version of multi-file upload, extending the Vaadin
 * multi-file upload Vaadin add-on.
 * 
 * @author SkyCrawl
 */
public class MyMultiUpload extends MultiFileUpload {
	private static final long serialVersionUID = 1274280572318620L;

	public MyMultiUpload(MyUploadStateWindow uploadStateWindow, boolean multiple) {
		super(new UploadFinishedHandler() {
			@Override
			public void handleFile(InputStream stream, String filename, String mimetype, long size) {
				// no implementation
			}
		}, uploadStateWindow, multiple);
	}

	// ----------------------------------------------------------------------
	// INHERITED INTERFACE

	@Override
	public MyUploadStatePanel getUploadStatePanel() {
		return (MyUploadStatePanel) super.getUploadStatePanel();
	}

	@Override
	protected MyUploadStatePanel createStatePanel(UploadStateWindow uploadStateWindow) {
		return new MyUploadStatePanel((MyUploadStateWindow) uploadStateWindow, new MyUploadReceiver());
	}

	// ----------------------------------------------------------------------
	// OTHER PUBLIC INTERFACE

	/**
	 * Add a custom upload event handlers, like when a new one starts.
	 */
	public void addFileUploadEventsCallback(IFileUploadEvents callbacks) {
		getUploadStatePanel().addUploadStartListener(callbacks);
	}
}
