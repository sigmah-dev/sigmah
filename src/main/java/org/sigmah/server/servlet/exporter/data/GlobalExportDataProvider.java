package org.sigmah.server.servlet.exporter.data;

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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;

import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.impl.GlobalExportHibernateDAO;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.export.GlobalExport;
import org.sigmah.server.domain.export.GlobalExportContent;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.handler.GetValueHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.utils.CsvBuilder;
import org.sigmah.server.servlet.exporter.utils.CsvParser;
import org.sigmah.server.servlet.exporter.utils.ExportConstants;
import org.sigmah.server.servlet.exporter.utils.ExportConstants.MultiItemText;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class Provides global export data
 * 
 * @author sherzod (v1.3)
 */
@Singleton
public class GlobalExportDataProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExportDataProvider.class);

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

	private final Injector injector;
	private final CsvBuilder csvBuilder;
	private final CsvParser csvParser;

	@Inject
	public GlobalExportDataProvider(final Injector injector) {
		this.injector = injector;
		this.csvBuilder = new CsvBuilder();
		this.csvParser = new CsvParser();
	}

	public void persistGlobalExportDataAsCsv(final GlobalExport globalExport, EntityManager em, Map<String, List<String[]>> exportData) throws Exception {
		for (final String pModelName : exportData.keySet()) {
			final GlobalExportContent content = new GlobalExportContent();
			content.setGlobalExport(globalExport);
			content.setProjectModelName(pModelName);
			content.setCsvContent(csvBuilder.buildCsv(exportData.get(pModelName)));
			em.persist(content);
		}
	}

	public Map<String, List<String[]>> getBackedupGlobalExportData(EntityManager em, Integer gExportId) {
		final Map<String, List<String[]>> exportData = new TreeMap<String, List<String[]>>();
		final GlobalExport export = em.find(GlobalExport.class, gExportId);
		final List<GlobalExportContent> contents = export.getContents();
		for (final GlobalExportContent content : contents) {
			final List<String[]> csvData = csvParser.parseCsv(content.getCsvContent());
			exportData.put(content.getProjectModelName(), csvData);
		}
		return exportData;
	}

	public Map<String, List<String[]>> generateGlobalExportData(final Integer organizationId, EntityManager entityManager, final I18nServer i18nTranslator,
			final Language language, final ServletExecutionContext context) throws Exception {
		if (entityManager == null) {
			entityManager = injector.getInstance(EntityManager.class);
		}

		final GlobalExportDAO exportDAO = injector.getInstance(GlobalExportHibernateDAO.class);
		final Organization organization = entityManager.find(Organization.class, organizationId);
		final List<ProjectModel> pModels = exportDAO.getProjectModelsByOrganization(organization);

		final ProjectDAO projectDao = injector.getInstance(ProjectDAO.class);
		final List<Project> projects = projectDao.getProjects(pModels);

		// project model and its projects
		final Map<String, List<Project>> pModelProjectsMap = new HashMap<String, List<Project>>();
		for (final Project project : projects) {
			if (project.getDateDeleted() == null) {
				final String pModelName = project.getProjectModel().getName();

				List<Project> pModelProjects = pModelProjectsMap.get(pModelName);
				if (pModelProjects == null) {
					pModelProjects = new ArrayList<Project>();
					pModelProjectsMap.put(pModelName, pModelProjects);
				}
				pModelProjects.add(project);
			}
		}

		// project model and its globally exportable fields
		final Map<String, List<FlexibleElement>> pModelElementsMap = new HashMap<String, List<FlexibleElement>>();
		for (final ProjectModel projectModel : pModels) {
			if (projectModel.getStatus() != ProjectModelStatus.DRAFT) {
				final String pModelName = projectModel.getName();

				final List<FlexibleElement> pModelElements = new ArrayList<FlexibleElement>();
				pModelElementsMap.put(pModelName, pModelElements);

				// detail elements
				fillElementList(pModelElements, projectModel.getProjectDetails().getLayout());

				// phase elements
				for (final PhaseModel phaseModel : projectModel.getPhaseModels()) {
					fillElementList(pModelElements, phaseModel.getLayout());
				}
			}
		}

		// final CommandHandler<GetValue, ValueResult> handler = new GetValueHandler();
		final CommandHandler<GetValue, ValueResult> handler = injector.getInstance(GetValueHandler.class);

		final Map<String, List<String[]>> pModelExportDataMap = new TreeMap<String, List<String[]>>();

		// collect export data
		for (final String pModelName : pModelElementsMap.keySet()) {

			// if no project for a given project model, skip even headers for
			// flexible elements
			if (pModelProjectsMap.get(pModelName) == null)
				continue;

			final List<FlexibleElement> elements = pModelElementsMap.get(pModelName);
			final List<String[]> exportData = new ArrayList<String[]>();
			pModelExportDataMap.put(pModelName, exportData);

			// field titles
			final String[] titles = new String[elements.size()];

			boolean isFirstLine = true;
			// projects
			for (final Project project : pModelProjectsMap.get(pModelName)) {

				final String[] values = new String[elements.size()];

				int titleIndex = 0;
				int valueIndex = 0;

				// fields
				for (final FlexibleElement element : elements) {

					// command to get element value
					final String elementName = "element." + element.getClass().getSimpleName();
					final GetValue command = new GetValue(project.getId(), element.getId(), elementName, null);

					try {

						final ValueResult valueResult = handler.execute(command, null);

						// prepare value and label
						ValueLabel pair = null;
						/* DEF FLEXIBLE */
						if (elementName.equals("element.DefaultFlexibleElement") || elementName.equals("element.BudgetElement")) {
							pair = getDefElementPair(valueResult, element, project, entityManager, i18nTranslator, language);

						} else /* CHECKBOX */if (elementName.equals("element.CheckboxElement")) {
							pair = getCheckboxElementPair(valueResult, element, i18nTranslator, language);
						} else /* TEXT AREA */if (elementName.equals("element.TextAreaElement")) {
							pair = getTextAreaElementPair(valueResult, element);

						}/* TRIPLET */
						if (elementName.equals("element.TripletsListElement")) {
							pair = getTripletPair(element, valueResult);

						}/* CHOICE */
						if (elementName.equals("element.QuestionElement")) {
							pair = getChoicePair(element, valueResult);
						}

						// titles

						if (isFirstLine) {
							titles[titleIndex++] = pair != null ? pair.getFormattedLabel() : null;
						}

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

						values[valueIndex++] = valueStr;

					} catch (Exception e) {
						LOGGER.error("Failed to get the value of element '" + element.getId() + "' of project '" + project.getId() + "'.", e);
					}

				}

				// add titles
				if (isFirstLine) {
					exportData.add(titles);
					isFirstLine = false;
				}

				// add values
				exportData.add(values);

			}// projects

		}

		return pModelExportDataMap;
	}

	public MultiItemText formatMultipleChoices(List<QuestionChoiceElement> list, String values) {
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
		return new MultiItemText(value, lines);
	}

	public MultiItemText formatTripletValues(List<ListableValue> list) {
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

		return new MultiItemText(value, lines);
	}

	public ValueLabel getTripletPair(final FlexibleElement element, final ValueResult valueResult) {
		String value = null;
		int lines = 1;

		if (valueResult != null && valueResult.isValueDefined()) {
			final MultiItemText item = formatTripletValues(valueResult.getValuesObject());
			value = item.text;
			lines = item.lineCount;
		}

		return new ValueLabel(element.getLabel(), value, lines);
	}

	public ValueLabel getChoicePair(final FlexibleElement element, final ValueResult valueResult) {
		String value = null;
		int lines = 1;

		if (valueResult != null && valueResult.isValueDefined()) {
			final QuestionElement questionElement = (QuestionElement) element;
			final Boolean multiple = questionElement.getMultiple();
			if (multiple != null && multiple) {
				final MultiItemText item = formatMultipleChoices(questionElement.getChoices(), valueResult.getValueObject());
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

	public ValueLabel getTextAreaElementPair(final ValueResult valueResult, final FlexibleElement element) {

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

	public ValueLabel getCheckboxElementPair(final ValueResult valueResult, final FlexibleElement element, final I18nServer i18nTranslator,
			final Language language) {
		String value = i18nTranslator.t(language, "no");

		if (valueResult != null && valueResult.getValueObject() != null) {
			if (valueResult.getValueObject().equalsIgnoreCase("true"))
				value = i18nTranslator.t(language, "yes");

		}
		return new ValueLabel(element.getLabel(), value);
	}

	private String getUserName(User u) {

		String name = "";
		if (u != null)
			name = u.getFirstName() != null ? u.getFirstName() + " " + u.getName() : u.getName();

		return name;
	}

	public ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element, final Object object, final Class<?> clazz,
			final EntityManager entityManager, final I18nServer i18nTranslator, final Language language) {
		if (clazz.equals(Project.class)) {
			return getDefElementPair(valueResult, element, (Project) object, entityManager, i18nTranslator, language);
		} else {
			return getDefElementPair(valueResult, element, (OrgUnit) object, entityManager, i18nTranslator, language);
		}
	}

	public ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element, final Project project, final EntityManager entityManager,
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
					for (final OrgUnit orgUnit : project.getPartners()) {
						orgUnitId = orgUnit.getId();
						break;
					}
				}
				OrgUnit orgUnit = entityManager.find(OrgUnit.class, orgUnitId);
				if (orgUnit != null)
					value = orgUnit.getName() + " - " + orgUnit.getFullName();

			}
				break;

		}
		return new ValueLabel(label, value);
	}

	public ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element, final OrgUnit orgUnit, final EntityManager entityManager,
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

			case BUDGET: {
				BudgetElement budgetElement = (BudgetElement) element;

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

	public static String clearHtmlFormatting(String text) {
		if (text != null && text.length() > 0) {
			text = text.replaceAll("<br>", " ");
			text = text.replaceAll("<[^>]+>|\\n", "");
			text = text.trim().replaceAll(" +", " ");
		}
		return text;
	}

	private void fillElementList(final List<FlexibleElement> elements, final Layout layout) {
		for (final LayoutGroup group : layout.getGroups()) {
			for (final LayoutConstraint constraint : group.getConstraints()) {
				final FlexibleElement element = constraint.getElement();
				if (element.isGloballyExportable())
					elements.add(element);
			}
		}
	}

}
