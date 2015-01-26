package org.sigmah.server.handler.base;

import java.lang.reflect.ParameterizedType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.dispatch.ExecutionContext;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.util.DomainFilters;
import org.sigmah.server.inject.util.Injectors;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.DispatchException;

import com.google.inject.Inject;
import java.lang.reflect.Type;

/**
 * A super class for handlers which manages the specific execution context.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <C>
 *          The command type.
 * @param <R>
 *          The result type.
 */
public abstract class AbstractCommandHandler<C extends Command<R>, R extends Result> extends EntityManagerProvider implements CommandHandler<C, R> {

	/**
	 * Ensures that the execution context extends the {@link UserExecutionContext} class.
	 * 
	 * @param context
	 *          The execution context.
	 * @throws DispatchException
	 *           If the context doesn't extends the {@link UserExecutionContext} class.
	 */
	private static void ensureGuiceExecutionContext(final ExecutionContext context) throws CommandException {
		if (!(context instanceof UserExecutionContext)) {
			throw new CommandException("The execution context doesn't extends '"
				+ UserExecutionContext.class.getCanonicalName()
				+ "'. The handler cannot be executed.");
		}
	}

	/**
	 * The command type.
	 */
	private Class<C> clazz;

	@Inject
	private Mapper mapper;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final Class<C> getCommandType() {

		if (clazz == null) {
			final Class<?> clazz = Injectors.getClass(this);
			final Type type = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
			
			if(type instanceof Class) {
				this.clazz = (Class<C>) type;
			} else if(type instanceof ParameterizedType) {
				// If the command handler is parametrized, retrieves the raw class.
				this.clazz = (Class<C>) ((ParameterizedType)type).getRawType();
			} else {
				throw new UnsupportedOperationException("Type is not supported: " + type);
			}
		}

		return clazz;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final R execute(final C command, final ExecutionContext context) throws CommandException {

		if (context != null) {

			ensureGuiceExecutionContext(context);

			final UserExecutionContext uContext = (UserExecutionContext) context;

			// Activate filters into hibernate session.
			DomainFilters.applyUserFilter(uContext.getUser(), em());

			return execute(command, uContext);

		} else {

			return execute(command, null);

		}

	}

	/**
	 * Executes the given {@code command} within given {@code context}.
	 * 
	 * @param command
	 *          The command
	 * @param context
	 *          The execution context.
	 * @return The command execution result.
	 * @throws CommandException
	 *           If the command execution fails.
	 */
	protected abstract R execute(final C command, final UserExecutionContext context) throws CommandException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void rollback(final C command, R result, final ExecutionContext context) throws CommandException {

		if (context != null) {

			ensureGuiceExecutionContext(context);

			final UserExecutionContext uContext = (UserExecutionContext) context;
			rollback(command, result, uContext);

		} else {

			rollback(command, result, null);

		}

	}

	/**
	 * Rollbacks the given {@code command} execution.<br/>
	 * The default implementation does nothing.
	 * 
	 * @param command
	 *          The command.
	 * @param result
	 *          The command result.
	 * @param context
	 *          The context.
	 * @throws CommandException
	 *           If the rollback failed.
	 */
	public void rollback(final C command, R result, final UserExecutionContext context) throws CommandException {
		// Default implementation does nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return new ToStringBuilder(this).toString();
	}

	/**
	 * Returns the {@link Mapper} instance.
	 * 
	 * @return The {@link Mapper} instance, never {@code null}.
	 */
	protected final Mapper mapper() {
		return mapper;
	}

}
