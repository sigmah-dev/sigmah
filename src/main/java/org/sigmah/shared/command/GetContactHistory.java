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
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.command.result.ListResult;

public class GetContactHistory extends AbstractCommand<ListResult<ContactHistory>> {
  private static final long serialVersionUID = 5615701725561806283L;

  private Integer contactId;

  private Boolean lastOnly;

  public GetContactHistory() {
    // Serialization
  }

  public GetContactHistory(Integer contactId) {
    this(contactId, false);
  }

  public GetContactHistory(Integer contactId, Boolean lastOnly) {
    this.contactId = contactId;
    this.lastOnly = lastOnly;
  }

  public Integer getContactId() {
    return contactId;
  }

  public boolean isLastOnly() {
    return lastOnly;
  }
}
