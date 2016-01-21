package org.sigmah.shared.dto.referential;

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
public enum ElementTypeEnum {

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
	 * Returns the given {@code elementType} corresponding name value.<br/>
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
}
