package org.sigmah.server.domain.element;
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

import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.util.ValueResultUtils;

@Entity
@Table(name = EntityConstants.DEFAULT_CONTACT_FLEXIBLE_ELEMENT_TABLE)
public class DefaultContactFlexibleElement extends FlexibleElement {
  private static final long serialVersionUID = 2545492387257612242L;

  @Column(name = EntityConstants.DEFAULT_CONTACT_FLEXIBLE_ELEMENT_COLUMN_TYPE)
  @Enumerated(EnumType.STRING)
  private DefaultContactFlexibleElementType type;

  public DefaultContactFlexibleElementType getType() {
    return type;
  }

  public void setType(DefaultContactFlexibleElementType type) {
    this.type = type;
  }

  public String getFormattedValue(Contact contact) {
    switch (type) {
      case COUNTRY:
        if (contact.getCountry() == null) {
          return null;
        }
        return contact.getCountry().getName();
      case CREATION_DATE:
        return SimpleDateFormat.getDateTimeInstance().format(contact.getDateCreated());
      case DIRECT_MEMBERSHIP:
        if (contact.getParent() == null) {
          return null;
        }
        return contact.getParent().getFullName();
      case EMAIL_ADDRESS:
        return contact.getEmail();
      case FAMILY_NAME:
        return contact.getName();
      case FIRST_NAME:
        return contact.getFirstname();
      case LOGIN:
        return contact.getLogin();
      case MAIN_ORG_UNIT:
        if (contact.getMainOrgUnit() == null) {
          return null;
        }
        return contact.getMainOrgUnit().getFullName();
      case ORGANIZATION_NAME:
        return contact.getName();
      case PHONE_NUMBER:
        return contact.getPhoneNumber();
      case PHOTO:
        return contact.getPhoto();
      case POSTAL_ADDRESS:
        return contact.getPostalAddress();
      case SECONDARY_ORG_UNITS:
        if (contact.getSecondaryOrgUnits() == null) {
          return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < contact.getSecondaryOrgUnits().size(); i++) {
          if (i != 0) {
            stringBuilder.append(", ");
          }
          stringBuilder.append(contact.getSecondaryOrgUnits().get(i).getFullName());
        }
        return stringBuilder.toString();
      case TOP_MEMBERSHIP:
        if (contact.getRoot() == null) {
          return null;
        }
        return contact.getRoot().getFullName();
      default:
        throw new IllegalStateException();
    }
  }

  public String getSerializedValue(Contact contact) {
    switch (type) {
      case COUNTRY:
        if (contact.getCountry() == null) {
          return null;
        }
        return String.valueOf(contact.getCountry().getId());
      case CREATION_DATE:
        return String.valueOf(contact.getDateCreated().getTime());
      case DIRECT_MEMBERSHIP:
        if (contact.getParent() == null) {
          return null;
        }
        return String.valueOf(contact.getParent().getId());
      case EMAIL_ADDRESS:
        return contact.getEmail();
      case FAMILY_NAME:
        return contact.getName();
      case FIRST_NAME:
        return contact.getFirstname();
      case LOGIN:
        return contact.getLogin();
      case MAIN_ORG_UNIT:
        if (contact.getMainOrgUnit() == null) {
          return null;
        }
        return String.valueOf(contact.getMainOrgUnit().getId());
      case ORGANIZATION_NAME:
        return contact.getName();
      case PHONE_NUMBER:
        return contact.getPhoneNumber();
      case PHOTO:
        return contact.getPhoto();
      case POSTAL_ADDRESS:
        return contact.getPostalAddress();
      case SECONDARY_ORG_UNITS:
        if (contact.getSecondaryOrgUnits() == null) {
          return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < contact.getSecondaryOrgUnits().size(); i++) {
          if (i != 0) {
            stringBuilder.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
          }
          stringBuilder.append(contact.getSecondaryOrgUnits().get(i).getId());
        }
        return stringBuilder.toString();
      case TOP_MEMBERSHIP:
        if (contact.getRoot() == null) {
          return null;
        }
        return String.valueOf(contact.getRoot().getId());
      default:
        throw new IllegalStateException();
    }
  }
}
