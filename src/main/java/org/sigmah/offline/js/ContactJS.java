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
import com.google.gwt.core.client.JsDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

public final class ContactJS extends JavaScriptObject {
  protected ContactJS() {
  }

  public static ContactJS toJavaScript(ContactDTO contactDTO) {
    ContactJS contactJS = Values.createJavaScriptObject(ContactJS.class);
    contactJS.setId(contactDTO.getId());
    contactJS.setContactModel(contactDTO.getContactModel());
    if (contactDTO.getUserId() != null) {
      contactJS.setUserId(contactDTO.getUserId());
    }
    if (contactDTO.getOrganizationId() != null) {
      contactJS.setOrganizationId(contactDTO.getOrganizationId());
    }
    contactJS.setName(contactDTO.getName());
    contactJS.setFirstName(contactDTO.getFirstname());
    contactJS.setMainOrgUnit(contactDTO.getMainOrgUnit());
    contactJS.setSecondaryOrgUnits(contactDTO.getSecondaryOrgUnits());
    contactJS.setLogin(contactDTO.getLogin());
    contactJS.setEmail(contactDTO.getEmail());
    contactJS.setPhoneNumber(contactDTO.getPhoneNumber());
    contactJS.setPostalAddress(contactDTO.getPostalAddress());
    contactJS.setPhoto(contactDTO.getPhoto());
    contactJS.setCountry(contactDTO.getCountry());
    contactJS.setParent(contactDTO.getParent());
    contactJS.setDateCreated(contactDTO.getDateCreated());
    return contactJS;
  }

