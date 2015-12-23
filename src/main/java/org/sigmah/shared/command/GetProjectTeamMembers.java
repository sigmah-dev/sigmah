package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dto.UserDTO;

public class GetProjectTeamMembers extends AbstractCommand<TeamMembersResult> {
	private Integer projectId;

	public GetProjectTeamMembers() {
		// Serialization.
	}

	public GetProjectTeamMembers(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectId", projectId);
	}
}
