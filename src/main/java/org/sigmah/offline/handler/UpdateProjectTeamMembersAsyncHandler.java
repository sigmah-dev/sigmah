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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ProjectTeamMembersAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.UpdateProjectTeamMembers;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.TeamMembersResult;

public class UpdateProjectTeamMembersAsyncHandler implements AsyncCommandHandler<UpdateProjectTeamMembers, TeamMembersResult>,
	DispatchListener<UpdateProjectTeamMembers, TeamMembersResult> {

	private final ProjectTeamMembersAsyncDAO projectTeamMembersAsyncDAO;
	private final UpdateDiaryAsyncDAO updateDiaryAsyncDAO;

	public UpdateProjectTeamMembersAsyncHandler(ProjectTeamMembersAsyncDAO projectTeamMembersAsyncDAO, UpdateDiaryAsyncDAO updateDiaryAsyncDAO) {
		this.projectTeamMembersAsyncDAO = projectTeamMembersAsyncDAO;
		this.updateDiaryAsyncDAO = updateDiaryAsyncDAO;
	}

	@Override
	public void execute(final UpdateProjectTeamMembers command, OfflineExecutionContext executionContext,
											final AsyncCallback<TeamMembersResult> callback) {
		updateDiaryAsyncDAO.saveOrUpdate(command);

		projectTeamMembersAsyncDAO.get(command.getProjectId(), new CommandResultHandler<TeamMembersResult>() {
			@Override
			protected void onCommandSuccess(TeamMembersResult result) {
				TeamMembersResult teamMembersResult = new TeamMembersResult();
				teamMembersResult.setProjectId(command.getProjectId());
				teamMembersResult.setProjectManager(result.getProjectManager());
				teamMembersResult.setTeamMembers(command.getTeamMembers());
				teamMembersResult.setTeamMemberProfiles(command.getTeamMemberProfiles());

				RequestManager<TeamMembersResult> requestManager = new RequestManager<TeamMembersResult>(teamMembersResult, callback);
				updateTeamMembers(teamMembersResult, requestManager);
			}
		});
	}

	private <M> void updateTeamMembers(final TeamMembersResult teamMembersResult, final RequestManager<M> requestManager) {
		final int futureId = requestManager.prepareRequest();

		projectTeamMembersAsyncDAO.saveOrUpdate(teamMembersResult, new RequestManagerCallback<M, TeamMembersResult>(requestManager, futureId) {
			@Override
			public void onRequestSuccess(TeamMembersResult result) {
				// NOOP
			}
		});

		requestManager.ready();
	}

	@Override
	public void onSuccess(UpdateProjectTeamMembers command, TeamMembersResult result, Authentication authentication) {
		projectTeamMembersAsyncDAO.saveOrUpdate(result);
	}
}
