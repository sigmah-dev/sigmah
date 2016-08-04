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

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetUsersByOrganization} command
 *
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetUsersByOrganizationHandler extends AbstractCommandHandler<GetUsersByOrganization, ListResult<UserDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetUsersByOrganizationHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<UserDTO> execute(final GetUsersByOrganization cmd, final UserExecutionContext context) throws CommandException {

		final List<UserDTO> userDTOList = new ArrayList<UserDTO>();

		LOG.debug("Gets users for organization #{}.", cmd.getOrganizationId());

		final Integer userId = cmd.getUserId();

		if (userId == null) {

			final TypedQuery<User> q = em().createQuery("SELECT u FROM User u WHERE u.organization.id = :orgid", User.class);
			q.setParameter("orgid", cmd.getOrganizationId());

			final List<User> users = q.getResultList();

			if (users != null) {
				for (final User u : users) {
					final UserDTO userDTO = mapper().map(u, new UserDTO(), cmd.getMappingMode());
					userDTO.generateCompleteName();
					userDTOList.add(userDTO);
				}
			}

		} else {

			final TypedQuery<User> q = em().createQuery("SELECT u FROM User u WHERE u.id = :userid AND u.organization.id = :orgid", User.class);
			q.setParameter("userid", userId);
			q.setParameter("orgid", cmd.getOrganizationId());

			try {
				final User u = q.getSingleResult();
				final UserDTO userDTO = mapper().map(u, new UserDTO(), cmd.getMappingMode());
				userDTO.generateCompleteName();
				userDTOList.add(userDTO);
			} catch (NoResultException e) {
				// nothing.
			}
		}

		LOG.debug("Found {} users.", userDTOList.size());

		return new ListResult<UserDTO>(userDTOList);
	}
}
