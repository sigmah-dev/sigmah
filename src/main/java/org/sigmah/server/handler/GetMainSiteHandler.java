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

import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetMainSite;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.SiteDTO;

/**
 * Handler for the {@link GetMainSiteHandler} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetMainSiteHandler extends AbstractCommandHandler<GetMainSite, SiteDTO> {

	@Override
	protected SiteDTO execute(GetMainSite command, UserDispatch.UserExecutionContext context) throws CommandException {
		final Project project = em().find(Project.class, command.getProjectId());
		
		if(project == null) {
			throw new IllegalArgumentException("Project '" + command.getProjectId() + "' was not been found.");
		}
		
		return mapper().map(project.getMainSite(), new SiteDTO());
	}
	
}
