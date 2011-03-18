package org.sigmah.shared.domain.profile;

import org.sigmah.client.i18n.I18N;

/**
 * List of the permissions linked to a privacy group.
 * 
 * @author tmi
 * 
 */
public enum PrivacyGroupPermissionEnum {

    /**
     * Forbids all actions.
     */
    NONE,

    /**
     * Allows to view.
     */
    READ,

    /**
     * Allows to view and edit.
     */
    WRITE;
    
    public static String getName(PrivacyGroupPermissionEnum pg){
		String pgName = "default";
		switch(pg){
		case NONE : pgName = I18N.CONSTANTS.none(); break;
		case READ : pgName = I18N.CONSTANTS.view(); break;
		case WRITE : pgName = I18N.CONSTANTS.edit(); break;
		default : pgName = null;
		}
		
		return pgName;
	}
    
    public static PrivacyGroupPermissionEnum translatePGPermission(String pg){
		
		PrivacyGroupPermissionEnum pgName = null;
		if(I18N.CONSTANTS.none().equals(pg))
			pgName = PrivacyGroupPermissionEnum.NONE;
		else if(I18N.CONSTANTS.view().equals(pg))
			pgName = PrivacyGroupPermissionEnum.READ;
		else if(I18N.CONSTANTS.edit().equals(pg))
			pgName =PrivacyGroupPermissionEnum.WRITE;
				
		return pgName;
	}
}
