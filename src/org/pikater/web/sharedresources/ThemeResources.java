package org.pikater.web.sharedresources;

import java.io.File;

import org.pikater.shared.util.IOUtils;

import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;

public class ThemeResources {
	/*
	 * GENERAL NOTES:
	 * - Resources can be both static (a downloadable WEB-INF file for instance) and dynamic (URL link needs to be constructed).
	 * - Stream resources allow creating dynamic resource content. Charts are typical examples of dynamic images.
	 * 
	 * NOTABLE SERVER-RELATED TYPES:
	 * - com.vaadin.server.FileDownloader
	 * - com.vaadin.server.ExternalResource
	 * - com.vaadin.server.StreamResource
	 * - Link - a link to internal or external resource - can open a new window
	 * 
	 *  THEMERESOURCE NOTE:
	 * - {@link ThemeResource} (see below) looks for files in vaadin's static resource folder, see 
	 * {@link #getVaadinRelativePathForResource(String)}
	 */

	// ----------------------------------------------------------------
	// THEME RESOURCE DEFINITIONS:

	/*
	 * Banner.
	 */
	public static final String relPath_IMG_banner = "images/banner.png";

	/*
	 * Notification icons.
	 */
	public static final String relPath_IMG_notificationInfoIcon = "images/win8metroicons/notifications/icon-info-48x48.png";
	public static final String relPath_IMG_notificationSuccessIcon = "images/win8metroicons/notifications/icon-success-48x48.png";
	public static final String relPath_IMG_notificationWarnIcon = "images/win8metroicons/notifications/icon-warn-48x48.png";
	public static final String relPath_IMG_notificationErrorIcon = "images/win8metroicons/notifications/icon-error-48x48.png";

	/*
	 * Boxes.
	 */
	public static final String relPath_IMG_boxInputIcon = "images/win8metroicons/boxes/icon-input-48x48.png";
	public static final String relPath_IMG_boxOutputIcon = "images/win8metroicons/boxes/icon-output-48x48.png";
	public static final String relPath_IMG_boxSearcherIcon = "images/win8metroicons/boxes/icon-searcher-48x48.png";
	public static final String relPath_IMG_boxComputingIcon = "images/win8metroicons/boxes/icon-computing-48x48.png";
	public static final String relPath_IMG_boxRecommenderIcon = "images/win8metroicons/boxes/icon-recommender-48x48.png";
	public static final String relPath_IMG_boxWrapperIcon = "images/win8metroicons/boxes/icon-wrapper-48x48.png";
	public static final String relPath_IMG_boxDataProcessingIcon = "images/win8metroicons/boxes/icon-dataprocessing-48x48.png";
	public static final String relPath_IMG_boxEvaluationIcon = "images/win8metroicons/boxes/icon-evaluation-48x48.png";
	public static final String relPath_IMG_boxMiscellaneousIcon = "images/win8metroicons/boxes/icon-miscellaneous-48x48.png";

	/*
	 * Various.
	 */
	public static final String relPath_IMG_checkIcon16 = "images/check-16x16.png";
	public static final String relPath_IMG_clearIcon16 = "images/clear-16x16.png";
	public static final String relPath_IMG_closeIcon16 = "images/close-icon-16x16.png";
	public static final String relPath_IMG_minimizeIcon16 = "images/minimize-icon-16x16.png";
	public static final String relPath_IMG_nextIcon16 = "images/Arrow-Next-icon-16x16.png";
	public static final String relPath_IMG_plusIcon16 = "images/Plus-icon-16x16.png";

	// ----------------------------------------------------------------
	// RESOURCE FIELDS:

	public static final FileResource prop_appConf = new FileResource(new File(IOUtils.joinPathComponents(IOUtils.getAbsoluteWEBINFCONFPath(), "appServer.properties")));

	/*
	 * Banner.
	 */
	public static final ThemeResource img_banner = new ThemeResource(relPath_IMG_banner);

	/*
	 * Notifications icons.
	 */
	public static final ThemeResource img_notificationInfoIcon = new ThemeResource(relPath_IMG_notificationInfoIcon);
	public static final ThemeResource img_notificationSuccessIcon = new ThemeResource(relPath_IMG_notificationSuccessIcon);
	public static final ThemeResource img_notificationWarnIcon = new ThemeResource(relPath_IMG_notificationWarnIcon);
	public static final ThemeResource img_notificationErrorIcon = new ThemeResource(relPath_IMG_notificationErrorIcon);

	/*
	 * Various.
	 */
	public static final ThemeResource img_checkIcon16 = new ThemeResource(relPath_IMG_checkIcon16);
	public static final ThemeResource img_clearIcon16 = new ThemeResource(relPath_IMG_clearIcon16);
	public static final ThemeResource img_nextIcon16 = new ThemeResource(relPath_IMG_nextIcon16);
	public static final ThemeResource img_plusIcon16 = new ThemeResource(relPath_IMG_plusIcon16);
	public static final ThemeResource img_closeIcon16 = new ThemeResource(relPath_IMG_closeIcon16);
	public static final ThemeResource img_minimizeIcon16 = new ThemeResource(relPath_IMG_minimizeIcon16);

	// ----------------------------------------------------------------
	// PUBLIC METHODS:

	/**
	 * <p>Returns a real-time relative path of the desired resource that can be included
	 * statically in HTML. The resource is required to be in vaadin's static resource folder,
	 * e.g. "WebContent/VAADIN/themes/{theme-name}/".</p>
	 * 
	 * @param relativeResourcePath theme-relative path to a file
	 */
	public static String getVaadinRelativePathForResource(String relativeResourcePath) {
		return "./VAADIN/themes/pikater/" + relativeResourcePath;
	}
}
