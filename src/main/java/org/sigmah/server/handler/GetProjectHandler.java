package org.sigmah.server.handler;

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
			dto = mapper().map(project, new ProjectDTO(), cmd.getMappingMode());
			projectMapper.fillBudget(project, dto);
		}
		dto.setCurrentAmendment(mapper().map(amendment, AmendmentDTO.class));
		if (project.getOrgUnit() != null) {
			dto.setOrgUnitId(project.getOrgUnit().getId());
		}

		return dto;
	}
}
