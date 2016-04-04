package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.UserUnitsResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * Retrieves org units for the given user id.
 */
public class GetUserUnitsByUser extends AbstractCommand<UserUnitsResult> {

	private Integer userId;

	/**
	 * Mapping mode.
	 */
	private OrgUnitDTO.Mode mode;

	protected GetUserUnitsByUser() {
		// Serialization.
	}

	public GetUserUnitsByUser(final Integer userId, final OrgUnitDTO.Mode mode) {
		this.userId = userId;
		this.mode = mode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("userId", userId);
		builder.append("mappingMode", mode);
	}

	public int getUserId() {
		return userId;
	}

	public OrgUnitDTO.Mode getMode() {
		return mode;
	}

}
