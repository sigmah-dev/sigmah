package org.sigmah.client.security;

import org.sigmah.client.dispatch.ExceptionHandler;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.security.InvalidSessionException;
import org.sigmah.shared.security.UnauthorizedAccessException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.inject.Inject;

/**
 * Handles exception thrown by the command pattern.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SecureExceptionHandler implements ExceptionHandler {

	/**
	 * The injected event bus.
	 */
	private final EventBus eventBus;

	@Inject
	public SecureExceptionHandler(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status onFailure(final Throwable e) {

		if (e instanceof IncompatibleRemoteServiceException) {
			// The correct response to receiving an instance of this exception in the AsyncCallback.onFailure(Throwable)
			// method is to get the application into a state where a browser refresh can be done.
			// TODO This needs to be handled by the user interface somewhere.
			if (Log.isErrorEnabled()) {
				Log.error("Exception handler intercepts an IncompatibleRemoteServiceException ; application refresh should fix the problem.", e);
			}

			N10N.errorNotif(I18N.CONSTANTS.incompatibleRemoteServiceException());
			
			return Status.RETRY_OFFLINE;

		} else if (e instanceof InvocationException) {
			// Network connection problem.
			if (Log.isErrorEnabled()) {
				Log.error("Exception handler intercepts an InvocationException probably due to network connection problem.", e);
			}

			N10N.errorNotif("The process cannot be performed due to network connection problem.");// TODO [i18n]
			
			return Status.RETRY_OFFLINE;

		} else if (e instanceof InvalidSessionException) {

			// Intercepts an invalid session exception.

			if (Log.isDebugEnabled()) {
				Log.debug("Exception handler intercepts an invalid session.");
			}

			// Logout: clears the session and navigates to login page.
			eventBus.logout();

			return Status.STOP;

		} else if (e instanceof UnauthorizedAccessException) {

			// Intercepts a unauthorized access.

			if (Log.isDebugEnabled()) {
				Log.debug("Exception handler intercepts an unauthorized access.", e);
			}

			// The user executed an unauthorized action.

			// Inform the user.
			N10N.error(I18N.CONSTANTS.navigation_unauthorized_action());

			return Status.STOP;

		} else if (e instanceof FunctionalException) {

			// Intercepts a functional exception.

			if (Log.isWarnEnabled()) {
				Log.warn("Exception handler intercepts a functional exception.", e);
			}

			// Lets the dispatch manages the error.
			return Status.CONTINUE;

		} else if (e instanceof CommandException) {

			// Intercepts a service exception.

			if (Log.isErrorEnabled()) {
				Log.error("Exception handler intercepts a command exception.", e);
			}

			// Lets the dispatch manages the error.
			return Status.CONTINUE;

		} else {

			if (Log.isFatalEnabled()) {
				Log.fatal("Exception handler intercepts an unknown exception.", e);
			}

			// Lets the dispatch manages the error.
			return Status.CONTINUE;
		}
	}
}
