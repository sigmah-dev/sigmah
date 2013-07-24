--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.1
-- Dumped by pg_dump version 9.1.1
-- Started on 2012-09-25 15:33:23

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 264 (class 3079 OID 11638)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2673 (class 0 OID 0)
-- Dependencies: 264
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_with_oids = false;

--
-- TOC entry 161 (class 1259 OID 65343)
-- Dependencies: 5
-- Name: activity; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 162 (class 1259 OID 65349)
-- Dependencies: 5
-- Name: adminentity; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 163 (class 1259 OID 65352)
-- Dependencies: 5
-- Name: adminlevel; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE adminlevel (
    adminlevelid integer NOT NULL,
    allowadd boolean NOT NULL,
    name character varying(30) NOT NULL,
    countryid integer NOT NULL,
    parentid integer
);


--
-- TOC entry 164 (class 1259 OID 65355)
-- Dependencies: 5
-- Name: amendment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE amendment (
    id_amendment integer NOT NULL,
    history_date timestamp without time zone,
    revision integer,
    status character varying(255),
    version integer,
    id_log_frame integer,
    id_project integer
);


--
-- TOC entry 165 (class 1259 OID 65358)
-- Dependencies: 5
-- Name: amendment_history_token; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE amendment_history_token (
    amendment_id_amendment integer NOT NULL,
    values_id_history_token integer NOT NULL
);


--
-- TOC entry 166 (class 1259 OID 65361)
-- Dependencies: 5
-- Name: attribute; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE attribute (
    attributeid integer NOT NULL,
    datedeleted timestamp without time zone,
    name character varying(50) NOT NULL,
    sortorder integer NOT NULL,
    attributegroupid integer NOT NULL
);


--
-- TOC entry 167 (class 1259 OID 65364)
-- Dependencies: 5
-- Name: attributegroup; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE attributegroup (
    attributegroupid integer NOT NULL,
    category character varying(50),
    datedeleted timestamp without time zone,
    multipleallowed boolean NOT NULL,
    name character varying(255),
    sortorder integer NOT NULL
);


--
-- TOC entry 168 (class 1259 OID 65367)
-- Dependencies: 5
-- Name: attributegroupinactivity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE attributegroupinactivity (
    attributegroupid integer NOT NULL,
    activityid integer NOT NULL
);


--
-- TOC entry 169 (class 1259 OID 65370)
-- Dependencies: 5
-- Name: attributevalue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE attributevalue (
    attributeid integer NOT NULL,
    siteid integer NOT NULL,
    value boolean
);


--
-- TOC entry 170 (class 1259 OID 65373)
-- Dependencies: 5
-- Name: authentication; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE authentication (
    authtoken character varying(32) NOT NULL,
    datecreated timestamp without time zone,
    datelastactive timestamp without time zone,
    userid integer NOT NULL
);


--
-- TOC entry 171 (class 1259 OID 65376)
-- Dependencies: 5
-- Name: budget; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget (
    id_budget bigint NOT NULL,
    total_amount real NOT NULL
);


--
-- TOC entry 172 (class 1259 OID 65379)
-- Dependencies: 5
-- Name: budget_distribution_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget_distribution_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 173 (class 1259 OID 65382)
-- Dependencies: 5
-- Name: budget_part; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget_part (
    id_budget_part bigint NOT NULL,
    amount real NOT NULL,
    label character varying(2048) NOT NULL,
    id_budget_parts_list bigint NOT NULL
);


--
-- TOC entry 174 (class 1259 OID 65388)
-- Dependencies: 5
-- Name: budget_parts_list_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget_parts_list_value (
    id_budget_parts_list bigint NOT NULL,
    id_budget bigint NOT NULL
);


--
-- TOC entry 175 (class 1259 OID 65391)
-- Dependencies: 5
-- Name: category_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE category_element (
    id_category_element integer NOT NULL,
    color_hex character varying(6) NOT NULL,
    label text NOT NULL,
    id_organization integer,
    id_category_type integer NOT NULL
);


--
-- TOC entry 176 (class 1259 OID 65397)
-- Dependencies: 5
-- Name: category_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE category_type (
    id_category_type integer NOT NULL,
    icon_name character varying(8192) NOT NULL,
    label character varying(8192) NOT NULL,
    id_organization integer
);


--
-- TOC entry 177 (class 1259 OID 65403)
-- Dependencies: 5
-- Name: checkbox_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE checkbox_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 178 (class 1259 OID 65406)
-- Dependencies: 5
-- Name: country; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE country (
    countryid integer NOT NULL,
    x1 double precision NOT NULL,
    x2 double precision NOT NULL,
    y1 double precision NOT NULL,
    y2 double precision NOT NULL,
    iso2 character varying(2),
    name character varying(50) NOT NULL
);


