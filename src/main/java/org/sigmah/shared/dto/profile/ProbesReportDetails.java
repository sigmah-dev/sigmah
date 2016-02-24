package org.sigmah.shared.dto.profile;

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
