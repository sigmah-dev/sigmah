package org.sigmah.client.dispatch;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.Result;

/**
 * Provides an interface through which caches can monitor the
 * execution of remote service calls.
 *
 * @param <C> Type of command to listen to.
 * @param <R> Result type.
 */
public interface DispatchListener<C extends Command<R>, R extends Result> {
    /**
     * Called following the successful dispatch of the given command.
	 * @param command Dispatched command.
	 * @param result Result of the command.
	 * @param authentication authenticated user.
     */
    void onSuccess(C command, R result, Authentication authentication);
}
