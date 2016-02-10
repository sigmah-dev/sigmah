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

import java.util.List;

import javax.persistence.NoResultException;

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
		return CollectionUtils.isNotEmpty(em().createQuery("SELECT o from OrgUnitProfile o WHERE o.user = :user").setParameter("user", user).getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitProfile findOrgUnitProfileByUser(final User user) {
		try {

			return em().createQuery("SELECT o from OrgUnitProfile o WHERE o.user = :user", entityClass).setParameter("user", user).getSingleResult();

		} catch (final NoResultException e) {
			return null;

		} catch (final Throwable t) {
			throw t;
		}
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
