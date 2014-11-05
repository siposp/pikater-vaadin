package org.pikater.web;

import java.util.logging.Level;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.pikater.core.agents.gateway.WebToCoreEntryPoint;
import org.pikater.shared.quartz.PikaterJobScheduler;
import org.pikater.shared.util.IOUtils;
import org.pikater.web.config.WebAppConfiguration;

/**
 * Application life cycle event listener. Provides routines for startup/shutdown
 * of the web application.
 * 
 * @author SkyCrawl
 */
@WebListener
public class StartupAndQuitListener implements ServletContextListener {
	/**
	 * Application startup event.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		/*
		 * Should the application ever support clustering, context replication
		 * could be a good way to start. The following is a mere "hint"... on no
		 * account it is a full implementation.
		 */

		/*
		 * // event.getServletContext().setAttribute("config", this);
		 * ServletContext ctx = event.getServletContext();
		 * if(ctx.getAttribute("storage") == null) // servlet context is
		 * replicated and was successfully loaded { // create new
		 * ctx.setAttribute("storage", SharedStorage.getInstance()); } else {
		 * System.out.println("storage was already set"); }
		 */

		/*
		 * This is where the actual initialization begins.
		 * 
		 * GENERAL NOTE: don't alter the code order. There may be some
		 * dependencies.
		 */

		try {
			announceCheckOrAction("setting initial application state & essential variables");
			WebAppConfiguration.setContext(event.getServletContext());
			IOUtils.setAbsoluteBaseAppPath(event.getServletContext()
					.getRealPath("/"));

			announceCheckOrAction("initializing task scheduler");
			PikaterJobScheduler.initStaticScheduler(IOUtils.joinPathComponents(
					IOUtils.getAbsoluteWEBINFCLASSESPath(),
					IOUtils.getRelativePath(PikaterJobScheduler.class)));

			if (WebAppConfiguration.isCoreEnabled()) {
				announceCheckOrAction("checking core connection");
				try {
					WebToCoreEntryPoint.checkLocalConnection();
				} catch (Exception e) {
					throw new IllegalStateException(
							"Could not establish connection with pikater core.",
							e);
				}
			}

			announceCheckOrAction("database connection");
			/*
			 * TODO: doesn't work in context listeners for some reason because
			 * of driver loading issues:
			 * if(!ServerConfigurationInterface.avoidUsingDBForNow() &&
			 * !MyPGConnection.isConnectionToCurrentPGDBEstablished()) {
			 * Class.forName("org.postgresql.Driver");
			 * DriverManager.registerDriver(new Driver()); throw new
			 * IllegalStateException("Could not connect to database."); }
			 */

			PikaterWebLogger
					.log(Level.INFO,
							"\n**********************************************************\n"
									+ "APPLICATION SETUP SUCCESSFULLY FINISHED\n"
									+ "**********************************************************");
		} catch (Exception e) {
			PikaterWebLogger.log(Level.SEVERE,
					"APPLICATION LAUNCH REQUIREMENTS WERE NOT MET. ABORTED.");
			throw new IllegalStateException(e); // will be printed just
												// afterwards
		}
	}

	/**
	 * Application shutdown event.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		PikaterJobScheduler.shutdownStaticScheduler();
	}

	// ---------------------------------------------------------------
	// PRIVATE INTERFACE

	private void announceCheckOrAction(String message) {
		PikaterWebLogger.log(Level.INFO,
				"**********************************************************\n"
						+ "CHECKING REQUIREMENT / DOING ACTION: " + message);
	}
}