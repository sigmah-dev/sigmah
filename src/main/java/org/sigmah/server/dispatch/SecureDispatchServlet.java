package org.sigmah.server.dispatch;

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

import javax.validation.ConstraintViolationException;

import org.sigmah.client.security.SecureDispatchAsync.CommandExecution;
import org.sigmah.client.security.SecureDispatchService;
import org.sigmah.server.domain.User;
import org.sigmah.server.security.SecureSessionValidator;
import org.sigmah.server.security.SecureSessionValidator.Access;
import org.sigmah.server.servlet.util.Servlets;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.DispatchException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.security.InvalidSessionException;
import org.sigmah.shared.security.UnauthorizedAccessException;
import org.sigmah.shared.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Abstract secure dispatch servlet.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class SecureDispatchServlet extends RemoteServiceServlet implements SecureDispatchService {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8811128792493692516L;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SecureDispatchServlet.class);

	/**
	 * Injected {@link SecureSessionValidator} instance.
	 */
	private final SecureSessionValidator sessionValidator;

	/**
	 * Injected server-side {@link Dispatch} instance.
	 */
	private final Dispatch dispatch;

	/**
	 * Initializes the {@code SecureDispatchServlet} that uses injected arguments.
	 * 
	 * @param sessionValidator
	 *          The secure session validator service.
	 * @param dispatch
	 *          The dispatch service.
	 */
	@Inject
	public SecureDispatchServlet(final SecureSessionValidator sessionValidator, final Dispatch dispatch) {
		this.sessionValidator = sessionValidator;
		this.dispatch = dispatch;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void log(final String msg) {
		this.log(msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void log(final String message, final Throwable t) {
		if (t != null) {
			if (LOG.isErrorEnabled()) {
				LOG.error(message, t);
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug(message);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Command<R>, R extends Result> R execute(final CommandExecution<C, R> commandExecution) throws DispatchException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Executing dispatch command.");
		}

		if (sessionValidator == null) {
			throw new CommandException("No session validator found for servlet '" + getServletName() + "'. Please verify your server-side configuration.");
		}

		if (dispatch == null) {
			throw new CommandException("No dispatch found for servlet '" + getServletName() + "'. Please verify your server-side configuration.");
		}

		User user = null;
		final String authToken = commandExecution.getAuthenticationToken();

		// Roles validation case.
		try {

			// Validates the user session and user access.
			final Access access = sessionValidator.validate(authToken, commandExecution);
			user = access.getUser();

			switch (access.getAccessType()) {

				case INVALID_SESSION:

					if (LOG.isDebugEnabled()) {
						LOG.debug("COMMAND EXECUTION FAILED - Command execution: '{}' ; User: '{}' ; Error: Invalid auth token '{}'.", commandExecution,
							Servlets.logUser(user), authToken);
					}

					throw new InvalidSessionException();

				case UNAUTHORIZED_ACCESS:

					if (LOG.isDebugEnabled()) {
						LOG
							.debug("COMMAND EXECUTION FAILED - Command execution: '{}' ; User: '{}' ; Error: Unauthorized access.", commandExecution, Servlets.logUser(user));
					}

					throw new UnauthorizedAccessException();

				default:

					// Access granted.
					if (LOG.isTraceEnabled()) {
						LOG.trace("COMMAND EXECUTION GRANTED - Command execution: '{}' ; User: '{}'.", commandExecution, Servlets.logUser(user));
					}

					// Command execution.
					return dispatch.execute(commandExecution, user, getThreadLocalRequest());
			}

		} catch (final FunctionalException e) {

			// Functional exception.
			if (LOG.isWarnEnabled()) {
				LOG.warn(
					"COMMAND EXECUTION ABORTED: A functional exception has been raised - Command execution: '"
						+ commandExecution
						+ "' ; User: '"
						+ Servlets.logUser(user)
						+ "'.", e);
			}

			throw e;

		} catch (final CommandException e) {

			// Command execution exception.
			if (LOG.isErrorEnabled()) {
				LOG.error("COMMAND EXECUTION FAILED - Command execution: '" + commandExecution + "' ; User: '" + Servlets.logUser(user) + "'.", e);
			}

			throw e;

		} catch (final ConstraintViolationException e) {

			// Bean validation failed.
			if (LOG.isErrorEnabled()) {
				LOG.error("COMMAND EXECUTION FAILED - Command execution: '"
					+ commandExecution
					+ "' ; User: '"
					+ Servlets.logUser(user)
					+ "' ; Error: A bean validation failed while executing '"
					+ commandExecution.getCommand()
					+ "'. Consider performing the validation on client-side.\n"
					+ Servlets.logConstraints(e.getConstraintViolations()), e);
			}

			throw new ValidationException("A bean validation failed while executing '" + commandExecution.getCommand() + "'.", e);

		} catch (final Throwable e) {

			// Server unknown error.
			if (LOG.isErrorEnabled()) {
				LOG.error("COMMAND EXECUTION FAILED - Command execution: '"
					+ commandExecution
					+ "' ; User: '"
					+ Servlets.logUser(user)
					+ "' ; RuntimeException while executing.", e);
			}

			throw new CommandException("Server error.", e);
		}
	}

}
