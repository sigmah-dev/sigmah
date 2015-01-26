package org.sigmah.offline.dispatch;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <C>
 * @param <R>
 */
public interface AsyncCommandHandler<C extends Command<R>, R extends Result> {
	void execute(C command, OfflineExecutionContext executionContext, AsyncCallback<R> callback);
}
