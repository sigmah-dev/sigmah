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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ContactRelationshipAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetContactRelationships;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ContactRelationship;
import org.sigmah.shared.command.result.ListResult;

public class GetContactRelationshipsAsyncHandler implements AsyncCommandHandler<GetContactRelationships,
    ListResult<ContactRelationship>>, DispatchListener<GetContactRelationships, ListResult<ContactRelationship>> {
  private final ContactRelationshipAsyncDAO contactRelationshipAsyncDAO;

  @Inject
  public GetContactRelationshipsAsyncHandler(ContactRelationshipAsyncDAO contactRelationshipAsyncDAO) {
    this.contactRelationshipAsyncDAO = contactRelationshipAsyncDAO;
  }

  @Override
  public void execute(GetContactRelationships command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<ContactRelationship>> callback) {
    contactRelationshipAsyncDAO.findByContactId(command.getContactId(), callback);
  }

  @Override
  public void onSuccess(GetContactRelationships command, ListResult<ContactRelationship> result, Authentication authentication) {
    if (result == null || result.getList() == null) {
      return;
    }

    for (ContactRelationship contactRelationship : result.getList()) {
      // Set the contactId to make ContactRelationshipJS indexed by contactId in IndexedDB
      contactRelationship.setContactId(command.getContactId());
      contactRelationshipAsyncDAO.saveOrUpdate(contactRelationship);
    }
  }
}
