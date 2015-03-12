package org.sigmah.client.page;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.conf.PropertyName;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Page enumeration which values represent URL tokens.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Claire Yang (cyang@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public enum Page implements IsSerializable {

	PROJECT_DASHBOARD(Pages.PROJECT_PARENT_KEY, "dashboard"),
	PROJECT_DETAILS(Pages.PROJECT_PARENT_KEY, "details"),
	PROJECT_LOGFRAME(Pages.PROJECT_PARENT_KEY, "logframe"),
	PROJECT_INDICATORS_MANAGEMENT(Pages.PROJECT_PARENT_KEY, "indicators-management"),
	PROJECT_INDICATORS_ENTRIES(Pages.PROJECT_PARENT_KEY, "indicators-entries"),
	PROJECT_INDICATORS_MAP(Pages.PROJECT_PARENT_KEY, "indicators-map"),
	PROJECT_CALENDAR(Pages.PROJECT_PARENT_KEY, "calendar"),
	PROJECT_REPORTS(Pages.PROJECT_PARENT_KEY, "reports"),
	PROJECT_EXPORTS(Pages.PROJECT_PARENT_KEY, "exports"),
	PROJECT_EXPORTS_SETTING(Pages.PROJECT_PARENT_KEY, "exports-setting"),
	PROJECT_AMENDMENT_RENAME(Pages.PROJECT_PARENT_KEY, "amendment-rename"),
	PROJECT_AMENDMENT_DIFF(Pages.PROJECT_PARENT_KEY, "amendment-diff"),

	REMINDER_EDIT("reminder-edit"),
	REMINDER_HISTORY("reminder-history"),
	LINKED_PROJECT("linked-project"),
	CALENDAR_EVENT("calendar-event"),
	REPORT_CREATE("report-create"),
	ATTACH_FILE("attach-file"),
	INDICATOR_EDIT("indicator-edit"),
	SITE_EDIT("site-edit"),

	ORGUNIT_DASHBOARD(Pages.ORGUNIT_PARENT_KEY, "dashboard"),
	ORGUNIT_DETAILS(Pages.ORGUNIT_PARENT_KEY, "details"),
	ORGUNIT_CALENDAR(Pages.ORGUNIT_PARENT_KEY, "calendar"),
	ORGUNIT_REPORTS(Pages.ORGUNIT_PARENT_KEY, "reports"),

	ADMIN_USERS(Pages.ADMIN_PARENT_KEY, "users"),
	ADMIN_ORG_UNITS(Pages.ADMIN_PARENT_KEY, "org-units"),
	ADMIN_PROJECTS_MODELS(Pages.ADMIN_PARENT_KEY, "projects-models"),
	ADMIN_ORG_UNITS_MODELS(Pages.ADMIN_PARENT_KEY, "org-units-models"),
	ADMIN_REPORTS_MODELS(Pages.ADMIN_PARENT_KEY, "reports"),
	ADMIN_CATEGORIES(Pages.ADMIN_PARENT_KEY, "categories"),
	ADMIN_PARAMETERS(Pages.ADMIN_PARENT_KEY, "parameters"),
	ADMIN_IMPORTATION_SCHEME(Pages.ADMIN_PARENT_KEY, "importation_scheme"),
	ADMIN_ADD_IMPORTATION_SCHEME(Pages.ADMIN_PARENT_KEY, "add_importation_scheme"),
	ADMIN_ADD_VARIABLE_IMPORTATION_SCHEME(Pages.ADMIN_PARENT_KEY, "add_variable_importation_shceme"),
	ADMIN_ADD_IMPORTATION_SCHEME_MODEL(Pages.ADMIN_PARENT_KEY, "add_importation_scheme_model"),
	ADMIN_ADD_IMPORTATION_SCHEME_MODEL_MATCHING_RULE(Pages.ADMIN_PARENT_KEY, "add_importation_scheme_model_matching_rule"),
	IMPORT_MODEL(Pages.ADMIN_PARENT_KEY, "import-model"),

	ADMIN_PRIVACY_GROUP_EDIT("privacy-group-edit"),
	ADMIN_PROFILE_EDIT("profile-edit"),
	ADMIN_USER_EDIT("user-edit"),

	ADMIN_ADD_ORG_UNIT("add-org-unit"),
	ADMIN_MOVE_ORG_UNIT("move-org-unit"),

	ADMIN_ADD_PROJECT_MODEL("add-project-model"),
	ADMIN_ADD_ORG_UNIT_MODEL("add-org-unit-model"),
	ADMIN_EDIT_LAYOUT_GROUP_MODEL("layout-group-edit"),
	ADMIN_EDIT_FLEXIBLE_ELEMENT("flexible-element-edit"),
	ADMIN_EDIT_FLEXIBLE_ELEMENT_ADD_BUDGETSUBFIELD("add-budgetsubfield-flexible-element-edit"),
	ADMIN_EDIT_PHASE_MODEL("phase-model-edit"),
	
	OFFLINE_SELECT_FILES("offline-select-files"),

	// OTHER PAGES.

	DASHBOARD("dashboard"),
	MOCKUP("mockup"),
	LOGIN("login"),
	LOST_PASSWORD("lost-password"),
	RESET_PASSWORD("reset-password"),
	CHANGE_OWN_PASSWORD("change-own-password"),
	CREDITS("credits", true),
	HELP("help", true),
	CREATE_PROJECT("create-project", true),
	IMPORT_VALUES("import-values"), ;

	private final String parentKey;
	private final String token;
	private final boolean skipHistory;
	private String pageTitle;

	/**
	 * Instantiates a new {@code Page} object.
	 * 
	 * @param token
	 *          The page token (must be unique).
	 * @throws IllegalArgumentException
	 *           If the page token is invalid or non-unique.
	 */
	private Page(final String token) {
		this(null, token, false);
	}

	/**
	 * Instantiates a new {@code Page} object with its title.
	 * 
	 * @param parentKey
	 *          The parent key.
	 * @param token
	 *          The page token (must be unique).
	 * @throws IllegalArgumentException
	 *           If the page token is invalid or non-unique.
	 */
	private Page(final String parentKey, final String token) {
		this(parentKey, token, false);
	}

	/**
	 * Instantiates a new {@code Page} object with its history configuration.
	 * 
	 * @param token
	 *          The page token (must be unique).
	 * @param skipHistory
	 *          Defines if the page must be considered in the history.
	 * @throws IllegalArgumentException
	 *           If the page token is invalid or non-unique.
	 */
	private Page(final String token, final boolean skipHistory) {
		this(null, token, skipHistory);
	}

	/**
	 * Instantiates a new {@code Page} object with its title and history configuration.
	 * 
	 * @param parentKey
	 *          The parent key.
	 * @param token
	 *          The page token (must be unique).
	 * @param skipHistory
	 *          Defines if the page must be considered in the history.
	 * @throws IllegalArgumentException
	 *           If the page token is invalid or non-unique.
	 */
	private Page(final String parentKey, final String token, final boolean skipHistory) {
		this.parentKey = parentKey;
		this.token = (parentKey != null ? parentKey + Pages.KEY_SUFFIX : "") + token;
		this.skipHistory = skipHistory;

		if (ClientUtils.isBlank(getToken())) {
			throw new IllegalArgumentException("Invalid page token: '" + getToken() + "'.");
		}
		if (Pages.PAGES.containsKey(getToken())) {
			throw new IllegalArgumentException("Non-unique page token: '" + getToken() + "'.");
		}
		Pages.PAGES.put(getToken(), this);
	}

	/**
	 * Indicates if the page needs to be considered in the history.
	 * 
	 * @return {@code true} if the page needs to be considered in the history, {@code false} otherwise.
	 */
	public boolean skipHistory() {
		return skipHistory;
	}

	/**
	 * Returns the parent key.
	 * 
	 * @return The parent key.
	 */
	public String getParentKey() {
		return parentKey;
	}

	/**
	 * Returns the page token.
	 * 
	 * @return The page token.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Returns a new request for this page.
	 * 
	 * @return A new {@link PageRequest} instance for the current page.
	 */
	public PageRequest request() {
		return new PageRequest(this);
	}

	/**
	 * A convenience method for calling {@code request().addParameter(name, value)}.
	 * 
	 * @param name
	 *          The URL parameter name.
	 * @param value
	 *          The URL parameter value.
	 * @return A {@code PageRequest} instance with the given parameter.
	 * @see PageRequest#addParameter(RequestParameter, Object)
	 */
	public PageRequest requestWith(final RequestParameter name, final Object value) {
		return new PageRequest(this).addParameter(name, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return token;
	}

	/**
	 * Sets the page title.<br/>
	 * If not {@code null}, this value will be returned by {@link #getTitle(Page)} method.
	 * 
	 * @param pageTitle
	 *          The new page title carried by this instance.
	 */
	public void setTitle(final String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/**
	 * <p>
	 * Returns the given {@code page} corresponding title.
	 * </p>
	 * <p>
	 * The method follows this pattern:
	 * <ol>
	 * <li>The page is {@code null}: returns {@code null}.</li>
	 * <li>The page carries a dynamic title value: returns the dynamic page title (see {@link #setTitle(String)} method).</li>
	 * <li>[<em>client-side only</em>] Returns a static i18n constant associated to the page.</li>
	 * <li>Returns an error value containing the page token.</li>
	 * </ol>
	 * </p>
	 * 
	 * @param page
	 *          The {@link Page} instance.
	 * @return The given {@code page} corresponding title, or {@code null}.
	 */
	// Static title getter allows Page instance to be used on server-side.
	public static String getTitle(final Page page) {

		if (page == null) {
			return null;
		}

		if (page.pageTitle != null) {
			return page.pageTitle;
		}

		if (!GWT.isClient()) {
			return PropertyName.error(page.token);
		}

		switch (page) {
			case DASHBOARD:
				return I18N.CONSTANTS.dashboard();
			case MOCKUP:
				return "Mock-up";
			case CREDITS:
				return I18N.CONSTANTS.credits();
			case HELP:
				return I18N.CONSTANTS.help();
			case LOST_PASSWORD:
				return I18N.CONSTANTS.loginPasswordForgotten();
			case ADMIN_USERS:
				return I18N.CONSTANTS.adminboard();
			case ADMIN_ORG_UNITS:
				return I18N.CONSTANTS.adminboard();
			case ADMIN_PROJECTS_MODELS:
				return I18N.CONSTANTS.adminboard();
			case ADMIN_ORG_UNITS_MODELS:
				return I18N.CONSTANTS.adminboard();
			case ADMIN_REPORTS_MODELS:
				return I18N.CONSTANTS.adminboard();
			case ADMIN_CATEGORIES:
				return I18N.CONSTANTS.adminboard();
			case ADMIN_PARAMETERS:
				return I18N.CONSTANTS.adminboard();
			case CREATE_PROJECT:
				return I18N.CONSTANTS.createProject();
			default:
				return PropertyName.error(page.token);
		}
	}

}
