package org.sigmah.server.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dao.UserPermissionDAO;
import org.sigmah.server.dao.UserUnitDAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserPermission;
import org.sigmah.server.domain.profile.GlobalPermission;
import org.sigmah.server.domain.profile.OrgUnitProfile;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.handler.GetProjectsHandler;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Trigger class that updates {@link UserPermission} table when one of the following actions happened:
 * <ol>
 * <li>User created/modified.</li>
 * <li>OrgUnit changes its parent.</li>
 * <li>Project created/modified/deleted.</li>
 * <li>Profile modified.</li>
 * </ol>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class UserPermissionPolicy {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UserPermissionPolicy.class);

	/**
	 * Injected {@link UserPermissionDAO}.
	 */
	private final UserPermissionDAO userPermissionDAO;

	/**
	 * Injected {@link UserDAO}.
	 */
	private final UserDAO userDAO;

	/**
	 * Injected {@link UserUnitDAO}.
	 */
	private final UserUnitDAO userUnitDAO;

	/**
	 * get projects attached to the user's main orgunit and all the child orgunit of it.
	 */
	private final GetProjectsHandler projectsHandler;

	@Inject
	public UserPermissionPolicy(UserPermissionDAO userPermissionDAO, UserDAO userDAO, UserUnitDAO userUnitDAO, GetProjectsHandler projectsHandler) {
		this.userPermissionDAO = userPermissionDAO;
		this.userDAO = userDAO;
		this.userUnitDAO = userUnitDAO;
		this.projectsHandler = projectsHandler;
	}

	/**
	 * Each time a user is created or modified a method will go through all projects which are editable by the user
	 * (projects attached to the user main orgunit, and all the child orgunits of it), and add a row in the table
	 * userpermission for each of them if the user has the EDIT_PROJECT global privilege
	 */
	public void updateUserPermissionByUser(User user) throws CommandException {

		final OrgUnitProfile userOrgUnit = user.getOrgUnitWithProfiles();

		// delete existing userpermission entries related to the user
		userPermissionDAO.deleteByUser(user.getId());

		// check new profile set for EDIT_PROJECT global permission
		boolean granted = isGranted(userOrgUnit, GlobalPermissionEnum.EDIT_PROJECT);
		if (!granted) /* skip the rest of part if user has no enough permission */
			return;

		final GetProjects getCommand = new GetProjects();
		List<Integer> orgUnitIds = new ArrayList<Integer>(Arrays.asList(userOrgUnit.getOrgUnit().getId()));
		getCommand.setOrgUnitsIds(orgUnitIds);
		getCommand.setMappingMode(ProjectDTO.Mode.BASE);

		final ListResult<ProjectDTO> result = projectsHandler.execute(getCommand, null);

		// create and persist userpermission entity for each project
		for (final ProjectDTO project : result.getList()) {
			userPermissionDAO.createAndPersist(user, userOrgUnit.getOrgUnit(), project.getId());
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("UserPermission updated.");
		}
	}

	/**
	 * Overloaded version of updateUserPermissionByUser(User)
	 */
	public void updateUserPermissionByUser(Integer userId) throws CommandException {
		final User user = userDAO.findById(userId);
		updateUserPermissionByUser(user);
	}

	/**
	 * When the global privilege "EDIT_PROJECT" is added/removed to a profile, UserPermissions is updated for the users
	 * who have included this profile
	 */
	public void updateUserPermissionByProfile(Integer profileId) throws CommandException {
		// for newly created profile no need to update userpermission
		if (profileId == null || profileId < 0) {
			return;
		}

		// Retrieves the users who have this profile.
		final List<User> users = userDAO.findUsersByProfile(profileId);

		if (users != null) {
			for (final User user : users) {
				updateUserPermissionByUser(user);
			}
		}
	}

	/**
	 * <ol>
	 * <li>fetch all parent org units of a given orgUnit</li>
	 * <li>get a list of users of orgunit and its parents</li>
	 * <li>update userpermission for each user</li>
	 * </ol>
	 */
	public void updateUserPermissionByOrgUnit(OrgUnit orgUnit) throws CommandException {

		List<OrgUnit> orgUnitList = new LinkedList<OrgUnit>();
		orgUnitList.add(orgUnit);
		fetchParentsUntilRoot(orgUnit, orgUnitList);

		final List<User> users = userUnitDAO.findUsersByOrgUnit(orgUnitList);

		if (users != null) {
			for (final User user : users) {
				updateUserPermissionByUser(user);
			}
		}
	}

	/**
	 * Delete UserPermission by OrgUnit's projects
	 */
	public void deleteUserPermssionByOrgUnit(OrgUnit orgUnit) {
		List<Project> projects = userPermissionDAO.getOrgUnitProjects(orgUnit);
		if (!projects.isEmpty()) {
			userPermissionDAO.deleteByProjects(projects);
		}
	}

	/**
	 * Deletes UserPermission by project
	 */
	public void deleteUserPemissionByProject(int projectId) {
		userPermissionDAO.deleteByProject(projectId);
	}

	/**
	 * Fetch orgUnits recursively until root(including) element
	 */
	private void fetchParentsUntilRoot(OrgUnit orgUnit, List<OrgUnit> list) {
		OrgUnit parent = orgUnit.getParentOrgUnit();
		if (parent != null) {
			list.add(parent);
			fetchParentsUntilRoot(parent, list);
		}
	}

	/**
	 * Utiliy to check the user's grant for a given permission
	 */
	public boolean isGranted(final OrgUnitProfile userOrgUnit, final GlobalPermissionEnum permission) {
		List<Profile> profiles = userOrgUnit.getProfiles();

		for (final Profile profile : profiles) {
			if (profile.getGlobalPermissions() != null) {
				for (final GlobalPermission p : profile.getGlobalPermissions()) {
					if (p.getPermission().equals(permission)) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
