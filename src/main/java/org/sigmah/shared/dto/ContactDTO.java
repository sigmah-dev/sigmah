package org.sigmah.shared.dto;
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

import java.util.Set;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

public class ContactDTO extends AbstractModelDataEntityDTO<Integer> {
  private static final long serialVersionUID = -6688968774985926447L;

  public static final String ENTITY_NAME = "Contact";

  public static final String ID = "id";
  public static final String CONTACT_MODEL = "contactModel";
  public static final String USER = "user";
  public static final String ORGANIZATION = "organization";
  public static final String NAME = "name";
  public static final String FIRSTNAME = "firstname";
  public static final String FULLNAME = "fullname";
  public static final String MAIN_ORG_UNIT = "mainOrgUnit";
  public static final String SECONDARY_ORG_UNITS = "secondaryOrgUnits";
  public static final String LOGIN = "login";
  public static final String EMAIL = "email";
  public static final String PHONE_NUMBER = "phoneNumber";
  public static final String POSTAL_ADDRESS = "postalAddress";
  public static final String PHOTO = "photo";
  public static final String COUNTRY = "country";
  public static final String PARENT = "parent";
  public static final String DATE_CREATED = "dateCreated";

  public Integer getId() {
    return get(ID);
  }

  public void setId(Integer id) {
    set(ID, id);
  }

  public Integer getContactModelId() {
    return get(CONTACT_MODEL);
  }

  public void setContactModelId(Integer contactModelId) {
    set(CONTACT_MODEL, contactModelId);
  }

  public Integer getUserId() {
    return get(USER);
  }

  public void setUserId(Integer userId) {
    set(USER, userId);
  }

  public Integer getOrganizationId() {
    return get(ORGANIZATION);
  }

  public void setOrganizationId(Integer organizationId) {
    set(ORGANIZATION, organizationId);
  }

  public String getName() {
    return get(NAME);
  }

  public void setName(String name) {
    set(NAME, name);

    initFullname();
  }

  public String getFirstname() {
    return get(FIRSTNAME);
  }

  public void setFirstname(String firstname) {
    set(FIRSTNAME, firstname);

    initFullname();
  }

  public void initFullname() {
    String firstname = getFirstname();
    String name = getName();

    if (firstname == null && name == null) {
      return;
    }

    if (firstname == null) {
      set(FULLNAME, name.toUpperCase());
      return;
    }

    if (name == null) {
      set(FULLNAME, firstname);
      return;
    }

    set(FULLNAME, firstname + " " + name.toUpperCase());
  }

  public Integer getMainOrgUnitId() {
    return get(MAIN_ORG_UNIT);
  }

  public void setMainOrgUnitId(Integer mainOrgUnitId) {
    set(MAIN_ORG_UNIT, mainOrgUnitId);
  }

  public Set<Integer> getSecondaryOrgUnitIds() {
    return get(SECONDARY_ORG_UNITS);
  }

  public void setSecondaryOrgUnitIds(Set<Integer> secondaryOrgUnitIds) {
    set(SECONDARY_ORG_UNITS, secondaryOrgUnitIds);
  }

  public String getLogin() {
    return get(LOGIN);
  }

  public void setLogin(String login) {
    set(LOGIN, login);
  }

  public String getEmail() {
    return get(EMAIL);
  }

  public void setEmail(String email) {
    set(EMAIL, email);
  }

  public String getPhoneNumber() {
    return get(PHONE_NUMBER);
  }

  public void setPhoneNumber(String phoneNumber) {
    set(PHONE_NUMBER, phoneNumber);
  }

  public String getPostalAddress() {
    return get(POSTAL_ADDRESS);
  }

  public void setPostalAddress(String postalAddress) {
    set(POSTAL_ADDRESS, postalAddress);
  }

  public String getPhoto() {
    return get(PHOTO);
  }

  public void setPhoto(String photo) {
    set(PHOTO, photo);
  }

  public Integer getCountryId() {
    return get(COUNTRY);
  }

  public void setCountryId(Integer countryId) {
    set(COUNTRY, countryId);
  }

  public Integer getParentId() {
    return get(PARENT);
  }

  public void setParentId(Integer parent) {
    set(PARENT, parent);
  }


  public String getDateCreated() {
    return get(DATE_CREATED);
  }

  public void setDateCreated(String dateCreated) {
    set(DATE_CREATED, dateCreated);
  }

  @Override
  public String getEntityName() {
    return ENTITY_NAME;
  }
}
