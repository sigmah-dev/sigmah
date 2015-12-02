package org.sigmah.offline.handler;

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

	@Override
	public void execute(GetMonitoredPoints command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<MonitoredPointDTO>> callback) {
		if(command.getProjectId() == null) {
			loadAllMonitoredPoints(callback);
		} else {
			loadProjectMonitoredPoints(command.getProjectId(), callback);
		}
	}

	@Override
	public void onSuccess(GetMonitoredPoints command, ListResult<MonitoredPointDTO> result, Authentication authentication) {
		monitoredPointAsyncDAO.saveOrUpdate(result);
	}

	private void loadAllMonitoredPoints(final AsyncCallback<ListResult<MonitoredPointDTO>> callback) {
		monitoredPointAsyncDAO.getAllWithoutCompletionDate(new AsyncCallback<List<MonitoredPointDTO>>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
			public void onSuccess(List<MonitoredPointDTO> result) {
				final RequestManager<ListResult<MonitoredPointDTO>> manager = new RequestManager<ListResult<MonitoredPointDTO>>(new ListResult<MonitoredPointDTO>(result), callback);
						
				for (final MonitoredPointDTO monitoredPoint : result) {
					projectAsyncDAO.getByIndexWithoutDependencies("pointsListId", monitoredPoint.getParentListId(), new RequestManagerCallback<ListResult<MonitoredPointDTO>, ProjectDTO>(manager) {
						@Override
						public void onRequestSuccess(ProjectDTO result) {
							if (result != null) {
								monitoredPoint.setProjectId(result.getId());
								monitoredPoint.setProjectCode(result.getName());
								monitoredPoint.setProjectName(result.getFullName());
					}
				}
			});
		}
				manager.ready();
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
