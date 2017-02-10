package org.sigmah.shared.dto.referential;
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

import com.google.gwt.core.client.GWT;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

public enum DefaultContactFlexibleElementType implements Result, LogicalElementType {
  FAMILY_NAME(false, true, true, false, 1),
  FIRST_NAME(false, true, true, false, 2),
  ORGANIZATION_NAME(false, true, false, true, 1),
  MAIN_ORG_UNIT(false, false, true, true, null),
  SECONDARY_ORG_UNITS(false, false, true, true, null),
  CREATION_DATE(false, false, true, true, null),
  LOGIN(false, false, false, false, null),
  EMAIL_ADDRESS(true, true, true, true, 4),
  PHONE_NUMBER(true, true, true, true, 5),
  POSTAL_ADDRESS(true, true, true, true, 6),
  PHOTO(true, true, true, true, null),
  COUNTRY(true, true, true, true, 7),
  DIRECT_MEMBERSHIP(true, true, true, true, null),
  TOP_MEMBERSHIP(true, false, true, true, 3);

  private boolean deletable;
  private boolean updatable;
  private boolean visibleForIndividualType;
  private boolean visibleForOrganizationType;
  private Integer defaultCardOrder;

  DefaultContactFlexibleElementType(boolean deletable, boolean updatable, boolean visibleForIndividualType, boolean visibleForOrganizationType, Integer defaultCardOrder) {
    this.deletable = deletable;
    this.updatable = updatable;
    this.visibleForIndividualType = visibleForIndividualType;
    this.visibleForOrganizationType = visibleForOrganizationType;
    this.defaultCardOrder = defaultCardOrder;
  }

  public boolean isDeletable() {
    return deletable;
  }

  public boolean isUpdatable() {
    return updatable;
  }

  public boolean isVisibleForIndividualType() {
    return visibleForIndividualType;
  }

  public boolean isVisibleForOrganizationType() {
    return visibleForOrganizationType;
  }

  public boolean isVisibleForType(ContactModelType type) {
    switch (type) {
      case INDIVIDUAL:
        return isVisibleForIndividualType();
      case ORGANIZATION:
        return isVisibleForOrganizationType();
      default:
        throw new IllegalStateException("Unknown ContactModelType : " + type);
    }
  }

  public Integer getDefaultCardOrder() {
    return defaultCardOrder;
  }

  /**
   * Returns the given {@code flexibleElementType} corresponding name.<br/>
   * This method should be executed from client-side. If executed from server-side, it returns the enum constant name.
   *
   * @param flexibleElementType The flexibleElement type.
   * @return the given {@code flexibleElementType} corresponding name, or {@code null}.
   */
  public static String getName(final DefaultContactFlexibleElementType flexibleElementType) {

    if (flexibleElementType == null) {
      return null;
    }

    if (!GWT.isClient()) {
      return flexibleElementType.name();
    }

    switch (flexibleElementType) {
      case FAMILY_NAME:
        return I18N.CONSTANTS.contactFamilyName();
      case FIRST_NAME:
        return I18N.CONSTANTS.contactFirstName();
      case ORGANIZATION_NAME:
        return I18N.CONSTANTS.contactOrganizationName();
      case MAIN_ORG_UNIT:
        return I18N.CONSTANTS.contactMainOrgUnit();
      case SECONDARY_ORG_UNITS:
        return I18N.CONSTANTS.contactSecondaryOrgUnits();
      case CREATION_DATE:
        return I18N.CONSTANTS.contactCreationDate();
      case LOGIN:
        return I18N.CONSTANTS.contactLogin();
      case EMAIL_ADDRESS:
        return I18N.CONSTANTS.contactEmailAddress();
      case PHONE_NUMBER:
        return I18N.CONSTANTS.contactPhoneNumber();
      case POSTAL_ADDRESS:
        return I18N.CONSTANTS.contactPostalAddress();
      case PHOTO:
        return I18N.CONSTANTS.contactPhoto();
      case COUNTRY:
        return I18N.CONSTANTS.contactCountry();
      case DIRECT_MEMBERSHIP:
        return I18N.CONSTANTS.contactDirectMembership();
      case TOP_MEMBERSHIP:
        return I18N.CONSTANTS.contactTopMembership();
      default:
        return flexibleElementType.name();
    }
  }

  @Override
  public ElementTypeEnum toElementTypeEnum() {
    return ElementTypeEnum.DEFAULT_CONTACT;
  }

  @Override
  public TextAreaType toTextAreaType() {
    return null;
  }

  @Override
  public DefaultFlexibleElementType toDefaultFlexibleElementType() {
    return null;
  }

  @Override
  public DefaultContactFlexibleElementType toDefaultContactFlexibleElementType() {
    return this;
  }

  @Override
  public String getDescription() {
    return I18N.CONSTANTS.flexibleElementDefault() + " (" + getName(this) + ')';
  }
}
