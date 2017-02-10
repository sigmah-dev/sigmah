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

public class ContactDuplicatedProperty extends BaseModelData implements Result {
  private static final long serialVersionUID = 974729148236257362L;

  public static final String PROPERTY_LABEL = "propertyLabel";
  public static final String FORMATTED_NEW_VALUE = "newValue";
  public static final String FORMATTED_OLD_VALUE = "oldValue";
  public static final String SERIALIZED_NEW_VALUE = "serializedNewValue";
  public static final String SERIALIZED_OLD_VALUE = "serializedOldValue";
  public static final String FLEXIBLE_ELEMENT_ID = "flexibleElementId";
  public static final String VALUE_TYPE = "valueType";

  public String getPropertyLabel() {
    return get(PROPERTY_LABEL);
  }

  public void setPropertyLabel(String propertyLabel) {
    set(PROPERTY_LABEL, propertyLabel);
  }

  public String getFormattedNewValue() {
    return get(FORMATTED_NEW_VALUE);
  }

  public void setFormattedNewValue(String newValue) {
    set(FORMATTED_NEW_VALUE, newValue);
  }

  public String getFormattedOldValue() {
    return get(FORMATTED_OLD_VALUE);
  }

  public void setFormattedOldValue(String oldValue) {
    set(FORMATTED_OLD_VALUE, oldValue);
  }

  public String getSerializedNewValue() {
    return get(SERIALIZED_NEW_VALUE);
  }

  public void setSerializedNewValue(String serializedNewValue) {
    set(SERIALIZED_NEW_VALUE, serializedNewValue);
  }

  public String getSerializedOldValue() {
    return get(SERIALIZED_OLD_VALUE);
  }

  public void setSerializedOldValue(String serializedOldValue) {
    set(SERIALIZED_OLD_VALUE, serializedOldValue);
  }

  public Integer getFlexibleElementId() {
    return get(FLEXIBLE_ELEMENT_ID);
  }

  public void setFlexibleElementId(Integer flexibleElementId) {
    set(FLEXIBLE_ELEMENT_ID, flexibleElementId);
  }

  public ValueType getValueType() {
    return get(VALUE_TYPE);
  }

  public void setValueType(ValueType valueType) {
    set(VALUE_TYPE, valueType);
  }

  public enum ValueType {
    STRING, IMAGE
  }
}
