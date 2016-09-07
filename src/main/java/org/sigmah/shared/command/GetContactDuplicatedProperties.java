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

import java.util.Map;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.command.result.ListResult;

public class GetContactDuplicatedProperties extends AbstractCommand<ListResult<ContactDuplicatedProperty>> {
  private static final long serialVersionUID = 1736383302721297803L;

  private Integer oldContactId;
  private Integer newContactId;
  private Map<String, Object> newContactProperties;

  public GetContactDuplicatedProperties() {
    // Serialization
  }

  public GetContactDuplicatedProperties(Integer oldContactId, Integer newContactId, Map<String, Object> newContactProperties) {
    this.oldContactId = oldContactId;
    this.newContactId = newContactId;
    this.newContactProperties = newContactProperties;
  }

  public Integer getOldContactId() {
    return oldContactId;
  }

  public Integer getNewContactId() {
    return newContactId;
  }

  public Map<String, Object> getNewContactProperties() {
    return newContactProperties;
  }
}
