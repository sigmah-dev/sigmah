package org.sigmah.server.handler;

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
