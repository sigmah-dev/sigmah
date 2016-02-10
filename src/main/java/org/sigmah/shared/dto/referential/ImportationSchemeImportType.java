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
