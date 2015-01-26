package org.sigmah.shared.command;

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
