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

import java.util.Date;

public class ContactHistory extends BaseModelData implements Result {
  private static final long serialVersionUID = -2066389453096136392L;

  public static final String ID = "ID";
  public static final String UPDATED_AT = "updatedAt";
  public static final String USER_FULL_NAME = "userFullName";
  public static final String FORMATTED_CHANGE_TYPE = "changeType";
  public static final String SUBJECT = "subject";
  public static final String FORMATTED_VALUE = "formattedValue";
  public static final String VALUE_TYPE = "valueType";
  public static final String COMMENT = "comment";

  public Integer getId() {
    return get(ID);
  }

  public void setId(Integer id) {
    set(ID, id);
  }

  public Date getUpdatedAt() {
    return get(UPDATED_AT);
  }

  public void setUpdatedAt(Date updatedAt) {
    set(UPDATED_AT, updatedAt);
  }

  public String getUserFullName() {
    return get(USER_FULL_NAME);
  }

  public void setUserFullName(String userFullName) {
    set(USER_FULL_NAME, userFullName);
  }

  public String getFormattedChangeType() {
    return get(FORMATTED_CHANGE_TYPE);
  }

  public void setFormattedChangeType(String formattedChangeType) {
    set(FORMATTED_CHANGE_TYPE, formattedChangeType);
  }

  public String getSubject() {
    return get(SUBJECT);
  }

  public void setSubject(String subject) {
    set(SUBJECT, subject);
  }

  public String getFormattedValue() {
    return get(FORMATTED_VALUE);
  }

  public void setFormattedValue(String formattedValue) {
    set(FORMATTED_VALUE, formattedValue);
  }

  public ValueType getValueType() {
    return get(VALUE_TYPE);
  }

  public void setValueType(ValueType valueType) {
    set(VALUE_TYPE, valueType);
  }

  public String getComment() {
    return get(COMMENT);
  }

  public void setComment(String comment) {
    set(COMMENT, comment);
  }

  public enum ValueType {
    STRING, IMAGE
  }
}
