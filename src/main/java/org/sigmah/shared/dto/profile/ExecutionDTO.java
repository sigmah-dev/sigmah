package org.sigmah.shared.dto.profile;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.sigmah.client.util.profiler.Scenario;

/**
 * Class that represent one execution of scenrio details server side.
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
public class ExecutionDTO implements Serializable{
	/**
	 * Scenario 
	 */
	private Scenario scenario;
	/**
	 * date of Scenario
	 */
	private Date date;
	/**
	 * duration of Scenario
	 */
	private double duration;
	/**
	 * User email adresse.
	 */
	private String userEmailAddress;
	/**
	 * Sigmah version number
	 */
	private String versionNumber;
	/**
	 * Web browser agent
	 */
	private String userAgent;
	/**
	 * Mode of senario. online or off line
	 */
	private boolean onligne;
	/**
	 * Application status.
	 */
	private String applicationCacheStatus;
	/**
	 * List of checkpoints in the scenario.
	 */
	private List<CheckPointDTO> checkpoints=new ArrayList<CheckPointDTO>();

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public String getUserEmailAddress() {
		return userEmailAddress;
	}

	public void setUserEmailAddress(String userEmailAddress) {
		this.userEmailAddress = userEmailAddress;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public boolean isOnligne() {
		return onligne;
	}

	public void setOnligne(boolean onligne) {
		this.onligne = onligne;
	}

	public String getApplicationCacheStatus() {
		return applicationCacheStatus;
	}

	public void setApplicationCacheStatus(String applicationCacheStatus) {
		this.applicationCacheStatus = applicationCacheStatus;
	}

	public List<CheckPointDTO> getCheckpoints() {
		return checkpoints;
	}

	public void setCheckpoints(List<CheckPointDTO> checkpoints) {
		this.checkpoints = checkpoints;
	}
	
	
}
