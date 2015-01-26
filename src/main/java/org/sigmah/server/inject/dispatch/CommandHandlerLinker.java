package org.sigmah.server.inject.dispatch;

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
