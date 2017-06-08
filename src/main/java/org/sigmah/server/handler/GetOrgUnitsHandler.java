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

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link GetOrgUnit} command implementation.
 */
public class GetOrgUnitsHandler extends AbstractCommandHandler<GetOrgUnits, ListResult<OrgUnitDTO>> {
	private static final Logger LOG = LoggerFactory.getLogger(GetOrgUnitsHandler.class);

	private final Mapper mapper;
	private final OrgUnitDAO orgUnitDAO;

	@Inject
	public GetOrgUnitsHandler(Mapper mapper, OrgUnitDAO orgUnitDAO) {
		this.mapper = mapper;
		this.orgUnitDAO = orgUnitDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<OrgUnitDTO> execute(GetOrgUnits cmd, final UserExecutionContext context) throws CommandException {
		List<OrgUnit> orgUnits;
		if (cmd.getOrgUnitIds() == null) {
			orgUnits = orgUnitDAO.findByOrganizationId(context.getUser().getOrganization().getId());
		} else {
			orgUnits = orgUnitDAO.findByIds(cmd.getOrgUnitIds());
		}

		List<OrgUnitDTO> orgUnitDTOs = new ArrayList<>();
		for (OrgUnit orgUnit : orgUnits) {
			OrgUnitDTO map = mapper.map(orgUnit, new OrgUnitDTO());
			orgUnitDTOs.add(map);
		}

		return new ListResult<>(orgUnitDTOs);
	}

}
