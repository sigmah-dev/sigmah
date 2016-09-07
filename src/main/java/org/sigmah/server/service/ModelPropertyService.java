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

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectReportDAO;
import org.sigmah.server.dao.QuestionChoiceElementDAO;
import org.sigmah.server.dao.TripletValueDAO;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.element.*;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.domain.value.File;
import org.sigmah.server.domain.value.TripletValue;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.sigmah.shared.util.ValueResultUtils;

public class ModelPropertyService {
  private final ContactDAO contactDAO;
  private final CountryDAO countryDAO;
  private final FileDAO fileDAO;
  private final QuestionChoiceElementDAO questionChoiceElementDAO;
  private final OrgUnitDAO orgUnitDAO;
  private final ProjectReportDAO projectReportDAO;
  private final TripletValueDAO tripletValueDAO;
  private final I18nServer i18nServer;

  @Inject
  public ModelPropertyService(ContactDAO contactDAO, CountryDAO countryDAO, FileDAO fileDAO, QuestionChoiceElementDAO questionChoiceElementDAO,
                              OrgUnitDAO orgUnitDAO, ProjectReportDAO projectReportDAO, TripletValueDAO tripletValueDAO, I18nServer i18nServer) {
    this.contactDAO = contactDAO;
    this.countryDAO = countryDAO;
    this.fileDAO = fileDAO;
    this.questionChoiceElementDAO = questionChoiceElementDAO;
    this.orgUnitDAO = orgUnitDAO;
    this.projectReportDAO = projectReportDAO;
    this.tripletValueDAO = tripletValueDAO;
    this.i18nServer = i18nServer;
  }

  public String getFormattedValue(FlexibleElement element, String value, Language language) {
    if (element instanceof TextAreaElement) {
      TextAreaType textAreaType = TextAreaType.fromCode(((TextAreaElement) element).getType());
      return getTextareaFormattedValue(value, textAreaType);
    } else if (element instanceof ContactListElement) {
      return getContactListElementFormattedValue(value);
    } else if (element instanceof CheckboxElement) {
      return getCheckboxFormattedValue(value, language);
    } else if (element instanceof QuestionElement) {
      return getQuestionFormattedValue(value);
    } else if (element instanceof ReportElement || element instanceof ReportListElement) {
      return getReportFormattedValue(value);
    } else if (element instanceof TripletsListElement) {
      return getTripletFormattedValue(value);
    } else if (element instanceof FilesListElement) {
      return getFileFormattedValue(value);
    } else if (element instanceof DefaultContactFlexibleElement) {
      return getDefaultContactFormattedValue(value, ((DefaultContactFlexibleElement) element).getType());
    } else if (element instanceof ComputationElement) {
      return value;
    } else {
      throw new IllegalStateException();
    }
  }

