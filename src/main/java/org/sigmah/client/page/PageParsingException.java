package org.sigmah.client.page;

/**
 * Thrown of a page URL cannot be parsed.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class PageParsingException extends Exception {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = -7562901661618711604L;

	public PageParsingException() {
	}

	public PageParsingException(String message) {
		super(message);
	}

	public PageParsingException(Throwable cause) {
		super(cause);
	}

	public PageParsingException(String message, Throwable cause) {
		super(message, cause);
	}

}
