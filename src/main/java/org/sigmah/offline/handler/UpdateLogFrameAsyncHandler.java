package org.sigmah.offline.handler;

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
		updateLogFrame(command.getProjectId(), result, new RequestManager<Void>(null, null));
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
