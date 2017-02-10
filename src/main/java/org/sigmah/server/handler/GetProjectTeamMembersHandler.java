package org.sigmah.server.handler;

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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.sigmah.server.dao.ProfileDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectTeamMembers;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

public class GetProjectTeamMembersHandler extends AbstractCommandHandler<GetProjectTeamMembers, TeamMembersResult> {
	private final UserDAO userDAO;
	private final ProfileDAO profileDAO;

	@Inject
	GetProjectTeamMembersHandler(UserDAO userDAO, ProfileDAO profileDAO) {
		this.userDAO = userDAO;
		this.profileDAO = profileDAO;
	}

	@Override
	protected TeamMembersResult execute(GetProjectTeamMembers command, UserDispatch.UserExecutionContext context) throws CommandException {
		User manager = userDAO.getProjectManager(command.getProjectId());
		UserDTO managerDTO = mapper().map(manager, UserDTO.class, UserDTO.Mode.WITH_BASE_PROFILES);
		if (managerDTO != null) {
			managerDTO.generateCompleteName();
		}

		List<Profile> profiles = profileDAO.getProjectTeamMemberProfiles(command.getProjectId());
		List<ProfileDTO> profileDTOs = new ArrayList<>();
		for (Profile profile : profiles) {
			ProfileDTO profileDTO = mapper().map(profile, ProfileDTO.class, ProfileDTO.Mode.BASE);
			profileDTOs.add(profileDTO);
		}

		List<User> users = userDAO.getProjectTeamMembers(command.getProjectId());
		List<UserDTO> userDTOs = new ArrayList<>();
		for (User user : users) {
			UserDTO userDTO = mapper().map(user, UserDTO.class, UserDTO.Mode.WITH_BASE_PROFILES);
			userDTO.generateCompleteName();
			userDTOs.add(userDTO);
		}

		TeamMembersResult teamMembersResult = new TeamMembersResult();
		teamMembersResult.setProjectId(command.getProjectId());
		teamMembersResult.setProjectManager(managerDTO);
		teamMembersResult.setTeamMemberProfiles(profileDTOs);
		teamMembersResult.setTeamMembers(userDTOs);
		return teamMembersResult;
	}
}
