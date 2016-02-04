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



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.sigmah.server.servlet.exporter.utils.CsvParser;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dispatch.FunctionalException.ErrorCode;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

/**
 * CSV implementation of {@link Importer}.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class CsvImporter extends Importer {

	private List<String[]> lines;
	private Integer cursor;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInputStream(InputStream inputStream) throws IOException {
		
		final String stringFromStream = inputStreamToString(inputStream, "UTF-8");
		this.lines = new CsvParser().parseCsv(stringFromStream);
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
		
		if (cursor == null || cursor == lines.size()) {
			nextSchemeModel();
			cursor = scheme.getFirstRow();
		}
		
		if (cursor < lines.size()) {
			return getCorrespondancePerSheetOrLine(cursor++, null);
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return hasNextLine() || hasNextSchemeModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
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
					} catch(NumberFormatException nfe) {
						throw new FunctionalException(nfe, ErrorCode.IMPORT_INVALID_COLUMN_REFERENCE, reference);
					}
				}
				break;
			default:
				logWarnFormatImportTypeIncoherence();
				break;
			}
		}
		return columnValue;
	}
	
	/**
	 * Read fully the given input stream and return it as a <code>String</code>.
	 * <p/>
	 * The input stream is not closed by this method.
	 * 
	 * @param inputStream 
	 *          Stream to read.
	 * @param encoding
	 *          Encoding to use.
	 * @return The content of the input stream as a <code>String</code>.
	 * @throws IOException If an error occur while reading the stream.
	 */
	private String inputStreamToString(final InputStream inputStream, final String encoding) throws IOException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		final byte[] bytes = new byte[1024];

		int length = inputStream.read(bytes);
		while(length > 0) {
			outputStream.write(bytes, 0, length);
			length = inputStream.read(bytes);
		}
		
		return outputStream.toString(encoding);
	}
	
	/**
	 * Verify if the stream has more rows to read before moving on to the next
	 * scheme model.
	 * 
	 * @return <code>true</code> if there is more lignes,
	 * <code>false</code> otherwise.
	 */
	private boolean hasNextLine() {
		return cursor == null || cursor < lines.size();
	}
	
}
