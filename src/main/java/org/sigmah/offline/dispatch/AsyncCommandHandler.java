package org.sigmah.offline.dispatch;

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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Instances of this interface will handle specific types of {@link Command} classes when the user is offline.
 * 
 * @param <C> Command type.
 * @param <R> Result type of the command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @since 2.0
 */
public interface AsyncCommandHandler<C extends Command<R>, R extends Result> {
    
    /**
	 * Handles the specified {@code command}.
     * <p>
     * If an exception occurs during the execution, it should be sent back through the given <code>callback</code>.
	 * 
	 * @param command
	 *          The command.
     * @param executionContext
     *          The execution context (contains information about the user executing the command).
     * @param callback
     *          Will be called when the execution finishes.
	 */
	void execute(C command, OfflineExecutionContext executionContext, AsyncCallback<R> callback);
    
}
