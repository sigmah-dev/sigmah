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
	 * Delete a project
	 */
	DELETE_PROJECT,

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
	// Removes temporarily the permission to avoid viewing non functional
	// screens
	// VIEW_ACTIVITYINFO,

	/**
	 * Validate the amendement.
	 */
	VALID_AMENDEMENT,

	/**
	 * for viewing the logframe sub-tab
	 */
	VIEW_LOGFRAME,

	/**
	 * for creating/modifying/deleting objectives, expected results, activities,
	 * hypothesis or linking/unlinking indicators to the logframe
	 */
	EDIT_LOGFRAME,

	/**
	 * for viewing the agenda sub-tab
	 */
	VIEW_AGENDA,

	/**
	 * 
	 * for creating/deleting/modifying events in the agenda
	 */
	EDIT_AGENDA,

	/**
	 * 
	 * for creating/deleting/modifying/closing reminders created by the user
	 */

	EDIT_OWN_REMINDERS,

	/**
	 * 
	 * for creating/deleting/modifying/closing reminders created by the user or
	 * by other users
	 */

	EDIT_ALL_REMINDERS,

	/**
	 * Show global export button in projects list
	 */
	GLOBAL_EXPORT;

	public static String getName(GlobalPermissionEnum gp) {
		String gpName = "default";
		switch (gp) {
		case VIEW_PROJECT:
			gpName = I18N.CONSTANTS.VIEW_PROJECT();
			break;
		case EDIT_PROJECT:
			gpName = I18N.CONSTANTS.EDIT_PROJECT();
			break;
		case CREATE_PROJECT:
			gpName = I18N.CONSTANTS.CREATE_PROJECT();
			break;
		case DELETE_PROJECT:
			gpName = I18N.CONSTANTS.DELETE_PROJECT();
			break;
		case CHANGE_PHASE:
			gpName = I18N.CONSTANTS.CHANGE_PHASE();
			break;
		case VIEW_ADMIN:
			gpName = I18N.CONSTANTS.VIEW_ADMIN();
			break;
		case MANAGE_USER:
			gpName = I18N.CONSTANTS.MANAGE_USER();
			break;
		case MANAGE_UNIT:
			gpName = I18N.CONSTANTS.MANAGE_UNIT();
			break;
		case REMOVE_FILE:
			gpName = I18N.CONSTANTS.REMOVE_FILE();
			break;
		// case VIEW_ACTIVITYINFO:
		// gpName = I18N.CONSTANTS.VIEW_ACTIVITYINFO();
		// break;
		case VALID_AMENDEMENT:
			gpName = I18N.CONSTANTS.VALIDER_AMENDEMENT();
			break;
		case VIEW_LOGFRAME:
			gpName = I18N.CONSTANTS.VIEW_LOGFRAME();
			break;
		case EDIT_LOGFRAME:
			gpName = I18N.CONSTANTS.EDIT_LOGFRAME();
			break;
		case VIEW_AGENDA:
			gpName = I18N.CONSTANTS.VIEW_AGENDA();
			break;
		case EDIT_AGENDA:
			gpName = I18N.CONSTANTS.EDIT_AGENDA();
			break;
		case EDIT_ALL_REMINDERS:
			gpName = I18N.CONSTANTS.EDIT_ALL_REMINDERS();
			break;
		case EDIT_OWN_REMINDERS:
			gpName = I18N.CONSTANTS.EDIT_OWN_REMINDERS();
			break;
		case GLOBAL_EXPORT:
			gpName = I18N.CONSTANTS.globalExport();
			break;

		default:
			gpName = null;
		}

		return gpName;
	}
}
