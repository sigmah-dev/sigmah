package org.sigmah.shared.servlet;

/**
 * <p>
 * Servlet request exception thrown if a {@link ServletRequestBuilder} execution fails.
 * </p>
 * <p>
 * Runtime version of {@link com.google.gwt.http.client.RequestException}.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ServletRequestException extends RuntimeException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3940128253031129188L;

	public ServletRequestException(final Throwable cause) {
		super(cause);
	}

	public ServletRequestException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
