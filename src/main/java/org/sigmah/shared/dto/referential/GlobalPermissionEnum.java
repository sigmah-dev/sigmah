package org.sigmah.shared.dto.referential;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.core.client.GWT;
import java.util.ArrayList;
import java.util.List;

/**
 * List of the global permissions.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum GlobalPermissionEnum implements Result {
	
	/*
	 * Project-related permissions. 
	 */

	/**
	 * View the projects list and the project page.
	 */
	VIEW_PROJECT(GlobalPermissionCategory.PROJECT),

	/**
	 * Edit and save the project details, the project phases, the project funding, the log frame and the calendar.
	 */
	EDIT_PROJECT(VIEW_PROJECT),

	/**
	 * Create a new project or a new funding.
	 */
	CREATE_PROJECT(GlobalPermissionCategory.PROJECT),
	
	/**
	 * Create a new test project.
	 */
	CREATE_TEST_PROJECT(CREATE_PROJECT),

	/**
	 * Delete a project.
	 */
	DELETE_PROJECT(GlobalPermissionCategory.PROJECT),
	
	/**
	 * Lock or unlock a project.
	 */
	LOCK_PROJECT(GlobalPermissionCategory.PROJECT),
	
	/**
	 * Modify locked content. (i. e. content of closed phases and content of closed projects).
	 */
	MODIFY_LOCKED_CONTENT(EDIT_PROJECT),
	
	/**
	 * Remove a file (in the files list flexible element of projects).
	 */
	REMOVE_PROJECT_FILE(EDIT_PROJECT),

	/**
	 * Close or activate a phase.
	 */
	CHANGE_PHASE(EDIT_PROJECT),
	
	/**
	 * For relating projects.
	 */
	RELATE_PROJECT(EDIT_PROJECT),
	
	/**
	 * Validate the amendement.
	 */
	VALID_AMENDEMENT(EDIT_PROJECT),

	/**
	 * for viewing the logframe sub-tab.
	 */
	VIEW_LOGFRAME(VIEW_PROJECT),

	/**
	 * for creating/modifying/deleting objectives, expected results, activities,
	 * hypothesis or linking/unlinking indicators to the logframe.
	 */
	EDIT_LOGFRAME(VIEW_LOGFRAME),

	/**
	 * for viewing the agenda sub-tab.
	 */
	VIEW_PROJECT_AGENDA(VIEW_PROJECT),

	/**
	 * for creating/deleting/modifying events in the agenda.
	 */
	EDIT_PROJECT_AGENDA(VIEW_PROJECT_AGENDA),
	
	/**
	 * for creating/deleting/modifying/closing reminders created by the user.
	 */

	EDIT_OWN_REMINDERS(VIEW_PROJECT),
	
	/**
	 * for displaying the import button.
	 */
	IMPORT_BUTTON(GlobalPermissionCategory.OTHER),
	
	/**
	 * for creating/deleting/modifying/closing reminders created by the user or 
	 * by other users.
	 */

	EDIT_ALL_REMINDERS(EDIT_OWN_REMINDERS),
	
	/**
	 * for viewing the two indicator sub-tabs.
	 */
	VIEW_INDICATOR(VIEW_PROJECT),

	/**
	 * for creating/deleting/modifying indicator definitions.
	 */
	MANAGE_INDICATOR(VIEW_INDICATOR),

	/**
	 * for editing values of existing indicators.
	 */
	EDIT_INDICATOR(VIEW_INDICATOR),
	
	/**
	 * For viewing Project > Map.
	 */
	VIEW_MAPTAB(VIEW_INDICATOR),
	
	/**
	 * For setting/editing the main location(site).
	 */
	MANAGE_MAIN_SITE(VIEW_MAPTAB),
	
	/**
	 * For creating/editing sites.
	 */
	MANAGE_SITES(VIEW_MAPTAB),
	
	
	/*
	 * Org unit-related permissions. 
	 */
	
	/**
	 * Edit and save the org. unit content.
	 */
	EDIT_ORG_UNIT(GlobalPermissionCategory.ORG_UNIT),
	
	/**
	 * Remove a file (in the files list flexible element of org. units).
	 */
	REMOVE_ORG_UNIT_FILE(EDIT_ORG_UNIT),

	/**
	 * For viewing the agenda sub-tab of org. units.
	 */
	VIEW_ORG_UNIT_AGENDA(GlobalPermissionCategory.ORG_UNIT),
	
	/**
	 * For creating/deleting/modifying events in the agenda of org. units.
	 */
	EDIT_ORG_UNIT_AGENDA(VIEW_ORG_UNIT_AGENDA),
	
	
	/*
	 * Administration-related permissions. 
	 */

	/**
	 * View the admin link.
	 */
	VIEW_ADMIN(GlobalPermissionCategory.ADMINISTRATION),

	/**
	 * View the admin page to manage users.
	 */
	MANAGE_USERS(VIEW_ADMIN),

	/**
	 * View the admin page to manage the org units.
	 */
	MANAGE_ORG_UNITS(VIEW_ADMIN),
	
	/**
	 * View the admin page to manage project models.
	 */
	MANAGE_PROJECT_MODELS(VIEW_ADMIN),

	/**
	 * View the admin page to manage org unit models.
	 */
	MANAGE_ORG_UNIT_MODELS(VIEW_ADMIN),

	/**
	 * View the admin page to manage report models.
	 */
	MANAGE_REPORT_MODELS(VIEW_ADMIN),

	/**
	 * View the admin page to manage categories.
	 */
	MANAGE_CATEGORIES(VIEW_ADMIN),

	/**
	 * View the admin page to manage importation schemes.
	 */
	MANAGE_IMPORTATION_SCHEMES(VIEW_ADMIN),

	/**
	 * View the admin page to manage system settings.
	 */
	MANAGE_SETTINGS(VIEW_ADMIN),

	/**
	 * Show global export button in projects list.
	 */
	GLOBAL_EXPORT(GlobalPermissionCategory.OTHER),
	
	/**
	 * For exporting HXL data.
	 */
	EXPORT_HXL(GlobalPermissionCategory.OTHER),
	
	/**
     * For changing own password.
     */
	CHANGE_PASSWORD(GlobalPermissionCategory.OTHER),
	/**
	 * For mangment measure performances.
	 */
	PROBES_MANGMENT(GlobalPermissionCategory.OTHER);
	
	/**
	 * The global permission category (never {@code null}).
	 */
	private final GlobalPermissionCategory category;
	
	/**
	 * The parent permission.
	 * {@code null} if a category is defined.
	 */
	private final GlobalPermissionEnum parent;

	/**
	 * Initializes the global permission with its category.
	 * 
	 * @param category
	 *          The category, or {@code null} if not categorized.
	 */
	private GlobalPermissionEnum(final GlobalPermissionCategory category) {
		this.category = category == null ? GlobalPermissionCategory._NONE : category;
		this.parent = null;
	}
	
	/**
	 * 
	 * @param parent 
	 */
	private GlobalPermissionEnum(final GlobalPermissionEnum parent) {
		this.category = parent.getCategory();
		this.parent = parent;
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
	 * Returns the parent permission.
	 * 
	 * @return The parent permission or <code>null</code>.
	 */
	public GlobalPermissionEnum getParent() {
		return parent;
	}
	
	public boolean hasDependency() {
		return getParent() != null;
	}
	
	public List<GlobalPermissionEnum> getChildren() {
		final ArrayList<GlobalPermissionEnum> children = new ArrayList<GlobalPermissionEnum>();
		
		for (GlobalPermissionEnum permission : values()) {
			if (permission != this && permission.getParent() == this) {
				children.add(permission);
			}
		}
		
		return children;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	public boolean isLeaf() {
		return getChildren().isEmpty();
	}

	/**
	 * Global permissions category.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum GlobalPermissionCategory {

		// WARNING: Categories ordinal order is used in profiles administration.

		PROJECT,
		
		INDICATOR,
		
		ORG_UNIT,

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
					
				case INDICATOR:
					return I18N.CONSTANTS.categoryIndicator();
					
				case ORG_UNIT:
					return I18N.CONSTANTS.categoryOrgUnit();

				case ADMINISTRATION:
					return I18N.CONSTANTS.categoryAdministration();

				case OTHER:
					return I18N.CONSTANTS.categoryOthers();

				default:
					return I18N.CONSTANTS.categoryNotMapped();
			}
		}
		
		public List<GlobalPermissionEnum> getChildren() {
			final ArrayList<GlobalPermissionEnum> permissions = new ArrayList<GlobalPermissionEnum>();
			
			for (GlobalPermissionEnum permission : GlobalPermissionEnum.values()) {
				if (permission.getCategory() == this && permission.getParent() == null) {
					permissions.add(permission);
				}
			}
			
			return permissions;
		}
		
		public boolean isEmpty() {
			return getChildren().isEmpty();
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
				
			case CREATE_TEST_PROJECT:
				return I18N.CONSTANTS.CREATE_TEST_PROJECT();
				
			case DELETE_PROJECT:
				return I18N.CONSTANTS.DELETE_PROJECT();

			case LOCK_PROJECT:
				return I18N.CONSTANTS.LOCK_PROJECT();
				
			case MODIFY_LOCKED_CONTENT:
				return I18N.CONSTANTS.MODIFY_LOCKED_CONTENT();
				
			case REMOVE_PROJECT_FILE:
				return I18N.CONSTANTS.REMOVE_PROJECT_FILE();
				
			case CHANGE_PHASE:
				return I18N.CONSTANTS.CHANGE_PHASE();
				
			case RELATE_PROJECT:
				return I18N.CONSTANTS.RELATE_PROJECT();
				
			case VALID_AMENDEMENT:
				return I18N.CONSTANTS.VALID_AMENDEMENT();

			case VIEW_LOGFRAME:
				return I18N.CONSTANTS.VIEW_LOGFRAME();

			case EDIT_LOGFRAME:
				return I18N.CONSTANTS.EDIT_LOGFRAME();

			case VIEW_PROJECT_AGENDA:
				return I18N.CONSTANTS.VIEW_AGENDA();

			case EDIT_PROJECT_AGENDA:
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
				
			case VIEW_MAPTAB:
				return I18N.CONSTANTS.VIEW_MAPTAB();
				
			case MANAGE_MAIN_SITE:
				return I18N.CONSTANTS.MANAGE_MAIN_SITE();
				
			case MANAGE_SITES:
				return I18N.CONSTANTS.MANAGE_SITES();

			case GLOBAL_EXPORT:
				return I18N.CONSTANTS.GLOBAL_EXPORT();
				
			case EDIT_ORG_UNIT:
				return I18N.CONSTANTS.EDIT_ORG_UNIT();
				
			case REMOVE_ORG_UNIT_FILE:
				return I18N.CONSTANTS.REMOVE_ORG_UNIT_FILE();
				
			case VIEW_ORG_UNIT_AGENDA:
				return I18N.CONSTANTS.VIEW_ORG_UNIT_AGENDA();
				
			case EDIT_ORG_UNIT_AGENDA:
				return I18N.CONSTANTS.EDIT_ORG_UNIT_AGENDA();
			
			case VIEW_ADMIN:
				return I18N.CONSTANTS.VIEW_ADMIN();

			case MANAGE_USERS:
				return I18N.CONSTANTS.MANAGE_USERS();

			case MANAGE_ORG_UNITS:
				return I18N.CONSTANTS.MANAGE_ORG_UNITS();
				
			case MANAGE_PROJECT_MODELS:
				return I18N.CONSTANTS.MANAGE_PROJECT_MODELS();
				
			case MANAGE_ORG_UNIT_MODELS:
				return I18N.CONSTANTS.MANAGE_ORG_UNIT_MODELS();
				
			case MANAGE_REPORT_MODELS:
				return I18N.CONSTANTS.MANAGE_REPORT_MODELS();
				
			case MANAGE_CATEGORIES:
				return I18N.CONSTANTS.MANAGE_CATEGORIES();
				
			case MANAGE_IMPORTATION_SCHEMES:
				return I18N.CONSTANTS.MANAGE_IMPORTATION_SCHEMES();
				
			case MANAGE_SETTINGS:
				return I18N.CONSTANTS.MANAGE_SETTINGS();
				
			case EXPORT_HXL:
				return I18N.CONSTANTS.EXPORT_HXL();
				
			case PROBES_MANGMENT:
				return I18N.CONSTANTS.PROBES_MANGMENT();
			default:
				return globalPermission.name();
		}
	}

}
