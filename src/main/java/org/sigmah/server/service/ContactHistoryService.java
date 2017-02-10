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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.FlexibleElementDAO;
import org.sigmah.server.dao.HistoryTokenDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.ContactListElement;
import org.sigmah.server.domain.element.DefaultContactFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

public class ContactHistoryService {
  private final ContactDAO contactDAO;
  private final FlexibleElementDAO flexibleElementDAO;
  private final OrgUnitDAO orgUnitDAO;
  private final ProjectDAO projectDAO;
  private final HistoryTokenDAO historyTokenDAO;
  private final I18nServer i18nServer;
  private final ModelPropertyService modelPropertyService;

  @Inject
  public ContactHistoryService(ContactDAO contactDAO, FlexibleElementDAO flexibleElementDAO, OrgUnitDAO orgUnitDAO,
                               ProjectDAO projectDAO, HistoryTokenDAO historyTokenDAO, I18nServer i18nServer,
                               ModelPropertyService modelPropertyService) {
    this.contactDAO = contactDAO;
    this.flexibleElementDAO = flexibleElementDAO;
    this.orgUnitDAO = orgUnitDAO;
    this.projectDAO = projectDAO;
    this.historyTokenDAO = historyTokenDAO;
    this.i18nServer = i18nServer;
    this.modelPropertyService = modelPropertyService;
  }

  public List<ContactHistory> findHistory(Integer contactId, Language language) {
    Contact contact = contactDAO.findById(contactId);
    List<ContactHistory> contactHistories = new ArrayList<ContactHistory>();
    contactHistories.addAll(generateHistoryFromContactValues(contact, language));
    contactHistories.addAll(generateHistoryFromInboundRelationships(contact, language));

    // Let's sort all these items by their date
    Collections.sort(contactHistories, new Comparator<ContactHistory>() {
      @Override
      public int compare(ContactHistory first, ContactHistory second) {
        return -first.getUpdatedAt().compareTo(second.getUpdatedAt());
      }
    });

    return contactHistories;
  }

  private List<ContactHistory> generateHistoryFromContactValues(Contact contact, Language language) {
    List<ContactHistory> contactHistories = new ArrayList<ContactHistory>();
    for (LayoutGroup layoutGroup : contact.getContactModel().getDetails().getLayout().getGroups()) {
      for (LayoutConstraint layoutConstraint : layoutGroup.getConstraints()) {
        FlexibleElement flexibleElement = layoutConstraint.getElement();

        List<HistoryToken> historyTokens = historyTokenDAO.findByContainerIdAndFlexibleElementId(contact.getId(), flexibleElement.getId());
        for (HistoryToken historyToken : historyTokens) {
          ContactHistory contactHistory = new ContactHistory();
          contactHistory.setId(historyToken.getId());
          contactHistory.setComment(historyToken.getComment());
          contactHistory.setUpdatedAt(historyToken.getDate());

          if (historyToken.getUser() != null) {
            contactHistory.setUserFullName(historyToken.getUser().getFullName());
          }
          if (flexibleElement instanceof ContactListElement) {
            contactHistory.setFormattedChangeType(getRelationshipFormatter("Contact", historyToken.getType(), language));
            Contact relatedContact = contactDAO.findById(Integer.parseInt(historyToken.getValue()));
            contactHistory.setSubject(relatedContact.getFullName());
            contactHistory.setFormattedValue(flexibleElement.getLabel());
            contactHistory.setValueType(ContactHistory.ValueType.STRING);
          } else {
            contactHistory.setFormattedChangeType(i18nServer.t(language, "contactHistoryChangeTypeUpdatedProperty"));
            contactHistory.setFormattedValue(modelPropertyService.getFormattedValue(flexibleElement, historyToken.getValue(), language));

            if (flexibleElement instanceof DefaultContactFlexibleElement) {
              contactHistory.setSubject(modelPropertyService.getDefaultContactPropertyLabel(((DefaultContactFlexibleElement) flexibleElement).getType(), language));

              if (((DefaultContactFlexibleElement) flexibleElement).getType() == DefaultContactFlexibleElementType.PHOTO) {
                contactHistory.setValueType(ContactHistory.ValueType.IMAGE);
              } else {
                contactHistory.setValueType(ContactHistory.ValueType.STRING);
              }
            } else {
              contactHistory.setSubject(flexibleElement.getLabel());
              contactHistory.setValueType(ContactHistory.ValueType.STRING);
            }
          }
          contactHistories.add(contactHistory);
        }
      }
    }
    return contactHistories;
  }

  private List<ContactHistory> generateHistoryFromInboundRelationships(Contact contact, Language language) {
    List<ContactHistory> contactHistories = new ArrayList<ContactHistory>();
    List<HistoryToken> historyTokens = historyTokenDAO.findByIdInSerializedValue(contact.getId());
    for (HistoryToken historyToken : historyTokens) {
      FlexibleElement flexibleElement = flexibleElementDAO.findById(historyToken.getElementId());
      // We are looking here for relationships going on the current contact
      // The flexible element must be a contact list element
      if (!(flexibleElement instanceof ContactListElement)) {
        continue;
      }

      ContactHistory contactHistory = new ContactHistory();
      contactHistories.add(contactHistory);

      contactHistory.setId(historyToken.getId());
      contactHistory.setComment(historyToken.getComment());
      contactHistory.setUpdatedAt(historyToken.getDate());
      contactHistory.setFormattedValue(flexibleElement.getLabel());
      contactHistory.setValueType(ContactHistory.ValueType.STRING);

      if (historyToken.getUser() != null) {
        contactHistory.setUserFullName(historyToken.getUser().getFullName());
      }

      Contact relatedContact = contactDAO.findById(historyToken.getProjectId());
      if (relatedContact != null) {
        contactHistory.setSubject(relatedContact.getFullName());
        contactHistory.setFormattedChangeType(getRelationshipFormatter("Contact", historyToken.getType(), language));
        continue;
      }

      Project relatedProject = projectDAO.findById(historyToken.getProjectId());
      if (relatedProject != null) {
        contactHistory.setSubject(relatedProject.getFullName());
        contactHistory.setFormattedChangeType(getRelationshipFormatter("Project", historyToken.getType(), language));
        continue;
      }

      OrgUnit relatedOrgUnit = orgUnitDAO.findById(historyToken.getProjectId());
      contactHistory.setSubject(relatedOrgUnit.getFullName());
      contactHistory.setFormattedChangeType(getRelationshipFormatter("OrgUnit", historyToken.getType(), language));
    }
    return contactHistories;
  }

  private String getRelationshipFormatter(String modelName, ValueEventChangeType valueEventChangeType, Language language) {
    switch (valueEventChangeType) {
      case ADD:
        return i18nServer.t(language, "contactHistoryChangeTypeAdded" + modelName + "Relationship");
      case REMOVE:
        return i18nServer.t(language, "contactHistoryChangeTypeRemoved" + modelName + "Relationship");
      case EDIT: // fall through (always ADD or REMOVE in case of ContactListElement)
      default:
        throw new IllegalStateException("ValueEventChangeType not supported : " + valueEventChangeType);
    }
  }
}
