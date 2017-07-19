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

import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.dao.OrganizationDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.UpdateOrganization;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for {@link UpdateOrganization} command.
 * 
 * @author Aurélien Ponçon
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class UpdateOrganizationHandler extends AbstractCommandHandler<UpdateOrganization, OrganizationDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UpdateOrganizationHandler.class);

	private final Mapper mapper;
	private final OrganizationDAO organizationDAO;

	@Inject
	public UpdateOrganizationHandler(final Mapper mapper, final OrganizationDAO organizationDAO) {
		this.mapper = mapper;
		this.organizationDAO = organizationDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganizationDTO execute(final UpdateOrganization cmd, final UserExecutionContext context) throws CommandException {

		final OrganizationDTO form = cmd.getOrganization();

		if (form == null || form.getId() == null || StringUtils.isBlank(form.getName())) {
			throw new CommandException("Invalid command arguments: '" + cmd + "'.");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Persisting organization data: '{}'.", cmd);
		}

		final Organization organization = organizationDAO.findById(form.getId());

		if (organization == null) {
			throw new CommandException("Organization with id '" + form.getId() + "' cannot be found.");
		}

		organization.setName(form.getName());
		organization.setLogo(form.getLogo());
		organization.setSolrcore_url(form.getSolrCoreUrl());

		organizationDAO.persist(organization, context.getUser());

		return mapper.map(organization, new OrganizationDTO());
	}

}
