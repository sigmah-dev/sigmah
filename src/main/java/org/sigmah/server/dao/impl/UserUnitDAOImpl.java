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

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.UserUnitDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.OrgUnitProfile;

/**
 * UserUnitDAO implementation.
 * 
 * @author nrebiai
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UserUnitDAOImpl extends AbstractDAO<OrgUnitProfile, Integer> implements UserUnitDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean doesOrgUnitProfileExist(final User user) {
		List resultList = em().createQuery("SELECT o from OrgUnitProfile o WHERE o.user = :user")
			.setParameter("user", user)
			.getResultList();
		return CollectionUtils.isNotEmpty(resultList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitProfile findMainOrgUnitProfileByUserId(Integer userId) {
		try {
			TypedQuery<OrgUnitProfile> query = em().createQuery(
				"SELECT oup " +
				"FROM OrgUnitProfile oup " +
				"JOIN FETCH oup.profiles p " +
				"JOIN FETCH oup.orgUnit o " +
				"WHERE oup.user.id = :userId " +
				"AND oup.type = 'MAIN' ",
				OrgUnitProfile.class
			);
			return query.setParameter("userId", userId).getSingleResult();
		} catch (final NoResultException e) {
			return null;
		} catch (final Throwable t) {
			throw t;
		}
	}

	@Override
	public Set<Integer> findSecondaryOrgUnitIdsByUserId(Integer userId) {
		TypedQuery<Integer> query = em().createQuery(
			"SELECT oup.orgUnit.id " +
			"FROM OrgUnitProfile oup " +
			"WHERE oup.user.id = :userId " +
			"AND oup.type = 'SECONDARY' ",
			Integer.class
		);
		return new HashSet<>(query.setParameter("userId", userId).getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> findUsersByOrgUnit(final List<OrgUnit> orgUnits) {
		return em().createQuery("SELECT o.user FROM OrgUnitProfile o WHERE o.orgUnit IN (:orgUnits)", User.class).setParameter("orgUnits", orgUnits)
			.getResultList();
	}
}
