package org.sigmah.server.servlet.importer;

import java.util.List;
import java.util.Map;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

import com.google.inject.Injector;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Excel implementation of {@link Importer}.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class ExcelImporter extends Importer {

	private final Workbook workbook;

	public ExcelImporter(Injector injector, Map<String, Object> properties, UserDispatch.UserExecutionContext executionContext) throws CommandException {
		super(injector, properties, executionContext);
		
		if (properties.get("importedExcelDocument") != null) {
			this.workbook = (Workbook) properties.get("importedExcelDocument");
			
		} else {
			this.workbook = null;
			throw new IllegalArgumentException("Incompatible Document Format");
		}

		getCorrespondances(schemeModelList);
	}

	@Override
	protected void getCorrespondances(List<ImportationSchemeModelDTO> schemeModelList) throws CommandException {
		for (ImportationSchemeModelDTO schemeModelDTO : schemeModelList) {
			// Get the variable and the flexible element for the identification
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
		
		if (reference != null && !reference.isEmpty()) {
			switch (scheme.getImportType()) {
			case ROW:
				if (sheetName != null) {
					final Sheet hssfsheet = workbook.getSheet(sheetName);
					if (hssfsheet != null) {
						final Row excelRow = hssfsheet.getRow(rowNumber);
						final int column = getColumnFromReference(reference);
						if (excelRow != null) {
							final Cell cellObject = excelRow.getCell(column);
							cellValue = getCellValue(cellObject);
						}
					}
				}
				break;
				
			case SEVERAL:
				if (sheetName != null) {
					final Sheet hssfsheet = workbook.getSheet(sheetName);
					if (hssfsheet != null) {
						final int row = getRowFromReference(reference);
						final int column = getColumnFromReference(reference);
						final Row excelRow = hssfsheet.getRow(row);
						if (excelRow != null) {
							final Cell cellObject = excelRow.getCell(column);
							cellValue = getCellValue(cellObject);
						}
					}
				}
				break;
				
			case UNIQUE:
				String[] references = reference.trim().split(Separators.SHEET_CELL_SEPARATOR);
				if (references.length == 2) {
					if(references[0] != null) {
						final Sheet hssfsheet = workbook.getSheet(references[0]);
						if(hssfsheet != null) {
							final int row = getRowFromReference(references[1]);
							final int column = getColumnFromReference(references[1]);

							final Row excelRow = hssfsheet.getRow(row);

							if (excelRow != null) {
								final Cell cellObject = excelRow.getCell(column);
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
