package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectTeamMembers;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;

public class GetProjectTeamMembersHandler extends AbstractCommandHandler<GetProjectTeamMembers, TeamMembersResult> {
	private final UserDAO userDAO;

	@Inject
	GetProjectTeamMembersHandler(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	protected TeamMembersResult execute(GetProjectTeamMembers command, UserDispatch.UserExecutionContext context) throws CommandException {
		User manager = userDAO.getProjectManager(command.getProjectId());
		UserDTO managerDTO = mapper().map(manager, UserDTO.class, UserDTO.Mode.WITH_BASE_PROFILES);
		managerDTO.generateCompleteName();
		List<User> users = userDAO.getProjectTeamMembers(command.getProjectId());
		List<UserDTO> userDTOs = new ArrayList<>();
		for (User user : users) {
			UserDTO userDTO = mapper().map(user, UserDTO.class, UserDTO.Mode.WITH_BASE_PROFILES);
			userDTO.generateCompleteName();
			userDTOs.add(userDTO);
		}

		TeamMembersResult teamMembersResult = new TeamMembersResult();
		teamMembersResult.setProjectManager(managerDTO);
		teamMembersResult.setTeamMembers(userDTOs);
		return teamMembersResult;
	}
}
