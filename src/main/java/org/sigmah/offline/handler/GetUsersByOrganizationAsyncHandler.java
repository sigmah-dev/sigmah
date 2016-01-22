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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(GetUsersByOrganization command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<UserDTO>> callback) {
		userDAO.getByOrganization(command.getOrganizationId(), callback);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(GetUsersByOrganization command, ListResult<UserDTO> result, Authentication authentication) {
		userDAO.saveAll(result, command.getOrganizationId());
	}
	
}
