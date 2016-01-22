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
import org.sigmah.server.dao.OrgUnitModelDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetOrgUnitModels;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import org.sigmah.server.domain.OrgUnit;

/**
 * Retrieves the list of org unit models available to the user.
 * 
 * @author nrebiai (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetOrgUnitModelsHandler extends AbstractCommandHandler<GetOrgUnitModels, ListResult<OrgUnitModelDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetOrgUnitModelsHandler.class);

	/**
	 * Injected {@link OrgUnitModelDAO}.
	 */
	@Inject
	private OrgUnitModelDAO orgUnitModelDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<OrgUnitModelDTO> execute(final GetOrgUnitModels cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Retrieving OrgUnit models for command: '{}'.", cmd);

		final Integer organizationId = context.getUser().getOrganization().getId();
		final Integer topModelId;
		
		final OrgUnit rootOrgUnit = context.getUser().getOrganization().getRoot();
		if (rootOrgUnit != null) {
			topModelId = rootOrgUnit.getOrgUnitModel().getId();
		} else {
			topModelId = null;
		}

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

		final List<OrgUnitModel> models = orgUnitModelDAO.findOrgUnitModelsVisibleToOrganization(organizationId, statusFilters);

		final List<OrgUnitModelDTO> orgUnitModelDTOList = new ArrayList<OrgUnitModelDTO>();

		// Mapping (entity -> dto).
		for (final OrgUnitModel model : models) {
			final OrgUnitModelDTO dto = mapper().map(model, new OrgUnitModelDTO(), cmd.getMappingMode());
			dto.setTopOrgUnitModel(model.getId().equals(topModelId));
			orgUnitModelDTOList.add(dto);
		}

		LOG.debug("Found {} org unit models.", orgUnitModelDTOList.size());

		return new ListResult<OrgUnitModelDTO>(orgUnitModelDTOList);
	}

}