  public ContactDTO toDTO() {
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setId(getId());
    contactDTO.setContactModel(getContactModelDTO());
    if (hasUserId()) {
      contactDTO.setUserId(getUserId());
    }
    if (hasOrganizationId()) {
      contactDTO.setOrganizationId(getOrganizationId());
    }
    contactDTO.setName(getName());
    contactDTO.setFirstname(getFirstName());
    contactDTO.setMainOrgUnit(getMainOrgUnitDTO());
    contactDTO.setSecondaryOrgUnits(getSecondaryOrgUnitDTOs());
    contactDTO.setLogin(getLogin());
    contactDTO.setEmail(getEmail());
    contactDTO.setPhoneNumber(getPhoneNumber());
    contactDTO.setPostalAddress(getPostalAddress());
    contactDTO.setPhoto(getPhoto());
    contactDTO.setCountry(getCountryDTO());
    contactDTO.setParent(getParentDTO());
    contactDTO.setDateCreated(getDateCreatedDTO());
    return contactDTO;
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

  public native boolean hasUserId() /*-{
    return !!this.userId;
  }-*/;

  public native int getUserId() /*-{
    return this.userId;
  }-*/;

  public native void setUserId(int userId) /*-{
    this.userId = userId;
  }-*/;

  public native boolean hasOrganizationId() /*-{
    return !!this.userId;
  }-*/;

  public native int getOrganizationId() /*-{
    return this.organizationId;
  }-*/;

  public native void setOrganizationId(int organizationId) /*-{
    this.organizationId = organizationId;
  }-*/;

  public native String getName() /*-{
    return this.name;
  }-*/;

  public native void setName(String name) /*-{
    this.name = name;
  }-*/;

  public native String getFirstName() /*-{
    return this.firstName;
  }-*/;

  public native void setFirstName(String firstName) /*-{
    this.firstName = firstName;
  }-*/;

  public OrgUnitDTO getMainOrgUnitDTO() {
    return getMainOrgUnit().toDTO();
  }

  public native OrgUnitJS getMainOrgUnit() /*-{
    return this.mainOrgUnit;
  }-*/;

  public void setMainOrgUnit(OrgUnitDTO mainOrgUnit) {
    setMainOrgUnit(OrgUnitJS.toJavaScript(mainOrgUnit));
  }

  public native void setMainOrgUnit(OrgUnitJS mainOrgUnit) /*-{
    this.mainOrgUnit = mainOrgUnit;
  }-*/;

  public List<OrgUnitDTO> getSecondaryOrgUnitDTOs() {
    JsArray<OrgUnitJS> secondaryOrgUnits = getSecondaryOrgUnits();
    List<OrgUnitDTO> secondaryOrgUnitDTOs = new ArrayList<OrgUnitDTO>(secondaryOrgUnits.length());
    for (int i = 0; i < secondaryOrgUnits.length(); i++) {
      secondaryOrgUnitDTOs.add(secondaryOrgUnits.get(i).toDTO());
    }
    return secondaryOrgUnitDTOs;
  }

  public native JsArray<OrgUnitJS> getSecondaryOrgUnits() /*-{
    return this.secondaryOrgUnits;
  }-*/;

  @SuppressWarnings("unchecked")
  public void setSecondaryOrgUnits(List<OrgUnitDTO> secondaryOrgUnitDTOs) {
    JsArray<OrgUnitJS> secondaryOrgUnits = (JsArray<OrgUnitJS>) JavaScriptObject.createArray();
    for (OrgUnitDTO orgUnitDTO : secondaryOrgUnitDTOs) {
      secondaryOrgUnits.push(OrgUnitJS.toJavaScript(orgUnitDTO));
    }

    setSecondaryOrgUnits(secondaryOrgUnits);
  }

  public native void setSecondaryOrgUnits(JsArray<OrgUnitJS> secondaryOrgUnits) /*-{
    this.secondaryOrgUnits = secondaryOrgUnits;
  }-*/;

  public native String getLogin() /*-{
    return this.login;
  }-*/;

  public native void setLogin(String login) /*-{
    this.login = login;
  }-*/;

  public native String getEmail() /*-{
    return this.email;
  }-*/;

  public native void setEmail(String email) /*-{
    this.email = email;
  }-*/;

  public native String getPhoneNumber() /*-{
    return this.phoneNumber;
  }-*/;

  public native void setPhoneNumber(String phoneNumber) /*-{
    this.phoneNumber = phoneNumber;
  }-*/;

  public native String getPostalAddress() /*-{
    return this.postalAddress;
  }-*/;

  public native void setPostalAddress(String postalAddress) /*-{
    this.postalAddress = postalAddress;
  }-*/;

  public native String getPhoto() /*-{
    return this.photo;
  }-*/;

  public native void setPhoto(String photo) /*-{
    this.photo = photo;
  }-*/;

  public CountryDTO getCountryDTO() {
    if (getCountry() == null) {
      return null;
    }
    return getCountry().toDTO();
  }

  public native CountryJS getCountry() /*-{
    return this.country;
  }-*/;

  public void setCountry(CountryDTO country) {
    if (country == null) {
      return;
    }
    setCountry(CountryJS.toJavaScript(country));
  }

  public native void setCountry(CountryJS country) /*-{
    this.country = country;
  }-*/;

  public ContactDTO getParentDTO() {
    if (getParent() == null) {
      return null;
    }
    return getParent().toDTO();
  }

  public native ContactJS getParent() /*-{
    return this.parent;
  }-*/;

  public void setParent(ContactDTO parent) {
    if (parent == null) {
      return;
    }
    setParent(ContactJS.toJavaScript(parent));
  }

  public native void setParent(ContactJS parent) /*-{
    this.parent = parent;
  }-*/;

  public Date getDateCreatedDTO() {
    JsDate dateCreated = getDateCreated();
    return new Date((long) dateCreated.getTime());
  }

  public native JsDate getDateCreated() /*-{
    return this.dateCreated;
  }-*/;

  public void setDateCreated(Date dateCreated) {
    setDateCreated(JsDate.create(dateCreated.getTime()));
  }

  public native void setDateCreated(JsDate dateCreated) /*-{
    this.dateCreated = dateCreated;
  }-*/;
}
