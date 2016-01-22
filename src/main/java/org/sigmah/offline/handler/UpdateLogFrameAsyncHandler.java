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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.UpdateLogFrameHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UpdateLogFrameAsyncHandler implements AsyncCommandHandler<UpdateLogFrame, LogFrameDTO>, DispatchListener<UpdateLogFrame, LogFrameDTO> {

	@Inject
	private ProjectAsyncDAO projectAsyncDAO;
	
	@Inject
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
	
	@Override
	public void execute(final UpdateLogFrame command, OfflineExecutionContext executionContext, AsyncCallback<LogFrameDTO> callback) {
		updateDiaryAsyncDAO.saveOrUpdate(command);
		
		final RequestManager<LogFrameDTO> requestManager = new RequestManager<LogFrameDTO>(command.getLogFrame(), callback);
		updateLogFrame(command.getProjectId(), command.getLogFrame(), requestManager);
	}

	@Override
	public void onSuccess(UpdateLogFrame command, LogFrameDTO result, Authentication authentication) {
		if(result != null) {
			updateLogFrame(command.getProjectId(), result, new RequestManager<Void>(null, null));
		}
	}
	
	private <M> void updateLogFrame(final Integer projectId, final LogFrameDTO logFrame, final RequestManager<M> requestManager) {
		final int futureId = requestManager.prepareRequest();
		
		projectAsyncDAO.get(projectId, new RequestManagerCallback<M, ProjectDTO>(requestManager) {

			@Override
			public void onRequestSuccess(ProjectDTO result) {
				if(result != null) {
					result.setLogFrame(logFrame);
					projectAsyncDAO.saveOrUpdate(result, new RequestManagerCallback<M, ProjectDTO>(requestManager, futureId) {

						@Override
						public void onRequestSuccess(ProjectDTO result) {
							// Done
						}
						
					});
					
				} else {
					Log.warn("Project '" + projectId + "' was not found in the local database.");
					requestManager.setRequestSuccess(futureId);
				}
			}
			
		});
		
		requestManager.ready();
	}
}
