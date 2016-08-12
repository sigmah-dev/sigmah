package org.sigmah.shared.command.result;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
