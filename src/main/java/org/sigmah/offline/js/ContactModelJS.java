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

import org.sigmah.shared.dto.ContactCardDTO;
import org.sigmah.shared.dto.ContactDetailsDTO;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

public final class ContactModelJS extends JavaScriptObject {
  protected ContactModelJS() {
  }

  public static ContactModelJS toJavaScript(ContactModelDTO contactModelDTO) {
    ContactModelJS contactModelJS = (ContactModelJS) JavaScriptObject.createObject();
    contactModelJS.setId(contactModelDTO.getId());
    contactModelJS.setName(contactModelDTO.getName());
    contactModelJS.setStatus(contactModelDTO.getStatus());
    contactModelJS.setType(contactModelDTO.getType());
    contactModelJS.setDateMaintenance(contactModelDTO.getDateMaintenance());
    contactModelJS.setDetails(contactModelDTO.getDetails(), contactModelJS);
    contactModelJS.setCard(contactModelDTO.getCard(), contactModelJS);
    return contactModelJS;
  }

  public ContactModelDTO toDTO() {
    ContactModelDTO contactModelDTO = new ContactModelDTO();
    contactModelDTO.setId(getId());
    contactModelDTO.setName(getName());
    contactModelDTO.setStatus(getStatusDTO());
    contactModelDTO.setType(getTypeDTO());
    contactModelDTO.setDateMaintenance(getDateMaintenanceDTO());
    contactModelDTO.setDetails(getDetailsDTO(contactModelDTO));
    contactModelDTO.setCard(getCardDTO(contactModelDTO));
    return contactModelDTO;
  }

  public native int getId() /*-{
    return this.id;
	}-*/;

  public native void setId(int id) /*-{
		this.id = id;
	}-*/;

  public native String getName() /*-{
    return this.name;
  }-*/;

  public native void setName(String name) /*-{
    this.name = name;
  }-*/;

  public ProjectModelStatus getStatusDTO() {
    return ProjectModelStatus.valueOf(getStatus());
  }

  public native String getStatus() /*-{
    return this.status;
  }-*/;

  public void setStatus(ProjectModelStatus status) {
    setStatus(status.name());
  }

  public native void setStatus(String status) /*-{
    this.status = status;
  }-*/;

  public ContactModelType getTypeDTO() {
    return ContactModelType.valueOf(getType());
  }

  public native String getType() /*-{
    return this.type;
  }-*/;

  public void setType(ContactModelType type) {
    setType(type.name());
  }

  public native void setType(String type) /*-{
    this.type = type;
  }-*/;

  public native boolean isUnderMaintenance() /*-{
    return !!this.dateMaintenance;
  }-*/;

  public Date getDateMaintenanceDTO() {
    if (!isUnderMaintenance()) {
      return null;
    }
    return new Date((long) getDateMaintenance().getTime());
  }

  public native JsDate getDateMaintenance() /*-{
    return this.dateMaintenance;
  }-*/;

  public void setDateMaintenance(Date dateMaintenance) {
	  if(dateMaintenance != null)
		  setDateMaintenance(JsDate.create(dateMaintenance.getTime()));
  }

  public native void setDateMaintenance(JsDate dateMaintenance) /*-{
    this.dateMaintenance = dateMaintenance;
  }-*/;

  public ContactDetailsDTO getDetailsDTO() {
    return getDetails().toDTO();
  }

  public ContactDetailsDTO getDetailsDTO(ContactModelDTO contactModelDTO) {
	    return getDetails().toDTO(contactModelDTO);
  }

  public native ContactDetailsJS getDetails() /*-{
    return this.details;
  }-*/;

  public void setDetails(ContactDetailsDTO details) {
	setDetails(ContactDetailsJS.toJavaScript(details));
  }
  
  public void setDetails(ContactDetailsDTO details, ContactModelJS contactModelJS) {
	setDetails(ContactDetailsJS.toJavaScript(details, contactModelJS));
  }

  public native void setDetails(ContactDetailsJS details) /*-{
    this.details = details;
  }-*/;

  public ContactCardDTO getCardDTO() {
    return getCard().toDTO();
  }

  public ContactCardDTO getCardDTO(ContactModelDTO contactModelDTO) {
	return getCard().toDTO(contactModelDTO);
  }

  public native ContactCardJS getCard() /*-{
    return this.card;
  }-*/;

  public void setCard(ContactCardDTO card) {
    setCard(ContactCardJS.toJavaScript(card));
  }
  
  public void setCard(ContactCardDTO card, ContactModelJS contactModelJS) {
	setCard(ContactCardJS.toJavaScript(card, contactModelJS));
  }

  public native void setCard(ContactCardJS card) /*-{
    this.card = card;
  }-*/;
}
