package org.sigmah.server.handler;

import com.google.inject.persist.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.sigmah.server.dao.ProfileDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateProjectTeamMembers;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

public class UpdateProjectTeamMembersHandler extends AbstractCommandHandler<UpdateProjectTeamMembers, TeamMembersResult> {
	private final ProfileDAO profileDAO;
	private final ProjectDAO projectDAO;
	private final UserDAO userDAO;

	@Inject
	UpdateProjectTeamMembersHandler(ProfileDAO profileDAO, ProjectDAO projectDAO, UserDAO userDAO) {
		this.profileDAO = profileDAO;
		this.projectDAO = projectDAO;
		this.userDAO = userDAO;
	}

	@Override
	@Transactional
	protected TeamMembersResult execute(UpdateProjectTeamMembers command, UserDispatch.UserExecutionContext context) throws CommandException {
		// TODO: Verify if the user is allowed to update team members
		Project project = projectDAO.findById(command.getProjectId());
		Set<Integer> teamMemberIds = new HashSet<>();
		Set<Integer> teamMemberProfileIds = new HashSet<>();

		// TODO: Verify that the users are related to the org unit of the project
		for (UserDTO user : command.getTeamMembers()) {
			teamMemberIds.add(user.getId());
		}
		for (ProfileDTO profile : command.getTeamMemberProfiles()) {
			teamMemberProfileIds.add(profile.getId());
		}

		List<User> teamMembers = userDAO.findByIds(teamMemberIds);
		List<Profile> teamMemberProfiles = profileDAO.findByIds(teamMemberProfileIds);

		project = projectDAO.updateProjectTeamMembers(project, teamMembers, teamMemberProfiles, context.getUser());

		UserDTO managerDTO = mapper().map(project.getManager(), UserDTO.class, UserDTO.Mode.WITH_BASE_PROFILES);
		managerDTO.generateCompleteName();

		List<ProfileDTO> profileDTOs = new ArrayList<>();
		for (Profile profile : project.getTeamMemberProfiles()) {
			ProfileDTO profileDTO = mapper().map(profile, ProfileDTO.class, ProfileDTO.Mode.BASE);
			profileDTOs.add(profileDTO);
		}
		List<UserDTO> userDTOs = new ArrayList<>();
		for (User user : project.getTeamMembers()) {
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
