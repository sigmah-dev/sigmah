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
