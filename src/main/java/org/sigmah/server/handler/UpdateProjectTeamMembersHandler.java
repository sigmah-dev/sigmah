package org.sigmah.server.handler;

import com.google.inject.persist.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateProjectTeamMembers;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;

public class UpdateProjectTeamMembersHandler extends AbstractCommandHandler<UpdateProjectTeamMembers, TeamMembersResult> {
	private final ProjectDAO projectDAO;
	private final UserDAO userDAO;

	@Inject
	UpdateProjectTeamMembersHandler(ProjectDAO projectDAO, UserDAO userDAO) {
		this.projectDAO = projectDAO;
		this.userDAO = userDAO;
	}

	@Override
	@Transactional
	protected TeamMembersResult execute(UpdateProjectTeamMembers command, UserDispatch.UserExecutionContext context) throws CommandException {
		// TODO: Verify if the user is allowed to update team members
		Project project = projectDAO.findById(command.getProjectId());
		Set<Integer> teamMemberIds = new HashSet<>();

		// TODO: Verify that the users are related to the org unit of the project
		for (UserDTO user : command.getTeamMembers()) {
			teamMemberIds.add(user.getId());
		}

		List<User> teamMembers = userDAO.findByIds(teamMemberIds);
		project = projectDAO.updateProjectTeamMembers(project, teamMembers, context.getUser());

		UserDTO managerDTO = mapper().map(project.getManager(), UserDTO.class, UserDTO.Mode.WITH_BASE_PROFILES);
		managerDTO.generateCompleteName();

		List<UserDTO> userDTOs = new ArrayList<>();
		for (User user : project.getTeamMembers()) {
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
