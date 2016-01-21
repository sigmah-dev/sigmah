package org.sigmah.client.util;

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


public final class AdminUtil {

	public static final String ADMIN_PROJECT_MODEL = "projectModel";
	public static final String ADMIN_ORG_UNIT_MODEL = "orgUnitModel";
	public static final String ADMIN_CATEGORY_MODEL = "categoryModel";
	public static final String ADMIN_REPORT_MODEL = "reportModel";

	public static final String ADMIN_SCHEMA = "schema";
	public static final String ADMIN_IMPORTATION_SCHEME_MODEL = "importationSchemeModel";
	public static final String PROP_LOG_FRAME = "log_frame";

	public static final String PROP_LOG_FRAME_NAME = "logframe name";
	public static final String PROP_OBJ_MAX = "objectives_max";
	public static final String PROP_OBJ_MAX_PER_GROUP = "objectives_max_per_group";
	public static final String PROP_OBJ_ENABLE_GROUPS = "objectives_enable_groups";
	public static final String PROP_OBJ_MAX_GROUPS = "objectives_max_groups";
	public static final String PROP_A_MAX = "activities_max";
	public static final String PROP_A_ENABLE_GROUPS = "activities_enable_groups";
	public static final String PROP_A_MAX_PER_RESULT = "activities_max_per_result";
	public static final String PROP_A_MAX_GROUPS = "activities_max_groups";
	public static final String PROP_A_MAX_PER_GROUP = "activities_max_per_group";
	public static final String PROP_R_MAX = "results_max";
	public static final String PROP_R_ENABLE_GROUPS = "results_enable_groups";
	public static final String PROP_R_MAX_PER_OBJ = "results_max_per_obj";
	public static final String PROP_R_MAX_GROUPS = "results_max_groups";
	public static final String PROP_R_MAX_PER_GROUP = "results_max_per_group";
	public static final String PROP_P_MAX = "prerequisites_max";
	public static final String PROP_P_ENABLE_GROUPS = "prerequisites_enable_groups";
	public static final String PROP_P_MAX_GROUPS = "prerequisites_max_groups";
	public static final String PROP_P_MAX_PER_GROUP = "prerequisites_max_per_group";

	public static final String PROP_FX_REPORT_MODEL = "FlexibleElementReportModel";

	/**
	 * References a {@link Number} data (thus, it can reference an {@code Integer} or a {@code Long}).
	 */
	public static final String PROP_FX_MAX_LIMIT = "FlexibleElementMaxLimit";

	/**
	 * References a {@link Number} data (thus, it can reference an {@code Integer} or a {@code Long}).
	 */
	public static final String PROP_FX_MIN_LIMIT = "FlexibleElementMinLimit";

	public static final String PROP_FX_TEXT_TYPE = "FlexibleElementTextType";
	public static final String PROP_FX_LENGTH = "FlexibleElementLength";
	public static final String PROP_FX_DECIMAL = "FlexibleElementDecimal";
	public static final String PROP_FX_Q_MULTIPLE = "FlexibleElementQuestionMultiple";
	public static final String PROP_FX_Q_QUALITY = "FlexibleElementQuestionQuality";
	public static final String PROP_FX_Q_CATEGORY = "FlexibleElementQuestionCategory";
	public static final String PROP_FX_Q_CHOICES = "FlexibleElementQuestionChoices";
	public static final String PROP_FX_Q_CHOICES_DISABLED = "FlexibleElementQuestionChoicesDisabled";
	public static final String PROP_FX_B_BUDGETSUBFIELDS = "FlexibleElementBudgetSubFields";
	public static final String PROP_FX_B_BUDGET_RATIO_DIVIDEND = "FlexibleElementBudgetRatioDividend";
	public static final String PROP_FX_B_BUDGET_RATIO_DIVISOR = "FlexibleElementBudgetRatioDivisor";

