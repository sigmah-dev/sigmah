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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.DefaultContactFlexibleElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.exporter.data.LogFrameExportData;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.server.servlet.exporter.data.columns.GlobalExportDataColumn;
import org.sigmah.server.servlet.exporter.data.columns.GlobalExportFlexibleElementColumn;
import org.sigmah.server.servlet.exporter.data.columns.GlobalExportIterativeGroupColumn;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;

public class ExporterUtil {

	/**
	 * Gets the label of the flexible element.
	 * 
	 * @param fleDTO
	 * @param i18nTranslator
	 * @param language
	 * @return The flexible element i18n label.
	 */
	public static String getFlexibleElementLabel(FlexibleElementDTO fleDTO, I18nServer i18nTranslator, final Language language) {
		String fleName = null;
		if (fleDTO.getLabel() != null) {
			fleName = fleDTO.getLabel();
		} else if (fleDTO instanceof DefaultFlexibleElementDTO) {
			fleName = getFlexibleElementLabel(((DefaultFlexibleElementDTO) fleDTO).getType(), i18nTranslator, language);
		} else if (fleDTO instanceof DefaultContactFlexibleElementDTO) {
			fleName = getDefaultContactFlexibleElementLabel(((DefaultContactFlexibleElementDTO) fleDTO).getType(), i18nTranslator, language);
		}
		return fleName;
	}

	/**
	 * Gets the label of the flexible element.
	 * 
	 * @param fle
	 * @param i18nTranslator
	 * @param language
	 * @return The flexible element i18n label.
	 */
	public static String getFlexibleElementLabel(FlexibleElement fle, I18nServer i18nTranslator, final Language language) {
		String fleName = null;
		if (fle.getLabel() != null) {
			fleName = fle.getLabel();
		} else if (fle instanceof DefaultFlexibleElement) {
			fleName = getFlexibleElementLabel(((DefaultFlexibleElement) fle).getType(), i18nTranslator, language);
		} else if (fle instanceof DefaultContactFlexibleElement) {
			fleName = getDefaultContactFlexibleElementLabel(((DefaultContactFlexibleElement) fle).getType(), i18nTranslator, language);
		}
		return fleName;
	}

	/**
	 * Gets the label of the default flexible element type
	 * 
	 * @param type
	 * @param i18nTranslator
	 * @param language
	 * @return The default flexible element i18n label.
	 */
	public static String getFlexibleElementLabel(DefaultFlexibleElementType type, I18nServer i18nTranslator, final Language language) {
		String fleName = null;
		switch (type) {
			case BUDGET:
				fleName = i18nTranslator.t(language, "projectBudget");
				break;
			case CODE:
				fleName = i18nTranslator.t(language, "projectName");
				break;
			case COUNTRY:
				fleName = i18nTranslator.t(language, "projectCountry");
				break;
			case END_DATE:
				fleName = i18nTranslator.t(language, "projectEndDate");
				break;
			case MANAGER:
				fleName = i18nTranslator.t(language, "projectManager");
				break;
			case ORG_UNIT:
				fleName = i18nTranslator.t(language, "orgUnit");
				break;
			case OWNER:
				fleName = i18nTranslator.t(language, "projectOwner");
				break;
			case START_DATE:
				fleName = i18nTranslator.t(language, "projectStartDate");
				break;
			case TITLE:
				fleName = i18nTranslator.t(language, "projectFullName");
				break;
			default:
				break;
		}
		return fleName;
	}

