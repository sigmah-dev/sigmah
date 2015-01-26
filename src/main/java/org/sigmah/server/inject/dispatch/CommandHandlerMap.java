package org.sigmah.server.inject.dispatch;

import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

/**
 * Command-Handler map interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <C>
 *          The command type.
 * @param <R>
 *          The command result type.
 */
interface CommandHandlerMap<C extends Command<R>, R extends Result> {

	/**
	 * Returns the command class.
	 * 
	 * @return the command class.
	 */
	Class<C> getCommandClass();

	/**
	 * Returns the commandHandler class.
	 * 
	 * @return the commandHandler class.
	 */
	Class<? extends CommandHandler<C, R>> getCommandHandlerClass();

}
