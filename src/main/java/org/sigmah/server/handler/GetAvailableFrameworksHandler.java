package org.sigmah.server.handler;

import com.google.inject.Inject;

import java.util.List;

import org.sigmah.server.dao.FrameworkDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Framework;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetAvailableFrameworks;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.FrameworkDTO;

public class GetAvailableFrameworksHandler extends AbstractCommandHandler<GetAvailableFrameworks, ListResult<FrameworkDTO>> {
	private final FrameworkDAO frameworkDAO;

	@Inject
	GetAvailableFrameworksHandler(FrameworkDAO frameworkDAO) {
		this.frameworkDAO = frameworkDAO;
	}

	@Override
	protected ListResult<FrameworkDTO> execute(GetAvailableFrameworks command, UserDispatch.UserExecutionContext context)
		throws CommandException {
		List<Framework> availableFrameworks = frameworkDAO.findAvailableFrameworksForOrganizationId(context.getUser().getOrganization().getId());
		return new ListResult<>(mapper().mapCollection(availableFrameworks, FrameworkDTO.class));
	}
}
