package org.pikater.web.requests;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;

/**
 * A special utility class to fiddle with {@link HttpRequest} and provide
 * some additional interface.
 * 
 * @author SkyCrawl
 */
public class HttpRequestUtils {
	/**
	 * <h1>First a little introduction to the problem this method solves:</h1>
	 * When trying to get the servlet path of a request, two components are the 
	 * most important:</br>
	 * <ol>
	 * <li> Servlet path - defined by {@link HttpServletRequest#getServletPath()}
	 * <li> Path info - defined by {@link HttpServletRequest#getPathInfo()}
	 * </ol>
	 * These methods may work with the servlet container to find out if a servlet is mapped to the
	 * request URL. If it is, the first method returns a non-null string depicting the servlet mapping
	 * and the second method returns the rest of the request URL. However, if the request is not directly
	 * mapped to a servlet, the first method returns null and the second returns the full request path info...
	 * </br></br>
	 * <h1>Now onto what this method actually does:</h1>
	 * This method tries to overcome the above issue by always returning the servlet path, whether mapped
	 * to a servlet or not. 
	 * @return Whether the servlet path is defined:</br>
	 * <ul>
	 * <li> "http://localhost:8080/Pikater" or "http://localhost:8080/Pikater/" => it is not
	 * <li> "http://localhost:8080/Pikater/index" or "http://localhost:8080/Pikater/index/..." => it is
	 * </ul>
	 */
	public static String getServletPathWhetherMappedOrNot(HttpServletRequest request) {
		String result = request.getServletPath();
		if ((result == null) || result.isEmpty()) {
			result = request.getPathInfo();
		}
		if ((result == null) || result.isEmpty()) {
			throw new IllegalStateException("Both servlet context and path info can not be empty!");
		} else {
			final int fromIndex = result.startsWith("/") ? 1 : 0;
			int toIndex = fromIndex;
			while ((toIndex < result.length()) && (result.charAt(toIndex) != '/')) {
				toIndex++;
			}
			if (fromIndex == toIndex) {
				return null;
			} else {
				return result.substring(fromIndex, toIndex);
			}
		}
	}

	public static String getFullURL(HttpServletRequest request) {
		return getURLPrefix(request, HttpRequestComponent.P7_QUERYSTRING);
	}

	/**
	 * @param stopAtIncl the component to stop the URL at - inclusive
	 */
	public static String getURLPrefix(HttpServletRequest request, HttpRequestComponent stopAtIncl) {
		StringBuilder result = new StringBuilder();
		for (HttpRequestComponent currentComponent : HttpRequestComponent.values()) { // returns the constants in the order they're declared!
			if (currentComponent.compareTo(stopAtIncl) <= 0) {
				writeRequestFragment(request, currentComponent, result);
			} else {
				break;
			}
		}
		return result.toString();
	}

	/**
	 * <font color="red">RED ALERT:</font> this method assumes that we use HTTP/HTTPS.
	 */
	private static void writeRequestFragment(HttpServletRequest request, HttpRequestComponent currentComponent, StringBuilder builder) {
		switch (currentComponent) {
		case P1_SCHEME:
			if (request.getScheme().equalsIgnoreCase("http") || request.getScheme().equalsIgnoreCase("https")) {
				builder.append(request.getScheme());
			} else {
				builder.append("http");
			}
			break;
		case P2_SERVERNAME:
			builder.append("://").append(request.getServerName());
			break;
		case P3_PORT:
			// port number 80 is omitted for HTTP and 443 for HTTPS
			if ((request.getServerPort() != 80) && (request.getServerPort() != 443)) { // we assume HTTP(S) is used
				builder.append(":").append(request.getServerPort());
			}
			break;
		case P4_APPCONTEXT:
			builder.append(request.getContextPath());
			break;
		case P5_SERVLETCONTEXT:
			builder.append(request.getServletPath());
			break;
		case P6_PATHINFO:
			if (request.getPathInfo() != null) {
				builder.append(request.getPathInfo());
			}
			break;
		case P7_QUERYSTRING:
			if (request.getQueryString() != null) {
				builder.append("?").append(request.getQueryString());
			}
			break;
		default:
			throw new IllegalStateException("Unknown state: " + currentComponent.name());
		}
	}
}
