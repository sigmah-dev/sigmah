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
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUserDatabase;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDatabaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetUserDatabase} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetUserDatabaseHandler extends AbstractCommandHandler<GetUserDatabase, ListResult<UserDatabaseDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetUserDatabaseHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<UserDatabaseDTO> execute(final GetUserDatabase cmd, final UserExecutionContext context) throws CommandException {

		final List<UserDatabaseDTO> userDatabaseDTOList = new ArrayList<UserDatabaseDTO>();

		// Creates selection query.
		final TypedQuery<UserDatabase> query = em().createQuery("SELECT u FROM UserDatabase u  WHERE u.owner = :userId ORDER BY u.id", UserDatabase.class);
		query.setParameter("userId", context.getUser().getId());

		// Gets all users entities.
		final List<UserDatabase> dbs = query.getResultList();

		// Mapping (entity → dto).
		if (dbs != null) {
			for (final UserDatabase oneDB : dbs) {
				userDatabaseDTOList.add(mapper().map(oneDB, new UserDatabaseDTO()));
			}
		}

		LOG.debug("Found {} databases.", userDatabaseDTOList.size());

		return new ListResult<UserDatabaseDTO>(userDatabaseDTOList);
	}
}
