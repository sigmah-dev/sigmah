package org.sigmah.server.dispatch.impl;

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


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.StopWatch;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.security.SecureDispatchAsync.CommandExecution;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.dispatch.CommandHandlerRegistry;
import org.sigmah.server.dispatch.Dispatch;
import org.sigmah.server.dispatch.ExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.DispatchException;
import org.sigmah.shared.dispatch.UnsupportedCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Dispatch custom implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UserDispatch implements Dispatch {

	/**
	 * Execution context provided to handlers in order to execute sub-commands.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static final class UserExecutionContext extends ServletExecutionContext implements ExecutionContext {

		/**
		 * The dispatch instance.
		 */
		private final UserDispatch dispatch;

		/**
		 * Sub-commands.
		 */
		private final List<CommandResult<?, ?>> commandResults;

		/**
		 * The application URL.
		 */
		private final String applicationUrl;

		/**
		 * Initializes a new user execution context.
		 * 
		 * @param {@link #dispatch}
		 * @param {@link #user}
		 * @param {@link #request}
		 * @param {@link #originPageToken}
		 */
		private UserExecutionContext(final UserDispatch dispatch, final User user, final HttpServletRequest request, final String originPageToken) {

			super(user, request, originPageToken);

			this.dispatch = dispatch;
			this.commandResults = new java.util.ArrayList<CommandResult<?, ?>>();
			this.applicationUrl = request != null ? request.getHeader("Referer").split(PageRequest.URL_TOKEN)[0] : null;
		}

		/**
		 * <p>
		 * Initializes a new user execution context from the given {@code servletContext}.
		 * </p>
		 * <p>
		 * <b>Warning: this context cannot allow sub-command execution or provide application url.</b>
		 * </p>
		 * 
		 * @param servletContext
		 *          The servlet execution context.
		 */
		public UserExecutionContext(final ServletExecutionContext servletContext) {

			super(servletContext.getUser(), servletContext.getRequest(), servletContext.getOriginPageToken());

			this.dispatch = null;
			this.commandResults = null;
			this.applicationUrl = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <C extends Command<R>, R extends Result> R execute(final C command) throws CommandException {
			return execute(command, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <C extends Command<R>, R extends Result> R execute(final C command, final boolean allowRollback) throws CommandException {

			// Executes the sub-action.
			final R result = dispatch.doExecute(command, this);

			// Registers it and its result.
			if (allowRollback) {
				commandResults.add(new CommandResult<C, R>(command, result));
			}

			return result;
		}

		/**
		 * Cancels the memorized commands or results.
		 * 
		 * @throws DispatchException
		 */
		private void rollback() throws DispatchException {
			for (int i = commandResults.size() - 1; i >= 0; i--) {
				final CommandResult<?, ?> actionResult = commandResults.get(i);
				rollback(actionResult);
			}
		}

		/**
		 * Rollbacks a command execution.
		 * 
		 * @param <C>
		 *          The command type.
		 * @param <R>
		 *          The result type.
		 * @param commandResult
		 *          The command and the command result.
		 * @throws DispatchException
		 *           If the roll back failed.
		 */
		private <C extends Command<R>, R extends Result> void rollback(final CommandResult<C, R> commandResult) throws DispatchException {
			dispatch.doRollback(commandResult.getCommand(), commandResult.getResult(), this);
		}

		/**
		 * The application URL.
		 * 
		 * @return The application URL.
		 */
		public final String getApplicationUrl() {
			return getApplicationUrl(null, null);
		}

		/**
		 * The application URL.
		 * 
		 * @param page
		 *          The specific page to include into URL.
		 * @param parameters
		 *          The page parameters to include into URL.
		 * @return The application URL.
		 */
		public final String getApplicationUrl(final Page page, final Map<RequestParameter, String> parameters) {
			if (page == null) {
				return applicationUrl;
			}
			return applicationUrl + PageRequest.toUrl(page.getToken(), parameters);
		}
	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UserDispatch.class);

	/**
	 * The command-handler registry instance used to find commands handler.
	 */
	private final CommandHandlerRegistry handlerRegistry;

	@Inject
	public UserDispatch(final CommandHandlerRegistry handlerRegistry) {
		this.handlerRegistry = handlerRegistry;
	}

	/**
	 * Find the given {@code command} corresponding handler.
	 * 
	 * @param <C>
	 *          The command type.
	 * @param <R>
	 *          The command result type.
	 * @param command
	 *          The command.
	 * @return the given {@code command} corresponding handler.
	 * @throws UnsupportedCommandException
	 *           If no handler cannot be found for the command.
	 */
	private <C extends Command<R>, R extends Result> CommandHandler<C, R> findHandler(final C command) throws UnsupportedCommandException {

		// Asks the handler to the registry.
		final CommandHandler<C, R> handler = handlerRegistry.findHandler(command);

		// If there is no handler, throws an exception.
		if (handler == null) {
			throw new UnsupportedCommandException(command);
		}

		return handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Command<R>, R extends Result> R execute(final CommandExecution<C, R> commandExecution, final User user, final HttpServletRequest request)
			throws DispatchException {

		// Builds a new user execution context.
		final UserExecutionContext context = createContext(user, request, commandExecution.getCurrentPageToken());

		try {

			// Tries to execute the action.
			return doExecute(commandExecution.getCommand(), context);

		} catch (final CommandException e) {
			// Rollback if necessary.
			context.rollback();
			throw e;
		}
	}
	
	/**
	 * Executes the given command from server side.
	 * 
	 * @param <C> 
	 *			Command type.
	 * @param <R> 
	 *			Result type.
	 * @param command 
	 *			Command to execute.
	 * @param executionContext 
	 *			Execution context of the servlet.
	 * @return Execution result.
	 * @throws DispatchException 
	 *			If the command handler execution fails.
	 */
	public <C extends Command<R>, R extends Result> R execute(final C command, final ServletExecutionContext executionContext)
			throws DispatchException {
		
		// Builds a new user execution context.
		final UserExecutionContext context = createContext(executionContext.getUser(), executionContext.getRequest(), null);

		try {
			// Tries to execute the action.
			return doExecute(command, context);

		} catch (final CommandException e) {
			// Rollback if necessary.
			context.rollback();
			throw e;
		}
	}
	
	/**
	 * Creates a new <code>UserExecutionContext</code> for the given request and
	 * user.
	 * 
	 * @param user
	 *          The user executing the command.
	 * @param request
	 *          The servlet HTTP request.
	 * @param originPageToken
	 *          Token of the page.
	 * @return A new <code>UserExecutionContext</code>.
	 */
	public UserExecutionContext createContext(final User user, final HttpServletRequest request, final String originPageToken) {
		
		return new UserExecutionContext(this, user, request, originPageToken);
	}

	/**
	 * Executes a command.
	 * 
	 * @param <C>
	 *          The command type.
	 * @param <R>
	 *          The command result type.
	 * @param context
	 *          The execution context.
	 * @return The command execution result.
	 * @throws CommandException
	 *           If the command handler execution fails.
	 */
	private <C extends Command<R>, R extends Result> R doExecute(final C command, final UserExecutionContext context) throws CommandException {

		// Retrieves the handler.
		final CommandHandler<C, R> handler = findHandler(command);

		if (LOG.isDebugEnabled()) {
			LOG.debug("EXECUTING COMMAND - Command: '{}' ; Handler: '{}' ; User: '{}'.", command, handler, context.getUser());
		}

		final StopWatch chrono;
		if (LOG.isDebugEnabled()) {
			chrono = new StopWatch();
			chrono.start();

		} else {
			chrono = null;
		}

		// Asks for the action execution.
		final R executionResult = handler.execute(command, context);

		if (LOG.isDebugEnabled() && chrono != null) {
			chrono.stop();
			LOG.debug("COMMAND '{}' EXECUTED IN {} MS.", command, chrono.getTime());
		}

		return executionResult;
	}

	/**
	 * Rollbacks a command.
	 * 
	 * @param <C>
	 *          The command type.
	 * @param <R>
	 *          The command result type.
	 * @param command
	 *          The command.
	 * @param result
	 *          The command result.
	 * @param ctx
	 *          The execution context.
	 * @throws DispatchException
	 *           If execution fails.
	 */
	private <C extends Command<R>, R extends Result> void doRollback(final C command, final R result, final UserExecutionContext context)
			throws DispatchException {

		// Retrieves the handler.
		final CommandHandler<C, R> handler = findHandler(command);

		if (LOG.isInfoEnabled()) {
			LOG.info("ROLLBACKING COMMAND - Action: '{}' ; Handler: '{}' ; User: '{}'.", command, handler, context.getUser());
		}

		// Asks for the action rollback.
		handler.rollback(command, result, context);
	}

}
