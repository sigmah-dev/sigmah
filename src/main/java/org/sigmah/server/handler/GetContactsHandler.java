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

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactDTO;

public class GetContactsHandler extends AbstractCommandHandler<GetContacts, ListResult<ContactDTO>> {
  private final ContactDAO contactDAO;

  @Inject GetContactsHandler(ContactDAO contactDAO) {
    this.contactDAO = contactDAO;
  }

  @Override
  protected ListResult<ContactDTO> execute(GetContacts command, UserDispatch.UserExecutionContext context) throws CommandException {
    Integer organizationId = context.getUser().getOrganization().getId();
    List<Contact> contacts;
    if (command.getContactIds() == null || command.getContactIds().isEmpty()) {
      contacts = contactDAO.findContactsByTypeAndContactModels(organizationId, command.getType(),
          command.getContactModelIds(), command.isOnlyContactWithoutUser(), command.isWithEmailNotNull(), command.getOrgUnitsIds());
    } else {
      contacts = contactDAO.findByIds(command.getContactIds());
    }

    List<ContactDTO> contactDTOs = new ArrayList<>();
    for (Contact contact : contacts) {
      if (!contact.isDeleted()) {
        contactDTOs.add(mapper().map(contact, ContactDTO.class));
      }
    }

    // FIXME: Add ContactDTO.Mode.MAIN_INFORMATION when dozer 5.5.2 will be ready
    // see https://github.com/DozerMapper/dozer/commit/5e179bb68c91e60d63bf9f44bf64b7ca70f61520
    return new ListResult<>(contactDTOs);
  }
}
