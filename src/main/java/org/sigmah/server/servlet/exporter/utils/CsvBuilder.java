package org.sigmah.server.servlet.exporter.utils;

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

import org.sigmah.server.servlet.exporter.data.cells.GlobalExportDataCell;

public class CsvBuilder {

	public static final int INITIAL_STRING_SIZE = 128;

	private char separator;

	private char quotechar;

	private char escapechar;

	private String lineEnd;

	/** The character used for escaping quotes. */
	public static final char DEFAULT_ESCAPE_CHARACTER = '"';

	/** The default separator to use if none is supplied to the constructor. */
	public static final char DEFAULT_SEPARATOR = ',';

	/**
	 * The default quote character to use if none is supplied to the constructor.
	 */
	public static final char DEFAULT_QUOTE_CHARACTER = '"';

	/** The quote constant to use when you wish to suppress all quoting. */
	public static final char NO_QUOTE_CHARACTER = '\u0000';

	/** The escape constant to use when you wish to suppress all escaping. */
	public static final char NO_ESCAPE_CHARACTER = '\u0000';

	/** Default line terminator uses platform encoding. */
	public static final String DEFAULT_LINE_END = "\n";

	public CsvBuilder() {
		this.separator = DEFAULT_SEPARATOR;
		this.quotechar = DEFAULT_QUOTE_CHARACTER;
		this.escapechar = DEFAULT_ESCAPE_CHARACTER;
		this.lineEnd = DEFAULT_LINE_END;
	}

	/*
	 * For given list of arrays builds CSV string
	 */
	public String buildCsv(List<GlobalExportDataCell[]> allLines) {
		final StringBuilder container = new StringBuilder(INITIAL_STRING_SIZE);
		for (GlobalExportDataCell[] line : allLines) {
			buildLine(line, container);
		}

		return container.toString();
	}

	private void buildLine(GlobalExportDataCell[] nextLine, StringBuilder container) {

		if (nextLine == null)
			return;

		StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
		for (int i = 0; i < nextLine.length; i++) {

			if (i != 0) {
				sb.append(separator);
			}

			String nextElement = nextLine[i].toCSVString();
			if (nextElement == null)
				continue;
			if (quotechar != NO_QUOTE_CHARACTER)
				sb.append(quotechar);

			sb.append(stringContainsSpecialCharacters(nextElement) ? processLine(nextElement) : nextElement);

			if (quotechar != NO_QUOTE_CHARACTER)
				sb.append(quotechar);
		}

		sb.append(lineEnd);

		container.append(sb);
	}

	private boolean stringContainsSpecialCharacters(String line) {
		return line.indexOf(quotechar) != -1 || line.indexOf(escapechar) != -1;
	}

	private StringBuilder processLine(String nextElement) {
		StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
		for (int j = 0; j < nextElement.length(); j++) {
			char nextChar = nextElement.charAt(j);
			if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
				sb.append(escapechar).append(nextChar);
			} else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
				sb.append(escapechar).append(nextChar);
			} else {
				sb.append(nextChar);
			}
		}

		return sb;
	}

}
