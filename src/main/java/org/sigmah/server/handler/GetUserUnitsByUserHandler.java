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

import javax.inject.Inject;

import org.sigmah.server.dao.UserUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.profile.OrgUnitProfile;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUserUnitsByUser;
import org.sigmah.shared.command.result.UserUnitsResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserUnitDTO;

public class GetUserUnitsByUserHandler extends AbstractCommandHandler<GetUserUnitsByUser, UserUnitsResult> {
	private final UserUnitDAO userUnitDAO;

	@Inject
	GetUserUnitsByUserHandler(UserUnitDAO userUnitDAO) {
		this.userUnitDAO = userUnitDAO;
	}

	@Override
	protected UserUnitsResult execute(GetUserUnitsByUser command, UserDispatch.UserExecutionContext context) throws CommandException {
		OrgUnitProfile mainUserUnit = userUnitDAO.findMainOrgUnitProfileByUserId(command.getUserId());
		UserUnitDTO mainUserUnitDTO = mapper().map(mainUserUnit, new UserUnitDTO());
		mainUserUnitDTO.setMainUserUnit(true);

		List<OrgUnitProfile> secondaryUserUnits = userUnitDAO.findSecondaryOrgUnitProfilesByUserId(command.getUserId());
		List<UserUnitDTO> secondaryUserUnitsDTO = new ArrayList<UserUnitDTO>(secondaryUserUnits.size());
		for (OrgUnitProfile secondaryUserUnit : secondaryUserUnits) {
			UserUnitDTO secondaryUserUnitDTO = mapper().map(secondaryUserUnit, UserUnitDTO.class);
			secondaryUserUnitDTO.setMainUserUnit(false);
			secondaryUserUnitsDTO.add(secondaryUserUnitDTO);
		}

		UserUnitsResult userUnitsResult = new UserUnitsResult();
		userUnitsResult.setUserId(command.getUserId());
		userUnitsResult.setMainUserUnit(mainUserUnitDTO);
		userUnitsResult.setSecondaryUserUnits(secondaryUserUnitsDTO);
		return userUnitsResult;
	}
}
