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

import org.sigmah.shared.dto.ContactCardDTO;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

public class ContactCardJS extends JavaScriptObject {
  protected ContactCardJS() {
  }

  public static ContactCardJS toJavaScript(ContactCardDTO contactCardDTO) {
    ContactCardJS contactCardJS = (ContactCardJS) JavaScriptObject.createObject();
    contactCardJS.setId(contactCardDTO.getId());
    contactCardJS.setContactModel(contactCardDTO.getContactModel());
    contactCardJS.setLayout(contactCardDTO.getLayout());
    return contactCardJS;
  }

  public ContactCardDTO toDTO() {
    ContactCardDTO contactCardDTO = new ContactCardDTO();
    contactCardDTO.setId(getId());
    contactCardDTO.setContactModel(getContactModelDTO());
    contactCardDTO.setLayout(getLayoutDTO());
    return contactCardDTO;
  }

  public native int getId() /*-{
		return this.id;
	}-*/;

  public native void setId(int id) /*-{
		this.id = id;
	}-*/;

  public ContactModelDTO getContactModelDTO() {
    return getContactModel().toDTO();
  }

  public native ContactModelJS getContactModel() /*-{
    return this.contactModel;
  }-*/;

  public void setContactModel(ContactModelDTO contactModel) {
    setContactModel(ContactModelJS.toJavaScript(contactModel));
  }

  public native void setContactModel(ContactModelJS contactModel) /*-{
    this.contactModel = contactModel;
  }-*/;

  public LayoutDTO getLayoutDTO() {
    return getLayout().toDTO();
  }

  public native LayoutJS getLayout() /*-{
    return this.layout;
  }-*/;

  public void setLayout(LayoutDTO layout) {
    setLayout(LayoutJS.toJavaScript(layout));
  }

  public native void setLayout(LayoutJS layout) /*-{
    this.layout = layout;
  }-*/;
}
