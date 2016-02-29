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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
public class ProbesReportDetails {
	/**
	 * Start time
	 */
	private Date startTime;
	/**
	 * End time.
	 */
	private Date endTime;
	/**
	 * user agent name.
	 */
	private String userAgent;
	/**
	 * simah version
	 */
	private String  versionNumber;
	/**
	 * List of scenarions details.
	 */
	private List<ScenarioDetailsDTO> senarios=new ArrayList<ScenarioDetailsDTO>();

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

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	

	public List<ScenarioDetailsDTO> getSenarios() {
		return senarios;
	}

	public void setSenarios(List<ScenarioDetailsDTO> senarios) {
		this.senarios = senarios;
	}
	
}
