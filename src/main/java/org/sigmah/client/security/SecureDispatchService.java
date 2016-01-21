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
import org.sigmah.shared.dispatch.DispatchException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * <p>
 * Secure dispatch service.
 * </p>
 * <p>
 * This service is used to execute {@link Command}s on server-side through {@code RPC} protocol.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.client.security.SecureDispatchServiceAsync
 */
@RemoteServiceRelativePath(SecureDispatchService.REMOTE_SERVICE_RELATIVE_PATH)
public interface SecureDispatchService extends RemoteService {

	/**
	 * Remote service relative path.
	 */
	public static final String REMOTE_SERVICE_RELATIVE_PATH = "dispatch";

	/**
	 * Executes the given {@code commandExecution}.
	 * 
	 * @param commandExecution
	 *          The command excution wrapper (containing command to execute, auth token, etc.).
	 * @param <C> Command type.
	 * @param <R> Result type.
	 * @return the command execution result.
	 * @throws DispatchException
	 *           If the command execution fails.
	 */
	<C extends Command<R>, R extends Result> R execute(final CommandExecution<C, R> commandExecution) throws DispatchException;

}
