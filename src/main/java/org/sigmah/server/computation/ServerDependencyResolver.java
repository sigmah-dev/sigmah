package org.sigmah.server.computation;

import com.google.inject.Inject;
import java.util.List;
import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.shared.computation.DependencyResolver;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.computation.dependency.ContributionDependency;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.DependencyVisitor;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class ServerDependencyResolver implements DependencyResolver {
	
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
	
	private void resolve(final CollectionDependency dependency) {
		final List<ProjectModel> projectModels = projectModelDAO.findProjectModelsWithName(dependency.getScope().getModelName());
				
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
					return;
				}
				dependency.setFlexibleElement(element);
			}
		}
	}
	
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
