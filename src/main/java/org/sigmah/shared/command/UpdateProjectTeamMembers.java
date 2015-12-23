package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dto.UserDTO;

import java.util.List;

public class UpdateProjectTeamMembers extends AbstractCommand<TeamMembersResult> {
	private Integer projectId;
	private List<UserDTO> teamMembers;

	/**
	 * Serialization
	 */
	public UpdateProjectTeamMembers() {
	}

	public UpdateProjectTeamMembers(Integer projectId, List<UserDTO> teamMembers) {
		this.projectId = projectId;
		this.teamMembers = teamMembers;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public List<UserDTO> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<UserDTO> teamMembers) {
		this.teamMembers = teamMembers;
	}
}
