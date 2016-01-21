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
