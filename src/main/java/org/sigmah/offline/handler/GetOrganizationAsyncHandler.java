package org.sigmah.offline.handler;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.OrganizationAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.dto.organization.OrganizationDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetOrganizationHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetOrganizationAsyncHandler implements AsyncCommandHandler<GetOrganization, OrganizationDTO>, DispatchListener<GetOrganization, OrganizationDTO> {

	private final OrganizationAsyncDAO organizationDAO;

	@Inject
	public GetOrganizationAsyncHandler(OrganizationAsyncDAO organizationDAO) {
		this.organizationDAO = organizationDAO;
	}
	
	@Override
	public void execute(GetOrganization command, OfflineExecutionContext executionContext, AsyncCallback<OrganizationDTO> callback) {
		organizationDAO.get(command.getId(), callback);
	}

	@Override
	public void onSuccess(GetOrganization command, OrganizationDTO result, Authentication authentication) {
		if(result != null) {
			organizationDAO.saveOrUpdate(result);
		}
	}
}
