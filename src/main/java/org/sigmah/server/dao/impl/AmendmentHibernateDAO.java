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
