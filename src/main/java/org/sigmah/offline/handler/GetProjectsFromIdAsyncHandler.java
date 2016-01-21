package org.sigmah.offline.handler;

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

import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetProjectsFromId;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetProjectsFromIdHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class GetProjectsFromIdAsyncHandler implements AsyncCommandHandler<GetProjectsFromId, ListResult<ProjectDTO>> {

	private final ProjectAsyncDAO projectAsyncDAO;

	@Inject
	public GetProjectsFromIdAsyncHandler(ProjectAsyncDAO projectAsyncDAO) {
		this.projectAsyncDAO = projectAsyncDAO;
	}
	
	@Override
	public void execute(GetProjectsFromId command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<ProjectDTO>> callback) {
		projectAsyncDAO.getProjectsByIds(command.getIds(), callback);
	}
}
