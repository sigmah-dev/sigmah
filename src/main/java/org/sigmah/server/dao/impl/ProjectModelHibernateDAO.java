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


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * {@link ProjectModelDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ProjectModelHibernateDAO extends AbstractDAO<ProjectModel, Integer> implements ProjectModelDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectModel> findProjectModelsVisibleToOrganization(final Integer organizationId) {
		return findProjectModelsVisibleToOrganization(organizationId, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectModel> findProjectModelsVisibleToOrganization(final Integer organizationId, final List<ProjectModelStatus> status) {

		final StringBuilder builder = new StringBuilder(
			"SELECT "
		  + "  pm "
		  + "FROM "
		  + "  ProjectModel pm "
		  + "  LEFT JOIN FETCH pm.projectBanner pb "
		  + "  LEFT JOIN FETCH pm.projectDetails pd "
		  + "  LEFT JOIN FETCH pm.logFrameModel lfm "
		  + "WHERE "
		  + "  EXISTS ("
		  + "    SELECT 1 FROM ProjectModelVisibility pmv WHERE pmv.organization.id = :organizationId"
		  + "  )");
		if (CollectionUtils.isNotEmpty(status)) {
			builder.append(" AND pm.status IN :status ");
		}
		if(!status.contains(ProjectModelStatus.UNDER_MAINTENANCE)) {
			builder.append(" AND (pm.dateMaintenance is null OR pm.dateMaintenance > :now)");
		}
		builder.append("ORDER BY pm.name");

		final TypedQuery<ProjectModel> query = em().createQuery(builder.toString(), ProjectModel.class);
		query.setParameter("organizationId", organizationId);
		if (CollectionUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if(!status.contains(ProjectModelStatus.UNDER_MAINTENANCE)) {
			query.setParameter("now", new Timestamp(new Date().getTime()));
		}

		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectModel> findProjectModelsWithName(final String name) {
		
		final TypedQuery<ProjectModel> query = em().createQuery("SELECT pm FROM ProjectModel pm WHERE pm.name = :name", ProjectModel.class);
		query.setParameter("name", name);
		
		return query.getResultList();
	}

}
