package org.sigmah.client.dispatch;

import com.allen_sauer.gwt.log.client.Log;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.validation.ValidationException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.i18n.I18N;
import org.sigmah.offline.dispatch.UnavailableCommandException;

/**
 * Abstract class handling {@link Command} execution callback.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <R>
 *          The command result type.
 */
public abstract class CommandResultHandler<R extends Result> implements AsyncCallback<R> {

	/**
	 * A default void result instance.
	 */
	public static final CommandResultHandler<VoidResult> Void = voidResult();

	/**
	 * Builds a new empty result handler instance.<br>
	 * Can be cast to the appropriate type if necessary.
	 */
	public static final <T extends Result> CommandResultHandler<T> voidResult() {
		return new CommandResultHandler<T>() {

			@Override
			public void onCommandSuccess(final T result) {
				return;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onSuccess(final R result) {
		onCommandSuccess(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onFailure(final Throwable caught) {

		if (caught instanceof ValidationException) {
			// Validation exception
			onCommandViolation((ValidationException) caught);

		} else if (caught instanceof FunctionalException) {
			// Functional exception.
			onFunctionalException((FunctionalException) caught);
			
		} else if (caught instanceof UnavailableCommandException) {
			// Command is unavailable when offline.
			onUnavailableCommandException((UnavailableCommandException) caught);

		} else {
			onCommandFailure(caught);
		}
	}

	// --------------------------------------------------------------------------------
	//
	// OVERRIDABLE METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Callback executed on command execution success.
	 * 
	 * @param result
	 *          The command execution result.
	 */
	protected abstract void onCommandSuccess(final R result);

	/**
	 * Callback executed if server-side process throws an exception.<br>
	 * <em>Default implementation simply throws a {@link RuntimeException}.</em>
	 * 
	 * @param caught
	 *          The exception.
	 */
	protected void onCommandFailure(final Throwable caught) {
		// Default behaviour that can be overrided by child implementation.
		throw new RuntimeException(caught);
	}

	/**
	 * Callback executed if server-side process throws a constraint violation exception.<br>
	 * <em>Default implementation simply throws a {@link RuntimeException}.</em>
	 * 
	 * @param caught
	 *          The validation exception.
	 * @see ValidationException
	 */
	protected void onCommandViolation(final ValidationException caught) {
		// Default behaviour that can be overrided by child implementation.
		onCommandFailure(caught);
	}

	/**
	 * Method called when the server throws a <b>functional</b> exception.<br>
	 * The default implementation displays a <b>warning</b> message.
	 * 
	 * @param exception
	 *          The functional exception (cannot be {@code null}).
	 * @see FunctionalException
	 */
	protected void onFunctionalException(final FunctionalException exception) {
		// Default implementation displays a warning message.
		N10N.warn(exception.getTitle(), exception.getMessage());
	}

	/**
	 * Method called when the user tries to access an unavailable functionnality
	 * in offline mode.
	 * 
	 * @param exception 
	 *			The exception (cannot be {@code null}).
	 */
	protected void onUnavailableCommandException(final UnavailableCommandException exception) {
		Log.warn("Command unavailable when offline.", exception);
		N10N.info(I18N.CONSTANTS.sigmahOfflineUnavailable(), I18N.CONSTANTS.sigmahOfflineNotAvailable());
	}
}
