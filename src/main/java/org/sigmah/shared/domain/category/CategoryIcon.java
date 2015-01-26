package org.sigmah.shared.domain.category;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;

public enum CategoryIcon {
    CIRCLE, CROSS, DIAMOND, SQUARE, STAR, TRIANGLE;
    
    private static Map<String, CategoryIcon> nameMap = new HashMap<String, CategoryIcon>();
    
    public static String getName(CategoryIcon e){
		
		nameMap.clear();
		nameMap.put(I18N.CONSTANTS.adminCategoryCircle(), CategoryIcon.CIRCLE);
		nameMap.put(I18N.CONSTANTS.adminCategoryCross(), CategoryIcon.CROSS);
		nameMap.put(I18N.CONSTANTS.adminCategoryDiamond(), CategoryIcon.DIAMOND);
		nameMap.put(I18N.CONSTANTS.adminCategorySquare(), CategoryIcon.SQUARE);
		nameMap.put(I18N.CONSTANTS.adminCategoryStar(), CategoryIcon.STAR);
		nameMap.put(I18N.CONSTANTS.adminCategoryTriangle(), CategoryIcon.TRIANGLE);
		String name = null;
		
		for(Map.Entry<String, CategoryIcon> entry : nameMap.entrySet()){
			if(entry.getValue().equals(e)){
				return name = entry.getKey();
			}
		}
		return name;
	}
    
    public static CategoryIcon getIcon(String e){
		
    	nameMap.clear();
    	nameMap.put(I18N.CONSTANTS.adminCategoryCircle(), CategoryIcon.CIRCLE);
		nameMap.put(I18N.CONSTANTS.adminCategoryCross(), CategoryIcon.CROSS);
		nameMap.put(I18N.CONSTANTS.adminCategoryDiamond(), CategoryIcon.DIAMOND);
		nameMap.put(I18N.CONSTANTS.adminCategorySquare(), CategoryIcon.SQUARE);
		nameMap.put(I18N.CONSTANTS.adminCategoryStar(), CategoryIcon.STAR);
		nameMap.put(I18N.CONSTANTS.adminCategoryTriangle(), CategoryIcon.TRIANGLE);
		
		CategoryIcon type = null;
		
		for(Map.Entry<String, CategoryIcon> entry : nameMap.entrySet()){
			if(entry.getKey().equals(e)){
				return type = entry.getValue();
			}
		}
		return type;
	}
}
