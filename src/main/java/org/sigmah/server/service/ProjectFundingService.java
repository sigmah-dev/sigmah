package org.sigmah.server.service;

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
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.ProjectFundingDTO;

import com.google.inject.Singleton;

/**
 * {@link ProjectFunding} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectFundingService extends AbstractEntityService<ProjectFunding, Integer, ProjectFundingDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectFunding create(final PropertyMap properties, final UserExecutionContext context) {

		// Retrieves parameters.
		final Object fundingId = properties.get(ProjectFundingDTO.FUNDING_ID);
		final Object fundedId = properties.get(ProjectFundingDTO.FUNDED_ID);
		final Object percentage = properties.get(ProjectFundingDTO.PERCENTAGE);

		// Retrieves projects.
		final Project fundingProject = em().find(Project.class, fundingId);
		final Project fundedProject = em().find(Project.class, fundedId);

		// Retrieves the eventual already existing link.
		final TypedQuery<ProjectFunding> query = em().createQuery("SELECT f FROM ProjectFunding f WHERE f.funding = :p1 AND f.funded = :p2", entityClass);
		query.setParameter("p1", fundingProject);
		query.setParameter("p2", fundedProject);

		ProjectFunding funding;

		// Updates or creates the link.
		try {

			funding = query.getSingleResult();

		} catch (final NoResultException e) {
			funding = new ProjectFunding();
			funding.setFunding(fundingProject);
			funding.setFunded(fundedProject);
		}

		funding.setPercentage((Double) percentage);

		// Saves.
		em().persist(funding);

		return funding;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectFunding update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		ProjectFunding projectFunding = em().find(ProjectFunding.class, entityId);

		if (projectFunding != null) {
			projectFunding.setPercentage((Double) changes.get(ProjectFundingDTO.PERCENTAGE));
			projectFunding = em().merge(projectFunding);
		}

		return projectFunding;
	}

}
