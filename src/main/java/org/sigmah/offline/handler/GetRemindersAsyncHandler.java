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

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ReminderAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetRemindersHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetRemindersAsyncHandler implements AsyncCommandHandler<GetReminders, ListResult<ReminderDTO>>, DispatchListener<GetReminders, ListResult<ReminderDTO>> {

	@Inject
	private ReminderAsyncDAO reminderAsyncDAO;

	@Inject
	private ProjectAsyncDAO projectAsyncDAO;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(GetReminders command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<ReminderDTO>> callback) {
		if(command.getProjectId() == null) {
			loadAllReminders(callback);
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
	
	private void loadAllReminders(final AsyncCallback<ListResult<ReminderDTO>> callback) {
		reminderAsyncDAO.getAllWithoutCompletionDate(new AsyncCallback<List<ReminderDTO>>() {
			@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
			public void onSuccess(List<ReminderDTO> result) {
				final RequestManager<ListResult<ReminderDTO>> manager = new RequestManager<ListResult<ReminderDTO>>(new ListResult<ReminderDTO>(result), callback);
						
				for (final ReminderDTO reminder : result) {
					projectAsyncDAO.getByIndexWithoutDependencies("remindersListId", reminder.getParentListId(), new RequestManagerCallback<ListResult<ReminderDTO>, ProjectDTO>(manager) {
						@Override
						public void onRequestSuccess(ProjectDTO result) {
							if (result != null) {
								reminder.setProjectId(result.getId());
								reminder.setProjectCode(result.getName());
								reminder.setProjectName(result.getFullName());
					}
				}
			});
		}
				manager.ready();
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
