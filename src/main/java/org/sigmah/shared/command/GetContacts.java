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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

public class GetContacts extends AbstractCommand<ListResult<ContactDTO>> {
  private static final long serialVersionUID = -945246727047007961L;

  private Set<Integer> contactIds;
  private ContactModelType type;
  private Set<Integer> contactModelIds;
  private boolean onlyContactWithoutUser;
  private boolean withEmailNotNull;
  private Set<Integer> orgUnitsIds;

  public GetContacts() {
    // no-op
  }

  public GetContacts(Set<Integer> contactIds) {
    this.contactIds = contactIds;
  }

  public GetContacts(ContactModelType type) {
    this(type, Collections.<Integer>emptySet(), false, false);
  }

  public GetContacts(ContactModelType type, Set<Integer> contactModelIds) {
    this(type, contactModelIds, false, false);
  }

  public GetContacts(ContactModelType type, boolean onlyContactWithoutUser, boolean withEmailNotNull) {
    this(type, Collections.<Integer>emptySet(), onlyContactWithoutUser, withEmailNotNull);
  }

  public GetContacts(ContactModelType type, Set<Integer> contactModelIds, boolean onlyContactWithoutUser, boolean withEmailNotNull) {
    this.type = type;
    this.contactModelIds = contactModelIds;
    this.onlyContactWithoutUser = onlyContactWithoutUser;
    this.withEmailNotNull = withEmailNotNull;
  }

  public Set<Integer> getContactIds() {
    return contactIds;
  }

  public ContactModelType getType() {
    return type;
  }

  public Set<Integer> getContactModelIds() {
    return contactModelIds;
  }

  public boolean isOnlyContactWithoutUser() {
    return onlyContactWithoutUser;
  }

  public boolean isWithEmailNotNull() {
    return withEmailNotNull;
  }

  public Set<Integer> getOrgUnitsIds() {
    return orgUnitsIds;
  }

  public void setOrgUnitsIds(Set<Integer> orgUnitsIds) {
    this.orgUnitsIds = orgUnitsIds;
  }

  @Override
  protected void appendToString(final ToStringBuilder builder) {
    builder.append("type", type);
  }
}
