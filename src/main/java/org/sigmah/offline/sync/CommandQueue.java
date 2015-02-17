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
	private final List<QueueEntry<?>> entries;

	/**
	 * Create a new command queue.
	 * 
	 * @param callback Callback to call when the execution finishes.
	 * @param dispatchAsync Command dispatcher to use.
	 */
	public CommandQueue(AsyncCallback<Void> callback, DispatchAsync dispatchAsync) {
		this.callback = callback;
		this.entries = new ArrayList<QueueEntry<?>>();
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
	 * Add an user defined entry to the queue.
	 * 
	 * @param entry Entry to add.
	 */
	public void add(QueueEntry<?> entry) {
		entries.add(entry);
	}
	
	/**
	 * Start the execution.
	 * 
	 * @param <R> Result type.
	 * @param loadables Elements to mask during dispatch.
	 */
	public <R> void run(final Loadable... loadables) {
		if(!entries.isEmpty()) {
			final QueueEntry<R> entry = (QueueEntry<R>) entries.remove(0);
			
			try {
				entry.run(new AsyncCallback<R>() {

					@Override
					public void onFailure(Throwable caught) {
						stopOnFailure(caught);
					}

					@Override
					public void onSuccess(R result) {
						run(loadables);
					}

				}, loadables);
				
			} catch(RuntimeException e) {
				stopOnFailure(e);
			}
			
		} else {
			callback.onSuccess(null);
		}
	}
	
	private void stopOnFailure(Throwable caught) {
		entries.clear();
		callback.onFailure(caught);
	}
	
	/**
	 * Entry type.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 */
	private class Entry<C extends Command<R>, R extends Result> implements QueueEntry<R> {
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

		@Override
		public void run(final AsyncCallback<R> callback, Loadable... loadables) {
			dispatchAsync.execute(command, new AsyncCallback<R>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(R result) {
					Entry.this.callback.onSuccess(result);
					callback.onSuccess(result);
				}
			}, loadables);
		}
	}
}