	/**
	 * Gets the label of the default contact flexible element type
	 *
	 * @param type
	 * @param i18nTranslator
	 * @param language
	 * @return The default contact flexible element i18n label.
	 */
	public static String getDefaultContactFlexibleElementLabel(DefaultContactFlexibleElementType type, I18nServer i18nTranslator, final Language language) {
		String fleName = null;
		switch (type) {
			case FAMILY_NAME:
				fleName = i18nTranslator.t(language, "contactFamilyName");
				break;
			case FIRST_NAME:
				fleName = i18nTranslator.t(language, "contactFirstName");
				break;
			case ORGANIZATION_NAME:
				fleName = i18nTranslator.t(language, "contactOrganizationName");
				break;
			case MAIN_ORG_UNIT:
				fleName = i18nTranslator.t(language, "contactMainOrgUnit");
				break;
			case SECONDARY_ORG_UNITS:
				fleName = i18nTranslator.t(language, "contactSecondaryOrgUnits");
				break;
			case CREATION_DATE:
				fleName = i18nTranslator.t(language, "contactCreationDate");
				break;
			case LOGIN:
				fleName = i18nTranslator.t(language, "contactLogin");
				break;
			case EMAIL_ADDRESS:
				fleName = i18nTranslator.t(language, "contactEmailAddress");
				break;
			case PHONE_NUMBER:
				fleName = i18nTranslator.t(language, "contactPhoneNumber");
				break;
			case POSTAL_ADDRESS:
				fleName = i18nTranslator.t(language, "contactPostalAddress");
				break;
			case PHOTO:
				fleName = i18nTranslator.t(language, "contactPhoto");
				break;
			case COUNTRY:
				fleName = i18nTranslator.t(language, "contactCountry");
				break;
			case DIRECT_MEMBERSHIP:
				fleName = i18nTranslator.t(language, "contactDirectMembership");
				break;
			case TOP_MEMBERSHIP:
				fleName = i18nTranslator.t(language, "contactTopMembership");
				break;
			default:
				break;
		}
		return fleName;
	}

	public static class BudgetValues {
		private Double spent;
		private Double planned;

		public BudgetValues(BudgetElement budgetElement, ValueResult valueResult) {
			boolean hasValue = valueResult != null && valueResult.isValueDefined();

			Double plannedBudget = 0d;
			Double spentBudget = 0d;
			if (hasValue) {
				final Map<Integer, String> val = ValueResultUtils.splitMapElements(valueResult.getValueObject());

				if (budgetElement.getRatioDividend() != null) {
					if (val.get(budgetElement.getRatioDividend().getId()) != null) {
						spentBudget = Double.valueOf(val.get(budgetElement.getRatioDividend().getId()));

					}
				}

				if (budgetElement.getRatioDivisor() != null) {
					if (val.get(budgetElement.getRatioDivisor().getId()) != null) {
						plannedBudget = Double.valueOf(val.get(budgetElement.getRatioDivisor().getId()));

					}
				}
			}

			spent = spentBudget;
			planned = plannedBudget;
		}

		public Double getSpent() {
			return spent;
		}

		public Double getPlanned() {
			return planned;
		}

		public Double getRatio() {
			return spent/planned;
		}
	}

	public static class ChoiceValue {
		private String valueLabels;
		private String valueIds;

		public ChoiceValue(QuestionElement questionElement, ValueResult valueResult) {
			if (valueResult != null && valueResult.isValueDefined()) {
				if (questionElement.getMultiple()) {
					final ExportConstants.MultiItemText item = formatMultipleChoices(questionElement.getChoices(), valueResult.getValueObject());
					valueLabels = item.text;

					String selectedChoicesIds = "";
					for (Integer id : ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject())) {
						for (QuestionChoiceElement choice : questionElement.getChoices()) {
							if (id.equals(choice.getId())) {
								if (choice.getCategoryElement() != null) {
									id = choice.getCategoryElement().getId();
								}
								break;
							}
						}
						selectedChoicesIds += id + ", ";
					}
					if (selectedChoicesIds.length() > 0) {
						valueIds = selectedChoicesIds.substring(0, selectedChoicesIds.length() - 2);
					}
				} else {
					final String idChoice = valueResult.getValueObject();
					for (QuestionChoiceElement choice : questionElement.getChoices()) {
						if (idChoice.equals(String.valueOf(choice.getId()))) {
							if (choice.getCategoryElement() != null) {
								valueLabels = choice.getCategoryElement().getLabel();
								valueIds = String.valueOf(choice.getCategoryElement().getId());
							} else {
								valueLabels = choice.getLabel();
							}
							break;
						}
					}
				}
			}
		}

		public String getValueLabels() {
			return valueLabels;
		}

