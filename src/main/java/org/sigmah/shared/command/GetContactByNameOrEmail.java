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

import java.util.Set;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

public class GetContactByNameOrEmail extends AbstractCommand<ListResult<ContactDTO>> {
  private static final long serialVersionUID = -4520509671523305967L;

  private String search;
  private boolean withDeleted;
  private boolean onlyWithoutUser;
  private ContactModelType allowedType;
  private Set<Integer> allowedModelIds;
  private Set<Integer> excludedIds;
  private Integer checkboxElementId;

  public GetContactByNameOrEmail() {
    // Serialization
  }

  public GetContactByNameOrEmail(String search, boolean withDeleted, boolean onlyWithoutUser, ContactModelType allowedType, Set<Integer> allowedModelIds, Set<Integer> excludedIds, Integer checkboxElementId) {
    this.search = search;
    this.withDeleted = withDeleted;
    this.onlyWithoutUser = onlyWithoutUser;
    this.allowedType = allowedType;
    this.allowedModelIds = allowedModelIds;
    this.excludedIds = excludedIds;
    this.checkboxElementId = checkboxElementId;
  }

  public String getSearch() {
    return search;
  }

  public boolean getWithDeleted() {
    return withDeleted;
  }

  public boolean getOnlyWithoutUser() {
    return onlyWithoutUser;
  }

  public ContactModelType getAllowedType() {
    return allowedType;
  }

  public Set<Integer> getAllowedModelIds() {
    return allowedModelIds;
  }

  public Set<Integer> getExcludedIds() {
    return excludedIds;
  }

  public Integer getCheckboxElementId() {
    return checkboxElementId;
  }
}
