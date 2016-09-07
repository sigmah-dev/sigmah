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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.server.servlet.exporter.utils.ExcelUtils;
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
 * Base excel template for project/orgunit/contact excel templates
 * 
 * @author sherzod (v1.3)
 */
public abstract class BaseSynthesisExcelTemplate implements ExportTemplate {

	private final BaseSynthesisData data;
	private final HSSFWorkbook wb;
	private HSSFRow row = null;
	private HSSFCell cell = null;
	private final ExcelUtils utils;
	private CellRangeAddress region;
	private final float defHeight = ExportConstants.TITLE_ROW_HEIGHT;
	private final int labelColWidth = 60;
	private final int valueColWidth = 60;
	private final Class<?> clazz;

	private final ServletExecutionContext context;
	private Injector injector;

	public BaseSynthesisExcelTemplate(final BaseSynthesisData data, final HSSFWorkbook wb, final Class<?> clazz, final ServletExecutionContext context, final I18nServer i18nTranslator, final Language language, final Injector injector) throws Throwable {

		this.context = context;
		this.wb = wb;
		this.data = data;
		this.clazz = clazz;
		this.injector = injector;

		String title = data.getLocalizedVersion("projectSynthesis");
		if (clazz.equals(OrgUnit.class))
			title = data.getLocalizedVersion("orgUnitSynthesis");
		if (clazz.equals(Contact.class))
			title = data.getLocalizedVersion("contactSynthesis");

		final HSSFSheet sheet = wb.createSheet(title);
		utils = new ExcelUtils(wb);
		int rowIndex = -1;

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex, 8.65f);

