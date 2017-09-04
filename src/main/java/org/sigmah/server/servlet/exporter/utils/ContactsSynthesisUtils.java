package org.sigmah.server.servlet.exporter.utils;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.data.LogFrameExportData;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetAvailableFrameworks;
import org.sigmah.shared.command.GetContactRelationships;
import org.sigmah.shared.command.GetContactRelationshipsInFramework;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ContactRelationship;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.FrameworkDTO;
import org.sigmah.shared.dto.FrameworkElementDTO;
import org.sigmah.shared.dto.FrameworkHierarchyDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.ContactListElementDTO;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;

public class ContactsSynthesisUtils {

	public static class ContactSheetData {

		private final String title;
		private final List<ExportDataCell> headers = new ArrayList<>();
		private final List<List<ExportDataCell>> lines = new ArrayList<>();

		public ContactSheetData(String title) {
			this.title = title;
		}

		public void addHeader(ExportDataCell header) {
			headers.add(header);
		}

		public void addHeaders(List<ExportDataCell> headers) {
			this.headers.addAll(headers);
		}

		public void addLine(List<ExportDataCell> line) {
			lines.add(line);
		}

		public List<ExportDataCell> getHeaders() {
			return headers;
		}

		public List<List<ExportDataCell>> getLines() {
			return lines;
		}

		public String getTitle() {
			return title;
		}
	}

	public static List<ContactSheetData> createProjectContactListData(final Integer projectId, final Exporter exporter,
																									final I18nServer i18nTranslator, final Language language) throws Throwable {

		final List<ContactSheetData> sheets = new ArrayList<>();

		final ProjectDTO project = exporter.execute(new GetProject(projectId, null));

		for (LayoutGroupDTO group : project.getProjectModel().getProjectDetails().getLayout().getGroups()) {

			for (LayoutConstraintDTO constraint : group.getConstraints()) {
				if (constraint.getFlexibleElementDTO() instanceof ContactListElementDTO) {
					sheets.add(createContactListTab(projectId, group, (ContactListElementDTO)constraint.getFlexibleElementDTO(),
							exporter, i18nTranslator, language));
				}
			}
		}

		return sheets;
	}

	public static ContactSheetData createContactListTab(final Integer projectId, final LayoutGroupDTO layoutGroup,
																					final ContactListElementDTO contactListElement, final Exporter exporter,
																					final I18nServer i18nTranslator, final Language language) throws Throwable  {

		if (layoutGroup.getHasIterations()) {
			return createContactListTabWithIterations(projectId, layoutGroup, contactListElement, exporter, i18nTranslator, language);
		} else {
			return createContactListSimpleTab(projectId, layoutGroup, contactListElement, exporter, i18nTranslator, language);
		}
	}

