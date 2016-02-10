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
import java.util.Set;

import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.sigmah.server.dao.AdminDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.AdminEntity;

/**
 * AdminHibernateDAO implementation.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class AdminHibernateDAO extends AbstractDAO<AdminEntity, Integer> implements AdminDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AdminEntity> findRootEntities(int levelId) {
		final TypedQuery<AdminEntity> query =
				em().createQuery("SELECT entity FROM AdminEntity entity WHERE entity.level.id = :levelId ORDER BY entity.name", AdminEntity.class);
		query.setParameter("levelId", levelId);

		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AdminEntity> findChildEntities(int levelId, int parentEntityId) {

		final TypedQuery<AdminEntity> query =
				em().createQuery("SELECT entity FROM AdminEntity entity WHERE entity.level.id = :levelId AND entity.parent.id = :parentId ORDER BY entity.name",
					AdminEntity.class);
		query.setParameter("levelId", levelId);
		query.setParameter("parentId", parentEntityId);

		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AdminEntity> find(int entityLevelId, int parentEntityId, int activityId) {
		Query q = query();
		if (activityId > -1) {
			q.withSitesOfActivityId(activityId);
		}
		if (entityLevelId > -1) {
			q.level(entityLevelId);
		}
		if (parentEntityId > -1) {
			q.withParentEntityId(parentEntityId);
		}
		return q.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query query() {

		final Criteria criteria = createCriteria();

		return new Query() {

			@Override
			public Query level(int levelId) {
				criteria.createAlias("entity.level", "level").add(Restrictions.eq("level.id", levelId));
				return this;
			}

			@Override
			public Query withParentEntityId(int id) {
				criteria.createAlias("entity.parent", "parent").add(Restrictions.eq("parent.id", id));
				return this;
			}

			@Override
			public Query withSitesOfActivityId(int id) {
				DetachedCriteria havingActivities =
						DetachedCriteria.forClass(AdminEntity.class, "entity").createAlias("locations", "location").createAlias("location.sites", "site")
							.createAlias("site.activity", "activity").add(Restrictions.eq("activity.id", id)).setProjection(Projections.property("entity.id"));

				criteria.add(Subqueries.propertyIn("entity.id", havingActivities));
				return this;
			}

			@Override
			@SuppressWarnings("unchecked")
			public List<AdminEntity> execute() {
				return criteria.list();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AdminEntity> findBySiteIds(Set<Integer> siteIds) {
		// FIXME [DAO] Method not implemented !
		return null;
	}

	// --------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------

	private Criteria createCriteria() {
		return getSession(em()).createCriteria(AdminEntity.class, "entity");
	}

}
