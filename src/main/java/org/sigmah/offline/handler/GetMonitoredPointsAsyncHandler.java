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
import org.sigmah.offline.dao.MonitoredPointAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.result.ListResult;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetMonitoredPointsHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetMonitoredPointsAsyncHandler implements AsyncCommandHandler<GetMonitoredPoints, ListResult<MonitoredPointDTO>>, DispatchListener<GetMonitoredPoints, ListResult<MonitoredPointDTO>> {
	
	@Inject
	private MonitoredPointAsyncDAO monitoredPointAsyncDAO;
	
	@Inject
	private ProjectAsyncDAO projectAsyncDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(GetMonitoredPoints command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<MonitoredPointDTO>> callback) {
		if(command.getProjectId() == null) {
			loadAllMonitoredPoints(command, callback);
		} else {
			loadProjectMonitoredPoints(command.getProjectId(), callback);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(GetMonitoredPoints command, ListResult<MonitoredPointDTO> result, Authentication authentication) {
		monitoredPointAsyncDAO.saveAll(result, null);
	}

	private void loadAllMonitoredPoints(GetMonitoredPoints command, final AsyncCallback<ListResult<MonitoredPointDTO>> callback) {

		Set<Integer> orgUnitIds = command.getOrgUnitIds();
		projectAsyncDAO.getProjectsByIds(orgUnitIds, new AsyncCallback<ListResult<ProjectDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(final ListResult<ProjectDTO> projectsResult) {
				final int[] index = new int[]{0};
				final List<MonitoredPointDTO> reminders = new ArrayList<MonitoredPointDTO>();
				for (ProjectDTO projectDTO : projectsResult.getData()) {
					monitoredPointAsyncDAO.getAllByParentListId(projectDTO.getRemindersList().getId(), new AsyncCallback<List<MonitoredPointDTO>>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(List<MonitoredPointDTO> remindersResult) {
							for (MonitoredPointDTO reminderDTO : remindersResult) {
								if (reminderDTO.getCompletionDate() != null) {
									continue;
								}

								reminders.add(reminderDTO);
							}

							if (++index[0] >= projectsResult.getSize()) {
								callback.onSuccess(new ListResult<MonitoredPointDTO>(reminders));
							}
						}
					});
				}
			}
		});
	}
	
	private void loadProjectMonitoredPoints(final int projectId, final AsyncCallback<ListResult<MonitoredPointDTO>> callback) {
		projectAsyncDAO.get(projectId, new AsyncCallback<ProjectDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(ProjectDTO result) {
				if(result != null && result.getPointsList() != null) {
					monitoredPointAsyncDAO.getAllByParentListId(result.getPointsList().getId(), wrapCallback(callback));
				} else {
					callback.onSuccess(new ListResult<MonitoredPointDTO>(new ArrayList<MonitoredPointDTO>()));
				}
			}
		});
	}
	
	private AsyncCallback<List<MonitoredPointDTO>> wrapCallback(final AsyncCallback<ListResult<MonitoredPointDTO>> callback) {
		return new AsyncCallback<List<MonitoredPointDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(List<MonitoredPointDTO> result) {
				callback.onSuccess(new ListResult<MonitoredPointDTO>(result));
			}
		};
	}
}
