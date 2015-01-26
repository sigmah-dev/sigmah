package org.sigmah.server.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mail.MailService;
import org.sigmah.server.security.Authenticator;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.PasswordManagementCommand;
import org.sigmah.shared.command.result.StringResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.referential.EmailKey;
import org.sigmah.shared.dto.referential.EmailKeyEnum;
import org.sigmah.shared.dto.referential.EmailType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link PasswordManagementCommand}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PasswordManagementCommandHandler extends AbstractCommandHandler<PasswordManagementCommand, StringResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PasswordManagementCommandHandler.class);

	/**
	 * <p>
	 * Timeout for "<em>change password</em>" tokens (in seconds).<br/>
	 * After this amount of time, token becomes invalid.
	 * </p>
	 * <p>
	 * <em>Set on 24 hours.</em>
	 * </p>
	 */
	private static final long CHANGE_PASSWORD_TIMEOUT = 24 * 60 * 60;

	/**
	 * Injected {@link UserDAO}.
	 */
	private final UserDAO userDAO;

	/**
	 * Injected {@link Authenticator} service.
	 */
	private final Authenticator authenticator;

	/**
	 * Injected {@link MailService}.
	 */
	private final MailService mailService;

	@Inject
	public PasswordManagementCommandHandler(final UserDAO userDAO, final Authenticator authenticator, final MailService mailService) {
		this.userDAO = userDAO;
		this.authenticator = authenticator;
		this.mailService = mailService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringResult execute(final PasswordManagementCommand command, final UserExecutionContext context) throws CommandException {

		if (command.getAction() == null) {
			throw new CommandException("Invalid command 'action' property: '" + command.getAction() + "'.");
		}

		final String email = command.getEmail();
		final Language language = command.getLanguage();
		final String newPassword = command.getNewPassword();
		final String changePasswordToken = command.getChangePasswordToken();

		final String result;

		switch (command.getAction()) {
			case ForgotPassword:
				forgotPassword(email, language, context);
				result = null;
				break;
			case UpdatePassword:
				updatePassword(email, newPassword, context);
				result = null;
				break;
			case RetrieveEmailFromToken:
				// Retrieves 'change password' token corresponding user email.
				result = retrieveEmailFromToken(changePasswordToken, context);
				break;
			default:
				throw new CommandException("Invalid command 'action' property: '" + command.getAction() + "'.");
		}

		return new StringResult(result);
	}

	/**
	 * Sends a "<em>lost password</em>" email to the given {@code userEmail} corresponding {@link User}.
	 * 
	 * @param email
	 *          The user email.
	 * @param language
	 *          The user language.
	 * @param context
	 *          The execution context.
	 * @throws CommandException
	 *           If an error occurs or arguments are invalid.
	 */
	@Transactional(rollbackOn = { Exception.class
	})
	// Transactional methods cannot be private (see Guice documentation).
	protected void forgotPassword(final String email, final Language language, final UserExecutionContext context) throws CommandException {

		if (StringUtils.isBlank(email)) {
			throw new CommandException("User email is invalid.");
		}

		// Retrieves user.
		final User user = userDAO.findUserByEmail(email);

		if (user == null) {
			throw new CommandException("No user found for email '" + email + "'.");
		}

		// Unique key is stored and sent by email.
		final String changePasswordKey = UUID.randomUUID().toString().replaceAll("-", "");

		// Stores the "change password" key and date.
		user.setChangePasswordKey(changePasswordKey);
		user.setDateChangePasswordKeyIssued(new Date());
		userDAO.persist(user, context.getUser());

		// Builds a link to pass by email, so the user can reset the password following the link.
		final Map<RequestParameter, String> linkParameters = new HashMap<>();
		linkParameters.put(RequestParameter.ID, changePasswordKey);
		final String resetPasswordLink = context.getApplicationUrl(Page.RESET_PASSWORD, linkParameters);

		// Sends email to user.
		final Map<EmailKey, String> emailParameters = new HashMap<>();
		final EmailType emailType = EmailType.LOST_PASSWORD;
		emailParameters.put(EmailKeyEnum.RESET_PASSWORD_LINK, resetPasswordLink);

		if (!mailService.send(emailType, emailParameters, language, email)) {
			throw new CommandException("Sending email '" + emailType + "' to '" + email + "' failed. Rollbacking.");
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Password reset email has been successfully sent to '{}' with following reset link: '{}'.", email, resetPasswordLink);
		}
	}

	/**
	 * Updates the given {@code email} corresponding {@link User}'s password with the given one.
	 * 
	 * @param email
	 *          The user email.
	 * @param newPassword
	 *          The new password.
	 * @param context
	 *          The execution context.
	 * @throws CommandException
	 *           If an error occurs or arguments are invalid.
	 */
	private void updatePassword(final String email, final String newPassword, final UserExecutionContext context) throws CommandException {

		if (StringUtils.isBlank(email)) {
			throw new CommandException("User email is invalid.");
		}

		if (StringUtils.isBlank(newPassword)) {
			throw new CommandException("User's new password is invalid.");
		}

		final User user = userDAO.findUserByEmail(email);

		if (user == null) {
			throw new CommandException("No user found for email '" + email + "'.");
		}

		user.setHashedPassword(authenticator.hashPassword(newPassword));

		// Deactivates "change password" key.
		user.clearChangePasswordKey();
		userDAO.persist(user, context.getUser());

		if (LOG.isInfoEnabled()) {
			LOG.info("User '{}' ({}) password has been successfully updated using password reset link.", user, email);
		}
	}

	/**
	 * Retrieves the given "<em>change password</em>" {@code token} corresponding user email.
	 * 
	 * @param token
	 *          The "<em>change password</em>" token.
	 * @param context
	 *          The execution context.
	 * @return The user email, or {@code null} if no user found for {@code token} or invalid token.
	 * @throws CommandException
	 *           If an error occurs or arguments are invalid.
	 */
	private String retrieveEmailFromToken(final String token, final UserExecutionContext context) throws CommandException {

		if (StringUtils.isBlank(token)) {
			throw new CommandException("Change passord token is invalid.");
		}

		// Finds user.
		final User user = userDAO.findUserByChangePasswordKey(token);

		if (user == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No user found for \"change password\" token '{}'.", token);
			}
			return null;
		}

		if (user.getDateChangePasswordKeyIssued() == null) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("User '{}' has been found for \"change password\" token '{}', but its issued date is null (should not be possible).", user, token);
			}
			return null;
		}

		// Has token expired?
		final long timeSpentInSecs = (new Date().getTime() - user.getDateChangePasswordKeyIssued().getTime()) / 1000;

		if (timeSpentInSecs > CHANGE_PASSWORD_TIMEOUT) {
			// Expired token.
			if (LOG.isDebugEnabled()) {
				LOG.debug("User '{}' has an expired \"change password\" token '{}'. Reseting user \"change password\" properties.", user, token);
			}

			user.clearChangePasswordKey();
			userDAO.persist(user, context.getUser());

			return null;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("User '{}' has beend found for \"change password\" token '{}'.", user, token);
		}

		return user.getEmail();
	}

}
