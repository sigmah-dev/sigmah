package org.sigmah.server.handler;

import com.google.inject.Inject;

import java.util.List;

import org.sigmah.server.dao.FrameworkFulfillmentDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.FrameworkFulfillment;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetFrameworkFulfillmentsByProjectModelId;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.FrameworkFulfillmentDTO;

public class GetFrameworkFulfillmentsByProjectModelIdHandler extends AbstractCommandHandler<GetFrameworkFulfillmentsByProjectModelId, ListResult<FrameworkFulfillmentDTO>> {
	private final FrameworkFulfillmentDAO frameworkFulfillmentDAO;

	@Inject
	GetFrameworkFulfillmentsByProjectModelIdHandler(FrameworkFulfillmentDAO frameworkFulfillmentDAO) {
		this.frameworkFulfillmentDAO = frameworkFulfillmentDAO;
	}

	@Override
	protected ListResult<FrameworkFulfillmentDTO> execute(GetFrameworkFulfillmentsByProjectModelId command, UserDispatch.UserExecutionContext context) throws CommandException {
		List<FrameworkFulfillment> frameworkFulfillments = frameworkFulfillmentDAO.findByProjectModelId(command.getProjectModelId());
		return new ListResult<>(mapper().mapCollection(frameworkFulfillments, FrameworkFulfillmentDTO.class));
	}
}
