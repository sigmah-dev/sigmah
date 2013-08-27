package org.sigmah.shared.domain.importation;

import org.sigmah.client.i18n.I18N;

public enum ImportationSchemeImportType {

	/**
	 * Each row of the file corresponds to a new project
	 */
	ROW,

	/**
	 * The project is defined on one or several sheet
	 */
	UNIQUE,

	/**
	 * There is one project by sheet in the file
	 */
	SEVERAL;

	public static String getStringValue(ImportationSchemeImportType importType) {
		String importTypeName = "default";
		switch (importType) {
		case ROW:
			importTypeName = I18N.CONSTANTS.importTypeRow();
			break;
		case SEVERAL:
			importTypeName = I18N.CONSTANTS.importTypeSeveral();
			break;
		case UNIQUE:
			importTypeName = I18N.CONSTANTS.importTypeUnique();
			break;
		default:
			break;
		}
		return importTypeName;
	}
}
