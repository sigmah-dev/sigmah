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
		String gpName = "default";
		switch(status){
		case DRAFT : gpName = I18N.CONSTANTS.DRAFT(); break;
		case READY : gpName = I18N.CONSTANTS.READY(); break;
		case USED : gpName = I18N.CONSTANTS.USED(); break;
		case UNAVAILABLE : gpName = I18N.CONSTANTS.UNAVAILABLE(); break;
		default : gpName = null;
		}
		
		return gpName;
	}
}
