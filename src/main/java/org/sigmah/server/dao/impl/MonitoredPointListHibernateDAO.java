package org.sigmah.server.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dao.MonitoredPointListDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.reminder.MonitoredPointList;

/**
 * {@link MonitoredPointListDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MonitoredPointListHibernateDAO extends AbstractDAO<MonitoredPointList, Integer> implements MonitoredPointListDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitoredPointList findByProjectId(final Integer projectId) {

		final TypedQuery<MonitoredPointList> query = em().createQuery("SELECT p.pointsList FROM Project p WHERE p.id = :projectId", entityClass);
		query.setParameter("projectId", projectId);

		try {

			return query.getSingleResult();

		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

}
