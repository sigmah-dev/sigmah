SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET search_path = ${SCHEMA_POSTGRES};
DROP TABLE IF EXISTS value CASCADE;
DROP TABLE IF EXISTS userpermission CASCADE;
DROP TABLE IF EXISTS userlogin CASCADE;
DROP TABLE IF EXISTS userdatabase CASCADE;
DROP TABLE IF EXISTS user_unit_profiles CASCADE;
DROP TABLE IF EXISTS user_unit CASCADE;
DROP TABLE IF EXISTS triplets_list_element CASCADE;
DROP TABLE IF EXISTS triplet_value CASCADE;
DROP TABLE IF EXISTS textarea_element CASCADE;
DROP TABLE IF EXISTS site CASCADE;
DROP TABLE IF EXISTS richtextelement CASCADE;
DROP TABLE IF EXISTS reporttemplate CASCADE;
DROP TABLE IF EXISTS reportsubscription CASCADE;
DROP TABLE IF EXISTS reportingperiod CASCADE;
DROP TABLE IF EXISTS report_list_element CASCADE;
DROP TABLE IF EXISTS report_element CASCADE;
DROP TABLE IF EXISTS reminder_list CASCADE;
DROP TABLE IF EXISTS reminder CASCADE;
DROP TABLE IF EXISTS question_element CASCADE;
DROP TABLE IF EXISTS question_choice_element CASCADE;
DROP TABLE IF EXISTS quality_framework CASCADE;
DROP TABLE IF EXISTS quality_criterion_type CASCADE;
DROP TABLE IF EXISTS quality_criterion_children CASCADE;
DROP TABLE IF EXISTS quality_criterion CASCADE;
DROP TABLE IF EXISTS projectreportversion CASCADE;
DROP TABLE IF EXISTS projectreportmodelsection CASCADE;
DROP TABLE IF EXISTS projectreportmodel CASCADE;
DROP TABLE IF EXISTS projectreport CASCADE;
DROP TABLE IF EXISTS project_userlogin CASCADE;
DROP TABLE IF EXISTS project_model_visibility CASCADE;
DROP TABLE IF EXISTS project_model CASCADE;
DROP TABLE IF EXISTS project_funding CASCADE;
DROP TABLE IF EXISTS project_details CASCADE;
DROP TABLE IF EXISTS project_banner CASCADE;
DROP TABLE IF EXISTS project CASCADE;
DROP TABLE IF EXISTS profile CASCADE;
DROP TABLE IF EXISTS privacy_group_permission CASCADE;
DROP TABLE IF EXISTS privacy_group CASCADE;
DROP TABLE IF EXISTS phase_model_sucessors CASCADE;
DROP TABLE IF EXISTS phase_model_definition CASCADE;
DROP TABLE IF EXISTS phase_model CASCADE;
DROP TABLE IF EXISTS phase CASCADE;
DROP TABLE IF EXISTS personalevent CASCADE;
DROP TABLE IF EXISTS personalcalendar CASCADE;
DROP TABLE IF EXISTS partnerindatabase CASCADE;
DROP TABLE IF EXISTS partner CASCADE;
DROP TABLE IF EXISTS orgunitpermission CASCADE;
DROP TABLE IF EXISTS organization CASCADE;
DROP TABLE IF EXISTS org_unit_model CASCADE;
DROP TABLE IF EXISTS org_unit_details CASCADE;
DROP TABLE IF EXISTS org_unit_banner CASCADE;
DROP TABLE IF EXISTS monitored_point_list CASCADE;
DROP TABLE IF EXISTS monitored_point CASCADE;
DROP TABLE IF EXISTS message_element CASCADE;
DROP TABLE IF EXISTS log_frame_specific_objective CASCADE;
DROP TABLE IF EXISTS log_frame_prerequisite CASCADE;
DROP TABLE IF EXISTS log_frame_model CASCADE;
DROP TABLE IF EXISTS log_frame_indicators CASCADE;
DROP TABLE IF EXISTS log_frame_group CASCADE;
DROP TABLE IF EXISTS log_frame_expected_result CASCADE;
DROP TABLE IF EXISTS log_frame_element CASCADE;
DROP TABLE IF EXISTS log_frame_activity CASCADE;
DROP TABLE IF EXISTS log_frame CASCADE;
DROP TABLE IF EXISTS locationtype CASCADE;
DROP TABLE IF EXISTS locationadminlink CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS layout_group CASCADE;
DROP TABLE IF EXISTS layout_constraint CASCADE;
DROP TABLE IF EXISTS layout CASCADE;
DROP TABLE IF EXISTS keyquestion CASCADE;
DROP TABLE IF EXISTS indicatorvalue CASCADE;
DROP TABLE IF EXISTS indicators_list_value CASCADE;
DROP TABLE IF EXISTS indicators_list_element CASCADE;
DROP TABLE IF EXISTS indicator_labels CASCADE;
DROP TABLE IF EXISTS indicator_datasource CASCADE;
DROP TABLE IF EXISTS indicator CASCADE;
DROP TABLE IF EXISTS history_token CASCADE;
DROP SEQUENCE IF EXISTS hibernate_sequence CASCADE;
DROP TABLE IF EXISTS global_permission CASCADE;
DROP TABLE IF EXISTS flexible_element CASCADE;
DROP TABLE IF EXISTS files_list_element CASCADE;
DROP TABLE IF EXISTS file_version CASCADE;
DROP TABLE IF EXISTS file_meta CASCADE;
DROP TABLE IF EXISTS default_flexible_element CASCADE;
DROP TABLE IF EXISTS country CASCADE;
DROP TABLE IF EXISTS checkbox_element CASCADE;
DROP TABLE IF EXISTS category_type CASCADE;
DROP TABLE IF EXISTS category_element CASCADE;
DROP TABLE IF EXISTS budget_parts_list_value CASCADE;
DROP TABLE IF EXISTS budget_part CASCADE;
DROP TABLE IF EXISTS budget_distribution_element CASCADE;
DROP TABLE IF EXISTS budget CASCADE;
DROP TABLE IF EXISTS authentication CASCADE;
DROP TABLE IF EXISTS attributevalue CASCADE;
DROP TABLE IF EXISTS attributegroupinactivity CASCADE;
DROP TABLE IF EXISTS attributegroup CASCADE;
DROP TABLE IF EXISTS attribute CASCADE;
DROP TABLE IF EXISTS amendment_history_token CASCADE;
DROP TABLE IF EXISTS amendment CASCADE;
DROP TABLE IF EXISTS adminlevel CASCADE;
DROP TABLE IF EXISTS adminentity CASCADE;
DROP TABLE IF EXISTS activity CASCADE;
DROP EXTENSION plpgsql;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
SET search_path = ${SCHEMA_POSTGRES};
SET default_tablespace = '';
SET default_with_oids = false;
CREATE TABLE activity (
    activityid integer NOT NULL,
    allowedit boolean NOT NULL,
    isassessment boolean NOT NULL,
    category character varying(255),
    datedeleted timestamp without time zone,
    mapicon character varying(255),
    name character varying(45) NOT NULL,
    reportingfrequency integer NOT NULL,
    sortorder integer NOT NULL,
    databaseid integer NOT NULL,
    locationtypeid integer NOT NULL
);
CREATE TABLE adminentity (
    adminentityid integer NOT NULL,
    x1 double precision,
    x2 double precision,
    y1 double precision,
    y2 double precision,
    code character varying(15),
    name character varying(50) NOT NULL,
    soundex character varying(50),
    adminlevelid integer NOT NULL,
    adminentityparentid integer
);
CREATE TABLE adminlevel (
    adminlevelid integer NOT NULL,
    allowadd boolean NOT NULL,
    name character varying(30) NOT NULL,
    countryid integer NOT NULL,
    parentid integer
);
CREATE TABLE amendment (
    id_amendment integer NOT NULL,
    history_date timestamp without time zone,
    revision integer,
    status character varying(255),
    version integer,
    id_log_frame integer,
    id_project integer
);
CREATE TABLE amendment_history_token (
    amendment_id_amendment integer NOT NULL,
    values_id_history_token integer NOT NULL
);
CREATE TABLE attribute (
    attributeid integer NOT NULL,
    datedeleted timestamp without time zone,
    name character varying(50) NOT NULL,
    sortorder integer NOT NULL,
    attributegroupid integer NOT NULL
);
CREATE TABLE attributegroup (
    attributegroupid integer NOT NULL,
    category character varying(50),
    datedeleted timestamp without time zone,
    multipleallowed boolean NOT NULL,
    name character varying(255),
    sortorder integer NOT NULL
);
CREATE TABLE attributegroupinactivity (
    attributegroupid integer NOT NULL,
    activityid integer NOT NULL
);
CREATE TABLE attributevalue (
    attributeid integer NOT NULL,
    siteid integer NOT NULL,
    value boolean
);
CREATE TABLE authentication (
    authtoken character varying(32) NOT NULL,
    datecreated timestamp without time zone,
    datelastactive timestamp without time zone,
    userid integer NOT NULL
);

