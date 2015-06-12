package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dispatch.NotCachedException;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetProjectsHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetProjectAsyncHandler implements AsyncCommandHandler<GetProject, ProjectDTO>, DispatchListener<GetProject, ProjectDTO> {

	private final ProjectAsyncDAO projectAsyncDAO;

	@Inject
	public GetProjectAsyncHandler(ProjectAsyncDAO projectAsyncDAO) {
		this.projectAsyncDAO = projectAsyncDAO;
	}
	
	@Override
	public void execute(final GetProject command, OfflineExecutionContext executionContext, final AsyncCallback<ProjectDTO> callback) {
		projectAsyncDAO.get(command.getProjectId(), new AsyncCallback<ProjectDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(ProjectDTO result) {
				if(result != null) {
					callback.onSuccess(result);
				} else {
					callback.onFailure(new NotCachedException("Requested project '" + command.getProjectId() + "' was not found in the local database."));
				}
			}
		});
	}

	@Override
	public void onSuccess(GetProject command, ProjectDTO result, Authentication authentication) {
        // TODO: Do something better. Maybe mix data from the database and from the given DTO ? Switch on the mapping mode ?
        if(result.getCurrentMappingMode() == null) {
            projectAsyncDAO.saveOrUpdate(result);
        }
	}
}
