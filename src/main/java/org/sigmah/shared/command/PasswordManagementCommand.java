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
import org.sigmah.shared.command.result.StringResult;

/**
 * <p>
 * Manages a user password.
 * </p>
 * <p>
 * This command handles multiple actions:
 * <ul>
 * <li>{@link Action#ForgotPassword}.</li>
 * <li>{@link Action#UpdatePassword}.</li>
 * <li>{@link Action#RetrieveEmailFromToken}.</li>
 * </ul>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PasswordManagementCommand extends AbstractCommand<StringResult> {

	/**
	 * Command multiple actions.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Action {

		/**
		 * Sends a 'lost password' email to the user.<br/>
		 * Returns {@code null}.
		 */
		ForgotPassword,

		/**
		 * Updates the user password with a new one.<br/>
		 * Returns {@code null}.
		 */
		UpdatePassword,

		/**
		 * Retrieves a user email from a "<em>change password</em>" token.<br/>
		 * Returns the user email, or {@code null} if no user found or token is out-of-date.
		 */
		RetrieveEmailFromToken;

	}

	private Action action;

	private String email;
	private Language language;
	private String newPassword;
	private String changePasswordToken;

	/**
	 * Empty constructor necessary for RPC serialization.
	 */
	public PasswordManagementCommand() {
		// Serialization.
	}

	/**
	 * Initializes a new command sending a "<em>lost password</em>" email to the given user {@code email}.
	 * 
	 * @param email
	 *          The user's email.
	 * @param language
	 *          The user language. The email is sent using this language.
	 * @see Action#ForgotPassword
	 */
	public PasswordManagementCommand(final String email, final Language language) {
		this.action = Action.ForgotPassword;
		this.email = email;
		this.language = language;
	}

	/**
	 * Initializes a new command updating the given {@code email} corresponding user's password with the given
	 * {@code newPassword}.
	 * 
	 * @param email
	 *          The user's email.
	 * @param newPassword
	 *          The user's new password.
	 * @see Action#UpdatePassword
	 */
	public PasswordManagementCommand(final String email, final String newPassword) {
		this.action = Action.UpdatePassword;
		this.email = email;
		this.newPassword = newPassword;
	}

	/**
	 * Initializes a new command retrieving the given {@code changePasswordToken} corresponding user email.
	 * 
	 * @param changePasswordToken
	 *          The "<em>change password</em>" token.
	 * @see Action#RetrieveEmailFromToken
	 */
	public PasswordManagementCommand(final String changePasswordToken) {
		this.action = Action.RetrieveEmailFromToken;
		this.changePasswordToken = changePasswordToken;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("action", action);
		builder.append("email", email);
		builder.append("changePasswordToken", changePasswordToken);
	}

	public Action getAction() {
		return action;
	}

	public String getEmail() {
		return email;
	}

	public Language getLanguage() {
		return language;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getChangePasswordToken() {
		return changePasswordToken;
	}

}
