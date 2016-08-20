package org.sigmah.server.servlet.exporter.template;

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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.inject.Injector;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.handler.GetLayoutGroupIterationsHandler;
import org.sigmah.server.handler.GetValueHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.BaseSynthesisData;
import org.sigmah.server.servlet.exporter.data.LogFrameExportData;
import org.sigmah.server.servlet.exporter.utils.CalcUtils;
import org.sigmah.server.servlet.exporter.utils.ExportConstants;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Base calc template for project/orgunit calc templates
 * 
 * @author sherzod (v1.3)
 */
public class BaseSynthesisCalcTemplate implements ExportTemplate {

	private final Table table;
	private final SpreadsheetDocument doc;
	private final BaseSynthesisData data;
	private final Class<?> clazz;

	private Row row;

	private final ServletExecutionContext context;
	private Injector injector;

	public BaseSynthesisCalcTemplate(final BaseSynthesisData data, final SpreadsheetDocument doc, final Class<?> clazz, final ServletExecutionContext context, final I18nServer i18nTranslator, final Language language, final Injector injector) throws Throwable {
		this.context = context;
		this.data = data;
		this.doc = doc;
		table = doc.getSheetByIndex(0);
		this.clazz = clazz;
		this.injector = injector;

		String title = data.getLocalizedVersion("projectSynthesis");
		if (clazz.equals(OrgUnit.class))
			title = data.getLocalizedVersion("orgUnitSynthesis");

		table.setTableName(title.replace(" ", "_"));
		int rowIndex = -1;

		// skip row
		++rowIndex;

		// title
		CalcUtils.putMainTitle(table, ++rowIndex, data.getNumbOfCols(), title.toUpperCase());

		// emptry row
		CalcUtils.putEmptyRow(table, ++rowIndex);

		// column headers
		row = table.getRowByIndex(++rowIndex);
		CalcUtils.putHeader(row, 2, data.getLocalizedVersion("adminFlexibleName"));
		CalcUtils.putHeader(row, 3, data.getLocalizedVersion("value"));
		row.setHeight(5, false);

		// empty row
		row = table.getRowByIndex(++rowIndex);
		row.setHeight(3.8, false);
		row.getCellByIndex(1).setCellStyleName(null);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);

		// details
		row = table.getRowByIndex(++rowIndex);
		CalcUtils.putHeader(row, 1, data.getLocalizedVersion("projectDetails"));

		// Project synthesis
		if (clazz.equals(Project.class)) {
			rowIndex = putLayout(table, data.getProject().getProjectModel().getProjectDetails().getLayout(), rowIndex, i18nTranslator, language);

			// empty row
			row = table.getRowByIndex(++rowIndex);
			row.setHeight(3.8, false);
			row.getCellByIndex(1).setCellStyleName(null);
			row.getCellByIndex(2).setCellStyleName(null);
			row.getCellByIndex(3).setCellStyleName(null);

			// run through project phases to get synthesis data
			for (final PhaseModel phaseModel : data.getProject().getProjectModel().getPhaseModels()) {

				// phase name
				row = table.getRowByIndex(++rowIndex);
				CalcUtils.putHeader(row, 1, phaseModel.getName());
				rowIndex = putLayout(table, phaseModel.getLayout(), rowIndex, i18nTranslator, language);

			}
		} else {
			// Org Unit synthesis
			rowIndex = putLayout(table, data.getOrgUnit().getOrgUnitModel().getDetails().getLayout(), rowIndex, i18nTranslator, language);
		}

