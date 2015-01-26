package org.sigmah.shared.dto.referential;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.core.client.GWT;

/**
 * List of the global permissions.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum GlobalPermissionEnum implements Result {

	/**
	 * View the projects list and the project page.
	 */
	VIEW_PROJECT(GlobalPermissionCategory.PROJECT),

	/**
	 * Edit and save the project details, the project phases, the project funding, the log frame and the calendar.
	 */
	EDIT_PROJECT(GlobalPermissionCategory.PROJECT),

	/**
	 * Create a new project or a new funding.
	 */
	CREATE_PROJECT(GlobalPermissionCategory.PROJECT),

	/**
	 * Delete a project.
	 */
	DELETE_PROJECT(GlobalPermissionCategory.PROJECT),
	
	/**
	 * Lock or unlock a project.
	 */
	LOCK_PROJECT(GlobalPermissionCategory.PROJECT),

	/**
	 * Close or activate a phase.
	 */
	CHANGE_PHASE(GlobalPermissionCategory.PROJECT),

	/**
	 * View the admin link.
	 */
	VIEW_ADMIN(GlobalPermissionCategory.ADMINISTRATION),

	/**
	 * View the admin page to manage users.
	 */
	MANAGE_USER(GlobalPermissionCategory.ADMINISTRATION),

	/**
	 * View the admin page to manage the org units.
	 */
	MANAGE_UNIT(GlobalPermissionCategory.ADMINISTRATION),

	/**
	 * Remove a file (in the files list flexible element).
	 */
	REMOVE_FILE(GlobalPermissionCategory.OTHER),

	/**
	 * Validate the amendement.
	 */
	VALID_AMENDEMENT(GlobalPermissionCategory.PROJECT),

	/**
	 * for viewing the logframe sub-tab
	 */
	VIEW_LOGFRAME(null),

	/**
	 * for creating/modifying/deleting objectives, expected results, activities, hypothesis or linking/unlinking
	 * indicators to the logframe
	 */
	EDIT_LOGFRAME(null),

	/**
	 * for viewing the agenda sub-tab
	 */
	VIEW_AGENDA(null),

	/**
	 * for creating/deleting/modifying events in the agenda
	 */
	EDIT_AGENDA(null),

	/**
	 * for creating/deleting/modifying/closing reminders created by the user
	 */

	EDIT_OWN_REMINDERS(null),

	/**
	 * for creating/deleting/modifying/closing reminders created by the user or by other users
	 */

	EDIT_ALL_REMINDERS(null),

	/**
	 * for viewing the two indicator sub-tabs
	 */
	VIEW_INDICATOR(null),

	/**
	 * for creating/deleting/modifying indicator definitions
	 */
	MANAGE_INDICATOR(null),

	/**
	 * for editing values of existing indicators
	 */
	EDIT_INDICATOR(null),

	/**
	 * Show global export button in projects list
	 */
	GLOBAL_EXPORT(GlobalPermissionCategory.OTHER),
	
	/**
	 * For viewing Project > Map
	 */
	VIEW_MAPTAB(null),
	
	/**
	 * For setting/editing the main location(site)
	 */
	MANAGE_MAIN_SITE(null),
	
	/**
	 * For cretaing/editing sites
	 */
	MANAGE_SITES(null),
	
	/**
	 * For exporting HXL data
	 */
	EXPORT_HXL(null);

	/**
	 * The global permission category (never {@code null}).
	 */
	private final GlobalPermissionCategory category;

	/**
	 * Initializes the global permission with its category.
	 * 
	 * @param category
	 *          The category, or {@code null} if not categorized.
	 */
	private GlobalPermissionEnum(final GlobalPermissionCategory category) {
		this.category = category == null ? GlobalPermissionCategory._NONE : category;
	}

	/**
	 * Returns the global permission related category.
	 * 
	 * @return The global permission related category (never {@code null}).
	 */
	public GlobalPermissionCategory getCategory() {
		return category;
	}

	/**
	 * Global permissions category.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum GlobalPermissionCategory {

		// WARNING: Categories ordinal order is used in profiles administration.

		PROJECT,

		ADMINISTRATION,

		OTHER,

		/**
		 * Uncategorized.
		 */
		// Should be the last category.
		_NONE;

		/**
		 * <p>
		 * Returns the given {@code globalPermissionCategory} corresponding name.<br>
		 * If the given {@code globalPermissionCategory} is {@code null}, the method returns an <em>unmapped</em> name.
		 * </p>
		 * <p>
		 * If this method is executed from server-side, it returns the given {@code globalPermissionCategory} constant name.
		 * </p>
		 * 
		 * @param globalPermissionCategory
		 *          The global permission category.
		 * @return the given {@code globalPermissionCategory} corresponding name.
		 */
		public static String getName(GlobalPermissionCategory globalPermissionCategory) {

			if (globalPermissionCategory == null) {
				globalPermissionCategory = GlobalPermissionCategory._NONE;
			}

			if (!GWT.isClient()) {
				return globalPermissionCategory.name();
			}

			switch (globalPermissionCategory) {

				case PROJECT:
					return I18N.CONSTANTS.categoryProject();

				case ADMINISTRATION:
					return I18N.CONSTANTS.categoryAdministration();

				case OTHER:
					return I18N.CONSTANTS.categoryOthers();

				default:
					return I18N.CONSTANTS.categoryNotMapped();
			}
		}
	}

	/**
	 * <p>
	 * Returns the given {@code globalPermission} corresponding name.
	 * </p>
	 * <p>
	 * If this method is executed from server-side, it returns the given {@code globalPermission} constant name.
	 * </p>
	 * 
	 * @param globalPermission
	 *          The global permission.
	 * @return the given {@code globalPermission} corresponding name, or {@code null}.
	 */
	public static String getName(final GlobalPermissionEnum globalPermission) {

		if (globalPermission == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return globalPermission.name();
		}

		switch (globalPermission) {

			case VIEW_PROJECT:
				return I18N.CONSTANTS.VIEW_PROJECT();

			case EDIT_PROJECT:
				return I18N.CONSTANTS.EDIT_PROJECT();

			case CREATE_PROJECT:
				return I18N.CONSTANTS.CREATE_PROJECT();

			case DELETE_PROJECT:
				return I18N.CONSTANTS.DELETE_PROJECT();

			case LOCK_PROJECT:
				return I18N.CONSTANTS.LOCK_PROJECT();
				
			case CHANGE_PHASE:
				return I18N.CONSTANTS.CHANGE_PHASE();

			case VIEW_ADMIN:
				return I18N.CONSTANTS.VIEW_ADMIN();

			case MANAGE_USER:
				return I18N.CONSTANTS.MANAGE_USER();

			case MANAGE_UNIT:
				return I18N.CONSTANTS.MANAGE_UNIT();

			case REMOVE_FILE:
				return I18N.CONSTANTS.REMOVE_FILE();

			case VALID_AMENDEMENT:
				return I18N.CONSTANTS.VALID_AMENDEMENT();

			case VIEW_LOGFRAME:
				return I18N.CONSTANTS.VIEW_LOGFRAME();

			case EDIT_LOGFRAME:
				return I18N.CONSTANTS.EDIT_LOGFRAME();

			case VIEW_AGENDA:
				return I18N.CONSTANTS.VIEW_AGENDA();

			case EDIT_AGENDA:
				return I18N.CONSTANTS.EDIT_AGENDA();

			case EDIT_ALL_REMINDERS:
				return I18N.CONSTANTS.EDIT_ALL_REMINDERS();

			case EDIT_OWN_REMINDERS:
				return I18N.CONSTANTS.EDIT_OWN_REMINDERS();

			case VIEW_INDICATOR:
				return I18N.CONSTANTS.VIEW_INDICATOR();

			case MANAGE_INDICATOR:
				return I18N.CONSTANTS.MANAGE_INDICATOR();

			case EDIT_INDICATOR:
				return I18N.CONSTANTS.EDIT_INDICATOR();

			case GLOBAL_EXPORT:
				return I18N.CONSTANTS.GLOBAL_EXPORT();
				
			case VIEW_MAPTAB:
				return I18N.CONSTANTS.VIEW_MAPTAB();
				
			case MANAGE_MAIN_SITE:
				return I18N.CONSTANTS.MANAGE_MAIN_SITE();
				
			case MANAGE_SITES:
				return I18N.CONSTANTS.MANAGE_SITES();
				
			case EXPORT_HXL:
				return I18N.CONSTANTS.EXPORT_HXL();

			default:
				return globalPermission.name();
		}
	}

}
