package org.sigmah.client.dispatch;

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
