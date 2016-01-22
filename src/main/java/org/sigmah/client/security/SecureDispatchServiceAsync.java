package org.sigmah.client.security;

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

import org.sigmah.client.security.SecureDispatchAsync.CommandExecution;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Secure dispatch asynchronous service.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface SecureDispatchServiceAsync {

	/**
	 * Executes the given {@code commandExecution} corresponding {@link Command} and executes the given {@code callback}
	 * once command has been processed.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 * @param commandExecution
	 *          The {@link CommandExecution} containing {@link Command} to execute.
	 * @param callback
	 *          The callback executed once command has been processed.
	 */
	<C extends Command<R>, R extends Result> void execute(final CommandExecution<C, R> commandExecution, final AsyncCallback<Result> callback);

}
