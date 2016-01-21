package org.sigmah.client.util.profiler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 * Active strategy, record executions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ActiveProfilerStrategy implements ProfilerStrategy {
	
	private final Map<Scenario, Execution> executions = new EnumMap<Scenario, Execution>(Scenario.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startScenario(Scenario scenario) {
		Log.debug("Started recording of scenario " + scenario + "...");
		executions.put(scenario, Execution.create(scenario));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseScenario(Scenario scenario) {
		final Execution execution = executions.get(scenario);
		if (execution != null) {
			execution.setDuration(execution.getDuration() + durationOfExecution(execution));
			execution.setDate(null);
			Log.debug("Paused execution of " + scenario + ".");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resumeScenario(Scenario scenario) {
		final Execution execution = executions.get(scenario);
		if (execution != null) {
			if (execution.getDate() != null) {
				Log.warn("Can't resume execution of " + scenario + " because it is already running.");
				return;
			}
			execution.setDate(new Date());
			Log.debug("Resumed execution of " + scenario + "...");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Execution endScenario(Scenario scenario) {
		final Execution execution = executions.remove(scenario);
		if (execution != null) {
			final double duration = durationOfExecution(execution);
			Log.debug(scenario + " duration was " + duration + "s.");
			
			execution.setDuration(duration);
			
			// Diff time between checkpoints.
			double lastCheckpointTime = 0.0;
			final JsArray<Checkpoint> checkpoints = execution.getCheckpoints();
			for (int index = 0; index < checkpoints.length(); index++) {
				final Checkpoint checkpoint = checkpoints.get(index);
				checkpoint.setDuration(checkpoint.getTime() - lastCheckpointTime);
				lastCheckpointTime = checkpoint.getTime();
			}
		}
		return execution;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markCheckpoint(Scenario scenario, String checkpoint) {
		final Execution execution = executions.get(scenario);
		if (execution != null && execution.getDate() != null) {
			execution.addCheckpoint(checkpoint, durationOfExecution(execution));
		} else {
			Log.warn("Can't mark checkpoint for scenario " + scenario + " because it is paused or not started.");
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
	
}
