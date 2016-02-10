package org.sigmah.shared.command;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.Authentication;

/**
 * Command processing the login action for given credentials.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LoginCommand extends AbstractCommand<Authentication> {

	// Authentication credentials.
	private String login;
	private String password;
	private Language language;

	/**
	 * Empty constructor necessary for RPC serialization.
	 */
	public LoginCommand() {
		// Serialization.
	}

	/**
	 * Initializes a new command executing login process for the given credentials.
	 * 
	 * @param login
	 *          The user login.
	 * @param password
	 *          The user plain text password.
	 * @param language
	 *          The selected language.
	 */
	public LoginCommand(final String login, final String password, final Language language) {
		this.login = login;
		this.password = password;
		this.language = language;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		// Do not log plain text password!
		builder.append("login", login);
		builder.append("language", language);
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public Language getLanguage() {
		return language;
	}

}
