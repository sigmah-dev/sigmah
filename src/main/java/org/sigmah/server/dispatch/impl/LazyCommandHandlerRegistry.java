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

import java.util.HashMap;
import java.util.Map;

import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.dispatch.CommandHandlerRegistry;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * <p>
 * This is a lazy-loading implementation of the registry. It will only create command handlers when they are first used.
 * All {@link CommandHandler} implementations <b>must</b> have a public, default constructor.
 * </p>
 * <p>
 * This uses Guice to create instances of registered {@link CommandHandler}s on in a lazy manner. That is, they are only
 * created upon the first request of a handler for the {@link Command} it is registered with, rather than requiring the
 * class to be constructed when the registry is initialized.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class LazyCommandHandlerRegistry implements CommandHandlerRegistry {

	/**
	 * The {@link Command} classes with their corresponding {@link CommandHandler} classes.
	 */
	private final Map<Class<? extends Command<?>>, Class<? extends CommandHandler<?, ?>>> handlerClasses;

	/**
	 * The {@link Command} classes with their corresponding {@link CommandHandler} implementations.
	 */
	private final Map<Class<? extends Command<?>>, CommandHandler<?, ?>> handlers;

	/**
	 * Injected injector.
	 */
	private final Injector injector;

	@Inject
	public LazyCommandHandlerRegistry(final Injector injector) {
		this.injector = injector;
		handlerClasses = new HashMap<>(100);
		handlers = new HashMap<>(100);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Command<R>, R extends Result> void addHandlerClass(final Class<C> commandClass, final Class<? extends CommandHandler<C, R>> handlerClass) {
		handlerClasses.put(commandClass, handlerClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Command<R>, R extends Result> void removeHandlerClass(final Class<C> commandClass, final Class<? extends CommandHandler<C, R>> handlerClass) {

		final Class<? extends CommandHandler<?, ?>> oldHandlerClass = handlerClasses.get(commandClass);

		if (oldHandlerClass == handlerClass) {
			handlerClasses.remove(commandClass);
			handlers.remove(commandClass);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Command<R>, R extends Result> CommandHandler<C, R> findHandler(final C command) {

		CommandHandler<?, ?> handler = handlers.get(command.getClass());

		if (handler == null) {
			final Class<? extends CommandHandler<?, ?>> handlerClass = handlerClasses.get(command.getClass());
			if (handlerClass != null) {
				handler = injector.getInstance(handlerClass); // Retrieves instance from guice injector.
				if (handler != null)
					handlers.put(handler.getCommandType(), handler);
			}
		}

		return (CommandHandler<C, R>) handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearHandlers() {
		handlers.clear();
	}

}
