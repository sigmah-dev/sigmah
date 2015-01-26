package org.sigmah.server.servlet.exporter.utils;

import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;

public class ExporterUtil {

	/**
	 * Gets the label of the flexible element.
	 * 
	 * @param fleDTO
	 * @param i18nTranslator
	 * @param language
	 * @return The flexible element i18n label.
	 */
	public static String getFlexibleElementLabel(FlexibleElementDTO fleDTO, I18nServer i18nTranslator, final Language language) {
		String fleName = null;
		if (fleDTO.getLabel() != null) {
			fleName = fleDTO.getLabel();
		} else if (fleDTO instanceof DefaultFlexibleElementDTO) {
			fleName = getFlexibleElementLabel(((DefaultFlexibleElementDTO) fleDTO).getType(), i18nTranslator, language);
		}
		return fleName;
	}

	/**
	 * Gets the label of the flexible element.
	 * 
	 * @param fle
	 * @param i18nTranslator
	 * @param language
	 * @return The flexible element i18n label.
	 */
	public static String getFlexibleElementLabel(FlexibleElement fle, I18nServer i18nTranslator, final Language language) {
		String fleName = null;
		if (fle.getLabel() != null) {
			fleName = fle.getLabel();
		} else if (fle instanceof DefaultFlexibleElement) {
			fleName = getFlexibleElementLabel(((DefaultFlexibleElement) fle).getType(), i18nTranslator, language);
		}
		return fleName;
	}

	/**
	 * Gets the label of the default flexible element type
	 * 
	 * @param type
	 * @param i18nTranslator
	 * @param language
	 * @return The default flexible element i18n label.
	 */
	public static String getFlexibleElementLabel(DefaultFlexibleElementType type, I18nServer i18nTranslator, final Language language) {
		String fleName = null;
		switch (type) {
			case BUDGET:
				fleName = i18nTranslator.t(language, "projectBudget");
				break;
			case CODE:
				fleName = i18nTranslator.t(language, "projectName");
				break;
			case COUNTRY:
				fleName = i18nTranslator.t(language, "projectCountry");
				break;
			case END_DATE:
				fleName = i18nTranslator.t(language, "projectEndDate");
				break;
			case MANAGER:
				fleName = i18nTranslator.t(language, "projectManager");
				break;
			case ORG_UNIT:
				fleName = i18nTranslator.t(language, "orgUnit");
				break;
			case OWNER:
				fleName = i18nTranslator.t(language, "projectOwner");
				break;
			case START_DATE:
				fleName = i18nTranslator.t(language, "projectStartDate");
				break;
			case TITLE:
				fleName = i18nTranslator.t(language, "projectFullName");
				break;
			default:
				break;
		}
		return fleName;
	}
}
