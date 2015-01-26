package org.sigmah.shared.dto.referential;

import java.io.Serializable;

import org.sigmah.client.i18n.I18N;

import com.google.gwt.core.client.GWT;

/**
 * Import status codes enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum ImportStatusCode implements Serializable {

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

	/**
	 * Returns the given {@code code} corresponding string value.<br/>
	 * This method should be executed from client-side. If executed from server-side, it returns the enum constant name.
	 * 
	 * @param code
	 *          The {@code ImportStatusCode} code.
	 * @return the given {@code code} corresponding string value, or {@code null}.
	 */
	public static String getStringValue(final ImportStatusCode code) {

		if (code == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return code.name();
		}

		switch (code) {
			case ORGUNIT_FOUND_CODE:
				return I18N.CONSTANTS.importOrgUnitFound();
			case ORGUNIT_NOT_FOUND_CODE:
				return I18N.CONSTANTS.importOrgUnitNotFound();
			case PROJECT_FOUND_CODE:
				return I18N.CONSTANTS.importProjectFound();
			case PROJECT_LOCKED_CODE:
				return I18N.CONSTANTS.importProjectCoreLocked();
			case PROJECT_NOT_FOUND_CODE:
				return I18N.CONSTANTS.importProjectNotFound();
			case SEVERAL_ORGUNITS_FOUND_CODE:
				return I18N.CONSTANTS.importSeveralOrgUnitsFound();
			case SEVERAL_PROJECTS_FOUND_CODE:
				return I18N.CONSTANTS.importSeveralProjectsFound();
			default:
				return code.name();
		}
	}

	public static boolean isFound(final ImportStatusCode entityStatus) {
		return ORGUNIT_FOUND_CODE.equals(entityStatus) || PROJECT_FOUND_CODE.equals(entityStatus);
	}

}
