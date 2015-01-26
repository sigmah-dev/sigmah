package org.sigmah.shared.validation;

import org.sigmah.shared.dispatch.CommandException;

/**
 * Thrown if a bean validation failed.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ValidationException extends CommandException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 164327114753031131L;

	protected ValidationException() {
		// Serialization.
	}

	public ValidationException(final String message) {
		super(message);
	}

	public ValidationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ValidationException(final Throwable cause) {
		super(cause);
	}

}
