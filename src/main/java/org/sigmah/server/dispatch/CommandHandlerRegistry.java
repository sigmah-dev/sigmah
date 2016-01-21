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

/**
 * <p>
 * Command-Handler registry interface.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface CommandHandlerRegistry {

	/**
	 * Searches the registry and returns the first handler which supports the specied command, or <code>null</code> if
	 * none is available.
	 * 
	 * @param command
	 *          The command.
	 * @return The command handler.
	 */
	<C extends Command<R>, R extends Result> CommandHandler<C, R> findHandler(final C command);

	/**
	 * Clears all registered handlers from the registry.
	 */
	void clearHandlers();

	/**
	 * Registers the specified {@link CommandHandler} class with the registry.
	 * 
	 * @param commandClass
	 *          The command class the handler handles.
	 * @param handlerClass
	 *          The handler class.
	 */
	<C extends Command<R>, R extends Result> void addHandlerClass(final Class<C> commandClass, final Class<? extends CommandHandler<C, R>> handlerClass);

	/**
	 * Removes any registration of the specified class, as well as any instances which have been created.
	 * 
	 * @param commandClass
	 *          The command class the handler handles.
	 * @param handlerClass
	 *          The handler class.
	 */
	<C extends Command<R>, R extends Result> void removeHandlerClass(final Class<C> commandClass, final Class<? extends CommandHandler<C, R>> handlerClass);

}
