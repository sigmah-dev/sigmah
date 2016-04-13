package org.sigmah.shared.command;

import java.util.Set;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * Retrieves org units with their ids.
 */
public class GetOrgUnits extends AbstractCommand<ListResult<OrgUnitDTO>> {
	private Set<Integer> orgUnitIds;

	/**
	 * Mapping mode.
	 */
	private OrgUnitDTO.Mode mode;

	protected GetOrgUnits() {
		// Serialization.
	}

	public GetOrgUnits(final Set<Integer> orgUnitIds, final OrgUnitDTO.Mode mode) {
		this.orgUnitIds = orgUnitIds;
		this.mode = mode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("orgUnitIds", orgUnitIds);
		builder.append("mappingMode", mode);
	}

	public Set<Integer> getOrgUnitIds() {
		return orgUnitIds;
	}

	public OrgUnitDTO.Mode getMode() {
		return mode;
	}

}
