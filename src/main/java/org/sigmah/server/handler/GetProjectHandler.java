package org.sigmah.server.handler;

import org.sigmah.server.dao.AmendmentDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Amendment;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.security.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import org.sigmah.server.handler.util.ProjectMapper;

/**
 * {@link GetProject} corresponding handler implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectHandler extends AbstractCommandHandler<GetProject, ProjectDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetProjectHandler.class);

	/**
	 * Injected {@link ProjectDAO}.
	 */
	@Inject
	private ProjectDAO projectDAO;

	/**
	 * Injected {@link AmendmentDAO}.
	 */
	@Inject
	private AmendmentDAO amendmentDAO;
	
	/**
	 * Injected project mapper.
	 */
	@Inject
	private ProjectMapper projectMapper;

	/**
	 * Gets a project from the database and maps it into a {@link ProjectDTO} object.
	 * 
	 * @return The {@link ProjectDTO} object.
	 */
	@Override
	public ProjectDTO execute(final GetProject cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Getting projet from the database for command: '{}'.", cmd);

		final Integer projectId = cmd.getProjectId();

		// --
		// Retrieving project.
		// --

		final Project project;
		final Amendment amendment;

		if (cmd.getAmendmentId() == null) {
			project = projectDAO.findById(projectId);
			amendment = null;

		} else {
			project = amendmentDAO.findAmendmentProject(cmd.getAmendmentId());
			amendment = amendmentDAO.findById(cmd.getAmendmentId());
		}

		if (project == null) {
			LOG.debug("No project exists for id #{}.", projectId);
			throw new CommandException("No project can be found for id #" + projectId);
		}

		if (!Handlers.isProjectVisible(project, context.getUser())) {
			LOG.debug("User is not authorized to access project with id #{}.", projectId);
			throw new UnauthorizedAccessException("User is not authorized to access project with id #" + projectId);
		}

		// --
		// Processing project mapping.
		// --

		final ProjectDTO dto;
		if(cmd.getMappingMode() == ProjectDTO.Mode._USE_PROJECT_MAPPER) {
			dto = projectMapper.map(project, true);
		} else {
			dto = mapper().map(project, ProjectDTO.class, cmd.getMappingMode());
			projectMapper.fillBudget(project, dto);
		}
		dto.setCurrentAmendment(mapper().map(amendment, AmendmentDTO.class));

		for (final OrgUnit orgUnit : project.getPartners()) {
			dto.setOrgUnitId(orgUnit.getId());
			break;
		}

		return dto;
	}
}
