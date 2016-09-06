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
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.List;
import javax.persistence.TypedQuery;
import org.sigmah.server.computation.ServerComputations;
import org.sigmah.server.computation.ServerValueResolver;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.util.EntityLogicalElementTypes;
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
 * Service handling the update of computations from server-side events.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
@Singleton
public class ComputationService extends EntityManagerProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ComputationService.class);

	/**
	 * Injection of the service updating the flexible elements values.
	 */
	@Inject
	private ValueService valueService;
	
	/**
	 * Injection of the resolver of values for computation elements.
	 */
	@Inject
	private ServerValueResolver valueResolver;
	
	/**
	 * Search for the project model containing the given computation element.
	 * 
	 * @param computationElement
	 *			Computation element to use for the search.
	 * @return The parent project model or <code>null</code> if the computation
	 * element is not attached to a project model.
	 */
	public ProjectModel getParentProjectModel(final ComputationElement computationElement) {
		
		final TypedQuery<ProjectModel> phaseModelsQuery = em().createQuery("SELECT pm From ProjectModel pm join pm.phaseModels phm join phm.layout l join l.groups g join g.constraints c WHERE :element = c.element", ProjectModel.class);
		phaseModelsQuery.setParameter("element", computationElement);
		
		final List<ProjectModel> modelsInPhaseModels = phaseModelsQuery.getResultList();
		if (!modelsInPhaseModels.isEmpty()) {
			return modelsInPhaseModels.get(0);
		}
		
		final TypedQuery<ProjectModel> detailsQuery = em().createQuery("SELECT pm From ProjectModel pm join pm.projectDetails d join d.layout l join l.groups g join g.constraints c WHERE :element = c.element", ProjectModel.class);
		detailsQuery.setParameter("element", computationElement);
		
		final List<ProjectModel> modelsInDetails = detailsQuery.getResultList();
		if (!modelsInDetails.isEmpty()) {
			return modelsInDetails.get(0);
		}
		
		return null;
	}
	
	/**
	 * Update the value of the given computation element for the given project.
	 * 
	 * @param computationElement
	 *			Computation element to update.
	 * @param project
	 *			Project containing the values.
	 * @param user 
	 *			Author of the update.
	 */
	public void updateComputationValueForProject(final ComputationElement computationElement, final Project project, final User user) {
		
		final Computation computation = Computations.parse(computationElement.getRule(), ServerComputations.getAllElementsFromModel(project.getProjectModel()));
		
		final Future<String> computedValue = new Future<>();
		computation.computeValueWithResolver(project.getId(), valueResolver, computedValue.defer());

		try {
			final ComputedValue value = ComputedValues.from(computedValue.getOrThrow());
			valueService.saveValue(value.toString(), computationElement, project.getId(), user);
		} catch (Throwable t) {
			LOGGER.error("An error occured when computing the formula of the element '" + computationElement.getId() + "' for project '" + project.getId() + "'.", t);
		}
	}
	
	/**
	 * Search every computation element referencing contributions.
	 * 
	 * @return A collection of every computation element whose formula includes
	 * a contribution.
	 */
	public Collection<ComputationElement> getComputationElementsReferencingContributions() {
		
		final TypedQuery<ComputationElement> query = em().createQuery("SELECT ce FROM ComputationElement ce WHERE ce.rule LIKE '%@contribution%'", ComputationElement.class);
		return query.getResultList();
	}
	
	public Collection<ComputationElement> getComputationElementsReferencingElement(final FlexibleElement element) {
		
		final TypedQuery<ComputationElement> query = em().createQuery("SELECT ce FROM ComputationElement ce WHERE ce.rule LIKE :criteria", ComputationElement.class);
		query.setParameter("criteria", "%" + Instructions.ID_PREFIX 
				+ element.getId() 
				+ ValueResultUtils.BUDGET_VALUE_SEPARATOR 
				+ element.getCode() 
				+ ValueResultUtils.BUDGET_VALUE_SEPARATOR 
				+ EntityLogicalElementTypes.of(element) + '%');
		
		return query.getResultList();
	}
}
