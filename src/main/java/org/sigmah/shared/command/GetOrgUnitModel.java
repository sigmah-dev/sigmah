package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.OrgUnitModelDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetOrgUnitModel extends AbstractCommand<OrgUnitModelDTO> {

	private Integer id;

	private OrgUnitModelDTO.Mode mappingMode;

	protected GetOrgUnitModel() {
		// Serialization.
	}

	public GetOrgUnitModel(Integer id, OrgUnitModelDTO.Mode mappingMode) {
		this.id = id;
		this.mappingMode = mappingMode;
	}

	public Integer getId() {
		return id;
	}

	public OrgUnitModelDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
