package org.sigmah.server.endpoint.export.sigmah.importer;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
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
				// TODO Not implemented yet
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
	public Object getValueFromVariable(String reference, Integer lineNumber, String sheetName) {
		// Get the cell value
		// Get the variable value in the document
		Object cellValue = null;
		Integer row = 0;
		Integer cell = 0;
		Sheet hssfsheet;
		Row excelRow;
		Cell cellObject;
		if (reference != null && !reference.isEmpty()) {
			switch (scheme.getImportType()) {
			case ROW:
				break;
			case SEVERAL:
				hssfsheet = workbook.getSheet(sheetName);
				row = Integer.valueOf(Character.toString(reference.charAt(1))) - 1;
				cell = getNumericValuefromCharacter(reference.charAt(0));
				excelRow = hssfsheet.getRow(row);
				if (excelRow != null) {
					cellObject = excelRow.getCell(cell);
					if (cellObject != null) {
						switch (cellObject.getCellType()) {
						case HSSFCell.CELL_TYPE_BOOLEAN:
							cellValue = cellObject.getBooleanCellValue();
							break;

						case HSSFCell.CELL_TYPE_STRING:
							cellValue = cellObject.getStringCellValue();
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:
							cellValue = cellObject.getNumericCellValue();
							break;

						default:
							break;
						}
					}
				}

				break;
			case UNIQUE:
				String[] references = reference.trim().split(ImportUtils.SHEET_CELL_SEPARATOR);
				if (references.length == 2) {
					hssfsheet = workbook.getSheet(references[0]);
					row = Integer.valueOf(Character.toString(references[1].charAt(1))) - 1;
					cell = getNumericValuefromCharacter(references[1].charAt(0));
					excelRow = hssfsheet.getRow(row);
					if (excelRow != null) {
						cellObject = excelRow.getCell(cell);
						if (cellObject != null) {
							switch (cellObject.getCellType()) {
							case HSSFCell.CELL_TYPE_BOOLEAN:
								cellValue = cellObject.getBooleanCellValue();
								break;

							case HSSFCell.CELL_TYPE_STRING:
								cellValue = cellObject.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								cellValue = cellObject.getNumericCellValue();
								break;

							default:
								break;
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
}
