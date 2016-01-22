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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectModels;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Retrieves the list of project models available to the user.
 * 
 * @author tmi (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectModelsHandler extends AbstractCommandHandler<GetProjectModels, ListResult<ProjectModelDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetProjectModelsHandler.class);

	/**
	 * Injected {@link ProjectModelDAO}.
	 */
	@Inject
	private ProjectModelDAO projectModelDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProjectModelDTO> execute(final GetProjectModels cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Retrieving project models for command: '{}'.", cmd);

		final List<ProjectModelStatus> statusFilters;

		if (ArrayUtils.isEmpty(cmd.getStatusFilters())) {
			// Default filter (visible status).
			statusFilters = new ArrayList<>();
			statusFilters.add(ProjectModelStatus.USED);
			statusFilters.add(ProjectModelStatus.READY);

		} else {
			// Provided filter.
			statusFilters = Arrays.asList(cmd.getStatusFilters());
		}

		// Filters only models visible to the authenticated user's organization.
		final Organization organization = context.getUser().getOrganization();
		final Integer organizationId = organization != null ? organization.getId() : Integer.MIN_VALUE;
		final List<ProjectModel> models = projectModelDAO.findProjectModelsVisibleToOrganization(organizationId, statusFilters);

		LOG.debug("Found {} project models.", models.size());

		return new ListResult<ProjectModelDTO>(mapper().mapCollection(models, ProjectModelDTO.class, cmd.getMappingMode()));
	}

}
