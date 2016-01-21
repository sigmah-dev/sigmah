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
