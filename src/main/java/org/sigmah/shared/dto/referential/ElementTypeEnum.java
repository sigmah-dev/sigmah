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


import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;

import com.google.gwt.core.client.GWT;

/**
 * Element types enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum ElementTypeEnum implements LogicalElementType {

	CHECKBOX("CheckboxElement"),
	
	COMPUTATION("ComputationElement"),
	
	CORE_VERSION("CoreVersionElement"),

	DEFAULT("DefaultFlexibleElement"),

	FILES_LIST("FilesListElement"),

	INDICATORS("IndicatorsListElement"),

	MESSAGE("MessageElement"),

	QUESTION("QuestionElement"),

	REPORT("ReportElement"),

	REPORT_LIST("ReportListElement"),

	TEXT_AREA("TextAreaElement"),

	TRIPLETS("TripletsListElement");

	private static Map<String, ElementTypeEnum> nameMap;

	private final String className;

	private ElementTypeEnum(String className) {
		this.className = className;
	}
    
	/**
	 * Root package of domain elements.
	 */
	private static final String ROOT_PACKAGE = "org.sigmah.server.domain.element.";

	/**
	 * Returns the given {@code elementType} corresponding name value.
	 * This method should be executed from client-side. If executed from server-side, it returns a default name.
	 * 
	 * @param elementType
	 *          The {@code ElementTypeEnum} value.
	 * @return the given {@code elementType} corresponding name value, or a default name.
	 */
	public static String getName(final ElementTypeEnum elementType) {

		if (elementType == null || !GWT.isClient()) {
			return I18N.CONSTANTS.flexibleElementDefault();
		}

		switch (elementType) {
			case CHECKBOX:
				return I18N.CONSTANTS.flexibleElementCheckbox();
			case COMPUTATION:
				return I18N.CONSTANTS.flexibleElementComputation();
			case CORE_VERSION:
				return I18N.CONSTANTS.flexibleElementCoreVersion();
			case DEFAULT:
				return I18N.CONSTANTS.flexibleElementDefault();
			case FILES_LIST:
				return I18N.CONSTANTS.flexibleElementFilesList();
			case INDICATORS:
				return I18N.CONSTANTS.flexibleElementIndicatorsList();
			case MESSAGE:
				return I18N.CONSTANTS.flexibleElementMessage();
			case QUESTION:
				return I18N.CONSTANTS.flexibleElementQuestion();
			case REPORT:
				return I18N.CONSTANTS.flexibleElementReport();
			case REPORT_LIST:
				return I18N.CONSTANTS.flexibleElementReportList();
			case TEXT_AREA:
				return I18N.CONSTANTS.flexibleElementTextArea();
			case TRIPLETS:
				return I18N.CONSTANTS.flexibleElementTripletsList();
			default:
				return I18N.CONSTANTS.flexibleElementDefault();
		}
	}

	public static ElementTypeEnum getType(final String name) {
		if (nameMap == null) {
			nameMap = new HashMap<String, ElementTypeEnum>();
			nameMap.put(I18N.CONSTANTS.flexibleElementCheckbox(), ElementTypeEnum.CHECKBOX);
			nameMap.put(I18N.CONSTANTS.flexibleElementComputation(), ElementTypeEnum.COMPUTATION);
			nameMap.put(I18N.CONSTANTS.flexibleElementCoreVersion(), ElementTypeEnum.CORE_VERSION);
			nameMap.put(I18N.CONSTANTS.flexibleElementDefault(), ElementTypeEnum.DEFAULT);
			nameMap.put(I18N.CONSTANTS.flexibleElementFilesList(), ElementTypeEnum.FILES_LIST);
			nameMap.put(I18N.CONSTANTS.flexibleElementIndicatorsList(), ElementTypeEnum.INDICATORS);
			nameMap.put(I18N.CONSTANTS.flexibleElementMessage(), ElementTypeEnum.MESSAGE);
			nameMap.put(I18N.CONSTANTS.flexibleElementQuestion(), ElementTypeEnum.QUESTION);
			nameMap.put(I18N.CONSTANTS.flexibleElementReport(), ElementTypeEnum.REPORT);
			nameMap.put(I18N.CONSTANTS.flexibleElementReportList(), ElementTypeEnum.REPORT_LIST);
			nameMap.put(I18N.CONSTANTS.flexibleElementTextArea(), ElementTypeEnum.TEXT_AREA);
			nameMap.put(I18N.CONSTANTS.flexibleElementTripletsList(), ElementTypeEnum.TRIPLETS);
		}

		final ElementTypeEnum value = nameMap.get(name);
		return value != null ? value : DEFAULT;
	}

	public static String getClassName(final ElementTypeEnum e) {
		if (e != null) {
			return ROOT_PACKAGE + e.className;
		} else {
			return ROOT_PACKAGE + "default";
		}
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ElementTypeEnum toElementTypeEnum() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextAreaType toTextAreaType() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultFlexibleElementType toDefaultFlexibleElementType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return getName(this);
    }
    
}
