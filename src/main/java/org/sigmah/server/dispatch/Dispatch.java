package org.sigmah.server.dispatch;

import javax.servlet.http.HttpServletRequest;

import org.sigmah.client.security.SecureDispatchAsync.CommandExecution;
import org.sigmah.server.domain.User;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.DispatchException;

/**
 * Executes commands and returns the results.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface Dispatch {

	/**
	 * Executes the specified {@code command} and returns the appropriate result.
	 * 
	 * @param <C>
	 *          The command type.
	 * @param <R>
	 *          The {@link Result} type returned by {@code command} execution.
	 * @param commandExecution
	 *          The command execution (containing {@link Command} to execute).
	 * @param user
	 *          The user executing the command.
	 * @param request
	 *          The servlet HTTP request.
	 * @return The command's result.
	 * @throws DispatchException
	 *           If the command execution failed.
	 */
	<C extends Command<R>, R extends Result> R execute(final CommandExecution<C, R> commandExecution, final User user, final HttpServletRequest request)
			throws DispatchException;

}
