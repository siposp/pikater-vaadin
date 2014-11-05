package org.pikater.web.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.sharedresources.ResourceRegistrar;
import org.pikater.web.sharedresources.download.IDownloadResource;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;

/**
 * A download servlet returning streams of shared resources registered with
 * {@link ResourceRegistrar}.
 * 
 * @author SkyCrawl
 */
@WebServlet(value = "/download", asyncSupported = true, loadOnStartup = 1)
public class DynamicDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 8511309463555430804L;

	private static final int DEFAULT_BUFFER_SIZE = 2048;
	public static final int EXPIRATION_TIME_IN_SECONDS = 15;

	//--------------------------------------------------------------
	// INHERITED INTERFACE

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		IDownloadResource downloadableResource = null;
		try {
			String token = req.getParameter("t");
			UUID resourceID = ResourceRegistrar.toResourceID(token);
			downloadableResource = (IDownloadResource) ResourceRegistrar.getResource(resourceID);
		} catch (Exception e) {
			ResourceRegistrar.handleError(e, resp);
			return;
		}

		if (downloadableResource.getSize() > Integer.MAX_VALUE) { // "HttpServletResponse.setContentLength()" only accepts int type
			PikaterWebLogger.log(Level.SEVERE, String.format("The file '%s' was not served because it is too large (larger than MAX_INT).", downloadableResource.getFilename()));
			resp.sendError(HttpStatus.SC_BAD_REQUEST, "The requested file is too large to serve.");
		} else {
			resp.setContentType(downloadableResource.getMimeType());
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + downloadableResource.getFilename() + "\"");
			resp.setContentLength((int) downloadableResource.getSize());

			// THIS IS THE PLACE TO SET CACHE TIME OR OTHER THINGS, IF YOU MEAN TO

			InputStream input = null;
			try {
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				OutputStream output = resp.getOutputStream();
				input = downloadableResource.getStream();
				int length;
				while ((length = input.read(buffer)) > 0) {
					output.write(buffer, 0, length);
				}
			} catch (IOException e) {
				PikaterWebLogger.logThrowable(
						String.format("Client most likely disconnected or aborted transferring the file '%s' but this needs to be logged anyway.", downloadableResource.getFilename()), e);
				MyNotifications.showError("Download ended with error", downloadableResource.getFilename());
			} catch (Exception e) {
				PikaterWebLogger.logThrowable(String.format("Unknown error occured while uploading file '%s'.", downloadableResource.getFilename()), e);
				MyNotifications.showError("Download ended with error", downloadableResource.getFilename());
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}