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

import java.util.List;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.ContactDuplicationService;
import org.sigmah.server.service.ContactService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.command.GetContactDuplicatedProperties;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;

public class GetContactDuplicatedPropertiesHandler extends AbstractCommandHandler<GetContactDuplicatedProperties, ListResult<ContactDuplicatedProperty>> {
  private final ContactDuplicationService contactDuplicationService;
  private final ContactService contactService;
  private final ContactDAO contactDAO;

  @Inject
  public GetContactDuplicatedPropertiesHandler(ContactDuplicationService contactDuplicationService, ContactService contactService, ContactDAO contactDAO) {
    this.contactDuplicationService = contactDuplicationService;
    this.contactService = contactService;
    this.contactDAO = contactDAO;
  }

  @Override
  protected ListResult<ContactDuplicatedProperty> execute(GetContactDuplicatedProperties command, UserDispatch.UserExecutionContext context) throws CommandException {
    Contact newContact;
    Contact oldContact;
    if (command.getNewContactId() != null) {
      // This contact was freshly updated.
      newContact = contactDAO.findById(command.getNewContactId());
    } else {
      // The contact was not created.
      // Let's generate a new contact from the user data without persisting it
      newContact = contactService.createVirtual(new PropertyMap(command.getNewContactProperties()), context);
    }
    oldContact = contactDAO.findById(command.getOldContactId());

    List<ContactDuplicatedProperty> duplicatedProperties = contactDuplicationService.extractProperties(newContact, oldContact, context.getLanguage());
    return new ListResult<>(duplicatedProperties);
  }
}
