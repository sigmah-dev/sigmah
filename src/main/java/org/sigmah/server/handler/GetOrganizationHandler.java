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

import org.sigmah.server.dao.OrganizationDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * {@link GetOrganization} command implementation.
 * 
 * @author tmi
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetOrganizationHandler extends AbstractCommandHandler<GetOrganization, OrganizationDTO> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(GetOrganizationHandler.class);

	private OrganizationDAO organizationDAO;
	private final Mapper mapper;

	@Inject
	public GetOrganizationHandler(final OrganizationDAO organizationDAO, final Mapper mapper) {
		this.organizationDAO = organizationDAO;
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganizationDTO execute(final GetOrganization cmd, final UserExecutionContext context) throws CommandException {

		final Integer organizationId = cmd.getId();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting organization id#{} from the database.", organizationId);
		}

		final Organization organization = organizationDAO.findById(organizationId);

		final OrganizationDTO result;

		if (organization == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Organization id#{} doesn't exist.", organizationId);
			}
			result = null;

		} else {
			result = mapper.map(organization, OrganizationDTO.class, cmd.getMode());
		}

		return result;
	}

}
