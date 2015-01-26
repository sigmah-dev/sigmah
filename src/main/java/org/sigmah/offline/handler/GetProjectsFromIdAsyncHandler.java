package org.sigmah.offline.handler;

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
