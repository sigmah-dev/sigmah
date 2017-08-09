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
import java.util.Map;
import java.util.Set;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.ContactModelDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ValueDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactDTO;

public class ContactService extends AbstractEntityService<Contact, Integer, ContactDTO> {
  private final ContactDAO contactDAO;
  private final ContactModelDAO contactModelDAO;
  private final OrgUnitDAO orgUnitDAO;
  private final ValueDAO valueDAO;

  @Inject
  public ContactService(ContactDAO contactDAO, ContactModelDAO contactModelDAO, OrgUnitDAO orgUnitDAO, ValueDAO valueDAO) {
    this.contactDAO = contactDAO;
    this.contactModelDAO = contactModelDAO;
    this.orgUnitDAO = orgUnitDAO;
    this.valueDAO = valueDAO;
  }

  @Override
  public Contact create(PropertyMap properties, UserDispatch.UserExecutionContext context) throws CommandException {
    Contact contact = generateContact(properties);
    if (contact == null) {
      return null;
    }

    Contact persistedContact = contactDAO.persist(contact, context.getUser());
    if (properties.containsKey(ContactDTO.CHECKBOX_ELEMENT_TO_SET_TO_TRUE)) {
      Value value = new Value();
      value.setContainerId(contact.getId());
      value.setValue("true");
      value.setElement(em().find(FlexibleElement.class, properties.get(ContactDTO.CHECKBOX_ELEMENT_TO_SET_TO_TRUE)));
      value.setLastModificationAction('C');
      value.setLastModificationDate(new Date());
      value.setLastModificationUser(context.getUser());
      valueDAO.persist(value, context.getUser());
    }
    return persistedContact;
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

    for (Map.Entry<String, Object> entry : changes.entrySet()) {
      if ("dateDeleted".equals(entry.getKey())) {

        // Get the current contact
        Contact contact = em().find(Contact.class, entityId);

        // Mark the project in the state "deleted" (but don't delete it
        // really)
        contact.delete();

        // Save
        em().merge(contact);
      }
    }

    return em().find(Contact.class, entityId);
  }

  public Contact generateContact(PropertyMap properties) {
    Integer contactModelId = properties.get(ContactDTO.CONTACT_MODEL);
    if (contactModelId == null) {
      return null;
    }
    String email = properties.get(ContactDTO.EMAIL);
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
    contact.setEmail(email);
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
