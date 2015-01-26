package org.sigmah.shared.command;

import org.sigmah.shared.command.result.OrgUnitListResult;

public class GetOrgUnitsByModel implements Command<OrgUnitListResult> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8956715452312610144L;
	private Integer orgUnitModelId;

	public GetOrgUnitsByModel() {

	}

	public GetOrgUnitsByModel(Integer orgUnitModelId) {
		this.setOrgUnitModelId(orgUnitModelId);
	}

	/**
	 * @return the orgUnitModelId
	 */
	public Integer getOrgUnitModelId() {
		return orgUnitModelId;
	}

	/**
	 * @param orgUnitModelId
	 *            the orgUnitModelId to set
	 */
	public void setOrgUnitModelId(Integer orgUnitModelId) {
		this.orgUnitModelId = orgUnitModelId;
	}

}
