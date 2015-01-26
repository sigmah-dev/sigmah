package org.sigmah.server.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.User;

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

}
