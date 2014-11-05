package org.pikater.web.filters;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.pikater.web.PikaterWebLogger;
import org.pikater.web.vaadin.CustomConfiguredUIServlet.PikaterUI;

@WebFilter(filterName = "filter1", description = "Adds the default UI identifier to incoming default requests "
		+ "(e.g. 'localhost:8080/Pikater' => 'localhost:8080/Pikater/index)'. To achieve this, a client redirect "
		+ "is sent so that the client browser's address bar reflects the change and bookmarking works properly.")
public class RequestCodificatorFilter extends AbstractFilter {
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		// determine basic information & references
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

		// print debug info if needed
		if (shouldPrintDebugInfo()) {
			printRequestComponents(RequestCodificatorFilter.class.getSimpleName(), httpRequest);
		}

		// actually do stuff
		if (!isServletPathDefined(httpRequest)) {
			if (shouldPrintDebugInfo()) {
				PikaterWebLogger.log(Level.WARNING, "Request is about to be redirected...");
			}
			clientRedirect(servletResponse, PikaterUI.INDEX_PAGE.getURLPattern());
		} else {
			if (shouldPrintDebugInfo()) {
				PikaterWebLogger.log(Level.WARNING, "Request is about to be further processed...");
			}
			chain.doFilter(servletRequest, servletResponse); // pass the request along the filter chain
		}
	}
}