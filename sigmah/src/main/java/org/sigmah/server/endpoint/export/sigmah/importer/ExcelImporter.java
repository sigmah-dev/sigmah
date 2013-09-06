package org.sigmah.server.endpoint.export.sigmah.importer;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sigmah.server.endpoint.export.sigmah.Importer;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.ImportUtils;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

import com.google.inject.Injector;

public class ExcelImporter extends Importer {

	private final Workbook workbook;

	public ExcelImporter(Injector injector, Map<String, Object> properties, User user) throws Throwable {
		super(injector, properties, user);
		if (properties.get("importedExcelDocument") != null) {
			this.workbook = (Workbook) properties.get("importedExcelDocument");
		} else {
			this.workbook = null;
			throw new ServletException("Incompatible Document Format");
		}

		getCorrespondances(schemeModelList);
	}

	@Override
	protected void getCorrespondances(List<ImportationSchemeModelDTO> schemeModelList) throws Throwable {
		for (ImportationSchemeModelDTO schemeModelDTO : schemeModelList) {
			// GetThe variable and the flexible element for the identification

			switch (scheme.getImportType()) {
			case ROW:
				Sheet sheet = null;
				if (scheme.getSheetName() != null && !scheme.getSheetName().isEmpty()) {
					sheet = workbook.getSheet(scheme.getSheetName());
				} else if (workbook.getNumberOfSheets() > 0) {
					sheet = workbook.getSheetAt(0);
				}
				if (sheet != null) {
					int firstRow = 0;
					if (scheme.getFirstRow() != null) {
						firstRow = scheme.getFirstRow();
					}

					for (int i = firstRow; i < sheet.getLastRowNum(); i++) {
						getCorrespondancePerSheetOrLine(schemeModelDTO, i, scheme.getSheetName());
					}
				}

				break;
			case SEVERAL:
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					getCorrespondancePerSheetOrLine(schemeModelDTO, null, workbook.getSheetName(i));
				}
				break;
			case UNIQUE:
				getCorrespondancePerSheetOrLine(schemeModelDTO, null, null);
				break;
			default:
				break;

			}
		}

	}

	@Override
	public Object getValueFromVariable(String reference, Integer rowNumber, String sheetName) {
		// Get the cell value
		// Get the variable value in the document
		Object cellValue = null;
		Integer row = 0;
		Integer column = 0;
		Sheet hssfsheet;
		Row excelRow;
		Cell cellObject;
		if (reference != null && !reference.isEmpty()) {
			switch (scheme.getImportType()) {
			case ROW:
				if (sheetName != null) {
					hssfsheet = workbook.getSheet(sheetName);
					if (hssfsheet != null) {
						excelRow = hssfsheet.getRow(rowNumber);
						column = getColumnFromReference(reference);
						if (excelRow != null) {
							cellObject = excelRow.getCell(column);
							cellValue = getCellValue(cellObject);
						}
					}
				}
				break;
			case SEVERAL:
				if (sheetName != null) {
					hssfsheet = workbook.getSheet(sheetName);
					if (hssfsheet != null) {
						row = getRowFromReference(reference);
						column = getColumnFromReference(reference);
						excelRow = hssfsheet.getRow(row);
						if (excelRow != null) {
							cellObject = excelRow.getCell(column);
							cellValue = getCellValue(cellObject);
						}
					}
				}

				break;
			case UNIQUE:
				String[] references = reference.trim().split(ImportUtils.SHEET_CELL_SEPARATOR);
				if (references.length == 2) {
					if(references[0] != null) {
						hssfsheet = workbook.getSheet(references[0]);
						if(hssfsheet != null) {
							row = getRowFromReference(references[1]);
							column = getColumnFromReference(references[1]);

							excelRow = hssfsheet.getRow(row);

							if (excelRow != null) {
								cellObject = excelRow.getCell(column);
								cellValue = getCellValue(cellObject);
							}
						}
						
					}
					
					
				}

				break;
			default:
				break;

			}
		}
		return cellValue;
	}

	private Object getCellValue(Cell cellObject) {
		Object cellValue = null;
		if (cellObject != null) {
			switch (cellObject.getCellType()) {
			case HSSFCell.CELL_TYPE_BOOLEAN:
				cellValue = cellObject.getBooleanCellValue();
				break;

			case HSSFCell.CELL_TYPE_STRING:
				cellValue = cellObject.getStringCellValue();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cellObject)) {
					cellValue = cellObject.getDateCellValue();
				} else {
					cellValue = cellObject.getNumericCellValue();
				}
				break;

			default:
				break;
			}
		}

		return cellValue;
	}

}
