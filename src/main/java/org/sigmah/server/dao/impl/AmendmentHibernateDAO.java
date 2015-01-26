package org.sigmah.server.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dao.AmendmentDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Amendment;
import org.sigmah.server.domain.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AmendmentDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class AmendmentHibernateDAO extends AbstractDAO<Amendment, Integer> implements AmendmentDAO {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AmendmentHibernateDAO.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Project findAmendmentProject(final Integer amendmentId) {

		final TypedQuery<Project> query = em().createQuery("SELECT a.parentProject FROM Amendment a WHERE a.id = :amendmentId", Project.class);
		query.setParameter("amendmentId", amendmentId);

		try {

			return query.getSingleResult();

		} catch (NoResultException e) {
			return null;

		} catch (NonUniqueResultException e) {
			LOG.warn("Non unique amendment entity with id '{}'.", amendmentId);
			return null;
		}
	}

}
