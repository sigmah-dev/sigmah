package org.sigmah.offline.handler;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.OrgUnitAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetOrgUnitHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetOrgUnitAsyncHandler implements AsyncCommandHandler<GetOrgUnit, OrgUnitDTO>, DispatchListener<GetOrgUnit, OrgUnitDTO> {

	private final OrgUnitAsyncDAO orgUnitAsyncDAO;

	@Inject
	public GetOrgUnitAsyncHandler(OrgUnitAsyncDAO orgUnitAsyncDAO) {
		this.orgUnitAsyncDAO = orgUnitAsyncDAO;
	}
	
	@Override
	public void execute(GetOrgUnit command, OfflineExecutionContext executionContext, AsyncCallback<OrgUnitDTO> callback) {
		orgUnitAsyncDAO.get(command.getId(), callback);
	}

	@Override
	public void onSuccess(GetOrgUnit command, OrgUnitDTO result, Authentication authentication) {
		orgUnitAsyncDAO.saveOrUpdate(result);
	}
}
