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

import java.io.Serializable;

import org.sigmah.client.i18n.I18N;

import com.google.gwt.core.client.GWT;

/**
 * Element extracted value status enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum ElementExtractedValueStatus implements Serializable {

	/**
	 * the value has the expected format
	 */
	VALID_VALUE,

	INVALID_TRIPLET_VALUE,

	/**
	 * the value is not a number
	 */
	INVALID_NUMBER_VALUE,

	INVALID_DATE_VALUE,

	/**
	 * The value doesn't satisfy the restrictions
	 */
	FORBIDDEN_VALUE,

	NOT_IMPORTABLE_FIELD,

	/**
	 * The value is not in the question element's choices
	 */
	INVALID_QUESTION_VALUE;

	/**
	 * Returns the given {@code code} corresponding message.
	 * This method should be executed from client-side. If executed from server-side, it returns the enum constant name.
	 * 
	 * @param code
	 *          The {@code ElementExtractedValueStatus} code.
	 * @return the given {@code code} corresponding message, or {@code null}.
	 */
	public static String getMessage(final ElementExtractedValueStatus code) {

		if (code == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return code.name();
		}

		switch (code) {
			case FORBIDDEN_VALUE:
				return I18N.CONSTANTS.importElementExtractedValueStatusForbiddenValue();
			case INVALID_NUMBER_VALUE:
				return I18N.CONSTANTS.importElementExtractedValueStatusInvalidNumberValue();
			case INVALID_DATE_VALUE:
				return I18N.CONSTANTS.importElementExtractedValueStatusInvalidDateValue();
			case INVALID_TRIPLET_VALUE:
				return I18N.CONSTANTS.importElementExtractedValueStatusInvalidTripletValue();
			case INVALID_QUESTION_VALUE:
				return I18N.CONSTANTS.importElementExtractedValueStatusInvalidQuestionValue();
			case NOT_IMPORTABLE_FIELD:
				return I18N.CONSTANTS.importElementExtractedValueStatusNotImportable();
			default:
				return code.name();
		}
	}

}
