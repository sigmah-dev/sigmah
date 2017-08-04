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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.ContactUnit;
import org.sigmah.server.domain.OrgUnit;

/**
 * {@link OrgUnitDAO} implementation.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class OrgUnitHibernateDAO extends AbstractDAO<OrgUnit, Integer> implements OrgUnitDAO {
	@Override
	public Set<Integer> getOrgUnitTreeIdsByUserId(Integer userId) {
		TypedQuery<Integer> query = em().createQuery(
			"SELECT oup.orgUnit.id " +
			"FROM OrgUnitProfile oup " +
			"JOIN oup.user u " +
			"WHERE u.id = :userId ",
			Integer.class
		);
		query.setParameter("userId", userId);
		Set<Integer> userOrgUnitIds = new HashSet<>(query.getResultList());
		Set<Integer> orgUnitIds = new HashSet<>();
		for (Integer orgUnitId : userOrgUnitIds) {
			if (orgUnitIds.contains(orgUnitId)) {
				// if the current orgUnitId is already in the set, it means that it is a parent of a previous orgUnit
				// and that it was already crawled
				continue;
			}
			orgUnitIds.addAll(getOrgUnitTreeIds(orgUnitId));
		}
		return orgUnitIds;
	}

	@Override
	public Set<Integer> getOrgUnitTreeIds(Integer rootId) {
		Set<Integer> orgUnitIds = new HashSet<>();
		Integer currentOrgUnitId = rootId;
		while (currentOrgUnitId != null) {
			orgUnitIds.add(currentOrgUnitId);
			currentOrgUnitId = getParentId(currentOrgUnitId);
		}
		return orgUnitIds;
	}

	private Integer getParentId(Integer childId) {
		TypedQuery<Integer> query = em().createQuery(
			"SELECT o.parentOrgUnit.id " +
			"FROM OrgUnit o " +
			"WHERE o.id = :childId",
			Integer.class
		);
		query.setParameter("childId", childId);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<OrgUnit> findByOrganizationId(Integer organizationId) {
		TypedQuery<OrgUnit> query = em().createQuery(
				"SELECT o " +
				"FROM OrgUnit o " +
				"WHERE o.organization.id = :organizationId ",
				OrgUnit.class
		);
		query.setParameter("organizationId", organizationId);
		return query.getResultList();
	}

	@Override public List<ContactUnit> getContactUnit(List<Integer> contactIds) {
		TypedQuery<ContactUnit> query = em().createQuery(
				"SELECT c " +
						"FROM ContactUnit c " +
						"WHERE c.idContact in :contactIds ",
				ContactUnit.class
		);
		query.setParameter("contactIds", contactIds);
		return query.getResultList();
	}
}
