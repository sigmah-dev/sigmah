package org.sigmah.offline.handler;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.UserAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetUsersByOrganizationHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetUsersByOrganizationAsyncHandler implements AsyncCommandHandler<GetUsersByOrganization, ListResult<UserDTO>>, DispatchListener<GetUsersByOrganization, ListResult<UserDTO>> {

	private final UserAsyncDAO userDAO;

	@Inject
	public GetUsersByOrganizationAsyncHandler(UserAsyncDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	public void execute(GetUsersByOrganization command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<UserDTO>> callback) {
		userDAO.getByOrganization(command.getOrganizationId(), callback);
	}

	@Override
	public void onSuccess(GetUsersByOrganization command, ListResult<UserDTO> result, Authentication authentication) {
		userDAO.saveOrUpdate(result, command.getOrganizationId());
	}
}
