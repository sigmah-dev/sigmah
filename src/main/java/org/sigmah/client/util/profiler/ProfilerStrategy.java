package org.sigmah.client.util.profiler;

/**
 * Strategy for the profiler.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface ProfilerStrategy {
	
	/**
	 * Start a new recording for the given scenario.
	 * 
	 * @param scenario Scenario to start.
	 */
	void startScenario(Scenario scenario);
	
	/**
	 * Pause the clock for the given scenario.
	 * 
	 * @param scenario Scenario to pause.
	 */
	void pauseScenario(Scenario scenario);
	
	/**
	 * Resume the clock for the given scenario.
	 * 
	 * @param scenario Scenario to resume.
	 */
	void resumeScenario(Scenario scenario);
	
	/**
	 * End the execution for the given scenario.
	 * 
	 * @param scenario Scenario to end.
	 * @return Execution result.
	 */
	Execution endScenario(Scenario scenario);
	
	/**
	 * Save the split time from the previous checkpoint to this one.
	 * 
	 * @param scenario Scenario to mark.
	 * @param checkpoint Checkpoint name.
	 */
	void markCheckpoint(Scenario scenario, String checkpoint);
	
}
