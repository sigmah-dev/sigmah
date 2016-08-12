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
