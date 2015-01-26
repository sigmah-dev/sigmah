package org.sigmah.server.handler;

import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectModel;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for {@link GetProjectModel} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectModelHandler extends AbstractCommandHandler<GetProjectModel, ProjectModelDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetProjectModelHandler.class);

	/**
	 * Injected {@link ProjectModelDAO}.
	 */
	@Inject
	private ProjectModelDAO projectModelDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectModelDTO execute(final GetProjectModel cmd, final UserExecutionContext context) throws CommandException {

		final Integer modelId = cmd.getModelId();
		LOG.debug("Retrieving project model with id '{}'.", modelId);

		final ProjectModel model = projectModelDAO.findById(modelId);

		if (model == null) {
			LOG.debug("Project model with id #{} does not exist.", modelId);
			return null;
		}

		LOG.debug("Found project model with id #{}.", modelId);

		return mapper().map(model, ProjectModelDTO.class, cmd.getMappingMode());
	}

}
