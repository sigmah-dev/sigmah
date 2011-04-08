package org.sigmah.shared.domain.element;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;

/**
 * All possible types of default flexible element.
 * 
 * @author tmi
 * 
 */
public enum DefaultFlexibleElementType {
    CODE, TITLE, BUDGET, START_DATE, END_DATE, COUNTRY, OWNER, MANAGER, ORG_UNIT;
    
    private static Map<String, DefaultFlexibleElementType> nameMap = new HashMap<String, DefaultFlexibleElementType>();
    
    public static String getName(DefaultFlexibleElementType e){
		
		nameMap.clear();
		nameMap.put(I18N.CONSTANTS.projectName(), DefaultFlexibleElementType.CODE);
		nameMap.put(I18N.CONSTANTS.projectFullName(), DefaultFlexibleElementType.TITLE);
		nameMap.put(I18N.CONSTANTS.projectBudget(),DefaultFlexibleElementType.BUDGET);
		nameMap.put(I18N.CONSTANTS.projectStartDate(), DefaultFlexibleElementType.START_DATE);
		nameMap.put(I18N.CONSTANTS.projectEndDate(), DefaultFlexibleElementType.END_DATE);
		nameMap.put(I18N.CONSTANTS.projectCountry(), DefaultFlexibleElementType.COUNTRY);
		nameMap.put(I18N.CONSTANTS.projectOwner(), DefaultFlexibleElementType.OWNER);
		nameMap.put(I18N.CONSTANTS.projectManager(), DefaultFlexibleElementType.MANAGER);
		nameMap.put(I18N.CONSTANTS.orgunit(), DefaultFlexibleElementType.ORG_UNIT);
		
		String name = null;
		
		for(Map.Entry<String, DefaultFlexibleElementType> entry : nameMap.entrySet()){
			if(entry.getValue().equals(e)){
				return name = entry.getKey();
			}
		}
		return name;
	}
}
