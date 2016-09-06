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

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.ProjectModel} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public interface ProjectModelDAO extends DAO<ProjectModel, Integer> {

	/**
	 * Finds the project models visible to the given {@code organizationId}.
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @return The project models visible to the given {@code organizationId}, ordered by name.
	 */
	List<ProjectModel> findProjectModelsVisibleToOrganization(Integer organizationId);

	/**
	 * Finds the project models visible to the given {@code organizationId} which status is included into given
	 * {@code status} filters (if any).
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @param status
	 *          Filters project models which status is included into given {@code status}.<br>
	 *          Ignored if {@code null} or empty.
	 * @return The project models visible to the given {@code organizationId}, ordered by name.
	 */
	List<ProjectModel> findProjectModelsVisibleToOrganization(Integer organizationId, List<ProjectModelStatus> status);
	
	/**
	 * Finds the project models with the given name.
	 * 
	 * @param name
	 *			Name of the project models to find.
	 * @return A list of the project models with the given name.
	 */
	List<ProjectModel> findProjectModelsWithName(String name);

}
