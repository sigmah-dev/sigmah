/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dao;

import java.util.List;

import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserPermission;


/**
 * Data Access Object for the {@link org.sigmah.shared.domain.UserPermission} domain class.
 * Implemented by {@link org.sigmah.server.dao.hibernate.DAOInvocationHandler proxy}
 *
 * @author Alex Bertram
 */
public interface UserPermissionDAO extends DAO<UserPermission, Integer> {	
	void deleteByUser(int userId);
	void createAndPersist(User user, OrgUnit orgUnit, Integer projectId);
	void deleteByProject(int projectId);
	void deleteByOrgUnits(List<OrgUnit> orgUnitList);
	void deleteByProjects(List<Project> projects);
	List<Project> getOrgUnitProjects(OrgUnit orgUnit);
	
}
