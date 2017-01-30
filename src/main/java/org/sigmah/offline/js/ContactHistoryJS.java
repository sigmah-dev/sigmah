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
import com.google.gwt.core.client.JsDate;

import java.util.Date;

import org.sigmah.shared.command.result.ContactHistory;

public final class ContactHistoryJS extends JavaScriptObject {
  protected ContactHistoryJS() {
  }

  public static ContactHistoryJS toJavaScript(ContactHistory contactHistory) {
    ContactHistoryJS contactHistoryJS = (ContactHistoryJS) JavaScriptObject.createObject();
    contactHistoryJS.setId(contactHistory.getId());
    contactHistoryJS.setContactId(contactHistory.getContactId());
    contactHistoryJS.setUserFullName(contactHistory.getUserFullName());
    contactHistoryJS.setFormattedChangeType(contactHistory.getFormattedChangeType());
    contactHistoryJS.setFormattedValue(contactHistory.getFormattedValue());
    contactHistoryJS.setSubject(contactHistory.getSubject());
    contactHistoryJS.setValueType(contactHistory.getValueType());
    contactHistoryJS.setUpdatedAt(contactHistory.getUpdatedAt());
    contactHistoryJS.setComment(contactHistory.getComment());
    return contactHistoryJS;
  }

  public ContactHistory toDTO() {
    ContactHistory contactHistory = new ContactHistory();
    contactHistory.setId(getId());
    contactHistory.setUserFullName(getUserFullName());
    contactHistory.setFormattedChangeType(getFormattedChangeType());
    contactHistory.setFormattedValue(getFormattedValue());
    contactHistory.setSubject(getSubject());
    contactHistory.setValueType(getValueTypeDTO());
    contactHistory.setUpdatedAt(getUpdatedAtDTO());
    contactHistory.setComment(getComment());
    return contactHistory;
  }

  public native int getId() /*-{
    return this.id;
	}-*/;

  public native void setId(int id) /*-{
		this.id = id;
	}-*/;

  public native int getContactId() /*-{
    return this.contactId;
	}-*/;

  public native void setContactId(int contactId) /*-{
		this.contactId = contactId;
	}-*/;

  public native String getUserFullName() /*-{
		return this.userFullName;
	}-*/;

  public native void setUserFullName(String userFullName) /*-{
		this.userFullName = userFullName;
	}-*/;

  public native String getFormattedChangeType() /*-{
		return this.formattedChangeType;
	}-*/;

  public native void setFormattedChangeType(String formattedChangeType) /*-{
		this.formattedChangeType = formattedChangeType;
	}-*/;

  public native String getSubject() /*-{
		return this.subject;
	}-*/;

  public native void setSubject(String subject) /*-{
		this.subject = subject;
	}-*/;

  public native String getFormattedValue() /*-{
		return this.formattedValue;
	}-*/;

  public native void setFormattedValue(String formattedValue) /*-{
		this.formattedValue = formattedValue;
	}-*/;

  public native String getComment() /*-{
		return this.comment;
	}-*/;

  public native void setComment(String comment) /*-{
		this.comment = comment;
	}-*/;

  public ContactHistory.ValueType getValueTypeDTO() {
    return ContactHistory.ValueType.valueOf(getValueType());
  }

  public native String getValueType() /*-{
		return this.valueType;
	}-*/;

  public void setValueType(ContactHistory.ValueType valueType) {
    setValueType(valueType.name());
  }

  public native void setValueType(String valueType) /*-{
		this.valueType = valueType;
	}-*/;

  public Date getUpdatedAtDTO() {
    if (getUpdatedAt() == null) {
      return null;
    }
    return new Date((long) getUpdatedAt().getTime());
  }

  public native JsDate getUpdatedAt() /*-{
		return this.updatedAt;
	}-*/;

  public void setUpdatedAt(Date updatedAt) {
    if (updatedAt != null) {
      setUpdatedAt(JsDate.create(updatedAt.getTime()));
    }
  }

  public native void setUpdatedAt(JsDate updatedAt) /*-{
		this.updatedAt = updatedAt;
	}-*/;
}
