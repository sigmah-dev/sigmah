package org.sigmah.shared.dto.referential;

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
	 * Returns the given {@code code} corresponding message.<br/>
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
