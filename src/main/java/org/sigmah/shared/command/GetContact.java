package org.sigmah.shared.command;
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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.ContactDTO;

public class GetContact extends AbstractCommand<ContactDTO> {
  private static final long serialVersionUID = 5740604058297808831L;

  private Integer contactId;
  private ContactDTO.Mode mode;

  public GetContact() {
    // Serialization
  }

  public GetContact(Integer contactId, ContactDTO.Mode mode) {
    this.contactId = contactId;
    this.mode = mode;
  }

  public Integer getContactId() {
    return contactId;
  }

  public ContactDTO.Mode getMode() {
    return mode;
  }
}