	public static final String PROP_FX_NAME = "name";
	public static final String PROP_FX_TYPE = "type";
	public static final String PROP_FX_GROUP = "group";
	public static final String PROP_FX_ORDER_IN_GROUP = "orderInGroup";
	public static final String PROP_FX_IN_BANNER = "inBanner";
	public static final String PROP_FX_POS_IN_BANNER = "posBanner";
	public static final String PROP_FX_IS_COMPULSARY = "isCompulsory";
	public static final String PROP_FX_PRIVACY_GROUP = "privacyGroup";
	public static final String PROP_FX_AMENDABLE = "amendable";
	public static final String PROP_FX_EXPORTABLE = "exportable";
	public static final String PROP_FX_BUDGET_SUBFIELD = "budgetSubField";
	public static final String PROP_FX_BUDGET_SUBFIELD_NAME = "name";

	public static final String PROP_FX_LC_BANNER = "layoutConstraintBanner";
	public static final String PROP_FX_LC = "layoutConstraint";

	public static final String PROP_FX_FLEXIBLE_ELEMENT = "flexibleElement";

	public static final String PROP_FX_OLD_FIELDS = "oldFieldProperties";

	public static final String PROP_NEW_GROUP_LAYOUT = "NewLayoutGroup";

	public static final String PROP_PHASE_MODEL = "NewPhaseModel";
	public static final String PROP_PHASE_ROWS = "PhaseRows";
	public static final String PROP_PHASE_ROOT = "PhaseRoot";
	public static final String PROP_PHASE_ORDER = "PhaseDisplayOrder";
	public static final String PROP_PHASE_GUIDE = "PhaseGuide";

	public static final String PROP_REPORT_MODEL_NAME = "ProjectReportModelName";
	public static final String PROP_REPORT_SECTION_MODEL = "ProjectReportModelSection";

	public static final String PROP_CATEGORY_TYPE = "NewCategoryType";
	public static final String PROP_CATEGORY_TYPE_NAME = "NewCategoryTypeName";
	public static final String PROP_CATEGORY_TYPE_ICON = "NewCategoryTypeIcon";
	public static final String PROP_CATEGORY_ELEMENT = "NewCategoryElement";
	public static final String PROP_CATEGORY_ELEMENT_NAME = "NewCategoryElementName";
	public static final String PROP_CATEGORY_ELEMENT_COLOR = "NewCategoryElementColor";

	public static final String PROP_PM_NAME = "ProjectModelName";
	public static final String PROP_PM_USE = "ProjectModelUse";
	public static final String PROP_PM_STATUS = "ProjectModelStatus";
	public static final String PROP_PM_MAINTENANCE_DATE = "ProjectModelMaintenanceDate";

	public static final String PROP_OM_NAME = "OrgUnitModelName";
	public static final String PROP_OM_STATUS = "OrgUnitModelStatus";
	public static final String PROP_OM_TITLE = "OrgUnitModelTitle";
	public static final String PROP_OM_HAS_BUDGET = "OrgUnitModelBudget";
	public static final String PROP_OM_HAS_SITE = "OrgUnitModelSite";
	public static final String PROP_OM_CONTAINS_PROJECTS = "OrgUnitModelContainProjects";
	public static final String PROP_OM_MAINTENANCE_DATE = "OrgUnitModelMaintenanceDate";

	public static final String PROP_SCH_NAME = "importationSchemeName";
	public static final String PROP_SCH_FILE_FORMAT = "importationSchemeFileFormat";
	public static final String PROP_SCH_IMPORT_TYPE = "importationSchemeImportType";
	public static final String PROP_SCH_SHEET_NAME = "importationSchemeSheetName";
	public static final String PROP_SCH_FIRST_ROW = "importationSchemeFirstRow";

	public static final String PROP_VAR_VARIABLE = "variable";
	public static final String PROP_VAR_NAME = "name";
	public static final String PROP_VAR_REFERENCE = "reference";
	public static final String PROP_VAR_FLE_ID_KEY = "importationSchemeIdKey";
	public static final String PROP_VAR_FLE_BUDGETSUBFIELDS = "variableBudgetSubFields";

	private AdminUtil() {
		// Only provides static constants.
	}

}
