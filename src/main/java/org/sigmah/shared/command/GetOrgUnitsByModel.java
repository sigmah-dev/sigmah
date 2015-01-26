package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * GetOrgUnitsByModel command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetOrgUnitsByModel extends AbstractCommand<ListResult<OrgUnitDTO>> {

	private Integer orgUnitModelId;
	private OrgUnitDTO.Mode mappingMode;

	protected GetOrgUnitsByModel() {
		// Serialization.
	}

	public GetOrgUnitsByModel(Integer orgUnitModelId, OrgUnitDTO.Mode mappingMode) {
		this.orgUnitModelId = orgUnitModelId;
		this.mappingMode = mappingMode;
	}

	/**
	 * @return the orgUnitModelId
	 */
	public Integer getOrgUnitModelId() {
		return orgUnitModelId;
	}

	public OrgUnitDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
