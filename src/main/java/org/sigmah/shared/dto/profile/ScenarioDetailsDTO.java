package org.sigmah.shared.dto.profile;

import java.util.Date;
import org.sigmah.client.util.profiler.Scenario;

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
 *
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
public class ScenarioDetailsDTO {
	/**
	 * Senario name.
	 */
	private Scenario scenario;
	/**
	 * Start time  scenario.
	 */
	private Date startTime;

	/**
	 * End time scenario.
	 */
	private Date endTime;
	/**
	 * Duration of fast execution.
	 */
	private double minDuartion;
	/**
	 * Duration of the lowest execution.
	 */
	private double maxDuration;
	/**
	 * Avrage of execution
	 */
	private double avrageDuration;

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public double getMinDuartion() {
		return minDuartion;
	}

	public void setMinDuartion(double minDuartion) {
		this.minDuartion = minDuartion;
	}

	public double getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(double maxDuration) {
		this.maxDuration = maxDuration;
	}

	public double getAvrageDuration() {
		return avrageDuration;
	}

	public void setAvrageDuration(double avrageDuration) {
		this.avrageDuration = avrageDuration;
	}
	 
	
	
}
