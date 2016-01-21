package org.sigmah.client.util.profiler;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.offline.appcache.ApplicationCache;
import org.sigmah.offline.event.JavaScriptEvent;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.status.ApplicationStateManager;

/**
 * JavaScript profiler. Measure performances of the application.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class Profiler implements ProfilerStrategy {
	
	/**
	 * Shared instance.
	 */
	public static final Profiler INSTANCE = new Profiler();
	
	public static final String DATABASE_NAME = "profiler";
	
	/**
	 * Current strategy.
	 */
	private ProfilerStrategy strategy = new ActiveProfilerStrategy();
	
	private final ExecutionAsyncDAO executionAsyncDAO = new ExecutionAsyncDAO();
	private AuthenticationProvider authenticationProvider;
	private ApplicationStateManager applicationStateManager;
	
	/**
	 * Activate or deactivate profiling.
	 * 
	 * @param active <code>true</code> to active the profiler, 
	 * <code>false</code> to deactivate it.
	 */
	public void setActive(boolean active) {
		if (active) {
			strategy = new ActiveProfilerStrategy();
		} else {
			strategy = new InactiveProfilerStrategy();
		}
	}
	
	/**
	 * Removes the database used by the profiler.
	 */
	public void deleteDatabase() {
		final IndexedDB indexedDB = new IndexedDB();
		final NativeOpenDatabaseRequest<ProfilerStore> request = indexedDB.deleteDatabase(DATABASE_NAME);

		request.addSuccessHandler(new JavaScriptEvent() {

			@Override
			public void onEvent(JavaScriptObject event) {
				// Success.
			}

		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startScenario(Scenario scenario) {
		strategy.startScenario(scenario);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseScenario(Scenario scenario) {
		strategy.pauseScenario(scenario);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resumeScenario(Scenario scenario) {
		strategy.resumeScenario(scenario);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markCheckpoint(Scenario scenario, String checkpoint) {
		strategy.markCheckpoint(scenario, checkpoint);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Execution endScenario(Scenario scenario) {
		final Execution execution = strategy.endScenario(scenario);
		if (execution != null) {
			execution.setUserEmailAddress(authenticationProvider.get().getUserEmail());
			execution.setOnline(applicationStateManager.getLastState() == ApplicationState.ONLINE);
			executionAsyncDAO.saveOrUpdate(execution);
		}
		return execution;
	}
	
	/**
	 * Generates a CSV file from the collected data.
	 * 
	 * @param callback Called when the generation is done.
	 */
	public void generateCSV(final AsyncCallback<String> callback) {
		executionAsyncDAO.forEach(new AsyncCallback<Execution>() {
			
			private final StringBuilder csvBuilder = new StringBuilder("scenario;duration;date;version;user-agent\n");
			private final DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_FULL);

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Execution execution) {
				if (execution != null) {
					if (execution.isOnline() && ApplicationCache.Status.IDLE.name().equals(execution.getApplicationCacheStatus())) {
						csvBuilder.append(execution.getScenarioName()).append(';')
								.append(formatDoubleForCSV(execution.getDuration())).append(';')
								.append(formatter.format(execution.getDate())).append(';')
								.append(execution.getVersionNumber()).append(";\"")
								.append(execution.getUserAgent()).append('\"');

						for (final Checkpoint checkpoint : execution.getCheckpointSequence()) {
							csvBuilder.append(';').append(checkpoint.getName())
									.append(';').append(formatDoubleForCSV(checkpoint.getDuration()));
						}

						csvBuilder.append("\r\n");
					}
				} else {
					callback.onSuccess(csvBuilder.toString());
				}
			}
		});
	}
	
	public void generateMarkdownReport() {
		
	}
	
	/**
	 * Format the given double value to a String suitable for a CSV file.
	 * 
	 * @param value Value to format.
	 * @return The formatted value.
	 */
	private String formatDoubleForCSV(final double value) {
		return Double.toString(((int) (value * 1000)) / 1000.0).replace('.', ',');
	}

	// ---
	// GETTERS & SETTERS
	// ---
	
	public ApplicationStateManager getApplicationStateManager() {
		return applicationStateManager;
	}

	public void setApplicationStateManager(ApplicationStateManager applicationStateManager) {
		this.applicationStateManager = applicationStateManager;
	}

	public AuthenticationProvider getAuthenticationProvider() {
		return authenticationProvider;
	}

	public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
}
