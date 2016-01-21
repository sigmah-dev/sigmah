package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.server.dao.AdminDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.AdminEntity;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetAdminEntities;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.AdminEntityDTO;

import com.google.inject.Inject;

/**
 * handler for {@link GetAdminEntities} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetAdminEntitiesHandler extends AbstractCommandHandler<GetAdminEntities, ListResult<AdminEntityDTO>> {

	protected final AdminDAO adminDAO;

	@Inject
	public GetAdminEntitiesHandler(AdminDAO adminDAO) {
		this.adminDAO = adminDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<AdminEntityDTO> execute(final GetAdminEntities cmd, final UserExecutionContext context) throws CommandException {

		// List<AdminEntity> entities = adminDAO.find(cmd.getLevelId(), cmd.getParentId(), cmd.getActivityId());

		AdminDAO.Query query = adminDAO.query().level(cmd.getLevelId());

		if (cmd.getParentId() != null) {
			query.withParentEntityId(cmd.getParentId());
		}
		if (cmd.getActivityId() != null) {
			query.withSitesOfActivityId(cmd.getActivityId());
		}

		List<AdminEntity> entities = query.execute();

		List<AdminEntityDTO> models = new ArrayList<AdminEntityDTO>();

		for (AdminEntity entity : entities) {
			models.add(mapper().map(entity, new AdminEntityDTO()));
		}

		return new ListResult<AdminEntityDTO>(models);
	}
}
