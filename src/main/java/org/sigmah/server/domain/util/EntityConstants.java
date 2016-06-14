package org.sigmah.server.domain.util;

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

/**
 * <p>
 * Utility class referencing entities constants (table and column names, sizes, etc.).
 * </p>
 * <p>
 * <em>Thank you for maintaining entities alphabetical order in this class.</em>
 * </p>
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class EntityConstants extends org.sigmah.shared.dto.util.EntityConstants {

	private EntityConstants() {
		// This class only provides static constants.
	}

	// --------------------------------------------------------------------------------
	//
	// _COMMON.
	//
	// --------------------------------------------------------------------------------

	public static final String COLUMN_DEFINITION_TEXT = "TEXT";

	public static final String COLUMN_DATE_CREATED = "dateCreated";
	public static final String COLUMN_DATE_EDITED = "dateEdited";
	public static final String COLUMN_DATE_DELETED = "datedeleted";
	/**
	 * Userd for column date_deleted
	 */
	public static final String COLUMN_DATE_DELETED_ = "date_deleted";
	public static final String COLUMN_SORT_ORDER = "SortOrder";

	public static final int LOCALE_MAX_LENGTH = 10;
	public static final int EMAIL_MAX_LENGTH = 75;
	public static final int ISO2_CODE_MAX_LENGTH = 2;

	// --------------------------------------------------------------------------------
	//
	// ACTIVITY ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ACTIVITY_TABLE = "Activity";
	public static final String ACTIVITY_COLUMN_ID = "ActivityId";
	public static final String ACTIVITY_COLUMN_NAME = "Name";
	public static final String ACTIVITY_COLUMN_CATEGORY = "category";
	public static final String ACTIVITY_COLUMN_REPORTING_FREQUENCY = "ReportingFrequency";
	public static final String ACTIVITY_COLUMN_ASSESSMENT = "IsAssessment";
	public static final String ACTIVITY_COLUMN_ALLOW_EDIT = "AllowEdit";
	public static final String ACTIVITY_COLUMN_MAP_ICON = "mapIcon";

	public static final int ACTIVITY_NAME_MAX_LENGTH = 45;
	public static final int ACTIVITY_CATEGORY_MAX_LENGTH = 255;
	public static final int ACTIVITY_MAP_ICON_MAX_LENGTH = 255;

	// --------------------------------------------------------------------------------
	//
	// ADMIN ENTITY ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ADMIN_ENTITY_TABLE = "AdminEntity";
	public static final String ADMIN_ENTITY_COLUMN_ID = "AdminEntityId";
	public static final String ADMIN_ENTITY_COLUMN_NAME = "Name";
	public static final String ADMIN_ENTITY_COLUMN_SOUNDEX = "Soundex";
	public static final String ADMIN_ENTITY_COLUMN_CODE = "Code";
	public static final String ADMIN_ENTITY_COLUMN_PARENT = "AdminEntityParentId";

	public static final int ADMIN_ENTITY_CODE_MAX_LENGTH = 15;

	// --------------------------------------------------------------------------------
	//
	// ADMIN LEVEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ADMIN_LEVEL_TABLE = "AdminLevel";
	public static final String ADMIN_LEVEL_COLUMN_ID = "AdminLevelId";
	public static final String ADMIN_LEVEL_COLUMN_NAME = "Name";
	public static final String ADMIN_LEVEL_COLUMN_ALLOW_ADD = "AllowAdd";
	public static final String ADMIN_LEVEL_COLUMN_PARENT = "ParentId";

	public static final int ADMIN_LEVEL_NAME_MAX_LENGTH = 30;

	// --------------------------------------------------------------------------------
	//
	// AMENDMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String AMENDMENT_TABLE = "amendment";
	public static final String AMENDMENT_COLMUN_NAME = "name";
	public static final String AMENDMENT_COLMUN_ID = "id_amendment";
	public static final String AMENDMENT_COLMUN_VERSION = "version";
	public static final String AMENDMENT_COLMUN_REVISION = "revision";
	public static final String AMENDMENT_COLMUN_STATUS = "status";
	public static final String AMENDMENT_COLMUN_DATE = "history_date";

	// --------------------------------------------------------------------------------
	//
	// ATTRIBUTE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ATTRIBUTE_TABLE = "Attribute";
	public static final String ATTRIBUTE_COLUMN_ID = "AttributeId";
	public static final String ATTRIBUTE_COLUMN_NAME = "Name";

	// --------------------------------------------------------------------------------
	//
	// ATTRIBUTE GROUP ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ATTRIBUTE_GROUP_TABLE = "AttributeGroup";
	public static final String ATTRIBUTE_GROUP_COLUMN_ID = "AttributeGroupId";
	public static final String ATTRIBUTE_GROUP_COLUMN_NAME = "Name";
	public static final String ATTRIBUTE_GROUP_COLUMN_CATEGORY = "category";
	public static final String ATTRIBUTE_GROUP_MULTIPLE_ALLOWED = "multipleAllowed";

	public static final int ATTRIBUTE_GROUP_NAME_MAX_LENGTH = 255;
	public static final int ATTRIBUTE_GROUP_CATEGORY_MAX_LENGTH = 50;

	public static final String ATTRIBUTE_GROUP_ACTIVITY_LINK_TABLE = "AttributeGroupInActivity";

	// --------------------------------------------------------------------------------
	//
	// ATTRIBUTE VALUE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ATTRIBUTE_VALUE_TABLE = "AttributeValue";
	public static final String ATTRIBUTE_VALUE_COLUMN_VALUE = "Value";

	// --------------------------------------------------------------------------------
	//
	// AUTHENTICATION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String AUTHENTICATION_TABLE = "Authentication";
	public static final String AUTHENTICATION_COLUMN_ID = "AuthToken";
	public static final String AUTHENTICATION_COLUMN_DATE_LAST_ACTIVE = "dateLastActive";

	public static final int AUTHENTICATION_ID_MAX_LENGTH = 32;

	// --------------------------------------------------------------------------------
	//
	// BOUNDS EMBEDDABLE.
	//
	// --------------------------------------------------------------------------------

	public static final String BOUNDS_COLUMN_X1 = "x1";
	public static final String BOUNDS_COLUMN_Y1 = "y1";
	public static final String BOUNDS_COLUMN_X2 = "x2";
	public static final String BOUNDS_COLUMN_Y2 = "y2";

	// --------------------------------------------------------------------------------
	//
	// BUDGET ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String BUDGET_TABLE = "budget";
	public static final String BUDGET_COLUMN_ID = "id_budget";
	public static final String BUDGET_COLUMN_TOTAL_AMOUNT = "total_amount";

	public static final int BUDGET_TOTAL_AMOUNT_PRECISION = 2;

	// --------------------------------------------------------------------------------
	//
	// BUDGET DISTRIBUTION ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String BUDGET_DISTRIBUTION_ELEMENT_TABLE = "budget_distribution_element";

	// --------------------------------------------------------------------------------
	//
	// BUDGET ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String BUDGET_ELEMENT_TABLE = "budget_element";
	// TODO Replace with 'id_budget_sub_field' column.
	public static final String BUDGET_ELEMENT_COLUMN_ID_RATIO_DIVIDEND = "id_ratio_dividend";
	// TODO Replace with 'id_budget_sub_field' column.
	public static final String BUDGET_ELEMENT_COLUMN_ID_RATIO_DIVISOR = "id_ratio_divisor";

	// --------------------------------------------------------------------------------
	//
	// BUDGET PART ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String BUDGET_PART_TABLE = "budget_part";
	public static final String BUDGET_PART_COLUMN_ID = "id_budget_part";
	public static final String BUDGET_PART_COLUMN_TOTAL_AMOUNT = "amount";
	public static final String BUDGET_PART_COLUMN_LABEL = "label";

	public static final int BUDGET_PART_TOTAL_AMOUNT_PRECISION = 2;
	public static final int BUDGET_PART_LABEL_MAX_LENGTH = 2048;

	// --------------------------------------------------------------------------------
	//
	// BUDGET PART LIST VALUE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String BUDGET_PART_LIST_VALUE_TABLE = "budget_parts_list_value";
	public static final String BUDGET_PART_LIST_VALUE_COLUMN_ID = "id_budget_parts_list";

	// --------------------------------------------------------------------------------
	//
	// BUDGET SUB FIELD ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String BUDGET_SUB_FIELD_TABLE = "budget_sub_field";
	public static final String BUDGET_SUB_FIELD_COLUMN_ID = "id_budget_sub_field";
	// TODO Replace with 'id_flexible_element' column.
	public static final String BUDGET_SUB_FIELD_COLMUN_ID_BUDGET_ELEMENT = "id_budget_element";
	public static final String BUDGET_SUB_FIELD_COLMUN_LABEL = "label";
	public static final String BUDGET_SUB_FIELD_COLMUN_FIELD_ORDER = "fieldorder";
	public static final String BUDGET_SUB_FIELD_COLMUN_TYPE = "type";

	// --------------------------------------------------------------------------------
	//
	// CATEGORY TYPE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CATEGORY_TYPE_TABLE = "category_type";
	public static final String CATEGORY_TYPE_COLUMN_ID = "id_category_type";
	public static final String CATEGORY_TYPE_COLUMN_ICON_NAME = "icon_name";
	public static final String CATEGORY_TYPE_COLUMN_LABEL = "label";

	public static final int CATEGORY_TYPE_LABEL_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// CATEGORY TYPE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CATEGORY_ELEMENT_TABLE = "category_element";
	public static final String CATEGORY_ELEMENT_COLUMN_ID = "id_category_element";
	public static final String CATEGORY_ELEMENT_COLUMN_COLOR_HEX = "color_hex";
	public static final String CATEGORY_ELEMENT_COLUMN_LABEL = "label";

	public static final int CATEGORY_ELEMENT_COLOR_HEX_MAX_LENGTH = 6;

	// --------------------------------------------------------------------------------
	//
	// CHECKBOX ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CHECKBOX_ELEMENT_TABLE = "checkbox_element";

	// --------------------------------------------------------------------------------
	//
	// COMPUTATION ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String COMPUTATION_ELEMENT_TABLE = "computation_element";
	public static final String COMPUTATION_ELEMENT_COLUMN_RULE = "rule";
	public static final String COMPUTATION_ELEMENT_COLUMN_MINIMUM_VALUE = "minimum";
	public static final String COMPUTATION_ELEMENT_COLUMN_MAXIMUM_VALUE = "maximum";
	
	public static final int COMPUTATION_ELEMENT_RULE_MAX_LENGTH = 1500;
	
	// --------------------------------------------------------------------------------
	//
	// CORE VERSION ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CORE_VERSION_ELEMENT_TABLE = "core_version_element";
	
	// --------------------------------------------------------------------------------
	//
	// COUNTRY ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String COUNTRY_TABLE = "Country";
	public static final String COUNTRY_COLUMN_ID = "CountryId";
	public static final String COUNTRY_COLUMN_NAME = "Name";
	public static final String COUNTRY_COLUMN_ISO_CODE = "ISO2";
	
	// --------------------------------------------------------------------------------
	//
	// CRITERION TYPE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CRITERION_TYPE_TABLE = "quality_criterion_type";
	public static final String CRITERION_TYPE_COLUMN_ID = "id_criterion_type";
	public static final String CRITERION_TYPE_COLUMN_LABEL = "label";
	public static final String CRITERION_TYPE_COLUMN_LEVEL = "level";

	public static final int CRITERION_TYPE_LABEL_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// DEFAULT FLEXIBLE ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String DEFAULT_FLEXIBLE_ELEMENT_TABLE = "default_flexible_element";
	public static final String DEFAULT_FLEXIBLE_ELEMENT_COLMUN_TYPE = "type";

	// --------------------------------------------------------------------------------
	//
	// FILES LIST ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FILES_LIST_ELEMENT_TABLE = "files_list_element";
	public static final String FILES_LIST_ELEMENT_COLUMN_MAX_LIMIT = "max_limit";

	// --------------------------------------------------------------------------------
	//
	// FILE META ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FILE_META_TABLE = "file_meta";
	public static final String FILE_META_COLUMN_ID = "id_file";
	public static final String FILE_META_COLUMN_DATE_DELETED = "datedeleted";
	public static final String FILE_META_COLUMN_NAME = "name";

	// --------------------------------------------------------------------------------
	//
	// FILES VERSION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FILE_VERSION_TABLE = "file_version";
	public static final String FILE_VERSION_COLUMN_ID = "id_file_version";
	public static final String FILE_VERSION_COLUMN_ADDED_DATE = "added_date";
	public static final String FILE_VERSION_COLUMN_COMMENTS = "comments";
	public static final String FILE_VERSION_COLUMN_DATE_DELETED = "datedeleted";
	public static final String FILE_VERSION_COLUMN_EXTENSION = "extension";
	public static final String FILE_VERSION_COLUMN_NAME = "name";
	public static final String FILE_VERSION_COLUMN_PATH = "path";
	public static final String FILE_VERSION_COLUMN_SIZE = "size";
	public static final String FILE_VERSION_COLUMN_VERSION_NUMBER = "version_number";
	public static final String FILE_VERSION_COLUMN_ID_AUTHOR = "id_author";
	public static final String FILE_VERSION_COLUMN_ID_FILE = "id_file";

	public static final int FILE_VERSION_EXTENSION_MAX_SIZE = 1024;

	// --------------------------------------------------------------------------------
	//
	// FLEXIBLE ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FLEXIBLE_ELEMENT_TABLE = "flexible_element";
	public static final String FLEXIBLE_ELEMENT_COLUMN_ID = "id_flexible_element";
	public static final String FLEXIBLE_ELEMENT_COLUMN_LABEL = "label";
	public static final String FLEXIBLE_ELEMENT_COLUMN_CODE = "code";
	public static final String FLEXIBLE_ELEMENT_COLUMN_VALIDATES = "validates";
	public static final String FLEXIBLE_ELEMENT_COLUMN_AMENDABLE = "amendable";
	public static final String FLEXIBLE_ELEMENT_COLUMN_EXPORTABLE = "exportable";
	public static final String FLEXIBLE_ELEMENT_COLUMN_GLOBALLY_EXPORTABLE = "globally_exportable";
	public static final String FLEXIBLE_ELEMENT_COLUMN_DISABLED_DATE = "disabled_date";
	public static final String FLEXIBLE_ELEMENT_COLUMN_CREATION_DATE = "creation_date";
	
	public static final int FLEXIBLE_ELEMENT_CODE_MAX_LENGTH = 30;

	// --------------------------------------------------------------------------------
	//
	// GLOBAL EXPORT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String GLOBAL_EXPORT_TABLE = "global_export";
	public static final String GLOBAL_EXPORT_COLUMN_ID = "id";
	public static final String GLOBAL_EXPORT_COLUMN_DATE = "generated_date";
	// TODO Replace with 'Organization' id column.
	public static final String GLOBAL_EXPORT_COLUMN_ORGANIZATION = "organization_id";

	// --------------------------------------------------------------------------------
	//
	// GLOBAL EXPORT CONTENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String GLOBAL_EXPORT_CONTENT_TABLE = "global_export_content";
	public static final String GLOBAL_EXPORT_CONTENT_COLUMN_ID = "id";
	public static final String GLOBAL_EXPORT_CONTENT_COLUMN_PROJECT_MODEL_NAME = "project_model_name";
	public static final String GLOBAL_EXPORT_CONTENT_COLUMN_CSV_CONTENT = "csv_content";
	// TODO Replace with 'GlobalExport' id column.
	public static final String GLOBAL_EXPORT_CONTENT_COLUMN_GLOBAL_EXPORT_ID = "global_export_id";

	public static final int GLOBAL_EXPORT_CONTENT_PROJECT_MODEL_NAME_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// GLOBAL EXPORT SETTINGS ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String GLOBAL_EXPORT_SETTINGS_TABLE = "global_export_settings";
	public static final String GLOBAL_EXPORT_SETTINGS_COLUMN_ID = "id";
	public static final String GLOBAL_EXPORT_SETTINGS_COLUMN_EXPORT_FORMAT = "export_format";
	public static final String GLOBAL_EXPORT_SETTINGS_COLUMN_DEFAULT_ORG_EXPORT_FORMAT = "default_organization_export_format";
	public static final String GLOBAL_EXPORT_SETTINGS_COLUMN_LAST_EXPORT_DATE = "last_export_date";
	public static final String GLOBAL_EXPORT_SETTINGS_COLUMN_AUTO_EXPORT_FREQUENCY = "auto_export_frequency";
	public static final String GLOBAL_EXPORT_SETTINGS_COLUMN_AUTO_DELETE_FREQUENCY = "auto_delete_frequency";
	public static final String GLOBAL_EXPORT_SETTINGS_COLUMN_LOCALE = "locale_string";
	// TODO Replace with 'Organization' id column.
	public static final String GLOBAL_EXPORT_SETTINGS_COLUMN_ORGANIZATION_ID = "organization_id";

	public static final int GLOBAL_EXPORT_SETTINGS_LOCALE_MAX_LENGTH = 4;

	// --------------------------------------------------------------------------------
	//
	// GLOBAL PERMISSION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String GLOBAL_PERMISSION_TABLE = "global_permission";
	public static final String GLOBAL_PERMISSION_COLUMN_ID = "id_global_permission";
	public static final String GLOBAL_PERMISSION_COLUMN_PERMISSION = "permission";

	// --------------------------------------------------------------------------------
	//
	// HISTORY TOKEN ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String HISTORY_TOKEN_TABLE = "history_token";
	public static final String HISTORY_TOKEN_COLUMN_ID = "id_history_token";
	public static final String HISTORY_TOKEN_COLUMN_TYPE = "change_type";
	public static final String HISTORY_TOKEN_COLUMN_VALUE = "value";
	public static final String HISTORY_TOKEN_COLUMN_DATE = "history_date";
	// TODO Replace with 'flexible_element' id colunmn
	public static final String HISTORY_TOKEN_COLUMN_ELEMENT_ID = "id_element";
	// TODO Replace with 'UserLogin' id column.
	public static final String HISTORY_TOKEN_COLUMN_USER_ID = "id_user";
	public static final String HISTORY_TOKEN_COLUMN_COMMENT = "comment";
	public static final String HISTORY_TOKEN_COLUMN_CORE_VERSION = "core_version";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION SCHEME ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String IMPORTATION_SCHEME_TABLE = "importation_scheme";
	public static final String IMPORTATION_SCHEME_COLUMN_ID = "sch_id";
	public static final String IMPORTATION_SCHEME_COLUMN_NAME = "sch_name";
	public static final String IMPORTATION_SCHEME_COLUMN_FILE_FORMAT = "sch_file_format";
	public static final String IMPORTATION_SCHEME_COLUMN_IMPORT_TYPE = "sch_import_type";
	public static final String IMPORTATION_SCHEME_COLUMN_FIRST_ROW = "sch_first_row";
	public static final String IMPORTATION_SCHEME_COLUMN_SHEET_NAME = "sch_sheet_name";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION SCHEME MODEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String IMPORTATION_SCHEME_MODEL_TABLE = "importation_scheme_model";
	public static final String IMPORTATION_SCHEME_MODEL_COLUMN_ID = "sch_mod_id";
	public static final String IMPORTATION_SCHEME_MODEL_COLUMN_DATE_DELETED = "datedeleted";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION SCHEME VARIABLE BUDGET ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String VARIABLE_BUDGET_ELEMENT_TABLE = "importation_scheme_variable_budget_element";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION SCHEME VARIABLE BUDGET SUB FIELD ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String VARIABLE_BUDGET_SUB_FIELD_TABLE = "importation_variable_budget_sub_field";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION SCHEME VARIABLE FLEXIBLE ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String VARIABLE_FLEXIBLE_ELEMENT_TABLE = "importation_scheme_variable_flexible_element";
	public static final String VARIABLE_FLEXIBLE_ELEMENT_COLUMN_ID = "var_fle_id";
	public static final String VARIABLE_FLEXIBLE_ELEMENT_COLUMN_DATE_DELETED = "datedeleted";
	public static final String VARIABLE_FLEXIBLE_ELEMENT_COLUMN_IS_KEY = "var_fle_is_key";

	// --------------------------------------------------------------------------------
	//
	// IMPORTATION VARIABLE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String IMPORTATION_VARIABLE_TABLE = "importation_scheme_variable";
	public static final String IMPORTATION_VARIABLE_COLUMN_ID = "var_id";
	public static final String IMPORTATION_VARIABLE_COLUMN_NAME = "var_name";
	public static final String IMPORTATION_VARIABLE_COLUMN_REFERENCE = "var_reference";

	// --------------------------------------------------------------------------------
	//
	// INDICATOR ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String INDICATOR_TABLE = "Indicator";
	public static final String INDICATOR_COLUMN_ID = "IndicatorId";
	public static final String INDICATOR_COLUMN_NAME = "Name";
	public static final String INDICATOR_COLUMN_UNITS = "Units";
	public static final String INDICATOR_COLUMN_OBJECTIVE = "Objective";
	public static final String INDICATOR_COLUMN_DESCRIPTION = "description";
	public static final String INDICATOR_COLUMN_CATEGORY = "Category";
	public static final String INDICATOR_COLUMN_COLLECT_INTERVENTION = "CollectIntervention";
	public static final String INDICATOR_COLUMN_COLLECT_MONITORING = "collectMonitoring";
	public static final String INDICATOR_COLUMN_AGGREGATION = "Aggregation";
	public static final String INDICATOR_COLUMN_CODE = "ListHeader";
	public static final String INDICATOR_COLUMN_SOURCE_OF_VERIFICATION = "sourceOfVerification";
	public static final String INDICATOR_COLUMN_DIRECT_DATA_ENTRY_ENABLED = "directDataEntryEnabled";

	public static final String INDICATOR_COLUMN_DEFINITION_DIRECT_DATA_ENTRY_ENABLED = "BOOLEAN NOT NULL DEFAULT TRUE";

	public static final int INDICATOR_NAME_MAX_LENGTH = 1024;
	public static final int INDICATOR_UNITS_MAX_LENGTH = 15;
	public static final int INDICATOR_OBJECTIVE_PRECISION = 15;
	public static final int INDICATOR_OBJECTIVE_SCALE = 0;
	public static final int INDICATOR_CATEGORY_MAX_LENGTH = 1024;
	public static final int INDICATOR_CODE_MAX_LENGTH = 30;

	public static final String INDICATOR_SELF_LINK_TABLE = "indicator_datasource";
	public static final String INDICATOR_SELF_LINK_COLUMN = "IndicatorSourceId";

	// --------------------------------------------------------------------------------
	//
	// INDICATORS LIST ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String INDICATORS_LIST_ELEMENT_TABLE = "indicators_list_element";

	// --------------------------------------------------------------------------------
	//
	// INDICATORS LIST VALUE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String INDICATORS_LIST_VALUE_TABLE = "indicators_list_value";
	public static final String INDICATORS_LIST_VALUE_COLUMN_ID_INDICATOR = "id_indicator";
	public static final String INDICATORS_LIST_VALUE_COLUMN_ID_INDICATORS_LIST = "id_indicators_list";

	// --------------------------------------------------------------------------------
	//
	// INDICATOR VALUE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String INDICATOR_VALUE_TABLE = "IndicatorValue";
	public static final String INDICATOR_VALUE_COLUMN_VALUE = "Value";

	public static final int INDICATOR_VALUE_VALUE_PRECISION = 15;
	public static final int INDICATOR_VALUE_VALUE_SCALE = 0;

	// --------------------------------------------------------------------------------
	//
	// KEY QUESTION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String KEY_QUESTION_TABLE = "KeyQuestion";
	public static final String KEY_QUESTION_COLUMN_ID = "id";
	public static final String KEY_QUESTION_COLUMN_SORT_ORDER = "sort_order";
	public static final String KEY_QUESTION_COLUMN_LABEL = "label";
	// TODO Replace with 'project report model section' table id colunmn
	public static final String KEY_QUESTION_COLUMN_SECTION_ID = "sectionid";
	// TODO Replace with 'quality criterion' table id colunmn
	public static final String KEY_QUESTION_COLUMN_QUALITY_CRITERION_ID = "qualitycriterion_id_quality_criterion";

	public static final int KEY_QUESTION_LABEL_MAX_LENGTH = 255;

	// --------------------------------------------------------------------------------
	//
	// LAYOUT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LAYOUT_TABLE = "layout";
	public static final String LAYOUT_COLUMN_ID = "id_layout";
	public static final String LAYOUT_COLUMN_ROWS_COUNT = "rows_count";
	public static final String LAYOUT_COLUMN_COLUMNS_COUT = "columns_count";

	// --------------------------------------------------------------------------------
	//
	// LAYOUT CONSTRAINT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LAYOUT_CONSTRAINT_TABLE = "layout_constraint";
	public static final String LAYOUT_CONSTRAINT_COLUMN_ID = "id_layout_constraint";
	// TODO Replace with common 'SortOrder' column.
	public static final String LAYOUT_CONSTRAINT_COLUMN_SORT_ORDER = "sort_order";

	// --------------------------------------------------------------------------------
	//
	// LAYOUT GROUP ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LAYOUT_GROUP_TABLE = "layout_group";
	public static final String LAYOUT_GROUP_COLUMN_ID = "id_layout_group";
	public static final String LAYOUT_GROUP_COLUMN_ROW_INDEX = "row_index";
	public static final String LAYOUT_GROUP_COLUMN_COLUMN_INDEX = "column_index";
	public static final String LAYOUT_GROUP_COLUMN_TITLE = "title";
	public static final String LAYOUT_GROUP_COLUMN_HAS_ITERATIONS = "has_iterations";

	public static final int LAYOUT_GROUP_TITLE_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// LAYOUT GROUP ITERATION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LAYOUT_GROUP_ITERATION_TABLE = "layout_group_iteration";
	public static final String LAYOUT_GROUP_ITERATION_COLUMN_ID = "id_layout_group_iteration";
	public static final String LAYOUT_GROUP_ITERATION_COLUMN_ID_CONTAINER = "id_container";
	public static final String LAYOUT_GROUP_ITERATION_COLUMN_NAME = "name";


	// --------------------------------------------------------------------------------
	//
	// LOCATION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOCATION_TABLE = "Location";
	public static final String LOCATION_COLUMN_ID = "LocationID";
	public static final String LOCATION_COLUMN_NAME = "Name";
	public static final String LOCATION_COLUMN_X = "X";
	public static final String LOCATION_COLUMN_Y = "Y";
	public static final String LOCATION_COLUMN_AXE = "Axe";
	public static final String LOCATION_COLUMN_LOCATION_GUID = "LocationGuid";

	public static final String LOCATION_ADMIN_ENTITY_LINK_TABLE = "LocationAdminLink";

	public static final int LOCATION_GUID_MAX_LENGTH = 36;
	public static final int LOCATION_COORDINATE_PRECISION = 7;
	public static final int LOCATION_COORDINATE_SCALE = 0;

	// --------------------------------------------------------------------------------
	//
	// LOCATION TYPE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOCATION_TYPE_TABLE = "LocationType";
	public static final String LOCATION_TYPE_COLUMN_ID = "LocationTypeId";
	public static final String LOCATION_TYPE_COLUMN_REUSE = "Reuse";
	public static final String LOCATION_TYPE_COLUMN_NAME = "Name";
	// TODO Replace with 'AdminLevel' id column.
	public static final String LOCATION_TYPE_COLUMN_ADMIN_LEVEL = "BoundAdminLevelId";

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOGFRAME_TABLE = "log_frame";
	public static final String LOGFRAME_COLUMN_ID = "id_log_frame";
	public static final String LOGFRAME_COLUMN_MAIN_OBJECTIVE = "main_objective";
	// TODO Replace with 'LogFrameModel' id column.
	public static final String LOGFRAME_COLUMN_LOGFRAME_MODEL_ID = "id_log_frame_model";

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME ACTIVITY ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOGFRAME_ACTIVITY_TABLE = "log_frame_activity";
	public static final String LOGFRAME_ACTIVITY_COLUMN_TITLE = "title";
	public static final String LOGFRAME_ACTIVITY_COLUMN_START_DATE = "startDate";
	public static final String LOGFRAME_ACTIVITY_COLUMN_END_DATE = "endDate";
	public static final String LOGFRAME_ACTIVITY_COLUMN_ADVANCEMENT = "advancement";
	// TODO Replace with 'LogframeElement' id column.
	public static final String LOGFRAME_ACTIVITY_COLUMN_EXPECTED_RESULT_ID = "id_result";

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOGFRAME_ELEMENT_TABLE = "log_frame_element";
	public static final String LOGFRAME_ELEMENT_COLUMN_ID = "id_element";
	public static final String LOGFRAME_ELEMENT_COLUMN_CODE = "code";
	public static final String LOGFRAME_ELEMENT_COLUMN_POSITION = "position";
	public static final String LOGFRAME_ELEMENT_COLUMN_RISKS_ASSUMPTIONS = "risksAndAssumptions";

	public static final String LOGFRAME_ELEMENT_INDICATOR_LINK_TABLE = "log_frame_indicators";

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME EXPECTED RESULT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOGFRAME_EXPECTED_RESULT_TABLE = "log_frame_expected_result";
	public static final String LOGFRAME_EXPECTED_RESULT_COLUMN_INTERVENTION_LOGIC = "intervention_logic";
	// TODO Replace with 'LogframeElement' id column.
	public static final String LOGFRAME_EXPECTED_RESULT_COLUMN_SPECIFIC_OBJ_ID = "id_specific_objective";

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME GROUP ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOGFRAME_GROUP_TABLE = "log_frame_group";
	public static final String LOGFRAME_GROUP_COLUMN_ID = "id_group";
	public static final String LOGFRAME_GROUP_COLUMN_TYPE = "type";
	public static final String LOGFRAME_GROUP_COLUMN_LABEL = "label";

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME MODEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOGFRAME_MODEL_TABLE = "log_frame_model";
	public static final String LOGFRAME_MODEL_COLUMN_ID = "id_log_frame";
	public static final String LOGFRAME_MODEL_COLUMN_NAME = "name";
	public static final String LOGFRAME_MODEL_COLUMN_SO_ENABLE_GROUPS = "so_enable_groups";
	public static final String LOGFRAME_MODEL_COLUMN_SO_MAX = "so_max";
	public static final String LOGFRAME_MODEL_COLUMN_SO_GP_MAX = "so_gp_max";
	public static final String LOGFRAME_MODEL_COLUMN_SO_PER_GP_MAX = "so_per_gp_max";
	public static final String LOGFRAME_MODEL_COLUMN_ER_ENABLE_GROUPS = "er_enable_groups";
	public static final String LOGFRAME_MODEL_COLUMN_ER_MAX = "er_max";
	public static final String LOGFRAME_MODEL_COLUMN_ER_GP_MAX = "er_gp_max";
	public static final String LOGFRAME_MODEL_COLUMN_ER_PER_GP_MAX = "er_per_gp_max";
	public static final String LOGFRAME_MODEL_COLUMN_ER_PER_SO_MAX = "er_per_so_max";
	public static final String LOGFRAME_MODEL_COLUMN_A_ENABLE_GROUPS = "a_enable_groups";
	public static final String LOGFRAME_MODEL_COLUMN_A_MAX = "a_max";
	public static final String LOGFRAME_MODEL_COLUMN_A_GP_MAX = "a_gp_max";
	public static final String LOGFRAME_MODEL_COLUMN_A_PER_GP_MAX = "a_per_gp_max";
	public static final String LOGFRAME_MODEL_COLUMN_A_PER_ER_MAX = "a_per_er_max";
	public static final String LOGFRAME_MODEL_COLUMN_P_ENABLE_GROUPS = "p_enable_groups";
	public static final String LOGFRAME_MODEL_COLUMN_P_MAX = "p_max";
	public static final String LOGFRAME_MODEL_COLUMN_P_GP_MAX = "p_gp_max";
	public static final String LOGFRAME_MODEL_COLUMN_P_PER_GP_MAX = "p_per_gp_max";

	public static final int LOGFRAME_MODEL_NAME_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME PREREQUISITE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOGFRAME_PREREQUISITE_TABLE = "log_frame_prerequisite";
	public static final String LOGFRAME_PREREQUISITE_COLUMN_ID = "id_prerequisite";
	public static final String LOGFRAME_PREREQUISITE_COLUMN_CODE = "code";
	public static final String LOGFRAME_PREREQUISITE_COLUMN_CONTENT = "content";
	public static final String LOGFRAME_PREREQUISITE_COLUMN_POSITION = "position";

	// --------------------------------------------------------------------------------
	//
	// LOGFRAME SPECIFIC OBJECTIVE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String LOGFRAME_SPECIFIC_OBJ_TABLE = "log_frame_specific_objective";
	public static final String LOGFRAME_SPECIFIC_OBJ_COLUMN_INTERVENTION_LOGIC = "intervention_logic";

	// --------------------------------------------------------------------------------
	//
	// MESSAGE ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String MESSAGE_ELEMENT_TABLE = "message_element";

	// --------------------------------------------------------------------------------
	//
	// MONITORED POINT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String MONITORED_POINT_TABLE = "monitored_point";
	public static final String MONITORED_POINT_COLUMN_ID = "id_monitored_point";
	public static final String MONITORED_POINT_COLUMN_LABEL = "label";
	public static final String MONITORED_POINT_COLUMN_EXPECTED_DATE = "expected_date";
	public static final String MONITORED_POINT_COLUMN_COMPLETION_DATE = "completion_date";
	public static final String MONITORED_POINT_COLUMN_DELETED = "deleted";

	public static final int MONITORED_POINT_LABEL_MAX_LENGTH = 8192;

	// TODO Replace with 'MonitoredPointList' id column.
	public static final String MONITORED_POINT_COLUMN_MONITORED_POINT_LIST_ID = "id_list";

	// --------------------------------------------------------------------------------
	//
	// MONITORED POINT LIST ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String MONITORED_POINT_LIST_TABLE = "monitored_point_list";
	public static final String MONITORED_POINT_LIST_COLUMN_ID = "id_monitored_point_list";

	// --------------------------------------------------------------------------------
	//
	// MONITORED POINT HISTORY ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String MONITORED_POINT_HISTORY_TABLE = "monitored_point_history";
	public static final String MONITORED_POINT_HISTORY_COLUMN_ID = "id_monitored_point_history";
	public static final String MONITORED_POINT_HISTORY_COLUMN_TYPE = "change_type";
	public static final String MONITORED_POINT_HISTORY_COLUMN_GENERATED_DATE = "generated_date";
	public static final String MONITORED_POINT_HISTORY_COLUMN_VALUE = "value";
	// TODO Replace with 'UserLogin' id column.
	public static final String MONITORED_POINT_HISTORY_COLUMN_USER_ID = "id_user";

	// --------------------------------------------------------------------------------
	//
	// ORGANIZATION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ORGANIZATION_TABLE = "organization";
	public static final String ORGANIZATION_COLUMN_ID = "id_organization";
	public static final String ORGANIZATION_COLUMN_NAME = "name";
	public static final String ORGANIZATION_COLUMN_LOGO = "logo";
	// TODO Replace with 'OrgUnit' id column.
	public static final String ORGANIZATION_COLUMN_ROOT_ORG_UNIT = "id_root_org_unit";

	// --------------------------------------------------------------------------------
	//
	// ORG UNIT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ORG_UNIT_TABLE = "Partner";
	public static final String ORG_UNIT_COLUMN_ID = "PartnerId";
	public static final String ORG_UNIT_COLUMN_NAME = "Name";
	public static final String ORG_UNIT_COLUMN_FULL_NAME = "FullName";
	public static final String ORG_UNIT_COLUMN_CALENDAR_ID = "calendarId";
	public static final String ORG_UNIT_COLUMN_DELETED = "deleted";
	// TODO Replace with 'Location' id column.
	public static final String ORG_UNIT_COLUMN_LOCATION = "location_LocationId";
	public static final String ORG_UNIT_COLUMN_PARENT = "parent_PartnerId";
	// TODO Replace with 'Organization' id column.
	public static final String ORG_UNIT_COLUMN_ORGANIZATION = "organization_id_organization";
	// TODO Replace with 'OrgUnitModel' id column.
	public static final String ORG_UNIT_COLUMN_ORG_UNIT_MODEL = "id_org_unit_model";
	// TODO Replace with 'Country' id column.
	public static final String ORG_UNIT_COLUMN_COUNTRY = "office_country_id";

	public static final String ORG_UNIT_USER_DATABASE_LINK_TABLE = "PartnerInDatabase";

	// --------------------------------------------------------------------------------
	//
	// ORG UNIT BANNER ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ORG_UNIT_BANNER_TABLE = "org_unit_banner";
	public static final String ORG_UNIT_BANNER_COLUMN_ID = "banner_id";
	// TODO Replace with 'OrgUnitModel' id column.
	public static final String ORG_UNIT_BANNER_COLUMN_ORG_UNIT_MODEL = "id_org_unit_model";

	// --------------------------------------------------------------------------------
	//
	// ORG UNIT DETAILS ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ORG_UNIT_DETAILS_TABLE = "org_unit_details";
	public static final String ORG_UNIT_DETAILS_COLUMN_ID = "details_id";
	// TODO Replace with 'OrgUnitModel' id column.
	public static final String ORG_UNIT_DETAILS_COLUMN_ORG_UNIT_MODEL = "id_org_unit_model";

	// --------------------------------------------------------------------------------
	//
	// ORG UNIT MODEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ORG_UNIT_MODEL_TABLE = "org_unit_model";
	public static final String ORG_UNIT_MODEL_COLUMN_ID = "org_unit_model_id";
	public static final String ORG_UNIT_MODEL_COLUMN_NAME = "name";
	public static final String ORG_UNIT_MODEL_COLUMN_HAS_BUDGET = "has_budget";
	public static final String ORG_UNIT_MODEL_COLUMN_TITLE = "title";
	public static final String ORG_UNIT_MODEL_COLUMN_CAN_CONTAIN_PROJECTS = "can_contain_projects";
	public static final String ORG_UNIT_MODEL_COLUMN_STATUS = "status";
	// TODO Replace with common 'dateDeleted' column.
	public static final String ORG_UNIT_MODEL_COLUMN_DATE_DELETED = "date_deleted";

	public static final int ORG_UNIT_MODEL_NAME_MAX_LENGTH = 8192;
	public static final int ORG_UNIT_MODEL_TITLE_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// ORG UNIT PERMISSION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ORG_UNIT_PERMISSION_TABLE = "OrgUnitPermission";
	public static final String ORG_UNIT_PERMISSION_COLUMN_ID = "id";
	public static final String ORG_UNIT_PERMISSION_COLUMN_VIEW_ALL = "viewAll";
	public static final String ORG_UNIT_PERMISSION_COLUMN_EDIT_ALL = "editall";
	// TODO Replace with 'OrgUnit' id column.
	public static final String ORG_UNIT_PERMISSION_COLUMN_ORG_UNIT = "unit_Id";
	// TODO Replace with 'User' id column.
	public static final String ORG_UNIT_PERMISSION_COLUMN_USER = "user_userid";

	// --------------------------------------------------------------------------------
	//
	// ORG UNIT PROFILE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String ORG_UNIT_PROFILE_TABLE = "user_unit";
	public static final String ORG_UNIT_PROFILE_COLUMN_ID = "id_user_unit";
	public static final String ORG_UNIT_PROFILE_COLUMN_NAME = "name";
	// TODO Replace with 'User' id column.
	public static final String ORG_UNIT_PROFILE_COLUMN_USER = "id_user";
	// TODO Replace with 'User' id column.
	public static final String ORG_UNIT_PROFILE_COLUMN_ORG_UNIT = "id_org_unit";

	public static final String ORG_UNIT_PROFILE_PROFILE_LINK_TABLE = "user_unit_profiles";
	public static final String ORG_UNIT_PROFILE_COLUMN_TYPE = "user_unit_type";

	// --------------------------------------------------------------------------------
	//
	// PERSONNAL CALENDAR ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PERSONAL_CALENDAR_TABLE = "personalcalendar";
	public static final String PERSONAL_CALENDAR_COLUMN_ID = "id";
	public static final String PERSONAL_CALENDAR_COLUMN_NAME = "name";

	// --------------------------------------------------------------------------------
	//
	// PERSONNAL EVENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PERSONAL_EVENT_TABLE = "personalevent";
	public static final String PERSONAL_EVENT_COLUMN_ID = "id";
	public static final String PERSONAL_EVENT_COLUMN_CALENDAR_ID = "calendarid";
	public static final String PERSONAL_EVENT_COLUMN_DATE_CREATED = "datecreated";
	public static final String PERSONAL_EVENT_COLUMN_DATE_DELETED = "datedeleted";
	public static final String PERSONAL_EVENT_COLUMN_DESCRIPTION = "description";
	public static final String PERSONAL_EVENT_COLUMN_END_DATE = "enddate";
	public static final String PERSONAL_EVENT_COLUMN_START_DATE = "startdate";
	public static final String PERSONAL_EVENT_COLUMN_SUMMARY = "summary";

	public static final int PERSONAL_EVENT_DESCRIPTION_MAX_LENGTH = 255;
	public static final int PERSONAL_EVENT_SUMMARY_MAX_LENGTH = 255;

	// --------------------------------------------------------------------------------
	//
	// PHASE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PHASE_TABLE = "phase";
	public static final String PHASE_COLUMN_ID = "id_phase";
	public static final String PHASE_COLUMN_START_DATE = "start_date";
	public static final String PHASE_COLUMN_END_DATE = "end_date";

	// --------------------------------------------------------------------------------
	//
	// PHASE MODEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PHASE_MODEL_TABLE = "phase_model";
	public static final String PHASE_MODEL_COLUMN_ID = "id_phase_model";
	public static final String PHASE_MODEL_COLUMN_NAME = "name";
	public static final String PHASE_MODEL_COLUMN_DISPLAY_ORDER = "display_order";
	public static final String PHASE_MODEL_COLUMN_GUIDE = "guide";

	public static final String PHASE_MODEL_SELF_LINK_TABLE = "phase_model_sucessors";
	public static final String PHASE_MODEL_SELF_LINK_COLUMN = "id_phase_model_successor";

	// TODO Replace with 'PhaseModelDefinition' id column.
	public static final String PHASE_MODEL_COLUMN_DEFINITION_ID = "definition_id";

	// --------------------------------------------------------------------------------
	//
	// PHASE MODEL DEFINITION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PHASE_MODEL_DEFINITION_TABLE = "phase_model_definition";
	public static final String PHASE_MODEL_DEFINITION_COLUMN_ID = "id_phase_model_definition";

	// --------------------------------------------------------------------------------
	//
	// PRIVACY GROUP ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PRIVACY_GROUP_TABLE = "privacy_group";
	public static final String PRIVACY_GROUP_COLUMN_ID = "id_privacy_group";
	public static final String PRIVACY_GROUP_COLUMN_CODE = "code";
	public static final String PRIVACY_GROUP_COLUMN_TITLE = "title";

	public static final int PRIVACY_GROUP_TITLE_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// PRIVACY GROUP PERMISSION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PRIVACY_GROUP_PERMISSION_TABLE = "privacy_group_permission";
	public static final String PRIVACY_GROUP_PERMISSION_COLUMN_ID = "id_permission";
	public static final String PRIVACY_GROUP_PERMISSION_COLUMN_PERMISSION = "permission";

	// --------------------------------------------------------------------------------
	//
	// PROFILE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROFILE_TABLE = "profile";
	public static final String PROFILE_COLUMN_ID = "id_profile";
	public static final String PROFILE_COLUMN_NAME = "name";

	public static final int PROFILE_NAME_MAX_LENGTH = 8196;

	// --------------------------------------------------------------------------------
	//
	// PROJECT ENTITY.
	//
	// --------------------------------------------------------------------------------

	// TODO Fake project column id often used in related tables.
	public static final String PROJECT_COLUMN_ID = "id_project";

	public static final String PROJECT_TABLE = "Project";
	public static final String PROJECT_COLUMN_CALENDAR_ID = "calendarId";
	public static final String PROJECT_COLUMN_END_DATE = "end_date";
	public static final String PROJECT_COLUMN_CLOSE_DATE = "close_date";
	public static final String PROJECT_COLUMN_AMENDMENT_VERSION = "amendment_version";
	public static final String PROJECT_COLUMN_AMENDMENT_REVISION = "amendment_revision";
	public static final String PROJECT_COLUMN_ACTIVITY_ADVANCEMENT = "activity_advancement";
	public static final String PROJECT_COLUMN_AMENDMENT_STATUS = "amendment_status";

	// TODO Replace with 'User' id column.
	public static final String PROJECT_COLUMN_USER_MANAGER_ID = "id_manager";
	// TODO Replace with 'Phase' id column.
	public static final String PROJECT_COLUMN_CURRENT_PHASE_ID = "id_current_phase";
	// TODO Replace with 'MonitoredPointList' id column (without 's' on pointS).
	public static final String PROJECT_COLUMN_MONITORED_POINT_LIST_ID = "id_monitored_points_list";

	public static final String PROJECT_COLUMN_USER_LINK_TABLE = "project_userlogin";

	public static final String PROJECT_COLUMN_TEAM_MEMBERS_LINK_TABLE = "project_team_members";

	public static final String PROJECT_COLUMN_TEAM_MEMBER_PROFILES_LINK_TABLE = "project_team_member_profiles";

	// --------------------------------------------------------------------------------
	//
	// PROJECT BANNER ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_BANNER_TABLE = "project_banner";
	public static final String PROJECT_BANNER_COLUMN_ID = "id";

	// --------------------------------------------------------------------------------
	//
	// PROJECT DETAILS ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_DETAILS_TABLE = "project_details";
	public static final String PROJECT_DETAILS_COLUMN_ID = "id";

	// --------------------------------------------------------------------------------
	//
	// PROJECT FUNDING ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_FUNDING_TABLE = "project_funding";
	public static final String PROJECT_FUNDING_COLUMN_ID = "id_funding";
	public static final String PROJECT_FUNDING_COLUMN_PERCENTAGE = "percentage";
	public static final String PROJECT_FUNDING_COLUMN_PROJECT_FUNDING = "id_project_funding";
	public static final String PROJECT_FUNDING_COLUMN_PROJECT_FUNDED = "id_project_funded";

	// --------------------------------------------------------------------------------
	//
	// PROJECT MODEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_MODEL_TABLE = "project_model";
	public static final String PROJECT_MODEL_COLUMN_ID = "id_project_model";
	public static final String PROJECT_MODEL_COLUMN_NAME = "name";
	public static final String PROJECT_MODEL_COLUMN_STATUS = "status";
	// TODO Replace with common 'dateDeleted' column.
	public static final String PROJECT_MODEL_COLUMN_DATE_DELETED = "date_deleted";
	public static final String PROJECT_MODEL_COLUMN_DATE_MAINTENANCE = "date_maintenance";

	// TODO Replace with 'PhaseModel' id column.
	public static final String PROJECT_MODEL_COLUMN_PHASE_MODEL_ID = "id_root_phase_model";
	public static final String PROJECT_MODEL_COLUMN_DEFAULT_TEAM_MEMBER_PROFILES_LINK_TABLE = "project_model_default_team_member_profiles";

	public static final int PROJECT_MODEL_NAME_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// PROJECT MODEL VISIBILITY ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_MODEL_VISIBILITY_TABLE = "project_model_visibility";
	public static final String PROJECT_MODEL_VISIBILITY_COLUMN_ID = "id_visibility";
	public static final String PROJECT_MODEL_VISIBILITY_COLUMN_TYPE = "type";

	// --------------------------------------------------------------------------------
	//
	// PROJECT REPORT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_REPORT_TABLE = "projectreport";
	public static final String PROJECT_REPORT_COLUMN_ID = "id";
	public static final String PROJECT_REPORT_COLUMN_DATE_DELETED = "datedeleted";
	public static final String PROJECT_REPORT_COLUMN_NAME = "name";
	// TODO Replace with 'projectreportversion' id column
	public static final String PROJECT_REPORT_COLUMN_CURRENT_VERSION_ID = "currentversion_id";
	// TODO Replace with 'flexible_element' id column
	public static final String PROJECT_REPORT_COLUMN_CURRENT_FLEXIBLE_ELEMENT_ID = "flexibleelement_id_flexible_element";
	// TODO Replace with 'projectreportmodel' id column
	public static final String PROJECT_REPORT_COLUMN_CURRENT_MODEL_ID = "model_id";
	// TODO Replace with 'partner' id column
	public static final String PROJECT_REPORT_COLUMN_CURRENT_ORG_UNIT_PARTNER_ID = "orgunit_partnerid";
	// TODO Replace with 'project' id column
	public static final String PROJECT_REPORT_COLUMN_CURRENT_PROJECT_ID = "project_databaseid";

	public static final int PROJECT_REPORT_NAME_MAX_LENGTH = 255;

	// --------------------------------------------------------------------------------
	//
	// PROJECT REPORT MODEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_REPORT_MODEL_TABLE = "projectreportmodel";
	public static final String PROJECT_REPORT_MODEL_COLUMN_ID = "id";
	public static final String PROJECT_REPORT_MODEL_COLUMN_NAME = "name";

	public static final int PROJECT_REPORT_MODEL_NAME_MAX_LENGTH = 255;

	// --------------------------------------------------------------------------------
	//
	// PROJECT REPORT MODEL SECTION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_REPORT_MODEL_SECTION_TABLE = "projectreportmodelsection";
	public static final String PROJECT_REPORT_MODEL_SECTION_COLUMN_ID = "id";
	public static final String PROJECT_REPORT_MODEL_SECTION_COLUMN_SORT_ORDER = "sort_order";
	public static final String PROJECT_REPORT_MODEL_SECTION_COLUMN_NAME = "name";
	public static final String PROJECT_REPORT_MODEL_SECTION_COLUMN_NB_OF_TEXTAREA = "numberoftextarea";
	// TODO replace with 'projectreportmodelsection' id column
	public static final String PROJECT_REPORT_MODEL_SECTION_COLUMN_PARENT_SECTION_MODEL_ID = "parentsectionmodelid";
	// TODO replace with 'projectreportmodel' id column
	public static final String PROJECT_REPORT_MODEL_SECTION_COLUMN_PROJECT_MODEL_ID = "projectmodelid";

	public static final int PROJECT_REPORT_MODEL_SECTION_NAME_MAX_LENGTH = 255;

	// --------------------------------------------------------------------------------
	//
	// PROJECT REPORT VERSION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String PROJECT_REPORT_VERSION_TABLE = "projectreportversion";
	public static final String PROJECT_REPORT_VERSION_COLUMN_ID = "id";
	public static final String PROJECT_REPORT_VERSION_COLUMN_EDIT_DATE = "editdate";
	public static final String PROJECT_REPORT_VERSION_COLUMN_PHASE_NAME = "phasename";
	public static final String PROJECT_REPORT_VERSION_COLUMN_VERSION = "version";
	// TODO replace with 'userlogin' id column
	public static final String PROJECT_REPORT_VERSION_COLUMN_USER_ID = "editor_userid";
	// TODO replace with 'projectreport' id column
	public static final String PROJECT_REPORT_VERSION_COLUMN_REPORT_ID = "report_id";

	public static final int PROJECT_REPORT_VERSION_PHASE_NAME_MAX_LENGTH = 255;

	// --------------------------------------------------------------------------------
	//
	// QUALITY CRITERION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String QUALITY_CRITERION_TABLE = "quality_criterion";
	public static final String QUALITY_CRITERION_COLUMN_ID = "id_quality_criterion";
	public static final String QUALITY_CRITERION_COLUMN_CODE = "code";
	public static final String QUALITY_CRITERION_COLUMN_LABEL = "label";

	public static final int QUALITY_CRITERION_CODE_MAX_LENGTH = 8192;

	public static final String QUALITY_CRITERION_SELF_LINK_TABLE = "quality_criterion_children";
	public static final String QUALITY_CRITERION_SELF_LINK_COLUMN = "id_quality_criterion_child";

	// --------------------------------------------------------------------------------
	//
	// QUALITY FRAMEWORK ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String QUALITY_FRAMEWORK_TABLE = "quality_framework";
	public static final String QUALITY_FRAMEWORK_COLUMN_ID = "id_quality_framework";
	public static final String QUALITY_FRAMEWORK_COLUMN_LABEL = "label";

	public static final int QUALITY_FRAMEWORK_LABEL_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// QUESTION CHOICE ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String QUESTION_CHOICE_ELEMENT_TABLE = "question_choice_element";
	public static final String QUESTION_CHOICE_ELEMENT_COLUMN_ID = "id_choice";
	public static final String QUESTION_CHOICE_ELEMENT_COLUMN_LABEL = "label";
	// TODO Replace with 'id_flexible_element' column
	public static final String QUESTION_CHOICE_ELEMENT_COLUMN_ID_QUESTION = "id_question";
	// TODO Replace with 'id_category_element' column
	public static final String QUESTION_CHOICE_ELEMENT_COLUMN_ID_CATEGORY_ELEMENT = "id_category_element";
	// TODO Replace with common 'SortOrder' column
	public static final String QUESTION_CHOICE_ELEMENT_COLUMN_SORT_ORDER = "sort_order";
	public static final String QUESTION_CHOICE_ELEMENT_COLUMN_DISABLED = "is_disabled";

	public static final int QUESTION_CHOICE_ELEMENT_LABEL_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// QUESTION ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String QUESTION_ELEMENT_TABLE = "question_element";
	public static final String QUESTION_ELEMENT_COLUMN_IS_MULTIPLE = "is_multiple";
	// TODO Replace with 'id_quality_criterion' column
	public static final String QUESTION_ELEMENT_COLUMN_ID_QUALITY_CRITERION = "id_quality_criterion";
	// TODO Replace with 'id_category_type' column
	public static final String QUESTION_ELEMENT_COLUMN_ID_CATEGORY_TYPE = "id_category_type";

	// --------------------------------------------------------------------------------
	//
	// REMINDER ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String REMINDER_TABLE = "reminder";
	public static final String REMINDER_COLUMN_ID = "id_reminder";
	public static final String REMINDER_COLUMN_LABEL = "label";
	public static final String REMINDER_COLUMN_EXPECTED_DATE = "expected_date";
	public static final String REMINDER_COLUMN_COMPLETION_DATE = "completion_date";
	public static final String REMINDER_COLUMN_DELETED = "deleted";
	// TODO Replace with 'ReminderList' id column
	public static final String REMINDER_COLUMN_REMINDER_LIST_ID = "id_list";

	public static final int REMINDER_LABEL_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// REMINDER LIST ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String REMINDER_LIST_TABLE = "reminder_list";
	public static final String REMINDER_LIST_COLUMN_ID = "id_reminder_list";

	// --------------------------------------------------------------------------------
	//
	// REMINDER HISTORY ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String REMINDER_HISTORY_TABLE = "reminder_history";
	public static final String REMINDER_HISTORY_COLUMN_ID = "id_reminder_history";
	public static final String REMINDER_HISTORY_COLUMN_DATE = "generated_date";
	public static final String REMINDER_HISTORY_COLUMN_TYPE = "change_type";
	public static final String REMINDER_HISTORY_COLUMN_VALUE = "value";
	// TODO Replace with 'UserLogin' id column.
	public static final String REMINDER_HISTORY_COLUMN_USER_ID = "id_user";

	// --------------------------------------------------------------------------------
	//
	// REPORT ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String REPORT_ELEMENT_TABLE = "report_element";
	// TODO Replace with 'projectreportmodel' id column
	public static final String REPORT_ELEMENT_COLUMN_MODEL_ID = "model_id";

	// --------------------------------------------------------------------------------
	//
	// REPORT DEFINITION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String REPORT_DEFINITION_TABLE = "ReportTemplate";
	public static final String REPORT_DEFINITION_COLUMN_ID = "ReportTemplateId";
	public static final String REPORT_DEFINITION_COLUMN_TITLE = "title";
	public static final String REPORT_DEFINITION_COLUMN_DAY = "day";
	public static final String REPORT_DEFINITION_COLUMN_VISIBILITY = "visibility";
	public static final String REPORT_DEFINITION_COLUMN_FREQUENCY = "frequency";
	public static final String REPORT_DEFINITION_COLUMN_XML = "xml";
	public static final String REPORT_DEFINITION_COLUMN_DESCRIPTION = "description";

	// TODO Replace with 'User' id column.
	public static final String REPORT_DEFINITION_COLUMN_OWNER_USER_ID = "OwnerUserId";

	// --------------------------------------------------------------------------------
	//
	// REPORT LIST ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String REPORT_LIST_ELEMENT_TABLE = "report_list_element";
	// TODO Replace with 'projectreportmodel' id column
	public static final String REPORT_LIST_ELEMENT_COLUMN_MODEL_ID = "model_id";

	// --------------------------------------------------------------------------------
	//
	// REPORT SUBSCRIPTION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String REPORT_SUBSCRIPTION_TABLE = "ReportSubscription";
	public static final String REPORT_SUBSCRIPTION_COLUMN_SUBSCRIBED = "subscribed";
	public static final String REPORT_SUBSCRIPTION_COLUMN_INVITING_USER_ID = "invitingUserId";

	// --------------------------------------------------------------------------------
	//
	// REPORTING PERIOD ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String REPORTING_PERIOD_TABLE = "ReportingPeriod";
	public static final String REPORTING_PERIOD_COLUMN_ID = "ReportingPeriodId";
	public static final String REPORTING_PERIOD_COLUMN_MONITORING = "Monitoring";
	public static final String REPORTING_PERIOD_COLUMN_DATE1 = "Date1";
	public static final String REPORTING_PERIOD_COLUMN_DATE2 = "Date2";
	public static final String REPORTING_PERIOD_COLUMN_COMMENTS = "comments";

	// --------------------------------------------------------------------------------
	//
	// RICH TEXT ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String RICH_TEXT_ELEMENT_TABLE = "richtextelement";
	public static final String RICH_TEXT_ELEMENT_COLUMN_ID = "id";
	public static final String RICH_TEXT_ELEMENT_COLUMN_SORT_ORDER = "sort_order";
	public static final String RICH_TEXT_ELEMENT_COLUMN_SECTION_ID = "sectionid";
	public static final String RICH_TEXT_ELEMENT_COLUMN_TEXT = "text";
	// TODO Replace with 'projectreportversion' id column
	public static final String RICH_TEXT_ELEMENT_COLUMN_VERSION_ID = "version_id";

	// --------------------------------------------------------------------------------
	//
	// SITE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String SITE_TABLE = "Site";
	public static final String SITE_COLUMN_ID = "SiteId";
	public static final String SITE_COLUMN_GUID = "SiteGuid";
	public static final String SITE_COLUMN_STATUS = "Status";
	public static final String SITE_COLUMN_DATE1 = "Date1";
	public static final String SITE_COLUMN_DATE2 = "Date2";
	public static final String SITE_COLUMN_TARGET = "target";
	public static final String SITE_COLUMN_DATE_SYNCHRONIZED = "DateSynchronized";
	public static final String SITE_COLUMN_COMMENTS = "comments";

	public static final String SITE_COLUMN_ASSESSMENT_SITE_ID = "AssessmentSiteId";

	public static final int SITE_GUID_MAX_LENGTH = 36;

	// --------------------------------------------------------------------------------
	//
	// TRIPLETS LIST ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String TRIPLETS_LIST_ELEMENT_TABLE = "triplets_list_element";

	// --------------------------------------------------------------------------------
	//
	// TRIPLETS VALUE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String TRIPLETS_VALUE_TABLE = "triplet_value";
	public static final String TRIPLETS_VALUE_COLUMN_ID = "id_triplet";
	public static final String TRIPLETS_VALUE_COLUMN_NAME = "name";
	public static final String TRIPLETS_VALUE_COLUMN_CODE = "CODE";
	public static final String TRIPLETS_VALUE_COLUMN_DATE_DELETED = "datedeleted";
	public static final String TRIPLETS_VALUE_COLUMN_PERIOD = "period";

	// --------------------------------------------------------------------------------
	//
	// TEXTAREA ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String TEXTAREA_ELEMENT_TABLE = "textarea_element";
	public static final String TEXTAREA_ELEMENT_COLUMN_TYPE = "type";
	public static final String TEXTAREA_ELEMENT_COLUMN_MIN_VALUE = "min_value";
	public static final String TEXTAREA_ELEMENT_COLUMN_MAX_VALUE = "max_value";
	public static final String TEXTAREA_ELEMENT_COLUMN_IS_DECIMAL = "is_decimal";
	public static final String TEXTAREA_ELEMENT_COLUMN_LENGTH = "length";

	// --------------------------------------------------------------------------------
	//
	// USER ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String USER_TABLE = "UserLogin";
	public static final String USER_COLUMN_ID = "UserId";
	public static final String USER_COLUMN_EMAIL = "Email";
	public static final String USER_COLUMN_FIRSTNAME = "FirstName";
	public static final String USER_COLUMN_NAME = "Name";
	public static final String USER_COLUMN_NEW_USER = "NewUser";
	public static final String USER_COLUMN_LOCALE = "Locale";
	public static final String USER_COLUMN_CHANGE_PASSWORD_KEY = "changePasswordKey";
	public static final String USER_COLUMN_PASSWORD = "Password";
	public static final String USER_COLUMN_ACTIVE = "Active";
	public static final String USER_COLUMN_DATE_CHANGE_PASSWORD_KEY_ISSUED = "dateChangePasswordKeyIssued";

	public static final int CHANGE_PASSWORD_KEY_MAX_LENGTH = 34;
	public static final int PASSWORD_MAX_LENGTH = 150;

	// --------------------------------------------------------------------------------
	//
	// USER DATABASE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String USER_DATABASE_TABLE = "UserDatabase";
	public static final String USER_DATABASE_COLUMN_ID = "DatabaseId";
	public static final String USER_DATABASE_COLUMN_START_DATE = "StartDate";
	public static final String USER_DATABASE_COLUMN_FULL_NAME = "FullName";
	public static final String USER_DATABASE_COLUMN_NAME = "Name";
	public static final String USER_DATABASE_COLUMN_LAST_SCHEMA_UPDATE = "lastSchemaUpdate";
	// TODO Replace with 'User' id column.
	public static final String USER_DATABASE_COLUMN_OWNER_USER_ID = "OwnerUserId";

	// --------------------------------------------------------------------------------
	//
	// USER PERMISSION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String USER_PERMISSION_TABLE = "UserPermission";
	public static final String USER_PERMISSION_COLUMN_ID = "UserPermissionId";
	public static final String USER_PERMISSION_COLUMN_ALLOW_VIEW = "AllowView";
	public static final String USER_PERMISSION_COLUMN_ALLOW_VIEW_ALL = "AllowViewAll";
	public static final String USER_PERMISSION_COLUMN_ALLOW_EDIT = "AllowEdit";
	public static final String USER_PERMISSION_COLUMN_ALLOW_EDIT_ALL = "AllowEditAll";
	public static final String USER_PERMISSION_COLUMN_ALLOW_DESIGN = "AllowDesign";
	public static final String USER_PERMISSION_COLUMN_ALLOW_MANAGE_USERS = "allowManageUsers";
	public static final String USER_PERMISSION_COLUMN_ALLOW_MANAGE_ALL_USERS = "allowManageAllUsers";
	public static final String USER_PERMISSION_COLUMN_LAST_SCHEMA_UPDATE = "lastSchemaUpdate";

	// --------------------------------------------------------------------------------
	//
	// VALUE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String VALUE_TABLE = "value";
	public static final String VALUE_COLUMN_ID = "id_value";
	public static final String VALUE_COLUMN_ID_PROJECT = "id_project";
	public static final String VALUE_COLUMN_ID_USER_LAST_MODIF = "id_user_last_modif";
	public static final String VALUE_COLUMN_ACTION_LAST_MODIF = "action_last_modif";
	public static final String VALUE_COLUMN_DATE_LAST_MODIF = "date_last_modif";
	public static final String VALUE_COLUMN_VALUE = "value";
	public static final String VALUE_COLUMN_LAYOUT_GROUP_ITERATION = "id_layout_group_iteration";

	// --------------------------------------------------------------------------------
	//
	// FRAMEWORK ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FRAMEWORK_TABLE = "framework";
	public static final String FRAMEWORK_COLUMN_ID = "id_framework";
	public static final String FRAMEWORK_COLUMN_AVAILABILITY_STATUS = "availability_status";
	public static final String FRAMEWORK_COLUMN_IMPLEMENTATION_STATUS = "implementation_status";
	public static final String FRAMEWORK_COLUMN_LABEL = "label";

	// --------------------------------------------------------------------------------
	//
	// FRAMEWORK FULFILLMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FRAMEWORK_FULFILLMENT_TABLE = "framework_fulfillment";
	public static final String FRAMEWORK_FULFILLMENT_COLUMN_ID = "id_framework_fulfillment";
	public static final String FRAMEWORK_FULFILLMENT_COLUMN_REJECT_REASON = "reject_reason";

	// --------------------------------------------------------------------------------
	//
	// FRAMEWORK HIERARCHY ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FRAMEWORK_HIERARCHY_TABLE = "framework_hierarchy";
	public static final String FRAMEWORK_HIERARCHY_COLUMN_ID = "id_framework_hierarchy";
	public static final String FRAMEWORK_HIERARCHY_COLUMN_LABEL = "label";
	public static final String FRAMEWORK_HIERARCHY_COLUMN_LEVEL = "level";
	public static final String FRAMEWORK_HIERARCHY_PARENT_HIERARCHY = "parent_hierarchy";

	// --------------------------------------------------------------------------------
	//
	// FRAMEWORK ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FRAMEWORK_ELEMENT_TABLE = "framework_element";
	public static final String FRAMEWORK_ELEMENT_COLUMN_ID = "id_framework_element";
	public static final String FRAMEWORK_ELEMENT_COLUMN_LABEL = "label";
	public static final String FRAMEWORK_ELEMENT_COLUMN_VALUE_RULE = "value_rule";
	public static final String FRAMEWORK_ELEMENT_COLUMN_DATA_TYPE = "data_type";

	// --------------------------------------------------------------------------------
	//
	// FRAMEWORK ELEMENT IMPLEMENTATION ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String FRAMEWORK_ELEMENT_IMPLEMENTATION_TABLE = "framework_element_implementation";
	public static final String FRAMEWORK_ELEMENT_IMPLEMENTATION_COLUMN_ID = "id_framework_element_implementation";

	// --------------------------------------------------------------------------------
	//
	// CONTACT MODEL ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CONTACT_MODEL_TABLE = "contact_model";
	public static final String CONTACT_MODEL_COLUMN_ID = "id_contact_model";
	public static final String CONTACT_MODEL_COLUMN_TYPE = "type";
	public static final String CONTACT_MODEL_COLUMN_NAME = "name";
	public static final String CONTACT_MODEL_COLUMN_STATUS = "status";
	public static final String CONTACT_MODEL_COLUMN_DATE_DELETED = "date_deleted";
	public static final String CONTACT_MODEL_COLUMN_DATE_MAINTENANCE = "date_maintenance";

	public static final int CONTACT_MODEL_NAME_MAX_LENGTH = 8192;

	// --------------------------------------------------------------------------------
	//
	// CONTACT CARD ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CONTACT_CARD_TABLE = "contact_card";
	public static final String CONTACT_CARD_COLUMN_ID = "id_contact_card";

	// --------------------------------------------------------------------------------
	//
	// CONTACT DETAILS ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CONTACT_DETAILS_TABLE = "contact_details";
	public static final String CONTACT_DETAILS_COLUMN_ID = "id_contact_details";

	// --------------------------------------------------------------------------------
	//
	// CONTACT DETAILS ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String DEFAULT_CONTACT_FLEXIBLE_ELEMENT_TABLE = "default_contact_flexible_element";
	public static final String DEFAULT_CONTACT_FLEXIBLE_ELEMENT_COLUMN_TYPE = "type";

	// --------------------------------------------------------------------------------
	//
	// CONTACT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final String CONTACT_TABLE = "contact";
	public static final String CONTACT_COLUMN_ID = "id_contact";
	public static final String CONTACT_COLUMN_NAME = "name";
	public static final String CONTACT_COLUMN_FIRSTNAME = "firstname";
	public static final String CONTACT_COLUMN_MAIN_ORG_UNIT = "id_main_org_unit";
	public static final String CONTACT_COLUMN_LOGIN = "login";
	public static final String CONTACT_COLUMN_EMAIL = "email";
	public static final String CONTACT_COLUMN_PHONE_NUMBER = "phone_number";
	public static final String CONTACT_COLUMN_POSTAL_ADDRESS = "postal_address";
	public static final String CONTACT_COLUMN_PHOTO = "photo";
	public static final String CONTACT_COLUMN_COUNTRY = "id_country";
	public static final String CONTACT_COLUMN_PARENT = "id_parent";
	public static final String CONTACT_COLUMN_USER = "id_user";
	public static final String CONTACT_COLUMN_DATE_CREATED = "date_created";
	public static final String CONTACT_COLUMN_DATE_DELETED = "date_deleted";

	public static final String CONTACT_ORG_UNIT_LINK_TABLE = "contact_unit";
	public static final String CONTACT_ORG_UNIT_COLUMN_ORG_UNIT = "id_org_unit";

	// --------------------------------------------------------------------------------
	//
	// CONTACT LIST ELEMENT ENTITY.
	//
	// --------------------------------------------------------------------------------
	public static final String CONTACT_LIST_ELEMENT_TABLE = "contact_list_element";
	public static final String CONTACT_LIST_ELEMENT_COLUMN_ALLOWED_TYPE = "allowed_type";
	public static final String CONTACT_LIST_ELEMENT_COLUMN_LIMIT = "contact_limit";
	public static final String CONTACT_LIST_ELEMENT_COLUMN_IS_MEMBER = "is_member";

	public static final String CONTACT_LIST_ELEMENT_ALLOWED_MODEL_LINK_TABLE = "contact_list_element_allowed_model";
}
