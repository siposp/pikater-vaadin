package org.pikater.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.pikater.shared.logging.GeneralPikaterLogger;
import org.pikater.shared.logging.IPikaterLogger;

/**
 * Special logger wrapper to be used by the web application.
 * 
 * @author SkyCrawl
 */
public class PikaterWebLogger extends GeneralPikaterLogger
{
	private static final IPikaterLogger	innerLogger	= createPikaterLogger(Logger.getLogger("log4j"));

	public static IPikaterLogger getLogger()
	{
		return innerLogger;
	}

	public static void logThrowable(String message, Throwable t)
	{
		getLogger().logThrowable(message, t);
	}

	public static void log(Level logLevel, String message)
	{
		getLogger().log(logLevel, message);
	}

	public static void log(Level logLevel, String source, String message)
	{
		getLogger().log(logLevel, source, message);
	}
}