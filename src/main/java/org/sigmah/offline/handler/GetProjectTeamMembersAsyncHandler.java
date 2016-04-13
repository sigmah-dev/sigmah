package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ProjectTeamMembersAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetProjectTeamMembers;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.TeamMembersResult;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetProjectTeamMembersHandler}.
 * Used when the user is offline.
 */
public class GetProjectTeamMembersAsyncHandler implements AsyncCommandHandler<GetProjectTeamMembers, TeamMembersResult>,
	DispatchListener<GetProjectTeamMembers, TeamMembersResult> {

	private final ProjectTeamMembersAsyncDAO projectTeamMembersAsyncDAO;

	@Inject GetProjectTeamMembersAsyncHandler(ProjectTeamMembersAsyncDAO projectTeamMembersAsyncDAO) {
		this.projectTeamMembersAsyncDAO = projectTeamMembersAsyncDAO;
	}

	@Override
	public void execute(GetProjectTeamMembers command, OfflineExecutionContext executionContext, AsyncCallback<TeamMembersResult> callback) {
		projectTeamMembersAsyncDAO.get(command.getProjectId(), callback);
	}

	@Override
	public void onSuccess(GetProjectTeamMembers command, TeamMembersResult result, Authentication authentication) {
		if(result != null) {
			projectTeamMembersAsyncDAO.saveOrUpdate(result);
		}
	}
}
