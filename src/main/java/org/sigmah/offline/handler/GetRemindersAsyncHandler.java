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
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.ReminderAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetRemindersHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetRemindersAsyncHandler implements AsyncCommandHandler<GetReminders, ListResult<ReminderDTO>>, DispatchListener<GetReminders, ListResult<ReminderDTO>> {


	private ReminderAsyncDAO reminderAsyncDAO;

	
	private ProjectAsyncDAO projectAsyncDAO;
	
	public GetRemindersAsyncHandler(ReminderAsyncDAO reminderAsyncDAO, ProjectAsyncDAO projectAsyncDAO) {
		this.reminderAsyncDAO = reminderAsyncDAO;
		this.projectAsyncDAO = projectAsyncDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(GetReminders command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<ReminderDTO>> callback) {
		if(command.getProjectId() == null) {
			loadAllReminders(command, callback);
		} else {
			loadProjectReminders(command.getProjectId(), callback);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(GetReminders command, ListResult<ReminderDTO> result, Authentication authentication) {
		reminderAsyncDAO.saveAll(result, null);
	}
	
	private void loadAllReminders(GetReminders command, final AsyncCallback<ListResult<ReminderDTO>> callback) {
		Set<Integer> orgUnitIds = command.getOrgUnitIds();
		projectAsyncDAO.getProjectsByIds(orgUnitIds, new AsyncCallback<ListResult<ProjectDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(final ListResult<ProjectDTO> projectsResult) {
				final List<ReminderDTO> reminders = new ArrayList<ReminderDTO>();
				for (ProjectDTO projectDTO : projectsResult.getData()) {
					reminderAsyncDAO.getAllByParentListId(projectDTO.getRemindersList().getId(), new AsyncCallback<List<ReminderDTO>>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(List<ReminderDTO> remindersResult) {
							final RequestManager<ListResult<ReminderDTO>> manager = new RequestManager<ListResult<ReminderDTO>>(
									new ListResult<ReminderDTO>(reminders), callback);

							for (final ReminderDTO reminderDTO : remindersResult) {
								if (reminderDTO.getCompletionDate() != null) {
									continue;
								}
								projectAsyncDAO.getByIndexWithoutDependencies("remindersListId", reminderDTO.getParentListId(), new RequestManagerCallback<ListResult<ReminderDTO>, ProjectDTO>(manager) {
									@Override
									public void onRequestSuccess(ProjectDTO result) {
										if (result == null) {
											return;
										}

										reminderDTO.setProjectId(result.getId());
										reminderDTO.setProjectCode(result.getName());
										reminderDTO.setProjectName(result.getFullName());
									}
								});

								reminders.add(reminderDTO);
							}

							manager.ready();
						}
					});
				}
			}
		});
	}

	private void loadProjectReminders(final int projectId, final AsyncCallback<ListResult<ReminderDTO>> callback) {
		projectAsyncDAO.get(projectId, new AsyncCallback<ProjectDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(ProjectDTO result) {
				if(result != null && result.getRemindersList() != null) {
					reminderAsyncDAO.getAllByParentListId(result.getRemindersList().getId(), wrapCallback(callback));
				} else {
					callback.onSuccess(new ListResult<ReminderDTO>(new ArrayList<ReminderDTO>()));
				}
			}
		});
	}
	
	private AsyncCallback<List<ReminderDTO>> wrapCallback(final AsyncCallback<ListResult<ReminderDTO>> callback) {
		return new AsyncCallback<List<ReminderDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(List<ReminderDTO> result) {
				callback.onSuccess(new ListResult<ReminderDTO>(result));
			}
		};
	}
}
