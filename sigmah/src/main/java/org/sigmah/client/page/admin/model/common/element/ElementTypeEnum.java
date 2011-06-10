package org.sigmah.client.page.admin.model.common.element;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;

public enum ElementTypeEnum{
	
	CHECKBOX,
	
	DEFAULT,
	
	FILES_LIST,
	
	INDICATORS,
	
	MESSAGE,
	
	QUESTION,
	
	REPORT,
	
	REPORT_LIST,
	
	TEXT_AREA,
	
	TRIPLETS;		
	
	private static Map<String, ElementTypeEnum> nameMap = new HashMap<String, ElementTypeEnum>();
	
	private static Map<String, ElementTypeEnum> classMap = new HashMap<String, ElementTypeEnum>();
	
	static {
		
		//classMap.put("BudgetDistributionElement", ElementTypeEnum.BUDGET);
		classMap.put("CheckboxElement", ElementTypeEnum.CHECKBOX);
		classMap.put("DefaultFlexibleElement", ElementTypeEnum.DEFAULT);
		classMap.put("FilesListElement", ElementTypeEnum.FILES_LIST);
		classMap.put("IndicatorsListElement", ElementTypeEnum.INDICATORS);
		classMap.put("MessageElement", ElementTypeEnum.MESSAGE);
		classMap.put("QuestionElement", ElementTypeEnum.QUESTION);
		classMap.put("ReportElement", ElementTypeEnum.REPORT);
		classMap.put("ReportListElement", ElementTypeEnum.REPORT_LIST);
		classMap.put("TextAreaElement", ElementTypeEnum.TEXT_AREA);
		classMap.put("TripletsListElement", ElementTypeEnum.TRIPLETS);
	}
	
	
	
	public static String getName(ElementTypeEnum e){
		
		nameMap.clear();
		//nameMap.put(I18N.CONSTANTS.projectBudget(), ElementTypeEnum.BUDGET);
		nameMap.put(I18N.CONSTANTS.flexibleElementCheckbox(), ElementTypeEnum.CHECKBOX);
		nameMap.put(I18N.CONSTANTS.flexibleElementDefault(),ElementTypeEnum.DEFAULT);
		nameMap.put(I18N.CONSTANTS.flexibleElementFilesList(), ElementTypeEnum.FILES_LIST);
		nameMap.put(I18N.CONSTANTS.flexibleElementIndicatorsList(), ElementTypeEnum.INDICATORS);
		nameMap.put(I18N.CONSTANTS.flexibleElementMessage(), ElementTypeEnum.MESSAGE);
		nameMap.put(I18N.CONSTANTS.flexibleElementQuestion(), ElementTypeEnum.QUESTION);
		nameMap.put(I18N.CONSTANTS.flexibleElementReport(), ElementTypeEnum.REPORT);
		nameMap.put(I18N.CONSTANTS.flexibleElementReportList(), ElementTypeEnum.REPORT_LIST);
		nameMap.put(I18N.CONSTANTS.flexibleElementTextArea(), ElementTypeEnum.TEXT_AREA);
		nameMap.put(I18N.CONSTANTS.flexibleElementTripletsList(), ElementTypeEnum.TRIPLETS);
		
		String name = I18N.CONSTANTS.flexibleElementDefault();
		
		for(Map.Entry<String, ElementTypeEnum> entry : nameMap.entrySet()){
			if(entry.getValue().equals(e)){
				return name = entry.getKey();
			}
		}
		return name;
	}
	
	public static ElementTypeEnum getType(String e){
		
		nameMap.clear();
		//nameMap.put(I18N.CONSTANTS.projectBudget(), ElementTypeEnum.BUDGET);
		nameMap.put(I18N.CONSTANTS.flexibleElementCheckbox(), ElementTypeEnum.CHECKBOX);
		nameMap.put(I18N.CONSTANTS.flexibleElementDefault(),ElementTypeEnum.DEFAULT);
		nameMap.put(I18N.CONSTANTS.flexibleElementFilesList(), ElementTypeEnum.FILES_LIST);
		nameMap.put(I18N.CONSTANTS.flexibleElementIndicatorsList(), ElementTypeEnum.INDICATORS);
		nameMap.put(I18N.CONSTANTS.flexibleElementMessage(), ElementTypeEnum.MESSAGE);
		nameMap.put(I18N.CONSTANTS.flexibleElementQuestion(), ElementTypeEnum.QUESTION);
		nameMap.put(I18N.CONSTANTS.flexibleElementReport(), ElementTypeEnum.REPORT);
		nameMap.put(I18N.CONSTANTS.flexibleElementReportList(), ElementTypeEnum.REPORT_LIST);
		nameMap.put(I18N.CONSTANTS.flexibleElementTextArea(), ElementTypeEnum.TEXT_AREA);
		nameMap.put(I18N.CONSTANTS.flexibleElementTripletsList(), ElementTypeEnum.TRIPLETS);
		
		ElementTypeEnum type = DEFAULT;
		
		for(Map.Entry<String, ElementTypeEnum> entry : nameMap.entrySet()){
			if(entry.getKey().equals(e)){
				return type = entry.getValue();
			}
		}
		return type;
	}
	
	public static String getClassName(ElementTypeEnum e){
		String name = "default";
		
		for(Map.Entry<String, ElementTypeEnum> entry : classMap.entrySet()){
			if(entry.getValue().equals(e)){
				return name = "org.sigmah.shared.domain.element." + entry.getKey();
			}
		}
		return "org.sigmah.shared.domain.element." + name;
	}
}
