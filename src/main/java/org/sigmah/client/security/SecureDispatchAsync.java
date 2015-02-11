package org.sigmah.client.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sigmah.client.dispatch.AbstractDispatchAsync;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.PageManager;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;
import org.sigmah.shared.command.Synchronize;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.status.ApplicationStateManager;

/**
 * This class is the default implementation of {@link DispatchAsync}, which is essentially the client-side access to the
 * {@link org.sigmah.server.dispatch.Dispatch} class on the server-side.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SecureDispatchAsync extends AbstractDispatchAsync {

	/**
	 * <p>
	 * Command execution wrapper containing following properties:
	 * <ul>
	 * <li>Authentication token (used to retrieve corresponding user).</li>
	 * <li>Command to execute.</li>
	 * <li>Current page token.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Implements {@link Command} to ensure {@code IsSerializable} implementation.
	 * </p>
	 * <p>
	 * Note: for consistency, this class is supposed to be located into {@code shared} package. However, in order to
	 * conserve <em>local</em> {@code private} constructor, it remains in client package.
	 * </p>
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 * @param <C>
	 *          The command type.
	 * @param <R>
	 *          The command result type.
	 */
	public static final class CommandExecution<C extends Command<R>, R extends Result> implements Command<R> {

		private String authToken;
		private C command;
		private String currentPageToken;

		/**
		 * Serialization constructor.
		 */
		private CommandExecution() {
			// Serialization.
		}

		/**
		 * Initialiazes a new {@code CommandExecution}.
		 * 
		 * @param authToken
		 *          The authentication token.
		 * @param command
		 *          The command to execute.
		 * @param currentPageToken
		 *          The current page token.
		 */
		private CommandExecution(final String authToken, final C command, final String currentPageToken) {
			this.authToken = authToken;
			this.command = command;
			this.currentPageToken = currentPageToken;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			final ToStringBuilder builder = new ToStringBuilder(this);
			builder.append("authToken", authToken);
			builder.append("command", command);
			builder.append("currentPageToken", currentPageToken);
			return builder.toString();
		}

		public String getAuthenticationToken() {
			return authToken;
		}

		public C getCommand() {
			return command;
		}

		public String getCurrentPageToken() {
			return currentPageToken;
		}

	}

	/**
	 * The RPC dispatch <em>real</em> service implementation.
	 */
	private static final SecureDispatchServiceAsync realService = GWT.create(SecureDispatchService.class);

	/**
	 * The authentication provider.
	 */
	private final AuthenticationProvider authenticationProvider;

	/**
	 * The application event bus.
	 */
	private final EventBus eventBus;

	/**
	 * The application page manager.
	 */
	private final PageManager pageManager;

	/**
	 * Listeners related to commands. Called when a command finishes its execution successfully.
	 */
	private final Map<Class<?>, List<DispatchListener<?, ?>>> listeners;

	/**
	 * Implementation of the RPC dispatch service used when the user is offline.
	 */
	private LocalDispatchServiceAsync offlineService;
	
	/**
	 * Connection state. Used to decide which service to call when dispatching
	 * a command.
	 */
	private boolean online;


	/**
	 * Initializes the {@code SecureDispatchAsync} with injected arguments.
	 * 
	 * @param authenticationProvider
	 *          The {@link AuthenticationProvider} instance.
	 * @param eventBus
	 *          The {@link EventBus} implementation.
	 * @param pageManager
	 *          The {@link PageManager} implementation.
	 * @param applicationStateManager
	 *			he {@link ApplicationStateManager} implementation.
	 */
	@Inject
	public SecureDispatchAsync(final AuthenticationProvider authenticationProvider, final EventBus eventBus, final PageManager pageManager, final ApplicationStateManager applicationStateManager) {
		this.authenticationProvider = authenticationProvider;
		this.eventBus = eventBus;
		this.pageManager = pageManager;
		this.listeners = new HashMap<Class<?>, List<DispatchListener<?, ?>>>();
		
		registerEventHandlers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Command<R>, R extends Result> void execute(final C command, final AsyncCallback<R> callback, final Loadable... loadables) {
		execute(command, callback, loadables != null ? Arrays.asList(loadables) : null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Command<R>, R extends Result> void execute(final C command, final AsyncCallback<R> callback, final Collection<Loadable> loadables) {

		// Sets loadable elements in "loading" state.
		final long startTime = new Date().getTime();
		setLoadableElementsEnabled(startTime, loadables, true);

		// Retrieving auth token.
		final String authToken = authenticationProvider.get().getAuthenticationToken();

		// Command execution.
		getDispatchService(command).execute(new CommandExecution<C, R>(authToken, command, pageManager.getCurrentPageToken()), new AsyncCallback<Result>() {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(Result result) {
				try {

					// Note: This cast is a dodgy hack to get around a GWT 1.6 async compiler issue
					SecureDispatchAsync.this.onSuccess(command, (R) result, callback);
					fireSuccess(command, (R) result);

				} finally {
					// Sets loadable elements in "normal" state.
					setLoadableElementsEnabled(startTime, loadables, false);
				}
			}

			@Override
			public void onFailure(final Throwable caught) {
				try {

					SecureDispatchAsync.this.onFailure(command, caught, callback, loadables);

				} finally {
					// Sets loadable elements in "normal" state.
					setLoadableElementsEnabled(startTime, loadables, false);
				}
			}
		});
	}

	public <C extends Command<R>, R extends Result> void registerListener(Class<C> commandClass, DispatchListener<C, R> listener) {
		List<DispatchListener<?, ?>> commandListeners = listeners.get(commandClass);
		if (commandListeners == null) {
			commandListeners = new ArrayList<DispatchListener<?, ?>>();
			listeners.put(commandClass, commandListeners);
		}
		commandListeners.add(listener);
	}

	protected <C extends Command<R>, R extends Result> void fireSuccess(C command, R result) {
		if (online) {
			final List<DispatchListener<?, ?>> commandListeners = listeners.get(command.getClass());
			if (commandListeners != null) {
				for (final DispatchListener<?, ?> listener : commandListeners) {
					@SuppressWarnings("unchecked")
					final DispatchListener<C, R> cast = (DispatchListener<C, R>) listener;
					cast.onSuccess(command, result, authenticationProvider.get());
				}
			}
		}
	}

	public void setOfflineService(LocalDispatchServiceAsync offlineService) {
		this.offlineService = offlineService;
	}

	// --------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Running commands start timestamp with their corresponding {@code Loadable} element(s).
	 */
	private static final Map<Long, Collection<Loadable>> commandsMap = new HashMap<Long, Collection<Loadable>>();

	/**
	 * Running actions start timestamp with their corresponding {@code Loadable} element(s).
	 */
	private static final Map<Loadable, Integer> loadablesMap = new HashMap<Loadable, Integer>();

	/**
	 * One loadable count.
	 */
	private static final Integer ONE = new Integer(1);

	/**
	 * Command execution with no response timeout limit (in milliseconds).<br/>
	 * <em>Currently set to <b>5 minutes</b>.</em>
	 */
	private static final Long TIMEOUT = new Long(5 * 60 * 1000);

	/**
	 * Sets the given {@code loadables} elements with the given {@code loading} state.
	 * 
	 * @param loadables
	 *          The {@link Loadable} elements.
	 * @param loading
	 *          {@code true} to set elements in loading mode.
	 */
	private void setLoadableElementsEnabled(final Long startTime, final Collection<Loadable> loadables, final boolean loading) {

		if (Log.isTraceEnabled()) {
			Log.trace("Loading state: " + loading + " ; Running actions: " + commandsMap.size());
		}

		// Registering command and Updating loadables elements states (if any).
		if (loading) {
			commandsMap.put(startTime, loadables);

			if (ClientUtils.isNotEmpty(loadables)) {
				// Updating loadable elements state.
				for (final Loadable loadable : loadables) {
					if (loadable == null) {
						continue;
					}
					final Integer count = loadablesMap.get(loadable);
					loadablesMap.put(loadable, count == null ? ONE : count + 1);
					loadable.setLoading(true);
				}
			}

		} else {
			disableLoadingState(loadables);
			commandsMap.remove(startTime);

			// Clearing commands that have exceeded timeout limit.
			final Set<Long> commandsToRemove = new HashSet<Long>();
			final Long now = new Date().getTime();
			for (final Entry<Long, Collection<Loadable>> entry : commandsMap.entrySet()) {
				if (TIMEOUT.compareTo(now - entry.getKey()) <= 0) {
					commandsToRemove.add(entry.getKey());
				}
			}
			for (final Long commandToRemove : commandsToRemove) {
				disableLoadingState(commandsMap.get(commandToRemove));
				commandsMap.remove(commandToRemove);
			}
		}

		// Updating application loader state.
		if (eventBus != null) {
			eventBus.updateZoneRequest(Zone.APP_LOADER.requestWith(RequestParameter.CONTENT, ClientUtils.isNotEmpty(commandsMap)));
		}
	}

	/**
	 * Disables the {@code loading} state of the given {@code loadables} elements.
	 * 
	 * @param loadables
	 *          The loadables elements (does nothing if {@code null} or empty).
	 */
	private static void disableLoadingState(final Collection<Loadable> loadables) {

		if (ClientUtils.isEmpty(loadables)) {
			return;
		}

		for (final Loadable loadable : loadables) {
			if (loadable == null) {
				continue;
			}
			final Integer count = loadablesMap.get(loadable);
			if (count == null || ONE.equals(count)) {
				// Disabling 'loading' state ONLY IF no more command references this loadable.
				loadablesMap.remove(loadable);
				loadable.setLoading(false);

			} else {
				loadablesMap.put(loadable, count - 1);
			}
		}
	}
	
	/**
	 * Begin to listen to events from the event bus.
	 */
	private void registerEventHandlers() {
		eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {

			@Override
			public void handleEvent(OfflineEvent event) {
				online = event.getState() == ApplicationState.ONLINE;
			}
		});
	}

	private <C extends Command<R>, R extends Result> SecureDispatchServiceAsync getDispatchService(C command) {
		if (online || command instanceof Synchronize) {
			return realService;
		} else {
			return offlineService;
		}
	}
}
