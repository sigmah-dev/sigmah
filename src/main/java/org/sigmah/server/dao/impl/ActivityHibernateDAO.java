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
