package org.sigmah.server.dao.impl;

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
