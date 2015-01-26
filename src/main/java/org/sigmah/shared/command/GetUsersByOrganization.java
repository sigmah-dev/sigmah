package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;

/**
 * GetUsersByOrganization command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetUsersByOrganization extends AbstractCommand<ListResult<UserDTO>> {

	private int organizationId;
	private Integer userId;
	private UserDTO.Mode mappingMode;

	protected GetUsersByOrganization() {
		// Serialization.
	}

	public GetUsersByOrganization(int organizationId, UserDTO.Mode mappingMode) {
		this(organizationId, null, mappingMode);
	}

	public GetUsersByOrganization(int organizationId, Integer userId, UserDTO.Mode mappingMode) {
		this.organizationId = organizationId;
		this.userId = userId;
		this.mappingMode = mappingMode;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public Integer getUserId() {
		return userId;
	}

	public UserDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
