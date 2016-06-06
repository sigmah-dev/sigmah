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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.LayoutGroupDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.ValueDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.ContactListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetContactRelationships;
import org.sigmah.shared.command.result.ContactRelationship;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.util.ValueResultUtils;

public class GetContactRelationshipsHandler extends AbstractCommandHandler<GetContactRelationships, ListResult<ContactRelationship>> {
  private final ValueDAO valueDAO;
  private final ContactDAO contactDAO;
  private final ProjectDAO projectDAO;
  private final OrgUnitDAO orgUnitDAO;
  private final LayoutGroupDAO layoutGroupDAO;
  private final I18nServer i18nServer;

  @Inject
  public GetContactRelationshipsHandler(ValueDAO valueDAO, ContactDAO contactDAO, ProjectDAO projectDAO, OrgUnitDAO orgUnitDAO, LayoutGroupDAO layoutGroupDAO, I18nServer i18nServer) {
    this.valueDAO = valueDAO;
    this.contactDAO = contactDAO;
    this.projectDAO = projectDAO;
    this.orgUnitDAO = orgUnitDAO;
    this.layoutGroupDAO = layoutGroupDAO;
    this.i18nServer = i18nServer;
  }

  @Override
  protected ListResult<ContactRelationship> execute(GetContactRelationships command, UserDispatch.UserExecutionContext context) throws CommandException {
    List<ContactRelationship> contactRelationships = new ArrayList<>();
    if (command.getDirections().contains(ContactRelationship.Direction.INBOUND)) {
      contactRelationships.addAll(getInboundRelationships(command.getContactId(), context.getLanguage()));
    }
    if (command.getDirections().contains(ContactRelationship.Direction.OUTBOUND)) {
      contactRelationships.addAll(getOutboundRelationships(command.getContactId(), context.getLanguage()));
    }
    return new ListResult<>(contactRelationships);
  }

  private List<ContactRelationship> getInboundRelationships(Integer contactId, Language language) {
    List<ContactRelationship> relations = new ArrayList<>();

    List<Value> values = valueDAO.findValuesByContainerId(contactId);
    for (Value value : values) {
      FlexibleElement flexibleElement = value.getElement();
      if (!(flexibleElement instanceof ContactListElement)) {
        // the element must be a ContactListElement
        continue;
      }

      LayoutGroup layoutGroup = layoutGroupDAO.getByElementId(flexibleElement.getId());
      Set<Integer> contactIds = new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(value.getValue()));
      for (Integer relatedContactId : contactIds) {
        ContactRelationship contactRelationship = new ContactRelationship();
        contactRelationship.setFieldName(flexibleElement.getLabel());
        contactRelationship.setGroupName(layoutGroup.getTitle());
        if (((ContactListElement) flexibleElement).isMember() && ((ContactListElement) flexibleElement).getAllowedType() == ContactModelType.ORGANIZATION) {
          contactRelationship.setDirection(ContactRelationship.Direction.INBOUND);
        }
        contactRelationship.setRelationshipId(relatedContactId);

        applyContainer(contactRelationship, relatedContactId, language);

        relations.add(contactRelationship);
      }
    }

    return relations;
  }

  private List<ContactRelationship> getOutboundRelationships(Integer contactId, Language language) {
    List<ContactRelationship> relations = new ArrayList<>();

    List<Value> values = valueDAO.findValuesByIdInSerializedValue(contactId);
    for (Value value : values) {
      FlexibleElement flexibleElement = value.getElement();
      LayoutGroup layoutGroup = layoutGroupDAO.getByElementId(flexibleElement.getId());
      if (!(flexibleElement instanceof ContactListElement)) {
        // the element must be a ContactListElement
        continue;
      }

      ContactRelationship contactRelationship = new ContactRelationship();
      contactRelationship.setFieldName(flexibleElement.getLabel());
      contactRelationship.setGroupName(layoutGroup.getTitle());
      if (((ContactListElement) flexibleElement).isMember() && ((ContactListElement) flexibleElement).getAllowedType() == ContactModelType.ORGANIZATION) {
        contactRelationship.setDirection(ContactRelationship.Direction.OUTBOUND);
      }
      contactRelationship.setRelationshipId(value.getContainerId());

      applyContainer(contactRelationship, value.getContainerId(), language);

      relations.add(contactRelationship);
    }

    return relations;
  }

  private void applyContainer(ContactRelationship contactRelationship, Integer containerId, Language language) {
    Contact contact = contactDAO.findById(containerId);
    if (contact != null) {
      contactRelationship.setType(ContactRelationship.Type.CONTACT);
      contactRelationship.setFormattedType(contact.getContactModel().getName());
      contactRelationship.setName(contact.getFullName());
      return;
    }

    Project project = projectDAO.findById(containerId);
    if (project != null) {
      contactRelationship.setType(ContactRelationship.Type.PROJECT);
      contactRelationship.setFormattedType(i18nServer.t(language, "contactRelationshipProjectType"));
      if (project.getFullName() != null && !project.getFullName().isEmpty()) {
        contactRelationship.setName(project.getName() + " - " + project.getFullName());
      } else {
        contactRelationship.setName(project.getName());
      }
      return;
    }

    OrgUnit orgUnit = orgUnitDAO.findById(containerId);
    if (orgUnit != null) {
      contactRelationship.setType(ContactRelationship.Type.ORGUNIT);
      contactRelationship.setFormattedType(i18nServer.t(language, "contactRelationshipOrgUnitType"));
      if (orgUnit.getFullName() != null && !orgUnit.getFullName().isEmpty()) {
        contactRelationship.setName(orgUnit.getName() + " - " + orgUnit.getFullName());
      } else {
        contactRelationship.setName(orgUnit.getName());
      }
    }
  }
}
