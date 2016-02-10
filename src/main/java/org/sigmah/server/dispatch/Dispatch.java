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
