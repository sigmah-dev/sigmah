package org.sigmah.server.dispatch;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Instances of this interface will handle specific types of {@link Command} classes.
 * 
 * @param <C>
 *          The command type.
 * @param <R>
 *          The command result type.
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface CommandHandler<C extends Command<R>, R extends Result> {

	/**
	 * Returns the type of {@link Command} supported by this handler.
	 * 
	 * @return The type of {@link Command} supported by this handler.
	 */
	Class<C> getCommandType();

	/**
	 * Handles the specified {@code command}.
	 * 
	 * @param command
	 *          The command.
	 * @return The command execution {@link Result}.
	 * @throws CommandException
	 *           If there is a problem performing the specified command.
	 */
	R execute(final C command, final ExecutionContext context) throws CommandException;

	/**
	 * Attempts to roll back the specified {@code command}.
	 * 
	 * @param command
	 *          The command.
	 * @param result
	 *          The result of the command.
	 * @param context
	 *          The execution context.
	 * @throws CommandException
	 *           If there is a problem performing the specified command rollback.
	 */
	void rollback(final C command, final R result, final ExecutionContext context) throws CommandException;

}
