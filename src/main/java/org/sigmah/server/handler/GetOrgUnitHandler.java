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


import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * {@link GetOrgUnit} command implementation.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetOrgUnitHandler extends AbstractCommandHandler<GetOrgUnit, OrgUnitDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetOrgUnitHandler.class);

	private final Mapper mapper;

	@Inject
	public GetOrgUnitHandler(Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitDTO execute(GetOrgUnit cmd, final UserExecutionContext context) throws CommandException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting org unit with id #{} from the database.", cmd.getId());
		}

		final OrgUnit orgUnit = em().find(OrgUnit.class, cmd.getId());

		// No org unit.
		if (orgUnit == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Org unit with id # doesn't exist.", cmd.getId());
			}
			return null;
		}

		// The user cannot see this org unit.
		if (!Handlers.isOrgUnitVisible(orgUnit, context.getUser())) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("User cannot see org unit id #{}, returns null.", cmd.getId());
			}
			return null;
		}

		return mapper.map(orgUnit, OrgUnitDTO.class);
	}

}
