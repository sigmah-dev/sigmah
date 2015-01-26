package org.sigmah.server.domain.util;

/**
 * <p>
 * Utility class referencing entities filters propertities (native SQL conditions, filters keys, etc.).
 * </p>
 * <p>
 * <em>Thank you for maintaining entities alphabetical order in this class.</em>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class EntityFilters {

	private EntityFilters() {
		// This class only provides static constants.
	}

	/**
	 * <p>
	 * <em>User filter</em> key.<br/>
	 * Filters data that user is not authorized to see.
	 * </p>
	 * <p>
	 * Expects a {@code currentUserId} parameter.
	 * </p>
	 * 
	 * @see #CURRENT_USER_ID
	 */
	public static final String USER_VISIBLE = "userVisible";

	/**
	 * <p>
	 * <em>Deleted filter</em> key.<br/>
	 * Hides deleted data.
	 * </p>
	 * <p>
	 * This filter does not expect parameters.
	 * </p>
	 */
	public static final String HIDE_DELETED = "hideDeleted";

	/**
	 * The {@code currentUserId} filter parameter key.
	 * 
	 * @see #USER_VISIBLE
	 */
	public static final String CURRENT_USER_ID = "currentUserId";

	// --------------------------------------------------------------------------------
	//
	// ACTIVITY GROUP ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String ACTIVITY_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// ATTRIBUTE GROUP ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String ATTRIBUTE_GROUP_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// FILE META ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String FILE_META_HIDE_DELETED_CONDITION = EntityConstants.FILE_META_COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// FILE META ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String FILE_VERSION_HIDE_DELETED_CONDITION = EntityConstants.FILE_VERSION_COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION SCHEME ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String IMPORTATION_SCHEME_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION SCHEME ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String IMPORTATION_SCHEME_MODEL_HIDE_DELETED_CONDITION = EntityConstants.IMPORTATION_SCHEME_MODEL_COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION SCHEME VARIALBE FLEXIBLE ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String VARIABLE_FLEXIBLE_ELEMENT_HIDE_DELETED_CONDITION = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION VARIABLE ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String IMPORTATION_VARIABLE_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// INDICATOR ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String INDICATOR_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// INDICATOR VALUE ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String INDICATOR_VALUE_HIDE_DELETED_CONDITION = "("
		+ EntityConstants.INDICATOR_COLUMN_ID
		+ " NOT IN (SELECT i."
		+ EntityConstants.INDICATOR_COLUMN_ID
		+ " FROM "
		+ EntityConstants.INDICATOR_TABLE
		+ " i WHERE i."
		+ EntityConstants.COLUMN_DATE_DELETED
		+ " IS NOT NULL))";

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String LOGFRAME_ELEMENT_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// ORG UNIT ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String ORG_UNIT_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED_ + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// PERSONAL EVENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String PERSONAL_EVENT_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// PROJECT MODEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String PROJECT_MODEL_HIDE_DELETED_CONDITION = EntityConstants.PROJECT_MODEL_COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// PROJECT REPORT ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String PROJECT_REPORT_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// REPORT DEFINITION ENTITY.
	//
	// --------------------------------------------------------------------------------

	// USER VISIBLE FILTER.

	private static final String REPORT_DEFINITION_SELECT = "SELECT p."
		+ EntityConstants.USER_COLUMN_ID
		+ " FROM "
		+ EntityConstants.USER_PERMISSION_TABLE
		+ " p WHERE p."
		+ EntityConstants.USER_PERMISSION_COLUMN_ALLOW_VIEW
		+ " AND p."
		+ EntityConstants.USER_COLUMN_ID
		+ " = :"
		+ CURRENT_USER_ID
		+ " AND p."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " = "
		+ EntityConstants.USER_DATABASE_COLUMN_ID;

	/**
	 * Expects a {@code currentUserId} parameter.
	 * 
	 * @see #CURRENT_USER_ID
	 */
	public static final String REPORT_DEFINITION_USER_VISIBLE_CONDITION = "(:"
		+ CURRENT_USER_ID
		+ " = "
		+ EntityConstants.REPORT_DEFINITION_COLUMN_OWNER_USER_ID
		+ " OR ("
		+ EntityConstants.REPORT_DEFINITION_COLUMN_VISIBILITY
		+ " = 1 AND ("
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " IS NULL OR "
		+ CURRENT_USER_ID
		+ " IN ("
		+ REPORT_DEFINITION_SELECT
		+ "))))";

	// HIDE DELETED FILTER.

	public static final String REPORT_DEFINITION_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// REPORTING PERIOD ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String REPORTING_PERIOD_HIDE_DELETED_CONDITION = "("
		+ EntityConstants.INDICATOR_COLUMN_ID
		+ " NOT IN (SELECT i."
		+ EntityConstants.INDICATOR_COLUMN_ID
		+ " FROM "
		+ EntityConstants.INDICATOR_TABLE
		+ " i WHERE i."
		+ EntityConstants.COLUMN_DATE_DELETED
		+ " IS NOT NULL))";

	// --------------------------------------------------------------------------------
	//
	// SITE ENTITY.
	//
	// --------------------------------------------------------------------------------

	// USER VISIBLE FILTER.

	private static final String SITE_SELECT_UP = "SELECT p."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " FROM "
		+ EntityConstants.USER_PERMISSION_TABLE
		+ " p WHERE p."
		+ EntityConstants.USER_COLUMN_ID
		+ " = :"
		+ CURRENT_USER_ID;

	private static final String SITE_F1 = "SELECT d."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " FROM "
		+ EntityConstants.USER_DATABASE_TABLE
		+ " d WHERE d."
		+ EntityConstants.USER_DATABASE_COLUMN_OWNER_USER_ID
		+ " = :"
		+ CURRENT_USER_ID;

	private static final String SITE_F2 = "d."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " IN ("
		+ SITE_SELECT_UP
		+ " AND p."
		+ EntityConstants.USER_PERMISSION_COLUMN_ALLOW_VIEW_ALL
		+ ')';

	private static final String SITE_F3 = "d."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " IN ("
		+ SITE_SELECT_UP
		+ " AND p."
		+ EntityConstants.USER_PERMISSION_COLUMN_ALLOW_VIEW
		+ " AND p."
		+ EntityConstants.ORG_UNIT_COLUMN_ID
		+ " = "
		+ EntityConstants.ORG_UNIT_COLUMN_ID
		+ ")";

	/**
	 * Expects a {@code currentUserId} parameter.
	 * 
	 * @see #CURRENT_USER_ID
	 */
	public static final String SITE_USER_VISIBLE_CONDITION = '('
		+ EntityConstants.ACTIVITY_COLUMN_ID
		+ " IN (SELECT a."
		+ EntityConstants.ACTIVITY_COLUMN_ID
		+ " FROM "
		+ EntityConstants.ACTIVITY_TABLE
		+ " a WHERE a."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " IN ("
		+ SITE_F1
		+ " OR "
		+ SITE_F2
		+ " OR "
		+ SITE_F3
		+ ")))";

	// HIDE DELETED FILTER.

	public static final String SITE_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// TRIPLET VALUE ENTITY.
	//
	// --------------------------------------------------------------------------------

	// HIDE DELETED FILTER.

	public static final String TRIPLET_VALUE_HIDE_DELETED_CONDITION = EntityConstants.TRIPLETS_VALUE_COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// USER DATABASE ENTITY.
	//
	// --------------------------------------------------------------------------------

	// USER VISIBLE FILTER.

	private static final String UD_F1 = "SELECT p."
		+ EntityConstants.USER_COLUMN_ID
		+ " FROM "
		+ EntityConstants.USER_PERMISSION_TABLE
		+ " p WHERE p."
		+ EntityConstants.USER_PERMISSION_COLUMN_ALLOW_VIEW
		+ " AND p."
		+ EntityConstants.USER_COLUMN_ID
		+ " = :"
		+ CURRENT_USER_ID
		+ " AND p."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " = "
		+ EntityConstants.USER_DATABASE_COLUMN_ID;

	private static final String UD_F2 = "SELECT p."
		+ EntityConstants.ORG_UNIT_PERMISSION_COLUMN_USER
		+ " FROM "
		+ EntityConstants.ORG_UNIT_PERMISSION_TABLE
		+ " p LEFT JOIN "
		+ EntityConstants.ORG_UNIT_USER_DATABASE_LINK_TABLE
		+ " m ON (p."
		+ EntityConstants.ORG_UNIT_PERMISSION_COLUMN_ORG_UNIT
		+ " = m."
		+ EntityConstants.ORG_UNIT_COLUMN_ID
		+ ") WHERE m."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " = "
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " AND p."
		+ EntityConstants.ORG_UNIT_PERMISSION_COLUMN_VIEW_ALL;

	/**
	 * Expects a {@code currentUserId} parameter.
	 * 
	 * @see #CURRENT_USER_ID
	 */
	public static final String USER_DATABASE_USER_VISIBLE_CONDITION = "(:"
		+ CURRENT_USER_ID
		+ " = "
		+ EntityConstants.USER_DATABASE_COLUMN_OWNER_USER_ID
		+ " OR :"
		+ CURRENT_USER_ID
		+ " IN ("
		+ UD_F1
		+ ") OR :"
		+ CURRENT_USER_ID
		+ " IN ("
		+ UD_F2
		+ "))";

	// HIDE DELETED FILTER.

	public static final String USER_DATABASE_HIDE_DELETED_CONDITION = EntityConstants.COLUMN_DATE_DELETED + " IS NULL";

	// --------------------------------------------------------------------------------
	//
	// USER PERMISSION ENTITY.
	//
	// --------------------------------------------------------------------------------

	// USER VISIBLE FILTER.

	private static final String UP_SELECT = "SELECT p."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " FROM "
		+ EntityConstants.USER_PERMISSION_TABLE
		+ " p WHERE p."
		+ EntityConstants.USER_COLUMN_ID
		+ " = :"
		+ CURRENT_USER_ID;

	private static final String UP_F1 = "SELECT d."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " FROM "
		+ EntityConstants.USER_DATABASE_TABLE
		+ " d WHERE d."
		+ EntityConstants.USER_DATABASE_COLUMN_OWNER_USER_ID
		+ " = :"
		+ CURRENT_USER_ID;

	private static final String UP_F2 = "d."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " IN ("
		+ UP_SELECT
		+ " AND p."
		+ EntityConstants.USER_PERMISSION_COLUMN_ALLOW_MANAGE_ALL_USERS
		+ ")";

	private static final String UP_F3 = "d."
		+ EntityConstants.USER_DATABASE_COLUMN_ID
		+ " IN ("
		+ UP_SELECT
		+ " AND p."
		+ EntityConstants.USER_PERMISSION_COLUMN_ALLOW_MANAGE_USERS
		+ " AND p."
		+ EntityConstants.ORG_UNIT_COLUMN_ID
		+ " = "
		+ EntityConstants.ORG_UNIT_COLUMN_ID
		+ ")";

	/**
	 * Expects a {@code currentUserId} parameter.
	 * 
	 * @see #CURRENT_USER_ID
	 */
	public static final String USER_PERMISSION_USER_VISIBLE_CONDITION = EntityConstants.USER_DATABASE_COLUMN_ID
		+ " IN ("
		+ UP_F1
		+ " OR "
		+ UP_F2
		+ " OR "
		+ UP_F3
		+ ")";

	// HIDE DELETED FILTER.

	public static final String USER_PERMISSION_HIDE_DELETED_CONDITION = EntityConstants.USER_PERMISSION_COLUMN_ALLOW_VIEW;

}
