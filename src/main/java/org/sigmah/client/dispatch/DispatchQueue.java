package org.sigmah.client.dispatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.validation.ValidationException;

/**
 * <p>
 * Dispatch queue.
 * </p>
 * <p>
 * Enables parallel dispatch actions calls in an easy way.<br>
 * Client-side order can be preserved if necessary (attention, not server-side).
 * </p>
 * <p>
 * How does it work ?
 * 
 * <pre>
 * final DispatchQueue queue = new DispatchQueue(DispatchAsync) {
 * 
 * 	public void onComplete() {
 * 		// Do something... or not !
 * 	}
 * 
 * };
 * 
 * queue.add(command1, new CommandResultHandler&lt;R&gt;() {
 *   // Result handler implementation...
 * }
 * .add(command2, new CommandResultHandler&lt;R&gt;() {
 *   // Result handler implementation...
 * }
 * .add(command3, new CommandResultHandler&lt;R&gt;() {
 *   // Result handler implementation...
 * };
 * 
 * queue.start();
 * </pre>
 * 
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DispatchQueue {

	/**
	 * Inner private class wrapping a dispatch command execution.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 * @param <C>
	 *          The dispatch command type.
	 * @param <R>
	 *          The dispatch command result type.
	 */
	private final class CommandWrapper<C extends Command<R>, R extends Result> {

		private final int order;
		private final C command;
		private final CommandResultHandler<R> commandResultHandler;
		private final Collection<Loadable> loadables;

		/**
		 * Has already returned to the client-side and is waiting to be processed.
		 */
		private boolean ready;

		// Result data (success or failure).
		private R result;
		private Throwable caught;

		private CommandWrapper(final int order, final C command, final CommandResultHandler<R> commandResultHandler, final Collection<Loadable> loadables) {
			this.order = order;
			this.command = command;
			this.commandResultHandler = commandResultHandler;
			this.loadables = loadables;
		}

		/**
		 * Executes the current wrapped action.
		 */
		private void execute() {

			if (dispatch == null) {
				handleExecutionComplete();
				return;
			}

			dispatch.execute(command, new CommandResultHandler<R>() {

				@Override
				protected void onCommandSuccess(final R result) {

					CommandWrapper.this.result = result;
					ready = true;

					if (!preserveClientOrder || nextOrder.equals(order)) {
						handleSuccessResult();
					}
				}

				@Override
				protected void onCommandFailure(final Throwable caught) {

					CommandWrapper.this.caught = caught;
					ready = true;

					if (!preserveClientOrder || nextOrder.equals(order)) {
						handleFailureResult();
					}
				}

				@Override
				protected void onCommandViolation(final ValidationException caught) {

					CommandWrapper.this.caught = caught;
					ready = true;

					if (!preserveClientOrder || nextOrder.equals(order)) {
						handleViolationResult();
					}
				}

				@Override
				protected void onFunctionalException(final FunctionalException caught) {

					CommandWrapper.this.caught = caught;
					ready = true;

					if (!preserveClientOrder || nextOrder.equals(order)) {
						handleFunctionalException();
					}
				}

			}, loadables);
		}

		// Result type "1".
		private void handleSuccessResult() {
			try {
				commandResultHandler.onCommandSuccess(result);
			} finally {
				handleExecutionComplete();
				handleNextOrder(1);
			}
		}

		// Result type "2".
		private void handleFailureResult() {
			try {
				commandResultHandler.onCommandFailure(caught);
			} finally {
				handleExecutionComplete();
				handleNextOrder(2);
			}
		}

		// Result type "3".
		private void handleViolationResult() {
			try {
				commandResultHandler.onCommandViolation((ValidationException) caught);
			} finally {
				handleExecutionComplete();
				handleNextOrder(3);
			}
		}

		// Result type "4".
		private void handleFunctionalException() {
			try {
				commandResultHandler.onFunctionalException((FunctionalException) caught);
			} finally {
				handleExecutionComplete();
				handleNextOrder(4);
			}
		}

		private void handleNextOrder(final int resultType) {

			if (!preserveClientOrder || nextOrder == null) {
				return;
			}

			nextOrder++;

			if (nextOrder < commands.size() && commands.get(nextOrder).ready) {

				switch (resultType) {
					case 1:
						commands.get(nextOrder).handleSuccessResult();
						break;

					case 2:
						commands.get(nextOrder).handleFailureResult();
						break;

					case 3:
						commands.get(nextOrder).handleViolationResult();
						break;

					case 4:
						commands.get(nextOrder).handleFunctionalException();
						break;

					default:
						break;
				}
			}
		}
	}

	private final DispatchAsync dispatch;
	private final ArrayList<CommandWrapper<?, ?>> commands;

	private int commandsCount;
	private boolean running;

	private final boolean preserveClientOrder;
	private Integer nextOrder;

	/**
	 * Initializes a new {@code DispatchQueue}.
	 * 
	 * @param dispatch
	 *          The dispatch service (required).
	 */
	public DispatchQueue(final DispatchAsync dispatch) {
		this(dispatch, false);
	}

	/**
	 * Initializes a new {@code DispatchQueue}.
	 * 
	 * @param dispatch
	 *          The dispatch service (required).
	 * @param preserveClientOrder
	 *          Set to {@code true} to preserve <b>client</b> callback execution order (Note that <b>server</b> command
	 *          execution order cannot be guaranteed).<br>
	 *          Set to {@code false} to ignore order.
	 */
	public DispatchQueue(final DispatchAsync dispatch, final boolean preserveClientOrder) {
		this.dispatch = dispatch;
		this.commands = new ArrayList<CommandWrapper<?, ?>>(); // Should be ordered.
		this.preserveClientOrder = preserveClientOrder;
	}

	/**
	 * Adds a new {@code command} with its {@code commandResultHandler} to the current queue.
	 * 
	 * @param command
	 *          The dispatch command.
	 * @param commandResultHandler
	 *          The dispatch command result handler.
	 * @param loadables
	 *          (optional) The {@code Loadable} elements (may be {@code null}).
	 * @return the current queue.
	 */
	public final <C extends Command<R>, R extends Result> DispatchQueue add(final C command, final CommandResultHandler<R> commandResultHandler,
			final Loadable... loadables) {

		return add(command, commandResultHandler, ClientUtils.isEmpty(loadables) ? null : Arrays.asList(loadables));
	}

	/**
	 * Adds a new {@code command} with its {@code commandResultHandler} to the current queue.
	 * 
	 * @param command
	 *          The dispatch command.
	 * @param commandResultHandler
	 *          The dispatch command result handler.
	 * @param loadables
	 *          (optional) The {@code Loadable} elements collection (may be {@code null}).
	 * @return the current queue.
	 */
	public final <C extends Command<R>, R extends Result> DispatchQueue add(final C command, final CommandResultHandler<R> commandResultHandler,
			final Collection<Loadable> loadables) {

		if (command == null || commandResultHandler == null || running) {
			return this;
		}

		commands.add(new CommandWrapper<C, R>(commands.size(), command, commandResultHandler, loadables));
		return this;
	}

	/**
	 * Starts the queue commands (no new command should be added after this call).
	 */
	public final void start() {

		if (running) {
			return;
		}

		running = true;
		commandsCount = commands.size();

		if (commandsCount == 0) {
			commandsCount++;
			handleExecutionComplete();
			return;
		}

		nextOrder = commands.get(0).order;

		for (final CommandWrapper<?, ?> commandResultHandler : commands) {
			commandResultHandler.execute();
		}

		return;
	}

	/**
	 * Handled command execution complete.
	 */
	private final void handleExecutionComplete() {
		commandsCount--;

		if (commandsCount != 0) {
			return;
		}

		// "try/finally" in case custom implementation fails.
		try {

			onComplete();

		} finally {
			running = false;
			nextOrder = null;
		}
	}

	/**
	 * Method executed once <b>all</b> executed commands are complete (success or error).<br/>
	 * <em>Can be overridden by custom implementation (default implementation does nothing).</em>
	 */
	protected void onComplete() {
		// Default implementation does nothing.
	}

}
