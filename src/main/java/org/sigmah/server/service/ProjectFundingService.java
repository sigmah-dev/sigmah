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
import java.util.Arrays;
import java.util.List;
import org.sigmah.server.computation.ServerComputations;
import org.sigmah.server.computation.ServerValueResolver;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.Computations;
import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.util.Future;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ProjectFunding} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectFundingService extends AbstractEntityService<ProjectFunding, Integer, ProjectFundingDTO> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectFundingService.class);
	
	@Inject
	private ServerValueResolver valueResolver;
	
	@Inject
	private ValueService valueService;
	
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
		final List<ComputationElement> impactedComputations = getImpactedComputationsForModels(fundingProject.getProjectModel(), fundedProject.getProjectModel());

		for (final ComputationElement computationElement : impactedComputations) {
			final TypedQuery<ProjectModel> modelQuery = em().createQuery("SELECT pm From ProjectModel pm WHERE :element = pm.phaseModels.layout.groups.constraints.element OR :element = pm.projectDetails.layout.groups.constraints.element", ProjectModel.class);
			modelQuery.setParameter("element", computationElement);
			
			final ProjectModel parentModel = modelQuery.getSingleResult();
			
			if (parentModel != null) {
				final Computation computation = Computations.parse(computationElement.getRule(), ServerComputations.getAllElementsFromModel(parentModel));
				
				for (final Project project : Arrays.asList(fundingProject, fundedProject)) {
					if (parentModel.equals(project.getProjectModel())) {
						final Future<String> computedValue = new Future<>();
						computation.computeValueWithResolver(project.getId(), valueResolver, computedValue.defer());

						try {
							final ComputedValue value = ComputedValues.from(computedValue.getOrThrow());
							valueService.saveValue(value.toString(), computationElement, project.getId(), context.getUser());
						} catch (Throwable t) {
							LOGGER.error("An error occured when computing the formula of the element '" + computationElement.getId() + "' for project '" + project.getId() + "'.", t);
						}
					}
				}
			}
		}
		
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
	
	private List<ComputationElement> getImpactedComputationsForModels(final ProjectModel... models) {
		final List<ComputationElement> impactedComputations = new ArrayList<>();
		
		for (final ProjectModel model : models) {
			final TypedQuery<ComputationElement> query = em().createQuery("SELECT ce FROM ComputationElement ce WHERE ce.rule LIKE :modelReference", ComputationElement.class);
			query.setParameter("modelReference", '%' + Instructions.ID_PREFIX + model.getId() + ValueResultUtils.BUDGET_VALUE_SEPARATOR + model.getName() + '%');
			
			impactedComputations.addAll(query.getResultList());
		}
		
		return impactedComputations;
	}

}
