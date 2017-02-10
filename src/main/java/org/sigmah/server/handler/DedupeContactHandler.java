package org.sigmah.server.handler;
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

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dao.FlexibleElementDAO;
import org.sigmah.server.dao.HistoryTokenDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ValueDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.DefaultContactFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DedupeContact;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.util.ValueResultUtils;

public class DedupeContactHandler extends AbstractCommandHandler<DedupeContact, ContactDTO> {
  private final ContactDAO contactDAO;
  private final CountryDAO countryDAO;
  private final FlexibleElementDAO flexibleElementDAO;
  private final HistoryTokenDAO historyTokenDAO;
  private final OrgUnitDAO orgUnitDAO;
  private final ValueDAO valueDAO;

  @Inject
  public DedupeContactHandler(ContactDAO contactDAO, CountryDAO countryDAO, FlexibleElementDAO flexibleElementDAO, HistoryTokenDAO historyTokenDAO, OrgUnitDAO orgUnitDAO, ValueDAO valueDAO) {
    this.contactDAO = contactDAO;
    this.countryDAO = countryDAO;
    this.flexibleElementDAO = flexibleElementDAO;
    this.historyTokenDAO = historyTokenDAO;
    this.orgUnitDAO = orgUnitDAO;
    this.valueDAO = valueDAO;
  }

  @Override
  protected ContactDTO execute(DedupeContact command, UserDispatch.UserExecutionContext context) throws CommandException {
    Contact targetedContact = contactDAO.findById(command.getTargetedContactId());
    if (command.getOriginContactId() != null) {
      Contact originContact = contactDAO.findById(command.getOriginContactId());
      mergeContacts(originContact, targetedContact, command.getContactDuplicatedProperties(), context.getUser());
    } else {
      applyProperties(command.getContactDuplicatedProperties(), targetedContact, context.getUser());
    }

    return mapper().map(targetedContact, new ContactDTO());
  }

  private void mergeContacts(Contact originContact, Contact targetedContact, List<ContactDuplicatedProperty> properties, User user) {
    for (ContactDuplicatedProperty property : properties) {
      FlexibleElement flexibleElement = flexibleElementDAO.findById(property.getFlexibleElementId());
      if (flexibleElement instanceof DefaultContactFlexibleElement) {
        applyDefaultValue(originContact, targetedContact, (DefaultContactFlexibleElement) flexibleElement);
        continue;
      }

      Value oldValue = valueDAO.getValueByElementAndContainer(property.getFlexibleElementId(), targetedContact.getId());

      HistoryToken historyToken = valueToHistoryToken(oldValue, user);
      historyTokenDAO.persist(historyToken, user);

      Value newValue = valueDAO.getValueByElementAndContainer(property.getFlexibleElementId(), originContact.getId());
      oldValue.setValue(newValue.getValue());
      valueDAO.persist(oldValue, user);
    }
    originContact.delete();
    contactDAO.persist(originContact, user);
  }

  private HistoryToken valueToHistoryToken(Value value, User user) {
    HistoryToken historyToken = new HistoryToken();
    // XXX: Set comment?
    historyToken.setDate(new Date());
    historyToken.setElementId(value.getElement().getId());
    historyToken.setProjectId(value.getContainerId());
    historyToken.setType(ValueEventChangeType.EDIT);
    historyToken.setValue(value.getValue());
    historyToken.setUser(user);
    return historyToken;
  }

