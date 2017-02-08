package org.sigmah.server.computation;

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
import java.util.List;
import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.shared.computation.AbstractDependencyResolver;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.computation.dependency.ContributionDependency;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.DependencyVisitor;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolve dependencies of <code>Computation</code>s to external elements.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class ServerDependencyResolver extends AbstractDependencyResolver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDependencyResolver.class);

	@Inject
	private ProjectModelDAO projectModelDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resolve(final Dependency dependency) {
		dependency.accept(new DependencyVisitor() {
			
			@Override
			public void visit(final SingleDependency dependency) {
				// Nothing to do.
			}

			@Override
			public void visit(final CollectionDependency dependency) {
				resolve(dependency);
			}

			@Override
			public void visit(final ContributionDependency dependency) {
				resolve(dependency);
			}
		});
	}
	
	/**
	 * Resolve the given <code>CollectionDependency</code>.
	 * 
	 * @param dependency 
	 *			Dependency to resolve.
	 */
	private void resolve(final CollectionDependency dependency) {
		final List<ProjectModel> projectModels = projectModelDAO.findProjectModelsWithName(dependency.getScope().getModelName());
		
		if (projectModels.isEmpty()) {
			throw new IllegalArgumentException("Project Model '" + dependency.getScope().getModelName() + "' was not found.");
		}
				
		for (final ProjectModel projectModel : projectModels) {
			final FlexibleElementDTO element = ServerComputations.getElementWithCodeInModel(dependency.getElementCode(), projectModel);
			if (element != null) {
				if (dependency.getFlexibleElement() != null) {
					LOGGER.error("More than 1 element matched the couple projectModel '" 
							+ dependency.getScope().getModelName() 
							+ "' & elementCode '" 
							+ dependency.getElementCode() 
							+ "'. The computation may behave incorrectly.");
					// Marks the dependency as 'not resolved' by nulling its flexible element.
					dependency.setFlexibleElement(null);
					dependency.setProjectModelId(null);
					return;
				}
				dependency.setFlexibleElement(element);
				dependency.setProjectModelId(projectModel.getId());
			}
		}
		
		if (dependency.getFlexibleElement() == null) {
			throw new IllegalArgumentException("No element with code '" + dependency.getElementCode() + "' was found in model '" + dependency.getScope().getModelName() + "'.");
		}
	}
	
	/**
	 * Resolve the given <code>ContributionDependency</code>.
	 * 
	 * @param dependency 
	 *			Dependency to resolve.
	 */
	private void resolve(final ContributionDependency dependency) {
		final String modelName = dependency.getScope().getModelName();
		if (modelName == null) {
			return;
		}

		final List<ProjectModel> projectModels = projectModelDAO.findProjectModelsWithName(dependency.getScope().getModelName());
		if (projectModels.isEmpty()) {
			LOGGER.error("Project model '" + modelName + "' was not found.");
		}
		else if (projectModels.size() == 1) {
			dependency.setProjectModelId(projectModels.get(0).getId());
		}
		else {
			LOGGER.error("Multiple project models were found with name '" + modelName + "'. The computation may behave incorrectly.");
		}
	}
	
}
