package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ProjectReportAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetProjectReport;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.report.ProjectReportDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetProjectReportHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetProjectReportAsyncHandler implements AsyncCommandHandler<GetProjectReport, ProjectReportDTO>, DispatchListener<GetProjectReport, ProjectReportDTO> {

	@Inject
	private ProjectReportAsyncDAO projectReportAsyncDAO;
	
	@Override
	public void execute(GetProjectReport command, OfflineExecutionContext executionContext, AsyncCallback<ProjectReportDTO> callback) {
		projectReportAsyncDAO.get(command.getReportId(), callback);
	}

	@Override
	public void onSuccess(GetProjectReport command, ProjectReportDTO result, Authentication authentication) {
		if(result != null) {
			projectReportAsyncDAO.saveOrUpdate(result);
		}
	}
	
}
