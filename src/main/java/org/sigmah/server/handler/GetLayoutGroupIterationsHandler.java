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
import org.sigmah.server.dao.LayoutGroupIterationDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;

public class GetLayoutGroupIterationsHandler extends AbstractCommandHandler<GetLayoutGroupIterations, ListResult<LayoutGroupIterationDTO>> {

	private final LayoutGroupIterationDAO layoutGroupIterationDAO;

	@Inject
	public GetLayoutGroupIterationsHandler(LayoutGroupIterationDAO layoutGroupIterationDAO) {
		this.layoutGroupIterationDAO = layoutGroupIterationDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<LayoutGroupIterationDTO> execute(final GetLayoutGroupIterations cmd, final UserExecutionContext context) throws CommandException {
    return new ListResult<>(mapper().mapCollection(layoutGroupIterationDAO.findByLayoutGroupAndContainer(cmd.getLayoutGroupId(), cmd.getContainerId()), LayoutGroupIterationDTO.class));
	}
}
