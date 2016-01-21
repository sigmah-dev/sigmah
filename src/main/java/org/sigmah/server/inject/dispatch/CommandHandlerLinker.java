package org.sigmah.server.inject.dispatch;

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

import java.util.List;

import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.dispatch.CommandHandlerRegistry;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * This class links any registered {@link CommandHandler} instances with the default {@link CommandHandlerRegistry}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class CommandHandlerLinker {

	private CommandHandlerLinker() {
		// Only provides static methods.
	}

	/**
	 * Links the command handlers.
	 * 
	 * @param injector
	 *          The guice injector.
	 * @param registry
	 *          The command-handler registry.
	 */
	@Inject
	@SuppressWarnings({
											"unchecked",
											"rawtypes"
	})
	static void linkHandlers(final Injector injector, final CommandHandlerRegistry registry) {

		final List<Binding<CommandHandlerMap>> bindings = injector.findBindingsByType(TypeLiteral.get(CommandHandlerMap.class));

		for (final Binding<CommandHandlerMap> binding : bindings) {
			final CommandHandlerMap map = binding.getProvider().get();
			registry.addHandlerClass(map.getCommandClass(), map.getCommandHandlerClass());
		}
	}

}
