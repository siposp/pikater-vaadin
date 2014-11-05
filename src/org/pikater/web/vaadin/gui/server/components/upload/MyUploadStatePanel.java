package org.pikater.web.vaadin.gui.server.components.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.pikater.web.HttpContentType;

import com.google.gwt.thirdparty.guava.common.io.Files;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.StreamVariable.StreamingEndEvent;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.server.StreamVariable.StreamingStartEvent;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.SmartMultiUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStatePanel;

/**
 * Our own version of multi-file upload, extending the Vaadin
 * multi-file upload Vaadin add-on.
 * 
 * @author SkyCrawl
 */
public class MyUploadStatePanel extends UploadStatePanel {
	private static final long serialVersionUID = 2373915541113299145L;

	private final Collection<IFileUploadEvents> fileUploadEventsCallbacks;
	private final MyUploadReceiver uploadReceiver;

	public MyUploadStatePanel(MyUploadStateWindow window, MyUploadReceiver uploadReceiver) {
		super(window, uploadReceiver);

		this.fileUploadEventsCallbacks = new ArrayList<IFileUploadEvents>();
		this.uploadReceiver = uploadReceiver;
	}

	// ------------------------------------------------------------
	// INHERITED INTERFACE

	/*
	 * Just so we know there is something else we can customize.
	 */
	@Override
	public void setFinishedHandler(UploadFinishedHandler finishedHandler) {
		super.setFinishedHandler(finishedHandler);
	}

	/*
	 * Just so we know there is something else we can customize.
	 */
	@Override
	public void setMultiUpload(SmartMultiUpload multiUpload) {
		super.setMultiUpload(multiUpload);
	}

	@Override
	public void streamingStarted(StreamingStartEvent event) {
		// TODO: check whether maximum number of uploads has been reached and if so, wait until a slot is open
		super.streamingStarted(event); // always call this first, otherwise unexpected problems may occur
		for (IFileUploadEvents callbacks : fileUploadEventsCallbacks) {
			callbacks.uploadStarted(event);
		}
	}

	@Override
	public void streamingFailed(StreamingErrorEvent event) {
		super.streamingFailed(event); // always call this first, otherwise unexpected problems may occur
		for (IFileUploadEvents callbacks : fileUploadEventsCallbacks) {
			callbacks.uploadFailed(event);
		}
	}

	@Override
	public void streamingFinished(StreamingEndEvent event) {
		super.streamingFinished(event); // always call this first, otherwise unexpected problems may occur

		final HttpContentType originalType = HttpContentType.fromMimeType(event.getMimeType());
		try {
			if (originalType.hasExtensionsDefined()) {
				if (originalType.getExtensions().length == 1) {
					/*
					 * At this moment, the file has a temporary extension (.tmp). Original
					 * extension may be required.
					 */

					// determine absolute path of the file with its original extension
					String pathWithOriginalExtension = uploadReceiver.getUploadedFileHandler().getAbsolutePath();
					pathWithOriginalExtension = pathWithOriginalExtension.substring(0, pathWithOriginalExtension.lastIndexOf('.')).concat(originalType.getExtensions()[0]);

					// change extension of the uploaded file (needed for dataset conversion)
					File fileWithOriginalExtension = new File(pathWithOriginalExtension);
					Files.move(uploadReceiver.getUploadedFileHandler(), fileWithOriginalExtension);

					// callback
					for (IFileUploadEvents callbacks : fileUploadEventsCallbacks) {
						callbacks.uploadFinished(event, fileWithOriginalExtension);
					}
				} else {
					throw new IllegalStateException(String.format("Can not rename the uploaded file because the '%s' mime type has multiple " + "extensions defined.", originalType.getMimeType()));
				}
			} else {
				throw new IllegalStateException(String.format("Can not rename the uploaded file because the '%s' mime type has no " + "extensions defined.", originalType.getMimeType()));
			}
		} catch (Exception t) {
			streamingFailed(new StreamingErrorEvent() {
				private static final long serialVersionUID = 5878030943462489881L;

				@Override
				public String getMimeType() {
					return originalType.getMimeType();
				}

				@Override
				public String getFileName() {
					return uploadReceiver.getUploadedFileHandler().getName();
				}

				@Override
				public long getContentLength() {
					return uploadReceiver.getUploadedFileHandler().length();
				}

				@Override
				public long getBytesReceived() {
					return uploadReceiver.getUploadedFileHandler().length();
				}

				@Override
				public Exception getException() {
					return new IllegalStateException("The designated mime type has several extensions defined. Only one is required.");
				}
			});
		}
	}

	/**
	 * This can be used to slow down the file upload speed since the thread receiving
	 * input bytes can be put to sleep for a period of time.
	 */
	@Override
	public void onProgress(StreamVariable.StreamingProgressEvent event) {
		// System.out.println("Bytes received: " + event.getBytesReceived());
		/*
		try
		{
		    Thread.sleep(500);
		}
		catch (InterruptedException ex)
		{
		    // Logger.getLogger(WidgetTestApplication.class.getName()).log(Level.SEVERE, null, ex);
		}
		*/
		super.onProgress(event);
	}

	// ------------------------------------------------------------
	// OTHER PUBLIC INTERFACE

	public void addUploadStartListener(IFileUploadEvents callbacks) {
		this.fileUploadEventsCallbacks.add(callbacks);
	}
}