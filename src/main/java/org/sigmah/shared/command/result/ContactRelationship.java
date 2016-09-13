package org.sigmah.shared.command.result;
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

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ContactRelationship extends BaseModelData implements Result {
  private static final long serialVersionUID = 7831558378084550189L;

  public static final String RELATIONSHIP_ID = "relationId";
  public static final String FIELD_NAME = "fieldName";
  public static final String GROUP_NAME = "groupName";
  public static final String TYPE = "type";
  public static final String FORMATTED_TYPE = "formattedType";
  public static final String NAME = "name";
  public static final String DIRECTION = "direction";

  public Integer getRelationshipId() {
    return get(RELATIONSHIP_ID);
  }

  public void setRelationshipId(Integer relationshipId) {
    set(RELATIONSHIP_ID, relationshipId);
  }

  public String getFieldName() {
    return get(FIELD_NAME);
  }

  public void setFieldName(String fieldName) {
    set(FIELD_NAME, fieldName);
  }

  public String getGroupName() {
    return get(GROUP_NAME);
  }

  public void setGroupName(String groupName) {
    set(GROUP_NAME, groupName);
  }

  public Type getType() {
    return get(TYPE);
  }

  public void setType(Type type) {
    set(TYPE, type);
  }

  public String getFormattedType() {
    return get(FORMATTED_TYPE);
  }

  public void setFormattedType(String formattedType) {
    set(FORMATTED_TYPE, formattedType);
  }

  public String getName() {
    return get(NAME);
  }

  public void setName(String name) {
    set(NAME, name);
  }

  public Direction getDirection() {
    return get(DIRECTION);
  }

  public void setDirection(Direction direction) {
    set(DIRECTION, direction);
  }

  public enum Type {
    PROJECT, ORGUNIT, CONTACT
  }

  /**
   * OUTBOUND => has member
   * INBOUND => is member
   */
  public enum Direction {
    OUTBOUND, INBOUND
  }
}
