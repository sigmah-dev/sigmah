package org.sigmah.server.service;
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

import java.util.Date;
import java.util.Set;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.ContactModelDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactDTO;

public class ContactService extends AbstractEntityService<Contact, Integer, ContactDTO> {
  private final ContactDAO contactDAO;
  private final ContactModelDAO contactModelDAO;
  private final OrgUnitDAO orgUnitDAO;

  @Inject
  public ContactService(ContactDAO contactDAO, ContactModelDAO contactModelDAO, OrgUnitDAO orgUnitDAO) {
    this.contactDAO = contactDAO;
    this.contactModelDAO = contactModelDAO;
    this.orgUnitDAO = orgUnitDAO;
  }

  @Override
  public Contact create(PropertyMap properties, UserDispatch.UserExecutionContext context) throws CommandException {
    Contact contact = generateContact(properties);
    if (contact == null) {
      return null;
    }

    return contactDAO.persist(contact, context.getUser());
  }

  public Contact createVirtual(PropertyMap properties, UserDispatch.UserExecutionContext context) throws CommandException {
    Contact contact = generateContact(properties);
    if (contact == null) {
      return null;
    }

    return contact;
  }

  @Override
  public Contact update(Integer entityId, PropertyMap changes, UserDispatch.UserExecutionContext context) throws CommandException {
    throw new UnsupportedOperationException();
  }

  public Contact generateContact(PropertyMap properties) {
    Integer contactModelId = properties.get(ContactDTO.CONTACT_MODEL);
    if (contactModelId == null) {
      return null;
    }
    String login = properties.get(ContactDTO.LOGIN);
    String firstName = properties.get(ContactDTO.FIRSTNAME);
    String name = properties.get(ContactDTO.NAME);
    Integer mainOrgUnitId = properties.get(ContactDTO.MAIN_ORG_UNIT);
    Set<Integer> secondaryOrgUnitIds = properties.get(ContactDTO.SECONDARY_ORG_UNITS);
    if (mainOrgUnitId == null && secondaryOrgUnitIds != null && !secondaryOrgUnitIds.isEmpty()) {
      // no main org unit no secondary org unit
      secondaryOrgUnitIds = null;
    }

    ContactModel contactModel = contactModelDAO.findById(contactModelId);
    if (contactModel == null) {
      return null;
    }

    Contact contact = new Contact();
    contact.setContactModel(contactModel);
    contact.setLogin(login);
    contact.setFirstname(firstName);
    contact.setName(name);
    if (mainOrgUnitId != null) {
      contact.setMainOrgUnit(orgUnitDAO.findById(mainOrgUnitId));
    }
    if (secondaryOrgUnitIds != null) {
      contact.setSecondaryOrgUnits(orgUnitDAO.findByIds(secondaryOrgUnitIds));
    }
    contact.setDateCreated(new Date());
    return contact;
  }
}