	public static ContactSheetData createContactListTabWithIterations(final Integer projectId, final LayoutGroupDTO layoutGroup,
																												final ContactListElementDTO contactListElement, final Exporter exporter,
																												final I18nServer i18nTranslator, final Language language) throws Throwable {

		ContactSheetData result = new ContactSheetData(contactListElement.getLabel());

		ListResult<LayoutGroupIterationDTO> iterations = exporter.execute(new GetLayoutGroupIterations(layoutGroup.getId(), projectId, -1));

		boolean isFirst = true;

		List<ExportDataCell> iterationHeaders = new ArrayList<>();
		if (layoutGroup.getIterationType() == null) {
			iterationHeaders.add(new ExportStringCell(i18nTranslator.t(language, "iterationName")));
		} else {
			iterationHeaders.add(new ExportStringCell(i18nTranslator.t(language, "iterationNameMessage", layoutGroup.getIterationType())));
		}


		for (LayoutGroupIterationDTO iteration : iterations.getList()) {
			List<ExportDataCell> iterationValues = new ArrayList<>();
			List<ContactDTO> contacts = null;

			iterationValues.add(new ExportStringCell(iteration.getName()));

			for (LayoutConstraintDTO constraint : layoutGroup.getConstraints()) {
				FlexibleElementDTO element = constraint.getFlexibleElementDTO();

				try {
					ValueResult iterationValueResult = exporter.execute(new GetValue(projectId, element.getId(), element.getEntityName(), null, iteration.getId()));

					if (element == contactListElement) {
						Set<Integer> contactIds = new HashSet(ValueResultUtils.splitValuesAsInteger(iterationValueResult.getValueObject()));
						if (!contactIds.isEmpty()) {
							contacts = exporter.execute(new GetContacts(contactIds)).getList();
						}
					} else {
						if (isFirst) {
							iterationHeaders.add(new ExportStringCell(ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language)));
						}
						iterationValues.add(valueResultToDataCell(element, iterationValueResult, i18nTranslator, language));
					}
				} catch(Exception e) {
					// no value found in database : empty cells
					iterationValues.add(new ExportStringCell(""));
				}
			}

			if (contacts == null || contacts.isEmpty()) {
				continue;
			}

			for (ContactDTO contact : contacts) {
				List<ExportDataCell> line = new ArrayList<>();

				for (LayoutGroupDTO group : contact.getContactModel().getDetails().getLayout().getGroups()) {
					if (group.getHasIterations()) {
						// no default element in iterative groups
						continue;
					}

					for(LayoutConstraintDTO constraint : group.getConstraints()) {
						FlexibleElementDTO element = constraint.getFlexibleElementDTO();
						if (constraint.getFlexibleElementDTO() instanceof DefaultContactFlexibleElementDTO) {
							if (isFirst) {
								result.addHeader(new ExportStringCell(ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language)));
							}
							try {
								line.add(defaultValueToDataCell(element, contact));
							} catch(Exception e) {
								// no value found in database : empty cells
								line.add(new ExportStringCell(""));
							}
						}
					}

				}

				line.addAll(iterationValues);
				result.addLine(line);

				isFirst = false;
			}
		}

		result.addHeaders(iterationHeaders);

		return result;
	}

	public static ContactSheetData createContactListSimpleTab(final Integer projectId, final LayoutGroupDTO layoutGroup,
																												final ContactListElementDTO contactListElement, final Exporter exporter,
																												final I18nServer i18nTranslator, final Language language) throws Throwable {

		ContactSheetData result = new ContactSheetData(contactListElement.getLabel());

		boolean isFirst = true;

		List<ExportDataCell> iterationHeaders = new ArrayList<>();
		List<ExportDataCell> values = new ArrayList<>();
		List<ContactDTO> contacts = null;

		for (LayoutConstraintDTO constraint : layoutGroup.getConstraints()) {
			FlexibleElementDTO element = constraint.getFlexibleElementDTO();

			ValueResult valueResult = exporter.execute(new GetValue(projectId, element.getId(), "element." + element.getClass().getSimpleName(), null, null));

			if (element == contactListElement) {
				Set<Integer> contactIds = new HashSet(ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject()));
				if (!contactIds.isEmpty()) {
					contacts = exporter.execute(new GetContacts(contactIds)).getList();
				}
			} else {
				if (isFirst) {
					iterationHeaders.add(new ExportStringCell(ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language)));
				}
				values.add(valueResultToDataCell(element, valueResult, i18nTranslator, language));
			}
		}

		if (contacts == null || contacts.isEmpty()) {
			return result;
		}

		for (ContactDTO contact : contacts) {
			List<ExportDataCell> line = new ArrayList<>();

			for (LayoutGroupDTO group : contact.getContactModel().getDetails().getLayout().getGroups()) {
				if (group.getHasIterations()) {
					// no default element in iterative groups
					continue;
				}

				for(LayoutConstraintDTO constraint : group.getConstraints()) {
					FlexibleElementDTO element = constraint.getFlexibleElementDTO();
					if (constraint.getFlexibleElementDTO() instanceof DefaultContactFlexibleElementDTO) {
						if (isFirst) {
							result.addHeader(new ExportStringCell(ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language)));
						}
						line.add(defaultValueToDataCell(element, contact));
					}
				}

			}

			line.addAll(values);
			result.addLine(line);

			isFirst = false;
		}

		result.addHeaders(iterationHeaders);

		return result;
	}

	public static ContactSheetData createAllRelationsData(final Integer contactId, final Exporter exporter,
	final I18nServer i18nTranslator, final Language language) throws Throwable {

		final ListResult<ContactRelationship> contacts = exporter.execute(new GetContactRelationships(contactId));

		if (contacts == null || contacts.isEmpty()) {
			return null;
		}

		final ContactSheetData sheet = new ContactSheetData(i18nTranslator.t(language, "relationships"));

		sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipElementLabel")));
		sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipGroupTitle")));
		sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipType")));
		sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipName")));
		sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipMember")));

		for (ContactRelationship relationship : contacts.getList()) {

			List<ExportDataCell> line = new ArrayList<>();

			line.add(new ExportStringCell(relationship.getFieldName()));
			line.add(new ExportStringCell(relationship.getGroupName()));
			line.add(new ExportStringCell(relationship.getFormattedType()));
			line.add(new ExportStringCell(relationship.getName()));
			String isMember = relationship.getDirection() == ContactRelationship.Direction.INBOUND ? i18nTranslator.t(language, "yes"):i18nTranslator.t(language, "no");
			line.add(new ExportStringCell(isMember));

			sheet.addLine(line);
		}

		return sheet;
	}

	public static List<ContactSheetData> createFrameworkRelationsData(final Integer contactId, final Exporter exporter,
																											            	final I18nServer i18nTranslator, final Language language) throws Throwable {

    final ListResult<ContactRelationship> contacts = exporter.execute(new GetContactRelationships(contactId));

    if (contacts == null || contacts.isEmpty()) {
      return null;
    }

    ListResult<FrameworkDTO> frameworkResult = exporter.execute(new GetAvailableFrameworks());

    if (frameworkResult.isEmpty()) {
      return null;
    }

    List<ContactSheetData> sheets = new ArrayList<>();

    for (FrameworkDTO frameworkDTO : frameworkResult.getData()) {
      for (FrameworkHierarchyDTO frameworkHierarchyDTO : frameworkDTO
          .getFrameworkHierarchies()) {
        for (FrameworkElementDTO frameworkElementDTO : frameworkHierarchyDTO
            .getFrameworkElements()) {
          if (ElementTypeEnum.CONTACT_LIST.equals(frameworkElementDTO.getDataType())) {

            ContactSheetData sheet = new ContactSheetData(frameworkElementDTO.getLabel());
            sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipElementLabel")));
            sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipGroupTitle")));
            sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipType")));
            sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipName")));
            sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipMember")));

            ListResult<ContactRelationship> contactsInFramework = exporter.execute(new GetContactRelationshipsInFramework(contactId, frameworkDTO.getId()));

            for (ContactRelationship contactRelationship : contactsInFramework.getList()) {
              List<ExportDataCell> line = new ArrayList<>();

              line.add(new ExportStringCell(contactRelationship.getFieldName()));
              line.add(new ExportStringCell(contactRelationship.getGroupName()));
              line.add(new ExportStringCell(contactRelationship.getFormattedType()));
              line.add(new ExportStringCell(contactRelationship.getName()));
              String isMember = contactRelationship.getDirection() == ContactRelationship.Direction.INBOUND ? i18nTranslator.t(language, "yes"):i18nTranslator.t(language, "no");
              line.add(new ExportStringCell(isMember));

              sheet.addLine(line);
            }
            sheets.add(sheet);
          }
        }
      }
    }
    return sheets;
	}

	public static List<ContactSheetData> createRelationsByElementData(final Integer contactId, final Exporter exporter,
																																		final I18nServer i18nTranslator, final Language language) throws Throwable {

		final ListResult<ContactRelationship> contacts = exporter.execute(new GetContactRelationships(contactId));

		if (contacts == null || contacts.isEmpty()) {
			return null;
		}

		List<ContactSheetData> sheets = new ArrayList<>();

		Map<String, List<ContactRelationship>> relationshipsByElement = new HashMap<>();

		for (ContactRelationship relationship : contacts.getList()) {
			String sheetName = (relationship.getFieldName() + "_" + relationship.getGroupName()).toLowerCase();
			List<ContactRelationship> relationships = relationshipsByElement.get(sheetName);
			if (relationships == null) {
				relationships = new ArrayList<>();
				relationshipsByElement.put(sheetName, relationships);
			}

			relationships.add(relationship);
		}

		for (String element : relationshipsByElement.keySet()) {
			final ContactSheetData sheet = new ContactSheetData(element);
			sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipGroupTitle")));
			sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipType")));
			sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipName")));
			sheet.addHeader(new ExportStringCell(i18nTranslator.t(language, "contactRelationshipMember")));

			for (ContactRelationship relationship : relationshipsByElement.get(element)) {
				List<ExportDataCell> line = new ArrayList<>();

				line.add(new ExportStringCell(relationship.getGroupName()));
				line.add(new ExportStringCell(relationship.getFormattedType()));
				line.add(new ExportStringCell(relationship.getName()));
				String isMember = relationship.getDirection() == ContactRelationship.Direction.INBOUND ? i18nTranslator.t(language, "yes"):i18nTranslator.t(language, "no");
				line.add(new ExportStringCell(isMember));

				sheet.addLine(line);
			}

			sheets.add(sheet);
		}

		return sheets;
	}

	private static ExportDataCell valueResultToDataCell(final FlexibleElementDTO element, final ValueResult valueResult,
																											final I18nServer i18nTranslator, final Language language) {
		ExportDataCell val = null;

		String elementName = element.getEntityName();

		/* CHECKBOX */
		if (elementName.equals("element.CheckboxElement")) {
			val = new ExportStringCell(getCheckboxElementValue(valueResult, element, i18nTranslator, language));
		} else /* TEXT AREA */if (elementName.equals("element.TextAreaElement")) {
			val = new ExportStringCell(getTextAreaElementValue(valueResult, element));
		} else /* TRIPLET */if (elementName.equals("element.TripletsListElement")) {
			val = new ExportStringCell(getTripletValue(valueResult));
		} else /* CHOICE */if (elementName.equals("element.QuestionElement")) {
			val = new ExportStringCell(getChoiceValue(valueResult, (QuestionElementDTO) element));
		} else /* CONTACT_LIST */if (elementName.equals("element.ContactListElement")) {
			val = new ExportLinkCell(String.valueOf(ExporterUtil.getContactListCount(valueResult)), ExportConstants.CONTACT_SHEET_PREFIX + element.getLabel());
		}

		return val;
	}

	private static ExportDataCell defaultValueToDataCell(final FlexibleElementDTO element, final ContactDTO contact) {

		return new ExportStringCell(getDefElementValue(element, contact));
	}

	public static String getDefElementValue(final FlexibleElementDTO element, final ContactDTO contact) {
		Object value = null;

		final DefaultContactFlexibleElementDTO defaultElement = (DefaultContactFlexibleElementDTO) element;

		switch (defaultElement.getType()) {

			case FAMILY_NAME: {
				value = contact.getName();
			}
			break;

			case FIRST_NAME: {
				value = contact.getFirstname();
			}
			break;

			case ORGANIZATION_NAME: {
				value = contact.getOrganizationName();
			}
			break;

			case MAIN_ORG_UNIT: {
				OrgUnitDTO orgUnit = contact.getMainOrgUnit();
				if (orgUnit != null) {
					value = orgUnit.getName() + " - " + orgUnit.getFullName();
				}
			}
			break;

			case SECONDARY_ORG_UNITS: {
				List<OrgUnitDTO> orgUnits;
				orgUnits = contact.getSecondaryOrgUnits();

				String val = "";
				if (orgUnits != null) {
					for (OrgUnitDTO unit : orgUnits) {
						val += unit.getName() + " - " + unit.getFullName() + "\n";
					}
				}

				if (!val.isEmpty()) {
					value = val.substring(0, val.length() - 1);
				}
			}
			break;

			case CREATION_DATE: {
				value = contact.getDateCreated();
			}
			break;

			case LOGIN: {
				value = contact.getLogin();
			}
			break;

			case EMAIL_ADDRESS: {
				value = contact.getEmail();
			}
			break;

			case PHONE_NUMBER: {
				value = contact.getPhoneNumber();
			}
			break;

			case POSTAL_ADDRESS: {
				value = contact.getPostalAddress();
			}
			break;

			case PHOTO: {
				value = contact.getPhoto();
			}
			break;

			case COUNTRY: {
				value = contact.getCountry() != null ? contact.getCountry().getName() : null;
			}
			break;

			case DIRECT_MEMBERSHIP: {
				ContactDTO orgUnit = contact.getParent();
				if (orgUnit != null) {
					value = orgUnit.getFullName();
				}
			}
			break;

			case TOP_MEMBERSHIP: {
				ContactDTO orgUnit = contact.getRoot();
				if (orgUnit != null) {
					value = orgUnit.getFullName();
				}
			}
			break;

			default:
				break;
		}

		String result;

		if (value == null) {
			result = null;
		} else if (value instanceof String) {
			result = (String) value;
		} else if (value instanceof Double) {
			Double d = (Double) value;
			result = LogFrameExportData.AGGR_AVG_FORMATTER.format(d.doubleValue());
		} else if (value instanceof Long) {
			Long l = (Long) value;
			result = LogFrameExportData.AGGR_SUM_FORMATTER.format(l.longValue());
		} else { // date
			result = ExportConstants.EXPORT_DATE_FORMAT.format((Date) value);
		}

		return result;
	}

	private static String getCheckboxElementValue(final ValueResult valueResult, final FlexibleElementDTO element, final I18nServer i18nTranslator,
																				 final Language language) {
		String value = i18nTranslator.t(language, "no");

		if (valueResult != null && valueResult.getValueObject() != null) {
			if (valueResult.getValueObject().equalsIgnoreCase("true"))
				value = i18nTranslator.t(language, "yes");

		}
		return value;
	}

	private static String getTextAreaElementValue(final ValueResult valueResult, final FlexibleElementDTO element) {

		String value = null;
		final TextAreaElementDTO textAreaElement = (TextAreaElementDTO) element;

		if (valueResult != null && valueResult.isValueDefined()) {
			String strValue = valueResult.getValueObject();
			final TextAreaType type = TextAreaType.fromCode(textAreaElement.getType());
			if (type != null) {
				switch (type) {
					case NUMBER:
						if (textAreaElement.getIsDecimal()) {
							value = LogFrameExportData.AGGR_AVG_FORMATTER.format(Double.parseDouble(strValue));
						} else {
							value = LogFrameExportData.AGGR_SUM_FORMATTER.format(Long.parseLong(strValue));
						}
						break;
					case DATE:
						value = ExportConstants.EXPORT_DATE_FORMAT.format((Date) new Date(Long.parseLong(strValue)));
						break;
					default:
						value = strValue;
						break;
				}
			} else {
				value = strValue;
			}

		}

		return value;
	}

	private static String getTripletValue(final ValueResult valueResult) {
		String value = "";

		if (valueResult != null && valueResult.isValueDefined()) {
			value = formatTripletValues(valueResult.getValuesObject());
		}

		return value;
	}

	private static String formatTripletValues(List<ListableValue> list) {

		int lines = list.size() + 1;
		final StringBuilder builder = new StringBuilder();
		for (ListableValue s : list) {
			final TripletValueDTO tripletValue = (TripletValueDTO) s;
			builder.append(" - ");
			builder.append(tripletValue.getCode());
			builder.append(" - ");
			builder.append(tripletValue.getName());
			builder.append(" : ");
			builder.append(tripletValue.getPeriod());
			builder.append("\n");
		}
		String value = null;
		if (lines > 1) {
			value = builder.substring(0, builder.length() - 1);
		}

		return value;
	}

	private static String getChoiceValue(final ValueResult valueResult, final QuestionElementDTO element) {
		String valueLabels = "";

		if (valueResult != null && valueResult.isValueDefined()) {
			if (element.getMultiple()) {
				final ExportConstants.MultiItemText item = formatMultipleChoices(element.getChoices(), valueResult.getValueObject());
				valueLabels = item.text;

				final List<Integer> selectedChoicesIds = new ArrayList<>();
				for (Integer id : ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject())) {
					for (QuestionChoiceElementDTO choice : element.getChoices()) {
						if (id.equals(choice.getId())) {
							if (choice.getCategoryElement() != null) {
								id = choice.getCategoryElement().getId();
							}
							break;
						}
					}
					selectedChoicesIds.add(id);
				}
			} else {
				final String idChoice = valueResult.getValueObject();
				for (QuestionChoiceElementDTO choice : element.getChoices()) {
					if (idChoice.equals(String.valueOf(choice.getId()))) {
						if (choice.getCategoryElement() != null) {
							valueLabels = choice.getCategoryElement().getLabel();
						} else {
							valueLabels = choice.getLabel();
						}
						break;
					}
				}
			}
		}

		return valueLabels;
	}

	public static ExportConstants.MultiItemText formatMultipleChoices(List<QuestionChoiceElementDTO> list, String values) {
		final List<Integer> selectedChoicesId = ValueResultUtils.splitValuesAsInteger(values);
		final StringBuffer builder = new StringBuffer();
		int lines = 1;
		for (final QuestionChoiceElementDTO choice : list) {
			for (final Integer id : selectedChoicesId) {
				if (id.equals(choice.getId())) {
					builder.append(" - ");
					if (choice.getCategoryElement() != null) {
						builder.append(choice.getCategoryElement().getLabel());
					} else {
						builder.append(choice.getLabel());
					}
					builder.append("\n");
					lines++;
				}
			}
		}
		String value = null;
		if (lines > 1) {
			value = builder.substring(0, builder.length() - 1);
			lines--;
		}
		return new ExportConstants.MultiItemText(value, lines);
	}
}
