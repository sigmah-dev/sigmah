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

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUsersWithProfiles;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves the organization users list.
 * 
 * @author nrebiai (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetUsersWithProfilesHandler extends AbstractCommandHandler<GetUsersWithProfiles, ListResult<UserDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetUsersWithProfilesHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<UserDTO> execute(final GetUsersWithProfiles cmd, final UserExecutionContext context) throws CommandException {

		// --
		// Retrieves users.
		// --

		final TypedQuery<User> query = em().createQuery("SELECT u FROM User u WHERE u.organization = :org ORDER BY u.name", User.class);
		query.setParameter("org", context.getUser().getOrganization());

		final List<User> users = query.getResultList();

		// --
		// Builds result.
		// --

		final List<UserDTO> dtos = mapper().mapCollection(users, UserDTO.class, UserDTO.Mode.WITH_BASE_ORG_UNIT_AND_BASE_PROFILES);

		LOG.debug("Found {} users.", dtos.size());

		return new ListResult<UserDTO>(dtos);
	}

}
