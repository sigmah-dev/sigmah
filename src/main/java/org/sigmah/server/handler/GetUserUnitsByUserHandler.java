package org.sigmah.server.handler;

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
		UserUnitDTO mainUserUnitDTO = mapper().map(mainUserUnit, UserUnitDTO.class);
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
