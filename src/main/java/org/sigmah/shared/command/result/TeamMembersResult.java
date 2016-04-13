package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

public class TeamMembersResult implements Result {
	private Integer projectId;
	private UserDTO projectManager;
	private List<UserDTO> teamMembers;
	private List<ProfileDTO> teamMemberProfiles;

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public UserDTO getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(UserDTO projectManager) {
		this.projectManager = projectManager;
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
