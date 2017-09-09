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
import com.google.gwt.core.client.JsArray;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.UpdateContact;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;

public final class UpdateContactJS extends CommandJS {

  protected UpdateContactJS() {
  }

  public static UpdateContactJS toJavaScript(UpdateContact updateContact) {
    final UpdateContactJS updateContactJS = Values.createJavaScriptObject(UpdateContactJS.class);

    updateContactJS.setContactId(updateContact.getContactId());
    updateContactJS.setValues(updateContact.getValues(), updateContact);

    return updateContactJS;
  }

  public UpdateContact toUpdateContact() {
    final UpdateContact updateContact = new UpdateContact();

    updateContact.setContactId(getContactId());
    updateContact.setValues(getValuesAsList());

    return updateContact;
  }

  public native int getContactId() /*-{
		return this.contactId;
	}-*/;

  public native void setContactId(int contactId) /*-{
		this.contactId = contactId;
	}-*/;

  public native JsArray<ValueJS> getValues() /*-{
		return this.values;
	}-*/;

  public List<ValueEventWrapper> getValuesAsList() {
    if(getValues() != null) {
      final ArrayList<ValueEventWrapper> list = new ArrayList<ValueEventWrapper>();

      final JsArray<ValueJS> values = getValues();
      for(int index = 0; index < values.length(); index++) {
        final ValueJS valueJS = values.get(index);
        final ValueEventWrapper valueEventWrapper = valueJS.toValueEventWrapper();
        list.add(valueEventWrapper);
      }

      return list;
    }
    return null;
  }

  public void setValues(List<ValueEventWrapper> values, UpdateContact updateContact) {
    if(values != null) {
      final JsArray<ValueJS> array = (JsArray<ValueJS>) JavaScriptObject.createArray();

      for(final ValueEventWrapper value : values) {
        array.push(ValueJS.toJavaScript(updateContact, value));
      }

      setValues(array);
    }
  }

  public native void setValues(JsArray<ValueJS> values) /*-{
		this.values = values;
	}-*/;
}
