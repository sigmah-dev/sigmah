package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.endpoint.gwtrpc.handler.GetProjectsHandler;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetProjects.ProjectResultType;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.dao.UserDAO;
import org.sigmah.shared.dao.UserPermissionDAO;
import org.sigmah.shared.dao.UserUnitDAO;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.GlobalPermission;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.domain.profile.OrgUnitProfile;
import org.sigmah.shared.domain.profile.Profile;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/*
 * Trigger class that updates userpermission table 
 * when one of the following actions happened:
 * 1. User created/modified
 * 2. Orgunit changes its parent
 * 3. Project created/modified/deleted
 * 4. Profile modified
 */
@Singleton
public class UserPermissionPolicy {

	private static final Log log = LogFactory.getLog(UserPermissionPolicy.class);
	private final Injector injector;

	@Inject
	public UserPermissionPolicy(Injector injector) {
		this.injector = injector;
	}

	/*
	 * Each time a user is created or modified  
	 * a method will go through all projects which are editable by the 
	 * user (projects attached to the user main orgunit, and all the child orgunits of it), 
	 * and add a row in the table userpermission for each of them 
	 * if the user has the EDIT_PROJECT global privilege 
	 */
	public void updateUserPermissionByUser(User user) throws CommandException{
		
		final UserPermissionDAO userPermissionDAO = injector.getInstance(UserPermissionDAO.class);
		final OrgUnitProfile userOrgUnit = user.getOrgUnitWithProfiles();

		// delete existing userpermission entries related to the user
		userPermissionDAO.deleteByUser(user.getId());

		// check new profile set for EDIT_PROJECT global permission
		boolean granted = isGranted(userOrgUnit, GlobalPermissionEnum.EDIT_PROJECT);
		if (!granted) /* skip the rest of part if user has no enough permission */
			return;

		/*
		 *  get projects attached to the user's main orgunit
		 *  and all the child orgunit of it
		 */
		GetProjectsHandler projectsHandler = injector.getInstance(GetProjectsHandler.class);
		
		final GetProjects getCommand = new GetProjects();
		List<Integer> orgUnitIds = new ArrayList<Integer>(Arrays.asList(userOrgUnit.getOrgUnit().getId()));
		getCommand.setOrgUnitsIds(orgUnitIds);
		getCommand.setReturnType(ProjectResultType.ID);
		
		ProjectListResult result = (ProjectListResult) projectsHandler.execute(getCommand, null);
		
		// create and persist userpermission entity for each project
		for (Integer projectId : result.getListProjectsIds()) {
			userPermissionDAO.createAndPersist(user, userOrgUnit.getOrgUnit(),projectId);
		}

		log.info("UserPermission updated");
	}
	
	/*
	 * Overloaded version of updateUserPermissionByUser(User)
	 */
	public void updateUserPermissionByUser(Integer userId) throws CommandException {
		final User user = injector.getInstance(EntityManager.class).find(User.class, userId);
		updateUserPermissionByUser(user); 
	}
	
	/*
	 * When the global privilege "EDIT_PROJECT" is added/removed to a profile, 
	 * UserPermissions is updated for the users who have included this profile  
	 */
	public void updateUserPermissionByProfile(Integer profileId) throws CommandException {
		//for newly created profile no need to update userpermission
		if(profileId<0) return; 
		
		final UserDAO userDAO = injector.getInstance(UserDAO.class);
		
		//get list of users who have this profile
		List<Integer> userIds =  userDAO.getUserIdsByProfile(profileId);	
		if(userIds!=null){
			for(Integer userId : userIds){
				updateUserPermissionByUser(userId);
			}
		}
  	}
 
	
	/*
	 * 1. fetch all parent org units of a given orgUnit
	 * 2. get a list of users of orgunit and its parents
	 * 3. update userpermission for each user
	 */
	public void updateUserPermissionByOrgUnit(OrgUnit orgUnit) throws CommandException{
		final UserUnitDAO userUnitDAO=injector.getInstance(UserUnitDAO.class);
		
		List<OrgUnit> orgUnitList=new LinkedList<OrgUnit>();
		orgUnitList.add(orgUnit);		
		fetchParentsUntilRoot(orgUnit,orgUnitList);
		
		List<User> users=userUnitDAO.findUsersByOrgUnit(orgUnitList);
		
		if(users!=null){
			for(User user: users){
				updateUserPermissionByUser(user);
			}
		}	  		
	}
	
	/*
	 * Delete UserPermission by OrgUnit's projects
	 */
	public void deleteUserPermssionByOrgUnit(OrgUnit orgUnit){	 	
		UserPermissionDAO permissionDAO = injector.getInstance(UserPermissionDAO.class);
		List<Project> projects =  permissionDAO.getOrgUnitProjects(orgUnit);
		permissionDAO.deleteByProjects(projects);		
	}
	
	/*
	 * Deletes UserPermission by project
	 */
	public void deleteUserPemissionByProject(int projectId){
		UserPermissionDAO permissionDAO = injector.getInstance(UserPermissionDAO.class);
   	 	permissionDAO.deleteByProject(projectId);
	}
	

	/*
	 * Fetch orgUnits recursively until root(including) element
	 */		
	private void fetchParentsUntilRoot(OrgUnit orgUnit,List<OrgUnit> list){
		OrgUnit parent=orgUnit.getParent();
		if(parent!=null){
			list.add(parent);
			fetchParentsUntilRoot(parent,list);
		}
	}

	/*
	 * Utiliy to check the user's grant for a given permission
	 */
	public boolean isGranted(final OrgUnitProfile userOrgUnit,
			final GlobalPermissionEnum permission) {
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