CREATE TABLE budget (
    id_budget bigint NOT NULL,
    total_amount real NOT NULL
);
CREATE TABLE budget_distribution_element (
    id_flexible_element bigint NOT NULL
);
CREATE TABLE budget_part (
    id_budget_part bigint NOT NULL,
    amount real NOT NULL,
    label character varying(2048) NOT NULL,
    id_budget_parts_list bigint NOT NULL
);
CREATE TABLE budget_parts_list_value (
    id_budget_parts_list bigint NOT NULL,
    id_budget bigint NOT NULL
);
CREATE TABLE category_element (
    id_category_element integer NOT NULL,
    color_hex character varying(6) NOT NULL,
    label text NOT NULL,
    id_organization integer,
    id_category_type integer NOT NULL
);
CREATE TABLE category_type (
    id_category_type integer NOT NULL,
    icon_name character varying(8192) NOT NULL,
    label character varying(8192) NOT NULL,
    id_organization integer
);
CREATE TABLE checkbox_element (
    id_flexible_element bigint NOT NULL
);
CREATE TABLE country (
    countryid integer NOT NULL,
    x1 double precision NOT NULL,
    x2 double precision NOT NULL,
    y1 double precision NOT NULL,
    y2 double precision NOT NULL,
    iso2 character varying(2),
    name character varying(50) NOT NULL
);
CREATE TABLE default_flexible_element (
    type character varying(255),
    id_flexible_element bigint NOT NULL
);
CREATE TABLE file_meta (
    id_file integer NOT NULL,
    datedeleted timestamp without time zone,
    name text NOT NULL
);
CREATE TABLE file_version (
    id_file_version integer NOT NULL,
    added_date timestamp without time zone NOT NULL,
    comments text,
    datedeleted timestamp without time zone,
    extension character varying(1024),
    name text NOT NULL,
    path text NOT NULL,
    size bigint NOT NULL,
    version_number integer NOT NULL,
    id_author integer NOT NULL,
    id_file integer NOT NULL
);
CREATE TABLE files_list_element (
    max_limit integer,
    id_flexible_element bigint NOT NULL
);
CREATE TABLE flexible_element (
    id_flexible_element bigint NOT NULL,
    amendable boolean NOT NULL,
    label text,
    validates boolean NOT NULL,
    id_privacy_group integer
);
CREATE TABLE global_permission (
    id_global_permission integer NOT NULL,
    permission character varying(255) NOT NULL,
    id_profile integer NOT NULL
);
CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
CREATE TABLE history_token (
    id_history_token integer NOT NULL,
    history_date timestamp without time zone NOT NULL,
    id_element bigint NOT NULL,
    id_project integer NOT NULL,
    change_type character varying(255),
    value text NOT NULL,
    id_user integer
);
CREATE TABLE indicator (
    indicatorid integer NOT NULL,
    aggregation integer NOT NULL,
    category character varying(50),
    listheader character varying(30),
    collectintervention boolean NOT NULL,
    collectmonitoring boolean NOT NULL,
    datedeleted timestamp without time zone,
    description text,
    directdataentryenabled boolean DEFAULT true NOT NULL,
    name character varying(128) NOT NULL,
    objective double precision,
    sortorder integer NOT NULL,
    sourceofverification text,
    units character varying(15),
    activityid integer,
    databaseid integer,
    id_quality_criterion integer
);
CREATE TABLE indicator_datasource (
    indicatorid integer NOT NULL,
    indicatorsourceid integer NOT NULL
);
CREATE TABLE indicator_labels (
    indicator_indicatorid integer NOT NULL,
    element character varying(255),
    code integer NOT NULL
);
CREATE TABLE indicators_list_element (
    id_flexible_element bigint NOT NULL
);
CREATE TABLE indicators_list_value (
    id_indicators_list bigint NOT NULL,
    id_indicator integer NOT NULL
);
CREATE TABLE indicatorvalue (
    indicatorid integer NOT NULL,
    reportingperiodid integer NOT NULL,
    value double precision NOT NULL
);
CREATE TABLE keyquestion (
    id integer NOT NULL,
    sort_order integer,
    label character varying(255),
    sectionid integer,
    qualitycriterion_id_quality_criterion integer
);
CREATE TABLE layout (
    id_layout bigint NOT NULL,
    columns_count integer NOT NULL,
    rows_count integer NOT NULL
);
CREATE TABLE layout_constraint (
    id_layout_constraint bigint NOT NULL,
    sort_order integer,
    id_flexible_element bigint NOT NULL,
    id_layout_group bigint NOT NULL
);
CREATE TABLE layout_group (
    id_layout_group bigint NOT NULL,
    column_index integer NOT NULL,
    row_index integer NOT NULL,
    title character varying(8192),
    id_layout bigint NOT NULL
);
CREATE TABLE location (
    locationid integer NOT NULL,
    axe character varying(50),
    datecreated timestamp without time zone,
    dateedited timestamp without time zone,
    locationguid character varying(36),
    name character varying(50) NOT NULL,
    x double precision,
    y double precision,
    locationtypeid integer NOT NULL
);
CREATE TABLE locationadminlink (
    adminentityid integer NOT NULL,
    locationid integer NOT NULL
);
CREATE TABLE locationtype (
    locationtypeid integer NOT NULL,
    name character varying(50) NOT NULL,
    reuse boolean NOT NULL,
    boundadminlevelid integer,
    countryid integer NOT NULL
);
CREATE TABLE log_frame (
    id_log_frame integer NOT NULL,
    main_objective text,
    id_log_frame_model integer NOT NULL,
    id_project integer
);
CREATE TABLE log_frame_activity (
    advancement integer,
    enddate timestamp without time zone,
    startdate timestamp without time zone,
    title text,
    id_element integer NOT NULL,
    id_result integer NOT NULL
);
CREATE TABLE log_frame_element (
    id_element integer NOT NULL,
    assumptions text,
    code integer NOT NULL,
    "position" integer,
    risks text,
    id_group integer
);
CREATE TABLE log_frame_expected_result (
    intervention_logic text,
    id_element integer NOT NULL,
    id_specific_objective integer NOT NULL
);
CREATE TABLE log_frame_group (
    id_group integer NOT NULL,
    label text,
    type character varying(255),
    id_log_frame integer NOT NULL
);
CREATE TABLE log_frame_indicators (
    log_frame_element_id_element integer NOT NULL,
    indicators_indicatorid integer NOT NULL
);
CREATE TABLE log_frame_model (
    id_log_frame integer NOT NULL,
    a_gp_max integer,
    a_max integer,
    a_per_er_max integer,
    a_per_gp_max integer,
    a_enable_groups boolean,
    er_enable_groups boolean,
    p_enable_groups boolean,
    so_enable_groups boolean,
    er_gp_max integer,
    er_max integer,
    er_per_gp_max integer,
    er_per_so_max integer,
    name character varying(8192) NOT NULL,
    p_gp_max integer,
    p_max integer,
    p_per_gp_max integer,
    so_gp_max integer,
    so_max integer,
    so_per_gp_max integer,
    id_project_model bigint
);
CREATE TABLE log_frame_prerequisite (
    id_prerequisite integer NOT NULL,
    code integer NOT NULL,
    content text,
    "position" integer,
    id_group integer,
    id_log_frame integer NOT NULL
);
CREATE TABLE log_frame_specific_objective (
    intervention_logic text,
    id_element integer NOT NULL,
    id_log_frame integer NOT NULL
);
CREATE TABLE message_element (
    id_flexible_element bigint NOT NULL
);
CREATE TABLE monitored_point (
    id_monitored_point integer NOT NULL,
    completion_date timestamp without time zone,
    deleted boolean,
    expected_date timestamp without time zone NOT NULL,
    label character varying(8192) NOT NULL,
    id_file integer,
    id_list integer NOT NULL
);
CREATE TABLE monitored_point_list (
    id_monitored_point_list integer NOT NULL
);
CREATE TABLE org_unit_banner (
    banner_id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_org_unit_model integer
);
CREATE TABLE org_unit_details (
    details_id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_org_unit_model integer
);
CREATE TABLE org_unit_model (
    org_unit_model_id integer NOT NULL,
    can_contain_projects boolean NOT NULL,
    has_budget boolean,
    name character varying(8192) NOT NULL,
    status character varying(255) NOT NULL,
    title character varying(8192) NOT NULL,
    id_organization integer
);
CREATE TABLE organization (
    id_organization integer NOT NULL,
    logo text,
    name text NOT NULL,
    id_root_org_unit integer
);
CREATE TABLE orgunitpermission (
    id integer NOT NULL,
    editall boolean NOT NULL,
    viewall boolean NOT NULL,
    unit_id integer,
    user_userid integer
);
CREATE TABLE partner (
    partnerid integer NOT NULL,
    calendarid integer,
    deleted timestamp without time zone,
    fullname character varying(64),
    name character varying(16) NOT NULL,
    planned_budget double precision,
    received_budget double precision,
    spend_budget double precision,
    location_locationid integer,
    office_country_id integer,
    id_org_unit_model integer,
    organization_id_organization integer,
    parent_partnerid integer
);
CREATE TABLE partnerindatabase (
    partnerid integer NOT NULL,
    databaseid integer NOT NULL
);
CREATE TABLE personalcalendar (
    id integer NOT NULL,
    name character varying(255)
);
CREATE TABLE personalevent (
    id integer NOT NULL,
    calendarid integer,
    datecreated timestamp without time zone,
    datedeleted timestamp without time zone,
    description character varying(255),
    enddate timestamp without time zone,
    startdate timestamp without time zone,
    summary character varying(255)
);
CREATE TABLE phase (
    id_phase bigint NOT NULL,
    end_date timestamp without time zone,
    start_date timestamp without time zone,
    id_phase_model bigint NOT NULL,
    id_project integer NOT NULL
);
CREATE TABLE phase_model (
    id_phase_model bigint NOT NULL,
    display_order integer,
    guide text,
    name character varying(8192) NOT NULL,
    definition_id integer,
    id_layout bigint,
    id_project_model bigint NOT NULL
);
CREATE TABLE phase_model_definition (
    id_phase_model_definition integer NOT NULL
);
CREATE TABLE phase_model_sucessors (
    id_phase_model bigint NOT NULL,
    id_phase_model_successor bigint NOT NULL
);
CREATE TABLE privacy_group (
    id_privacy_group integer NOT NULL,
    code integer NOT NULL,
    title character varying(8192) NOT NULL,
    id_organization integer
);
CREATE TABLE privacy_group_permission (
    id_permission integer NOT NULL,
    permission character varying(255) NOT NULL,
    id_privacy_group integer NOT NULL,
    id_profile integer NOT NULL
);
CREATE TABLE profile (
    id_profile integer NOT NULL,
    name character varying(8196) NOT NULL,
    id_organization integer
);
CREATE TABLE project (
    activity_advancement integer,
    amendment_revision integer,
    amendment_status character varying(255),
    amendment_version integer,
    calendarid integer,
    close_date date,
    end_date date,
    planned_budget double precision,
    received_budget double precision,
    spend_budget double precision,
    databaseid integer NOT NULL,
    id_current_phase bigint,
    id_manager integer,
    id_monitored_points_list integer,
    id_project_model bigint,
    id_reminder_list integer
);
CREATE TABLE project_banner (
    id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_project_model bigint
);
CREATE TABLE project_details (
    id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_project_model bigint
);
CREATE TABLE project_funding (
    id_funding integer NOT NULL,
    percentage double precision,
    id_project_funded integer NOT NULL,
    id_project_funding integer NOT NULL
);
CREATE TABLE project_model (
    id_project_model bigint NOT NULL,
    name character varying(8192) NOT NULL,
    status character varying(255) NOT NULL,
    id_root_phase_model bigint
);
CREATE TABLE project_model_visibility (
    id_visibility integer NOT NULL,
    type character varying(255),
    id_project_model bigint NOT NULL,
    id_organization integer NOT NULL
);
CREATE TABLE project_userlogin (
    project_databaseid integer NOT NULL,
    favoriteusers_userid integer NOT NULL
);
CREATE TABLE projectreport (
    id integer NOT NULL,
    datedeleted timestamp without time zone,
    name character varying(255),
    currentversion_id integer,
    flexibleelement_id_flexible_element bigint,
    model_id integer,
    orgunit_partnerid integer,
    project_databaseid integer
);
CREATE TABLE projectreportmodel (
    id integer NOT NULL,
    name character varying(255),
    id_organization integer
);
CREATE TABLE projectreportmodelsection (
    id integer NOT NULL,
    sort_order integer,
    name character varying(255),
    numberoftextarea integer,
    parentsectionmodelid integer,
    projectmodelid integer
);
CREATE TABLE projectreportversion (
    id integer NOT NULL,
    editdate timestamp without time zone,
    phasename character varying(255),
    version integer,
    editor_userid integer,
    report_id integer
);
CREATE TABLE quality_criterion (
    id_quality_criterion integer NOT NULL,
    code character varying(8192) NOT NULL,
    label text NOT NULL,
    id_organization integer,
    id_quality_framework integer
);
CREATE TABLE quality_criterion_children (
    id_quality_criterion integer,
    id_quality_criterion_child integer NOT NULL
);
CREATE TABLE quality_criterion_type (
    id_criterion_type integer NOT NULL,
    label character varying(8192) NOT NULL,
    level integer NOT NULL,
    id_quality_framework integer NOT NULL
);
CREATE TABLE quality_framework (
    id_quality_framework integer NOT NULL,
    label character varying(8192) NOT NULL,
    id_organization integer
);
CREATE TABLE question_choice_element (
    id_choice bigint NOT NULL,
    label character varying(8192) NOT NULL,
    sort_order integer,
    id_category_element integer,
    id_question bigint NOT NULL
);
CREATE TABLE question_element (
    is_multiple boolean,
    id_flexible_element bigint NOT NULL,
    id_category_type integer,
    id_quality_criterion integer
);
CREATE TABLE reminder (
    id_reminder integer NOT NULL,
    completion_date timestamp without time zone,
    deleted boolean,
    expected_date timestamp without time zone NOT NULL,
    label character varying(8192) NOT NULL,
    id_list integer NOT NULL
);
CREATE TABLE reminder_list (
    id_reminder_list integer NOT NULL
);
CREATE TABLE report_element (
    id_flexible_element bigint NOT NULL,
    model_id integer
);
CREATE TABLE report_list_element (
    id_flexible_element bigint NOT NULL,
    model_id integer
);
CREATE TABLE reportingperiod (
    reportingperiodid integer NOT NULL,
    comments text,
    date1 date NOT NULL,
    date2 date NOT NULL,
    datecreated timestamp without time zone NOT NULL,
    datedeleted timestamp without time zone,
    dateedited timestamp without time zone NOT NULL,
    monitoring boolean NOT NULL,
    siteid integer NOT NULL
);
CREATE TABLE reportsubscription (
    reporttemplateid integer NOT NULL,
    userid integer NOT NULL,
    subscribed boolean NOT NULL,
    invitinguserid integer
);
CREATE TABLE reporttemplate (
    reporttemplateid integer NOT NULL,
    datedeleted timestamp without time zone,
    day integer,
    description text,
    frequency character varying(255),
    title character varying(255),
    visibility integer,
    xml text NOT NULL,
    databaseid integer,
    owneruserid integer NOT NULL
);
CREATE TABLE richtextelement (
    id integer NOT NULL,
    sort_order integer,
    sectionid integer,
    text text,
    version_id integer
);
CREATE TABLE site (
    siteid integer NOT NULL,
    comments text,
    date1 date,
    date2 date,
    datecreated timestamp without time zone NOT NULL,
    datedeleted timestamp without time zone,
    dateedited timestamp without time zone NOT NULL,
    datesynchronized timestamp without time zone,
    siteguid character varying(36),
    status integer NOT NULL,
    target integer NOT NULL,
    activityid integer,
    assessmentsiteid integer,
    databaseid integer NOT NULL,
    locationid integer NOT NULL,
    partnerid integer NOT NULL
);
CREATE TABLE textarea_element (
    is_decimal boolean,
    length integer,
    max_value bigint,
    min_value bigint,
    type character(1),
    id_flexible_element bigint NOT NULL
);
CREATE TABLE triplet_value (
    id_triplet bigint NOT NULL,
    code text NOT NULL,
    datedeleted timestamp without time zone,
    name text NOT NULL,
    period text NOT NULL
);
CREATE TABLE triplets_list_element (
    id_flexible_element bigint NOT NULL
);
CREATE TABLE user_unit (
    id_user_unit integer NOT NULL,
    id_org_unit integer NOT NULL,
    id_user integer NOT NULL
);
CREATE TABLE user_unit_profiles (
    id_user_unit integer NOT NULL,
    id_profile integer NOT NULL
);
CREATE TABLE userdatabase (
    databaseid integer NOT NULL,
    datedeleted timestamp without time zone,
    fullname character varying(500),
    lastschemaupdate timestamp without time zone NOT NULL,
    name character varying(16) NOT NULL,
    startdate date,
    countryid integer NOT NULL,
    owneruserid integer NOT NULL
);
CREATE TABLE userlogin (
    userid integer NOT NULL,
    active boolean,
    changepasswordkey character varying(34),
    datechangepasswordkeyissued timestamp without time zone,
    email character varying(75) NOT NULL,
    firstname character varying(50),
    password character varying(150),
    locale character varying(10) NOT NULL,
    name character varying(50) NOT NULL,
    newuser boolean NOT NULL,
    id_organization integer
);
CREATE TABLE userpermission (
    userpermissionid integer NOT NULL,
    allowdesign boolean NOT NULL,
    allowedit boolean NOT NULL,
    alloweditall boolean NOT NULL,
    allowmanageallusers boolean NOT NULL,
    allowmanageusers boolean NOT NULL,
    allowview boolean NOT NULL,
    allowviewall boolean NOT NULL,
    lastschemaupdate timestamp without time zone,
    databaseid integer NOT NULL,
    partnerid integer NOT NULL,
    userid integer NOT NULL
);
CREATE TABLE value (
    id_value bigint NOT NULL,
    id_project integer NOT NULL,
    action_last_modif character(1) NOT NULL,
    date_last_modif timestamp without time zone NOT NULL,
    value text,
    id_flexible_element bigint NOT NULL,
    id_user_last_modif integer NOT NULL
);