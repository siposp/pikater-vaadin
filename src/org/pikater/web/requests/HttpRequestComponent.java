package org.pikater.web.requests;

/**
 * @author SkyCrawl
 */
public enum HttpRequestComponent {
	/**
	 * E.g. <font color="red">http</font>://example.com:8080/MyApplication/MyServlet/{MyPath-with/}?MyQueryString.
	 */
	P1_SCHEME,

	/**
	 * E.g. http://<font color="red">example.com</font>:8080/MyApplication/MyServlet/{MyPath-with/}?MyQueryString.
	 */
	P2_SERVERNAME,

	/**
	 * E.g. http://example.com:<font color="red">8080</font>/MyApplication/MyServlet/{MyPath-with/}?MyQueryString. 
	 */
	P3_PORT,

	/**
	 * E.g. http://example.com:8080/<font color="red">MyApplication</font>/MyServlet/{MyPath-with/}?MyQueryString.
	 */
	P4_APPCONTEXT,

	/**
	 * E.g. http://example.com:8080/MyApplication/<font color="red">MyServlet</font>/{Path-with-all-/}?MyQueryString.
	 */
	P5_SERVLETCONTEXT,

	/**
	 * E.g. http://example.com:8080/MyApplication/MyServlet/<font color="red">{Path-with-all-/}</font>?MyQueryString.
	 */
	P6_PATHINFO,

	/**
	 * E.g. http://example.com:8080/MyApplication/MyServlet/{Path-with-all-/}?<font color="red">MyQueryString</font>.</br>
	 * Including URI fragments that are contained within query string.
	 */
	P7_QUERYSTRING;
}