--
-- TOC entry 179 (class 1259 OID 65409)
-- Dependencies: 5
-- Name: default_flexible_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE default_flexible_element (
    type character varying(255),
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 180 (class 1259 OID 65412)
-- Dependencies: 5
-- Name: file_meta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE file_meta (
    id_file integer NOT NULL,
    datedeleted timestamp without time zone,
    name text NOT NULL
);


--
-- TOC entry 181 (class 1259 OID 65418)
-- Dependencies: 5
-- Name: file_version; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 182 (class 1259 OID 65424)
-- Dependencies: 5
-- Name: files_list_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE files_list_element (
    max_limit integer,
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 183 (class 1259 OID 65427)
-- Dependencies: 5
-- Name: flexible_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE flexible_element (
    id_flexible_element bigint NOT NULL,
    amendable boolean NOT NULL,
    label text,
    validates boolean NOT NULL,
    id_privacy_group integer,
    exportable boolean DEFAULT true NOT NULL,
    globally_exportable boolean DEFAULT true NOT NULL
);

--
-- Dependencies: 5
-- Name: global_export; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE global_export (
    id bigint NOT NULL,
    generated_date timestamp without time zone NOT NULL,
    organization_id integer NOT NULL
);


--
-- Dependencies: 5
-- Name: global_export_content; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE global_export_content (
    id bigint NOT NULL,
    csv_content text,
    project_model_name character varying(8192) NOT NULL,
    global_export_id bigint NOT NULL
);


--
-- Dependencies: 5
-- Name: global_export_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE global_export_settings (
    id bigint NOT NULL,
    auto_delete_frequency integer,
    auto_export_frequency integer,
    default_organization_export_format character varying(255),
    export_format character varying(255),
    last_export_date timestamp without time zone,
    locale_string character varying(4) NOT NULL,
    organization_id integer NOT NULL
);

--
-- TOC entry 184 (class 1259 OID 65433)
-- Dependencies: 5
-- Name: global_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE global_permission (
    id_global_permission integer NOT NULL,
    permission character varying(255) NOT NULL,
    id_profile integer NOT NULL
);


--
-- TOC entry 185 (class 1259 OID 65436)
-- Dependencies: 5
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2674 (class 0 OID 0)
-- Dependencies: 185
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('hibernate_sequence', 1, false);


--
-- TOC entry 186 (class 1259 OID 65438)
-- Dependencies: 5
-- Name: history_token; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE history_token (
    id_history_token integer NOT NULL,
    history_date timestamp without time zone NOT NULL,
    id_element bigint NOT NULL,
    id_project integer NOT NULL,
    change_type character varying(255),
    value text NOT NULL,
    id_user integer
);


--
-- TOC entry 187 (class 1259 OID 65444)
-- Dependencies: 2188 5
-- Name: indicator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicator (
    indicatorid integer NOT NULL,
    aggregation integer NOT NULL,
    category character varying(1024),
    listheader character varying(30),
    collectintervention boolean NOT NULL,
    collectmonitoring boolean NOT NULL,
    datedeleted timestamp without time zone,
    description text,
    name character varying(1024) NOT NULL,
    objective double precision,
    sortorder integer NOT NULL,
    units character varying(15),
    activityid integer,
    databaseid integer,
    id_quality_criterion integer,
    sourceofverification text,
    directdataentryenabled boolean DEFAULT true NOT NULL
);


--
-- TOC entry 188 (class 1259 OID 65451)
-- Dependencies: 5
-- Name: indicator_datasource; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicator_datasource (
    indicatorid integer NOT NULL,
    indicatorsourceid integer NOT NULL
);


--
-- TOC entry 189 (class 1259 OID 65454)
-- Dependencies: 5
-- Name: indicator_labels; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicator_labels (
    indicator_indicatorid integer NOT NULL,
    element character varying(255),
    code integer NOT NULL
);


--
-- TOC entry 190 (class 1259 OID 65457)
-- Dependencies: 5
-- Name: indicatordatasource; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicatordatasource (
    indicatorid integer NOT NULL,
    indicatorsourceid integer NOT NULL
);


--
-- TOC entry 191 (class 1259 OID 65460)
-- Dependencies: 5
-- Name: indicators_list_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicators_list_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 192 (class 1259 OID 65463)
-- Dependencies: 5
-- Name: indicators_list_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicators_list_value (
    id_indicators_list bigint NOT NULL,
    id_indicator integer NOT NULL
);


--
-- TOC entry 193 (class 1259 OID 65466)
-- Dependencies: 5
-- Name: indicatorvalue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicatorvalue (
    indicatorid integer NOT NULL,
    reportingperiodid integer NOT NULL,
    value double precision NOT NULL
);


--
-- TOC entry 194 (class 1259 OID 65469)
-- Dependencies: 5
-- Name: keyquestion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE keyquestion (
    id integer NOT NULL,
    sort_order integer,
    label character varying(255),
    sectionid integer,
    qualitycriterion_id_quality_criterion integer
);


--
-- TOC entry 195 (class 1259 OID 65472)
-- Dependencies: 5
-- Name: layout; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE layout (
    id_layout bigint NOT NULL,
    columns_count integer NOT NULL,
    rows_count integer NOT NULL
);


--
-- TOC entry 196 (class 1259 OID 65475)
-- Dependencies: 5
-- Name: layout_constraint; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE layout_constraint (
    id_layout_constraint bigint NOT NULL,
    sort_order integer,
    id_flexible_element bigint NOT NULL,
    id_layout_group bigint NOT NULL
);


--
-- TOC entry 197 (class 1259 OID 65478)
-- Dependencies: 5
-- Name: layout_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE layout_group (
    id_layout_group bigint NOT NULL,
    column_index integer NOT NULL,
    row_index integer NOT NULL,
    title character varying(8192),
    id_layout bigint NOT NULL
);


--
-- TOC entry 198 (class 1259 OID 65484)
-- Dependencies: 5
-- Name: location; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 199 (class 1259 OID 65487)
-- Dependencies: 5
-- Name: locationadminlink; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE locationadminlink (
    adminentityid integer NOT NULL,
    locationid integer NOT NULL
);


--
-- TOC entry 200 (class 1259 OID 65490)
-- Dependencies: 5
-- Name: locationtype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE locationtype (
    locationtypeid integer NOT NULL,
    name character varying(50) NOT NULL,
    reuse boolean NOT NULL,
    boundadminlevelid integer,
    countryid integer NOT NULL
);


--
-- TOC entry 201 (class 1259 OID 65493)
-- Dependencies: 5
-- Name: log_frame; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame (
    id_log_frame integer NOT NULL,
    main_objective text,
    id_log_frame_model integer NOT NULL,
    id_project integer
);


--
-- TOC entry 202 (class 1259 OID 65499)
-- Dependencies: 5
-- Name: log_frame_activity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_activity (
    advancement integer,
    enddate timestamp without time zone,
    startdate timestamp without time zone,
    title text,
    id_element integer NOT NULL,
    id_result integer NOT NULL
);


--
-- TOC entry 203 (class 1259 OID 65505)
-- Dependencies: 5
-- Name: log_frame_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_element (
    id_element integer NOT NULL,
    code integer NOT NULL,
    "position" integer,
    id_group integer,
    risksandassumptions text
);


--
-- TOC entry 204 (class 1259 OID 65511)
-- Dependencies: 5
-- Name: log_frame_expected_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_expected_result (
    intervention_logic text,
    id_element integer NOT NULL,
    id_specific_objective integer NOT NULL
);


--
-- TOC entry 205 (class 1259 OID 65517)
-- Dependencies: 5
-- Name: log_frame_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_group (
    id_group integer NOT NULL,
    label text,
    type character varying(255),
    id_log_frame integer NOT NULL
);


--
-- TOC entry 206 (class 1259 OID 65523)
-- Dependencies: 5
-- Name: log_frame_indicators; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_indicators (
    log_frame_element_id_element integer NOT NULL,
    indicators_indicatorid integer NOT NULL
);


--
-- TOC entry 207 (class 1259 OID 65526)
-- Dependencies: 5
-- Name: log_frame_model; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 208 (class 1259 OID 65532)
-- Dependencies: 5
-- Name: log_frame_prerequisite; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_prerequisite (
    id_prerequisite integer NOT NULL,
    code integer NOT NULL,
    content text,
    "position" integer,
    id_group integer,
    id_log_frame integer NOT NULL
);


--
-- TOC entry 209 (class 1259 OID 65538)
-- Dependencies: 5
-- Name: log_frame_specific_objective; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_specific_objective (
    intervention_logic text,
    id_element integer NOT NULL,
    id_log_frame integer NOT NULL
);


--
-- TOC entry 210 (class 1259 OID 65544)
-- Dependencies: 5
-- Name: message_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE message_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 211 (class 1259 OID 65547)
-- Dependencies: 5
-- Name: monitored_point; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE monitored_point (
    id_monitored_point integer NOT NULL,
    completion_date timestamp without time zone,
    deleted boolean NOT NULL,
    expected_date timestamp without time zone NOT NULL,
    label character varying(8192) NOT NULL,
    id_file integer,
    id_list integer NOT NULL
);


--
-- TOC entry 212 (class 1259 OID 65553)
-- Dependencies: 5
-- Name: monitored_point_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE monitored_point_list (
    id_monitored_point_list integer NOT NULL
);


--
-- TOC entry 213 (class 1259 OID 65556)
-- Dependencies: 5
-- Name: org_unit_banner; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE org_unit_banner (
    banner_id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_org_unit_model integer
);


--
-- TOC entry 214 (class 1259 OID 65559)
-- Dependencies: 5
-- Name: org_unit_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE org_unit_details (
    details_id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_org_unit_model integer
);


--
-- TOC entry 215 (class 1259 OID 65562)
-- Dependencies: 5
-- Name: org_unit_model; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE org_unit_model (
    org_unit_model_id integer NOT NULL,
    can_contain_projects boolean NOT NULL,
    has_budget boolean,
    name character varying(8192) NOT NULL,
    status character varying(255) NOT NULL,
    title character varying(8192) NOT NULL,
    id_organization integer,
    date_deleted date
);


--
-- TOC entry 216 (class 1259 OID 65568)
-- Dependencies: 5
-- Name: organization; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE organization (
    id_organization integer NOT NULL,
    logo text,
    name text NOT NULL,
    id_root_org_unit integer
);


--
-- TOC entry 217 (class 1259 OID 65574)
-- Dependencies: 5
-- Name: orgunitpermission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE orgunitpermission (
    id integer NOT NULL,
    editall boolean NOT NULL,
    viewall boolean NOT NULL,
    unit_id integer,
    user_userid integer
);


--
-- TOC entry 218 (class 1259 OID 65577)
-- Dependencies: 5
-- Name: partner; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 219 (class 1259 OID 65580)
-- Dependencies: 5
-- Name: partnerindatabase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE partnerindatabase (
    partnerid integer NOT NULL,
    databaseid integer NOT NULL
);


--
-- TOC entry 220 (class 1259 OID 65583)
-- Dependencies: 5
-- Name: personalcalendar; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE personalcalendar (
    id integer NOT NULL,
    name character varying(255)
);


--
-- TOC entry 221 (class 1259 OID 65586)
-- Dependencies: 5
-- Name: personalevent; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE personalevent (
    id integer NOT NULL,
    calendarid integer,
    datecreated timestamp without time zone,
    datedeleted timestamp without time zone,
    description character varying(255),
    enddate timestamp with time zone,
    startdate timestamp with time zone,
    summary character varying(255)
);


--
-- TOC entry 222 (class 1259 OID 65592)
-- Dependencies: 5
-- Name: phase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE phase (
    id_phase bigint NOT NULL,
    end_date timestamp without time zone,
    start_date timestamp without time zone,
    id_phase_model bigint NOT NULL,
    id_project integer NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 65595)
-- Dependencies: 5
-- Name: phase_model; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE phase_model (
    id_phase_model bigint NOT NULL,
    display_order integer,
    guide text,
    name character varying(8192) NOT NULL,
    definition_id integer,
    id_layout bigint,
    id_project_model bigint NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 65601)
-- Dependencies: 5
-- Name: phase_model_definition; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE phase_model_definition (
    id_phase_model_definition integer NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 65604)
-- Dependencies: 5
-- Name: phase_model_sucessors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE phase_model_sucessors (
    id_phase_model bigint NOT NULL,
    id_phase_model_successor bigint NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 65607)
-- Dependencies: 5
-- Name: privacy_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE privacy_group (
    id_privacy_group integer NOT NULL,
    code integer NOT NULL,
    title character varying(8192) NOT NULL,
    id_organization integer
);


--
-- TOC entry 227 (class 1259 OID 65613)
-- Dependencies: 5
-- Name: privacy_group_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE privacy_group_permission (
    id_permission integer NOT NULL,
    permission character varying(255) NOT NULL,
    id_privacy_group integer NOT NULL,
    id_profile integer NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 65616)
-- Dependencies: 5
-- Name: profile; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE profile (
    id_profile integer NOT NULL,
    name character varying(8196) NOT NULL,
    id_organization integer
);


--
-- TOC entry 229 (class 1259 OID 65622)
-- Dependencies: 5
-- Name: project; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project (
    activity_advancement integer,
    amendment_revision integer,
    amendment_status character varying(255),
    amendment_version integer,
    calendarid integer,
    close_date timestamp without time zone,
    end_date timestamp without time zone,
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


--
-- TOC entry 230 (class 1259 OID 65625)
-- Dependencies: 5
-- Name: project_banner; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_banner (
    id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_project_model bigint
);


--
-- TOC entry 231 (class 1259 OID 65628)
-- Dependencies: 5
-- Name: project_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_details (
    id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_project_model bigint
);


--
-- TOC entry 232 (class 1259 OID 65631)
-- Dependencies: 5
-- Name: project_funding; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_funding (
    id_funding integer NOT NULL,
    percentage double precision,
    id_project_funded integer NOT NULL,
    id_project_funding integer NOT NULL
);


--
-- TOC entry 233 (class 1259 OID 65634)
-- Dependencies: 5
-- Name: project_model; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_model (
    id_project_model bigint NOT NULL,
    name character varying(8192) NOT NULL,
    status character varying(255) NOT NULL,
    id_root_phase_model bigint,
    date_deleted date
);


--
-- TOC entry 234 (class 1259 OID 65640)
-- Dependencies: 5
-- Name: project_model_visibility; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_model_visibility (
    id_visibility integer NOT NULL,
    type character varying(255),
    id_project_model bigint NOT NULL,
    id_organization integer NOT NULL
);


--
-- TOC entry 235 (class 1259 OID 65643)
-- Dependencies: 5
-- Name: project_userlogin; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_userlogin (
    project_databaseid integer NOT NULL,
    favoriteusers_userid integer NOT NULL
);


--
-- TOC entry 236 (class 1259 OID 65646)
-- Dependencies: 5
-- Name: projectreport; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 237 (class 1259 OID 65649)
-- Dependencies: 5
-- Name: projectreportmodel; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE projectreportmodel (
    id integer NOT NULL,
    name character varying(255),
    id_organization integer
);


--
-- TOC entry 238 (class 1259 OID 65652)
-- Dependencies: 5
-- Name: projectreportmodelsection; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE projectreportmodelsection (
    id integer NOT NULL,
    sort_order integer,
    name character varying(255),
    numberoftextarea integer,
    parentsectionmodelid integer,
    projectmodelid integer
);


--
-- TOC entry 239 (class 1259 OID 65655)
-- Dependencies: 5
-- Name: projectreportversion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE projectreportversion (
    id integer NOT NULL,
    editdate timestamp without time zone,
    phasename character varying(255),
    version integer,
    editor_userid integer,
    report_id integer
);


--
-- TOC entry 240 (class 1259 OID 65658)
-- Dependencies: 5
-- Name: quality_criterion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE quality_criterion (
    id_quality_criterion integer NOT NULL,
    code character varying(8192) NOT NULL,
    label text NOT NULL,
    id_organization integer,
    id_quality_framework integer
);


--
-- TOC entry 241 (class 1259 OID 65664)
-- Dependencies: 5
-- Name: quality_criterion_children; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE quality_criterion_children (
    id_quality_criterion integer,
    id_quality_criterion_child integer NOT NULL
);


--
-- TOC entry 242 (class 1259 OID 65667)
-- Dependencies: 5
-- Name: quality_criterion_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE quality_criterion_type (
    id_criterion_type integer NOT NULL,
    label character varying(8192) NOT NULL,
    level integer NOT NULL,
    id_quality_framework integer NOT NULL
);


--
-- TOC entry 243 (class 1259 OID 65673)
-- Dependencies: 5
-- Name: quality_framework; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE quality_framework (
    id_quality_framework integer NOT NULL,
    label character varying(8192) NOT NULL,
    id_organization integer
);


--
-- TOC entry 244 (class 1259 OID 65679)
-- Dependencies: 5
-- Name: question_choice_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE question_choice_element (
    id_choice bigint NOT NULL,
    label character varying(8192) NOT NULL,
    sort_order integer,
    id_category_element integer,
    id_question bigint NOT NULL
);


--
-- TOC entry 245 (class 1259 OID 65685)
-- Dependencies: 5
-- Name: question_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE question_element (
    is_multiple boolean,
    id_flexible_element bigint NOT NULL,
    id_category_type integer,
    id_quality_criterion integer
);


--
-- TOC entry 246 (class 1259 OID 65688)
-- Dependencies: 5
-- Name: reminder; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE reminder (
    id_reminder integer NOT NULL,
    completion_date timestamp without time zone,
    deleted boolean NOT NULL,
    expected_date timestamp without time zone NOT NULL,
    label character varying(8192) NOT NULL,
    id_list integer NOT NULL
);


--
-- TOC entry 247 (class 1259 OID 65694)
-- Dependencies: 5
-- Name: reminder_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE reminder_list (
    id_reminder_list integer NOT NULL
);


--
-- TOC entry 248 (class 1259 OID 65697)
-- Dependencies: 5
-- Name: report_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE report_element (
    model_id integer,
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 249 (class 1259 OID 65700)
-- Dependencies: 5
-- Name: report_list_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE report_list_element (
    model_id integer,
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 250 (class 1259 OID 65703)
-- Dependencies: 5
-- Name: reportingperiod; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 251 (class 1259 OID 65709)
-- Dependencies: 5
-- Name: reportsubscription; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE reportsubscription (
    reporttemplateid integer NOT NULL,
    userid integer NOT NULL,
    subscribed boolean NOT NULL,
    invitinguserid integer
);


--
-- TOC entry 252 (class 1259 OID 65712)
-- Dependencies: 5
-- Name: reporttemplate; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 253 (class 1259 OID 65718)
-- Dependencies: 5
-- Name: richtextelement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE richtextelement (
    id integer NOT NULL,
    sort_order integer,
    sectionid integer,
    text text,
    version_id integer
);


--
-- TOC entry 254 (class 1259 OID 65724)
-- Dependencies: 5
-- Name: site; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 255 (class 1259 OID 65730)
-- Dependencies: 5
-- Name: textarea_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE textarea_element (
    is_decimal boolean,
    length integer,
    max_value bigint,
    min_value bigint,
    type character(1),
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 256 (class 1259 OID 65733)
-- Dependencies: 5
-- Name: triplet_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE triplet_value (
    id_triplet bigint NOT NULL,
    code text NOT NULL,
    datedeleted timestamp without time zone,
    name text NOT NULL,
    period text NOT NULL
);


--
-- TOC entry 257 (class 1259 OID 65739)
-- Dependencies: 5
-- Name: triplets_list_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE triplets_list_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 258 (class 1259 OID 65742)
-- Dependencies: 5
-- Name: user_unit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE user_unit (
    id_user_unit integer NOT NULL,
    id_org_unit integer NOT NULL,
    id_user integer NOT NULL
);


--
-- TOC entry 259 (class 1259 OID 65745)
-- Dependencies: 5
-- Name: user_unit_profiles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE user_unit_profiles (
    id_user_unit integer NOT NULL,
    id_profile integer NOT NULL
);


--
-- TOC entry 260 (class 1259 OID 65748)
-- Dependencies: 5
-- Name: userdatabase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE userdatabase (
    databaseid integer NOT NULL,
    datedeleted timestamp without time zone,
    fullname character varying(500),
    lastschemaupdate timestamp without time zone NOT NULL,
    name character varying(50) NOT NULL,
    startdate timestamp without time zone,
    countryid integer NOT NULL,
    owneruserid integer NOT NULL
);


--
-- TOC entry 261 (class 1259 OID 65754)
-- Dependencies: 5
-- Name: userlogin; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 262 (class 1259 OID 65757)
-- Dependencies: 5
-- Name: userpermission; Type: TABLE; Schema: public; Owner: -
--

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


--
-- TOC entry 263 (class 1259 OID 65760)
-- Dependencies: 5
-- Name: value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE value (
    id_value bigint NOT NULL,
    id_project integer NOT NULL,
    action_last_modif character(1) NOT NULL,
    date_last_modif timestamp without time zone NOT NULL,
    value text,
    id_flexible_element bigint NOT NULL,
    id_user_last_modif integer NOT NULL
);



--
-- TOC entry 2190 (class 2606 OID 65767)
-- Dependencies: 161 161
-- Name: activity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT activity_pkey PRIMARY KEY (activityid);


--
-- TOC entry 2192 (class 2606 OID 65769)
-- Dependencies: 162 162
-- Name: adminentity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminentity
    ADD CONSTRAINT adminentity_pkey PRIMARY KEY (adminentityid);


--
-- TOC entry 2194 (class 2606 OID 65771)
-- Dependencies: 163 163
-- Name: adminlevel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT adminlevel_pkey PRIMARY KEY (adminlevelid);


--
-- TOC entry 2196 (class 2606 OID 65773)
-- Dependencies: 164 164
-- Name: amendment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment
    ADD CONSTRAINT amendment_pkey PRIMARY KEY (id_amendment);


--
-- TOC entry 2198 (class 2606 OID 65775)
-- Dependencies: 166 166
-- Name: attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attribute
    ADD CONSTRAINT attribute_pkey PRIMARY KEY (attributeid);


--
-- TOC entry 2200 (class 2606 OID 65777)
-- Dependencies: 167 167
-- Name: attributegroup_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributegroup
    ADD CONSTRAINT attributegroup_pkey PRIMARY KEY (attributegroupid);


--
-- TOC entry 2202 (class 2606 OID 65779)
-- Dependencies: 168 168 168
-- Name: attributegroupinactivity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT attributegroupinactivity_pkey PRIMARY KEY (activityid, attributegroupid);


--
-- TOC entry 2204 (class 2606 OID 65781)
-- Dependencies: 169 169 169
-- Name: attributevalue_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT attributevalue_pkey PRIMARY KEY (attributeid, siteid);


--
-- TOC entry 2206 (class 2606 OID 65783)
-- Dependencies: 170 170
-- Name: authentication_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authentication
    ADD CONSTRAINT authentication_pkey PRIMARY KEY (authtoken);


--
-- TOC entry 2210 (class 2606 OID 65785)
-- Dependencies: 172 172
-- Name: budget_distribution_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_distribution_element
    ADD CONSTRAINT budget_distribution_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2212 (class 2606 OID 65787)
-- Dependencies: 173 173
-- Name: budget_part_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_part
    ADD CONSTRAINT budget_part_pkey PRIMARY KEY (id_budget_part);


--
-- TOC entry 2214 (class 2606 OID 65789)
-- Dependencies: 174 174
-- Name: budget_parts_list_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_parts_list_value
    ADD CONSTRAINT budget_parts_list_value_pkey PRIMARY KEY (id_budget_parts_list);


--
-- TOC entry 2208 (class 2606 OID 65791)
-- Dependencies: 171 171
-- Name: budget_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget
    ADD CONSTRAINT budget_pkey PRIMARY KEY (id_budget);


--
-- TOC entry 2216 (class 2606 OID 65793)
-- Dependencies: 175 175
-- Name: category_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_element
    ADD CONSTRAINT category_element_pkey PRIMARY KEY (id_category_element);


--
-- TOC entry 2218 (class 2606 OID 65795)
-- Dependencies: 176 176
-- Name: category_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_type
    ADD CONSTRAINT category_type_pkey PRIMARY KEY (id_category_type);


--
-- TOC entry 2220 (class 2606 OID 65797)
-- Dependencies: 177 177
-- Name: checkbox_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY checkbox_element
    ADD CONSTRAINT checkbox_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2222 (class 2606 OID 65799)
-- Dependencies: 178 178
-- Name: country_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_pkey PRIMARY KEY (countryid);


--
-- TOC entry 2224 (class 2606 OID 65801)
-- Dependencies: 179 179
-- Name: default_flexible_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY default_flexible_element
    ADD CONSTRAINT default_flexible_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2226 (class 2606 OID 65803)
-- Dependencies: 180 180
-- Name: file_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_meta
    ADD CONSTRAINT file_meta_pkey PRIMARY KEY (id_file);


--
-- TOC entry 2228 (class 2606 OID 65805)
-- Dependencies: 181 181 181
-- Name: file_version_id_file_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_version
    ADD CONSTRAINT file_version_id_file_key UNIQUE (id_file, version_number);


--
-- TOC entry 2230 (class 2606 OID 65807)
-- Dependencies: 181 181
-- Name: file_version_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_version
    ADD CONSTRAINT file_version_pkey PRIMARY KEY (id_file_version);


--
-- TOC entry 2232 (class 2606 OID 65809)
-- Dependencies: 182 182
-- Name: files_list_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY files_list_element
    ADD CONSTRAINT files_list_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2234 (class 2606 OID 65811)
-- Dependencies: 183 183
-- Name: flexible_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY flexible_element
    ADD CONSTRAINT flexible_element_pkey PRIMARY KEY (id_flexible_element);



--
-- Dependencies: 185 185
-- Name: global_export_content_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export_content
    ADD CONSTRAINT global_export_content_pkey PRIMARY KEY (id);


--
-- Dependencies: 184 184
-- Name: global_export_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export
    ADD CONSTRAINT global_export_pkey PRIMARY KEY (id);


--
-- Dependencies: 186 186
-- Name: global_export_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export_settings
    ADD CONSTRAINT global_export_settings_pkey PRIMARY KEY (id);
	
	
--
-- TOC entry 2236 (class 2606 OID 65813)
-- Dependencies: 184 184
-- Name: global_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_permission
    ADD CONSTRAINT global_permission_pkey PRIMARY KEY (id_global_permission);


--
-- TOC entry 2238 (class 2606 OID 65815)
-- Dependencies: 186 186
-- Name: history_token_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY history_token
    ADD CONSTRAINT history_token_pkey PRIMARY KEY (id_history_token);


--
-- TOC entry 2242 (class 2606 OID 65817)
-- Dependencies: 188 188 188
-- Name: indicator_datasource_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT indicator_datasource_pkey PRIMARY KEY (indicatorid, indicatorsourceid);


--
-- TOC entry 2244 (class 2606 OID 65819)
-- Dependencies: 189 189 189
-- Name: indicator_labels_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_labels
    ADD CONSTRAINT indicator_labels_pkey PRIMARY KEY (indicator_indicatorid, code);


--
-- TOC entry 2240 (class 2606 OID 65821)
-- Dependencies: 187 187
-- Name: indicator_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator
    ADD CONSTRAINT indicator_pkey PRIMARY KEY (indicatorid);


--
-- TOC entry 2246 (class 2606 OID 65823)
-- Dependencies: 190 190 190
-- Name: indicatordatasource_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatordatasource
    ADD CONSTRAINT indicatordatasource_pkey PRIMARY KEY (indicatorid, indicatorsourceid);


--
-- TOC entry 2248 (class 2606 OID 65825)
-- Dependencies: 191 191
-- Name: indicators_list_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicators_list_element
    ADD CONSTRAINT indicators_list_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2250 (class 2606 OID 65827)
-- Dependencies: 192 192 192
-- Name: indicators_list_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicators_list_value
    ADD CONSTRAINT indicators_list_value_pkey PRIMARY KEY (id_indicators_list, id_indicator);


--
-- TOC entry 2252 (class 2606 OID 65829)
-- Dependencies: 193 193 193
-- Name: indicatorvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT indicatorvalue_pkey PRIMARY KEY (indicatorid, reportingperiodid);


--
-- TOC entry 2254 (class 2606 OID 65831)
-- Dependencies: 194 194
-- Name: keyquestion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT keyquestion_pkey PRIMARY KEY (id);


--
-- TOC entry 2258 (class 2606 OID 65833)
-- Dependencies: 196 196
-- Name: layout_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT layout_constraint_pkey PRIMARY KEY (id_layout_constraint);


--
-- TOC entry 2260 (class 2606 OID 65835)
-- Dependencies: 197 197
-- Name: layout_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_group
    ADD CONSTRAINT layout_group_pkey PRIMARY KEY (id_layout_group);


--
-- TOC entry 2256 (class 2606 OID 65837)
-- Dependencies: 195 195
-- Name: layout_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout
    ADD CONSTRAINT layout_pkey PRIMARY KEY (id_layout);


--
-- TOC entry 2262 (class 2606 OID 65839)
-- Dependencies: 198 198
-- Name: location_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY location
    ADD CONSTRAINT location_pkey PRIMARY KEY (locationid);


--
-- TOC entry 2264 (class 2606 OID 65841)
-- Dependencies: 199 199 199
-- Name: locationadminlink_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT locationadminlink_pkey PRIMARY KEY (locationid, adminentityid);


--
-- TOC entry 2266 (class 2606 OID 65843)
-- Dependencies: 200 200
-- Name: locationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationtype
    ADD CONSTRAINT locationtype_pkey PRIMARY KEY (locationtypeid);


--
-- TOC entry 2270 (class 2606 OID 65845)
-- Dependencies: 202 202
-- Name: log_frame_activity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT log_frame_activity_pkey PRIMARY KEY (id_element);


--
-- TOC entry 2272 (class 2606 OID 65847)
-- Dependencies: 203 203
-- Name: log_frame_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_element
    ADD CONSTRAINT log_frame_element_pkey PRIMARY KEY (id_element);


--
-- TOC entry 2274 (class 2606 OID 65849)
-- Dependencies: 204 204
-- Name: log_frame_expected_result_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT log_frame_expected_result_pkey PRIMARY KEY (id_element);


--
-- TOC entry 2276 (class 2606 OID 65851)
-- Dependencies: 205 205
-- Name: log_frame_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_group
    ADD CONSTRAINT log_frame_group_pkey PRIMARY KEY (id_group);


--
-- TOC entry 2278 (class 2606 OID 65853)
-- Dependencies: 206 206 206
-- Name: log_frame_indicators_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT log_frame_indicators_pkey PRIMARY KEY (log_frame_element_id_element, indicators_indicatorid);


--
-- TOC entry 2280 (class 2606 OID 65855)
-- Dependencies: 207 207
-- Name: log_frame_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_model
    ADD CONSTRAINT log_frame_model_pkey PRIMARY KEY (id_log_frame);


--
-- TOC entry 2268 (class 2606 OID 65857)
-- Dependencies: 201 201
-- Name: log_frame_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame
    ADD CONSTRAINT log_frame_pkey PRIMARY KEY (id_log_frame);


--
-- TOC entry 2282 (class 2606 OID 65859)
-- Dependencies: 208 208
-- Name: log_frame_prerequisite_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT log_frame_prerequisite_pkey PRIMARY KEY (id_prerequisite);


--
-- TOC entry 2284 (class 2606 OID 65861)
-- Dependencies: 209 209
-- Name: log_frame_specific_objective_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT log_frame_specific_objective_pkey PRIMARY KEY (id_element);


--
-- TOC entry 2286 (class 2606 OID 65863)
-- Dependencies: 210 210
-- Name: message_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY message_element
    ADD CONSTRAINT message_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2290 (class 2606 OID 65865)
-- Dependencies: 212 212
-- Name: monitored_point_list_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point_list
    ADD CONSTRAINT monitored_point_list_pkey PRIMARY KEY (id_monitored_point_list);


--
-- TOC entry 2288 (class 2606 OID 65867)
-- Dependencies: 211 211
-- Name: monitored_point_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT monitored_point_pkey PRIMARY KEY (id_monitored_point);


--
-- TOC entry 2292 (class 2606 OID 65869)
-- Dependencies: 213 213
-- Name: org_unit_banner_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT org_unit_banner_pkey PRIMARY KEY (banner_id);


--
-- TOC entry 2294 (class 2606 OID 65871)
-- Dependencies: 214 214
-- Name: org_unit_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT org_unit_details_pkey PRIMARY KEY (details_id);


--
-- TOC entry 2296 (class 2606 OID 65873)
-- Dependencies: 215 215
-- Name: org_unit_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_model
    ADD CONSTRAINT org_unit_model_pkey PRIMARY KEY (org_unit_model_id);


--
-- TOC entry 2298 (class 2606 OID 65875)
-- Dependencies: 216 216
-- Name: organization_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id_organization);


--
-- TOC entry 2300 (class 2606 OID 65877)
-- Dependencies: 217 217
-- Name: orgunitpermission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT orgunitpermission_pkey PRIMARY KEY (id);


--
-- Dependencies: 2316 219 186
-- Name: fk2fb477b2f85c2c3c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export_settings
    ADD CONSTRAINT fk2fb477b2f85c2c3c FOREIGN KEY (organization_id) REFERENCES organization(id_organization);

--
-- Dependencies: 184 219 2316
-- Name: fk9e763fd0f85c2c3c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export
    ADD CONSTRAINT fk9e763fd0f85c2c3c FOREIGN KEY (organization_id) REFERENCES organization(id_organization);

--
-- Dependencies: 185 2248 184
-- Name: fkdca84b0af33647b9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export_content
    ADD CONSTRAINT fkdca84b0af33647b9 FOREIGN KEY (global_export_id) REFERENCES global_export(id);


--
-- TOC entry 2302 (class 2606 OID 65879)
-- Dependencies: 218 218
-- Name: partner_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT partner_pkey PRIMARY KEY (partnerid);


--
-- TOC entry 2304 (class 2606 OID 65881)
-- Dependencies: 219 219 219
-- Name: partnerindatabase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT partnerindatabase_pkey PRIMARY KEY (databaseid, partnerid);


--
-- TOC entry 2306 (class 2606 OID 65883)
-- Dependencies: 220 220
-- Name: personalcalendar_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY personalcalendar
    ADD CONSTRAINT personalcalendar_pkey PRIMARY KEY (id);


--
-- TOC entry 2308 (class 2606 OID 65885)
-- Dependencies: 221 221
-- Name: personalevent_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY personalevent
    ADD CONSTRAINT personalevent_pkey PRIMARY KEY (id);


--
-- TOC entry 2314 (class 2606 OID 65887)
-- Dependencies: 224 224
-- Name: phase_model_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_definition
    ADD CONSTRAINT phase_model_definition_pkey PRIMARY KEY (id_phase_model_definition);


--
-- TOC entry 2312 (class 2606 OID 65889)
-- Dependencies: 223 223
-- Name: phase_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model
    ADD CONSTRAINT phase_model_pkey PRIMARY KEY (id_phase_model);


--
-- TOC entry 2316 (class 2606 OID 65891)
-- Dependencies: 225 225 225
-- Name: phase_model_sucessors_id_phase_model_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT phase_model_sucessors_id_phase_model_key UNIQUE (id_phase_model, id_phase_model_successor);


--
-- TOC entry 2318 (class 2606 OID 65893)
-- Dependencies: 225 225
-- Name: phase_model_sucessors_id_phase_model_successor_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT phase_model_sucessors_id_phase_model_successor_key UNIQUE (id_phase_model_successor);


--
-- TOC entry 2310 (class 2606 OID 65895)
-- Dependencies: 222 222
-- Name: phase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase
    ADD CONSTRAINT phase_pkey PRIMARY KEY (id_phase);


--
-- TOC entry 2322 (class 2606 OID 65897)
-- Dependencies: 227 227
-- Name: privacy_group_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT privacy_group_permission_pkey PRIMARY KEY (id_permission);


--
-- TOC entry 2320 (class 2606 OID 65899)
-- Dependencies: 226 226
-- Name: privacy_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group
    ADD CONSTRAINT privacy_group_pkey PRIMARY KEY (id_privacy_group);


--
-- TOC entry 2324 (class 2606 OID 65901)
-- Dependencies: 228 228
-- Name: profile_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (id_profile);


--
-- TOC entry 2328 (class 2606 OID 65903)
-- Dependencies: 230 230
-- Name: project_banner_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_banner
    ADD CONSTRAINT project_banner_pkey PRIMARY KEY (id);


--
-- TOC entry 2330 (class 2606 OID 65905)
-- Dependencies: 231 231
-- Name: project_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_details
    ADD CONSTRAINT project_details_pkey PRIMARY KEY (id);


--
-- TOC entry 2332 (class 2606 OID 65907)
-- Dependencies: 232 232
-- Name: project_funding_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_funding
    ADD CONSTRAINT project_funding_pkey PRIMARY KEY (id_funding);


--
-- TOC entry 2334 (class 2606 OID 65909)
-- Dependencies: 233 233
-- Name: project_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model
    ADD CONSTRAINT project_model_pkey PRIMARY KEY (id_project_model);


--
-- TOC entry 2336 (class 2606 OID 65911)
-- Dependencies: 234 234
-- Name: project_model_visibility_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT project_model_visibility_pkey PRIMARY KEY (id_visibility);


--
-- TOC entry 2326 (class 2606 OID 65913)
-- Dependencies: 229 229
-- Name: project_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pkey PRIMARY KEY (databaseid);


--
-- TOC entry 2338 (class 2606 OID 65915)
-- Dependencies: 235 235 235
-- Name: project_userlogin_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT project_userlogin_pkey PRIMARY KEY (project_databaseid, favoriteusers_userid);


--
-- TOC entry 2340 (class 2606 OID 65917)
-- Dependencies: 236 236
-- Name: projectreport_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT projectreport_pkey PRIMARY KEY (id);


--
-- TOC entry 2342 (class 2606 OID 65919)
-- Dependencies: 237 237
-- Name: projectreportmodel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodel
    ADD CONSTRAINT projectreportmodel_pkey PRIMARY KEY (id);


--
-- TOC entry 2344 (class 2606 OID 65921)
-- Dependencies: 238 238
-- Name: projectreportmodelsection_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT projectreportmodelsection_pkey PRIMARY KEY (id);


--
-- TOC entry 2346 (class 2606 OID 65923)
-- Dependencies: 239 239
-- Name: projectreportversion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT projectreportversion_pkey PRIMARY KEY (id);


--
-- TOC entry 2350 (class 2606 OID 65925)
-- Dependencies: 241 241 241
-- Name: quality_criterion_children_id_quality_criterion_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT quality_criterion_children_id_quality_criterion_key UNIQUE (id_quality_criterion, id_quality_criterion_child);


--
-- TOC entry 2352 (class 2606 OID 65927)
-- Dependencies: 241 241
-- Name: quality_criterion_children_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT quality_criterion_children_pkey PRIMARY KEY (id_quality_criterion_child);


--
-- TOC entry 2348 (class 2606 OID 65929)
-- Dependencies: 240 240
-- Name: quality_criterion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT quality_criterion_pkey PRIMARY KEY (id_quality_criterion);


--
-- TOC entry 2354 (class 2606 OID 65931)
-- Dependencies: 242 242
-- Name: quality_criterion_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_type
    ADD CONSTRAINT quality_criterion_type_pkey PRIMARY KEY (id_criterion_type);


--
-- TOC entry 2356 (class 2606 OID 65933)
-- Dependencies: 243 243
-- Name: quality_framework_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_framework
    ADD CONSTRAINT quality_framework_pkey PRIMARY KEY (id_quality_framework);


--
-- TOC entry 2358 (class 2606 OID 65935)
-- Dependencies: 244 244
-- Name: question_choice_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT question_choice_element_pkey PRIMARY KEY (id_choice);


--
-- TOC entry 2360 (class 2606 OID 65937)
-- Dependencies: 245 245
-- Name: question_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_element
    ADD CONSTRAINT question_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2364 (class 2606 OID 65939)
-- Dependencies: 247 247
-- Name: reminder_list_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reminder_list
    ADD CONSTRAINT reminder_list_pkey PRIMARY KEY (id_reminder_list);


--
-- TOC entry 2362 (class 2606 OID 65941)
-- Dependencies: 246 246
-- Name: reminder_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reminder
    ADD CONSTRAINT reminder_pkey PRIMARY KEY (id_reminder);


--
-- TOC entry 2366 (class 2606 OID 65943)
-- Dependencies: 248 248
-- Name: report_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_element
    ADD CONSTRAINT report_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2368 (class 2606 OID 65945)
-- Dependencies: 249 249
-- Name: report_list_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT report_list_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2370 (class 2606 OID 65947)
-- Dependencies: 250 250
-- Name: reportingperiod_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportingperiod
    ADD CONSTRAINT reportingperiod_pkey PRIMARY KEY (reportingperiodid);


--
-- TOC entry 2372 (class 2606 OID 65949)
-- Dependencies: 251 251 251
-- Name: reportsubscription_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT reportsubscription_pkey PRIMARY KEY (reporttemplateid, userid);


--
-- TOC entry 2374 (class 2606 OID 65951)
-- Dependencies: 252 252
-- Name: reporttemplate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT reporttemplate_pkey PRIMARY KEY (reporttemplateid);


--
-- TOC entry 2376 (class 2606 OID 65953)
-- Dependencies: 253 253
-- Name: richtextelement_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY richtextelement
    ADD CONSTRAINT richtextelement_pkey PRIMARY KEY (id);


--
-- TOC entry 2378 (class 2606 OID 65955)
-- Dependencies: 254 254
-- Name: site_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT site_pkey PRIMARY KEY (siteid);


--
-- TOC entry 2380 (class 2606 OID 65957)
-- Dependencies: 255 255
-- Name: textarea_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY textarea_element
    ADD CONSTRAINT textarea_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2382 (class 2606 OID 65959)
-- Dependencies: 256 256
-- Name: triplet_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY triplet_value
    ADD CONSTRAINT triplet_value_pkey PRIMARY KEY (id_triplet);


--
-- TOC entry 2384 (class 2606 OID 65961)
-- Dependencies: 257 257
-- Name: triplets_list_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY triplets_list_element
    ADD CONSTRAINT triplets_list_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2386 (class 2606 OID 65963)
-- Dependencies: 258 258
-- Name: user_unit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit
    ADD CONSTRAINT user_unit_pkey PRIMARY KEY (id_user_unit);


--
-- TOC entry 2388 (class 2606 OID 65965)
-- Dependencies: 259 259 259
-- Name: user_unit_profiles_id_user_unit_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT user_unit_profiles_id_user_unit_key UNIQUE (id_user_unit, id_profile);


--
-- TOC entry 2390 (class 2606 OID 65967)
-- Dependencies: 260 260
-- Name: userdatabase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT userdatabase_pkey PRIMARY KEY (databaseid);


--
-- TOC entry 2392 (class 2606 OID 65969)
-- Dependencies: 261 261
-- Name: userlogin_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userlogin
    ADD CONSTRAINT userlogin_email_key UNIQUE (email);


--
-- TOC entry 2394 (class 2606 OID 65971)
-- Dependencies: 261 261
-- Name: userlogin_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userlogin
    ADD CONSTRAINT userlogin_pkey PRIMARY KEY (userid);


--
-- TOC entry 2396 (class 2606 OID 65973)
-- Dependencies: 262 262
-- Name: userpermission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userpermission
    ADD CONSTRAINT userpermission_pkey PRIMARY KEY (userpermissionid);


--
-- TOC entry 2398 (class 2606 OID 65975)
-- Dependencies: 263 263 263
-- Name: value_id_flexible_element_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY value
    ADD CONSTRAINT value_id_flexible_element_key UNIQUE (id_flexible_element, id_project);


--
-- TOC entry 2400 (class 2606 OID 65977)
-- Dependencies: 263 263
-- Name: value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY value
    ADD CONSTRAINT value_pkey PRIMARY KEY (id_value);


--
-- TOC entry 2422 (class 2606 OID 65978)
-- Dependencies: 216 2297 176
-- Name: fk1432f9db87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_type
    ADD CONSTRAINT fk1432f9db87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2555 (class 2606 OID 65983)
-- Dependencies: 218 2301 258
-- Name: fk143d4d78b7206e89; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit
    ADD CONSTRAINT fk143d4d78b7206e89 FOREIGN KEY (id_org_unit) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2556 (class 2606 OID 65988)
-- Dependencies: 258 2393 261
-- Name: fk143d4d78dd0ca99c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit
    ADD CONSTRAINT fk143d4d78dd0ca99c FOREIGN KEY (id_user) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2475 (class 2606 OID 65993)
-- Dependencies: 216 2297 215
-- Name: fk15d234e987d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_model
    ADD CONSTRAINT fk15d234e987d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2531 (class 2606 OID 65998)
-- Dependencies: 2215 244 175
-- Name: fk17871bd711158eaf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT fk17871bd711158eaf FOREIGN KEY (id_category_element) REFERENCES category_element(id_category_element) DEFERRABLE;


--
-- TOC entry 2532 (class 2606 OID 66003)
-- Dependencies: 245 2359 244
-- Name: fk17871bd7d92f832c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT fk17871bd7d92f832c FOREIGN KEY (id_question) REFERENCES question_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2461 (class 2606 OID 66008)
-- Dependencies: 2239 187 206
-- Name: fk17e5a9f1a023ddc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT fk17e5a9f1a023ddc FOREIGN KEY (indicators_indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2462 (class 2606 OID 66013)
-- Dependencies: 206 2271 203
-- Name: fk17e5a9f1f6e4c4b8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT fk17e5a9f1f6e4c4b8 FOREIGN KEY (log_frame_element_id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;


--
-- TOC entry 2503 (class 2606 OID 66018)
-- Dependencies: 195 2255 230
-- Name: fk1bc8331244f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_banner
    ADD CONSTRAINT fk1bc8331244f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2504 (class 2606 OID 66023)
-- Dependencies: 230 2333 233
-- Name: fk1bc83312d196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_banner
    ADD CONSTRAINT fk1bc83312d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2548 (class 2606 OID 66028)
-- Dependencies: 254 2377 254
-- Name: fk2753671fcde08d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk2753671fcde08d FOREIGN KEY (assessmentsiteid) REFERENCES site(siteid) DEFERRABLE;


--
-- TOC entry 2549 (class 2606 OID 66033)
-- Dependencies: 254 2261 198
-- Name: fk275367368ddfa7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk275367368ddfa7 FOREIGN KEY (locationid) REFERENCES location(locationid) DEFERRABLE;


--
-- TOC entry 2550 (class 2606 OID 66038)
-- Dependencies: 260 2389 254
-- Name: fk275367494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk275367494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2551 (class 2606 OID 66043)
-- Dependencies: 254 2301 218
-- Name: fk27536779d901c9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk27536779d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2552 (class 2606 OID 66048)
-- Dependencies: 2189 161 254
-- Name: fk27536780bf17db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk27536780bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;


--
-- TOC entry 2403 (class 2606 OID 66053)
-- Dependencies: 162 2193 163
-- Name: fk2e3083f227f5cac7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminentity
    ADD CONSTRAINT fk2e3083f227f5cac7 FOREIGN KEY (adminlevelid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;


--
-- TOC entry 2404 (class 2606 OID 66058)
-- Dependencies: 162 2191 162
-- Name: fk2e3083f2ff2bada7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminentity
    ADD CONSTRAINT fk2e3083f2ff2bada7 FOREIGN KEY (adminentityparentid) REFERENCES adminentity(adminentityid) DEFERRABLE;


--
-- TOC entry 2479 (class 2606 OID 66063)
-- Dependencies: 198 218 2261
-- Name: fk33f574a8350d2271; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8350d2271 FOREIGN KEY (location_locationid) REFERENCES location(locationid) DEFERRABLE;


--
-- TOC entry 2480 (class 2606 OID 66068)
-- Dependencies: 215 218 2295
-- Name: fk33f574a84ba27d70; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a84ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;


--
-- TOC entry 2481 (class 2606 OID 66073)
-- Dependencies: 2301 218 218
-- Name: fk33f574a85179b874; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a85179b874 FOREIGN KEY (parent_partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2482 (class 2606 OID 66078)
-- Dependencies: 216 2297 218
-- Name: fk33f574a8cf94c360; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8cf94c360 FOREIGN KEY (organization_id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2483 (class 2606 OID 66083)
-- Dependencies: 178 2221 218
-- Name: fk33f574a8faec4abb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8faec4abb FOREIGN KEY (office_country_id) REFERENCES country(countryid) DEFERRABLE;


--
-- TOC entry 2542 (class 2606 OID 66088)
-- Dependencies: 251 252 2373
-- Name: fk35f790911741f030; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f790911741f030 FOREIGN KEY (reporttemplateid) REFERENCES reporttemplate(reporttemplateid) DEFERRABLE;


--
-- TOC entry 2543 (class 2606 OID 66093)
-- Dependencies: 251 2393 261
-- Name: fk35f7909148b34b53; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f7909148b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2544 (class 2606 OID 66098)
-- Dependencies: 261 2393 251
-- Name: fk35f7909173633c59; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f7909173633c59 FOREIGN KEY (invitinguserid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2533 (class 2606 OID 66103)
-- Dependencies: 2233 245 183
-- Name: fk3d05bba320d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba320d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2534 (class 2606 OID 66108)
-- Dependencies: 2347 240 245
-- Name: fk3d05bba370812310; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba370812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2535 (class 2606 OID 66113)
-- Dependencies: 245 176 2217
-- Name: fk3d05bba3b6ab611d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba3b6ab611d FOREIGN KEY (id_category_type) REFERENCES category_type(id_category_type) DEFERRABLE;


--
-- TOC entry 2476 (class 2606 OID 66118)
-- Dependencies: 2301 218 216
-- Name: fk4644ed33754a9e7e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk4644ed33754a9e7e FOREIGN KEY (id_root_org_unit) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2559 (class 2606 OID 66123)
-- Dependencies: 260 2393 261
-- Name: fk46aeba86a5c52bc6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT fk46aeba86a5c52bc6 FOREIGN KEY (owneruserid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2560 (class 2606 OID 66128)
-- Dependencies: 260 2221 178
-- Name: fk46aeba86b6676e25; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT fk46aeba86b6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;


--
-- TOC entry 2424 (class 2606 OID 66133)
-- Dependencies: 179 2233 183
-- Name: fk48d914c620d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY default_flexible_element
    ADD CONSTRAINT fk48d914c620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2527 (class 2606 OID 66138)
-- Dependencies: 240 241 2347
-- Name: fk4a73751d70812310; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT fk4a73751d70812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2528 (class 2606 OID 66143)
-- Dependencies: 2347 240 241
-- Name: fk4a73751dfe03d96d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT fk4a73751dfe03d96d FOREIGN KEY (id_quality_criterion_child) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2431 (class 2606 OID 66148)
-- Dependencies: 2389 260 187
-- Name: fk4d01ddef494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2432 (class 2606 OID 66153)
-- Dependencies: 240 2347 187
-- Name: fk4d01ddef70812310; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef70812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2433 (class 2606 OID 66158)
-- Dependencies: 161 187 2189
-- Name: fk4d01ddef80bf17db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef80bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;


--
-- TOC entry 2414 (class 2606 OID 66163)
-- Dependencies: 169 2377 254
-- Name: fk4ed7045544c2434b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT fk4ed7045544c2434b FOREIGN KEY (siteid) REFERENCES site(siteid) DEFERRABLE;


--
-- TOC entry 2415 (class 2606 OID 66168)
-- Dependencies: 2197 169 166
-- Name: fk4ed70455afed0b31; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT fk4ed70455afed0b31 FOREIGN KEY (attributeid) REFERENCES attribute(attributeid) DEFERRABLE;


--
-- TOC entry 2449 (class 2606 OID 66173)
-- Dependencies: 2261 198 199
-- Name: fk50408394368ddfa7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT fk50408394368ddfa7 FOREIGN KEY (locationid) REFERENCES location(locationid) DEFERRABLE;


--
-- TOC entry 2450 (class 2606 OID 66178)
-- Dependencies: 162 199 2191
-- Name: fk50408394cd1204fd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT fk50408394cd1204fd FOREIGN KEY (adminentityid) REFERENCES adminentity(adminentityid) DEFERRABLE;


--
-- TOC entry 2497 (class 2606 OID 66183)
-- Dependencies: 229 260 2389
-- Name: fk50c8e2f9494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2498 (class 2606 OID 66188)
-- Dependencies: 229 261 2393
-- Name: fk50c8e2f955bb91b6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f955bb91b6 FOREIGN KEY (id_manager) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2499 (class 2606 OID 66193)
-- Dependencies: 229 212 2289
-- Name: fk50c8e2f9b07b74ff; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9b07b74ff FOREIGN KEY (id_monitored_points_list) REFERENCES monitored_point_list(id_monitored_point_list) DEFERRABLE;


--
-- TOC entry 2500 (class 2606 OID 66198)
-- Dependencies: 233 2333 229
-- Name: fk50c8e2f9d196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2501 (class 2606 OID 66203)
-- Dependencies: 222 2309 229
-- Name: fk50c8e2f9dffa476a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9dffa476a FOREIGN KEY (id_current_phase) REFERENCES phase(id_phase) DEFERRABLE;


--
-- TOC entry 2502 (class 2606 OID 66208)
-- Dependencies: 229 2363 247
-- Name: fk50c8e2f9e2910b71; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9e2910b71 FOREIGN KEY (id_reminder_list) REFERENCES reminder_list(id_reminder_list) DEFERRABLE;


--
-- TOC entry 2425 (class 2606 OID 66213)
-- Dependencies: 181 261 2393
-- Name: fk52157d152c2c465c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_version
    ADD CONSTRAINT fk52157d152c2c465c FOREIGN KEY (id_author) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2426 (class 2606 OID 66218)
-- Dependencies: 2225 181 180
-- Name: fk52157d15d4cd29db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_version
    ADD CONSTRAINT fk52157d15d4cd29db FOREIGN KEY (id_file) REFERENCES file_meta(id_file) DEFERRABLE;


--
-- TOC entry 2507 (class 2606 OID 66223)
-- Dependencies: 229 2325 232
-- Name: fk52f38bd74485e32a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd74485e32a FOREIGN KEY (id_project_funding) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2508 (class 2606 OID 66228)
-- Dependencies: 232 2331 232
-- Name: fk52f38bd7597e985f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd7597e985f FOREIGN KEY (id_funding) REFERENCES project_funding(id_funding) DEFERRABLE;


--
-- TOC entry 2509 (class 2606 OID 66233)
-- Dependencies: 229 232 2325
-- Name: fk52f38bd7c908f825; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd7c908f825 FOREIGN KEY (id_project_funded) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2554 (class 2606 OID 66238)
-- Dependencies: 257 2233 183
-- Name: fk532b05fd20d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY triplets_list_element
    ADD CONSTRAINT fk532b05fd20d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2468 (class 2606 OID 66243)
-- Dependencies: 210 2233 183
-- Name: fk553ccec420d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY message_element
    ADD CONSTRAINT fk553ccec420d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2457 (class 2606 OID 66248)
-- Dependencies: 203 2275 205
-- Name: fk5a2e206f4f6005ee; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_element
    ADD CONSTRAINT fk5a2e206f4f6005ee FOREIGN KEY (id_group) REFERENCES log_frame_group(id_group) DEFERRABLE;


--
-- TOC entry 2418 (class 2606 OID 66253)
-- Dependencies: 2213 174 173
-- Name: fk5a830ade653f90a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_part
    ADD CONSTRAINT fk5a830ade653f90a FOREIGN KEY (id_budget_parts_list) REFERENCES budget_parts_list_value(id_budget_parts_list) DEFERRABLE;


--
-- TOC entry 2427 (class 2606 OID 66258)
-- Dependencies: 183 2233 182
-- Name: fk6459a12320d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY files_list_element
    ADD CONSTRAINT fk6459a12320d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2451 (class 2606 OID 66263)
-- Dependencies: 200 163 2193
-- Name: fk65214af20feb745; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationtype
    ADD CONSTRAINT fk65214af20feb745 FOREIGN KEY (boundadminlevelid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;


--
-- TOC entry 2452 (class 2606 OID 66268)
-- Dependencies: 178 200 2221
-- Name: fk65214afb6676e25; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationtype
    ADD CONSTRAINT fk65214afb6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;


--
-- TOC entry 2486 (class 2606 OID 66273)
-- Dependencies: 229 2325 222
-- Name: fk65b097bb13b3e6c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase
    ADD CONSTRAINT fk65b097bb13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2487 (class 2606 OID 66278)
-- Dependencies: 222 2311 223
-- Name: fk65b097bc9c78c91; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase
    ADD CONSTRAINT fk65b097bc9c78c91 FOREIGN KEY (id_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;


--
-- TOC entry 2441 (class 2606 OID 66283)
-- Dependencies: 193 187 2239
-- Name: fk676020c247c62157; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT fk676020c247c62157 FOREIGN KEY (indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2442 (class 2606 OID 66288)
-- Dependencies: 2369 250 193
-- Name: fk676020c284811db7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT fk676020c284811db7 FOREIGN KEY (reportingperiodid) REFERENCES reportingperiod(reportingperiodid) DEFERRABLE;


--
-- TOC entry 2420 (class 2606 OID 66293)
-- Dependencies: 216 175 2297
-- Name: fk67dfa4bb87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_element
    ADD CONSTRAINT fk67dfa4bb87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2421 (class 2606 OID 66298)
-- Dependencies: 175 176 2217
-- Name: fk67dfa4bbb6ab611d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_element
    ADD CONSTRAINT fk67dfa4bbb6ab611d FOREIGN KEY (id_category_type) REFERENCES category_type(id_category_type) DEFERRABLE;


--
-- TOC entry 2419 (class 2606 OID 66303)
-- Dependencies: 2207 174 171
-- Name: fk69676b09c9ce70ad; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_parts_list_value
    ADD CONSTRAINT fk69676b09c9ce70ad FOREIGN KEY (id_budget) REFERENCES budget(id_budget) DEFERRABLE;


--
-- TOC entry 2565 (class 2606 OID 66308)
-- Dependencies: 263 183 2233
-- Name: fk6ac917120d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY value
    ADD CONSTRAINT fk6ac917120d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2566 (class 2606 OID 66313)
-- Dependencies: 2393 263 261
-- Name: fk6ac91712922bbb3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY value
    ADD CONSTRAINT fk6ac91712922bbb3 FOREIGN KEY (id_user_last_modif) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2493 (class 2606 OID 66318)
-- Dependencies: 216 226 2297
-- Name: fk74e7b70887d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group
    ADD CONSTRAINT fk74e7b70887d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2448 (class 2606 OID 66323)
-- Dependencies: 2265 200 198
-- Name: fk752a03d58c0165bb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY location
    ADD CONSTRAINT fk752a03d58c0165bb FOREIGN KEY (locationtypeid) REFERENCES locationtype(locationtypeid) DEFERRABLE;


--
-- TOC entry 2525 (class 2606 OID 66328)
-- Dependencies: 2355 243 240
-- Name: fk76d1d76183d8e9ca; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT fk76d1d76183d8e9ca FOREIGN KEY (id_quality_framework) REFERENCES quality_framework(id_quality_framework) DEFERRABLE;


--
-- TOC entry 2526 (class 2606 OID 66333)
-- Dependencies: 216 240 2297
-- Name: fk76d1d76187d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT fk76d1d76187d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2411 (class 2606 OID 66338)
-- Dependencies: 2199 166 167
-- Name: fk7839ca7cda7c5e3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attribute
    ADD CONSTRAINT fk7839ca7cda7c5e3 FOREIGN KEY (attributegroupid) REFERENCES attributegroup(attributegroupid) DEFERRABLE;


--
-- TOC entry 2491 (class 2606 OID 66343)
-- Dependencies: 223 225 2311
-- Name: fk7a142472181ec2f8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT fk7a142472181ec2f8 FOREIGN KEY (id_phase_model_successor) REFERENCES phase_model(id_phase_model) DEFERRABLE;


--
-- TOC entry 2492 (class 2606 OID 66348)
-- Dependencies: 225 2311 223
-- Name: fk7a142472c9c78c91; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT fk7a142472c9c78c91 FOREIGN KEY (id_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;


--
-- TOC entry 2434 (class 2606 OID 66353)
-- Dependencies: 187 188 2239
-- Name: fk7a87f87547c62157; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT fk7a87f87547c62157 FOREIGN KEY (indicatorid) REFERENCES indicator(indicatorid);


--
-- TOC entry 2435 (class 2606 OID 66358)
-- Dependencies: 188 2239 187
-- Name: fk7a87f8755038b772; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT fk7a87f8755038b772 FOREIGN KEY (indicatorsourceid) REFERENCES indicator(indicatorid);


--
-- TOC entry 2513 (class 2606 OID 66363)
-- Dependencies: 229 235 2325
-- Name: fk8076a4d884058733; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT fk8076a4d884058733 FOREIGN KEY (project_databaseid) REFERENCES project(databaseid);


--
-- TOC entry 2514 (class 2606 OID 66368)
-- Dependencies: 235 261 2393
-- Name: fk8076a4d8efbea106; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT fk8076a4d8efbea106 FOREIGN KEY (favoriteusers_userid) REFERENCES userlogin(userid);


--
-- TOC entry 2530 (class 2606 OID 66373)
-- Dependencies: 216 243 2297
-- Name: fk807dbabe87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_framework
    ADD CONSTRAINT fk807dbabe87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2407 (class 2606 OID 66378)
-- Dependencies: 2267 164 201
-- Name: fk807f02ed9bc5c4da; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment
    ADD CONSTRAINT fk807f02ed9bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;


--
-- TOC entry 2408 (class 2606 OID 66383)
-- Dependencies: 2325 229 164
-- Name: fk807f02edb13b3e6c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment
    ADD CONSTRAINT fk807f02edb13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2539 (class 2606 OID 66388)
-- Dependencies: 249 183 2233
-- Name: fk8104218620d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT fk8104218620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2540 (class 2606 OID 66393)
-- Dependencies: 2341 237 249
-- Name: fk8104218654081a85; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT fk8104218654081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id);


--
-- TOC entry 2447 (class 2606 OID 66398)
-- Dependencies: 197 2255 195
-- Name: fk8435cd2a44f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_group
    ADD CONSTRAINT fk8435cd2a44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2520 (class 2606 OID 66403)
-- Dependencies: 237 2297 216
-- Name: fk85b7359c87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodel
    ADD CONSTRAINT fk85b7359c87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2453 (class 2606 OID 66408)
-- Dependencies: 201 229 2325
-- Name: fk88122cb2b13b3e6c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame
    ADD CONSTRAINT fk88122cb2b13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2454 (class 2606 OID 66413)
-- Dependencies: 2279 201 207
-- Name: fk88122cb2eee3ae75; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame
    ADD CONSTRAINT fk88122cb2eee3ae75 FOREIGN KEY (id_log_frame_model) REFERENCES log_frame_model(id_log_frame) DEFERRABLE;


--
-- TOC entry 2417 (class 2606 OID 66418)
-- Dependencies: 2233 183 172
-- Name: fk881d68fb20d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_distribution_element
    ADD CONSTRAINT fk881d68fb20d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2464 (class 2606 OID 66423)
-- Dependencies: 205 2275 208
-- Name: fk88c951234f6005ee; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT fk88c951234f6005ee FOREIGN KEY (id_group) REFERENCES log_frame_group(id_group) DEFERRABLE;


--
-- TOC entry 2465 (class 2606 OID 66428)
-- Dependencies: 208 201 2267
-- Name: fk88c951239bc5c4da; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT fk88c951239bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;


--
-- TOC entry 2455 (class 2606 OID 66433)
-- Dependencies: 2273 202 204
-- Name: fk89611ffc8012bc39; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT fk89611ffc8012bc39 FOREIGN KEY (id_result) REFERENCES log_frame_expected_result(id_element) DEFERRABLE;


--
-- TOC entry 2456 (class 2606 OID 66438)
-- Dependencies: 202 2271 203
-- Name: fk89611ffce41dae8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT fk89611ffce41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;


--
-- TOC entry 2561 (class 2606 OID 66443)
-- Dependencies: 216 2297 261
-- Name: fk8aa0da3e87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userlogin
    ADD CONSTRAINT fk8aa0da3e87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2553 (class 2606 OID 66448)
-- Dependencies: 183 2233 255
-- Name: fk8d80a2f720d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY textarea_element
    ADD CONSTRAINT fk8d80a2f720d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2469 (class 2606 OID 66453)
-- Dependencies: 2289 211 212
-- Name: fk8df3554a3dc0a3b1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT fk8df3554a3dc0a3b1 FOREIGN KEY (id_list) REFERENCES monitored_point_list(id_monitored_point_list) DEFERRABLE;


--
-- TOC entry 2470 (class 2606 OID 66458)
-- Dependencies: 180 2225 211
-- Name: fk8df3554ad4cd29db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT fk8df3554ad4cd29db FOREIGN KEY (id_file) REFERENCES file_meta(id_file) DEFERRABLE;


--
-- TOC entry 2471 (class 2606 OID 66463)
-- Dependencies: 2255 213 195
-- Name: fk90ee7d6c44f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT fk90ee7d6c44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2472 (class 2606 OID 66468)
-- Dependencies: 2295 213 215
-- Name: fk90ee7d6c4ba27d70; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT fk90ee7d6c4ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;


--
-- TOC entry 2428 (class 2606 OID 66473)
-- Dependencies: 226 183 2319
-- Name: fk91725e88e25e8842; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY flexible_element
    ADD CONSTRAINT fk91725e88e25e8842 FOREIGN KEY (id_privacy_group) REFERENCES privacy_group(id_privacy_group) DEFERRABLE;


--
-- TOC entry 2547 (class 2606 OID 66478)
-- Dependencies: 2345 253 239
-- Name: fk9752ca7398d45965; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY richtextelement
    ADD CONSTRAINT fk9752ca7398d45965 FOREIGN KEY (version_id) REFERENCES projectreportversion(id) DEFERRABLE;


--
-- TOC entry 2458 (class 2606 OID 66483)
-- Dependencies: 2283 209 204
-- Name: fk99d3ddf7d88379d4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT fk99d3ddf7d88379d4 FOREIGN KEY (id_specific_objective) REFERENCES log_frame_specific_objective(id_element) DEFERRABLE;


--
-- TOC entry 2459 (class 2606 OID 66488)
-- Dependencies: 2271 203 204
-- Name: fk99d3ddf7e41dae8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT fk99d3ddf7e41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;


--
-- TOC entry 2445 (class 2606 OID 66493)
-- Dependencies: 196 2233 183
-- Name: fk9bb4b21220d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT fk9bb4b21220d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2446 (class 2606 OID 66498)
-- Dependencies: 197 196 2259
-- Name: fk9bb4b212da924c21; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT fk9bb4b212da924c21 FOREIGN KEY (id_layout_group) REFERENCES layout_group(id_layout_group) DEFERRABLE;


--
-- TOC entry 2405 (class 2606 OID 66503)
-- Dependencies: 2221 163 178
-- Name: fk9ec33d95b6676e25; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT fk9ec33d95b6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;


--
-- TOC entry 2406 (class 2606 OID 66508)
-- Dependencies: 163 2193 163
-- Name: fk9ec33d95e01b109c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT fk9ec33d95e01b109c FOREIGN KEY (parentid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;


--
-- TOC entry 2401 (class 2606 OID 66513)
-- Dependencies: 260 161 2389
-- Name: fka126572f494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT fka126572f494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2402 (class 2606 OID 66518)
-- Dependencies: 2265 200 161
-- Name: fka126572f8c0165bb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT fka126572f8c0165bb FOREIGN KEY (locationtypeid) REFERENCES locationtype(locationtypeid) DEFERRABLE;


--
-- TOC entry 2494 (class 2606 OID 66523)
-- Dependencies: 227 2323 228
-- Name: fka1812f6692e83e47; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT fka1812f6692e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;


--
-- TOC entry 2495 (class 2606 OID 66528)
-- Dependencies: 2319 227 226
-- Name: fka1812f66e25e8842; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT fka1812f66e25e8842 FOREIGN KEY (id_privacy_group) REFERENCES privacy_group(id_privacy_group) DEFERRABLE;


--
-- TOC entry 2484 (class 2606 OID 66533)
-- Dependencies: 219 260 2389
-- Name: fka9a62c88494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT fka9a62c88494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2485 (class 2606 OID 66538)
-- Dependencies: 219 218 2301
-- Name: fka9a62c8879d901c9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT fka9a62c8879d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2529 (class 2606 OID 66543)
-- Dependencies: 2355 242 243
-- Name: fkb0b3e55883d8e9ca; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_type
    ADD CONSTRAINT fkb0b3e55883d8e9ca FOREIGN KEY (id_quality_framework) REFERENCES quality_framework(id_quality_framework) DEFERRABLE;


--
-- TOC entry 2521 (class 2606 OID 66548)
-- Dependencies: 237 238 2341
-- Name: fkb29299a98fa2795f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT fkb29299a98fa2795f FOREIGN KEY (projectmodelid) REFERENCES projectreportmodel(id) DEFERRABLE;


--
-- TOC entry 2522 (class 2606 OID 66553)
-- Dependencies: 238 238 2343
-- Name: fkb29299a9ae53865a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT fkb29299a9ae53865a FOREIGN KEY (parentsectionmodelid) REFERENCES projectreportmodelsection(id) DEFERRABLE;


--
-- TOC entry 2460 (class 2606 OID 66558)
-- Dependencies: 201 205 2267
-- Name: fkb4d3b8b29bc5c4da; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_group
    ADD CONSTRAINT fkb4d3b8b29bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;


--
-- TOC entry 2463 (class 2606 OID 66563)
-- Dependencies: 2333 233 207
-- Name: fkb526bd5cd196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_model
    ADD CONSTRAINT fkb526bd5cd196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2537 (class 2606 OID 66568)
-- Dependencies: 248 183 2233
-- Name: fkbc80a6f120d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_element
    ADD CONSTRAINT fkbc80a6f120d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2538 (class 2606 OID 66573)
-- Dependencies: 2341 248 237
-- Name: fkbc80a6f154081a85; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_element
    ADD CONSTRAINT fkbc80a6f154081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id);


--
-- TOC entry 2523 (class 2606 OID 66578)
-- Dependencies: 2339 239 236
-- Name: fkc093868b39413f9b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT fkc093868b39413f9b FOREIGN KEY (report_id) REFERENCES projectreport(id) DEFERRABLE;


--
-- TOC entry 2524 (class 2606 OID 66583)
-- Dependencies: 261 2393 239
-- Name: fkc093868b54402265; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT fkc093868b54402265 FOREIGN KEY (editor_userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2511 (class 2606 OID 66588)
-- Dependencies: 216 2297 234
-- Name: fkc10e64e87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT fkc10e64e87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2512 (class 2606 OID 66593)
-- Dependencies: 2333 233 234
-- Name: fkc10e64ed196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT fkc10e64ed196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2488 (class 2606 OID 66598)
-- Dependencies: 223 195 2255
-- Name: fkc11f1f6544f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f6544f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2489 (class 2606 OID 66603)
-- Dependencies: 223 233 2333
-- Name: fkc11f1f65d196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f65d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2490 (class 2606 OID 66608)
-- Dependencies: 224 223 2313
-- Name: fkc11f1f65e0174a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f65e0174a FOREIGN KEY (definition_id) REFERENCES phase_model_definition(id_phase_model_definition) DEFERRABLE;


--
-- TOC entry 2557 (class 2606 OID 66613)
-- Dependencies: 2323 228 259
-- Name: fkc37e36d192e83e47; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT fkc37e36d192e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;


--
-- TOC entry 2558 (class 2606 OID 66618)
-- Dependencies: 258 259 2385
-- Name: fkc37e36d1b3ab1d1c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT fkc37e36d1b3ab1d1c FOREIGN KEY (id_user_unit) REFERENCES user_unit(id_user_unit) DEFERRABLE;


--
-- TOC entry 2409 (class 2606 OID 66623)
-- Dependencies: 2195 164 165
-- Name: fkc514f4bc7b49ebc6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment_history_token
    ADD CONSTRAINT fkc514f4bc7b49ebc6 FOREIGN KEY (amendment_id_amendment) REFERENCES amendment(id_amendment) DEFERRABLE;


--
-- TOC entry 2410 (class 2606 OID 66628)
-- Dependencies: 165 186 2237
-- Name: fkc514f4bcbc854628; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment_history_token
    ADD CONSTRAINT fkc514f4bcbc854628 FOREIGN KEY (values_id_history_token) REFERENCES history_token(id_history_token) DEFERRABLE;


--
-- TOC entry 2545 (class 2606 OID 66633)
-- Dependencies: 252 260 2389
-- Name: fkc69ddee494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT fkc69ddee494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2546 (class 2606 OID 66638)
-- Dependencies: 252 261 2393
-- Name: fkc69ddeea5c52bc6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT fkc69ddeea5c52bc6 FOREIGN KEY (owneruserid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2510 (class 2606 OID 66643)
-- Dependencies: 223 233 2311
-- Name: fkc7b83283792a5c7c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model
    ADD CONSTRAINT fkc7b83283792a5c7c FOREIGN KEY (id_root_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;


--
-- TOC entry 2466 (class 2606 OID 66648)
-- Dependencies: 209 201 2267
-- Name: fkc979ef199bc5c4da; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT fkc979ef199bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;


--
-- TOC entry 2467 (class 2606 OID 66653)
-- Dependencies: 209 203 2271
-- Name: fkc979ef19e41dae8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT fkc979ef19e41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;


--
-- TOC entry 2429 (class 2606 OID 66658)
-- Dependencies: 184 228 2323
-- Name: fkcb8783eb92e83e47; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_permission
    ADD CONSTRAINT fkcb8783eb92e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;


--
-- TOC entry 2505 (class 2606 OID 66663)
-- Dependencies: 231 195 2255
-- Name: fkce2cbb1c44f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_details
    ADD CONSTRAINT fkce2cbb1c44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2506 (class 2606 OID 66668)
-- Dependencies: 231 233 2333
-- Name: fkce2cbb1cd196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_details
    ADD CONSTRAINT fkce2cbb1cd196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2562 (class 2606 OID 66673)
-- Dependencies: 262 261 2393
-- Name: fkd265581a48b34b53; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a48b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2563 (class 2606 OID 66678)
-- Dependencies: 262 260 2389
-- Name: fkd265581a494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2564 (class 2606 OID 66683)
-- Dependencies: 262 218 2301
-- Name: fkd265581a79d901c9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a79d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2443 (class 2606 OID 66688)
-- Dependencies: 194 2347 240
-- Name: fkd2af174536d186ad; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT fkd2af174536d186ad FOREIGN KEY (qualitycriterion_id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2444 (class 2606 OID 66693)
-- Dependencies: 194 238 2343
-- Name: fkd2af1745d8178e71; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT fkd2af1745d8178e71 FOREIGN KEY (sectionid) REFERENCES projectreportmodelsection(id) DEFERRABLE;


--
-- TOC entry 2430 (class 2606 OID 66698)
-- Dependencies: 186 261 2393
-- Name: fkd692428edd0ca99c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY history_token
    ADD CONSTRAINT fkd692428edd0ca99c FOREIGN KEY (id_user) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2541 (class 2606 OID 66703)
-- Dependencies: 250 254 2377
-- Name: fkdcfe056f44c2434b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportingperiod
    ADD CONSTRAINT fkdcfe056f44c2434b FOREIGN KEY (siteid) REFERENCES site(siteid) DEFERRABLE;


--
-- TOC entry 2412 (class 2606 OID 66708)
-- Dependencies: 168 161 2189
-- Name: fkdd8c951780bf17db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT fkdd8c951780bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;


--
-- TOC entry 2413 (class 2606 OID 66713)
-- Dependencies: 168 167 2199
-- Name: fkdd8c9517da7c5e3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT fkdd8c9517da7c5e3 FOREIGN KEY (attributegroupid) REFERENCES attributegroup(attributegroupid) DEFERRABLE;


--
-- TOC entry 2416 (class 2606 OID 66718)
-- Dependencies: 170 261 2393
-- Name: fkddeeae9848b34b53; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authentication
    ADD CONSTRAINT fkddeeae9848b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2515 (class 2606 OID 66723)
-- Dependencies: 236 183 2233
-- Name: fke0b8458d3cdc69db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d3cdc69db FOREIGN KEY (flexibleelement_id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2516 (class 2606 OID 66728)
-- Dependencies: 236 237 2341
-- Name: fke0b8458d54081a85; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d54081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id) DEFERRABLE;


--
-- TOC entry 2517 (class 2606 OID 66733)
-- Dependencies: 236 239 2345
-- Name: fke0b8458d5a50539e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d5a50539e FOREIGN KEY (currentversion_id) REFERENCES projectreportversion(id) DEFERRABLE;


--
-- TOC entry 2518 (class 2606 OID 66738)
-- Dependencies: 236 229 2325
-- Name: fke0b8458d84058733; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d84058733 FOREIGN KEY (project_databaseid) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2519 (class 2606 OID 66743)
-- Dependencies: 236 218 2301
-- Name: fke0b8458db2590b2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458db2590b2 FOREIGN KEY (orgunit_partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2536 (class 2606 OID 66748)
-- Dependencies: 246 247 2363
-- Name: fke116c072e22ec8c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reminder
    ADD CONSTRAINT fke116c072e22ec8c FOREIGN KEY (id_list) REFERENCES reminder_list(id_reminder_list) DEFERRABLE;


--
-- TOC entry 2423 (class 2606 OID 66753)
-- Dependencies: 177 183 2233
-- Name: fke1e36e8020d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY checkbox_element
    ADD CONSTRAINT fke1e36e8020d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2439 (class 2606 OID 66758)
-- Dependencies: 191 183 2233
-- Name: fkeb796c7620d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicators_list_element
    ADD CONSTRAINT fkeb796c7620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2496 (class 2606 OID 66763)
-- Dependencies: 228 216 2297
-- Name: fked8e89a987d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT fked8e89a987d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2477 (class 2606 OID 66768)
-- Dependencies: 217 261 2393
-- Name: fkf10e425774ec9247; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT fkf10e425774ec9247 FOREIGN KEY (user_userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2478 (class 2606 OID 66773)
-- Dependencies: 217 218 2301
-- Name: fkf10e4257d3cc239c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT fkf10e4257d3cc239c FOREIGN KEY (unit_id) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2437 (class 2606 OID 66778)
-- Dependencies: 190 2239 187
-- Name: fkf23eb7b447c62157; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatordatasource
    ADD CONSTRAINT fkf23eb7b447c62157 FOREIGN KEY (indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2438 (class 2606 OID 66783)
-- Dependencies: 190 187 2239
-- Name: fkf23eb7b45038b772; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatordatasource
    ADD CONSTRAINT fkf23eb7b45038b772 FOREIGN KEY (indicatorsourceid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2440 (class 2606 OID 66788)
-- Dependencies: 187 2239 192
-- Name: fkf8bf56b6530fdd8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicators_list_value
    ADD CONSTRAINT fkf8bf56b6530fdd8 FOREIGN KEY (id_indicator) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2473 (class 2606 OID 66793)
-- Dependencies: 2255 214 195
-- Name: fkfdcfbc0244f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT fkfdcfbc0244f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2474 (class 2606 OID 66798)
-- Dependencies: 214 2295 215
-- Name: fkfdcfbc024ba27d70; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT fkfdcfbc024ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;


--
-- TOC entry 2436 (class 2606 OID 66803)
-- Dependencies: 189 2239 187
-- Name: fkfe14c44f52429f27; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_labels
    ADD CONSTRAINT fkfe14c44f52429f27 FOREIGN KEY (indicator_indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;


-- Completed on 2012-09-25 15:33:24

--
-- PostgreSQL database dump complete
--

