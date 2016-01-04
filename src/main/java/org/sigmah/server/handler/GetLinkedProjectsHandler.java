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

import javax.persistence.Query;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.handler.util.ProjectMapper;
import org.sigmah.shared.command.GetLinkedProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;

import com.google.inject.Inject;

/**
 * Handler for the {@link GetLinkedProjects} command.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetLinkedProjectsHandler extends AbstractCommandHandler<GetLinkedProjects, ListResult<ProjectFundingDTO>> {

	/**
	 * Injected project mapper.
	 */
	@Inject
	private ProjectMapper projectMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProjectFundingDTO> execute(final GetLinkedProjects cmd, final UserExecutionContext context) throws CommandException {

		final Integer projectId = cmd.getProjectId();
		final LinkedProjectType type = cmd.getType();
		final IsMappingMode mappingMode = cmd.getMappingMode();

		if (projectId == null || type == null) {
			throw new CommandException("Invalid command arguments.");
		}

		final String queryTerm;
		switch (type) {
			case FUNDING_PROJECT:
				queryTerm = "funding";
				break;

			case FUNDED_PROJECT:
				queryTerm = "funded";
				break;

			default:
				throw new CommandException("Invalid linked project type.");
		}

		final Query query = em().createQuery("SELECT p." + queryTerm + " FROM Project p WHERE p.id = :projectId");
		query.setParameter("projectId", projectId);

		@SuppressWarnings("unchecked")
		final List<ProjectFunding> results = query.getResultList();

		final List<ProjectFundingDTO> dtos = new ArrayList<ProjectFundingDTO>();
		for (final ProjectFunding pf : results) {
			if (!Handlers.isProjectVisible(pf.getFunded(), context.getUser())) {
				continue;
			}
			if (!Handlers.isProjectVisible(pf.getFunding(), context.getUser())) {
				continue;
			}

			final ProjectFundingDTO pfDTO = new ProjectFundingDTO();
			pfDTO.setId(pf.getId());
			pfDTO.setPercentage(pf.getPercentage());

			if (mappingMode == ProjectDTO.Mode._USE_PROJECT_MAPPER) {
				pfDTO.setFunding(projectMapper.map(pf.getFunding(), false));
				pfDTO.setFunded(projectMapper.map(pf.getFunded(), false));

			} else {
				pfDTO.setFunding(mapper().map(pf.getFunding(), new ProjectDTO(), mappingMode));
				pfDTO.setFunded(mapper().map(pf.getFunded(), new ProjectDTO(), mappingMode));
			}

			dtos.add(pfDTO);
		}

		return new ListResult<>(dtos);
	}
}
