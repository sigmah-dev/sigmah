/**
 * 
 */
package org.sigmah.offline.handler;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ContactAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetContact;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dispatch.NotCachedException;
import org.sigmah.shared.dto.ContactDTO;

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
 * JavaScript implementation of {@link org.sigmah.server.handler.GetContactHandler}.
 * Used when the user is offline.
 * 
 * @author vde (vde@atolcd.com)
 */
@Singleton
public class GetContactAsyncHandler implements AsyncCommandHandler<GetContact, ContactDTO>, DispatchListener<GetContact, ContactDTO> {
	
	private final ContactAsyncDAO contactAsyncDAO;

	@Inject
	public GetContactAsyncHandler(ContactAsyncDAO contactAsyncDAO) {
		this.contactAsyncDAO = contactAsyncDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final GetContact command, OfflineExecutionContext executionContext, final AsyncCallback<ContactDTO> callback) {
		contactAsyncDAO.get(command.getContactId(), new AsyncCallback<ContactDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(ContactDTO result) {
				if (result != null) {
					callback.onSuccess(result);
				} else {
					callback.onFailure(new NotCachedException("Requested project '" + command.getContactId() + "' was not found in the local database."));
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(GetContact command, ContactDTO result, Authentication authentication) {
        // TODO: Do something better. Maybe mix data from the database and from the given DTO ? Switch on the mapping mode ?
        if (result != null && result.getCurrentMappingMode() == null) {
        	contactAsyncDAO.saveOrUpdate(result);
        }
	}
	
}
