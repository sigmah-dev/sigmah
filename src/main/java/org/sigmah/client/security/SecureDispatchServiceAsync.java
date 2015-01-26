package org.sigmah.client.security;

import org.sigmah.client.security.SecureDispatchAsync.CommandExecution;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Secure dispatch asynchronous service.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface SecureDispatchServiceAsync {

	/**
	 * Executes the given {@code commandExecution} corresponding {@link Command} and executes the given {@code callback}
	 * once command has been processed.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 * @param commandExecution
	 *          The {@link CommandExecution} containing {@link Command} to execute.
	 * @param callback
	 *          The callback executed once command has been processed.
	 */
	<C extends Command<R>, R extends Result> void execute(final CommandExecution<C, R> commandExecution, final AsyncCallback<Result> callback);

}
