package org.sigmah.server.servlet.importer;

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



import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import java.io.IOException;
import java.io.InputStream;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

/**
 * ODS implementation of {@link Importer}.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class OdsImporter extends Importer {

	private Document doc;
	
	private Table table;
	private Integer tableCursor;
	private Integer rowCursor;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInputStream(InputStream inputStream) throws IOException {
		try {
			this.doc = SpreadsheetDocument.loadDocument(inputStream);
		} catch (Exception ex) {
			throw new IOException("The format of the file given is invalid for the file format ODS.", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImportDetails next() {
		
		if (scheme.getImportType() != ImportationSchemeImportType.ROW) {
			logWarnFormatImportTypeIncoherence();
			return null;
		}
		
		ImportDetails details = null;
		
		switch (scheme.getImportType()) {
		case ROW:
			if (rowCursor == null || (table != null && rowCursor == table.getRowCount())) {
				nextSchemeModel();
				
				if (scheme.getFirstRow() != null) {
					rowCursor = Math.max(scheme.getFirstRow() - 1, 0);
				} else {
					rowCursor = 0;
				}
				if (table == null) {
					if (scheme.getSheetName() != null && !scheme.getSheetName().isEmpty()) {
						table = doc.getTableByName(scheme.getSheetName());
					} else {
						table = doc.getTableList().get(0);
					}
				}
			}
			if (table != null && rowCursor < table.getRowCount()) {
				details = getCorrespondancePerSheetOrLine(rowCursor, table.getTableName());
				rowCursor++;
			}
			break;
		case SEVERAL:
			if (tableCursor == null || tableCursor == doc.getTableList().size()) {
				nextSchemeModel();
				tableCursor = 0;
			}
			if (tableCursor < doc.getTableList().size()) {
				details = getCorrespondancePerSheetOrLine(null, doc.getTableList().get(tableCursor).getTableName());
				tableCursor++;
			}
			break;
		case UNIQUE:
			nextSchemeModel();
			details = getCorrespondancePerSheetOrLine(null, null);
			break;
		default:
			throw new UnsupportedOperationException("Given import type is not supported '" + scheme.getImportType() + "'.");
		}
		
		return details;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return hasNextRow() || hasNextSchemeModel();
	}

	/**
	 * {@inheritDoc}
	 */
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
				String[] references = reference.trim().split(Separators.SHEET_CELL_SEPARATOR);
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
				logWarnFormatImportTypeIncoherence();
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
	
	/**
	 * Verify if the stream has more rows to read before moving on to the next
	 * scheme model.
	 * 
	 * @return <code>true</code> if there is more lignes,
	 * <code>false</code> otherwise.
	 */
	private boolean hasNextRow() {
		switch (scheme.getImportType()) {
		case ROW:
			return rowCursor == null || (table != null && rowCursor < table.getRowCount());
		case SEVERAL:
			return tableCursor == null || tableCursor < doc.getTableList().size();
		case UNIQUE:
		default:
			return false;
		}
	}

}
