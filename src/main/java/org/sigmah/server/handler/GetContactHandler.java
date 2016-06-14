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

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetContact;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactDTO;

public class GetContactHandler extends AbstractCommandHandler<GetContact, ContactDTO> {
  private final ContactDAO contactDAO;

  @Inject
  public GetContactHandler(ContactDAO contactDAO) {
    this.contactDAO = contactDAO;
  }

  @Override
  protected ContactDTO execute(GetContact command, UserDispatch.UserExecutionContext context) throws CommandException {
    Contact contact = contactDAO.findById(command.getContactId());
    // FIXME: Add command.mode when dozer 5.5.2 will be ready
    // see https://github.com/DozerMapper/dozer/commit/5e179bb68c91e60d63bf9f44bf64b7ca70f61520
    return mapper().map(contact, new ContactDTO());
  }
}
