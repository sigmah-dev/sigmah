package org.sigmah.server.security;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.sigmah.server.domain.User;
import org.sigmah.shared.security.AuthenticationException;

/**
 * Authenticator service interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface Authenticator {

	/**
	 * Authenticates the given credentials.
	 * 
	 * @param login
	 *          The user login.
	 * @param password
	 *          The user plain text password.
	 * @return The authenticated {@link User} (never {@code null}).
	 * @throws AuthenticationException
	 *           If the given credentials cannot authenticate a valid active user.
	 */
	User authenticate(final String login, final String password) throws AuthenticationException;

	/**
	 * Hashes the given {@code plainTextPassword}.
	 * 
	 * @param plainTextPassword
	 *          The plain text password.
	 * @return The hashed password.
	 */
	String hashPassword(final String plainTextPassword);

	/**
	 * Generates a random password.<br>
	 * The generated password contains 8 characters with 2 caps, 1 number and 1 special character.
	 * 
	 * @return The generated random password.
	 */
	String generatePassword();

}
