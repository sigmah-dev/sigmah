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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ContactRelationship;
import org.sigmah.shared.command.result.ListResult;

public class GetContactRelationships extends AbstractCommand<ListResult<ContactRelationship>> {
  private static final long serialVersionUID = 569498740415522313L;

  private Integer contactId;
  private Set<ContactRelationship.Direction> directions = new HashSet<ContactRelationship.Direction>();

  public GetContactRelationships() {
    // Serialization
  }

  public GetContactRelationships(Integer contactId) {
    this(contactId, null);
  }

  public GetContactRelationships(Integer contactId, ContactRelationship.Direction direction) {
    this.contactId = contactId;
    if (direction != null) {
      this.directions.add(direction);
    } else {
      this.directions.addAll(Arrays.asList(ContactRelationship.Direction.values()));
    }
  }

  public Integer getContactId() {
    return contactId;
  }

  public Set<ContactRelationship.Direction> getDirections() {
    return directions;
  }
}
