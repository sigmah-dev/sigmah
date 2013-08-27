package org.sigmah.shared.domain.importation;

import org.sigmah.client.i18n.I18N;

public enum ImportationSchemeFileFormat {
	/**
	 * .xlx, .xls
	 */
	MS_EXCEL,

	/**
	 * .csv
	 */
	CSV,

	/**
	 * Open Document Spreadsheet
	 */
	ODS;

	public static String getStringValue(ImportationSchemeFileFormat fileFormat) {
		String fileFormatName = "default";
		switch (fileFormat) {
		case CSV:
			fileFormatName = I18N.CONSTANTS.csv();
			break;
		case MS_EXCEL:
			fileFormatName = I18N.CONSTANTS.excel();
			break;
		case ODS:
			fileFormatName = I18N.CONSTANTS.ods();
			break;
		default:
			break;
		}
		return fileFormatName;
	}
}
