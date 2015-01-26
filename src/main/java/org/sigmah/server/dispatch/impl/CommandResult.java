package org.sigmah.server.dispatch.impl;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

/**
 * This contains both the original {@link Command} and the {@link Result} of that command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <C>
 *          The command type.
 * @param <R>
 *          The result type.
 */
final class CommandResult<C extends Command<R>, R extends Result> {

	/**
	 * The command.
	 */
	private final C command;

	/**
	 * The command result.
	 */
	private final R result;

	public CommandResult(final C command, final R result) {
		this.command = command;
		this.result = result;
	}

	public C getCommand() {
		return command;
	}

	public R getResult() {
		return result;
	}

}
