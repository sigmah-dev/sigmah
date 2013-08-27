package org.sigmah.shared.dto;

import org.sigmah.client.i18n.I18N;

public class ImportUtils {

	public final static String SHEET_CELL_SEPARATOR = ":";

	public static enum ImportStatusCode {

		/**
		 * The project is not found
		 */
		PROJECT_NOT_FOUND_CODE,

		/**
		 * One project is found but is locked
		 */
		PROJECT_LOCKED_CODE,

		/**
		 * The project is found
		 */
		PROJECT_FOUND_CODE,

		/**
		 * Several projects are found
		 */
		SEVERAL_PROJECTS_FOUND_CODE,

		/**
		 * The org unit is not found
		 */
		ORGUNIT_NOT_FOUND_CODE,

		/**
		 * The org unit is found
		 */
		ORGUNIT_FOUND_CODE,

		/**
		 * Several org units are found
		 */
		SEVERAL_ORGUNITS_FOUND_CODE;

		public static String getStringValue(ImportStatusCode code) {
			String codeStringValue = "";
			switch (code) {
			case ORGUNIT_FOUND_CODE:
				codeStringValue = I18N.CONSTANTS.importOrgUnitFound();
				break;
			case ORGUNIT_NOT_FOUND_CODE:
				codeStringValue = I18N.CONSTANTS.importOrgUnitNotFound();
				break;
			case PROJECT_FOUND_CODE:
				codeStringValue = I18N.CONSTANTS.importProjectFound();
				break;
			case PROJECT_LOCKED_CODE:
				codeStringValue = I18N.CONSTANTS.importProjectCoreLocked();
				break;
			case PROJECT_NOT_FOUND_CODE:
				codeStringValue = I18N.CONSTANTS.importProjectNotFound();
				break;
			case SEVERAL_ORGUNITS_FOUND_CODE:
				codeStringValue = I18N.CONSTANTS.importSeveralOrgUnitsFound();
				break;
			case SEVERAL_PROJECTS_FOUND_CODE:
				codeStringValue = I18N.CONSTANTS.importSeveralProjectsFound();
				break;
			default:
				break;

			}
			return codeStringValue;
		}

	}

}
