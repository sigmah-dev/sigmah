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
