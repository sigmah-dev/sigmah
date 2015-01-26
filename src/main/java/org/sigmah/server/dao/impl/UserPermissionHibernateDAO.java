package org.sigmah.server.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.UserPermissionDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.UserPermission;

import com.google.inject.persist.Transactional;

/**
 * UserPermissionDAO implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UserPermissionHibernateDAO extends AbstractDAO<UserPermission, Integer> implements UserPermissionDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void deleteByUser(int userId) {
		update(em().createQuery("DELETE FROM UserPermission WHERE user.id = ?1").setParameter(1, userId));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void deleteByProject(int projectId) {
		update(em().createQuery("DELETE FROM UserPermission WHERE database.id = ?1").setParameter(1, projectId));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void deleteByOrgUnits(List<OrgUnit> orgUnitList) {
		update(em().createQuery("DELETE FROM UserPermission WHERE partner IN (?1)").setParameter(1, orgUnitList));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void deleteByProjects(List<Project> projects) {
		update(em().createQuery("DELETE FROM UserPermission WHERE database IN (?1)").setParameter(1, projects));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Project> getOrgUnitProjects(OrgUnit orgUnit) {
		final TypedQuery<Project> query =
				em().createQuery("SELECT p FROM Project p WHERE :unit MEMBER OF p.partners AND p.projectModel.status != 'DRAFT'", Project.class);
		query.setParameter("unit", orgUnit);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createAndPersist(User user, OrgUnit orgUnit, Integer projectId) {

		final UserPermission permission = new UserPermission(em().find(UserDatabase.class, projectId), user);

		permission.setPartner(orgUnit);
		permission.setAllowDesign(true);
		permission.setAllowEdit(true);
		permission.setAllowEditAll(true);
		permission.setAllowManageAllUsers(true);
		permission.setAllowManageUsers(true);
		permission.setAllowView(true);
		permission.setAllowViewAll(true);
		permission.setLastSchemaUpdate(new Date());

		persist(permission);
	}

}
