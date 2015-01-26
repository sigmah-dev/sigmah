package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetLinkedProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetLinkedProjectsHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetLinkedProjectsAsyncHandler implements AsyncCommandHandler<GetLinkedProjects, ListResult<ProjectFundingDTO>> {

    private final ProjectAsyncDAO projectAsyncDAO;

	@Inject
	public GetLinkedProjectsAsyncHandler(ProjectAsyncDAO projectAsyncDAO) {
		this.projectAsyncDAO = projectAsyncDAO;
	}
    
    @Override
    public void execute(final GetLinkedProjects command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<ProjectFundingDTO>> callback) {
        projectAsyncDAO.get(command.getProjectId(), new AsyncCallback<ProjectDTO>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ProjectDTO project) {
                final ArrayList<ProjectFundingDTO> fundings = new ArrayList<ProjectFundingDTO>();
				
				switch(command.getType()) {
					case FUNDED_PROJECT:
						fundings.addAll(project.getFunded());
						break;
					case FUNDING_PROJECT:
						fundings.addAll(project.getFunding());
						break;
				}
				
                callback.onSuccess(new ListResult<ProjectFundingDTO>(fundings));
            }
        });
    }
    
}
