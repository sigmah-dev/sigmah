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



import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

/**
 * Excel implementation of {@link Importer}.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class ExcelImporter extends Importer {

	private Workbook workbook;
	
	private Sheet sheet;
	private Integer sheetCursor;
	private Integer rowCursor;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInputStream(InputStream inputStream) throws IOException {
		
		try {
			this.workbook = WorkbookFactory.create(inputStream);
		} catch (InvalidFormatException ex) {
			throw new IOException("The format of the file given is invalid for the file format Excel.", ex);
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
			if (rowCursor == null || (sheet != null && rowCursor == sheet.getLastRowNum())) {
				nextSchemeModel();
				
				if (scheme.getFirstRow() != null) {
					rowCursor = scheme.getFirstRow();
				} else {
					rowCursor = 0;
				}
				if (sheet == null) {
					if (scheme.getSheetName() != null && !scheme.getSheetName().isEmpty()) {
						sheet = workbook.getSheet(scheme.getSheetName());
					} else {
						sheet = workbook.getSheetAt(0);
					}
				}
			}
			if (sheet != null && rowCursor < sheet.getLastRowNum()) {
				details = getCorrespondancePerSheetOrLine(rowCursor, sheet.getSheetName());
				rowCursor++;
			}
			break;
		case SEVERAL:
			if (sheetCursor == null || sheetCursor == workbook.getNumberOfSheets()) {
				nextSchemeModel();
				sheetCursor = 0;
			}
			if (sheetCursor < workbook.getNumberOfSheets()) {
				details = getCorrespondancePerSheetOrLine(null, workbook.getSheetName(sheetCursor));
				sheetCursor++;
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
				logWarnFormatImportTypeIncoherence();
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
			return rowCursor == null || (sheet != null && rowCursor < sheet.getLastRowNum());
		case SEVERAL:
			return sheetCursor == null || sheetCursor < workbook.getNumberOfSheets();
		case UNIQUE:
		default:
			return false;
		}
	}

}
