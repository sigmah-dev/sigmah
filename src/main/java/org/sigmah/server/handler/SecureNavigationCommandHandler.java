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

import org.sigmah.client.page.Page;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.security.SecureSessionValidator;
import org.sigmah.shared.command.SecureNavigationCommand;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.SecureNavigationResult;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import java.util.Set;

/**
 * Handler for {@link SecureNavigationCommand}.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SecureNavigationCommandHandler extends AbstractCommandHandler<SecureNavigationCommand, SecureNavigationResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SecureNavigationCommandHandler.class);

	private final SecureSessionValidator secureSessionValidator;

	private final OrgUnitDAO orgUnitDAO;
	private final ProjectDAO projectDAO;

	private final Mapper mapper;

	@Inject
	public SecureNavigationCommandHandler(final SecureSessionValidator secureSessionValidator, OrgUnitDAO orgUnitDAO,
		ProjectDAO projectDAO, final Mapper mapper) {
		this.secureSessionValidator = secureSessionValidator;
		this.orgUnitDAO = orgUnitDAO;
		this.projectDAO = projectDAO;
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SecureNavigationResult execute(final SecureNavigationCommand command, final UserExecutionContext context) throws CommandException {

		final User user = context.getUser();
		final Page page = command.getPage();

		final boolean granted = secureSessionValidator.isUserGranted(user, page);

		if (LOG.isTraceEnabled()) {
			if (granted) {
				LOG.trace("ACCESS GRANTED to page '{}' by user '{}'.", page, user);
			} else {
				LOG.trace("ACCESS UNAUTHORIZED to page '{}' by user '{}'.", page, user);
			}
		}

		Set<Integer> orgUnitIds = orgUnitDAO.getOrgUnitTreeIdsByUserId(user.getId());
		Set<Integer> memberOfProjectIds = projectDAO.findProjectIdsByTeamMemberIdAndOrgUnitIds(user.getId(), orgUnitIds);
		final Authentication authentication = Handlers.createAuthentication(user, context.getLanguage(), memberOfProjectIds, mapper);

		return new SecureNavigationResult(granted, authentication);
	}

}
