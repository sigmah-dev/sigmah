package org.sigmah.shared.dto.referential;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.core.client.GWT;

/**
 * All possible types of default flexible element.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum DefaultFlexibleElementType implements Result {

	CODE,
	TITLE,
	BUDGET,
	START_DATE,
	END_DATE,
	COUNTRY,
	OWNER,
	MANAGER,
	ORG_UNIT;

	/**
	 * Returns the given {@code flexibleElementType} corresponding name.<br/>
	 * This method should be executed from client-side. If executed from server-side, it returns the enum constant name.
	 * 
	 * @param flexibleElementType
	 *          The flexibleElement type.
	 * @return the given {@code flexibleElementType} corresponding name, or {@code null}.
	 */
	public static String getName(final DefaultFlexibleElementType flexibleElementType) {

		if (flexibleElementType == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return flexibleElementType.name();
		}

		switch (flexibleElementType) {
			case CODE:
				return I18N.CONSTANTS.projectName();

			case TITLE:
				return I18N.CONSTANTS.projectFullName();

			case BUDGET:
				return I18N.CONSTANTS.projectBudget();

			case START_DATE:
				return I18N.CONSTANTS.projectStartDate();

			case END_DATE:
				return I18N.CONSTANTS.projectEndDate();

			case COUNTRY:
				return I18N.CONSTANTS.projectCountry();

			case OWNER:
				return I18N.CONSTANTS.projectOwner();

			case MANAGER:
				return I18N.CONSTANTS.projectManager();

			case ORG_UNIT:
				return I18N.CONSTANTS.orgunit();

			default:
				return flexibleElementType.name();
		}
	}
}
