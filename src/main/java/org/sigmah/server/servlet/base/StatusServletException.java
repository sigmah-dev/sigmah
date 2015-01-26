package org.sigmah.server.servlet.base;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;

/**
 * Custom {@link ServletException} carrying a response status code.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see com.google.gwt.http.client.Response
 */
public class StatusServletException extends ServletException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8523719458748389335L;

	/**
	 * The response status code.
	 */
	private final int statusCode;

	public StatusServletException(final int statusCode) {
		this(statusCode, null, null);
	}

	public StatusServletException(final int statusCode, final Throwable cause) {
		this(statusCode, null, cause);
	}

	public StatusServletException(final int statusCode, final String message) {
		this(statusCode, message, null);
	}

	public StatusServletException(final int statusCode, final String message, final Throwable cause) {
		super(statusCode + (StringUtils.isNotBlank(message) ? ": " + message : ""), cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
