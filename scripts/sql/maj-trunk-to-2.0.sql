-- Add table for storing the password expiration policy of an organization (issue #438 / #517)
-- ------
CREATE TABLE password_expiration_policy
(
  id bigint NOT NULL,
  policy_type character varying(255),
  reference_date date,
  frequency integer,
  reset_for_new_users boolean NOT NULL,
  organization_id integer NOT NULL,
  CONSTRAINT password_expiration_policy_pkey PRIMARY KEY (id),
  CONSTRAINT fk2fb477b2f85c2c3c FOREIGN KEY (organization_id)
      REFERENCES organization (id_organization) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE password_expiration_policy OWNER TO sigmah;
-- default password expiration policy (with no automatic reset set)
INSERT INTO password_expiration_policy (id, policy_type, reset_for_new_users, organization_id) SELECT nextval('hibernate_sequence'), 'NEVER', false, MAX(id_organization) FROM organization;



-- Add column to store the last password change of an user. (issue #438 / #517)
-- ------
ALTER TABLE UserLogin ADD COLUMN last_password_change timestamp without time zone;



-- Add columns to store the start date of maintenance periods. (issue #419)
-- ------
ALTER TABLE flexible_element ADD COLUMN creation_date timestamp without time zone not null default current_timestamp;

ALTER TABLE project_model ADD COLUMN date_maintenance timestamp without time zone;
ALTER TABLE org_unit_model ADD COLUMN date_maintenance timestamp without time zone;

ALTER TABLE flexible_element ADD COLUMN is_disabled boolean default false;
ALTER TABLE question_choice_element ADD COLUMN is_disabled boolean default false;

-- Add column for the main site
-- ------
ALTER TABLE project ADD COLUMN mainsite int;

-- add column name in amendement (project core)

ALTER TABLE amendment ADD COLUMN name character varying(255);

-- Deletes all entries from the AdminEntity table which contains only admin entities from the Democratic Republic of the Congo 
-- ------

-- POSTGRESQL

DELETE FROM adminentity;


-- Updates existing users 'last password change' with current timestamp (issue #438 / #517)
-- ------
UPDATE UserLogin SET last_password_change = current_timestamp;



-- Updates existing global permissions to the new hierarchy of privileges (issue #616 and related)
-- ------
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'CREATE_TEST_PROJECT', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'REMOVE_PROJECT_FILE', p.id_profile FROM global_permission p WHERE p.permission='REMOVE_FILE';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'LOCK_PROJECT_CORE', p.id_profile FROM global_permission p WHERE p.permission='EDIT_PROJECT';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'VIEW_PROJECT_AGENDA', p.id_profile FROM global_permission p WHERE p.permission='VIEW_AGENDA';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'EDIT_PROJECT_AGENDA', p.id_profile FROM global_permission p WHERE p.permission='EDIT_AGENDA';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'RELATE_PROJECT', p.id_profile FROM global_permission p WHERE p.permission='EDIT_PROJECT';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'EDIT_ORG_UNIT', p.id_profile FROM global_permission p WHERE p.permission='MANAGE_UNIT';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'REMOVE_ORG_UNIT_FILE', p.id_profile FROM global_permission p WHERE p.permission='REMOVE_FILE';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'VIEW_ORG_UNIT_AGENDA', p.id_profile FROM global_permission p WHERE p.permission='VIEW_AGENDA';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'EDIT_ORG_UNIT_AGENDA', p.id_profile FROM global_permission p WHERE p.permission='EDIT_AGENDA';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_USERS', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_ORG_UNITS', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_PROJECT_MODELS', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_ORG_UNIT_MODELS', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_REPORT_MODELS', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_CATEGORIES', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_IMPORTATION_SCHEMES', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'MANAGE_SETTINGS', p.id_profile FROM global_permission p WHERE p.permission='VIEW_ADMIN';

DELETE FROM global_permission p WHERE p.permission='MANAGE_UNIT';
DELETE FROM global_permission p WHERE p.permission='REMOVE_FILE';
DELETE FROM global_permission p WHERE p.permission='MANAGE_USER';
DELETE FROM global_permission p WHERE p.permission='VIEW_AGENDA';
DELETE FROM global_permission p WHERE p.permission='EDIT_AGENDA';

--------
-- add importation sheme table
--------

CREATE TABLE importation_scheme
(
  sch_id bigint NOT NULL,
  datedeleted timestamp without time zone,
  sch_file_format character varying(255) NOT NULL,
  sch_first_row integer,
  sch_import_type character varying(255) NOT NULL,
  sch_name character varying(255) NOT NULL,
  sch_sheet_name character varying(255),
  CONSTRAINT importation_scheme_pkey PRIMARY KEY (sch_id )
);



CREATE TABLE importation_scheme_model
(
  sch_mod_id bigint NOT NULL,
  datedeleted timestamp without time zone,
  sch_id bigint NOT NULL,
  org_unit_model_id integer,
  id_project_model bigint,
  CONSTRAINT importation_scheme_model_pkey PRIMARY KEY (sch_mod_id ),
  CONSTRAINT fk_cgrmoq07kxyggtnldsvlwjqcs FOREIGN KEY (id_project_model)
      REFERENCES project_model (id_project_model) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ckwexvghil94ha4ct8b1wepxq FOREIGN KEY (org_unit_model_id)
      REFERENCES org_unit_model (org_unit_model_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_prpi3dmykj4nbdeyk3dhb51jn FOREIGN KEY (sch_id)
      REFERENCES importation_scheme (sch_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE importation_scheme_variable
(
  var_id bigint NOT NULL,
  datedeleted timestamp without time zone,
  var_name character varying(255) NOT NULL,
  var_reference character varying(255) NOT NULL,
  sch_id bigint NOT NULL,
  CONSTRAINT importation_scheme_variable_pkey PRIMARY KEY (var_id ),
  CONSTRAINT fk_khgoedwqkg3au5a2o3fe4g398 FOREIGN KEY (sch_id)
      REFERENCES importation_scheme (sch_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);



CREATE TABLE importation_scheme_variable_flexible_element
(
  var_fle_id bigint NOT NULL,
  datedeleted timestamp without time zone,
  var_fle_is_key boolean,
  id_flexible_element bigint NOT NULL,
  sch_mod_id bigint NOT NULL,
  var_id bigint,
  CONSTRAINT importation_scheme_variable_flexible_element_pkey PRIMARY KEY (var_fle_id ),
  CONSTRAINT fk_kr8tjw9mvseef9x0il6dojoh9 FOREIGN KEY (sch_mod_id)
      REFERENCES importation_scheme_model (sch_mod_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_nbgxlc3whl76ws07c99f70r6n FOREIGN KEY (var_id)
      REFERENCES importation_scheme_variable (var_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_q4la1it8wgg6nkosxi6rpd4cp FOREIGN KEY (id_flexible_element)
      REFERENCES flexible_element (id_flexible_element) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE importation_scheme_variable_budget_element
(
  var_fle_id bigint NOT NULL,
  CONSTRAINT importation_scheme_variable_budget_element_pkey PRIMARY KEY (var_fle_id ),
  CONSTRAINT fk_eu352p1mmft8pwwyylmwe63q8 FOREIGN KEY (var_fle_id)
      REFERENCES importation_scheme_variable_flexible_element (var_fle_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE importation_variable_budget_sub_field
(
  id_budget_sub_field bigint NOT NULL,
  var_id bigint NOT NULL,
  var_fle_id bigint NOT NULL,
  CONSTRAINT importation_variable_budget_sub_field_pkey PRIMARY KEY (id_budget_sub_field , var_id ),
  CONSTRAINT fk_dfeq1vnw6d3ooeqf1stt4x276 FOREIGN KEY (var_id)
      REFERENCES importation_scheme_variable (var_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ms0uq981iysge90gt1o3pf40q FOREIGN KEY (var_fle_id)
      REFERENCES importation_scheme_variable_budget_element (var_fle_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_og2mv36vlu4uu885yrpnhbl2q FOREIGN KEY (id_budget_sub_field)
      REFERENCES budget_sub_field (id_budget_sub_field) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
