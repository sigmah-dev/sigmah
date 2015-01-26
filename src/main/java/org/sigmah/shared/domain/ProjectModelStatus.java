package org.sigmah.shared.domain;

import org.sigmah.client.i18n.I18N;

/**
 * The different status of projects.
 * 
 * @author nrebiai
 * 
 */
public enum ProjectModelStatus {

	DRAFT, READY, USED, UNAVAILABLE;

    /**
     * Gets the translation value for the given status. To use only on the
     * client-side.
     * 
     * @param type
     *            The type.
     * @return The translation value for the given type.
     */
    public static String getName(ProjectModelStatus status){
		String statusName = "default";
		switch(status){
		case DRAFT : statusName = I18N.CONSTANTS.DRAFT(); break;
		case READY : statusName = I18N.CONSTANTS.READY(); break;
		case USED : statusName = I18N.CONSTANTS.USED(); break;
		case UNAVAILABLE : statusName = I18N.CONSTANTS.UNAVAILABLE(); break;
		default : statusName = null;
		}
		
		return statusName;
	}
    
    public static ProjectModelStatus getStatus(String status){
    	ProjectModelStatus statusEnum = DRAFT;
    	if(I18N.CONSTANTS.DRAFT().equals(status)){
    		statusEnum = DRAFT;
    	}else if(I18N.CONSTANTS.READY().equals(status)){
    		statusEnum = READY;
    	}else if(I18N.CONSTANTS.USED().equals(status)){
    		statusEnum = USED;
    	}else if(I18N.CONSTANTS.UNAVAILABLE().equals(status)){
    		statusEnum = UNAVAILABLE;
    	}
		
		return statusEnum;
	}
}
