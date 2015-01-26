package org.sigmah.server.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dao.ReminderListDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.reminder.ReminderList;

/**
 * {@link ReminderListDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReminderListHibernateDAO extends AbstractDAO<ReminderList, Integer> implements ReminderListDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReminderList findByProjectId(final Integer projectId) {

		final TypedQuery<ReminderList> query = em().createQuery("SELECT p.remindersList FROM Project p WHERE p.id = :projectId", entityClass);
		query.setParameter("projectId", projectId);

		try {

			return query.getSingleResult();

		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

}
