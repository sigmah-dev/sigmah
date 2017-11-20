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

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.FrameworkFulfillmentDAO;
import org.sigmah.server.dao.LayoutGroupDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.ValueDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.FrameworkElementImplementation;
import org.sigmah.server.domain.FrameworkFulfillment;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.ContactListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetContactRelationshipsInFramework;
import org.sigmah.shared.command.result.ContactRelationship;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.util.ValueResultUtils;

public class GetContactRelationshipsInFrameworkHandler extends AbstractCommandHandler<GetContactRelationshipsInFramework, ListResult<ContactRelationship>> {
  private final ValueDAO valueDAO;
  private final ContactDAO contactDAO;
  private final ProjectDAO projectDAO;
  private final OrgUnitDAO orgUnitDAO;
  private final LayoutGroupDAO layoutGroupDAO;
  private final I18nServer i18nServer;
  private final FrameworkFulfillmentDAO frameworkFulfillmentDAO;

  @Inject
  public GetContactRelationshipsInFrameworkHandler(ValueDAO valueDAO, ContactDAO contactDAO, ProjectDAO projectDAO, OrgUnitDAO orgUnitDAO, LayoutGroupDAO layoutGroupDAO, I18nServer i18nServer, FrameworkFulfillmentDAO frameworkFulfillmentDAO) {
    this.valueDAO = valueDAO;
    this.contactDAO = contactDAO;
    this.projectDAO = projectDAO;
    this.orgUnitDAO = orgUnitDAO;
    this.layoutGroupDAO = layoutGroupDAO;
    this.i18nServer = i18nServer;
    this.frameworkFulfillmentDAO = frameworkFulfillmentDAO;
  }

  @Override
  protected ListResult<ContactRelationship> execute(GetContactRelationshipsInFramework command, UserDispatch.UserExecutionContext context) throws CommandException {
    List<ContactRelationship> relations = new ArrayList<>();

    // Retrieving the available framework fulfillments
    for (FrameworkFulfillment frameworkFulfillment : frameworkFulfillmentDAO.findByFrameworkId(command.getFrameworkId())) {
      for (FrameworkElementImplementation frameworkElementImplementation : frameworkFulfillment.getFrameworkElementImplementations()) {
        // Implementation is about contact lists
        if (ElementTypeEnum.CONTACT_LIST.equals(frameworkElementImplementation.getFrameworkElement().getDataType())) {
          FlexibleElement flexibleElement = frameworkElementImplementation.getFlexibleElement();
          LayoutGroup layoutGroup = layoutGroupDAO.getByElementId(flexibleElement.getId());
          for (Value value : valueDAO.findValuesByFlexibleElementId(flexibleElement.getId())) {
            for (Integer relatedContactId : ValueResultUtils.splitValuesAsInteger(value.getValue())) {
              if (!relatedContactId.equals(command.getContactId())) {
                continue;
              }
              ContactRelationship contactRelationship = new ContactRelationship();
              contactRelationship.setFieldName(flexibleElement.getLabel());
              contactRelationship.setGroupName(layoutGroup.getTitle());
              if (((ContactListElement) flexibleElement).isMember() && ((ContactListElement) flexibleElement).getAllowedType() == ContactModelType.ORGANIZATION) {
                contactRelationship.setDirection(ContactRelationship.Direction.INBOUND);
              } else {
                contactRelationship.setDirection(ContactRelationship.Direction.OUTBOUND);
              }
              contactRelationship.setRelationshipId(relatedContactId);

              applyContainer(contactRelationship, value.getContainerId(), context.getLanguage());

              relations.add(contactRelationship);
            }
          }
        }
      }
    }
    return new ListResult<>(relations);
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
