package org.pikater.web.sharedresources;

/**
 * Exception used to denote a known and handled problem when using {@link
 * ResourceRegistrar}.
 * 
 * @author SkyCrawl
 */
public class ResourceException extends Exception {
	private static final long serialVersionUID = 6741732074523686135L;

	public ResourceException() {
		super();
	}

	public ResourceException(String message) {
		super(message);
	}

	public ResourceException(Throwable cause) {
		super(cause);
	}

	public ResourceException(String message, Throwable cause) {
		super(message, cause);
	}
}