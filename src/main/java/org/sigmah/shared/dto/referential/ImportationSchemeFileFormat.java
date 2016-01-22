package org.sigmah.shared.dto.referential;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.core.client.GWT;

/**
 * Importation scheme file formats enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum ImportationSchemeFileFormat implements Result {

	/**
	 * ".xlx", ".xls".
	 */
	MS_EXCEL,

	/**
	 * ".csv".
	 */
	CSV,

	/**
	 * Open Document Spreadsheet.
	 */
	ODS;

	/**
	 * Returns the given {@code fileFormat} corresponding name.<br/>
	 * This method should be executed from client-side. If executed from server-side, it returns the enum constant name.
	 * 
	 * @param fileFormat
	 *          The file format.
	 * @return the given {@code fileFormat} corresponding name, or {@code null}.
	 */
	public static String getStringValue(final ImportationSchemeFileFormat fileFormat) {

		if (fileFormat == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return fileFormat.name();
		}

		switch (fileFormat) {
			case CSV:
				return I18N.CONSTANTS.csv();

			case MS_EXCEL:
				return I18N.CONSTANTS.excel();

			case ODS:
				return I18N.CONSTANTS.ods();

			default:
				return fileFormat.name();
		}
	}

}
