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
