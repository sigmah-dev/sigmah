package org.sigmah.shared.dto.referential;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.core.client.GWT;

/**
 * Importation scheme import types enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum ImportationSchemeImportType implements Result {

	/**
	 * Each row of the file corresponds to a new project.
	 */
	ROW,

	/**
	 * The project is defined on one or several sheet.
	 */
	UNIQUE,

	/**
	 * There is one project by sheet in the file.
	 */
	SEVERAL;

	/**
	 * Returns the given {@code importType} corresponding name.<br/>
	 * This method should be executed from client-side. If executed from server-side, it returns the enum constant name.
	 * 
	 * @param importType
	 *          The import type.
	 * @return the given {@code importType} corresponding name, or {@code null}.
	 */
	public static String getStringValue(final ImportationSchemeImportType importType) {

		if (importType == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return importType.name();
		}

		switch (importType) {
			case ROW:
				return I18N.CONSTANTS.importTypeRow();

			case SEVERAL:
				return I18N.CONSTANTS.importTypeSeveral();

			case UNIQUE:
				return I18N.CONSTANTS.importTypeUnique();

			default:
				return importType.name();
		}
	}

}