  private String getContactListElementFormattedValue(String value) {
    if (value == null) {
      return null;
    }

    StringBuilder serializedValueBuilder = new StringBuilder();
    List<Contact> contacts = contactDAO.findByIds(new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(value)));
    for (int i = 0; i < contacts.size(); i++) {
      serializedValueBuilder.append(contacts.get(i).getFullName());
    }
    return serializedValueBuilder.toString();
  }

  private String getCheckboxFormattedValue(String value, Language language) {
    if ("true".equals(value)) {
      return i18nServer.t(language, "yes");
    }
    return i18nServer.t(language, "no");
  }

  private String getQuestionFormattedValue(String value) {
    if (value == null) {
      return null;
    }

    List<QuestionChoiceElement> choiceElements = questionChoiceElementDAO.findByIds(new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(value)));
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < choiceElements.size(); i++) {
      if (i != 0) {
        stringBuilder.append(", ");
      }
      stringBuilder.append(choiceElements.get(i).getLabel());
    }
    return stringBuilder.toString();
  }

  private String getTextareaFormattedValue(String value, TextAreaType textAreaType) {
    if (value == null) {
      return null;
    }

    switch (textAreaType) {
      case DATE:
        Date date = new Date(Long.parseLong(value));
        if (GWT.isClient()) {
          return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(date);
        }
        return SimpleDateFormat.getDateTimeInstance().format(date);
      case NUMBER:
      case PARAGRAPH:
      case TEXT:
        return value;
      default:
        throw new IllegalStateException();
    }
  }

  private String getReportFormattedValue(String value) {
    if (value == null) {
      return null;
    }

    List<ProjectReport> projectReports = projectReportDAO.findByIds(new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(value)));
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < projectReports.size(); i++) {
      if (i != 0) {
        stringBuilder.append(", ");
      }
      stringBuilder.append(projectReports.get(i).getName());
    }
    return stringBuilder.toString();
  }

  private String getTripletFormattedValue(String value) {
    if (value == null) {
      return null;
    }

    List<TripletValue> tripletValues = tripletValueDAO.findByIds(new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(value)));
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < tripletValues.size(); i++) {
      if (i != 0) {
        stringBuilder.append(", ");
      }
      TripletValue tripletValue = tripletValues.get(i);
      stringBuilder
          .append("(")
          .append(tripletValue.getCode())
          .append(tripletValue.getName())
          .append(tripletValue.getPeriod())
          .append(")");
    }
    return stringBuilder.toString();
  }

  private String getFileFormattedValue(String value) {
    if (value == null) {
      return null;
    }

    List<File> files = fileDAO.findByIds(new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(value)));
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < files.size(); i++) {
      if (i != 0) {
        stringBuilder.append(", ");
      }
      stringBuilder.append(files.get(i).getName());
    }
    return stringBuilder.toString();
  }

  private String getDefaultContactFormattedValue(String value, DefaultContactFlexibleElementType type) {
    if (value == null || value.isEmpty()) {
      return null;
    }

    switch (type) {
      case COUNTRY:
        return countryDAO.findById(Integer.parseInt(value)).getName();
      case CREATION_DATE:
        return SimpleDateFormat.getDateTimeInstance().format(new Date(Long.parseLong(value)));
      case DIRECT_MEMBERSHIP:
        return contactDAO.findById(Integer.parseInt(value)).getFullName();
      case MAIN_ORG_UNIT:
        return orgUnitDAO.findById(Integer.parseInt(value)).getFullName();
      case SECONDARY_ORG_UNITS:
        List<OrgUnit> orgUnits = orgUnitDAO.findByIds(new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(value)));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < orgUnits.size(); i++) {
          if (i != 0) {
            stringBuilder.append(", ");
          }
          stringBuilder.append(orgUnits.get(i).getFullName());
        }
        return stringBuilder.toString();
      case TOP_MEMBERSHIP:
        return contactDAO.findById(Integer.parseInt(value)).getFullName();
      case EMAIL_ADDRESS: // fall through
      case FAMILY_NAME: // fall through
      case FIRST_NAME: // fall through
      case LOGIN: // fall through
      case ORGANIZATION_NAME: // fall through
      case PHONE_NUMBER: // fall through
      case PHOTO: // fall through
      case POSTAL_ADDRESS:
        return value;
      default:
        throw new IllegalStateException();
    }
  }

  public String getDefaultContactPropertyLabel(DefaultContactFlexibleElementType type, Language language) {
    switch (type) {
      case COUNTRY:
        return i18nServer.t(language, "contactCountry");
      case CREATION_DATE:
        return i18nServer.t(language, "contactCreationDate");
      case DIRECT_MEMBERSHIP:
        return i18nServer.t(language, "contactDirectMembership");
      case EMAIL_ADDRESS:
        return i18nServer.t(language, "contactEmailAddress");
      case FAMILY_NAME:
        return i18nServer.t(language, "contactFamilyName");
      case FIRST_NAME:
        return i18nServer.t(language, "contactFirstName");
      case LOGIN:
        return i18nServer.t(language, "contactLogin");
      case MAIN_ORG_UNIT:
        return i18nServer.t(language, "contactMainOrgUnit");
      case ORGANIZATION_NAME:
        return i18nServer.t(language, "contactOrganizationName");
      case PHONE_NUMBER:
        return i18nServer.t(language, "contactPhoneNumber");
      case PHOTO:
        return i18nServer.t(language, "contactPhoto");
      case POSTAL_ADDRESS:
        return i18nServer.t(language, "contactPostalAddress");
      case SECONDARY_ORG_UNITS:
        return i18nServer.t(language, "contactSecondaryOrgUnits");
      case TOP_MEMBERSHIP:
        return i18nServer.t(language, "contactTopMembership");
      default:
        throw new IllegalStateException();
    }
  }
}
