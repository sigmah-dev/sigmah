package org.sigmah.shared.security;

import org.sigmah.shared.dispatch.CommandException;

/**
 * <p>
 * Unauthorized access exception.
 * </p>
 * <p>
 * Thrown if a user try to execute an action with insufficient rights.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UnauthorizedAccessException extends CommandException {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = -227740094906497505L;

	public UnauthorizedAccessException() {
		// Serialization.
	}

	public UnauthorizedAccessException(String message) {
		super(message);
	}

}