		public String getValueIds() {
			return valueIds;
		}
	}

	public static class ValueLabel {

		private String label;
		private Object value;
		private int lines = 1;

		public ValueLabel(String label, Object value) {
			this.label = label;
			this.value = value;
		}

		public ValueLabel(String label, Object value, int lines) {
			this.label = label;
			this.value = value;
			this.lines = lines;
		}

		public String getFormattedLabel() {
			return clearHtmlFormatting(label);
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public int getLines() {
			return lines;
		}

	}

	public static ExportConstants.MultiItemText formatMultipleChoices(List<QuestionChoiceElement> list, String values) {
		final List<Integer> selectedChoicesId = ValueResultUtils.splitValuesAsInteger(values);
		final StringBuffer builder = new StringBuffer();
		int lines = 1;
		for (final QuestionChoiceElement choice : list) {
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

	public static ExportConstants.MultiItemText formatTripletValues(List<ListableValue> list) {
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
			value = builder.substring(0, builder.length() - 2);
			lines--;
		}

		return new ExportConstants.MultiItemText(value, lines);
	}

	public static ValueLabel getTripletPair(final FlexibleElement element, final ValueResult valueResult) {
		String value = null;
		int lines = 1;

		if (valueResult != null && valueResult.isValueDefined()) {
			final ExportConstants.MultiItemText item = formatTripletValues(valueResult.getValuesObject());
			value = item.text;
			lines = item.lineCount;
		}

		return new ValueLabel(element.getLabel(), value, lines);
	}

	public static ValueLabel getContactListPair(final FlexibleElement element, final ValueResult valueResult, final EntityManager entityManager) {

		String value = null;
		int lines = 1;

		if (valueResult != null && valueResult.isValueDefined()) {

			// retrieving list values from database
			Query query = entityManager.createQuery("SELECT c FROM Contact c WHERE c.id IN (:idList)");
			query.setParameter("idList", ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject()));
			final List<Object> objectsList = query.getResultList();

			final StringBuilder builder = new StringBuilder();
			for (Object s : objectsList) {
				final Contact contactValue = (Contact) s;
				builder.append(" - ");
				builder.append(contactValue.getFullName());
				builder.append("\n");
				lines++;
			}

			if (lines > 1) {
				value = builder.substring(0, builder.length() - 1);
				lines--;
			}
		}

		return new ValueLabel(element.getLabel(), value, lines);
	}

	public static Integer getContactListCount(final ValueResult valueResult) {

		if (valueResult != null && valueResult.isValueDefined()) {
			return ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject()).size();
		}

		return 0;
	}

	public static ValueLabel getChoicePair(final FlexibleElement element, final ValueResult valueResult) {
		String value = null;
		int lines = 1;

		if (valueResult != null && valueResult.isValueDefined()) {
			final QuestionElement questionElement = (QuestionElement) element;
			final Boolean multiple = questionElement.getMultiple();
			if (multiple != null && multiple) {
				final ExportConstants.MultiItemText item = formatMultipleChoices(questionElement.getChoices(), valueResult.getValueObject());
				value = item.text;
				lines = item.lineCount;

			} else {
				final String idChoice = valueResult.getValueObject();
				for (QuestionChoiceElement choice : questionElement.getChoices()) {
					if (idChoice.equals(String.valueOf(choice.getId()))) {
						if (choice.getCategoryElement() != null) {
							value = choice.getCategoryElement().getLabel();
						} else {
							value = choice.getLabel();
						}
						break;
					}
				}
			}
		}

		return new ValueLabel(element.getLabel(), value, lines);
	}

	public static ValueLabel getTextAreaElementPair(final ValueResult valueResult, final FlexibleElement element) {

		Object value = null;
		final TextAreaElement textAreaElement = (TextAreaElement) element;

		if (valueResult != null && valueResult.isValueDefined()) {
			String strValue = valueResult.getValueObject();
			final TextAreaType type = TextAreaType.fromCode(textAreaElement.getType());
			if (type != null) {
				switch (type) {
					case NUMBER:
						if (textAreaElement.getIsDecimal()) {
							value = Double.parseDouble(strValue);
						} else {
							value = Long.parseLong(strValue);
						}
						break;
					case DATE:
						value = new Date(Long.parseLong(strValue));
						break;
					default:
						value = strValue;
						break;
				}
			} else {
				value = strValue;
			}

		}

		return new ValueLabel(element.getLabel(), value);
	}

	public static ValueLabel getCheckboxElementPair(final ValueResult valueResult, final FlexibleElement element, final I18nServer i18nTranslator,
																					 final Language language) {
		String value = i18nTranslator.t(language, "no");

		if (valueResult != null && valueResult.getValueObject() != null) {
			if (valueResult.getValueObject().equalsIgnoreCase("true"))
				value = i18nTranslator.t(language, "yes");

		}
		return new ValueLabel(element.getLabel(), value);
	}

	private static String getUserName(User u) {

		String name = "";
		if (u != null)
			name = u.getFirstName() != null ? u.getFirstName() + " " + u.getName() : u.getName();

		return name;
	}

	public static ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element, final Object object, final Class<?> clazz,
																			final EntityManager entityManager, final I18nServer i18nTranslator, final Language language) {
		if (clazz.equals(Project.class)) {
			return getDefElementPair(valueResult, element, (Project) object, entityManager, i18nTranslator, language);
		} else if (clazz.equals(OrgUnit.class)) {
			return getDefElementPair(valueResult, element, (OrgUnit) object, entityManager, i18nTranslator, language);
		} else {
			return getDefElementPair(valueResult, element, (Contact) object, entityManager, i18nTranslator, language);
		}
	}

	public static void addBudgetTitles(final List<ExportDataCell> titles, final FlexibleElement element, final I18nServer i18nTranslator, final Language language) {
		String budgetLabel = ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language);

		titles.add(new ExportStringCell(budgetLabel + " " + i18nTranslator.t(language, "spentBudget")));
		titles.add(new ExportStringCell(budgetLabel + " " + i18nTranslator.t(language, "plannedBudget")));
		titles.add(new ExportStringCell(budgetLabel + " " + i18nTranslator.t(language, "consumptionRatioBudget")));
	}

	public static void addBudgetValues(final List<ExportDataCell> values, final ValueResult valueResult, final FlexibleElement element, final I18nServer i18nTranslator, final Language language) {
		BudgetElement budgetElement = (BudgetElement) element;

		BudgetValues budget = new BudgetValues(budgetElement, valueResult);

		values.add(new ExportStringCell(String.valueOf(budget.getSpent())));
		values.add(new ExportStringCell(String.valueOf(budget.getPlanned())));
		values.add(new ExportStringCell(String.valueOf(budget.getRatio())));
	}

	public static void addChoiceTitles(final List<ExportDataCell> titles, final Set<CategoryType> categories, final FlexibleElement element, final I18nServer i18nTranslator, final Language language) {
		final QuestionElement questionElement = (QuestionElement) element;
		String choiceLabel = ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language);

		titles.add(new ExportStringCell(choiceLabel));
		if (questionElement.getCategoryType() != null) {
			titles.add(new ExportStringCell(choiceLabel + " (" + questionElement.getCategoryType().getLabel() + ") " + i18nTranslator.t(language, "categoryId")));
			categories.add(((QuestionElement) element).getCategoryType());
		}
	}

	public static void addChoiceValues(final List<ExportDataCell> values, final ValueResult valueResult, final FlexibleElement element) {

		ChoiceValue choiceValue = new ChoiceValue((QuestionElement) element, valueResult);

		values.add(new ExportStringCell(choiceValue.getValueLabels()));
		if (((QuestionElement)element).getCategoryType() != null) {
			values.add(new ExportStringCell(choiceValue.getValueIds()));
		}
	}

	public static ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element, final Project project, final EntityManager entityManager,
																			final I18nServer i18nTranslator, final Language language) {

		Object value = null;
		String label = ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language);

		final DefaultFlexibleElement defaultElement = (DefaultFlexibleElement) element;

		boolean hasValue = valueResult != null && valueResult.isValueDefined();

		switch (defaultElement.getType()) {
			case CODE: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = project.getName();
				}
			}
			break;
			case TITLE: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = project.getFullName();
				}
			}
			break;
			case START_DATE: {
				if (hasValue) {
					value = new Date(Long.parseLong(valueResult.getValueObject()));
				} else {
					value = project.getStartDate();
				}
			}
			break;
			case END_DATE: {
				if (hasValue) {
					value = new Date(Long.parseLong(valueResult.getValueObject()));
				} else {
					value = "";
					if (project.getEndDate() != null)
						value = project.getEndDate();
				}
			}
			break;
			case BUDGET: {
				BudgetElement budgetElement = (BudgetElement) element;

				// BUGFIX #732: Inverted plannedBudget and spentBudget.

				Double plannedBudget = 0d;
				Double spentBudget = 0d;
				if (hasValue) {
					final Map<Integer, String> values = ValueResultUtils.splitMapElements(valueResult.getValueObject());

					if (budgetElement.getRatioDividend() != null) {
						if (values.get(budgetElement.getRatioDividend().getId()) != null) {
							spentBudget = Double.valueOf(values.get(budgetElement.getRatioDividend().getId()));

						}
					}

					if (budgetElement.getRatioDivisor() != null) {
						if (values.get(budgetElement.getRatioDivisor().getId()) != null) {
							plannedBudget = Double.valueOf(values.get(budgetElement.getRatioDivisor().getId()));

						}
					}
				}
				value = spentBudget + " / " + plannedBudget;
			}
			break;
			case COUNTRY: {
				if (hasValue) {
					int countryId = Integer.parseInt(valueResult.getValueObject());
					value = entityManager.find(Country.class, countryId).getName();
				} else {
					value = project.getCountry().getName();
				}
			}
			break;
			case OWNER: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = getUserName(project.getOwner());
				}
			}
			break;
			case MANAGER: {
				if (hasValue) {
					int userId = Integer.parseInt(valueResult.getValueObject());
					value = getUserName(entityManager.find(User.class, userId));
				} else {
					value = getUserName(project.getManager());
				}
			}
			break;
			case ORG_UNIT: {
				int orgUnitId = -1;
				if (hasValue) {
					orgUnitId = Integer.parseInt(valueResult.getValueObject());
				} else {
					orgUnitId = project.getOrgUnit().getId();
				}
				OrgUnit orgUnit = entityManager.find(OrgUnit.class, orgUnitId);
				if (orgUnit != null)
					value = orgUnit.getName() + " - " + orgUnit.getFullName();

			}
			break;

		}
		return new ValueLabel(label, value);
	}

	public static ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element, final OrgUnit orgUnit, final EntityManager entityManager,
																			final I18nServer i18nTranslator, final Language language) {
		Object value = null;
		String label = ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language);

		final DefaultFlexibleElement defaultElement = (DefaultFlexibleElement) element;

		boolean hasValue = valueResult != null && valueResult.isValueDefined();

		switch (defaultElement.getType()) {

			case CODE: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = orgUnit.getName();
				}
			}
			break;

			case TITLE: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = orgUnit.getFullName();
				}
			}
			break;

			case COUNTRY: {
				if (hasValue) {
					int countryId = Integer.parseInt(valueResult.getValueObject());
					value = entityManager.find(Country.class, countryId).getName();
				} else {
					value = orgUnit.getOfficeLocationCountry() != null ? orgUnit.getOfficeLocationCountry().getName() : null;
				}
			}
			break;

			case MANAGER: {
				if (hasValue) {
					int userId = Integer.parseInt(valueResult.getValueObject());
					value = getUserName(entityManager.find(User.class, userId));
				} else {
					value = "";
				}
			}
			break;

			case ORG_UNIT: {
				OrgUnit parentOrgUnit = orgUnit.getParentOrgUnit();
				if (parentOrgUnit == null)
					parentOrgUnit = orgUnit;
				value = parentOrgUnit.getName() + " - " + parentOrgUnit.getFullName();
			}
			break;

			default:
				break;
		}
		return new ValueLabel(label, value);
	}

	public static ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element, final Contact contact, final EntityManager entityManager,
																						 final I18nServer i18nTranslator, final Language language) {
		Object value = null;
		String label = ExporterUtil.getFlexibleElementLabel(element, i18nTranslator, language);

		final DefaultContactFlexibleElement defaultElement = (DefaultContactFlexibleElement) element;

		boolean hasValue = valueResult != null && valueResult.isValueDefined();

		switch (defaultElement.getType()) {

			case FAMILY_NAME: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = contact.getName();
				}
			}
			break;

			case FIRST_NAME: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = contact.getFirstname();
				}
			}
			break;

			case ORGANIZATION_NAME: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = contact.getOrganization() != null ? contact.getOrganization().getName() : null;
				}
			}
			break;

			case MAIN_ORG_UNIT: {
				int orgUnitId = -1;
				if (hasValue) {
					orgUnitId = Integer.parseInt(valueResult.getValueObject());
				} else {
					orgUnitId = contact.getMainOrgUnit().getId();
				}
				OrgUnit orgUnit = entityManager.find(OrgUnit.class, orgUnitId);
				if (orgUnit != null) {
					value = orgUnit.getName() + " - " + orgUnit.getFullName();
				}
			}
			break;

			case SECONDARY_ORG_UNITS: {
				List<OrgUnit> orgUnits = new ArrayList<>();
				if (hasValue) {
					List<Integer> orgUnitsIds = ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject());
					for (Integer id : orgUnitsIds) {
						OrgUnit unit = entityManager.find(OrgUnit.class, id);
						if (unit != null)
							orgUnits.add(unit);
					}
				} else {
					orgUnits = contact.getSecondaryOrgUnits();
				}
				String val = "";
				for (OrgUnit unit : orgUnits) {
					val += unit.getName() + " - " + unit.getFullName() + "\n";
				}
				if (!val.isEmpty()) {
					value = val.substring(0, val.length() - 1);
				}
			}
			break;

			case CREATION_DATE: {
				if (hasValue) {
					value = new Date(Long.parseLong(valueResult.getValueObject()));
				} else {
					value = contact.getDateCreated();
				}
			}
			break;

			case LOGIN: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = contact.getLogin();
				}
			}
			break;

			case EMAIL_ADDRESS: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = contact.getEmail();
				}
			}
			break;

			case PHONE_NUMBER: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = contact.getPhoneNumber();
				}
			}
			break;

			case POSTAL_ADDRESS: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = contact.getPostalAddress();
				}
			}
			break;

			case PHOTO: {
				if (hasValue) {
					value = valueResult.getValueObject();
				} else {
					value = contact.getPhoto();
				}
			}
			break;

			case COUNTRY: {
				if (hasValue) {
					int countryId = Integer.parseInt(valueResult.getValueObject());
					value = entityManager.find(Country.class, countryId).getName();
				} else {
					value = contact.getCountry() != null ? contact.getCountry().getName() : null;
				}
			}
			break;

			case DIRECT_MEMBERSHIP: {
				int orgUnitId = -1;
				if (hasValue) {
					orgUnitId = Integer.parseInt(valueResult.getValueObject());
				} else {
					if (contact.getParent() != null) {
						orgUnitId = contact.getParent().getId();
					}
				}
				OrgUnit orgUnit = entityManager.find(OrgUnit.class, orgUnitId);
				if (orgUnit != null) {
					value = orgUnit.getName() + " - " + orgUnit.getFullName();
				}
			}
			break;

			case TOP_MEMBERSHIP: {
				int orgUnitId = -1;
				if (hasValue) {
					orgUnitId = Integer.parseInt(valueResult.getValueObject());
				} else {
					if (contact.getRoot() != null) {
						orgUnitId = contact.getRoot().getId();
					}
				}
				OrgUnit orgUnit = entityManager.find(OrgUnit.class, orgUnitId);
				if (orgUnit != null) {
					value = orgUnit.getName() + " - " + orgUnit.getFullName();
				}
			}
			break;

			default:
				break;
		}
		return new ValueLabel(label, value);
	}

	public static String clearHtmlFormatting(String text) {
		if (text != null && text.length() > 0) {
			text = text.replaceAll("<br>", " ");
			text = text.replaceAll("<[^>]+>|\\n", "");
			text = text.trim().replaceAll(" +", " ");
		}
		return text;
	}

	public static void fillElementList(final List<GlobalExportDataColumn> elements, final Layout layout) {
		for (final LayoutGroup group : layout.getGroups()) {
			if(group.getHasIterations()) {
				elements.add(new GlobalExportIterativeGroupColumn(group));
				continue;
			}

			for (final LayoutConstraint constraint : group.getConstraints()) {
				final FlexibleElement element = constraint.getElement();
				if (element.isGloballyExportable())
					elements.add(new GlobalExportFlexibleElementColumn(element));
			}
		}
	}

	public static String pairToValueString(ValueLabel pair) {
		// values
		String valueStr = null;
		if (pair != null) {
			Object value = pair.getValue();
			if (value == null) {
				valueStr = null;
			} else if (value instanceof String) {
				valueStr = (String) value;
			} else if (value instanceof Double) {
				Double d = (Double) value;
				valueStr = LogFrameExportData.AGGR_AVG_FORMATTER.format(d.doubleValue());
			} else if (value instanceof Long) {
				Long l = (Long) value;
				valueStr = LogFrameExportData.AGGR_SUM_FORMATTER.format(l.longValue());
			} else { // date
				valueStr = ExportConstants.EXPORT_DATE_FORMAT.format((Date) value);
			}
		}

		return valueStr;
	}
}
