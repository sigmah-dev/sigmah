package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.UserPermissionDTO;

/**
 * UserResult.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.shared.command.GetUsers
 */
public class UserResult extends PagingResult<UserPermissionDTO> {

	public UserResult() {
		// Serialization.
	}

	public UserResult(List<UserPermissionDTO> data) {
		super(data);
	}

	public UserResult(List<UserPermissionDTO> data, int offset, int totalCount) {
		super(data, offset, totalCount);
	}

}
