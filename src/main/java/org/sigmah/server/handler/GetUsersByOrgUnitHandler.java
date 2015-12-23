package org.sigmah.server.handler;

import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUsersByOrgUnit;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
			UserDTO userDTO = mapper().map(user, UserDTO.class, UserDTO.Mode.WITH_BASE_PROFILES);
			userDTO.generateCompleteName();
			userDTOs.add(userDTO);
		}
		return new ListResult<>(userDTOs);
	}
}
