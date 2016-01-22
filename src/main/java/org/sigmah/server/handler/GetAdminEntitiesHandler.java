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