  private void applyDefaultValue(Contact originContact, Contact targetedContact, DefaultContactFlexibleElement flexibleElement) {
    switch (flexibleElement.getType()) {
      case FIRST_NAME:
        targetedContact.setFirstname(originContact.getFirstname());
        break;
      case ORGANIZATION_NAME: // Fall through
      case FAMILY_NAME:
        targetedContact.setName(originContact.getName());
        break;
      case MAIN_ORG_UNIT:
        targetedContact.setMainOrgUnit(originContact.getMainOrgUnit());
        break;
      case SECONDARY_ORG_UNITS:
        targetedContact.setSecondaryOrgUnits(originContact.getSecondaryOrgUnits());
        break;
      case LOGIN:
        targetedContact.setLogin(originContact.getLogin());
        break;
      case EMAIL_ADDRESS:
        targetedContact.setEmail(originContact.getEmail());
        break;
      case PHONE_NUMBER:
        targetedContact.setPhoneNumber(originContact.getPhoneNumber());
        break;
      case POSTAL_ADDRESS:
        targetedContact.setPostalAddress(originContact.getPostalAddress());
        break;
      case PHOTO:
        targetedContact.setPhoto(originContact.getPhoto());
        break;
      case COUNTRY:
        targetedContact.setCountry(originContact.getCountry());
        break;
      case DIRECT_MEMBERSHIP:
        targetedContact.setParent(originContact.getParent());
        break;
      case CREATION_DATE: // Fall through
      case TOP_MEMBERSHIP:
        // NOOP, shouldn't be possible
        break;
      default:
        throw new IllegalStateException("Unknown DefaultContactFlexibleElementType : " + flexibleElement.getType());
    }
  }

  private void applyProperties(List<ContactDuplicatedProperty> properties, Contact contact, User user) {
    // This function is always called when the new contact was in creation
    // So only default values are concerned by the properties
    for (ContactDuplicatedProperty property : properties) {
      DefaultContactFlexibleElement flexibleElement = (DefaultContactFlexibleElement) flexibleElementDAO.findById(property.getFlexibleElementId());
      switch (flexibleElement.getType()) {
        case FIRST_NAME:
          contact.setFirstname(property.getSerializedNewValue());
          break;
        case ORGANIZATION_NAME: // Fall through
        case FAMILY_NAME:
          contact.setName(property.getSerializedNewValue());
          break;
        case MAIN_ORG_UNIT:
          if (property.getSerializedNewValue() == null) {
            contact.setMainOrgUnit(null);
            continue;
          }

          int mainOrgUnitId = Integer.parseInt(property.getSerializedNewValue());
          contact.setMainOrgUnit(orgUnitDAO.findById(mainOrgUnitId));
          break;
        case SECONDARY_ORG_UNITS:
          if (property.getSerializedNewValue() == null) {
            contact.setSecondaryOrgUnits(null);
            continue;
          }

          ArrayList<OrgUnit> orgUnits = new ArrayList<>();
          for (String serializedId : property.getSerializedNewValue().split(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)) {
            int secondaryOrgUnitId = Integer.parseInt(serializedId);
            orgUnits.add(orgUnitDAO.findById(secondaryOrgUnitId));
          }

          contact.setSecondaryOrgUnits(orgUnits);
          break;
        case LOGIN:
          contact.setLogin(property.getSerializedNewValue());
          break;
        case EMAIL_ADDRESS:
          contact.setEmail(property.getSerializedNewValue());
          break;
        case PHONE_NUMBER:
          contact.setPhoneNumber(property.getSerializedNewValue());
          break;
        case POSTAL_ADDRESS:
          contact.setPostalAddress(property.getSerializedNewValue());
          break;
        case PHOTO:
          contact.setPhoto(property.getSerializedNewValue());
          break;
        case COUNTRY:
          if (property.getSerializedNewValue() == null) {
            contact.setCountry(null);
            continue;
          }

          int countryId = Integer.parseInt(property.getSerializedNewValue());
          contact.setCountry(countryDAO.findById(countryId));
          break;
        case DIRECT_MEMBERSHIP:
          if (property.getSerializedNewValue() == null) {
            contact.setParent(null);
            continue;
          }

          int parentId = Integer.parseInt(property.getSerializedNewValue());
          contact.setParent(contactDAO.findById(parentId));
          break;
        case CREATION_DATE: // Fall through
        case TOP_MEMBERSHIP:
          // NOOP, shouldn't be possible
          break;
        default:
          throw new IllegalStateException("Unknown DefaultContactFlexibleElementType : " + flexibleElement.getType());
      }
    }
    contactDAO.persist(contact, user);
  }
}
