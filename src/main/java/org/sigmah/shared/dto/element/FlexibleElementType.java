package org.sigmah.shared.dto.element;

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


import java.util.HashMap;

import org.sigmah.client.i18n.I18N;

/**
 * Utility class to get some properties of each type of flexible element.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class FlexibleElementType {

	public static final HashMap<Class<? extends FlexibleElementDTO>, String> types;

	static {
		types = new HashMap<Class<? extends FlexibleElementDTO>, String>();
		types.put(BudgetDistributionElementDTO.class, I18N.CONSTANTS.flexibleElementBudgetDistribution());
		types.put(CheckboxElementDTO.class, I18N.CONSTANTS.flexibleElementCheckbox());
		types.put(FilesListElementDTO.class, I18N.CONSTANTS.flexibleElementFilesList());
		types.put(IndicatorsListElementDTO.class, I18N.CONSTANTS.flexibleElementIndicatorsList());
		types.put(MessageElementDTO.class, I18N.CONSTANTS.flexibleElementMessage());
		types.put(QuestionElementDTO.class, I18N.CONSTANTS.flexibleElementQuestion());
		types.put(TextAreaElementDTO.class, I18N.CONSTANTS.flexibleElementTextArea());
		types.put(TripletsListElementDTO.class, I18N.CONSTANTS.flexibleElementTripletsList());
		types.put(CoreVersionElementDTO.class, I18N.CONSTANTS.flexibleElementCoreVersion());
		types.put(ComputationElementDTO.class, I18N.CONSTANTS.flexibleElementComputation());
	}

	/**
	 * Provides only static methods.
	 */
	private FlexibleElementType() {
	}

	/**
	 * Gets a flexible element type name (translation key).
	 * 
	 * @param fe
	 *          The flexible element.
	 * @return The flexible element type name (translation key).
	 */
	public static <E extends FlexibleElementDTO> String getFlexibleElementTypeName(E fe) {
		if (fe instanceof DefaultFlexibleElementDTO) {
			return getDefaultFlexibleElementTypeName((DefaultFlexibleElementDTO) fe);
		}
        if(fe instanceof TextAreaElementDTO){
            return getTextAreaFlexibleElementTypeName((TextAreaElementDTO) fe);
        }
		return types.get(fe.getClass());
	}
    private static String getTextAreaFlexibleElementTypeName(TextAreaElementDTO element) {
		switch (element.getType()) {
        case 'P':
            return I18N.CONSTANTS.flexibleElementParagraph();
        case 'T':
            return I18N.CONSTANTS.flexibleElementTextArea();
        case 'D':
            return I18N.CONSTANTS.flexibleElementDate();
        case 'N':
            return I18N.CONSTANTS.flexibleElementNumber();
        default:
            return "checkerror";
        }
}
	private static String getDefaultFlexibleElementTypeName(DefaultFlexibleElementDTO element) {
		switch (element.getType()) {
			case CODE:
			case TITLE:
			case START_DATE:
			case END_DATE:
				return types.get(TextAreaElementDTO.class);
			case COUNTRY:
			case MANAGER:
			case ORG_UNIT:
				return types.get(QuestionElementDTO.class);
			case BUDGET:
				return types.get(BudgetDistributionElementDTO.class);
			default:
				return "???";
		}
	}
}
