package org.sigmah.offline.sync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

/**
 * Execute a queue of commands and stop completely if one of them fails.
 * <p/>
 * Each command has the possibility to add new commands to the queue. They will
 * be executed as well.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CommandQueue {
	
	/**
	 * Command dispatcher.
	 */
	private final DispatchAsync dispatchAsync;
	
	/**
	 * Called when the execution succeed or fails.
	 */
	private final AsyncCallback<Void> callback;
	
	/**
	 * Queue of command to execute.
	 */
	private final List<Entry<?, ?>> entries;

	/**
	 * Create a new command queue.
	 * 
	 * @param callback Callback to call when the execution finishes.
	 * @param dispatchAsync Command dispatcher to use.
	 */
	public CommandQueue(AsyncCallback<Void> callback, DispatchAsync dispatchAsync) {
		this.callback = callback;
		this.entries = new ArrayList<Entry<?, ?>>();
		this.dispatchAsync = dispatchAsync;
	}
	
	/**
	 * Add a command and its callback to the queue.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 * @param command Command to add to the queue.
	 * @param callback Callback to call when the command has been executed with success (not called in case of failure).
	 */
	public <C extends Command<R>, R extends Result> void add(C command, AsyncCallback<R> callback) {
		entries.add(new Entry<C, R>(command, callback));
	}
	
	/**
	 * Start the execution.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 * @param loadables Elements to mask during dispatch.
	 */
	public <C extends Command<R>, R extends Result> void run(final Loadable... loadables) {
		if(!entries.isEmpty()) {
			final Entry<C, R> entry = next();
			
			dispatchAsync.execute(entry.getCommand(), new AsyncCallback<R>() {

				@Override
				public void onFailure(Throwable caught) {
					stopOnFailure(caught);
				}

				@Override
				public void onSuccess(R result) {
					try {
						entry.getCallback().onSuccess(result);
						run(loadables);
						
					} catch(RuntimeException e) {
						stopOnFailure(e);
					}
				}
			}, loadables);
			
		} else {
			callback.onSuccess(null);
		}
	}
	
	private void stopOnFailure(Throwable caught) {
		entries.clear();
		callback.onFailure(caught);
	}
	
	/**
	 * Retrieves the next command and removes it from the queue.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 * @return The next command.
	 */
	private <C extends Command<R>, R extends Result> Entry<C, R> next() {
		return (Entry<C, R>) entries.remove(0);
	}
	
	/**
	 * Entry type.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 */
	private static class Entry<C extends Command<R>, R extends Result> {
		private final C command;
		private final AsyncCallback<R> callback;

		public Entry(C command, AsyncCallback<R> callback) {
			this.command = command;
			this.callback = callback;
		}

		public C getCommand() {
			return command;
		}
		
		public AsyncCallback<R> getCallback() {
			return callback;
		}
	}
}
