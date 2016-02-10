package org.sigmah.client.dispatch;

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
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.Result;

/**
 * Provides an interface through which caches can monitor the
 * execution of remote service calls.
 *
 * @param <C> Type of command to listen to.
 * @param <R> Result type.
 */
public interface DispatchListener<C extends Command<R>, R extends Result> {
    /**
     * Called following the successful dispatch of the given command.
	 * @param command Dispatched command.
	 * @param result Result of the command.
	 * @param authentication authenticated user.
     */
    void onSuccess(C command, R result, Authentication authentication);
}
