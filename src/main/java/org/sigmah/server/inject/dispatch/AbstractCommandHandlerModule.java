package org.sigmah.server.inject.dispatch;

import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.dispatch.CommandHandlerRegistry;
import org.sigmah.server.dispatch.Dispatch;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.internal.UniqueAnnotations;

/**
 * This is an abstract base class that configures Guice to inject {@link Dispatch} and {@link CommandHandler} instances.
 * If no other prior instance of {@link DispatchModule} has been installed, the standard {@link Dispatch} and
 * {@link CommandHandlerRegistry} classes will be configured.
 * <p/>
 * <p/>
 * Implement the {@link #configureHandlers()} method and call {@link #bindHandler(Class, Class)} to register handler
 * implementations. For example:
 * <p/>
 * 
 * <pre>
 * public class CommandHandlerModule extends AbstractCommandHandlerModule {
 *      \@Override
 *      protected void configureHandlers() {
 *          bindHandler( MyCommand.class, MyCommandHandler.class );
 *      }
 * }
 * </pre>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class AbstractCommandHandlerModule extends AbstractModule {

	/**
	 * {@link CommandHandlerMap} implementation.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 * @param <C>
	 *          The command type.
	 * @param <R>
	 *          The command result type.
	 */
	private static class CommandHandlerMapImpl<C extends Command<R>, R extends Result> implements CommandHandlerMap<C, R> {

		private final Class<C> commandClass;
		private final Class<? extends CommandHandler<C, R>> handlerClass;

		public CommandHandlerMapImpl(final Class<C> commandClass, final Class<? extends CommandHandler<C, R>> handlerClass) {
			this.commandClass = commandClass;
			this.handlerClass = handlerClass;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Class<C> getCommandClass() {
			return commandClass;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Class<? extends CommandHandler<C, R>> getCommandHandlerClass() {
			return handlerClass;
		}

	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractCommandHandlerModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void configure() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Installing command-handler module.");
		}

		// This will only get installed once due to equals/hashCode override.
		install(new DispatchModule());

		configureHandlers();
	}

	/**
	 * Override this method to configure handlers.
	 */
	protected abstract void configureHandlers();

	/**
	 * Binds the specified {@link CommandHandler} instance class.
	 * 
	 * @param commandClass
	 *          The command class.
	 * @param handlerClass
	 *          The command handler class.
	 */
	protected final <C extends Command<R>, R extends Result, H extends CommandHandler<C, R>> void bindHandler(final Class<C> commandClass,
			final Class<H> handlerClass) {
		bind(CommandHandlerMap.class).annotatedWith(UniqueAnnotations.create()).toInstance(new CommandHandlerMapImpl<C, R>(commandClass, handlerClass));
	}

}
