package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * Retrieves an org unit with the given id.
 * 
 * @author Tom Miette (tmiette@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetOrgUnit extends AbstractCommand<OrgUnitDTO> {

	/**
	 * Org unit id.
	 */
	private Integer id;

	/**
	 * Mapping mode.
	 */
	private OrgUnitDTO.Mode mode;

	protected GetOrgUnit() {
		// Serialization.
	}

	public GetOrgUnit(final Integer orgUnitId, final OrgUnitDTO.Mode mode) {
		this.id = orgUnitId;
		this.mode = mode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("orgUnitId", id);
		builder.append("mappingMode", mode);
	}

	public int getId() {
		return id;
	}

	public OrgUnitDTO.Mode getMode() {
		return mode;
	}

}
