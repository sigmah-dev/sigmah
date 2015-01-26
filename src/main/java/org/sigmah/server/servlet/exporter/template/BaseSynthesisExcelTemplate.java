package org.sigmah.server.servlet.exporter.template;

import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.BaseSynthesisData;
import org.sigmah.server.servlet.exporter.data.GlobalExportDataProvider;
import org.sigmah.server.servlet.exporter.data.GlobalExportDataProvider.ValueLabel;
import org.sigmah.server.servlet.exporter.utils.ExcelUtils;
import org.sigmah.server.servlet.exporter.utils.ExportConstants;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;

/**
 * Base excel template for project/orgunit excel templates
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

	public BaseSynthesisExcelTemplate(final BaseSynthesisData data, final HSSFWorkbook wb, final Class<?> clazz, final ServletExecutionContext context, final I18nServer i18nTranslator, final Language language) throws Throwable {

		this.context = context;
		this.wb = wb;
		this.data = data;
		this.clazz = clazz;

		String title = data.getLocalizedVersion("projectSynthesis");
		if (clazz.equals(OrgUnit.class))
			title = data.getLocalizedVersion("orgUnitSynthesis");

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
		} else {
			// Org Unit synthesis
			rowIndex = putLayout(sheet, data.getOrgUnit().getOrgUnitModel().getDetails().getLayout(), rowIndex, i18nTranslator, language);
		}

		sheet.setColumnWidth(0, 256 * 2);
		sheet.setColumnWidth(1, 256 * 25);
		sheet.setColumnWidth(2, 256 * labelColWidth);
		sheet.setColumnWidth(3, 256 * valueColWidth);
	}

	private int putLayout(final HSSFSheet sheet, final Layout layout, int rowIndex, final I18nServer i18nTranslator, final Language language) throws Throwable {

		int typeStartRow = rowIndex;
		boolean firstGroup = true;
		// layout groups for each phase
		for (final LayoutGroup layoutGroup : layout.getGroups()) {

			// layout group cell
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
				Integer id = (clazz.equals(Project.class)) ? data.getProject().getId() : data.getOrgUnit().getId();
				final GetValue command = new GetValue(id, element.getId(), elementName, null);

				final ValueResult valueResult = (ValueResult) data.getHandler().execute(command, new UserExecutionContext(context));

				// prepare value and label
				ValueLabel pair = null;
				boolean isMessage = false;
				/* DEF FLEXIBLE */
				if (elementName.equals("element.DefaultFlexibleElement") || elementName.equals("element.BudgetElement")) {
					pair =
							data.getDataProvider().getDefElementPair(valueResult, element, data.getProject() != null ? data.getProject() : data.getOrgUnit(), clazz,
								data.getEntityManager(), i18nTranslator, language);

				}
				/* CHECKBOX */
				else if (elementName.equals("element.CheckboxElement")) {
					pair = data.getDataProvider().getCheckboxElementPair(valueResult, element, i18nTranslator, language);
				}
				/* TEXT AREA */
				else if (elementName.equals("element.TextAreaElement")) {
					pair = data.getDataProvider().getTextAreaElementPair(valueResult, element);

				}
				/* TRIPLET */
				else if (elementName.equals("element.TripletsListElement")) {
					pair = data.getDataProvider().getTripletPair(element, valueResult);

				}/* CHOICE */else if (elementName.equals("element.QuestionElement")) {
					pair = data.getDataProvider().getChoicePair(element, valueResult);

				}/* MESSAGE */else if (elementName.equals("element.MessageElement")) {
					pair = new ValueLabel(data.getLocalizedVersion("flexibleElementMessage"), GlobalExportDataProvider.clearHtmlFormatting(element.getLabel()));
					isMessage = true;
				}

				if (pair != null)
					putElement(sheet, ++rowIndex, pair, isMessage);

			}// elements
		}

		region = new CellRangeAddress(typeStartRow, rowIndex, 1, 1);
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));

		return rowIndex;
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
