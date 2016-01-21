package org.sigmah.client.security;

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

			N10N.error(I18N.CONSTANTS.incompatibleRemoteServiceException());
			
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
			N10N.info(I18N.CONSTANTS.navigation_unauthorized_action());

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
