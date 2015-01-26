package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.OrgUnitDTOLight;

/**
 * Result for command {@link GetOrgUnitsByModel}
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 */
public class OrgUnitListResult implements CommandResult {

	private static final long serialVersionUID = 891937902307950870L;
	private List<OrgUnitDTOLight> orgUnitLightlist;

	public OrgUnitListResult() {

	}

	public List<OrgUnitDTOLight> getOrgUnitDTOLightList() {
		return orgUnitLightlist;
	}

	public void setOrgUnitDTOLightList(List<OrgUnitDTOLight> list) {
		this.orgUnitLightlist = list;
	}

}
