package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.MonitoredPointAsyncDAO;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.UpdateMonitoredPoints;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.UpdateMonitoredPointsHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UpdateMonitoredPointsAsyncHandler implements AsyncCommandHandler<UpdateMonitoredPoints, ListResult<MonitoredPointDTO>>, DispatchListener<UpdateMonitoredPoints, ListResult<MonitoredPointDTO>> {

	@Inject
	private MonitoredPointAsyncDAO monitoredPointAsyncDAO;
	
	@Inject
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
	
	@Override
	public void execute(final UpdateMonitoredPoints command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<MonitoredPointDTO>> callback) {
		monitoredPointAsyncDAO.saveAll(command.getList(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				callback.onSuccess(new ListResult<MonitoredPointDTO>(command.getList()));
			}
		});
		
		updateDiaryAsyncDAO.saveOrUpdate(command);
	}

	@Override
	public void onSuccess(UpdateMonitoredPoints command, ListResult<MonitoredPointDTO> result, Authentication authentication) {
		monitoredPointAsyncDAO.saveOrUpdate(result);
	}
	
}
