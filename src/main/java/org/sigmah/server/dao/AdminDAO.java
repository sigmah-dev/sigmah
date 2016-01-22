package org.sigmah.server.dao;

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

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.AdminEntity;
import org.sigmah.server.domain.AdminLevel;

/**
 * Data Access Object for {@link org.sigmah.server.domain.AdminEntity} classes.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface AdminDAO extends DAO<AdminEntity, Integer> {

	public static interface Query {

		Query level(int levelId);

		Query withParentEntityId(int parentEntityId);

		Query withSitesOfActivityId(int activityId);

		List<AdminEntity> execute();

	}

	/**
	 * @param levelId
	 *          The id of the administrative level for which to return the entities
	 * @return A list of administrative entities that constitute an administrative level. (e.g. return all provinces,
	 *         return all districts, etc)
	 */
	List<AdminEntity> findRootEntities(int levelId);

	/**
	 * Returns
	 * 
	 * @param levelId
	 *          id of the {@link AdminLevel} to search
	 * @param parentEntityId
	 *          the entity parent
	 * @return A list of the children of a given admin entity for at a given level.
	 */
	List<AdminEntity> findChildEntities(int levelId, int parentEntityId);

	/**
	 * Returns
	 * 
	 * @param entityLevelId
	 *          id of the {@link AdminLevel} to search
	 * @param parentEntityId
	 *          the entity parent
	 * @param activityId
	 *          activity linked to a related site
	 * @return A list of the children of a given admin entity for at a given level.
	 */
	List<AdminEntity> find(int entityLevelId, int parentEntityId, int activityId);

	List<AdminEntity> findBySiteIds(Set<Integer> siteIds);

	Query query();
}
