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
import javax.persistence.TypedQuery;

import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.User;

import org.apache.commons.collections4.CollectionUtils;

/**
 * UserDAO implementation.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UserHibernateDAO extends AbstractDAO<User, Integer> implements UserDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean doesUserExist(final String email) {
		return CollectionUtils.isNotEmpty(em().createQuery("SELECT u FROM User u WHERE u.email = :email", User.class).setParameter("email", email).getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findUserByEmail(final String email) {
		try {

			return em().createQuery("SELECT u FROM User u WHERE u.email = :email", User.class).setParameter("email", email).getSingleResult();

		} catch (final NoResultException e) {
			return null;

		} catch (final Exception e) {
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findUserByChangePasswordKey(final String key) {
		try {

			return em().createQuery("SELECT u FROM User u WHERE u.changePasswordKey = :key", User.class).setParameter("key", key).getSingleResult();

		} catch (final NoResultException e) {
			return null;

		} catch (final Exception e) {
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countUsersByProfile(final Integer profileId) {

		final StringBuilder query = new StringBuilder();

		query.append("SELECT ");
		query.append("  COUNT(u) ");
		query.append("FROM ");
		query.append("  User u ");
		query.append("  JOIN u.orgUnitWithProfiles oup ");
		query.append("  JOIN oup.profiles p ");
		query.append("WHERE ");
		query.append("  p.id = :profileId");

		return em().createQuery(query.toString(), Number.class).setParameter("profileId", profileId).getSingleResult().intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> findUsersByProfile(final Integer profileId) {

		final StringBuilder query = new StringBuilder();

		query.append("SELECT ");
		query.append("  u ");
		query.append("FROM ");
		query.append("  User u ");
		query.append("  JOIN FETCH u.orgUnitWithProfiles oup ");
		query.append("  JOIN oup.profiles p ");
		query.append("WHERE ");
		query.append("  p.id = :profileId");

		return em().createQuery(query.toString(), User.class).setParameter("profileId", profileId).getResultList();
	}

	@Override
	public User getProjectManager(Integer projectId) {
		TypedQuery<User> query = em().createQuery(
			"SELECT p.manager " +
			"FROM Project p " +
			"WHERE p.id = :projectId",
			User.class
		);
		query.setParameter("projectId", projectId);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<User> getProjectTeamMembers(Integer projectId) {
		TypedQuery<User> query = em().createQuery(
			"SELECT u " +
				"FROM Project p " +
				"JOIN p.teamMembers u " +
				"WHERE p.id = :projectId",
			User.class
		);
		query.setParameter("projectId", projectId);
		return query.getResultList();
	}
}
