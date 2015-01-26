package org.sigmah.server.dao.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.ActivityDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Activity;

/**
 * ActivityDAO implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ActivityHibernateDAO extends AbstractDAO<Activity, Integer> implements ActivityDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer queryMaxSortOrder(final Integer databaseId) {

		final TypedQuery<Number> query = em().createQuery("SELECT MAX(a.sortOrder) FROM Activity a WHERE a.database.id = :databaseId", Number.class);
		query.setParameter("databaseId", databaseId);

		final Number result = query.getSingleResult();
		return result != null ? result.intValue() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Activity> getActivitiesByDatabaseId(final Integer databaseId) {

		final TypedQuery<Activity> query = em().createQuery("SELECT a FROM Activity a WHERE a.database.id = :databaseId", Activity.class);
		query.setParameter("databaseId", databaseId);

		return new HashSet<Activity>(query.getResultList());
	}

}
