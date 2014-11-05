package org.pikater.web.config;

import javax.servlet.ServletContext;

import org.pikater.core.agents.gateway.WebToCoreEntryPoint;

/**
 * <p>Java representation of some settings found it the "web.xml" file. These settings should not
 * relate to Vaadin or other technologies used (there are other ways to work with them) but rather
 * focus on settings that immediately affect the application's behaviour. At this time, only 
 * {@link #isCoreEnabled()} satisfies that condition.</p>
 * 
 * <p>Furthermore, this class defines some added value in the form of special interface not related
 * to the application's deployment descriptor that (again) affect its behaviour. These are mainly
 * "debug" and "devel features".</p>
 * 
 * @author SkyCrawl
 */
public class WebAppConfiguration {
	/*
	 * Unique keys parameters stored in "web.xml".
	 */
	private static final String PARAMKEY_CORE_ENABLED = "coreEnabled";

	/*
	 * Inner variables.
	 */
	private static ServletContext WEBAPPCONTEXT = null;

	// **************************************************************************************************
	// PUBLIC SETTERS

	/**
	 * Set the web application's context. All relevant settings found in the deployment
	 * descriptor will be derived from it.
	 * @see {@link #getContextParam(String)}
	 */
	public static void setContext(ServletContext context) {
		if (WebAppConfiguration.WEBAPPCONTEXT != null) {
			throw new IllegalStateException("Application context is already defined.");
		} else {
			WebAppConfiguration.WEBAPPCONTEXT = context;
		}
	}

	/**
	 * Get the web application's context.
	 */
	public static ServletContext getContext() {
		return WEBAPPCONTEXT;
	}

	// **************************************************************************************************
	// OTHER PUBLIC INTERFACE

	/**
	 * Determines whether the web application is set to communicate with an instance
	 * of core system.
	 * <ul>
	 * <li> If true, the application's potential should be fully brought out. Use
	 * {@link WebToCoreEntryPoint} to communicate with the core system.
	 * <li> If false, some of the application's features can not be utilized to the
	 * fullest - experiments can not be queued for example. Only database related
	 * features remain unlimited.
	 * </ul> 
	 * 
	 */
	public static boolean isCoreEnabled() {
		return Boolean.parseBoolean((String) getContextParam(PARAMKEY_CORE_ENABLED));
	}

	/**
	 * Convenience method for development purposes.</br>
	 * Signals what kind of data should be used throughout the application.
	 * <ul>
	 * <li> If true, some dummy generated data.
	 * <li> If false, data from database.
	 * </ul>
	 * 
	 * This is useful to allow offline development when the database is offline
	 * or under maintenance.
	 * 
	 */
	public static boolean avoidUsingDBForNow() {
		return false;
	}

	/**
	 * Whether the application has been launched successfully and a valid
	 * configuration has been parsed.
	 */
	public static boolean isApplicationReadyToServe() {
		return isValid(); // when remote Jade launching is implemented, edit accordingly
	}

	/**
	 * Whether settings derived from the web deployment descriptor are all valid.
	 */
	public static boolean isValid() {
		return WEBAPPCONTEXT != null;
	}

	// **************************************************************************************************
	// PRIVATE INTERFACE

	@SuppressWarnings("unchecked")
	private static <T extends Object> T getContextParam(String key) {
		return (T) WEBAPPCONTEXT.getInitParameter(key);
	}
}
