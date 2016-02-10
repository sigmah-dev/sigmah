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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.util.DomainFilters;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.ProjectMapper;
import org.sigmah.shared.command.GetProjectsFromId;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for {@link GetProjectsFromId} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectsFromIdHandler extends AbstractCommandHandler<GetProjectsFromId, ListResult<ProjectDTO>> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(GetProjectsFromIdHandler.class);

	private final ProjectMapper projectMapper;

	@Inject
	public GetProjectsFromIdHandler(final ProjectMapper mapper) {
		this.projectMapper = mapper;
	}

	/**
	 * Gets the projects list from the database.
	 * 
	 * @return A {@link ListResult} containing the {@link ProjectDTO} elements.
	 */
	@Override
	public ListResult<ProjectDTO> execute(final GetProjectsFromId cmd, final UserExecutionContext context) throws CommandException {

		// Disable the ActivityInfo filter on Userdatabase.
		DomainFilters.disableUserFilter(em());

		LOG.debug("Gets projects for command: '{}'.", cmd);

		final TypedQuery<Project> query = em().createQuery("SELECT p FROM Project p WHERE p.id IN (:ids)", Project.class);
		query.setParameter("ids", cmd.getIds());

		final List<Project> projects = query.getResultList();

		// ---------------
		// Mapping and return.
		// ---------------

		final List<ProjectDTO> projectDTOList = new ArrayList<ProjectDTO>();
		final IsMappingMode mappingMode = cmd.getMappingMode(); // May be null.

		if (mappingMode == ProjectDTO.Mode._USE_PROJECT_MAPPER) {
			// Using specific project mapper.
			for (final Project project : projects) {
				projectDTOList.add(projectMapper.map(project, true));
			}

		} else {
			// Using provided mapping mode.
			projectDTOList.addAll(mapper().mapCollection(projects, ProjectDTO.class, cmd.getMappingMode()));
		}

		LOG.debug("Found {} project(s).", projects.size());

		return new ListResult<>(projectDTOList);
	}

}
