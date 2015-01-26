package org.sigmah.server.handler;

import java.util.List;

import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetTestProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for {@link GetTestProjects} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Tom Miette (tmiette@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetTestProjectsHandler extends AbstractCommandHandler<GetTestProjects, ListResult<ProjectDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetTestProjectsHandler.class);

	/**
	 * Injected {@link ProjectDAO}.
	 */
	@Inject
	private ProjectDAO projectDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProjectDTO> execute(final GetTestProjects cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Retrieving active draft project(s).");

		// Retrieves all active draft projects.
		final List<Project> projects = projectDAO.findDraftProjects(context.getUser().getId());

		LOG.debug("Found {} active draft project(s).", projects.size());

		return new ListResult<ProjectDTO>(mapper().mapCollection(projects, ProjectDTO.class, ProjectDTO.Mode.WITH_USER));
	}

}
