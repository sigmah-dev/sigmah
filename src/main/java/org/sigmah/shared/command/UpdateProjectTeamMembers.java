package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

import java.util.List;

public class UpdateProjectTeamMembers extends AbstractCommand<TeamMembersResult> {
	private Integer projectId;
	private List<UserDTO> teamMembers;
	private List<ProfileDTO> teamMemberProfiles;

	/**
	 * Serialization
	 */
	public UpdateProjectTeamMembers() {
	}

	public UpdateProjectTeamMembers(Integer projectId, List<UserDTO> teamMembers, List<ProfileDTO> teamMemberProfiles) {
		this.projectId = projectId;
		this.teamMembers = teamMembers;
		this.teamMemberProfiles = teamMemberProfiles;
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

	public List<ProfileDTO> getTeamMemberProfiles() {
		return teamMemberProfiles;
	}

	public void setTeamMemberProfiles(List<ProfileDTO> teamMemberProfiles) {
		this.teamMemberProfiles = teamMemberProfiles;
	}
}
