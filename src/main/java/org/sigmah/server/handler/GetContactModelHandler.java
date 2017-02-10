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

import org.sigmah.server.dao.ContactModelDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetContactModel;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactModelDTO;

public class GetContactModelHandler extends AbstractCommandHandler<GetContactModel, ContactModelDTO> {
  private final ContactModelDAO contactModelDAO;

  @Inject
  public GetContactModelHandler(ContactModelDAO contactModelDAO) {
    this.contactModelDAO = contactModelDAO;
  }

  @Override
  protected ContactModelDTO execute(GetContactModel command, UserDispatch.UserExecutionContext context) throws CommandException {
    return mapper().map(contactModelDAO.findById(command.getModelId()), new ContactModelDTO());
  }
}