		// title
		utils.putMainTitle(sheet, ++rowIndex, title.toUpperCase(), data.getNumbOfCols());

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex, ExportConstants.EMPTY_ROW_HEIGHT);

		// column headers
		row = sheet.createRow(++rowIndex);
		utils.putHeader(row, 2, data.getLocalizedVersion("adminFlexibleName"));
		utils.putHeader(row, 3, data.getLocalizedVersion("value"));

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex, ExportConstants.EMPTY_ROW_HEIGHT);

		// freeze pane
		sheet.createFreezePane(0, rowIndex);

		// detail
		row = sheet.createRow(++rowIndex);
		utils.putHeader(row, 1, data.getLocalizedVersion("projectDetails"));

		// Project synthesis
		if (clazz.equals(Project.class)) {
			rowIndex = putLayout(sheet, data.getProject().getProjectModel().getProjectDetails().getLayout(), rowIndex, i18nTranslator, language);

			// empty row
			utils.putEmptyRow(sheet, ++rowIndex, ExportConstants.EMPTY_ROW_HEIGHT);

			// run through project phases to get synthesis data
			for (final PhaseModel phaseModel : data.getProject().getProjectModel().getPhaseModels()) {

				// phase name
				row = sheet.createRow(++rowIndex);
				utils.putHeader(row, 1, phaseModel.getName());
				rowIndex = putLayout(sheet, phaseModel.getLayout(), rowIndex, i18nTranslator, language);

			}
		} else if (clazz.equals(OrgUnit.class)) {
			// Org Unit synthesis
			rowIndex = putLayout(sheet, data.getOrgUnit().getOrgUnitModel().getDetails().getLayout(), rowIndex, i18nTranslator, language);
		} else {
			// Contact synthesis
			rowIndex = putLayout(sheet, data.getContact().getContactModel().getDetails().getLayout(), rowIndex, i18nTranslator, language);
		}

		sheet.setColumnWidth(0, 256 * 2);
		sheet.setColumnWidth(1, 256 * 25);
		sheet.setColumnWidth(2, 256 * labelColWidth);
		sheet.setColumnWidth(3, 256 * valueColWidth);
	}

	private int putLayout(final HSSFSheet sheet, final Layout layout, int rowIndex, final I18nServer i18nTranslator, final Language language) throws Throwable {

		EntityManager entityManager = injector.getInstance(EntityManager.class);

		int typeStartRow = rowIndex;
		boolean firstGroup = true;
		Integer id;
		Object container;
		if (clazz.equals(Project.class)) {
			id = data.getProject().getId();
			container = data.getProject();
		} else if (clazz.equals(OrgUnit.class)) {
			id = data.getOrgUnit().getId();
			container = data.getOrgUnit();
		} else {
			id = data.getContact().getId();
			container = data.getContact();
		}
		// layout groups for each phase
		for (final LayoutGroup layoutGroup : layout.getGroups()) {

			// layout group cell

			if(layoutGroup.getHasIterations()) {

				if(createIterativeGroupSheet(layoutGroup, id, i18nTranslator, language, entityManager)) {
					if (!firstGroup) {
						row = sheet.createRow(++rowIndex);
					}
					firstGroup = false;

					row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);

					cell = sheet.getRow(rowIndex).createCell(2);
					cell.setCellStyle(utils.getGroupStyle(wb));
					cell.setCellValue(layoutGroup.getTitle());

					cell = sheet.getRow(rowIndex).createCell(3);
					utils.createLinkCell(cell, data.getLocalizedVersion("seeIterationDetails"), ExportConstants.GROUP_ITERATIONS_SHEET_PREFIX + layoutGroup.getTitle(), true);
				}

				continue;
			}

			if (!firstGroup) {
				row = sheet.createRow(++rowIndex);
			}
			firstGroup = false;

			row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);

			CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 2, data.getNumbOfCols());
			sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
			cell = sheet.getRow(rowIndex).createCell(2);
			cell.setCellStyle(utils.getGroupStyle(wb));
			cell.setCellValue(layoutGroup.getTitle());

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
				/* DEF FLEXIBLE */
				if (elementName.equals("element.DefaultFlexibleElement") || elementName.equals("element.DefaultContactFlexibleElement") || elementName.equals("element.BudgetElement")) {
					pair =
							ExporterUtil.getDefElementPair(valueResult, element, container, clazz,
								data.getEntityManager(), i18nTranslator, language);

				}
				/* CHECKBOX */
				else if (elementName.equals("element.CheckboxElement")) {
					pair = ExporterUtil.getCheckboxElementPair(valueResult, element, i18nTranslator, language);
				}
				/* TEXT AREA */
				else if (elementName.equals("element.TextAreaElement")) {
					pair = ExporterUtil.getTextAreaElementPair(valueResult, element);

				}
				/* TRIPLET */
				else if (elementName.equals("element.TripletsListElement")) {
					pair = ExporterUtil.getTripletPair(element, valueResult);

				}/* CHOICE */else if (elementName.equals("element.QuestionElement")) {
					pair = ExporterUtil.getChoicePair(element, valueResult);

				}/* CONTACT LIST */else if (elementName.equals("element.ContactListElement")) {
					if(data.getWithContacts()) {
						utils.putBorderedBasicCell(sheet, rowIndex, 2, element.getLabel());
						utils.createLinkCell(sheet.getRow(rowIndex).createCell(3), String.valueOf(ExporterUtil.getContactListCount(valueResult)), ExportConstants.CONTACT_SHEET_PREFIX + element.getLabel(), true);
					} else {
						pair = ExporterUtil.getContactListPair(element, valueResult, entityManager);
					}

				}/* MESSAGE */else if (elementName.equals("element.MessageElement")) {
					pair = new ExporterUtil.ValueLabel(data.getLocalizedVersion("flexibleElementMessage"), ExporterUtil.clearHtmlFormatting(element.getLabel()));
					pair.setMessage(true);
				}

				if (pair != null)
					putElement(sheet, ++rowIndex, pair, pair.isMessage());

			}// elements
		}

		region = new CellRangeAddress(typeStartRow, rowIndex, 1, 1);
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));

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

		final HSSFSheet sheet = wb.createSheet(ExportConstants.GROUP_ITERATIONS_SHEET_PREFIX + group.getTitle());

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
				List<ExportDataCell> values = new ArrayList<>();

				values.add(new ExportStringCell(iteration.getName()));

				for(LayoutConstraint constraint : constraints) {
					FlexibleElement element = constraint.getElement();
					String elementName = element.getClass().getSimpleName();
					GetValue cmd = new GetValue(containerId, constraint.getElement().getId(), "element." + constraint.getElement().getClass().getSimpleName(), null, iteration.getId());

					try {
						final ValueResult iterationValueResult = handler.execute(cmd, new UserExecutionContext(context));

						// prepare value and label
						ExportDataCell value = null;
            /* CHECKBOX */
						if (elementName.equals("CheckboxElement")) {
							value = new ExportStringCell(getCheckboxElementValue(iterationValueResult, element, i18nTranslator, language));
						} else /* TEXT AREA */if (elementName.equals("TextAreaElement")) {
							value = new ExportStringCell(getTextAreaElementValue(iterationValueResult, element));
						} else /* TRIPLET */if (elementName.equals("TripletsListElement")) {
							value = new ExportStringCell(getTripletValue(iterationValueResult));
						} else /* CHOICE */if (elementName.equals("QuestionElement")) {
							value = new ExportStringCell(getChoiceValue(iterationValueResult, (QuestionElement)element));
						} else /* CONTACT_LIST */if (elementName.equals("ContactListElement")) {
							if(data.getWithContacts()) {
								value = new ExportLinkCell(String.valueOf(ExporterUtil.getContactListCount(iterationValueResult)), ExportConstants.CONTACT_SHEET_PREFIX + element.getLabel());
							} else {
								value = new ExportStringCell(getContactListValue(iterationValueResult, entityManager));
							}
						}
						values.add(value);

					} catch (Exception e) {
						// no value found in database : empty cells
						values.add(new ExportStringCell(""));
						if (elementName.equals("QuestionElement")) {
							values.add(new ExportStringCell(""));
						}
					}
				}

				putLine(sheet, rowIndex++, values.toArray(new ExportDataCell[values.size()]));
			}

		} catch(Exception e) {

		}

		return true;
	}

	private void putHeaders(HSSFSheet sheet, String[] values) {
		int lineHeight = -1;

		row = sheet.createRow(0);

		for (int i = 0; i < values.length; i++) {
			utils.putHeader(row, i, values[i]);
			int cellHeight = utils.calculateLineCount(values[i], labelColWidth);
			if (cellHeight > lineHeight) {
				lineHeight = cellHeight;
			}
		}

		row.setHeightInPoints(lineHeight * defHeight);
	}

	private void putLine(HSSFSheet sheet, int rowIndex, ExportDataCell[] values) {
		int lineHeight = -1;

		row = sheet.createRow(rowIndex);

		for (int i = 0; i < values.length; i++) {
			ExportDataCell cell = values[i];
			if (cell instanceof ExportStringCell) {
				utils.putBorderedBasicCell(sheet, rowIndex, i, cell.toCSVString());
			} else {
				ExportLinkCell link = (ExportLinkCell)cell;
				utils.createLinkCell(sheet.getRow(rowIndex).createCell(i), link.getText(), link.getTarget(), true);
			}

			int cellHeight = utils.calculateLineCount(cell.toCSVString(), labelColWidth);
			if (cellHeight > lineHeight) {
				lineHeight = cellHeight;
			}
		}

		row.setHeightInPoints(lineHeight * defHeight);
	}

	private void putElement(HSSFSheet sheet, int rowIndex, ExporterUtil.ValueLabel pair, boolean isMessage) {
		row = sheet.createRow(rowIndex);
		utils.putBorderedBasicCell(sheet, rowIndex, 2, pair.getFormattedLabel());
		utils.putBorderedBasicCell(sheet, rowIndex, 3, pair.getValue());

		if (isMessage) {
			row.getCell(3).getCellStyle().setFont(utils.getItalicFont(wb, (short) 11));
		}

		int lineCount = Math.max(pair.getLines(), utils.calculateLineCount(pair.getFormattedLabel(), labelColWidth));

		if (pair.getValue() instanceof String) {
			lineCount = Math.max(lineCount, utils.calculateLineCount((String) pair.getValue(), labelColWidth));
		}

		row.setHeightInPoints(lineCount * defHeight);
	}

	@Override
	public void write(OutputStream output) throws Throwable {
		wb.write(output);

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
