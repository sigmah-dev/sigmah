package org.sigmah.shared.dto;

import java.io.Serializable;

import org.sigmah.client.i18n.I18N;


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
		 * 
		 * @param code
		 * @return
		 */

		public static String getMessage(ElementExtractedValueStatus code) {
			String statusStringValue = "";
			switch (code) {
			case FORBIDDEN_VALUE:
				statusStringValue = I18N.CONSTANTS.importElementExtractedValueStatusForbiddenValue();
				break;
			case INVALID_NUMBER_VALUE:
				statusStringValue = I18N.CONSTANTS.importElementExtractedValueStatusInvalidNumberValue();
				break;
			case INVALID_DATE_VALUE:
				statusStringValue = I18N.CONSTANTS.importElementExtractedValueStatusInvalidDateValue();
				break;
			case INVALID_TRIPLET_VALUE:
				statusStringValue = I18N.CONSTANTS.importElementExtractedValueStatusInvalidTripletValue();
				break;
			case INVALID_QUESTION_VALUE:
				statusStringValue = I18N.CONSTANTS.importElementExtractedValueStatusInvalidQuestionValue();
				break;
			case NOT_IMPORTABLE_FIELD:
				statusStringValue = I18N.CONSTANTS.importElementExtractedValueStatusNotImportable();
				break;
			default:
				break;

			}
			return statusStringValue;
		}

		
		ElementExtractedValueStatus() {
			  
        }

}
