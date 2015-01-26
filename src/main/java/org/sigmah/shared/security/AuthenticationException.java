package org.sigmah.shared.security;

import org.sigmah.shared.dispatch.FunctionalException;

/**
 * <p>
 * Authentication exception.
 * </p>
 * <p>
 * Thrown when user authentication fails (invalid login and/or password).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class AuthenticationException extends FunctionalException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5293424643003033715L;

	public AuthenticationException() {
		super(ErrorCode.AUTHENTICATION_FAILURE);
	}

}
