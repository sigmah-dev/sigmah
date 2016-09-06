package org.sigmah.client.dispatch;

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

import com.allen_sauer.gwt.log.client.Log;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import java.util.Collection;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.offline.status.ApplicationState;

/**
 * <p>
 * An abstract base class that provides methods that can be called to handle success or failure results from the remote
 * service.
 * </p>
 * <p>
 * These should be called by the implementation of
 * {@link DispatchAsync#execute(Command, AsyncCallback, org.sigmah.client.ui.widget.Loadable...)}.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class AbstractDispatchAsync implements DispatchAsync, OfflineEvent.Source {

	/**
	 * The dispatch exception handler.
	 */
	@Inject
	private ExceptionHandler exceptionHandler;
	
	@Inject
	protected EventBus eventBus;
	
	/**
	 * Handles a failed command execution.
	 * 
	 * @param command
	 *          The failed command.
	 * @param caught
	 *          The exception.
	 * @param callback
	 *          The execution callback.
	 */
	protected <C extends Command<R>, R extends Result> void onFailure(final C command, final Throwable caught, final AsyncCallback<R> callback, final Collection<Loadable> loadables) {

		final ExceptionHandler.Status status;
		
		if(exceptionHandler != null) {
			status = exceptionHandler.onFailure(caught);
		} else {
			status = ExceptionHandler.Status.CONTINUE;
		}
		
		switch(status) {
			case STOP:
				return;
			case CONTINUE:
				callback.onFailure(caught);
				break;
			case RETRY_OFFLINE:
				Log.warn("An error happened while handling the command '" + command + "'. Retrying offline.", caught);
				eventBus.fireEvent(new OfflineEvent(this, ApplicationState.OFFLINE));
				execute(command, callback, loadables);
				break;
		}
	}

	/**
	 * Handles a success command execution.
	 * 
	 * @param command
	 *          The succeed command.
	 * @param result
	 *          The command execution result.
	 * @param callback
	 *          The execution callback.
	 */
	protected <C extends Command<R>, R extends Result> void onSuccess(final C command, final R result, final AsyncCallback<R> callback) {
		callback.onSuccess(result);
	}

}
