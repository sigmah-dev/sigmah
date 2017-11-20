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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

public class ContactDTO extends AbstractModelDataEntityDTO<Integer> implements DefaultContactFlexibleElementContainer {
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
  public static final String ROOT = "root";
  public static final String DATE_CREATED = "dateCreated";
  public static final String EMAIL_WITH_FULLNAME = "emailWithFullname";
  public static final String TYPE = "type";
  public static final String CHECKBOX_ELEMENT_TO_SET_TO_TRUE = "checkboxElementIdToSetToTrue";

  public enum Mode implements IsMappingMode {
    BASIC_INFORMATION(new MappingField(MAIN_ORG_UNIT), new MappingField(SECONDARY_ORG_UNITS), new MappingField(COUNTRY),
        new MappingField(PARENT), new MappingField(ROOT), new MappingField(CONTACT_MODEL)),

    // Just take basic information.
    MAIN_INFORMATION(
        new CustomMappingField[] { new CustomMappingField(PARENT, Mode.BASIC_INFORMATION) },
        new MappingField(MAIN_ORG_UNIT), new MappingField(SECONDARY_ORG_UNITS), new MappingField(COUNTRY), new MappingField(ROOT)
    ),

    // Take all data.
    ALL(
        new CustomMappingField(MAIN_ORG_UNIT, OrgUnitDTO.Mode.BASE),
        new CustomMappingField(SECONDARY_ORG_UNITS, OrgUnitDTO.Mode.BASE),
        new CustomMappingField(PARENT, Mode.BASIC_INFORMATION),
        new CustomMappingField(ROOT, Mode.BASIC_INFORMATION)
    );

    private final CustomMappingField[] customFields;
    private final MappingField[] excludedFields;

    Mode(final MappingField... excludedFields) {
      this(null, excludedFields);
    }

    Mode(final CustomMappingField... customFields) {
      this(customFields, (MappingField[]) null);
    }

    Mode(final CustomMappingField[] customFields, final MappingField... excludedFields) {
      this.customFields = customFields;
      this.excludedFields = excludedFields;
    }

    @Override
    public String getMapId() {
      return name();
    }

    @Override
    public CustomMappingField[] getCustomFields() {
      return customFields;
    }

    @Override
    public MappingField[] getExcludedFields() {
      return excludedFields;
    }
  }

  public Integer getId() {
    return get(ID);
  }

  public void setId(Integer id) {
    set(ID, id);
  }

  public ContactModelDTO getContactModel() {
    return get(CONTACT_MODEL);
  }

  public void setContactModel(ContactModelDTO contactModel) {
	if(contactModel != null) {
		set(CONTACT_MODEL, contactModel);
    	initType();
	}
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

    initFullName();
  }

  public String getFirstname() {
    return get(FIRSTNAME);
  }

  public void setFirstname(String firstname) {
    set(FIRSTNAME, firstname);

    initFullName();
  }

  public void initFullName() {
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

    initEmailWithFullname();
  }

  public String getFullName() {
    return get(FULLNAME);
  }

  public OrgUnitDTO getMainOrgUnit() {
    return get(MAIN_ORG_UNIT);
  }

  public void setMainOrgUnit(OrgUnitDTO mainOrgUnit) {
    set(MAIN_ORG_UNIT, mainOrgUnit);
  }

  public List<OrgUnitDTO> getSecondaryOrgUnits() {
    return get(SECONDARY_ORG_UNITS);
  }

  public void setSecondaryOrgUnits(List<OrgUnitDTO> secondaryOrgUnits) {
    set(SECONDARY_ORG_UNITS, secondaryOrgUnits);
  }

  public Set<Integer> getOrgUnitIds() {
    HashSet<Integer> ids = new HashSet<Integer>();
    ids.add(getMainOrgUnit().getId());
    for (OrgUnitDTO orgUnitDTO : getSecondaryOrgUnits()) {
      ids.add(orgUnitDTO.getId());
    }
    return ids;
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

    initEmailWithFullname();
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

  public CountryDTO getCountry() {
    return get(COUNTRY);
  }

  public void setCountry(CountryDTO country) {
    set(COUNTRY, country);
  }

  public ContactDTO getParent() {
    return get(PARENT);
  }

  public void setParent(ContactDTO parent) {
    set(PARENT, parent);
  }

  public ContactDTO getRoot() {
    return get(ROOT);
  }

  public void setRoot(ContactDTO root) {
    set(ROOT, root);
  }

  public Date getDateCreated() {
    return get(DATE_CREATED);
  }

  public void setDateCreated(Date dateCreated) {
    set(DATE_CREATED, dateCreated);
  }

  @Override
  public String getEntityName() {
    return ENTITY_NAME;
  }

  @Override
  public String getFamilyName() {
    return getName();
  }

  @Override
  public String getOrganizationName() {
    return getName();
  }

  public String getEmailWithFullname() {
    return get(EMAIL_WITH_FULLNAME);
  }

  public void initEmailWithFullname() {
    String fullName = getFullName();
    String email = getEmail();
    if (fullName == null || fullName.isEmpty()) {
      set(EMAIL_WITH_FULLNAME, email);
    }
    set(EMAIL_WITH_FULLNAME, email + " (" + fullName + ")");
  }

  public ContactModelType getType() {
    return get(TYPE);
  }

  public void initType() {
    ContactModelDTO model = getContactModel();
    ContactModelType type = model.getType();

    set(TYPE, type);
  }

  /**
   * Gets all the flexible elements instances of the given class in this organizational unit (details page). The banner
   * is ignored because the elements in it are read-only.
   *
   * @param clazz
   *          The class of the searched flexible elements.
   * @return The elements localized for the given class, or <code>null</code> if there is no element of this class.
   */
  public List<LocalizedElement> getLocalizedElements(Class<? extends FlexibleElementDTO> clazz) {

    final ArrayList<LocalizedElement> elements = new ArrayList<LocalizedElement>();

    final List<ContactModelDTO.LocalizedElement> localizedElements = getContactModel().getLocalizedElements(clazz);

    if (localizedElements != null) {
      for (final ContactModelDTO.LocalizedElement localized : localizedElements) {
        elements.add(new LocalizedElement(localized));
      }
    }

    return elements;
  }

  public static final class LocalizedElement extends OrgUnitModelDTO.LocalizedElement {
    public LocalizedElement(ContactModelDTO.LocalizedElement localized) {
      super(localized.getElement());
    }
  }
}
