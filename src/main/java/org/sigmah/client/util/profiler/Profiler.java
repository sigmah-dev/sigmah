package org.sigmah.client.util.profiler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.offline.event.JavaScriptEvent;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.Database;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.status.ApplicationStateManager;
import org.sigmah.offline.sync.SuccessCallback;

/**
 * JavaScript profiler. Measure performances of the application.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class Profiler {
	
	/**
	 * Shared instance.
	 */
	public static final Profiler INSTANCE = new Profiler();
	
	private static final String DATABASE_NAME = "profiler";
	
	private static final String[] UNITS = {"ms", "s", "min", "h", "d"};
	private static final long[] UNIT_VALUES = {1000, 60, 60, 24};
			
	private final Map<Scenario, Execution> executions = new EnumMap<Scenario, Execution>(Scenario.class);
	private Database<ProfilerStore> database;
	
	private AuthenticationProvider authenticationProvider;
	private ApplicationStateManager applicationStateManage;
	
	public Profiler() {
		if (IndexedDB.isSupported()) {
			final IndexedDB indexedDB = new IndexedDB();
			final NativeOpenDatabaseRequest<ProfilerStore> request = indexedDB.open(DATABASE_NAME, ProfilerStore.class);
			
			request.addSuccessHandler(new JavaScriptEvent() {

				@Override
				public void onEvent(JavaScriptObject event) {
					Profiler.this.database = request.getResult();
				}
				
			});
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
	 * Start a new execution for the given scenario.
	 * 
	 * @param scenario Scenario to start.
	 * @return Identifier of the new execution.
	 */
	public String startScenario(Scenario scenario) {
		final Execution execution = Execution.create(scenario);
		final String identifier = identifierForExecution(execution);
		Log.debug("Started recording of execution " + identifier + "...");
		executions.put(scenario, execution);
		return identifier;
	}
	
	/**
	 * Pause the clock for the given scenario.
	 * 
	 * @param scenario Scenario to pause.
	 */
	public void pauseScenario(Scenario scenario) {
		final Execution execution = executions.get(scenario);
		if (execution != null) {
			execution.setDuration(execution.getDuration() + durationOfExecution(execution));
			execution.setDate(null);
			Log.debug("Paused execution " + identifierForExecution(execution) + ".");
		}
	}
	
	/**
	 * Resume the clock for the given scenario.
	 * 
	 * @param scenario Scenario to resume.
	 */
	public void resumeScenario(Scenario scenario) {
		final Execution execution = executions.get(scenario);
		if (execution != null) {
			if (execution.getDate() != null) {
				Log.warn("Can't resume execution of " + identifierForExecution(execution) + " because it is already running.");
				return;
			}
			execution.setDate(new Date());
			Log.debug("Resumed execution " + identifierForExecution(execution) + "...");
		}
	}
	
	/**
	 * Save the split time from the previous checkpoint to this one.
	 * 
	 * @param scenario Scenario to mark.
	 * @param checkpoint Checkpoint name.
	 */
	public void markCheckpoint(Scenario scenario, String checkpoint) {
		final Execution execution = executions.get(scenario);
		if (execution != null && execution.getDate() != null) {
			execution.addCheckpoint(checkpoint, durationOfExecution(execution));
		} else {
			Log.warn("Can't mark checkpoint for scenario " + scenario + " because it is paused or not started.");
		}
	}
	
	/**
	 * End the execution for the given scenario.
	 * 
	 * @param scenario Scenario to end.
	 */
	public void endScenario(Scenario scenario) {
		final Execution execution = executions.remove(scenario);
		if (execution != null) {
			final double duration = durationOfExecution(execution);
			Log.debug(identifierForExecution(execution) + " duration was " + formatDuration((long) (duration * 1000.0)));
			
			execution.setDuration(duration);
			execution.setUserEmailAddress(authenticationProvider.get().getUserEmail());
			execution.setOnline(applicationStateManage.getLastState() == ApplicationState.ONLINE);
			
			double lastCheckpointTime = 0.0;
			final JsArray<Checkpoint> checkpoints = execution.getCheckpoints();
			for (int index = 0; index < checkpoints.length(); index++) {
				final Checkpoint checkpoint = checkpoints.get(index);
				checkpoint.setDuration(checkpoint.getTime() - lastCheckpointTime);
				lastCheckpointTime = checkpoint.getTime();
			}
			
			if (database != null) {
				final Transaction<ProfilerStore> transaction = database.getTransaction(Transaction.Mode.READ_WRITE, Collections.singleton(ProfilerStore.EXECUTION));
				final ObjectStore objectStore = transaction.getObjectStore(ProfilerStore.EXECUTION);
				objectStore.add(execution).addCallback(new SuccessCallback<Request>() {

					@Override
					public void onSuccess(Request result) {
						// Rien
					}
				});
			}
		} 
	}
	
	/**
	 * Generates a CSV file from the collected data.
	 * 
	 * @param callback Called when the generation is done.
	 */
	public void generateCSV(final AsyncCallback<String> callback) {
		if (database != null) {
			final Transaction<ProfilerStore> transaction = database.getTransaction(Transaction.Mode.READ_ONLY, Collections.singleton(ProfilerStore.EXECUTION));
			final ObjectStore objectStore = transaction.getObjectStore(ProfilerStore.EXECUTION);
			
			objectStore.openCursor().addCallback(new SuccessCallback<Request>() {
				
				private final StringBuilder csvBuilder = new StringBuilder("scenario;duration;date;version;user-agent\n");
				private final DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_FULL);

				@Override
				public void onSuccess(Request result) {
					final Cursor cursor = result.getResult();
					if(cursor != null) {
						final Execution execution = cursor.getValue();
						csvBuilder.append(execution.getScenarioName()).append(';')
								.append(execution.getDuration()).append(';')
								.append(formatter.format(execution.getDate())).append(';')
								.append(execution.getVersionNumber()).append(";\"")
								.append(execution.getUserAgent()).append("\"\n");
						
						cursor.next();
					} else {
						callback.onSuccess(csvBuilder.toString());
					}
				}
			});
		} else {
			callback.onSuccess("IndexedDB database is not opened.");
		}
	}

	/**
	 * Generates an identifier for the given execution.
	 * 
	 * @param execution Execution to use.
	 * @return An identifier.
	 */
	private String identifierForExecution(final Execution execution) {
		final Date date = execution.getDate();
		if (date != null) {
			return execution.getScenarioName() + '-' + date.getTime();
		} else {
			return execution.getScenarioName() + "-PAUSE";
		}
	}
	
	/**
	 * Calculates the duration of the given execution.
	 * 
	 * @param execution Execution to measure.
	 * @return Duration of the given execution.
	 */
	private double durationOfExecution(final Execution execution) {
		double duration = execution.getDuration();
		
		final Date date = execution.getDate();
		if (date != null) {
			duration += (new Date().getTime() - date.getTime()) / 1000.0;
		}
		
		return duration;
	}
	
	/**
	 * Format the current duration.
	 * 
	 * @param duration Duration to format.
	 * @return The duration formatted.
	 */
	private String formatDuration(long duration) {
		final StringBuilder durationBuilder = new StringBuilder();
		
		long remaining = duration;
		for(int index = 0; remaining > 0 && index < UNIT_VALUES.length; index++) {
			final long value = UNIT_VALUES[index];
			
			durationBuilder.insert(0, UNITS[index])
					.insert(0, remaining % value)
					.insert(0, ' ');
			
			remaining /= value;
		}
		
		if (durationBuilder.length() == 0) {
			durationBuilder.append(0).append(' ').append(UNITS[0]);
		}
		
		return durationBuilder.toString();
	}
	
	// ---
	// GETTERS & SETTERS
	// ---
	
	public Database<ProfilerStore> getDatabase() {
		return database;
	}

	public ApplicationStateManager getApplicationStateManage() {
		return applicationStateManage;
	}

	public void setApplicationStateManage(ApplicationStateManager applicationStateManage) {
		this.applicationStateManage = applicationStateManage;
	}

	public AuthenticationProvider getAuthenticationProvider() {
		return authenticationProvider;
	}

	public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
}
