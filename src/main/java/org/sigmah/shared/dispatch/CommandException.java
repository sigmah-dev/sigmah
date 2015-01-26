package org.sigmah.shared.dispatch;

/**
 * <p>
 * Command exception.
 * </p>
 * <p>
 * Exception thrown by services when there is a low-level problem while processing a command execution.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CommandException extends DispatchException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2274470850863936403L;

	protected CommandException() {
		// Serialization.
	}

	public CommandException(final String message) {
		super(message);
	}

	public CommandException(final Throwable cause) {
		super(cause);
	}

	public CommandException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