		table.getColumnByIndex(0).setWidth(3.8);
		table.getColumnByIndex(1).setWidth(49);
		table.getColumnByIndex(2).setWidth(115);
		table.getColumnByIndex(3).setWidth(115);
	}

	private int putLayout(final Table table, final Layout layout, int rowIndex, final I18nServer i18nTranslator, final Language language) throws Throwable {

		EntityManager entityManager = injector.getInstance(EntityManager.class);

		int typeStartRow = rowIndex;
		boolean firstGroup = true;
		Integer id = (clazz.equals(Project.class)) ? data.getProject().getId() : data.getOrgUnit().getId();
		// layout groups for each phase
		for (final LayoutGroup layoutGroup : layout.getGroups()) {

			// layout group cell
			if(layoutGroup.getHasIterations()) {

				if(createIterativeGroupSheet(layoutGroup, id, i18nTranslator, language, entityManager)) {
					if (!firstGroup) {
						row = table.getRowByIndex(++rowIndex);
					}
					firstGroup = false;

					CalcUtils.putGroupCell(table, 2, rowIndex, layoutGroup.getTitle());
					CalcUtils.applyLink(table.getCellByPosition(3, rowIndex), data.getLocalizedVersion("seeIterationDetails"), ExportConstants.GROUP_ITERATIONS_SHEET_PREFIX + layoutGroup.getTitle());
				}

				continue;
			}

			if (!firstGroup) {
				row = table.getRowByIndex(++rowIndex);
			}
			firstGroup = false;

			CalcUtils.putGroupCell(table, 2, rowIndex, layoutGroup.getTitle());
			CalcUtils.mergeCell(table, 2, rowIndex, data.getNumbOfCols(), rowIndex);

			// elements for each layout group
			for (final LayoutConstraint constraint : layoutGroup.getConstraints()) {
				final FlexibleElement element = constraint.getElement();

				// skip if element is not exportable
				if (!element.isExportable())
					continue;

				final String elementName = "element." + element.getClass().getSimpleName();
				final GetValue command = new GetValue(id, element.getId(), elementName, null);

				final ValueResult valueResult = (ValueResult) data.getHandler().execute(command, new UserExecutionContext(context));

				// prepare value and label
				ExporterUtil.ValueLabel pair = null;
				boolean isMessage = false;
				/* DEF FLEXIBLE */
				if (elementName.equals("element.DefaultFlexibleElement") || elementName.equals("element.BudgetElement")) {
					pair =
							ExporterUtil.getDefElementPair(valueResult, element, data.getProject() != null ? data.getProject() : data.getOrgUnit(), clazz,
								data.getEntityManager(), i18nTranslator, language);

				}/* CHECKBOX */else if (elementName.equals("element.CheckboxElement")) {
					pair = ExporterUtil.getCheckboxElementPair(valueResult, element, i18nTranslator, language);
				} /* TEXT AREA */else if (elementName.equals("element.TextAreaElement")) {
					pair = ExporterUtil.getTextAreaElementPair(valueResult, element);

				}/* TRIPLET */else if (elementName.equals("element.TripletsListElement")) {
					pair = ExporterUtil.getTripletPair(element, valueResult);

				}/* CHOICE */else if (elementName.equals("element.QuestionElement")) {
					pair = ExporterUtil.getChoicePair(element, valueResult);

				}/* CONTACT LIST */else if (elementName.equals("element.ContactListElement")) {
					pair = ExporterUtil.getContactListPair(element, valueResult, entityManager);

				}/* MESSAGE */else if (elementName.equals("element.MessageElement")) {
					pair = new ExporterUtil.ValueLabel(data.getLocalizedVersion("flexibleElementMessage"), ExporterUtil.clearHtmlFormatting(element.getLabel()));
					isMessage = true;
				}

				if (pair != null)
					putElement(table, ++rowIndex, pair, isMessage);

			}// elements
		}
		CalcUtils.mergeCell(table, 1, typeStartRow, 1, rowIndex);

		return rowIndex;

	}

	// returns true if sheet was really created (if iterative group contains exportable fields)
	private boolean createIterativeGroupSheet(LayoutGroup group, Integer containerId, I18nServer i18nTranslator, Language language, EntityManager entityManager) {

		List<LayoutConstraint> allConstraints = group.getConstraints();
		List<LayoutConstraint> constraints = new ArrayList<>();

		// keeping only exportable constraints
		for (LayoutConstraint constraint : allConstraints) {
			if (constraint.getElement().isExportable()) {
				constraints.add(constraint);
			}
		}

		if (constraints.isEmpty()) {
			return false;
		}
		final Table sheet = doc.appendSheet(CalcUtils.normalizeAsLink(ExportConstants.GROUP_ITERATIONS_SHEET_PREFIX + group.getTitle()));

		final GetLayoutGroupIterations command = new GetLayoutGroupIterations(group.getId(), containerId, -1);

		// headers
		List<String> headers = new ArrayList<>();

		headers.add(i18nTranslator.t(language, "iterationName"));

		for (LayoutConstraint constraint : constraints) {
			FlexibleElement element = constraint.getElement();
			headers.add(element.getLabel());
		}

		putHeaders(sheet, headers.toArray(new String[headers.size()]));

		final CommandHandler<GetLayoutGroupIterations, ListResult<LayoutGroupIterationDTO>> iterationsHandler = injector.getInstance(GetLayoutGroupIterationsHandler.class);
		final CommandHandler<GetValue, ValueResult> handler = injector.getInstance(GetValueHandler.class);

		try {
			final ListResult<LayoutGroupIterationDTO> iterationsResult = iterationsHandler.execute(command, new UserExecutionContext(context));

			int rowIndex = 1;

			for (LayoutGroupIterationDTO iteration : iterationsResult.getList()) {
				List<String> values = new ArrayList<>();

				values.add(iteration.getName());

				for(LayoutConstraint constraint : constraints) {
					FlexibleElement element = constraint.getElement();
					String elementName = element.getClass().getSimpleName();
					GetValue cmd = new GetValue(containerId, constraint.getElement().getId(), "element." + constraint.getElement().getClass().getSimpleName(), null, iteration.getId());

					try {
						final ValueResult iterationValueResult = handler.execute(cmd, new UserExecutionContext(context));

						// prepare value and label
						String value = null;
            /* CHECKBOX */
						if (elementName.equals("CheckboxElement")) {
							value = getCheckboxElementValue(iterationValueResult, element, i18nTranslator, language);
						} else /* TEXT AREA */if (elementName.equals("TextAreaElement")) {
							value = getTextAreaElementValue(iterationValueResult, element);
						} else /* TRIPLET */if (elementName.equals("TripletsListElement")) {
							value = getTripletValue(iterationValueResult);
						} else /* CHOICE */if (elementName.equals("QuestionElement")) {
							value = getChoiceValue(iterationValueResult, (QuestionElement)element);
						} else /* CONTACT_LIST */if (elementName.equals("ContactListElement")) {
							value = getContactListValue(iterationValueResult, entityManager);
						}
						values.add(value);

					} catch (Exception e) {
						// no value found in database : empty cells
						values.add("");
						if (elementName.equals("QuestionElement")) {
							values.add("");
						}
					}
				}

				putLine(sheet, rowIndex++, values.toArray(new String[values.size()]));
			}

		} catch(Exception e) {

		}

		return true;
	}

	private void putHeaders(Table sheet, String[] values) {

		row = sheet.getRowByIndex(0);

		for (int i = 0; i < values.length; i++) {
			CalcUtils.putHeader(row, i, values[i]);
		}
	}

	private void putLine(Table sheet, int rowIndex, String[] values) {

		row = sheet.getRowByIndex(rowIndex);

		for (int i = 0; i < values.length; i++) {
			CalcUtils.createBasicCell(sheet, i, rowIndex, values[i]);
		}
	}

	private void putElement(Table table, int rowIndex, ExporterUtil.ValueLabel pair, boolean isMessage) {
		row = table.getRowByIndex(rowIndex);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);
		CalcUtils.createBasicCell(table, 2, rowIndex, pair.getFormattedLabel());
		CalcUtils.createBasicCell(table, 3, rowIndex, pair.getValue());
		if (isMessage)
			row.getCellByIndex(3).setFont(CalcUtils.getFont(11, false, true));
	}

	@Override
	public void write(OutputStream output) throws Throwable {
		doc.save(output);
		doc.close();
	}

	private String getCheckboxElementValue(final ValueResult valueResult, final FlexibleElement element, final I18nServer i18nTranslator,
																				 final Language language) {
		String value = i18nTranslator.t(language, "no");

		if (valueResult != null && valueResult.getValueObject() != null) {
			if (valueResult.getValueObject().equalsIgnoreCase("true"))
				value = i18nTranslator.t(language, "yes");

		}
		return value;
	}

	private String getTextAreaElementValue(final ValueResult valueResult, final FlexibleElement element) {

		String value = null;
		final TextAreaElement textAreaElement = (TextAreaElement) element;

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

	private String getTripletValue(final ValueResult valueResult) {
		String value = "";

		if (valueResult != null && valueResult.isValueDefined()) {
			value = formatTripletValues(valueResult.getValuesObject());
		}

		return value;
	}

	private String formatTripletValues(List<ListableValue> list) {

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

	private String getChoiceValue(final ValueResult valueResult, final QuestionElement element) {
		String valueLabels = "";

		if (valueResult != null && valueResult.isValueDefined()) {
			if (element.getMultiple()) {
				final ExportConstants.MultiItemText item = formatMultipleChoices(element.getChoices(), valueResult.getValueObject());
				valueLabels = item.text;

				final List<Integer> selectedChoicesIds = new ArrayList<>();
				for (Integer id : ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject())) {
					for (QuestionChoiceElement choice : element.getChoices()) {
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
				for (QuestionChoiceElement choice : element.getChoices()) {
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

	public static String getContactListValue(final ValueResult valueResult, final EntityManager entityManager) {

		String value = null;
		int lines = 0;

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

			if (lines > 0) {
				value = builder.substring(0, builder.length() - 1);
			}
		}

		return value;
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
}
