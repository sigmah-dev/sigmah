package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;

import java.util.Collections;
import java.util.Set;

public class GetUsersByOrgUnit extends AbstractCommand<ListResult<UserDTO>> {

	private Integer orgUnitId;
	private Set<Integer> withoutUserIds;

	protected GetUsersByOrgUnit() {
		// Serialization.
	}

	public GetUsersByOrgUnit(Integer orgUnitId) {
		this(orgUnitId, Collections.<Integer>emptySet());
	}

	public GetUsersByOrgUnit(Integer orgUnitId, Set<Integer> withoutUserIds) {
		this.orgUnitId = orgUnitId;
		this.withoutUserIds = withoutUserIds;
	}

	public Integer getOrgUnitId() {
		return orgUnitId;
	}

	public Set<Integer> getWithoutUserIds() {
		return withoutUserIds;
	}
}
