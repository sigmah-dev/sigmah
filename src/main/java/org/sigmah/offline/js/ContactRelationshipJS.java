package org.sigmah.offline.js;
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

import com.google.gwt.core.client.JavaScriptObject;

import org.sigmah.shared.command.result.ContactRelationship;

public class ContactRelationshipJS extends JavaScriptObject {
  protected ContactRelationshipJS() {
  }

  public static ContactRelationshipJS toJavaScript(ContactRelationship contactRelationshipDTO) {
    ContactRelationshipJS contactRelationshipJS = (ContactRelationshipJS) JavaScriptObject.createObject();
    contactRelationshipJS.setRelationshipId(contactRelationshipDTO.getRelationshipId());
    contactRelationshipJS.setName(contactRelationshipDTO.getName());
    contactRelationshipJS.setFieldName(contactRelationshipDTO.getFieldName());
    contactRelationshipJS.setGroupName(contactRelationshipDTO.getGroupName());
    contactRelationshipJS.setFormattedType(contactRelationshipDTO.getFormattedType());
    contactRelationshipJS.setType(contactRelationshipDTO.getType());
    contactRelationshipJS.setDirection(contactRelationshipDTO.getDirection());
    return contactRelationshipJS;
  }

  public ContactRelationship toDTO() {
    ContactRelationship contactRelationship = new ContactRelationship();
    contactRelationship.setRelationshipId(getRelationshipId());
    contactRelationship.setName(getName());
    contactRelationship.setFieldName(getFieldName());
    contactRelationship.setGroupName(getGroupName());
    contactRelationship.setFormattedType(getFormattedType());
    contactRelationship.setType(getTypeDTO());
    contactRelationship.setDirection(getDirectionDTO());
    return contactRelationship;
  }

  public native int getRelationshipId() /*-{
		return this.id;
	}-*/;

  public native void setRelationshipId(int id) /*-{
		this.id = id;
	}-*/;

  public native String getFieldName() /*-{
		return this.fieldName;
	}-*/;

  public native void setFieldName(String fieldName) /*-{
		this.fieldName = fieldName;
	}-*/;

  public native String getGroupName() /*-{
		return this.groupName;
	}-*/;

  public native void setGroupName(String groupName) /*-{
		this.groupName = groupName;
	}-*/;

  public native String getFormattedType() /*-{
		return this.formattedType;
	}-*/;

  public native void setFormattedType(String formattedType) /*-{
		this.formattedType = formattedType;
	}-*/;

  public native String getName() /*-{
		return this.name;
	}-*/;

  public native void setName(String name) /*-{
		this.name = name;
	}-*/;

  public ContactRelationship.Type getTypeDTO() {
    return ContactRelationship.Type.valueOf(getType());
  }

  public native String getType() /*-{
		return this.type;
	}-*/;

  public void setType(ContactRelationship.Type type) {
    setType(type.name());
  }

  public native void setType(String type) /*-{
		this.type = type;
	}-*/;

  public ContactRelationship.Direction getDirectionDTO() {
    return ContactRelationship.Direction.valueOf(getDirection());
  }

  public native String getDirection() /*-{
		return this.direction;
	}-*/;

  public void setDirection(ContactRelationship.Direction direction) {
    setDirection(direction.name());
  }

  public native void setDirection(String direction) /*-{
		this.direction = direction;
	}-*/;
}
