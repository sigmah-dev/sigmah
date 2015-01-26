package org.sigmah.shared.command.base;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.result.Result;

/**
 * <p>
 * Abstract dispatch command.
 * </p>
 * <p>
 * All command implementations should extend this abstract layer and declare an empty constructor.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <R>
 *          The command result type.
 */
public abstract class AbstractCommand<R extends Result> implements Command<R> {

	/**
	 * Empty constructor necessary for RPC serialization.
	 */
	protected AbstractCommand() {
		// Serialization.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		appendToString(builder);
		return builder.toString();
	}

	/**
	 * <p>
	 * Allows child commands to append other properties to the given {@code builder}.
	 * </p>
	 * <p>
	 * Use given builder this way:
	 * 
	 * <pre>
	 * builder.append(&quot;Property name&quot;, property);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param builder
	 *          The {@code toString} builder (never {@code null}).
	 */
	protected void appendToString(final ToStringBuilder builder) {
		// Child implementation should override this method.
	}

}
