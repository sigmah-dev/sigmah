package org.sigmah.server.handler;
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

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.ContactHistoryService;
import org.sigmah.shared.command.GetContactHistory;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;

public class GetContactHistoryHandler extends AbstractCommandHandler<GetContactHistory, ListResult<ContactHistory>> {
  private final ContactHistoryService contactHistoryService;

  @Inject
  public GetContactHistoryHandler(ContactHistoryService contactHistoryService) {
    this.contactHistoryService = contactHistoryService;
  }

  @Override
  protected ListResult<ContactHistory> execute(GetContactHistory command, UserDispatch.UserExecutionContext context) throws CommandException {
    List<ContactHistory> contactHistories = contactHistoryService.findHistory(command.getContactId(), context.getLanguage());

    if(command.isLastOnly() && !contactHistories.isEmpty()) {
      contactHistories = new ArrayList<ContactHistory>(contactHistories.subList(contactHistories.size()-1, contactHistories.size()));
    }

    return new ListResult<ContactHistory>(contactHistories);
  }
}
