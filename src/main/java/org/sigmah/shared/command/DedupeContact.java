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

import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.dto.ContactDTO;

public class DedupeContact extends AbstractCommand<ContactDTO> {
  private static final long serialVersionUID = 3273765689491170505L;

  private List<ContactDuplicatedProperty> contactDuplicatedProperties;
  private Integer originContactId;
  private Integer targetedContactId;

  public DedupeContact() {
    // Serialization
  }

  public DedupeContact(List<ContactDuplicatedProperty> contactDuplicatedProperties, Integer targetedContactId) {
    this.contactDuplicatedProperties = contactDuplicatedProperties;
    this.targetedContactId = targetedContactId;
  }

  public DedupeContact(List<ContactDuplicatedProperty> contactDuplicatedProperties, Integer originContactId, Integer targetedContactId) {
    this.contactDuplicatedProperties = contactDuplicatedProperties;
    this.originContactId = originContactId;
    this.targetedContactId = targetedContactId;
  }

  public List<ContactDuplicatedProperty> getContactDuplicatedProperties() {
    return contactDuplicatedProperties;
  }

  public Integer getOriginContactId() {
    return originContactId;
  }

  public Integer getTargetedContactId() {
    return targetedContactId;
  }
}
