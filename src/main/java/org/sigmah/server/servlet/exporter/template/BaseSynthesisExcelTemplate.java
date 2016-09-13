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
import java.util.List;

import javax.persistence.EntityManager;

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
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.domain.element.ContactListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.handler.GetLayoutGroupIterationsHandler;
import org.sigmah.server.handler.GetValueHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.BaseSynthesisData;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.server.servlet.exporter.utils.ExcelUtils;
import org.sigmah.server.servlet.exporter.utils.ExportConstants;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.server.servlet.exporter.utils.ValueLabel;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;

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
		
		final EntityId<Integer> container = data.getContainerWithClass(clazz);
		
		// layout groups for each phase
		for (final LayoutGroup layoutGroup : layout.getGroups()) {

			// layout group cell

			if(layoutGroup.getHasIterations()) {

				if(createIterativeGroupSheet(layoutGroup, container, i18nTranslator, language, entityManager)) {
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
				if (!element.isExportable()) {
					continue;
				}
				
				/* CONTACT LIST */
				if (data.isWithContacts() && element instanceof ContactListElement) {
					final ValueResult valueResult = ExporterUtil.getValueResult(element, container, data.getHandler());
					utils.putBorderedBasicCell(sheet, rowIndex, 2, element.getLabel());
					utils.createLinkCell(sheet.getRow(rowIndex).createCell(3), String.valueOf(ExporterUtil.getContactListCount(valueResult)), ExportConstants.CONTACT_SHEET_PREFIX + element.getLabel(), true);
				}
				/* OTHERS */
				else {
					final ValueLabel pair = ExporterUtil.getPair(element, container, data.getEntityManager(), data.getHandler(), i18nTranslator, language, data);
					
					if (pair != null) {
						putElement(sheet, ++rowIndex, pair, pair.isMessage());
					}
				}
			}// elements
		}

		region = new CellRangeAddress(typeStartRow, rowIndex, 1, 1);
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));

		return rowIndex;
	}

	/**
	 * Returns true if sheet was really created (if iterative group contains exportable fields).
	 */
	private boolean createIterativeGroupSheet(final LayoutGroup group, final EntityId<Integer> container, final I18nServer i18nTranslator, final Language language, final EntityManager entityManager) {

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

		final GetLayoutGroupIterations command = new GetLayoutGroupIterations(group.getId(), container.getId(), -1);

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

			for (final LayoutGroupIterationDTO iteration : iterationsResult.getList()) {
				final List<ExportDataCell> values = new ArrayList<>();
				values.add(new ExportStringCell(iteration.getName()));
				values.addAll(ExporterUtil.getCellsForIteration(iteration, constraints, container, entityManager, i18nTranslator, language, data));

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

	private void putElement(HSSFSheet sheet, int rowIndex, ValueLabel pair, boolean isMessage) {
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

}
