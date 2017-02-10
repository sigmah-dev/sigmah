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

import java.util.Collections;
import java.util.Set;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

public class GetContactModels extends AbstractCommand<ListResult<ContactModelDTO>> {
  private static final long serialVersionUID = 279291041835072569L;

  private ContactModelType type;
  private Set<Integer> allowedIds;
  private boolean onlyAvailable;

  public GetContactModels() {
    // Serialization
  }

  public GetContactModels(ContactModelType type, boolean onlyAvailable) {
    this(type, Collections.<Integer>emptySet(), onlyAvailable);
  }

  public GetContactModels(ContactModelType type, Set<Integer> allowedIds, boolean onlyAvailable) {
    this.type = type;
    this.allowedIds = allowedIds;
    this.onlyAvailable = onlyAvailable;
  }

  public ContactModelType getType() {
    return type;
  }

  public Set<Integer> getAllowedIds() {
    return allowedIds;
  }

  public boolean isOnlyAvailable() {
    return onlyAvailable;
  }
}
