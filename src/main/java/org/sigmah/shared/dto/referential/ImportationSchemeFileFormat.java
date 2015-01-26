package org.sigmah.shared.dto.referential;

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
