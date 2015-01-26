package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserPermission;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.UserPermission} domain class.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface UserPermissionDAO extends DAO<UserPermission, Integer> {

	void deleteByUser(int userId);

	void createAndPersist(User user, OrgUnit orgUnit, Integer projectId);

	void deleteByProject(int projectId);

	void deleteByOrgUnits(List<OrgUnit> orgUnitList);

	void deleteByProjects(List<Project> projects);

	List<Project> getOrgUnitProjects(OrgUnit orgUnit);

}
