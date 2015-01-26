package org.sigmah.server.dispatch;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.DispatchException;

/**
 * ExecutionContext instances are passed to {@link CommandHandler}s, and allows them to execute sub-commands. These
 * commands can be automatically rolled back if any part of the command handler fails.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ExecutionContext {

	/**
	 * Executes the given {@code command} in the current context. If {@code rollbackOnException} is set to {@code true},
	 * the command will be rolled back if the surrounding execution fails.
	 * 
	 * @param <C>
	 *          The command type.
	 * @param <R>
	 *          The result type.
	 * @param command
	 *          The command.
	 * @param allowRollback
	 *          If {@code true}, any failure in the surrounding execution will trigger a rollback of the action.
	 * @return The result.
	 * @throws DispatchException
	 */
	<C extends Command<R>, R extends Result> R execute(final C command, final boolean allowRollback) throws DispatchException;

	/**
	 * Executes the given {@code command} in the current context. If the surrounding execution fails, the command will be
	 * rolled back.
	 * 
	 * @param <C>
	 *          The command type.
	 * @param <R>
	 *          The result type.
	 * @param command
	 *          The command.
	 * @return The result.
	 * @throws DispatchException
	 */
	<C extends Command<R>, R extends Result> R execute(final C command) throws DispatchException;

}
