package org.sigmah.shared.domain.profile;

import org.sigmah.client.i18n.I18N;

/**
 * List of the global permissions.
 * 
 * @author tmi
 * 
 */
public enum GlobalPermissionEnum {

    /**
     * View the projects list and the project page.
     */
    VIEW_PROJECT,

    /**
     * Edit and save the project details, the project phases, the project
     * funding, the log frame and the calendar.
     */
    EDIT_PROJECT,

    /**
     * Create a new project or a new funding.
     */
    CREATE_PROJECT,

    /**
     * Close or activate a phase.
     */
    CHANGE_PHASE,

    /**
     * View the admin link.
     */
    VIEW_ADMIN,

    /**
     * View the admin page to manage users.
     */
    MANAGE_USER,

    /**
     * View the admin page to manage the org units.
     */
    MANAGE_UNIT,

    /**
     * Remove a file (in the files list flexible element).
     */
    REMOVE_FILE,

    /**
     * View the ActivityInfo menu.
     */
    VIEW_ACTIVITYINFO,
    
 
    /**
     * View the ActivityInfo menu.
     */
    VALID_AMENDEMENT;
    
    public static String getName(GlobalPermissionEnum gp){
		String gpName = "default";
		switch(gp){
		case VIEW_PROJECT : gpName = I18N.CONSTANTS.VIEW_PROJECT(); break;
		case EDIT_PROJECT : gpName = I18N.CONSTANTS.EDIT_PROJECT(); break;
		case CREATE_PROJECT : gpName = I18N.CONSTANTS.CREATE_PROJECT(); break;
		case CHANGE_PHASE : gpName = I18N.CONSTANTS.CHANGE_PHASE(); break;
		case VIEW_ADMIN : gpName = I18N.CONSTANTS.VIEW_ADMIN(); break;
		case MANAGE_USER : gpName = I18N.CONSTANTS.MANAGE_USER(); break;
		case MANAGE_UNIT : gpName = I18N.CONSTANTS.MANAGE_UNIT(); break;
		case REMOVE_FILE : gpName = I18N.CONSTANTS.REMOVE_FILE(); break;
		case VIEW_ACTIVITYINFO : gpName = I18N.CONSTANTS.VIEW_ACTIVITYINFO(); break;
		case  VALID_AMENDEMENT : gpName = I18N.CONSTANTS. VALIDER_AMENDEMENT(); break;
		default : gpName = null;
		}
		
		return gpName;
	}
}
