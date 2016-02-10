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


import java.util.List;
import java.util.Map;


import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

import com.google.inject.Injector;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dispatch.FunctionalException.ErrorCode;

/**
 * CSV implementation of {@link Importer}.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class CsvImporter extends Importer {

	private List<String[]> lines;

	@SuppressWarnings("unchecked")
	public CsvImporter(Injector injector,  Map<String, Object> properties, UserDispatch.UserExecutionContext executionContext) throws CommandException {
		super(injector,properties, executionContext);
		
		if(properties.get("importedCsvDocument") != null && properties.get("importedCsvDocument") instanceof List<?>){
			lines = (List<String[]>) properties.get("importedCsvDocument");
		}
		
		getCorrespondances(schemeModelList);
	}

	@Override
	protected void getCorrespondances(List<ImportationSchemeModelDTO> schemeModelList) throws CommandException {
		for (ImportationSchemeModelDTO schemeModelDTO : schemeModelList) {
			// GetThe variable and the flexible element for the identification

			switch (scheme.getImportType()) {
			case ROW:
				int firstRow = 0;
				if(scheme.getFirstRow() != null) {
					firstRow = scheme.getFirstRow();
				}
				for (int i = firstRow; i < lines.size(); i++) {
					getCorrespondancePerSheetOrLine(schemeModelDTO, i, null);
				}
				break;
			case SEVERAL:
				logWarnFormatImportTypeIncoherence();
				break;
			case UNIQUE:
				logWarnFormatImportTypeIncoherence();
				break;
			default:
				logWarnFormatImportTypeIncoherence();
				break;

			}
		}

	}

	@Override
	public String getValueFromVariable(String reference, Integer lineNumber, String sheetName) throws FunctionalException {
	
		String columnValue = "";
		if (reference != null && !reference.isEmpty()) {
			switch (scheme.getImportType()) {
			case ROW:
				// Get First Row and sheet name
				if(lineNumber != null && lineNumber >= 0 && lineNumber < lines.size()) {
					final String[] line = lines.get(lineNumber);
					try {
						final int column = Integer.valueOf(reference);
						
						if(column >= 0 && column < line.length) {
							columnValue = line[column];
						}
					} catch(NumberFormatException nfe){
						throw new FunctionalException(nfe, ErrorCode.IMPORT_INVALID_COLUMN_REFERENCE, reference);
					}
				}
				break;
			case SEVERAL:
				logWarnFormatImportTypeIncoherence();
				break;
			case UNIQUE:
				logWarnFormatImportTypeIncoherence();
				break;
			default:
				break;

			}
		}
		return columnValue;
	}

}
