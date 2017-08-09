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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.ContactUnit;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactDTO;

public class GetContactsHandler extends AbstractCommandHandler<GetContacts, ListResult<ContactDTO>> {
  private final ContactDAO contactDAO;
  private final OrgUnitDAO orgUnitDAO;

  @Inject GetContactsHandler(ContactDAO contactDAO, OrgUnitDAO orgUnitDAO) {
    this.contactDAO = contactDAO;
    this.orgUnitDAO = orgUnitDAO;
  }

  @Override
  protected ListResult<ContactDTO> execute(GetContacts command, UserDispatch.UserExecutionContext context) throws CommandException {
    Integer organizationId = context.getUser().getOrganization().getId();
    List<Contact> contacts;
    if (command.getContactIds() == null || command.getContactIds().isEmpty()) {
      contacts = contactDAO.findContactsByTypeAndContactModels(organizationId, command.getType(),
          command.getContactModelIds(), command.isOnlyContactWithoutUser(), command.isWithEmailNotNull(), command.getOrgUnitsIds(), command.getCheckboxElementId());
    } else {
      contacts = contactDAO.findByIds(command.getContactIds());
    }

    List<Integer> contactIds = new ArrayList<Integer>();
    for (Contact contact : contacts) {
      if (!contact.isDeleted()) {
        contactIds.add(contact.getId());
      }
    }

    // Explicitly secondary OrgUnit (to avoid a query for each contact)
    // Load ContactUnit (relation between Contact and OrgUnit)
    List<ContactUnit> contactOrgUnit = orgUnitDAO.getContactUnit(contactIds);
    Set<Integer> orgUnitIds = new HashSet<Integer>();
    Map<Integer, List<Integer>> orgUnitIdsByContactId = new HashMap<Integer, List<Integer>>();

    for (ContactUnit contactUnit : contactOrgUnit) {
      orgUnitIds.add(contactUnit.getIdOrgUnit());
      List<Integer> ids = orgUnitIdsByContactId.get(contactUnit.getIdContact());
      if (ids == null) {
        ids = new ArrayList<Integer>();
        orgUnitIdsByContactId.put(contactUnit.getIdContact(), ids);
      }
      ids.add(contactUnit.getIdOrgUnit());
    }
    // Load OrgUnit
    List<OrgUnit> orgUnits = orgUnitDAO.findByIds(orgUnitIds);
    Map<Integer, OrgUnit> orgUnitsById = new HashMap<Integer, OrgUnit>();
    for (OrgUnit orgUnit : orgUnits) {
      orgUnitsById.put(orgUnit.getId(), orgUnit);
    }

    // Set secondaryOrgUnit for each contact and map it as ContactDTO
    List<ContactDTO> contactDTOs = new ArrayList<>();
    for (Contact contact : contacts) {
      if (contact.isDeleted()) {
        continue;
      }
      List<OrgUnit> secondaryOrgUnits = new ArrayList<OrgUnit>();
      if (orgUnitIdsByContactId.get(contact.getId()) != null) {
        for (Integer orgUnitId : orgUnitIdsByContactId.get(contact.getId())) {
          OrgUnit orgUnit = orgUnitsById.get(orgUnitId);
          if (orgUnit != null) {
            secondaryOrgUnits.add(orgUnit);
          }
        }
      }
      contact.setSecondaryOrgUnits(secondaryOrgUnits);

      contactDTOs.add(mapper().map(contact, new ContactDTO()));
    }

    // FIXME: Add ContactDTO.Mode.MAIN_INFORMATION when dozer 5.5.2 will be ready
    // see https://github.com/DozerMapper/dozer/commit/5e179bb68c91e60d63bf9f44bf64b7ca70f61520
    return new ListResult<>(contactDTOs);
  }
}
