package org.sigmah.shared.dispatch;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

/**
 * Exception thrown if no handler can be found for a command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UnsupportedCommandException extends CommandException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7759072862530169401L;

	protected UnsupportedCommandException() {
		// Serialization.
	}

	@SuppressWarnings({ "unchecked"
	})
	public UnsupportedCommandException(final Command<? extends Result> command) {
		this((Class<? extends Command<? extends Result>>) command.getClass());
	}

	public UnsupportedCommandException(final Class<? extends Command<? extends Result>> commandClass) {
		super("No handler is registered for " + commandClass.getName());
	}

}
