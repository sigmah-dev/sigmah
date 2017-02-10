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
import java.util.Set;

import javax.inject.Inject;

import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUsersByOrgUnit;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

public class GetUsersByOrgUnitHandler extends AbstractCommandHandler<GetUsersByOrgUnit, ListResult<UserDTO>> {
	private final OrgUnitDAO orgUnitDAO;
	private final UserDAO userDAO;

	@Inject
	GetUsersByOrgUnitHandler(OrgUnitDAO orgUnitDAO, UserDAO userDAO) {
		this.orgUnitDAO = orgUnitDAO;
		this.userDAO = userDAO;
	}

	@Override
	public ListResult<UserDTO> execute(final GetUsersByOrgUnit command, final UserExecutionContext context) throws CommandException {
		Set<Integer> orgUnitIds = orgUnitDAO.getOrgUnitTreeIds(command.getOrgUnitId());
		List<User> users = userDAO.findUsersByOrgUnitIds(orgUnitIds, command.getWithoutUserIds());
		List<UserDTO> userDTOs = new ArrayList<>();
		for (User user : users) {
			UserDTO userDTO = mapper().map(user, new UserDTO(), UserDTO.Mode.WITH_BASE_PROFILES);
			userDTO.generateCompleteName();

			// Currently loaded OrgUnits doesn't have a link to their parent and to their children
			// Let's force the UserDTO to have these links
			if (user.getMainOrgUnitWithProfiles() != null) {
				userDTO.setMainOrgUnit(mapper().map(user.getMainOrgUnitWithProfiles().getOrgUnit(), OrgUnitDTO.class));
			}
			List<OrgUnit> secondaryOrgUnits = user.getSecondaryOrgUnits();
			if (secondaryOrgUnits != null && !secondaryOrgUnits.isEmpty()) {
				userDTO.setSecondaryOrgUnits(mapper().mapCollection(secondaryOrgUnits, OrgUnitDTO.class));
			}

			userDTOs.add(userDTO);
		}
		return new ListResult<>(userDTOs);
	}
}
