--------------------------------------------------------------------------------
--
--   PostgreSQL script to create all the first required data
--   to add a new empty Organization inside a Sigmah instance
--
--------------------------------------------------------------------------------
--
-- How to use it?
-- --------------
-- Use the search&replace tool of your text editor to replace all the following
-- parameters by the values you wan to give to them.
-- Example: replace all instances of "§OrganizationName§" by "MyNGO"
--
-- Once you've modified and run this script, you can log into Sigmah with the
-- specified email and the default password "sigmah" (you can change afterwards)
--
-- Parameters:
-- -----------
-- §OrganizationName§
-- §OrganizationLogoFilename§
-- §HeadquartersCountryCode§ (use an ISO 2-letters code like "AF" from http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements) 
-- §UserEmail§
-- §UserName§
-- §UserFirstName§
-- §UserLocale§ (must be "fr" or "en" or "es" (don't include quotes ("))
--
-- Default first user password is "sigmah"
--

START TRANSACTION;



-- Create Organization
INSERT INTO organization (id_organization, name, logo, id_root_org_unit) 
SELECT nextval('hibernate_sequence'), '§OrganizationName§', '§OrganizationLogoFilename§', NULL;


-- Create default org unit model
INSERT INTO org_unit_model (org_unit_model_id, name, has_budget, title, can_contain_projects, status, id_organization) 
SELECT nextval('hibernate_sequence'), 'Default empty organizational unit model', false, 'DefaultEmpty', true, 'DRAFT', MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§' ;

-- Fill default org unit model
INSERT INTO flexible_element (id_flexible_element, amendable, label, validates, id_privacy_group)
SELECT nextval('hibernate_sequence'), FALSE, 'DefaultEmptyOrgUnit-code', true, NULL;
INSERT INTO default_flexible_element (id_flexible_element, type)
SELECT MAX(id_flexible_element), 'CODE' FROM flexible_element WHERE label = 'DefaultEmptyOrgUnit-code';

INSERT INTO flexible_element (id_flexible_element, amendable, label, validates, id_privacy_group)
SELECT nextval('hibernate_sequence'), FALSE, 'DefaultEmptyOrgUnit-title', true, NULL;
INSERT INTO default_flexible_element (id_flexible_element, type)
SELECT MAX(id_flexible_element), 'TITLE' FROM flexible_element WHERE label = 'DefaultEmptyOrgUnit-title';

INSERT INTO flexible_element (id_flexible_element, amendable, label, validates, id_privacy_group)
SELECT nextval('hibernate_sequence'), FALSE, 'DefaultEmptyOrgUnit-country', true, NULL;
INSERT INTO default_flexible_element (id_flexible_element, type)
SELECT MAX(id_flexible_element), 'COUNTRY' FROM flexible_element WHERE label = 'DefaultEmptyOrgUnit-country';

INSERT INTO layout (id_layout, columns_count, rows_count) 
SELECT nextval('hibernate_sequence'), 3, 2;
INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout) 
SELECT nextval('hibernate_sequence'), 0, 0, NULL, MAX(id_layout) FROM layout;

INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
SELECT nextval('hibernate_sequence'), 0, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-code';

INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout) 
SELECT nextval('hibernate_sequence'), 0, 1, NULL, MAX(id_layout) FROM layout;

INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
SELECT nextval('hibernate_sequence'), 0, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-title';

INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout) 
SELECT nextval('hibernate_sequence'), 1, 0, NULL, MAX(id_layout) FROM layout;

INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) 
SELECT nextval('hibernate_sequence'), 0, MAX(id_flexible_element), MAX(id_layout_group) FROM layout_group, flexible_element WHERE label = 'DefaultEmptyOrgUnit-country';

INSERT INTO org_unit_banner (banner_id, id_layout, id_org_unit_model) 
SELECT nextval('hibernate_sequence'), MAX(id_layout), MAX(org_unit_model_id) FROM layout, org_unit_model WHERE title = 'DefaultEmpty';

INSERT INTO layout (id_layout, columns_count, rows_count)
SELECT nextval('hibernate_sequence'), 1, 1;

INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout) 
SELECT nextval('hibernate_sequence'), 0, 0, 'Organizational unit characteristics', MAX(id_layout) FROM layout;

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
INSERT INTO Partner (partnerid, fullname, name, planned_budget, received_budget, spend_budget, location_locationid, id_org_unit_model, organization_id_organization, parent_Partnerid, calendarid, office_country_id)
SELECT nextval('hibernate_sequence'), 'Empty HeadQuarters', 'emptyHQ', 0.0, 0.0, 0.0, NULL, MAX(org_unit_model_id), MAX(organization.id_organization), NULL, MAX(pc.id), MAX(c.CountryId) 
FROM org_unit_model, organization, Country c, PersonalCalendar pc WHERE title = 'DefaultEmpty' AND logo = '§OrganizationLogoFilename§' AND c.ISO2='§HeadquartersCountryCode§' ;


UPDATE organization SET id_root_org_unit = (SELECT MAX(Partnerid) FROM Partner WHERE name = 'emptyHQ') 
WHERE logo = '§OrganizationLogoFilename§';


-- Default admin profile for first user of a new organization (the "Pioneer Administrator") 
INSERT INTO profile (id_profile, name, id_organization)
SELECT nextval('hibernate_sequence'), 'PioneerAdministrator', MAX(id_organization)
FROM organization WHERE logo = '§OrganizationLogoFilename§';

INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'VIEW_PROJECT', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'CREATE_PROJECT', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'EDIT_PROJECT', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'CHANGE_PHASE', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'REMOVE_FILE', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'VIEW_ADMIN', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_UNIT', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_USER', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'VALID_AMENDEMENT', id_profile FROM profile WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'GLOBAL_EXPORT', id_profile FROM profile   WHERE name = 'PioneerAdministrator' AND id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');

INSERT INTO privacy_group (id_privacy_group, code, title, id_organization)
SELECT nextval('hibernate_sequence'), 0, 'Pioneer Administrator exclusive data', MAX(id_organization)
FROM organization WHERE logo = '§OrganizationLogoFilename§';

INSERT INTO privacy_group_permission (id_permission, permission, id_privacy_group, id_profile)
SELECT nextval('hibernate_sequence'), 'READ', pg.id_privacy_group, p.id_profile 
FROM privacy_group pg, profile p 
WHERE pg.title = 'Pioneer Administrator exclusive data' and pg.id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§') 
  AND p.name = 'PioneerAdministrator';

INSERT INTO privacy_group_permission (id_permission, permission, id_privacy_group, id_profile)
SELECT nextval('hibernate_sequence'), 'WRITE', pg.id_privacy_group, p.id_profile 
FROM privacy_group pg, profile p 
WHERE pg.title = 'Pioneer Administrator exclusive data' and pg.id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§') 
  AND p.name = 'PioneerAdministrator';



-- Create first organizational Admin User (the "Pioneer Administrator")
INSERT INTO UserLogin (userid, changePasswordKey, dateChangePasswordKeyIssued, Email, FirstName, Password, Locale, Name, NewUser, id_organization) 
SELECT nextval('hibernate_sequence'), NULL, NULL, '§UserEmail§', '§UserFirstName§', '$2a$10$pMcTA1p9fefR8U9NoOPei.H0eq/TbbdSF27M0tn9iDWBrA4JHeCDC', '§UserLocale§', '§UserName§', true, MAX(id_organization)
FROM organization WHERE logo = '§OrganizationLogoFilename§';

INSERT INTO user_unit (id_user_unit, id_org_unit, id_user) 
SELECT nextval('hibernate_sequence'), MAX(Partnerid), UserId
FROM Partner, UserLogin
WHERE Partner.name = 'emptyHQ' AND Email = '§UserEmail§' 
AND Partner.organization_id_organization = (
      SELECT MAX(id_organization)
      FROM organization
      WHERE logo = '§OrganizationLogoFilename§'
    )
GROUP BY UserId;

INSERT INTO user_unit_profiles (id_user_unit, id_profile) 
SELECT MAX(id_user_unit), MAX(id_profile) 
FROM user_unit, profile 
WHERE user_unit.id_user=(SELECT UserId FROM UserLogin WHERE Email = '§UserEmail§') 
  AND profile.name = 'PioneerAdministrator'
  AND profile.id_organization = (SELECT MAX(id_organization) FROM organization WHERE logo = '§OrganizationLogoFilename§');

-- Add basic default settings for Global Export
INSERT INTO global_export_settings(
            id, auto_delete_frequency, auto_export_frequency, default_organization_export_format, 
            export_format, last_export_date, locale_string, organization_id)
SELECT nextval('hibernate_sequence'), NULL, NULL, 'XLS', 'XLS', NULL, 'fr', MAX(id_organization) 
FROM organization WHERE logo = '§OrganizationLogoFilename§' ;


COMMIT;
