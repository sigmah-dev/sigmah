package org.sigmah.client.dispatch;

import java.util.Collection;

import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.server.dispatch.Dispatch;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <p>
 * This is an asynchronous equivalent of the {@link Dispatch} interface on the server side.
 * </p>
 * <p>
 * The reason it exists is because GWT currently can't correctly handle having generic method templates in method
 * signatures, for example:
 * 
 * <pre>
 * &lt;C&gt; C create(Class&lt;C&gt; type)
 * </pre>
 * 
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface DispatchAsync {

	/**
	 * Executes asynchronously the given {@code command} and process the given {@code callback} once asynchronous
	 * execution is done.
	 * 
	 * @param command
	 *          The executed command.
	 * @param callback
	 *          The callback executed once asynchronous execution is done.
	 * @param loadables
	 *          (optional) Loadable element(s) to set in <em>loading</em> state during command execution.
	 */
	<C extends Command<R>, R extends Result> void execute(final C command, final AsyncCallback<R> callback, final Loadable... loadables);

	/**
	 * Executes asynchronously the given {@code command} and process the given {@code callback} once asynchronous
	 * execution is done.
	 * 
	 * @param command
	 *          The executed command.
	 * @param callback
	 *          The callback executed once asynchronous execution is done.
	 * @param loadables
	 *          (optional) Loadable element(s) collection to set in <em>loading</em> state during command execution.
	 */
	<C extends Command<R>, R extends Result> void execute(final C command, final AsyncCallback<R> callback, final Collection<Loadable> loadables);

}
