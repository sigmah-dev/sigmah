package org.sigmah.client.util.profiler;

/**
 * Inactive strategy, no action.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class InactiveProfilerStrategy implements ProfilerStrategy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startScenario(Scenario scenario) {
		// Nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseScenario(Scenario scenario) {
		// Nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resumeScenario(Scenario scenario) {
		// Nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Execution endScenario(Scenario scenario) {
		// Nothing.
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markCheckpoint(Scenario scenario, String checkpoint) {
		// Nothing.
	}
	
}
