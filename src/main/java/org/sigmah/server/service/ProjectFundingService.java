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

import com.google.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.ProjectFundingDTO;

import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * {@link ProjectFunding} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectFundingService extends AbstractEntityService<ProjectFunding, Integer, ProjectFundingDTO> {

	/**
	 * Injection of the service updating the computation elements.
	 */
	@Inject
	private ComputationService computationService;
	
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
		
		// Updating related computation elements.
		updateComputationsReferencingModelsOfProjects(fundingProject, fundedProject, context.getUser());
		
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
			
			// Updating related computation elements.
			updateComputationsReferencingModelsOfProjects(projectFunding.getFunding(), projectFunding.getFunded(), context.getUser());
		}
		
		return projectFunding;
	}
	
	/**
	 * Update the computation elements referencing the contribution of the given
	 * projects.
	 * 
	 * @param fundingProject
	 *			Project funding the `fundedProject`.
	 * @param fundedProject
	 *			Project founded by `fundingProject`.
	 * @param user 
	 *			User changing the value of the contribution.
	 */
	private void updateComputationsReferencingModelsOfProjects(final Project fundingProject, final Project fundedProject, final User user) {
		
		final List<ComputationElement> impactedComputations = getComputationsReferencingModels(fundingProject.getProjectModel(), fundedProject.getProjectModel());
		
		for (final ComputationElement computationElement : impactedComputations) {
			
			final ProjectModel parentModel = computationService.getParentProjectModel(computationElement);
			
			if (parentModel != null) {
				if (parentModel.equals(fundedProject.getProjectModel())) {
					computationService.updateComputationValueForProject(computationElement, fundedProject, user);
				}
				if (parentModel.equals(fundingProject.getProjectModel())) {
					computationService.updateComputationValueForProject(computationElement, fundingProject, user);
				}
			}
		}
	}
	
	/**
	 * Find computation elements whose formula references the given project
	 * models.
	 * 
	 * @param models
	 *			Array of project models to search.
	 * @return A list of computation elements. Empty if none matched.
	 */
	private List<ComputationElement> getComputationsReferencingModels(final ProjectModel... models) {
		
		final List<ComputationElement> impactedComputations = new ArrayList<>();
		
		for (final ProjectModel model : models) {
			final TypedQuery<ComputationElement> query = em().createQuery("SELECT ce FROM ComputationElement ce WHERE ce.rule LIKE :modelReference", ComputationElement.class);
			query.setParameter("modelReference", '%' + Instructions.ID_PREFIX + model.getId() + ValueResultUtils.BUDGET_VALUE_SEPARATOR + model.getName() + "%@contribution%");
			
			impactedComputations.addAll(query.getResultList());
		}
		
		return impactedComputations;
	}
	
}
