package org.sigmah.server.endpoint.export.sigmah.importer;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.endpoint.export.sigmah.Importer;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.ImportUtils;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

import com.google.inject.Injector;

public class OdsImporter extends Importer {

	private final Document doc;

	public OdsImporter(Injector injector, Map<String, Object> properties, User user) throws Throwable {
		super(injector, properties, user);
		if (properties.get("importedOdsDocument") != null && properties.get("importedOdsDocument") instanceof Document) {
			doc = (Document) properties.get("importedOdsDocument");
		} else {
			doc = null;
			throw new ServletException("Incompatible Document Format");
		}

		getCorrespondances(schemeModelList);

	}

	@Override
	protected void getCorrespondances(List<ImportationSchemeModelDTO> schemeModelList) throws Throwable {
		for (ImportationSchemeModelDTO schemeModelDTO : schemeModelList) {
			// GetThe variable and the flexible element for the identification
			// key

			switch (scheme.getImportType()) {
			case ROW:
				Table sheetTable = null;
				if(scheme.getSheetName() != null && !scheme.getSheetName().isEmpty()) {
					sheetTable = doc.getTableByName(scheme.getSheetName());
				} else if(doc.getTableList().size() > 0){
					sheetTable = doc.getTableList().get(0);
				}
				if(sheetTable != null) {
					int firstRow = 0;
					if(scheme.getFirstRow() != null) {
						firstRow = scheme.getFirstRow();
					}
				
					for (int i = firstRow; i < sheetTable.getRowCount() ; i++) {
						getCorrespondancePerSheetOrLine(schemeModelDTO, i, scheme.getSheetName());
					}
				}
				break;
			case SEVERAL:
				for (Table sheet : doc.getTableList()) {
					getCorrespondancePerSheetOrLine(schemeModelDTO, null, sheet.getTableName());
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
		Cell varCell = null;
		Table sheet = null;
		if (reference != null && !reference.isEmpty()) {
			switch (scheme.getImportType()) {
			case ROW:
				Integer firstRow = scheme.getFirstRow();
				if (firstRow != null && firstRow >= 0 && scheme.getSheetName() != null) {
					sheet = doc.getTableByName(sheetName);
					if (sheet != null) {
						Row row = sheet.getRowByIndex(lineNumber);
						if (row != null) {
							Integer collNumber;
							try {
								collNumber = getColumnFromReference(reference);
							} catch (NumberFormatException e) {
								return null;
							}
							varCell = row.getCellByIndex(collNumber);
							if (varCell != null) {
								cellValue = getCellValue(varCell);
							}
						}
					}
				}
				break;
			case SEVERAL:
				sheet = doc.getTableByName(sheetName);
				if (sheet != null) {
					varCell = sheet.getCellByPosition(reference);
					if (varCell != null) {
						cellValue = getCellValue(varCell);
					}
				}
				break;
			case UNIQUE:
				String[] references = reference.trim().split(ImportUtils.SHEET_CELL_SEPARATOR);
				if (references.length == 2) {

					sheet = doc.getTableByName(references[0]);
					if (sheet != null) {
						varCell = sheet.getCellByPosition(references[1]);
						if (varCell != null) {
							cellValue = getCellValue(varCell);
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

	private Object getCellValue(Cell varCell) {
		Object cellValue = null;
		if ("boolean".equals(varCell.getValueType())) {
			// Boolean
			cellValue = varCell.getBooleanValue();
		} else if ("time".equals(varCell.getValueType())) {
			// Date
			cellValue = varCell.getTimeValue().getTime();
		} else if ("date".equals(varCell.getValueType())) {
			// Date
			cellValue = varCell.getDateValue().getTime();
		} else if ("float".equals(varCell.getValueType())) {
			// Double
			cellValue = varCell.getDoubleValue();
		} else if ("percentage".equals(varCell.getValueType())) {
			// Double
			cellValue = varCell.getPercentageValue();
		} else if ("currency".equals(varCell.getValueType())) {
			// String
			cellValue = varCell.getCurrencyCode();
		} else {
			// String
			cellValue = varCell.getStringValue();
		}
		return cellValue;
	}

}
