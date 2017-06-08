/**
 * 
 */
package org.sigmah.offline.handler;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ContactAsyncDAO;
import org.sigmah.offline.dao.OrgUnitAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

/**
 * 
 *  * JavaScript implementation of {@link org.sigmah.server.handler.GetContactsHandler}.
 * Used when the user is offline.
 * 
 * @author vde
 *
 */
@Singleton
public class GetContactsAsyncHandler implements AsyncCommandHandler<GetContacts, ListResult<ContactDTO>>,
		DispatchListener<GetContacts, ListResult<ContactDTO>> {

	private final ContactAsyncDAO contactAsyncDAO;
	
	@Inject
	public GetContactsAsyncHandler(Authentication authentication, ContactAsyncDAO contactAsyncDAO, OrgUnitAsyncDAO orgUnitAsyncDAO) {
		this.contactAsyncDAO = contactAsyncDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final GetContacts command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<ContactDTO>> callback) {
		contactAsyncDAO.getListResult(callback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(GetContacts command, ListResult<ContactDTO> result, Authentication authentication) {
		if (result != null && result.getList() != null) {
			contactAsyncDAO.saveAll(result.getList(), null);
		}
	}
	
	
}
