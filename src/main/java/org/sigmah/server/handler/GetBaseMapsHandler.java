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

import org.sigmah.server.dao.BaseMapDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetBaseMaps;
import org.sigmah.shared.command.result.BaseMapResult;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;

/**
 * Handler for the {@link GetBaseMaps} command
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetBaseMapsHandler extends AbstractCommandHandler<GetBaseMaps, BaseMapResult> {

	private final BaseMapDAO baseMapDAO;

	@Inject
	public GetBaseMapsHandler(BaseMapDAO baseMapDAO) {
		this.baseMapDAO = baseMapDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BaseMapResult execute(GetBaseMaps cmd, final UserExecutionContext context) throws CommandException {
		return new BaseMapResult(baseMapDAO.getBaseMaps());
	}
}
