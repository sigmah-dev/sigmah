--------------------------------------------------------------------------------
--
--   PostgreSQL function to create all the first required data
--   to add a new empty Organization inside a Sigmah instance
--
--------------------------------------------------------------------------------
--
-- Once you've runned this function, you can log into Sigmah with the
-- specified email and the default password "sigmah" (you can change afterwards)
--
-- Parameters:
-- -----------
-- p_organization_name
-- p_organization_logo_filename
-- p_headquarters_country_code (use an ISO 2-letters code like "AF" from http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements) 
-- p_user_email
-- p_user_name
-- p_user_first_name
-- p_user_locale (must be "fr" or "en" or "es" (don't include quotes ("))
--
-- Default first user password is "sigmah"
--


CREATE OR REPLACE FUNCTION create_new_organization(p_organization_name VARCHAR, p_organization_logo_filename VARCHAR, p_headquarters_country_code VARCHAR, p_user_email VARCHAR, p_user_name VARCHAR, p_user_first_name VARCHAR,  p_user_locale VARCHAR) RETURNS INTEGER AS $$
    DECLARE
        v_organization_id INTEGER;
        v_org_unit_model_id INTEGER;
	v_user_id INTEGER;
	v_profile_id INTEGER;
    BEGIN
	-- Create Organization
	SELECT nextval('hibernate_sequence') INTO v_organization_id;
	INSERT INTO organization (id_organization, name, logo, id_root_org_unit) 
	VALUES (v_organization_id, p_organization_name, p_organization_logo_filename, NULL);


	-- Create default org unit model
	SELECT nextval('hibernate_sequence') INTO v_org_unit_model_id;
	INSERT INTO org_unit_model (org_unit_model_id, name, has_budget, title, can_contain_projects, status, id_organization) 
	VALUES (v_org_unit_model_id, 'Default empty organizational unit model', false, 'DefaultEmpty', true, 'DRAFT', v_organization_id );

	-- Fill default org unit model
	INSERT INTO flexible_element (id_flexible_element, amendable, label, validates, id_privacy_group, exportable, globally_exportable)
	SELECT nextval('hibernate_sequence'), FALSE, 'DefaultEmptyOrgUnit-code', true, NULL, true, true;
	INSERT INTO default_flexible_element (id_flexible_element, type)
	SELECT MAX(id_flexible_element), 'CODE' FROM flexible_element WHERE label = 'DefaultEmptyOrgUnit-code';

	INSERT INTO flexible_element (id_flexible_element, amendable, label, validates, id_privacy_group, exportable, globally_exportable)
	SELECT nextval('hibernate_sequence'), FALSE, 'DefaultEmptyOrgUnit-title', true, NULL, true, true;
	INSERT INTO default_flexible_element (id_flexible_element, type)
	SELECT MAX(id_flexible_element), 'TITLE' FROM flexible_element WHERE label = 'DefaultEmptyOrgUnit-title';

	INSERT INTO flexible_element (id_flexible_element, amendable, label, validates, id_privacy_group, exportable, globally_exportable)
	SELECT nextval('hibernate_sequence'), FALSE, 'DefaultEmptyOrgUnit-country', true, NULL, true, true;
	INSERT INTO default_flexible_element (id_flexible_element, type)
	SELECT MAX(id_flexible_element), 'COUNTRY' FROM flexible_element WHERE label = 'DefaultEmptyOrgUnit-country';

	INSERT INTO layout (id_layout, columns_count, rows_count) 
	SELECT nextval('hibernate_sequence'), 3, 2;
	INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout, has_iterations) 
	SELECT nextval('hibernate_sequence'), 0, 0, NULL, MAX(id_layout), false FROM layout;

	INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
	SELECT nextval('hibernate_sequence'), 0, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-code';

	INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout, has_iterations) 
	SELECT nextval('hibernate_sequence'), 0, 1, NULL, MAX(id_layout), false FROM layout;

	INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
	SELECT nextval('hibernate_sequence'), 0, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-title';

	INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout, has_iterations) 
	SELECT nextval('hibernate_sequence'), 1, 0, NULL, MAX(id_layout), false FROM layout;

	INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
	SELECT nextval('hibernate_sequence'), 0, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-country';

	INSERT INTO org_unit_banner (banner_id, id_layout, id_org_unit_model) 
	SELECT nextval('hibernate_sequence'), MAX(id_layout), MAX(org_unit_model_id) FROM layout, org_unit_model WHERE title = 'DefaultEmpty';

	INSERT INTO layout (id_layout, columns_count, rows_count)
	SELECT nextval('hibernate_sequence'), 1, 1;

	INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout, has_iterations) 
	SELECT nextval('hibernate_sequence'), 0, 0, 'Organizational unit characteristics', MAX(id_layout), false FROM layout;

	INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
	SELECT nextval('hibernate_sequence'), 1, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-code';

	INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
	SELECT nextval('hibernate_sequence'), 2, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-title';      

	INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
	SELECT nextval('hibernate_sequence'), 3, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-country';

	INSERT INTO org_unit_details (details_id, id_layout, id_org_unit_model) 
	SELECT nextval('hibernate_sequence'), MAX(id_layout), MAX(org_unit_model_id) FROM layout, org_unit_model WHERE title = 'DefaultEmpty';



	-- Create default empty Org Unit
	INSERT INTO PersonalCalendar (id, name) SELECT nextval('hibernate_sequence'), 'Événements';
	INSERT INTO Partner (partnerid, fullname, name, location_locationid, id_org_unit_model, organization_id_organization, parent_Partnerid, calendarid, office_country_id)
	SELECT nextval('hibernate_sequence'), 'Empty HeadQuarters', 'emptyHQ', NULL, v_org_unit_model_id, v_organization_id, NULL, MAX(pc.id), MAX(c.CountryId) 
	FROM Country c, PersonalCalendar pc WHERE c.ISO2=p_headquarters_country_code ;


	UPDATE organization SET id_root_org_unit = (SELECT MAX(Partnerid) FROM Partner WHERE name = 'emptyHQ') 
	WHERE id_organization = v_organization_id;


	-- Default admin profile for first user of a new organization (the "Pioneer Administrator") 
	SELECT nextval('hibernate_sequence') INTO v_profile_id;
	INSERT INTO profile (id_profile, name, id_organization)
	VALUES (v_profile_id, 'PioneerAdministrator', v_organization_id);

	INSERT INTO global_permission (id_global_permission, permission, id_profile)
	SELECT nextval('hibernate_sequence'), global_privileges.*, id_profile 
	FROM profile, (select * 
	from unnest(ARRAY['VIEW_ALL_PROJECTS','EDIT_ORG_UNIT','VIEW_PROJECT_AGENDA','MANAGE_ORG_UNITS','DELETE_VISIBLE_CONTACTS',
	'LOCK_PROJECT','EDIT_PROJECT_TEAM_MEMBERS','VIEW_ADMIN','MANAGE_MAIN_SITE','REMOVE_ORG_UNIT_FILE','CREATE_ITERATIONS',
	'CREATE_TEST_PROJECT','MANAGE_CATEGORIES','MANAGE_SITES','VIEW_VISIBLE_CONTACTS','CHANGE_PHASE','VIEW_MAPTAB','VIEW_LOGFRAME',
	'EDIT_ALL_PROJECTS','CREATE_PROJECT','VIEW_PROJECT_TEAM_MEMBERS','MODIFY_LOCKED_CONTENT','MANAGE_ORG_UNIT_MODELS',
	'EDIT_PROJECT','REMOVE_PROJECT_FILE','EDIT_ORG_UNIT_AGENDA','RELATE_PROJECT','EDIT_LOGFRAME','VIEW_INDICATOR','MANAGE_INDICATOR',
	'MANAGE_PROJECT_MODELS','DELETE_PROJECT','MANAGE_CONTACT_MODELS','MANAGE_REPORT_MODELS','VALID_AMENDEMENT','GLOBAL_EXPORT',
	'PROBES_MANAGEMENT','EDIT_ALL_REMINDERS','EXPORT_ALL_CONTACTS','IMPORT_BUTTON','MANAGE_USERS','MANAGE_IMPORTATION_SCHEMES',
	'EDIT_VISIBLE_CONTACTS','MANAGE_SETTINGS','EXPORT_HXL','EDIT_INDICATOR','EDIT_OWN_REMINDERS','IMPORT_CONTACTS','CHANGE_PASSWORD',
	'EDIT_PROJECT_AGENDA','VIEW_MY_PROJECTS','VIEW_ORG_UNIT_AGENDA'])) as global_privileges
	WHERE name = 'PioneerAdministrator' AND id_organization = v_organization_id;


	INSERT INTO privacy_group (id_privacy_group, code, title, id_organization)
	SELECT nextval('hibernate_sequence'), 0, 'Pioneer Administrator exclusive data',v_organization_id;

	INSERT INTO privacy_group_permission (id_permission, permission, id_privacy_group, id_profile)
	SELECT nextval('hibernate_sequence'), 'WRITE', pg.id_privacy_group, v_profile_id 
	FROM privacy_group pg 
	WHERE pg.title = 'Pioneer Administrator exclusive data' and pg.id_organization = v_organization_id;
	
	-- Create first organizational Admin User (the "Pioneer Administrator")
	SELECT nextval('hibernate_sequence') INTO v_user_id;
	INSERT INTO UserLogin (userid, changePasswordKey, dateChangePasswordKeyIssued, Email, FirstName, Password, Locale, Name, NewUser, id_organization, active) 
	VALUES (v_user_id, NULL, NULL, p_user_email, p_user_first_name, '$2a$10$pMcTA1p9fefR8U9NoOPei.H0eq/TbbdSF27M0tn9iDWBrA4JHeCDC', p_user_locale, p_user_name, true, v_organization_id, true);

	INSERT INTO user_unit (id_user_unit, id_org_unit, id_user, user_unit_type) 
	SELECT nextval('hibernate_sequence'), MAX(Partnerid), v_user_id, 'MAIN'
	FROM Partner
	WHERE Partner.name = 'emptyHQ'  
			AND Partner.organization_id_organization = v_organization_id;

	INSERT INTO user_unit_profiles (id_user_unit, id_profile) 
	SELECT MAX(id_user_unit), MAX(id_profile) 
	FROM user_unit, profile 
	WHERE user_unit.id_user = v_user_id 
	  or profile.id_profile = v_profile_id;

	-- Add basic default settings for Global Export
	INSERT INTO global_export_settings(
				id, auto_delete_frequency, auto_export_frequency, default_organization_export_format, 
				export_format, last_export_date, locale_string, organization_id)
	SELECT nextval('hibernate_sequence'), NULL, NULL, 'XLS', 'XLS', NULL, p_user_locale, v_organization_id ;

	-- Default password expiration policy (with no automatic reset set)
	INSERT INTO password_expiration_policy (id, policy_type, reset_for_new_users, organization_id)
	SELECT nextval('hibernate_sequence'), 'NEVER', false, v_organization_id;
	

	-- Create contact model for organisation and users		
	PERFORM contact_model_create(v_organization_id, 'ORGANIZATION', p_organization_name || ' model');
	PERFORM contact_model_create(v_organization_id, 'INDIVIDUAL', 'Sigmah user model');
	
	INSERT INTO contact (id_contact, id_contact_model, id_organization, date_created)
	SELECT nextval('hibernate_sequence'), cm.id_contact_model, o.id_organization, NOW()
	FROM organization o
	JOIN contact_model cm ON (cm.id_organization = o.id_organization AND cm.name = o.name || ' model');

	INSERT INTO contact (id_contact, id_contact_model, id_user, id_parent, date_created)
	SELECT nextval('hibernate_sequence'), cm.id_contact_model, u.userid, parent.id_contact, NOW()
	FROM userlogin u
	JOIN contact_model cm ON (cm.id_organization = u.id_organization AND cm.name = 'Sigmah user model')
	JOIN contact parent ON (parent.id_organization = u.id_organization);


        RETURN v_organization_id;
    END;
$$ LANGUAGE plpgsql;

