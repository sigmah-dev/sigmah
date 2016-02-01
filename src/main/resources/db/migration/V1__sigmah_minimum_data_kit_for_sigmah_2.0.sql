--
-- TOC entry 161 (class 1259 OID 201851)
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
-- TOC entry 162 (class 1259 OID 201857)
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
-- TOC entry 163 (class 1259 OID 201860)
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
-- TOC entry 164 (class 1259 OID 201863)
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
    id_project integer,
    name character varying(255)
);


--
-- TOC entry 165 (class 1259 OID 201866)
-- Dependencies: 5
-- Name: amendment_history_token; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE amendment_history_token (
    amendment_id_amendment integer NOT NULL,
    values_id_history_token integer NOT NULL
);


--
-- TOC entry 166 (class 1259 OID 201869)
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
-- TOC entry 167 (class 1259 OID 201872)
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
-- TOC entry 168 (class 1259 OID 201875)
-- Dependencies: 5
-- Name: attributegroupinactivity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE attributegroupinactivity (
    attributegroupid integer NOT NULL,
    activityid integer NOT NULL
);


--
-- TOC entry 169 (class 1259 OID 201878)
-- Dependencies: 5
-- Name: attributevalue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE attributevalue (
    attributeid integer NOT NULL,
    siteid integer NOT NULL,
    value boolean
);


--
-- TOC entry 170 (class 1259 OID 201881)
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
-- TOC entry 171 (class 1259 OID 201884)
-- Dependencies: 5
-- Name: budget; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget (
    id_budget bigint NOT NULL,
    total_amount real NOT NULL
);


--
-- TOC entry 172 (class 1259 OID 201887)
-- Dependencies: 5
-- Name: budget_distribution_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget_distribution_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 270 (class 1259 OID 203425)
-- Dependencies: 5
-- Name: budget_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget_element (
    id_flexible_element bigint NOT NULL,
    id_ratio_divisor bigint,
    id_ratio_dividend bigint
);


--
-- TOC entry 173 (class 1259 OID 201890)
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
-- TOC entry 174 (class 1259 OID 201896)
-- Dependencies: 5
-- Name: budget_parts_list_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget_parts_list_value (
    id_budget_parts_list bigint NOT NULL,
    id_budget bigint NOT NULL
);


--
-- TOC entry 271 (class 1259 OID 203435)
-- Dependencies: 5
-- Name: budget_sub_field; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE budget_sub_field (
    id_budget_sub_field bigint NOT NULL,
    label character varying(255),
    id_budget_element bigint NOT NULL,
    fieldorder integer,
    type character varying(255)
);


--
-- TOC entry 175 (class 1259 OID 201899)
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
-- TOC entry 176 (class 1259 OID 201905)
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
-- TOC entry 177 (class 1259 OID 201911)
-- Dependencies: 5
-- Name: checkbox_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE checkbox_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 278 (class 1259 OID 203550)
-- Dependencies: 5
-- Name: core_version_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE core_version_element (
    id_flexible_element integer NOT NULL
);


--
-- TOC entry 178 (class 1259 OID 201914)
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
-- TOC entry 179 (class 1259 OID 201917)
-- Dependencies: 5
-- Name: default_flexible_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE default_flexible_element (
    type character varying(255),
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 180 (class 1259 OID 201920)
-- Dependencies: 5
-- Name: file_meta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE file_meta (
    id_file integer NOT NULL,
    datedeleted timestamp without time zone,
    name text NOT NULL
);


--
-- TOC entry 181 (class 1259 OID 201926)
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
-- TOC entry 182 (class 1259 OID 201932)
-- Dependencies: 5
-- Name: files_list_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE files_list_element (
    max_limit integer,
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 183 (class 1259 OID 201935)
-- Dependencies: 2241 2242 2243 2244 5
-- Name: flexible_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE flexible_element (
    id_flexible_element bigint NOT NULL,
    amendable boolean NOT NULL,
    label text,
    validates boolean NOT NULL,
    id_privacy_group integer,
    exportable boolean DEFAULT true NOT NULL,
    globally_exportable boolean DEFAULT true NOT NULL,
    creation_date timestamp without time zone DEFAULT now() NOT NULL,
    is_disabled boolean DEFAULT false,
    disabled_date timestamp without time zone
);


--
-- TOC entry 184 (class 1259 OID 201943)
-- Dependencies: 5
-- Name: global_export; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE global_export (
    id bigint NOT NULL,
    generated_date timestamp without time zone NOT NULL,
    organization_id integer NOT NULL
);


--
-- TOC entry 185 (class 1259 OID 201946)
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
-- TOC entry 186 (class 1259 OID 201952)
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
-- TOC entry 187 (class 1259 OID 201958)
-- Dependencies: 5
-- Name: global_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE global_permission (
    id_global_permission integer NOT NULL,
    permission character varying(255) NOT NULL,
    id_profile integer NOT NULL
);


--
-- TOC entry 188 (class 1259 OID 201961)
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
-- TOC entry 2799 (class 0 OID 0)
-- Dependencies: 188
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('hibernate_sequence', 1, true);


--
-- TOC entry 189 (class 1259 OID 201963)
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
    id_user integer,
    comment character varying(255),
    core_version integer
);


--
-- TOC entry 272 (class 1259 OID 203459)
-- Dependencies: 5
-- Name: importation_scheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE importation_scheme (
    sch_id bigint NOT NULL,
    datedeleted timestamp without time zone,
    sch_file_format character varying(255) NOT NULL,
    sch_first_row integer,
    sch_import_type character varying(255) NOT NULL,
    sch_name character varying(255) NOT NULL,
    sch_sheet_name character varying(255)
);


--
-- TOC entry 273 (class 1259 OID 203467)
-- Dependencies: 5
-- Name: importation_scheme_model; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE importation_scheme_model (
    sch_mod_id bigint NOT NULL,
    datedeleted timestamp without time zone,
    sch_id bigint NOT NULL,
    org_unit_model_id integer,
    id_project_model bigint
);


--
-- TOC entry 274 (class 1259 OID 203487)
-- Dependencies: 5
-- Name: importation_scheme_variable; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE importation_scheme_variable (
    var_id bigint NOT NULL,
    datedeleted timestamp without time zone,
    var_name character varying(255) NOT NULL,
    var_reference character varying(255) NOT NULL,
    sch_id bigint NOT NULL
);


--
-- TOC entry 276 (class 1259 OID 203520)
-- Dependencies: 5
-- Name: importation_scheme_variable_budget_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE importation_scheme_variable_budget_element (
    var_fle_id bigint NOT NULL
);


--
-- TOC entry 275 (class 1259 OID 203500)
-- Dependencies: 5
-- Name: importation_scheme_variable_flexible_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE importation_scheme_variable_flexible_element (
    var_fle_id bigint NOT NULL,
    datedeleted timestamp without time zone,
    var_fle_is_key boolean,
    id_flexible_element bigint NOT NULL,
    sch_mod_id bigint NOT NULL,
    var_id bigint
);


--
-- TOC entry 277 (class 1259 OID 203530)
-- Dependencies: 5
-- Name: importation_variable_budget_sub_field; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE importation_variable_budget_sub_field (
    id_budget_sub_field bigint NOT NULL,
    var_id bigint NOT NULL,
    var_fle_id bigint NOT NULL
);


--
-- TOC entry 190 (class 1259 OID 201969)
-- Dependencies: 2245 5
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
-- TOC entry 191 (class 1259 OID 201976)
-- Dependencies: 5
-- Name: indicator_datasource; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicator_datasource (
    indicatorid integer NOT NULL,
    indicatorsourceid integer NOT NULL
);


--
-- TOC entry 192 (class 1259 OID 201979)
-- Dependencies: 5
-- Name: indicator_labels; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicator_labels (
    indicator_indicatorid integer NOT NULL,
    element character varying(255),
    code integer NOT NULL
);


--
-- TOC entry 193 (class 1259 OID 201982)
-- Dependencies: 5
-- Name: indicatordatasource; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicatordatasource (
    indicatorid integer NOT NULL,
    indicatorsourceid integer NOT NULL
);


--
-- TOC entry 194 (class 1259 OID 201985)
-- Dependencies: 5
-- Name: indicators_list_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicators_list_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 195 (class 1259 OID 201988)
-- Dependencies: 5
-- Name: indicators_list_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicators_list_value (
    id_indicators_list bigint NOT NULL,
    id_indicator integer NOT NULL
);


--
-- TOC entry 196 (class 1259 OID 201991)
-- Dependencies: 5
-- Name: indicatorvalue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE indicatorvalue (
    indicatorid integer NOT NULL,
    reportingperiodid integer NOT NULL,
    value double precision NOT NULL
);


--
-- TOC entry 197 (class 1259 OID 201994)
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
-- TOC entry 198 (class 1259 OID 201997)
-- Dependencies: 5
-- Name: layout; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE layout (
    id_layout bigint NOT NULL,
    columns_count integer NOT NULL,
    rows_count integer NOT NULL
);


--
-- TOC entry 199 (class 1259 OID 202000)
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
-- TOC entry 200 (class 1259 OID 202003)
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
-- TOC entry 201 (class 1259 OID 202009)
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
-- TOC entry 202 (class 1259 OID 202012)
-- Dependencies: 5
-- Name: locationadminlink; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE locationadminlink (
    adminentityid integer NOT NULL,
    locationid integer NOT NULL
);


--
-- TOC entry 203 (class 1259 OID 202015)
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
-- TOC entry 204 (class 1259 OID 202018)
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
-- TOC entry 205 (class 1259 OID 202024)
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
-- TOC entry 206 (class 1259 OID 202030)
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
-- TOC entry 207 (class 1259 OID 202036)
-- Dependencies: 5
-- Name: log_frame_expected_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_expected_result (
    intervention_logic text,
    id_element integer NOT NULL,
    id_specific_objective integer NOT NULL
);


--
-- TOC entry 208 (class 1259 OID 202042)
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
-- TOC entry 209 (class 1259 OID 202048)
-- Dependencies: 5
-- Name: log_frame_indicators; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_indicators (
    log_frame_element_id_element integer NOT NULL,
    indicators_indicatorid integer NOT NULL
);


--
-- TOC entry 210 (class 1259 OID 202051)
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
-- TOC entry 211 (class 1259 OID 202057)
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
-- TOC entry 212 (class 1259 OID 202063)
-- Dependencies: 5
-- Name: log_frame_specific_objective; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE log_frame_specific_objective (
    intervention_logic text,
    id_element integer NOT NULL,
    id_log_frame integer NOT NULL
);


--
-- TOC entry 213 (class 1259 OID 202069)
-- Dependencies: 5
-- Name: message_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE message_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 214 (class 1259 OID 202072)
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
-- TOC entry 269 (class 1259 OID 203412)
-- Dependencies: 5
-- Name: monitored_point_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE monitored_point_history (
    id_monitored_point_history integer NOT NULL,
    generated_date timestamp without time zone NOT NULL,
    id_monitored_point integer NOT NULL,
    id_user integer NOT NULL,
    value text,
    change_type character varying(255) NOT NULL
);


--
-- TOC entry 215 (class 1259 OID 202078)
-- Dependencies: 5
-- Name: monitored_point_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE monitored_point_list (
    id_monitored_point_list integer NOT NULL
);


--
-- TOC entry 216 (class 1259 OID 202081)
-- Dependencies: 5
-- Name: org_unit_banner; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE org_unit_banner (
    banner_id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_org_unit_model integer
);


--
-- TOC entry 217 (class 1259 OID 202084)
-- Dependencies: 5
-- Name: org_unit_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE org_unit_details (
    details_id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_org_unit_model integer
);


--
-- TOC entry 218 (class 1259 OID 202087)
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
    date_deleted date,
    date_maintenance timestamp without time zone
);


--
-- TOC entry 219 (class 1259 OID 202093)
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
-- TOC entry 220 (class 1259 OID 202099)
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
-- TOC entry 221 (class 1259 OID 202102)
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
-- TOC entry 222 (class 1259 OID 202105)
-- Dependencies: 5
-- Name: partnerindatabase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE partnerindatabase (
    partnerid integer NOT NULL,
    databaseid integer NOT NULL
);


--
-- TOC entry 267 (class 1259 OID 203365)
-- Dependencies: 5
-- Name: password_expiration_policy; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE password_expiration_policy (
    id bigint NOT NULL,
    policy_type character varying(255),
    reference_date date,
    frequency integer,
    reset_for_new_users boolean NOT NULL,
    organization_id integer NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 202108)
-- Dependencies: 5
-- Name: personalcalendar; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE personalcalendar (
    id integer NOT NULL,
    name character varying(255)
);


--
-- TOC entry 224 (class 1259 OID 202111)
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
-- TOC entry 225 (class 1259 OID 202117)
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
-- TOC entry 226 (class 1259 OID 202120)
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
-- TOC entry 227 (class 1259 OID 202126)
-- Dependencies: 5
-- Name: phase_model_definition; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE phase_model_definition (
    id_phase_model_definition integer NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 202129)
-- Dependencies: 5
-- Name: phase_model_sucessors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE phase_model_sucessors (
    id_phase_model bigint NOT NULL,
    id_phase_model_successor bigint NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 202132)
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
-- TOC entry 230 (class 1259 OID 202138)
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
-- TOC entry 231 (class 1259 OID 202141)
-- Dependencies: 5
-- Name: profile; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE profile (
    id_profile integer NOT NULL,
    name character varying(8196) NOT NULL,
    id_organization integer
);


--
-- TOC entry 232 (class 1259 OID 202147)
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
    id_reminder_list integer,
    mainsite integer
);


--
-- TOC entry 233 (class 1259 OID 202150)
-- Dependencies: 5
-- Name: project_banner; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_banner (
    id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_project_model bigint
);


--
-- TOC entry 234 (class 1259 OID 202153)
-- Dependencies: 5
-- Name: project_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_details (
    id integer NOT NULL,
    id_layout bigint NOT NULL,
    id_project_model bigint
);


--
-- TOC entry 235 (class 1259 OID 202156)
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
-- TOC entry 236 (class 1259 OID 202159)
-- Dependencies: 5
-- Name: project_model; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_model (
    id_project_model bigint NOT NULL,
    name character varying(8192) NOT NULL,
    status character varying(255) NOT NULL,
    id_root_phase_model bigint,
    date_deleted date,
    date_maintenance timestamp without time zone
);


--
-- TOC entry 237 (class 1259 OID 202165)
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
-- TOC entry 238 (class 1259 OID 202168)
-- Dependencies: 5
-- Name: project_userlogin; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE project_userlogin (
    project_databaseid integer NOT NULL,
    favoriteusers_userid integer NOT NULL
);


--
-- TOC entry 239 (class 1259 OID 202171)
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
-- TOC entry 240 (class 1259 OID 202174)
-- Dependencies: 5
-- Name: projectreportmodel; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE projectreportmodel (
    id integer NOT NULL,
    name character varying(255),
    id_organization integer
);


--
-- TOC entry 241 (class 1259 OID 202177)
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
-- TOC entry 242 (class 1259 OID 202180)
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
-- TOC entry 243 (class 1259 OID 202183)
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
-- TOC entry 244 (class 1259 OID 202189)
-- Dependencies: 5
-- Name: quality_criterion_children; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE quality_criterion_children (
    id_quality_criterion integer,
    id_quality_criterion_child integer NOT NULL
);


--
-- TOC entry 245 (class 1259 OID 202192)
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
-- TOC entry 246 (class 1259 OID 202198)
-- Dependencies: 5
-- Name: quality_framework; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE quality_framework (
    id_quality_framework integer NOT NULL,
    label character varying(8192) NOT NULL,
    id_organization integer
);


--
-- TOC entry 247 (class 1259 OID 202204)
-- Dependencies: 2246 5
-- Name: question_choice_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE question_choice_element (
    id_choice bigint NOT NULL,
    label character varying(8192) NOT NULL,
    sort_order integer,
    id_category_element integer,
    id_question bigint NOT NULL,
    is_disabled boolean DEFAULT false
);


--
-- TOC entry 248 (class 1259 OID 202210)
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
-- TOC entry 249 (class 1259 OID 202213)
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
-- TOC entry 268 (class 1259 OID 203399)
-- Dependencies: 5
-- Name: reminder_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE reminder_history (
    id_reminder_history integer NOT NULL,
    generated_date timestamp without time zone NOT NULL,
    id_reminder integer NOT NULL,
    id_user integer NOT NULL,
    value text,
    change_type character varying(255) NOT NULL
);


--
-- TOC entry 250 (class 1259 OID 202219)
-- Dependencies: 5
-- Name: reminder_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE reminder_list (
    id_reminder_list integer NOT NULL
);


--
-- TOC entry 251 (class 1259 OID 202222)
-- Dependencies: 5
-- Name: report_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE report_element (
    model_id integer,
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 252 (class 1259 OID 202225)
-- Dependencies: 5
-- Name: report_list_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE report_list_element (
    model_id integer,
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 253 (class 1259 OID 202228)
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
-- TOC entry 254 (class 1259 OID 202234)
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
-- TOC entry 255 (class 1259 OID 202237)
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
-- TOC entry 256 (class 1259 OID 202243)
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
-- TOC entry 257 (class 1259 OID 202249)
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
-- TOC entry 258 (class 1259 OID 202255)
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
-- TOC entry 259 (class 1259 OID 202258)
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
-- TOC entry 260 (class 1259 OID 202264)
-- Dependencies: 5
-- Name: triplets_list_element; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE triplets_list_element (
    id_flexible_element bigint NOT NULL
);


--
-- TOC entry 261 (class 1259 OID 202267)
-- Dependencies: 5
-- Name: user_unit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE user_unit (
    id_user_unit integer NOT NULL,
    id_org_unit integer NOT NULL,
    id_user integer NOT NULL
);


--
-- TOC entry 262 (class 1259 OID 202270)
-- Dependencies: 5
-- Name: user_unit_profiles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE user_unit_profiles (
    id_user_unit integer NOT NULL,
    id_profile integer NOT NULL
);


--
-- TOC entry 263 (class 1259 OID 202273)
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
-- TOC entry 264 (class 1259 OID 202279)
-- Dependencies: 2247 5
-- Name: userlogin; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE userlogin (
    userid integer NOT NULL,
    active boolean DEFAULT true,
    changepasswordkey character varying(34),
    datechangepasswordkeyissued timestamp without time zone,
    email character varying(75) NOT NULL,
    firstname character varying(50),
    password character varying(150),
    locale character varying(10) NOT NULL,
    name character varying(50) NOT NULL,
    newuser boolean NOT NULL,
    id_organization integer,
    last_password_change timestamp without time zone
);


--
-- TOC entry 265 (class 1259 OID 202282)
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
-- TOC entry 266 (class 1259 OID 202285)
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
-- TOC entry 2677 (class 0 OID 201851)
-- Dependencies: 161
-- Data for Name: activity; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2678 (class 0 OID 201857)
-- Dependencies: 162
-- Data for Name: adminentity; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2679 (class 0 OID 201860)
-- Dependencies: 163
-- Data for Name: adminlevel; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (1, false, 'Province', 1, NULL);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (2, false, 'District', 1, 1);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (3, false, 'Territoire', 1, 2);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (4, false, 'Secteur', 1, 3);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (5, true, 'Groupement', 1, 4);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (7, false, 'Zone de Santé', 1, 1);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (8, true, 'Aire de Santé', 1, 7);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (9, false, 'Province', 322, NULL);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (10, false, 'District', 322, 9);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (11, false, 'Territoire', 322, 10);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (12, false, 'Secteur', 322, 11);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (13, true, 'Groupement', 322, 12);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (14, false, 'Zone de Santé', 322, 9);
INSERT INTO adminlevel (adminlevelid, allowadd, name, countryid, parentid) VALUES (15, true, 'Aire de Santé', 322, 14);


--
-- TOC entry 2680 (class 0 OID 201863)
-- Dependencies: 164
-- Data for Name: amendment; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2681 (class 0 OID 201866)
-- Dependencies: 165
-- Data for Name: amendment_history_token; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2682 (class 0 OID 201869)
-- Dependencies: 166
-- Data for Name: attribute; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2683 (class 0 OID 201872)
-- Dependencies: 167
-- Data for Name: attributegroup; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2684 (class 0 OID 201875)
-- Dependencies: 168
-- Data for Name: attributegroupinactivity; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2685 (class 0 OID 201878)
-- Dependencies: 169
-- Data for Name: attributevalue; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2686 (class 0 OID 201881)
-- Dependencies: 170
-- Data for Name: authentication; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2687 (class 0 OID 201884)
-- Dependencies: 171
-- Data for Name: budget; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2688 (class 0 OID 201887)
-- Dependencies: 172
-- Data for Name: budget_distribution_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2785 (class 0 OID 203425)
-- Dependencies: 270
-- Data for Name: budget_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2689 (class 0 OID 201890)
-- Dependencies: 173
-- Data for Name: budget_part; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2690 (class 0 OID 201896)
-- Dependencies: 174
-- Data for Name: budget_parts_list_value; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2786 (class 0 OID 203435)
-- Dependencies: 271
-- Data for Name: budget_sub_field; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2691 (class 0 OID 201899)
-- Dependencies: 175
-- Data for Name: category_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2692 (class 0 OID 201905)
-- Dependencies: 176
-- Data for Name: category_type; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2693 (class 0 OID 201911)
-- Dependencies: 177
-- Data for Name: checkbox_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2793 (class 0 OID 203550)
-- Dependencies: 278
-- Data for Name: core_version_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2694 (class 0 OID 201914)
-- Dependencies: 178
-- Data for Name: country; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (1, 12.187941840000001, 31.306000000000001, -13.45599996, 5.3860981539999999, 'CD', 'Congo, RD');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (249, -180, 180, -90, 180, 'AF', 'Afghanistan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (250, -180, 180, -90, 180, 'AX', 'Åland Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (251, -180, 180, -90, 180, 'AL', 'Albania');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (252, -180, 180, -90, 180, 'DZ', 'Algeria');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (253, -180, 180, -90, 180, 'AS', 'American Samoa');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (254, -180, 180, -90, 180, 'AD', 'Andorra');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (255, -180, 180, -90, 180, 'AO', 'Angola');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (256, -180, 180, -90, 180, 'AI', 'Anguilla');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (257, -180, 180, -90, 180, 'AQ', 'Antarctica');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (258, -180, 180, -90, 180, 'AG', 'Antigua and Barbuda');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (259, -180, 180, -90, 180, 'AR', 'Argentina');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (260, -180, 180, -90, 180, 'AM', 'Armenia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (261, -180, 180, -90, 180, 'AW', 'Aruba');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (262, -180, 180, -90, 180, 'AU', 'Australia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (263, -180, 180, -90, 180, 'AT', 'Austria');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (264, -180, 180, -90, 180, 'AZ', 'Azerbaijan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (265, -180, 180, -90, 180, 'BS', 'Bahamas');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (266, -180, 180, -90, 180, 'BH', 'Bahrain');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (267, -180, 180, -90, 180, 'BD', 'Bangladesh');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (268, -180, 180, -90, 180, 'BB', 'Barbados');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (269, -180, 180, -90, 180, 'BY', 'Belarus');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (270, -180, 180, -90, 180, 'BE', 'Belgium');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (271, -180, 180, -90, 180, 'BZ', 'Belize');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (272, -180, 180, -90, 180, 'BJ', 'Benin');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (273, -180, 180, -90, 180, 'BM', 'Bermuda');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (274, -180, 180, -90, 180, 'BT', 'Bhutan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (275, -180, 180, -90, 180, 'BO', 'Bolivia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (276, -180, 180, -90, 180, 'BA', 'Bosnia And Herzegovina');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (277, -180, 180, -90, 180, 'BW', 'Botswana');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (278, -180, 180, -90, 180, 'BV', 'Bouvet Island');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (279, -180, 180, -90, 180, 'BR', 'Brazil');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (280, -180, 180, -90, 180, 'IO', 'British Indian Ocean Territory');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (281, -180, 180, -90, 180, 'BN', 'Brunei Darussalam');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (282, -180, 180, -90, 180, 'BG', 'Bulgaria');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (283, -180, 180, -90, 180, 'BF', 'Burkina Faso');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (284, -180, 180, -90, 180, 'BI', 'Burundi');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (285, -180, 180, -90, 180, 'KH', 'Cambodia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (286, -180, 180, -90, 180, 'CM', 'Cameroon');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (287, -180, 180, -90, 180, 'CA', 'Canada');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (288, -180, 180, -90, 180, 'CV', 'Cape Verde');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (289, -180, 180, -90, 180, 'KY', 'Cayman Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (290, -180, 180, -90, 180, 'CF', 'Central African Republic');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (291, -180, 180, -90, 180, 'TD', 'Chad');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (292, -180, 180, -90, 180, 'CL', 'Chile');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (293, -180, 180, -90, 180, 'CN', 'China');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (294, -180, 180, -90, 180, 'CX', 'Christmas Island');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (295, -180, 180, -90, 180, 'CC', 'Cocos (Keeling) Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (296, -180, 180, -90, 180, 'CO', 'Colombia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (297, -180, 180, -90, 180, 'KM', 'Comoros');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (298, -180, 180, -90, 180, 'CG', 'Congo');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (300, -180, 180, -90, 180, 'CK', 'Cook Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (301, -180, 180, -90, 180, 'CR', 'Costa Rica');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (302, -180, 180, -90, 180, 'CI', 'Côte D''Ivoire');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (303, -180, 180, -90, 180, 'HR', 'Croatia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (304, -180, 180, -90, 180, 'CU', 'Cuba');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (305, -180, 180, -90, 180, 'CY', 'Cyprus');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (306, -180, 180, -90, 180, 'CZ', 'Czech Republic');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (307, -180, 180, -90, 180, 'DK', 'Denmark');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (308, -180, 180, -90, 180, 'DJ', 'Djibouti');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (309, -180, 180, -90, 180, 'DM', 'Dominica');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (310, -180, 180, -90, 180, 'DO', 'Dominican Republic');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (311, -180, 180, -90, 180, 'EC', 'Ecuador');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (312, -180, 180, -90, 180, 'EG', 'Egypt');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (313, -180, 180, -90, 180, 'SV', 'El Salvador');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (314, -180, 180, -90, 180, 'GQ', 'Equatorial Guinea');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (315, -180, 180, -90, 180, 'ER', 'Eritrea');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (316, -180, 180, -90, 180, 'EE', 'Estonia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (317, -180, 180, -90, 180, 'ET', 'Ethiopia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (318, -180, 180, -90, 180, 'FK', 'Falkland Islands (Malvinas)');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (319, -180, 180, -90, 180, 'FO', 'Faroe Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (320, -180, 180, -90, 180, 'FJ', 'Fiji');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (321, -180, 180, -90, 180, 'FI', 'Finland');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (322, -180, 180, -90, 180, 'FR', 'France');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (323, -180, 180, -90, 180, 'GF', 'French Guiana');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (324, -180, 180, -90, 180, 'PF', 'French Polynesia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (325, -180, 180, -90, 180, 'TF', 'French Southern Territories');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (326, -180, 180, -90, 180, 'GA', 'Gabo');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (327, -180, 180, -90, 180, 'GM', 'Gambia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (328, -180, 180, -90, 180, 'GE', 'Georgia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (329, -180, 180, -90, 180, 'DE', 'Germany');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (330, -180, 180, -90, 180, 'GH', 'Ghana');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (331, -180, 180, -90, 180, 'GI', 'Gibraltar');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (332, -180, 180, -90, 180, 'GR', 'Greece');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (333, -180, 180, -90, 180, 'GL', 'Greenland');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (334, -180, 180, -90, 180, 'GD', 'Grenada');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (335, -180, 180, -90, 180, 'GP', 'Guadeloupe');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (336, -180, 180, -90, 180, 'GU', 'Guam');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (337, -180, 180, -90, 180, 'GT', 'Guatemala');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (338, -180, 180, -90, 180, 'GG', 'Guernsey');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (339, -180, 180, -90, 180, 'GN', 'Guinea');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (340, -180, 180, -90, 180, 'GW', 'Guinea-Bissau');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (341, -180, 180, -90, 180, 'GY', 'Guyana');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (342, -180, 180, -90, 180, 'HT', 'Haiti');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (343, -180, 180, -90, 180, 'HM', 'Heard Island and Mcdonald Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (344, -180, 180, -90, 180, 'VA', 'Vatican City');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (345, -180, 180, -90, 180, 'HN', 'Honduras');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (346, -180, 180, -90, 180, 'HK', 'Hong Kong');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (347, -180, 180, -90, 180, 'HU', 'Hungary');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (348, -180, 180, -90, 180, 'IS', 'Iceland');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (349, -180, 180, -90, 180, 'IN', 'India');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (350, -180, 180, -90, 180, 'ID', 'Indonesia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (351, -180, 180, -90, 180, 'IR', 'Iran');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (352, -180, 180, -90, 180, 'IQ', 'Iraq');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (353, -180, 180, -90, 180, 'IE', 'Ireland');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (354, -180, 180, -90, 180, 'IM', 'Isle of Man');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (355, -180, 180, -90, 180, 'IL', 'Israel');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (356, -180, 180, -90, 180, 'IT', 'Italy');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (357, -180, 180, -90, 180, 'JM', 'Jamaica');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (358, -180, 180, -90, 180, 'JP', 'Japan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (359, -180, 180, -90, 180, 'JE', 'Jersey');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (360, -180, 180, -90, 180, 'JO', 'Jordan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (361, -180, 180, -90, 180, 'KZ', 'Kazakhstan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (362, -180, 180, -90, 180, 'KE', 'Kenya');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (363, -180, 180, -90, 180, 'KI', 'Kiribati');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (364, -180, 180, -90, 180, 'KP', 'Democratic People''s Republic of Korea');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (365, -180, 180, -90, 180, 'KR', 'Republic of Korea');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (366, -180, 180, -90, 180, 'KW', 'Kuwait');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (367, -180, 180, -90, 180, 'KG', 'Kyrgyzstan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (368, -180, 180, -90, 180, 'LA', 'Laos');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (369, -180, 180, -90, 180, 'LV', 'Latvia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (370, -180, 180, -90, 180, 'LB', 'Lebanon');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (371, -180, 180, -90, 180, 'LS', 'Lesotho');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (372, -180, 180, -90, 180, 'LR', 'Liberia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (373, -180, 180, -90, 180, 'LY', 'Libyan Arab Jamahiriya');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (374, -180, 180, -90, 180, 'LI', 'Liechtenstein');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (375, -180, 180, -90, 180, 'LT', 'Lithuania');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (376, -180, 180, -90, 180, 'LU', 'Luxembourg');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (377, -180, 180, -90, 180, 'MO', 'Macao');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (378, -180, 180, -90, 180, 'MK', 'Macedonia, FYRO');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (379, -180, 180, -90, 180, 'MG', 'Madagascar');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (380, -180, 180, -90, 180, 'MW', 'Malawi');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (381, -180, 180, -90, 180, 'MY', 'Malaysia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (382, -180, 180, -90, 180, 'MV', 'Maldives');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (383, -180, 180, -90, 180, 'ML', 'Mali');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (384, -180, 180, -90, 180, 'MT', 'Malta');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (385, -180, 180, -90, 180, 'MH', 'Marshall Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (386, -180, 180, -90, 180, 'MQ', 'Martinique');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (387, -180, 180, -90, 180, 'MR', 'Mauritania');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (388, -180, 180, -90, 180, 'MU', 'Mauritius');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (389, -180, 180, -90, 180, 'YT', 'Mayotte');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (390, -180, 180, -90, 180, 'MX', 'Mexico');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (391, -180, 180, -90, 180, 'FM', 'Micronesia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (392, -180, 180, -90, 180, 'MD', 'Moldova');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (393, -180, 180, -90, 180, 'MC', 'Monaco');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (394, -180, 180, -90, 180, 'MN', 'Mongolia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (395, -180, 180, -90, 180, 'ME', 'Montenegro');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (396, -180, 180, -90, 180, 'MS', 'Montserrat');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (397, -180, 180, -90, 180, 'MA', 'Morocco');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (398, -180, 180, -90, 180, 'MZ', 'Mozambique');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (399, -180, 180, -90, 180, 'MM', 'Myanmar');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (400, -180, 180, -90, 180, 'NA', 'Namibia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (401, -180, 180, -90, 180, 'NR', 'Nauru');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (402, -180, 180, -90, 180, 'NP', 'Nepal');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (403, -180, 180, -90, 180, 'NL', 'Netherlands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (404, -180, 180, -90, 180, 'AN', 'Netherlands Antilles');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (405, -180, 180, -90, 180, 'NC', 'New Caledonia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (406, -180, 180, -90, 180, 'NZ', 'New Zealand');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (407, -180, 180, -90, 180, 'NI', 'Nicaragua');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (408, -180, 180, -90, 180, 'NE', 'Niger');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (409, -180, 180, -90, 180, 'NG', 'Nigeria');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (410, -180, 180, -90, 180, 'NU', 'Niue');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (411, -180, 180, -90, 180, 'NF', 'Norfolk Island');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (412, -180, 180, -90, 180, 'MP', 'Northern Mariana Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (413, -180, 180, -90, 180, 'NO', 'Norway');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (414, -180, 180, -90, 180, 'OM', 'Oman');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (415, -180, 180, -90, 180, 'PK', 'Pakistan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (416, -180, 180, -90, 180, 'PW', 'Palau');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (417, -180, 180, -90, 180, 'PS', 'Palestinian Territory, Occupied');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (418, -180, 180, -90, 180, 'PA', 'Panama');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (419, -180, 180, -90, 180, 'PG', 'Papua New Guinea');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (420, -180, 180, -90, 180, 'PY', 'Paraguay');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (421, -180, 180, -90, 180, 'PE', 'Peru');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (422, -180, 180, -90, 180, 'PH', 'Philippines');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (423, -180, 180, -90, 180, 'PN', 'Pitcair');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (424, -180, 180, -90, 180, 'PL', 'Poland');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (425, -180, 180, -90, 180, 'PT', 'Portugal');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (426, -180, 180, -90, 180, 'PR', 'Puerto Rico');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (427, -180, 180, -90, 180, 'QA', 'Qatar');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (428, -180, 180, -90, 180, 'RE', 'Réunion');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (429, -180, 180, -90, 180, 'RO', 'Romania');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (430, -180, 180, -90, 180, 'RU', 'Russian Federation');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (431, -180, 180, -90, 180, 'RW', 'Rwanda');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (432, -180, 180, -90, 180, 'BL', 'Saint Barthélemy');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (433, -180, 180, -90, 180, 'SH', 'Saint Helena');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (434, -180, 180, -90, 180, 'KN', 'Saint Kitts And Nevis');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (435, -180, 180, -90, 180, 'LC', 'Saint Lucia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (436, -180, 180, -90, 180, 'MF', 'Saint Martin');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (437, -180, 180, -90, 180, 'PM', 'Saint Pierre And Miquelon');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (438, -180, 180, -90, 180, 'VC', 'Saint Vincent And The Grenadines');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (439, -180, 180, -90, 180, 'WS', 'Samoa');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (440, -180, 180, -90, 180, 'SM', 'San Marino');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (441, -180, 180, -90, 180, 'ST', 'Sao Tome And Principe');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (442, -180, 180, -90, 180, 'SA', 'Saudi Arabia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (443, -180, 180, -90, 180, 'SN', 'Senegal');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (444, -180, 180, -90, 180, 'RS', 'Serbia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (445, -180, 180, -90, 180, 'SC', 'Seychelles');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (446, -180, 180, -90, 180, 'SL', 'Sierra Leone');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (447, -180, 180, -90, 180, 'SG', 'Singapore');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (448, -180, 180, -90, 180, 'SK', 'Slovakia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (449, -180, 180, -90, 180, 'SI', 'Slovenia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (450, -180, 180, -90, 180, 'SB', 'Solomon Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (451, -180, 180, -90, 180, 'SO', 'Somalia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (452, -180, 180, -90, 180, 'ZA', 'South Africa');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (453, -180, 180, -90, 180, 'GS', 'South Georgia And The South Sandwich Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (454, -180, 180, -90, 180, 'ES', 'Spain');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (455, -180, 180, -90, 180, 'LK', 'Sri Lanka');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (456, -180, 180, -90, 180, 'SD', 'Sudan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (457, -180, 180, -90, 180, 'SR', 'Suriname');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (458, -180, 180, -90, 180, 'SJ', 'Svalbard And Jan Maye');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (459, -180, 180, -90, 180, 'SZ', 'Swaziland');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (460, -180, 180, -90, 180, 'SE', 'Sweden');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (461, -180, 180, -90, 180, 'CH', 'Switzerland');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (462, -180, 180, -90, 180, 'SY', 'Syrian Arab Republic');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (463, -180, 180, -90, 180, 'TW', 'Taiwan, Province Of China');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (464, -180, 180, -90, 180, 'TJ', 'Tajikistan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (465, -180, 180, -90, 180, 'TZ', 'Tanzania, United Republic Of');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (466, -180, 180, -90, 180, 'TH', 'Thailand');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (467, -180, 180, -90, 180, 'TL', 'Timor-Leste');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (468, -180, 180, -90, 180, 'TG', 'Togo');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (469, -180, 180, -90, 180, 'TK', 'Tokelau');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (470, -180, 180, -90, 180, 'TO', 'Tonga');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (471, -180, 180, -90, 180, 'TT', 'Trinidad And Tobago');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (472, -180, 180, -90, 180, 'TN', 'Tunisia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (473, -180, 180, -90, 180, 'TR', 'Turkey');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (474, -180, 180, -90, 180, 'TM', 'Turkmenistan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (475, -180, 180, -90, 180, 'TC', 'Turks And Caicos Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (476, -180, 180, -90, 180, 'TV', 'Tuvalu');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (477, -180, 180, -90, 180, 'UG', 'Uganda');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (478, -180, 180, -90, 180, 'UA', 'Ukraine');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (479, -180, 180, -90, 180, 'AE', 'United Arab Emirates');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (480, -180, 180, -90, 180, 'GB', 'United Kingdom');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (481, -180, 180, -90, 180, 'US', 'United States');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (482, -180, 180, -90, 180, 'UM', 'United States Minor Outlying Islands');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (483, -180, 180, -90, 180, 'UY', 'Uruguay');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (484, -180, 180, -90, 180, 'UZ', 'Uzbekistan');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (485, -180, 180, -90, 180, 'VU', 'Vanuatu');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (486, -180, 180, -90, 180, 'VE', 'Venezuela, Bolivarian Republic Of');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (487, -180, 180, -90, 180, 'VN', 'Viet Nam');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (488, -180, 180, -90, 180, 'VG', 'Virgin Islands, British');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (489, -180, 180, -90, 180, 'VI', 'Virgin Islands, U.S.');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (490, -180, 180, -90, 180, 'WF', 'Wallis And Futuna');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (491, -180, 180, -90, 180, 'EH', 'Western Sahara');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (492, -180, 180, -90, 180, 'YE', 'Yemen');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (493, -180, 180, -90, 180, 'ZM', 'Zambia');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (494, -180, 180, -90, 180, 'ZW', 'Zimbabwe');
INSERT INTO country (countryid, x1, x2, y1, y2, iso2, name) VALUES (495, -180, 180, -90, 180, 'SS', 'South Sudan');


--
-- TOC entry 2695 (class 0 OID 201917)
-- Dependencies: 179
-- Data for Name: default_flexible_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2696 (class 0 OID 201920)
-- Dependencies: 180
-- Data for Name: file_meta; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2697 (class 0 OID 201926)
-- Dependencies: 181
-- Data for Name: file_version; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2698 (class 0 OID 201932)
-- Dependencies: 182
-- Data for Name: files_list_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2699 (class 0 OID 201935)
-- Dependencies: 183
-- Data for Name: flexible_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2700 (class 0 OID 201943)
-- Dependencies: 184
-- Data for Name: global_export; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2701 (class 0 OID 201946)
-- Dependencies: 185
-- Data for Name: global_export_content; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2702 (class 0 OID 201952)
-- Dependencies: 186
-- Data for Name: global_export_settings; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2703 (class 0 OID 201958)
-- Dependencies: 187
-- Data for Name: global_permission; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2704 (class 0 OID 201963)
-- Dependencies: 189
-- Data for Name: history_token; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2787 (class 0 OID 203459)
-- Dependencies: 272
-- Data for Name: importation_scheme; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2788 (class 0 OID 203467)
-- Dependencies: 273
-- Data for Name: importation_scheme_model; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2789 (class 0 OID 203487)
-- Dependencies: 274
-- Data for Name: importation_scheme_variable; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2791 (class 0 OID 203520)
-- Dependencies: 276
-- Data for Name: importation_scheme_variable_budget_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2790 (class 0 OID 203500)
-- Dependencies: 275
-- Data for Name: importation_scheme_variable_flexible_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2792 (class 0 OID 203530)
-- Dependencies: 277
-- Data for Name: importation_variable_budget_sub_field; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2705 (class 0 OID 201969)
-- Dependencies: 190
-- Data for Name: indicator; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2706 (class 0 OID 201976)
-- Dependencies: 191
-- Data for Name: indicator_datasource; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2707 (class 0 OID 201979)
-- Dependencies: 192
-- Data for Name: indicator_labels; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2708 (class 0 OID 201982)
-- Dependencies: 193
-- Data for Name: indicatordatasource; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2709 (class 0 OID 201985)
-- Dependencies: 194
-- Data for Name: indicators_list_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2710 (class 0 OID 201988)
-- Dependencies: 195
-- Data for Name: indicators_list_value; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2711 (class 0 OID 201991)
-- Dependencies: 196
-- Data for Name: indicatorvalue; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2712 (class 0 OID 201994)
-- Dependencies: 197
-- Data for Name: keyquestion; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2713 (class 0 OID 201997)
-- Dependencies: 198
-- Data for Name: layout; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2714 (class 0 OID 202000)
-- Dependencies: 199
-- Data for Name: layout_constraint; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2715 (class 0 OID 202003)
-- Dependencies: 200
-- Data for Name: layout_group; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2716 (class 0 OID 202009)
-- Dependencies: 201
-- Data for Name: location; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2717 (class 0 OID 202012)
-- Dependencies: 202
-- Data for Name: locationadminlink; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2718 (class 0 OID 202015)
-- Dependencies: 203
-- Data for Name: locationtype; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (1, 'Localité', false, NULL, 1);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (2, 'Ecole Primaire', true, NULL, 1);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (3, 'Centre de Santé', true, NULL, 1);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (4, 'Aire de Santé', true, 8, 1);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (5, 'Territoire', true, 3, 1);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (6, 'Localité', true, NULL, 322);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (7, 'Ecole Primaire', true, NULL, 322);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (8, 'Centre de Santé', true, NULL, 322);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (9, 'Aire de Santé', true, 8, 322);
INSERT INTO locationtype (locationtypeid, name, reuse, boundadminlevelid, countryid) VALUES (10, 'Territoire', true, 3, 322);


--
-- TOC entry 2719 (class 0 OID 202018)
-- Dependencies: 204
-- Data for Name: log_frame; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2720 (class 0 OID 202024)
-- Dependencies: 205
-- Data for Name: log_frame_activity; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2721 (class 0 OID 202030)
-- Dependencies: 206
-- Data for Name: log_frame_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2722 (class 0 OID 202036)
-- Dependencies: 207
-- Data for Name: log_frame_expected_result; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2723 (class 0 OID 202042)
-- Dependencies: 208
-- Data for Name: log_frame_group; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2724 (class 0 OID 202048)
-- Dependencies: 209
-- Data for Name: log_frame_indicators; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2725 (class 0 OID 202051)
-- Dependencies: 210
-- Data for Name: log_frame_model; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2726 (class 0 OID 202057)
-- Dependencies: 211
-- Data for Name: log_frame_prerequisite; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2727 (class 0 OID 202063)
-- Dependencies: 212
-- Data for Name: log_frame_specific_objective; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2728 (class 0 OID 202069)
-- Dependencies: 213
-- Data for Name: message_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2729 (class 0 OID 202072)
-- Dependencies: 214
-- Data for Name: monitored_point; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2784 (class 0 OID 203412)
-- Dependencies: 269
-- Data for Name: monitored_point_history; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2730 (class 0 OID 202078)
-- Dependencies: 215
-- Data for Name: monitored_point_list; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2731 (class 0 OID 202081)
-- Dependencies: 216
-- Data for Name: org_unit_banner; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2732 (class 0 OID 202084)
-- Dependencies: 217
-- Data for Name: org_unit_details; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2733 (class 0 OID 202087)
-- Dependencies: 218
-- Data for Name: org_unit_model; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2734 (class 0 OID 202093)
-- Dependencies: 219
-- Data for Name: organization; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2735 (class 0 OID 202099)
-- Dependencies: 220
-- Data for Name: orgunitpermission; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2736 (class 0 OID 202102)
-- Dependencies: 221
-- Data for Name: partner; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2737 (class 0 OID 202105)
-- Dependencies: 222
-- Data for Name: partnerindatabase; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2782 (class 0 OID 203365)
-- Dependencies: 267
-- Data for Name: password_expiration_policy; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2738 (class 0 OID 202108)
-- Dependencies: 223
-- Data for Name: personalcalendar; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2739 (class 0 OID 202111)
-- Dependencies: 224
-- Data for Name: personalevent; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2740 (class 0 OID 202117)
-- Dependencies: 225
-- Data for Name: phase; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2741 (class 0 OID 202120)
-- Dependencies: 226
-- Data for Name: phase_model; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2742 (class 0 OID 202126)
-- Dependencies: 227
-- Data for Name: phase_model_definition; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2743 (class 0 OID 202129)
-- Dependencies: 228
-- Data for Name: phase_model_sucessors; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2744 (class 0 OID 202132)
-- Dependencies: 229
-- Data for Name: privacy_group; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2745 (class 0 OID 202138)
-- Dependencies: 230
-- Data for Name: privacy_group_permission; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2746 (class 0 OID 202141)
-- Dependencies: 231
-- Data for Name: profile; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2747 (class 0 OID 202147)
-- Dependencies: 232
-- Data for Name: project; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2748 (class 0 OID 202150)
-- Dependencies: 233
-- Data for Name: project_banner; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2749 (class 0 OID 202153)
-- Dependencies: 234
-- Data for Name: project_details; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2750 (class 0 OID 202156)
-- Dependencies: 235
-- Data for Name: project_funding; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2751 (class 0 OID 202159)
-- Dependencies: 236
-- Data for Name: project_model; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2752 (class 0 OID 202165)
-- Dependencies: 237
-- Data for Name: project_model_visibility; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2753 (class 0 OID 202168)
-- Dependencies: 238
-- Data for Name: project_userlogin; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2754 (class 0 OID 202171)
-- Dependencies: 239
-- Data for Name: projectreport; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2755 (class 0 OID 202174)
-- Dependencies: 240
-- Data for Name: projectreportmodel; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2756 (class 0 OID 202177)
-- Dependencies: 241
-- Data for Name: projectreportmodelsection; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2757 (class 0 OID 202180)
-- Dependencies: 242
-- Data for Name: projectreportversion; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2758 (class 0 OID 202183)
-- Dependencies: 243
-- Data for Name: quality_criterion; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (1, 'A', 'The project responds to a demonstrated need', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (2, 'B', 'The project achieves its objectives', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (3, 'C', 'The project removes or reduces the risk of negativ', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (4, 'D', 'The project aims for positive impacts beyond imple', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (5, 'E', 'The project is consistent with the agency’s mandat', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (6, 'F', 'The project respects the population', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (7, 'G', 'The project is flexible', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (8, 'H', 'The project is integrated in its institutional con', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (9, 'I', 'The agency has the necessary resources and experti', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (10, 'J', 'The agency has the appropriate management capacity', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (11, 'K', 'The agency makes optimal use of resources', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (12, 'L', 'The agency uses lessons drawn from experience', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (13, 'A1', 'People''s needs are identified and monitored', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (14, 'A2', 'The origins of people''s needs are analysed and tak', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (15, 'A3', 'The project responds to clearly defined needs', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (16, 'A4', 'The decision not to address all of the identified ', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (17, 'B1', 'Several operational strategies are explored', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (18, 'B2', 'Constraints are analysed and taken into account', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (19, 'B3', 'The project measures its progress towards achievin', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (20, 'C1', 'The risk of negative impacts on the environment is', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (21, 'C2', 'The risk of negative impacts on local economy and ', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (22, 'C3', 'The risk of negative impacts on the social and pol', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (23, 'C4', 'The risk of negative impacts on people''s security ', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (24, 'D1', 'The project purpose is identified', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (25, 'D2', 'The project strengthens people''s capacity to cope ', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (26, 'D3', 'The post-project period is thought about and plann', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (27, 'D4', 'Where appropriate, disaster-preparedness and/or pr', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (28, 'D5', 'Where appropriate, the project aims for economic a', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (29, 'E1', 'The agency''s mandate and principles are clearly de', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (30, 'E2', 'Political and legal issues relating to the crisis ', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (31, 'E3', 'The agency makes its position on the crisis clear', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (32, 'E4', 'The risk of the project being manipulated is ident', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (33, 'F1', 'Teams are aware of the appropriate behaviour they ', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (34, 'F2', 'The population is informed, consulted and involved', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (35, 'F3', 'The project takes into account the cultural, socia', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (36, 'F4', 'Necessary measures are taken to remove or reduce t', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (37, 'G1', 'Context changes are anticipated and monitored (ant', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (38, 'G2', 'The project is adapted in relation to context chan', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (39, 'H1', 'Actors and their activities are identified', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (40, 'H2', 'Effective coordination links the project with othe', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (41, 'H3', 'Opportunities to cooperate with other actors are e', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (42, 'I1', 'Necessary and available resources are estimated co', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (43, 'I2', 'Staff and other people involved in the project hav', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (44, 'I3', 'An appropriate amount of time is allocated to each', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (45, 'I4', 'The project is compatible with available resources', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (46, 'J1', 'Reporting lines and decision-making responsibiliti', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (47, 'J2', 'Good team management enables the project to run sm', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (48, 'J3', 'The methods used for collecting and processing inf', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (49, 'J4', 'Administrative, financial and logistics management', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (50, 'J5', 'The risks affecting project equipment are identifi', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (51, 'J6', 'The risks faced by your team are identified, taken', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (52, 'K1', 'The chosen strategy ensures optimal impact', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (53, 'K2', 'Project coverage is optimal', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (54, 'K3', 'Available resources are mobilised and used rationa', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (55, 'L1', 'The agency records relevant information over the c', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (56, 'L2', 'The agency learns lessons from experience', NULL, 1);
INSERT INTO quality_criterion (id_quality_criterion, code, label, id_organization, id_quality_framework) VALUES (57, 'L3', 'The agency uses lessons learnt from experience', NULL, 1);


--
-- TOC entry 2759 (class 0 OID 202189)
-- Dependencies: 244
-- Data for Name: quality_criterion_children; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (1, 13);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (1, 14);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (1, 15);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (1, 16);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (2, 17);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (2, 18);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (2, 19);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (3, 20);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (3, 21);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (3, 22);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (3, 23);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (4, 24);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (4, 25);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (4, 26);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (4, 27);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (4, 28);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (5, 29);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (5, 30);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (5, 31);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (5, 32);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (6, 33);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (6, 34);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (6, 35);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (6, 36);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (7, 37);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (7, 38);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (8, 39);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (8, 40);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (8, 41);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (9, 42);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (9, 43);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (9, 44);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (9, 45);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (10, 46);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (10, 47);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (10, 48);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (10, 49);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (10, 50);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (10, 51);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (11, 52);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (11, 53);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (11, 54);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (12, 55);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (12, 56);
INSERT INTO quality_criterion_children (id_quality_criterion, id_quality_criterion_child) VALUES (12, 57);


--
-- TOC entry 2760 (class 0 OID 202192)
-- Dependencies: 245
-- Data for Name: quality_criterion_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO quality_criterion_type (id_criterion_type, label, level, id_quality_framework) VALUES (1, 'Criterion', 0, 1);
INSERT INTO quality_criterion_type (id_criterion_type, label, level, id_quality_framework) VALUES (2, 'Key process', 1, 1);


--
-- TOC entry 2761 (class 0 OID 202198)
-- Dependencies: 246
-- Data for Name: quality_framework; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO quality_framework (id_quality_framework, label, id_organization) VALUES (1, 'Quality COMPAS', NULL);


--
-- TOC entry 2762 (class 0 OID 202204)
-- Dependencies: 247
-- Data for Name: question_choice_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2763 (class 0 OID 202210)
-- Dependencies: 248
-- Data for Name: question_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2764 (class 0 OID 202213)
-- Dependencies: 249
-- Data for Name: reminder; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2783 (class 0 OID 203399)
-- Dependencies: 268
-- Data for Name: reminder_history; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2765 (class 0 OID 202219)
-- Dependencies: 250
-- Data for Name: reminder_list; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2766 (class 0 OID 202222)
-- Dependencies: 251
-- Data for Name: report_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2767 (class 0 OID 202225)
-- Dependencies: 252
-- Data for Name: report_list_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2768 (class 0 OID 202228)
-- Dependencies: 253
-- Data for Name: reportingperiod; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2769 (class 0 OID 202234)
-- Dependencies: 254
-- Data for Name: reportsubscription; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2770 (class 0 OID 202237)
-- Dependencies: 255
-- Data for Name: reporttemplate; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2771 (class 0 OID 202243)
-- Dependencies: 256
-- Data for Name: richtextelement; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2772 (class 0 OID 202249)
-- Dependencies: 257
-- Data for Name: site; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2773 (class 0 OID 202255)
-- Dependencies: 258
-- Data for Name: textarea_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2774 (class 0 OID 202258)
-- Dependencies: 259
-- Data for Name: triplet_value; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2775 (class 0 OID 202264)
-- Dependencies: 260
-- Data for Name: triplets_list_element; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2776 (class 0 OID 202267)
-- Dependencies: 261
-- Data for Name: user_unit; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2777 (class 0 OID 202270)
-- Dependencies: 262
-- Data for Name: user_unit_profiles; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2778 (class 0 OID 202273)
-- Dependencies: 263
-- Data for Name: userdatabase; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2779 (class 0 OID 202279)
-- Dependencies: 264
-- Data for Name: userlogin; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2780 (class 0 OID 202282)
-- Dependencies: 265
-- Data for Name: userpermission; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2781 (class 0 OID 202285)
-- Dependencies: 266
-- Data for Name: value; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2249 (class 2606 OID 202292)
-- Dependencies: 161 161
-- Name: activity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT activity_pkey PRIMARY KEY (activityid);


--
-- TOC entry 2251 (class 2606 OID 202294)
-- Dependencies: 162 162
-- Name: adminentity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminentity
    ADD CONSTRAINT adminentity_pkey PRIMARY KEY (adminentityid);


--
-- TOC entry 2253 (class 2606 OID 202296)
-- Dependencies: 163 163
-- Name: adminlevel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT adminlevel_pkey PRIMARY KEY (adminlevelid);


--
-- TOC entry 2255 (class 2606 OID 202298)
-- Dependencies: 164 164
-- Name: amendment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment
    ADD CONSTRAINT amendment_pkey PRIMARY KEY (id_amendment);


--
-- TOC entry 2257 (class 2606 OID 202300)
-- Dependencies: 166 166
-- Name: attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attribute
    ADD CONSTRAINT attribute_pkey PRIMARY KEY (attributeid);


--
-- TOC entry 2259 (class 2606 OID 202302)
-- Dependencies: 167 167
-- Name: attributegroup_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributegroup
    ADD CONSTRAINT attributegroup_pkey PRIMARY KEY (attributegroupid);


--
-- TOC entry 2261 (class 2606 OID 202304)
-- Dependencies: 168 168 168
-- Name: attributegroupinactivity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT attributegroupinactivity_pkey PRIMARY KEY (activityid, attributegroupid);


--
-- TOC entry 2263 (class 2606 OID 202306)
-- Dependencies: 169 169 169
-- Name: attributevalue_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT attributevalue_pkey PRIMARY KEY (attributeid, siteid);


--
-- TOC entry 2265 (class 2606 OID 202308)
-- Dependencies: 170 170
-- Name: authentication_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authentication
    ADD CONSTRAINT authentication_pkey PRIMARY KEY (authtoken);


--
-- TOC entry 2269 (class 2606 OID 202310)
-- Dependencies: 172 172
-- Name: budget_distribution_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_distribution_element
    ADD CONSTRAINT budget_distribution_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2473 (class 2606 OID 203429)
-- Dependencies: 270 270
-- Name: budget_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_element
    ADD CONSTRAINT budget_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2271 (class 2606 OID 202312)
-- Dependencies: 173 173
-- Name: budget_part_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_part
    ADD CONSTRAINT budget_part_pkey PRIMARY KEY (id_budget_part);


--
-- TOC entry 2273 (class 2606 OID 202314)
-- Dependencies: 174 174
-- Name: budget_parts_list_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_parts_list_value
    ADD CONSTRAINT budget_parts_list_value_pkey PRIMARY KEY (id_budget_parts_list);


--
-- TOC entry 2267 (class 2606 OID 202316)
-- Dependencies: 171 171
-- Name: budget_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget
    ADD CONSTRAINT budget_pkey PRIMARY KEY (id_budget);


--
-- TOC entry 2475 (class 2606 OID 203442)
-- Dependencies: 271 271
-- Name: budget_sub_field_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_sub_field
    ADD CONSTRAINT budget_sub_field_pkey PRIMARY KEY (id_budget_sub_field);


--
-- TOC entry 2275 (class 2606 OID 202318)
-- Dependencies: 175 175
-- Name: category_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_element
    ADD CONSTRAINT category_element_pkey PRIMARY KEY (id_category_element);


--
-- TOC entry 2277 (class 2606 OID 202320)
-- Dependencies: 176 176
-- Name: category_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_type
    ADD CONSTRAINT category_type_pkey PRIMARY KEY (id_category_type);


--
-- TOC entry 2279 (class 2606 OID 202322)
-- Dependencies: 177 177
-- Name: checkbox_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY checkbox_element
    ADD CONSTRAINT checkbox_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2489 (class 2606 OID 203554)
-- Dependencies: 278 278
-- Name: core_version_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY core_version_element
    ADD CONSTRAINT core_version_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2281 (class 2606 OID 202324)
-- Dependencies: 178 178
-- Name: country_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_pkey PRIMARY KEY (countryid);


--
-- TOC entry 2283 (class 2606 OID 202326)
-- Dependencies: 179 179
-- Name: default_flexible_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY default_flexible_element
    ADD CONSTRAINT default_flexible_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2285 (class 2606 OID 202328)
-- Dependencies: 180 180
-- Name: file_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_meta
    ADD CONSTRAINT file_meta_pkey PRIMARY KEY (id_file);


--
-- TOC entry 2287 (class 2606 OID 202330)
-- Dependencies: 181 181 181
-- Name: file_version_id_file_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_version
    ADD CONSTRAINT file_version_id_file_key UNIQUE (id_file, version_number);


--
-- TOC entry 2289 (class 2606 OID 202332)
-- Dependencies: 181 181
-- Name: file_version_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_version
    ADD CONSTRAINT file_version_pkey PRIMARY KEY (id_file_version);


--
-- TOC entry 2291 (class 2606 OID 202334)
-- Dependencies: 182 182
-- Name: files_list_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY files_list_element
    ADD CONSTRAINT files_list_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2293 (class 2606 OID 202336)
-- Dependencies: 183 183
-- Name: flexible_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY flexible_element
    ADD CONSTRAINT flexible_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2297 (class 2606 OID 202338)
-- Dependencies: 185 185
-- Name: global_export_content_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export_content
    ADD CONSTRAINT global_export_content_pkey PRIMARY KEY (id);


--
-- TOC entry 2295 (class 2606 OID 202340)
-- Dependencies: 184 184
-- Name: global_export_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export
    ADD CONSTRAINT global_export_pkey PRIMARY KEY (id);


--
-- TOC entry 2299 (class 2606 OID 202342)
-- Dependencies: 186 186
-- Name: global_export_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export_settings
    ADD CONSTRAINT global_export_settings_pkey PRIMARY KEY (id);


--
-- TOC entry 2301 (class 2606 OID 202344)
-- Dependencies: 187 187
-- Name: global_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_permission
    ADD CONSTRAINT global_permission_pkey PRIMARY KEY (id_global_permission);


--
-- TOC entry 2303 (class 2606 OID 202346)
-- Dependencies: 189 189
-- Name: history_token_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY history_token
    ADD CONSTRAINT history_token_pkey PRIMARY KEY (id_history_token);


--
-- TOC entry 2479 (class 2606 OID 203471)
-- Dependencies: 273 273
-- Name: importation_scheme_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_model
    ADD CONSTRAINT importation_scheme_model_pkey PRIMARY KEY (sch_mod_id);


--
-- TOC entry 2477 (class 2606 OID 203466)
-- Dependencies: 272 272
-- Name: importation_scheme_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme
    ADD CONSTRAINT importation_scheme_pkey PRIMARY KEY (sch_id);


--
-- TOC entry 2485 (class 2606 OID 203524)
-- Dependencies: 276 276
-- Name: importation_scheme_variable_budget_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_variable_budget_element
    ADD CONSTRAINT importation_scheme_variable_budget_element_pkey PRIMARY KEY (var_fle_id);


--
-- TOC entry 2483 (class 2606 OID 203504)
-- Dependencies: 275 275
-- Name: importation_scheme_variable_flexible_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_variable_flexible_element
    ADD CONSTRAINT importation_scheme_variable_flexible_element_pkey PRIMARY KEY (var_fle_id);


--
-- TOC entry 2481 (class 2606 OID 203494)
-- Dependencies: 274 274
-- Name: importation_scheme_variable_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_variable
    ADD CONSTRAINT importation_scheme_variable_pkey PRIMARY KEY (var_id);


--
-- TOC entry 2487 (class 2606 OID 203534)
-- Dependencies: 277 277 277 277
-- Name: importation_variable_budget_sub_field_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_variable_budget_sub_field
    ADD CONSTRAINT importation_variable_budget_sub_field_pkey PRIMARY KEY (id_budget_sub_field, var_id, var_fle_id);


--
-- TOC entry 2307 (class 2606 OID 202348)
-- Dependencies: 191 191 191
-- Name: indicator_datasource_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT indicator_datasource_pkey PRIMARY KEY (indicatorid, indicatorsourceid);


--
-- TOC entry 2309 (class 2606 OID 202350)
-- Dependencies: 192 192 192
-- Name: indicator_labels_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_labels
    ADD CONSTRAINT indicator_labels_pkey PRIMARY KEY (indicator_indicatorid, code);


--
-- TOC entry 2305 (class 2606 OID 202352)
-- Dependencies: 190 190
-- Name: indicator_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator
    ADD CONSTRAINT indicator_pkey PRIMARY KEY (indicatorid);


--
-- TOC entry 2311 (class 2606 OID 202354)
-- Dependencies: 193 193 193
-- Name: indicatordatasource_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatordatasource
    ADD CONSTRAINT indicatordatasource_pkey PRIMARY KEY (indicatorid, indicatorsourceid);


--
-- TOC entry 2313 (class 2606 OID 202356)
-- Dependencies: 194 194
-- Name: indicators_list_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicators_list_element
    ADD CONSTRAINT indicators_list_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2315 (class 2606 OID 202358)
-- Dependencies: 195 195 195
-- Name: indicators_list_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicators_list_value
    ADD CONSTRAINT indicators_list_value_pkey PRIMARY KEY (id_indicators_list, id_indicator);


--
-- TOC entry 2317 (class 2606 OID 202360)
-- Dependencies: 196 196 196
-- Name: indicatorvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT indicatorvalue_pkey PRIMARY KEY (indicatorid, reportingperiodid);


--
-- TOC entry 2319 (class 2606 OID 202362)
-- Dependencies: 197 197
-- Name: keyquestion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT keyquestion_pkey PRIMARY KEY (id);


--
-- TOC entry 2323 (class 2606 OID 202364)
-- Dependencies: 199 199
-- Name: layout_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT layout_constraint_pkey PRIMARY KEY (id_layout_constraint);


--
-- TOC entry 2325 (class 2606 OID 202366)
-- Dependencies: 200 200
-- Name: layout_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_group
    ADD CONSTRAINT layout_group_pkey PRIMARY KEY (id_layout_group);


--
-- TOC entry 2321 (class 2606 OID 202368)
-- Dependencies: 198 198
-- Name: layout_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout
    ADD CONSTRAINT layout_pkey PRIMARY KEY (id_layout);


--
-- TOC entry 2327 (class 2606 OID 202370)
-- Dependencies: 201 201
-- Name: location_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY location
    ADD CONSTRAINT location_pkey PRIMARY KEY (locationid);


--
-- TOC entry 2329 (class 2606 OID 202372)
-- Dependencies: 202 202 202
-- Name: locationadminlink_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT locationadminlink_pkey PRIMARY KEY (locationid, adminentityid);


--
-- TOC entry 2331 (class 2606 OID 202374)
-- Dependencies: 203 203
-- Name: locationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationtype
    ADD CONSTRAINT locationtype_pkey PRIMARY KEY (locationtypeid);


--
-- TOC entry 2335 (class 2606 OID 202376)
-- Dependencies: 205 205
-- Name: log_frame_activity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT log_frame_activity_pkey PRIMARY KEY (id_element);


--
-- TOC entry 2337 (class 2606 OID 202378)
-- Dependencies: 206 206
-- Name: log_frame_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_element
    ADD CONSTRAINT log_frame_element_pkey PRIMARY KEY (id_element);


--
-- TOC entry 2339 (class 2606 OID 202380)
-- Dependencies: 207 207
-- Name: log_frame_expected_result_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT log_frame_expected_result_pkey PRIMARY KEY (id_element);


--
-- TOC entry 2341 (class 2606 OID 202382)
-- Dependencies: 208 208
-- Name: log_frame_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_group
    ADD CONSTRAINT log_frame_group_pkey PRIMARY KEY (id_group);


--
-- TOC entry 2343 (class 2606 OID 202384)
-- Dependencies: 209 209 209
-- Name: log_frame_indicators_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT log_frame_indicators_pkey PRIMARY KEY (log_frame_element_id_element, indicators_indicatorid);


--
-- TOC entry 2345 (class 2606 OID 202386)
-- Dependencies: 210 210
-- Name: log_frame_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_model
    ADD CONSTRAINT log_frame_model_pkey PRIMARY KEY (id_log_frame);


--
-- TOC entry 2333 (class 2606 OID 202388)
-- Dependencies: 204 204
-- Name: log_frame_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame
    ADD CONSTRAINT log_frame_pkey PRIMARY KEY (id_log_frame);


--
-- TOC entry 2347 (class 2606 OID 202390)
-- Dependencies: 211 211
-- Name: log_frame_prerequisite_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT log_frame_prerequisite_pkey PRIMARY KEY (id_prerequisite);


--
-- TOC entry 2349 (class 2606 OID 202392)
-- Dependencies: 212 212
-- Name: log_frame_specific_objective_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT log_frame_specific_objective_pkey PRIMARY KEY (id_element);


--
-- TOC entry 2351 (class 2606 OID 202394)
-- Dependencies: 213 213
-- Name: message_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY message_element
    ADD CONSTRAINT message_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2471 (class 2606 OID 203419)
-- Dependencies: 269 269
-- Name: monitored_point_history_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point_history
    ADD CONSTRAINT monitored_point_history_pkey PRIMARY KEY (id_monitored_point_history);


--
-- TOC entry 2355 (class 2606 OID 202396)
-- Dependencies: 215 215
-- Name: monitored_point_list_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point_list
    ADD CONSTRAINT monitored_point_list_pkey PRIMARY KEY (id_monitored_point_list);


--
-- TOC entry 2353 (class 2606 OID 202398)
-- Dependencies: 214 214
-- Name: monitored_point_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT monitored_point_pkey PRIMARY KEY (id_monitored_point);


--
-- TOC entry 2357 (class 2606 OID 202400)
-- Dependencies: 216 216
-- Name: org_unit_banner_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT org_unit_banner_pkey PRIMARY KEY (banner_id);


--
-- TOC entry 2359 (class 2606 OID 202402)
-- Dependencies: 217 217
-- Name: org_unit_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT org_unit_details_pkey PRIMARY KEY (details_id);


--
-- TOC entry 2361 (class 2606 OID 202404)
-- Dependencies: 218 218
-- Name: org_unit_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_model
    ADD CONSTRAINT org_unit_model_pkey PRIMARY KEY (org_unit_model_id);


--
-- TOC entry 2363 (class 2606 OID 202406)
-- Dependencies: 219 219
-- Name: organization_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id_organization);


--
-- TOC entry 2365 (class 2606 OID 202408)
-- Dependencies: 220 220
-- Name: orgunitpermission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT orgunitpermission_pkey PRIMARY KEY (id);


--
-- TOC entry 2367 (class 2606 OID 202425)
-- Dependencies: 221 221
-- Name: partner_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT partner_pkey PRIMARY KEY (partnerid);


--
-- TOC entry 2369 (class 2606 OID 202427)
-- Dependencies: 222 222 222
-- Name: partnerindatabase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT partnerindatabase_pkey PRIMARY KEY (databaseid, partnerid);


--
-- TOC entry 2467 (class 2606 OID 203369)
-- Dependencies: 267 267
-- Name: password_expiration_policy_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY password_expiration_policy
    ADD CONSTRAINT password_expiration_policy_pkey PRIMARY KEY (id);


--
-- TOC entry 2371 (class 2606 OID 202429)
-- Dependencies: 223 223
-- Name: personalcalendar_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY personalcalendar
    ADD CONSTRAINT personalcalendar_pkey PRIMARY KEY (id);


--
-- TOC entry 2373 (class 2606 OID 202431)
-- Dependencies: 224 224
-- Name: personalevent_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY personalevent
    ADD CONSTRAINT personalevent_pkey PRIMARY KEY (id);


--
-- TOC entry 2379 (class 2606 OID 202433)
-- Dependencies: 227 227
-- Name: phase_model_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_definition
    ADD CONSTRAINT phase_model_definition_pkey PRIMARY KEY (id_phase_model_definition);


--
-- TOC entry 2377 (class 2606 OID 202435)
-- Dependencies: 226 226
-- Name: phase_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model
    ADD CONSTRAINT phase_model_pkey PRIMARY KEY (id_phase_model);


--
-- TOC entry 2381 (class 2606 OID 202437)
-- Dependencies: 228 228 228
-- Name: phase_model_sucessors_id_phase_model_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT phase_model_sucessors_id_phase_model_key UNIQUE (id_phase_model, id_phase_model_successor);


--
-- TOC entry 2383 (class 2606 OID 202439)
-- Dependencies: 228 228
-- Name: phase_model_sucessors_id_phase_model_successor_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT phase_model_sucessors_id_phase_model_successor_key UNIQUE (id_phase_model_successor);


--
-- TOC entry 2375 (class 2606 OID 202441)
-- Dependencies: 225 225
-- Name: phase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase
    ADD CONSTRAINT phase_pkey PRIMARY KEY (id_phase);


--
-- TOC entry 2387 (class 2606 OID 202443)
-- Dependencies: 230 230
-- Name: privacy_group_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT privacy_group_permission_pkey PRIMARY KEY (id_permission);


--
-- TOC entry 2385 (class 2606 OID 202445)
-- Dependencies: 229 229
-- Name: privacy_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group
    ADD CONSTRAINT privacy_group_pkey PRIMARY KEY (id_privacy_group);


--
-- TOC entry 2389 (class 2606 OID 202447)
-- Dependencies: 231 231
-- Name: profile_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (id_profile);


--
-- TOC entry 2393 (class 2606 OID 202449)
-- Dependencies: 233 233
-- Name: project_banner_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_banner
    ADD CONSTRAINT project_banner_pkey PRIMARY KEY (id);


--
-- TOC entry 2395 (class 2606 OID 202451)
-- Dependencies: 234 234
-- Name: project_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_details
    ADD CONSTRAINT project_details_pkey PRIMARY KEY (id);


--
-- TOC entry 2397 (class 2606 OID 202453)
-- Dependencies: 235 235
-- Name: project_funding_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_funding
    ADD CONSTRAINT project_funding_pkey PRIMARY KEY (id_funding);


--
-- TOC entry 2399 (class 2606 OID 202455)
-- Dependencies: 236 236
-- Name: project_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model
    ADD CONSTRAINT project_model_pkey PRIMARY KEY (id_project_model);


--
-- TOC entry 2401 (class 2606 OID 202457)
-- Dependencies: 237 237
-- Name: project_model_visibility_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT project_model_visibility_pkey PRIMARY KEY (id_visibility);


--
-- TOC entry 2391 (class 2606 OID 202459)
-- Dependencies: 232 232
-- Name: project_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pkey PRIMARY KEY (databaseid);


--
-- TOC entry 2403 (class 2606 OID 202461)
-- Dependencies: 238 238 238
-- Name: project_userlogin_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT project_userlogin_pkey PRIMARY KEY (project_databaseid, favoriteusers_userid);


--
-- TOC entry 2405 (class 2606 OID 202463)
-- Dependencies: 239 239
-- Name: projectreport_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT projectreport_pkey PRIMARY KEY (id);


--
-- TOC entry 2407 (class 2606 OID 202465)
-- Dependencies: 240 240
-- Name: projectreportmodel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodel
    ADD CONSTRAINT projectreportmodel_pkey PRIMARY KEY (id);


--
-- TOC entry 2409 (class 2606 OID 202467)
-- Dependencies: 241 241
-- Name: projectreportmodelsection_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT projectreportmodelsection_pkey PRIMARY KEY (id);


--
-- TOC entry 2411 (class 2606 OID 202469)
-- Dependencies: 242 242
-- Name: projectreportversion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT projectreportversion_pkey PRIMARY KEY (id);


--
-- TOC entry 2415 (class 2606 OID 202471)
-- Dependencies: 244 244 244
-- Name: quality_criterion_children_id_quality_criterion_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT quality_criterion_children_id_quality_criterion_key UNIQUE (id_quality_criterion, id_quality_criterion_child);


--
-- TOC entry 2417 (class 2606 OID 202473)
-- Dependencies: 244 244
-- Name: quality_criterion_children_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT quality_criterion_children_pkey PRIMARY KEY (id_quality_criterion_child);


--
-- TOC entry 2413 (class 2606 OID 202475)
-- Dependencies: 243 243
-- Name: quality_criterion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT quality_criterion_pkey PRIMARY KEY (id_quality_criterion);


--
-- TOC entry 2419 (class 2606 OID 202477)
-- Dependencies: 245 245
-- Name: quality_criterion_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_type
    ADD CONSTRAINT quality_criterion_type_pkey PRIMARY KEY (id_criterion_type);


--
-- TOC entry 2421 (class 2606 OID 202479)
-- Dependencies: 246 246
-- Name: quality_framework_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_framework
    ADD CONSTRAINT quality_framework_pkey PRIMARY KEY (id_quality_framework);


--
-- TOC entry 2423 (class 2606 OID 202481)
-- Dependencies: 247 247
-- Name: question_choice_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT question_choice_element_pkey PRIMARY KEY (id_choice);


--
-- TOC entry 2425 (class 2606 OID 202483)
-- Dependencies: 248 248
-- Name: question_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_element
    ADD CONSTRAINT question_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2469 (class 2606 OID 203406)
-- Dependencies: 268 268
-- Name: reminder_history_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reminder_history
    ADD CONSTRAINT reminder_history_pkey PRIMARY KEY (id_reminder_history);


--
-- TOC entry 2429 (class 2606 OID 202485)
-- Dependencies: 250 250
-- Name: reminder_list_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reminder_list
    ADD CONSTRAINT reminder_list_pkey PRIMARY KEY (id_reminder_list);


--
-- TOC entry 2427 (class 2606 OID 202487)
-- Dependencies: 249 249
-- Name: reminder_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reminder
    ADD CONSTRAINT reminder_pkey PRIMARY KEY (id_reminder);


--
-- TOC entry 2431 (class 2606 OID 202489)
-- Dependencies: 251 251
-- Name: report_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_element
    ADD CONSTRAINT report_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2433 (class 2606 OID 202491)
-- Dependencies: 252 252
-- Name: report_list_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT report_list_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2435 (class 2606 OID 202493)
-- Dependencies: 253 253
-- Name: reportingperiod_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportingperiod
    ADD CONSTRAINT reportingperiod_pkey PRIMARY KEY (reportingperiodid);


--
-- TOC entry 2437 (class 2606 OID 202495)
-- Dependencies: 254 254 254
-- Name: reportsubscription_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT reportsubscription_pkey PRIMARY KEY (reporttemplateid, userid);


--
-- TOC entry 2439 (class 2606 OID 202497)
-- Dependencies: 255 255
-- Name: reporttemplate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT reporttemplate_pkey PRIMARY KEY (reporttemplateid);


--
-- TOC entry 2441 (class 2606 OID 202499)
-- Dependencies: 256 256
-- Name: richtextelement_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY richtextelement
    ADD CONSTRAINT richtextelement_pkey PRIMARY KEY (id);


--
-- TOC entry 2443 (class 2606 OID 202501)
-- Dependencies: 257 257
-- Name: site_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT site_pkey PRIMARY KEY (siteid);


--
-- TOC entry 2445 (class 2606 OID 202503)
-- Dependencies: 258 258
-- Name: textarea_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY textarea_element
    ADD CONSTRAINT textarea_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2447 (class 2606 OID 202505)
-- Dependencies: 259 259
-- Name: triplet_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY triplet_value
    ADD CONSTRAINT triplet_value_pkey PRIMARY KEY (id_triplet);


--
-- TOC entry 2449 (class 2606 OID 202507)
-- Dependencies: 260 260
-- Name: triplets_list_element_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY triplets_list_element
    ADD CONSTRAINT triplets_list_element_pkey PRIMARY KEY (id_flexible_element);


--
-- TOC entry 2451 (class 2606 OID 202509)
-- Dependencies: 261 261
-- Name: user_unit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit
    ADD CONSTRAINT user_unit_pkey PRIMARY KEY (id_user_unit);


--
-- TOC entry 2453 (class 2606 OID 202511)
-- Dependencies: 262 262 262
-- Name: user_unit_profiles_id_user_unit_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT user_unit_profiles_id_user_unit_key UNIQUE (id_user_unit, id_profile);


--
-- TOC entry 2455 (class 2606 OID 202513)
-- Dependencies: 263 263
-- Name: userdatabase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT userdatabase_pkey PRIMARY KEY (databaseid);


--
-- TOC entry 2457 (class 2606 OID 202515)
-- Dependencies: 264 264
-- Name: userlogin_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userlogin
    ADD CONSTRAINT userlogin_email_key UNIQUE (email);


--
-- TOC entry 2459 (class 2606 OID 202517)
-- Dependencies: 264 264
-- Name: userlogin_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userlogin
    ADD CONSTRAINT userlogin_pkey PRIMARY KEY (userid);


--
-- TOC entry 2461 (class 2606 OID 202519)
-- Dependencies: 265 265
-- Name: userpermission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userpermission
    ADD CONSTRAINT userpermission_pkey PRIMARY KEY (userpermissionid);


--
-- TOC entry 2463 (class 2606 OID 202521)
-- Dependencies: 266 266 266
-- Name: value_id_flexible_element_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY value
    ADD CONSTRAINT value_id_flexible_element_key UNIQUE (id_flexible_element, id_project);


--
-- TOC entry 2465 (class 2606 OID 202523)
-- Dependencies: 266 266
-- Name: value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY value
    ADD CONSTRAINT value_pkey PRIMARY KEY (id_value);


--
-- TOC entry 2511 (class 2606 OID 202524)
-- Dependencies: 219 2362 176
-- Name: fk1432f9db87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_type
    ADD CONSTRAINT fk1432f9db87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2647 (class 2606 OID 202529)
-- Dependencies: 221 261 2366
-- Name: fk143d4d78b7206e89; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit
    ADD CONSTRAINT fk143d4d78b7206e89 FOREIGN KEY (id_org_unit) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2648 (class 2606 OID 202534)
-- Dependencies: 2458 264 261
-- Name: fk143d4d78dd0ca99c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit
    ADD CONSTRAINT fk143d4d78dd0ca99c FOREIGN KEY (id_user) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2567 (class 2606 OID 202539)
-- Dependencies: 218 2362 219
-- Name: fk15d234e987d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_model
    ADD CONSTRAINT fk15d234e987d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2623 (class 2606 OID 202544)
-- Dependencies: 2274 247 175
-- Name: fk17871bd711158eaf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT fk17871bd711158eaf FOREIGN KEY (id_category_element) REFERENCES category_element(id_category_element) DEFERRABLE;


--
-- TOC entry 2624 (class 2606 OID 202549)
-- Dependencies: 2424 248 247
-- Name: fk17871bd7d92f832c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT fk17871bd7d92f832c FOREIGN KEY (id_question) REFERENCES question_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2553 (class 2606 OID 202554)
-- Dependencies: 209 190 2304
-- Name: fk17e5a9f1a023ddc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT fk17e5a9f1a023ddc FOREIGN KEY (indicators_indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2554 (class 2606 OID 202559)
-- Dependencies: 209 206 2336
-- Name: fk17e5a9f1f6e4c4b8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT fk17e5a9f1f6e4c4b8 FOREIGN KEY (log_frame_element_id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;


--
-- TOC entry 2663 (class 2606 OID 203448)
-- Dependencies: 2474 270 271
-- Name: fk1ba0600222f4f59; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_element
    ADD CONSTRAINT fk1ba0600222f4f59 FOREIGN KEY (id_ratio_divisor) REFERENCES budget_sub_field(id_budget_sub_field);


--
-- TOC entry 2664 (class 2606 OID 203453)
-- Dependencies: 270 271 2474
-- Name: fk1ba06002a2a3285a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_element
    ADD CONSTRAINT fk1ba06002a2a3285a FOREIGN KEY (id_ratio_dividend) REFERENCES budget_sub_field(id_budget_sub_field);


--
-- TOC entry 2662 (class 2606 OID 203430)
-- Dependencies: 179 2282 270
-- Name: fk1ba06002a82c370; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_element
    ADD CONSTRAINT fk1ba06002a82c370 FOREIGN KEY (id_flexible_element) REFERENCES default_flexible_element(id_flexible_element);


--
-- TOC entry 2595 (class 2606 OID 202564)
-- Dependencies: 2320 198 233
-- Name: fk1bc8331244f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_banner
    ADD CONSTRAINT fk1bc8331244f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2596 (class 2606 OID 202569)
-- Dependencies: 233 236 2398
-- Name: fk1bc83312d196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_banner
    ADD CONSTRAINT fk1bc83312d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2640 (class 2606 OID 202574)
-- Dependencies: 257 257 2442
-- Name: fk2753671fcde08d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk2753671fcde08d FOREIGN KEY (assessmentsiteid) REFERENCES site(siteid) DEFERRABLE;


--
-- TOC entry 2641 (class 2606 OID 202579)
-- Dependencies: 257 201 2326
-- Name: fk275367368ddfa7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk275367368ddfa7 FOREIGN KEY (locationid) REFERENCES location(locationid) DEFERRABLE;


--
-- TOC entry 2642 (class 2606 OID 202584)
-- Dependencies: 257 263 2454
-- Name: fk275367494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk275367494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2643 (class 2606 OID 202589)
-- Dependencies: 257 221 2366
-- Name: fk27536779d901c9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk27536779d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2644 (class 2606 OID 202594)
-- Dependencies: 257 161 2248
-- Name: fk27536780bf17db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY site
    ADD CONSTRAINT fk27536780bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;


--
-- TOC entry 2492 (class 2606 OID 202599)
-- Dependencies: 162 163 2252
-- Name: fk2e3083f227f5cac7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminentity
    ADD CONSTRAINT fk2e3083f227f5cac7 FOREIGN KEY (adminlevelid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;


--
-- TOC entry 2493 (class 2606 OID 202604)
-- Dependencies: 162 162 2250
-- Name: fk2e3083f2ff2bada7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminentity
    ADD CONSTRAINT fk2e3083f2ff2bada7 FOREIGN KEY (adminentityparentid) REFERENCES adminentity(adminentityid) DEFERRABLE;


--
-- TOC entry 2520 (class 2606 OID 202409)
-- Dependencies: 186 219 2362
-- Name: fk2fb477b2f85c2c3c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export_settings
    ADD CONSTRAINT fk2fb477b2f85c2c3c FOREIGN KEY (organization_id) REFERENCES organization(id_organization);


--
-- TOC entry 2659 (class 2606 OID 203370)
-- Dependencies: 2362 267 219
-- Name: fk2fb477b2f85c2c3c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY password_expiration_policy
    ADD CONSTRAINT fk2fb477b2f85c2c3c FOREIGN KEY (organization_id) REFERENCES organization(id_organization);


--
-- TOC entry 2571 (class 2606 OID 202609)
-- Dependencies: 221 201 2326
-- Name: fk33f574a8350d2271; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8350d2271 FOREIGN KEY (location_locationid) REFERENCES location(locationid) DEFERRABLE;


--
-- TOC entry 2572 (class 2606 OID 202614)
-- Dependencies: 221 218 2360
-- Name: fk33f574a84ba27d70; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a84ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;


--
-- TOC entry 2573 (class 2606 OID 202619)
-- Dependencies: 221 221 2366
-- Name: fk33f574a85179b874; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a85179b874 FOREIGN KEY (parent_partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2574 (class 2606 OID 202624)
-- Dependencies: 221 219 2362
-- Name: fk33f574a8cf94c360; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8cf94c360 FOREIGN KEY (organization_id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2575 (class 2606 OID 202629)
-- Dependencies: 221 178 2280
-- Name: fk33f574a8faec4abb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8faec4abb FOREIGN KEY (office_country_id) REFERENCES country(countryid) DEFERRABLE;


--
-- TOC entry 2634 (class 2606 OID 202634)
-- Dependencies: 254 255 2438
-- Name: fk35f790911741f030; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f790911741f030 FOREIGN KEY (reporttemplateid) REFERENCES reporttemplate(reporttemplateid) DEFERRABLE;


--
-- TOC entry 2635 (class 2606 OID 202639)
-- Dependencies: 254 264 2458
-- Name: fk35f7909148b34b53; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f7909148b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2636 (class 2606 OID 202644)
-- Dependencies: 254 264 2458
-- Name: fk35f7909173633c59; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f7909173633c59 FOREIGN KEY (invitinguserid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2625 (class 2606 OID 202649)
-- Dependencies: 248 183 2292
-- Name: fk3d05bba320d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba320d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2626 (class 2606 OID 202654)
-- Dependencies: 248 243 2412
-- Name: fk3d05bba370812310; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba370812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2627 (class 2606 OID 202659)
-- Dependencies: 248 176 2276
-- Name: fk3d05bba3b6ab611d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba3b6ab611d FOREIGN KEY (id_category_type) REFERENCES category_type(id_category_type) DEFERRABLE;


--
-- TOC entry 2568 (class 2606 OID 202664)
-- Dependencies: 219 221 2366
-- Name: fk4644ed33754a9e7e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk4644ed33754a9e7e FOREIGN KEY (id_root_org_unit) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2651 (class 2606 OID 202669)
-- Dependencies: 263 264 2458
-- Name: fk46aeba86a5c52bc6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT fk46aeba86a5c52bc6 FOREIGN KEY (owneruserid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2652 (class 2606 OID 202674)
-- Dependencies: 263 178 2280
-- Name: fk46aeba86b6676e25; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT fk46aeba86b6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;


--
-- TOC entry 2513 (class 2606 OID 202679)
-- Dependencies: 179 183 2292
-- Name: fk48d914c620d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY default_flexible_element
    ADD CONSTRAINT fk48d914c620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2619 (class 2606 OID 202684)
-- Dependencies: 244 243 2412
-- Name: fk4a73751d70812310; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT fk4a73751d70812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2620 (class 2606 OID 202689)
-- Dependencies: 244 243 2412
-- Name: fk4a73751dfe03d96d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT fk4a73751dfe03d96d FOREIGN KEY (id_quality_criterion_child) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2523 (class 2606 OID 202694)
-- Dependencies: 190 263 2454
-- Name: fk4d01ddef494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2524 (class 2606 OID 202699)
-- Dependencies: 190 243 2412
-- Name: fk4d01ddef70812310; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef70812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2525 (class 2606 OID 202704)
-- Dependencies: 190 161 2248
-- Name: fk4d01ddef80bf17db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef80bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;


--
-- TOC entry 2503 (class 2606 OID 202709)
-- Dependencies: 169 257 2442
-- Name: fk4ed7045544c2434b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT fk4ed7045544c2434b FOREIGN KEY (siteid) REFERENCES site(siteid) DEFERRABLE;


--
-- TOC entry 2504 (class 2606 OID 202714)
-- Dependencies: 169 166 2256
-- Name: fk4ed70455afed0b31; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT fk4ed70455afed0b31 FOREIGN KEY (attributeid) REFERENCES attribute(attributeid) DEFERRABLE;


--
-- TOC entry 2541 (class 2606 OID 202719)
-- Dependencies: 202 201 2326
-- Name: fk50408394368ddfa7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT fk50408394368ddfa7 FOREIGN KEY (locationid) REFERENCES location(locationid) DEFERRABLE;


--
-- TOC entry 2542 (class 2606 OID 202724)
-- Dependencies: 202 162 2250
-- Name: fk50408394cd1204fd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT fk50408394cd1204fd FOREIGN KEY (adminentityid) REFERENCES adminentity(adminentityid) DEFERRABLE;


--
-- TOC entry 2589 (class 2606 OID 202729)
-- Dependencies: 232 263 2454
-- Name: fk50c8e2f9494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2590 (class 2606 OID 202734)
-- Dependencies: 232 264 2458
-- Name: fk50c8e2f955bb91b6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f955bb91b6 FOREIGN KEY (id_manager) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2591 (class 2606 OID 202739)
-- Dependencies: 232 215 2354
-- Name: fk50c8e2f9b07b74ff; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9b07b74ff FOREIGN KEY (id_monitored_points_list) REFERENCES monitored_point_list(id_monitored_point_list) DEFERRABLE;


--
-- TOC entry 2592 (class 2606 OID 202744)
-- Dependencies: 232 236 2398
-- Name: fk50c8e2f9d196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2593 (class 2606 OID 202749)
-- Dependencies: 232 225 2374
-- Name: fk50c8e2f9dffa476a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9dffa476a FOREIGN KEY (id_current_phase) REFERENCES phase(id_phase) DEFERRABLE;


--
-- TOC entry 2594 (class 2606 OID 202754)
-- Dependencies: 232 250 2428
-- Name: fk50c8e2f9e2910b71; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9e2910b71 FOREIGN KEY (id_reminder_list) REFERENCES reminder_list(id_reminder_list) DEFERRABLE;


--
-- TOC entry 2514 (class 2606 OID 202759)
-- Dependencies: 181 264 2458
-- Name: fk52157d152c2c465c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_version
    ADD CONSTRAINT fk52157d152c2c465c FOREIGN KEY (id_author) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2515 (class 2606 OID 202764)
-- Dependencies: 181 180 2284
-- Name: fk52157d15d4cd29db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file_version
    ADD CONSTRAINT fk52157d15d4cd29db FOREIGN KEY (id_file) REFERENCES file_meta(id_file) DEFERRABLE;


--
-- TOC entry 2599 (class 2606 OID 202769)
-- Dependencies: 235 232 2390
-- Name: fk52f38bd74485e32a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd74485e32a FOREIGN KEY (id_project_funding) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2600 (class 2606 OID 202774)
-- Dependencies: 235 235 2396
-- Name: fk52f38bd7597e985f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd7597e985f FOREIGN KEY (id_funding) REFERENCES project_funding(id_funding) DEFERRABLE;


--
-- TOC entry 2601 (class 2606 OID 202779)
-- Dependencies: 235 232 2390
-- Name: fk52f38bd7c908f825; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd7c908f825 FOREIGN KEY (id_project_funded) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2646 (class 2606 OID 202784)
-- Dependencies: 260 183 2292
-- Name: fk532b05fd20d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY triplets_list_element
    ADD CONSTRAINT fk532b05fd20d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2560 (class 2606 OID 202789)
-- Dependencies: 213 183 2292
-- Name: fk553ccec420d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY message_element
    ADD CONSTRAINT fk553ccec420d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2549 (class 2606 OID 202794)
-- Dependencies: 206 208 2340
-- Name: fk5a2e206f4f6005ee; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_element
    ADD CONSTRAINT fk5a2e206f4f6005ee FOREIGN KEY (id_group) REFERENCES log_frame_group(id_group) DEFERRABLE;


--
-- TOC entry 2507 (class 2606 OID 202799)
-- Dependencies: 173 174 2272
-- Name: fk5a830ade653f90a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_part
    ADD CONSTRAINT fk5a830ade653f90a FOREIGN KEY (id_budget_parts_list) REFERENCES budget_parts_list_value(id_budget_parts_list) DEFERRABLE;


--
-- TOC entry 2516 (class 2606 OID 202804)
-- Dependencies: 182 183 2292
-- Name: fk6459a12320d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY files_list_element
    ADD CONSTRAINT fk6459a12320d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2543 (class 2606 OID 202809)
-- Dependencies: 203 163 2252
-- Name: fk65214af20feb745; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationtype
    ADD CONSTRAINT fk65214af20feb745 FOREIGN KEY (boundadminlevelid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;


--
-- TOC entry 2544 (class 2606 OID 202814)
-- Dependencies: 203 178 2280
-- Name: fk65214afb6676e25; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY locationtype
    ADD CONSTRAINT fk65214afb6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;


--
-- TOC entry 2578 (class 2606 OID 202819)
-- Dependencies: 225 232 2390
-- Name: fk65b097bb13b3e6c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase
    ADD CONSTRAINT fk65b097bb13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2579 (class 2606 OID 202824)
-- Dependencies: 225 226 2376
-- Name: fk65b097bc9c78c91; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase
    ADD CONSTRAINT fk65b097bc9c78c91 FOREIGN KEY (id_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;


--
-- TOC entry 2533 (class 2606 OID 202829)
-- Dependencies: 196 190 2304
-- Name: fk676020c247c62157; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT fk676020c247c62157 FOREIGN KEY (indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2534 (class 2606 OID 202834)
-- Dependencies: 196 253 2434
-- Name: fk676020c284811db7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT fk676020c284811db7 FOREIGN KEY (reportingperiodid) REFERENCES reportingperiod(reportingperiodid) DEFERRABLE;


--
-- TOC entry 2509 (class 2606 OID 202839)
-- Dependencies: 175 219 2362
-- Name: fk67dfa4bb87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_element
    ADD CONSTRAINT fk67dfa4bb87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2510 (class 2606 OID 202844)
-- Dependencies: 175 176 2276
-- Name: fk67dfa4bbb6ab611d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY category_element
    ADD CONSTRAINT fk67dfa4bbb6ab611d FOREIGN KEY (id_category_type) REFERENCES category_type(id_category_type) DEFERRABLE;


--
-- TOC entry 2508 (class 2606 OID 202849)
-- Dependencies: 174 171 2266
-- Name: fk69676b09c9ce70ad; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_parts_list_value
    ADD CONSTRAINT fk69676b09c9ce70ad FOREIGN KEY (id_budget) REFERENCES budget(id_budget) DEFERRABLE;


--
-- TOC entry 2657 (class 2606 OID 202854)
-- Dependencies: 266 183 2292
-- Name: fk6ac917120d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY value
    ADD CONSTRAINT fk6ac917120d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2658 (class 2606 OID 202859)
-- Dependencies: 266 264 2458
-- Name: fk6ac91712922bbb3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY value
    ADD CONSTRAINT fk6ac91712922bbb3 FOREIGN KEY (id_user_last_modif) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2585 (class 2606 OID 202864)
-- Dependencies: 229 219 2362
-- Name: fk74e7b70887d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group
    ADD CONSTRAINT fk74e7b70887d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2540 (class 2606 OID 202869)
-- Dependencies: 201 203 2330
-- Name: fk752a03d58c0165bb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY location
    ADD CONSTRAINT fk752a03d58c0165bb FOREIGN KEY (locationtypeid) REFERENCES locationtype(locationtypeid) DEFERRABLE;


--
-- TOC entry 2617 (class 2606 OID 202874)
-- Dependencies: 243 246 2420
-- Name: fk76d1d76183d8e9ca; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT fk76d1d76183d8e9ca FOREIGN KEY (id_quality_framework) REFERENCES quality_framework(id_quality_framework) DEFERRABLE;


--
-- TOC entry 2618 (class 2606 OID 202879)
-- Dependencies: 243 219 2362
-- Name: fk76d1d76187d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT fk76d1d76187d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2500 (class 2606 OID 202884)
-- Dependencies: 166 167 2258
-- Name: fk7839ca7cda7c5e3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attribute
    ADD CONSTRAINT fk7839ca7cda7c5e3 FOREIGN KEY (attributegroupid) REFERENCES attributegroup(attributegroupid) DEFERRABLE;


--
-- TOC entry 2583 (class 2606 OID 202889)
-- Dependencies: 228 226 2376
-- Name: fk7a142472181ec2f8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT fk7a142472181ec2f8 FOREIGN KEY (id_phase_model_successor) REFERENCES phase_model(id_phase_model) DEFERRABLE;


--
-- TOC entry 2584 (class 2606 OID 202894)
-- Dependencies: 228 226 2376
-- Name: fk7a142472c9c78c91; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT fk7a142472c9c78c91 FOREIGN KEY (id_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;


--
-- TOC entry 2526 (class 2606 OID 202899)
-- Dependencies: 191 190 2304
-- Name: fk7a87f87547c62157; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT fk7a87f87547c62157 FOREIGN KEY (indicatorid) REFERENCES indicator(indicatorid);


--
-- TOC entry 2527 (class 2606 OID 202904)
-- Dependencies: 191 190 2304
-- Name: fk7a87f8755038b772; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT fk7a87f8755038b772 FOREIGN KEY (indicatorsourceid) REFERENCES indicator(indicatorid);


--
-- TOC entry 2605 (class 2606 OID 202909)
-- Dependencies: 238 232 2390
-- Name: fk8076a4d884058733; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT fk8076a4d884058733 FOREIGN KEY (project_databaseid) REFERENCES project(databaseid);


--
-- TOC entry 2606 (class 2606 OID 202914)
-- Dependencies: 238 264 2458
-- Name: fk8076a4d8efbea106; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT fk8076a4d8efbea106 FOREIGN KEY (favoriteusers_userid) REFERENCES userlogin(userid);


--
-- TOC entry 2622 (class 2606 OID 202919)
-- Dependencies: 246 219 2362
-- Name: fk807dbabe87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_framework
    ADD CONSTRAINT fk807dbabe87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2496 (class 2606 OID 202924)
-- Dependencies: 164 204 2332
-- Name: fk807f02ed9bc5c4da; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment
    ADD CONSTRAINT fk807f02ed9bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;


--
-- TOC entry 2497 (class 2606 OID 202929)
-- Dependencies: 164 232 2390
-- Name: fk807f02edb13b3e6c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment
    ADD CONSTRAINT fk807f02edb13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2631 (class 2606 OID 202934)
-- Dependencies: 252 183 2292
-- Name: fk8104218620d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT fk8104218620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2632 (class 2606 OID 202939)
-- Dependencies: 252 240 2406
-- Name: fk8104218654081a85; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT fk8104218654081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id);


--
-- TOC entry 2539 (class 2606 OID 202944)
-- Dependencies: 200 198 2320
-- Name: fk8435cd2a44f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_group
    ADD CONSTRAINT fk8435cd2a44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2612 (class 2606 OID 202949)
-- Dependencies: 240 219 2362
-- Name: fk85b7359c87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodel
    ADD CONSTRAINT fk85b7359c87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2545 (class 2606 OID 202954)
-- Dependencies: 204 232 2390
-- Name: fk88122cb2b13b3e6c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame
    ADD CONSTRAINT fk88122cb2b13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2546 (class 2606 OID 202959)
-- Dependencies: 204 210 2344
-- Name: fk88122cb2eee3ae75; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame
    ADD CONSTRAINT fk88122cb2eee3ae75 FOREIGN KEY (id_log_frame_model) REFERENCES log_frame_model(id_log_frame) DEFERRABLE;


--
-- TOC entry 2506 (class 2606 OID 202964)
-- Dependencies: 172 183 2292
-- Name: fk881d68fb20d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_distribution_element
    ADD CONSTRAINT fk881d68fb20d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2556 (class 2606 OID 202969)
-- Dependencies: 211 208 2340
-- Name: fk88c951234f6005ee; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT fk88c951234f6005ee FOREIGN KEY (id_group) REFERENCES log_frame_group(id_group) DEFERRABLE;


--
-- TOC entry 2557 (class 2606 OID 202974)
-- Dependencies: 211 204 2332
-- Name: fk88c951239bc5c4da; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT fk88c951239bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;


--
-- TOC entry 2547 (class 2606 OID 202979)
-- Dependencies: 205 207 2338
-- Name: fk89611ffc8012bc39; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT fk89611ffc8012bc39 FOREIGN KEY (id_result) REFERENCES log_frame_expected_result(id_element) DEFERRABLE;


--
-- TOC entry 2548 (class 2606 OID 202984)
-- Dependencies: 205 206 2336
-- Name: fk89611ffce41dae8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT fk89611ffce41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;


--
-- TOC entry 2653 (class 2606 OID 202989)
-- Dependencies: 264 219 2362
-- Name: fk8aa0da3e87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userlogin
    ADD CONSTRAINT fk8aa0da3e87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2645 (class 2606 OID 202994)
-- Dependencies: 258 183 2292
-- Name: fk8d80a2f720d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY textarea_element
    ADD CONSTRAINT fk8d80a2f720d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2561 (class 2606 OID 202999)
-- Dependencies: 214 215 2354
-- Name: fk8df3554a3dc0a3b1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT fk8df3554a3dc0a3b1 FOREIGN KEY (id_list) REFERENCES monitored_point_list(id_monitored_point_list) DEFERRABLE;


--
-- TOC entry 2562 (class 2606 OID 203004)
-- Dependencies: 214 180 2284
-- Name: fk8df3554ad4cd29db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT fk8df3554ad4cd29db FOREIGN KEY (id_file) REFERENCES file_meta(id_file) DEFERRABLE;


--
-- TOC entry 2563 (class 2606 OID 203009)
-- Dependencies: 216 198 2320
-- Name: fk90ee7d6c44f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT fk90ee7d6c44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2564 (class 2606 OID 203014)
-- Dependencies: 216 218 2360
-- Name: fk90ee7d6c4ba27d70; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT fk90ee7d6c4ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;


--
-- TOC entry 2517 (class 2606 OID 203019)
-- Dependencies: 183 229 2384
-- Name: fk91725e88e25e8842; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY flexible_element
    ADD CONSTRAINT fk91725e88e25e8842 FOREIGN KEY (id_privacy_group) REFERENCES privacy_group(id_privacy_group) DEFERRABLE;


--
-- TOC entry 2639 (class 2606 OID 203024)
-- Dependencies: 256 242 2410
-- Name: fk9752ca7398d45965; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY richtextelement
    ADD CONSTRAINT fk9752ca7398d45965 FOREIGN KEY (version_id) REFERENCES projectreportversion(id) DEFERRABLE;


--
-- TOC entry 2550 (class 2606 OID 203029)
-- Dependencies: 207 212 2348
-- Name: fk99d3ddf7d88379d4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT fk99d3ddf7d88379d4 FOREIGN KEY (id_specific_objective) REFERENCES log_frame_specific_objective(id_element) DEFERRABLE;


--
-- TOC entry 2551 (class 2606 OID 203034)
-- Dependencies: 207 206 2336
-- Name: fk99d3ddf7e41dae8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT fk99d3ddf7e41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;


--
-- TOC entry 2537 (class 2606 OID 203039)
-- Dependencies: 199 183 2292
-- Name: fk9bb4b21220d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT fk9bb4b21220d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2538 (class 2606 OID 203044)
-- Dependencies: 199 200 2324
-- Name: fk9bb4b212da924c21; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT fk9bb4b212da924c21 FOREIGN KEY (id_layout_group) REFERENCES layout_group(id_layout_group) DEFERRABLE;


--
-- TOC entry 2518 (class 2606 OID 202414)
-- Dependencies: 219 2362 184
-- Name: fk9e763fd0f85c2c3c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export
    ADD CONSTRAINT fk9e763fd0f85c2c3c FOREIGN KEY (organization_id) REFERENCES organization(id_organization);


--
-- TOC entry 2494 (class 2606 OID 203049)
-- Dependencies: 163 178 2280
-- Name: fk9ec33d95b6676e25; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT fk9ec33d95b6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;


--
-- TOC entry 2495 (class 2606 OID 203054)
-- Dependencies: 163 163 2252
-- Name: fk9ec33d95e01b109c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT fk9ec33d95e01b109c FOREIGN KEY (parentid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;


--
-- TOC entry 2666 (class 2606 OID 203472)
-- Dependencies: 2398 236 273
-- Name: fk_cgrmoq07kxyggtnldsvlwjqcs; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_model
    ADD CONSTRAINT fk_cgrmoq07kxyggtnldsvlwjqcs FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model);


--
-- TOC entry 2667 (class 2606 OID 203477)
-- Dependencies: 2360 273 218
-- Name: fk_ckwexvghil94ha4ct8b1wepxq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_model
    ADD CONSTRAINT fk_ckwexvghil94ha4ct8b1wepxq FOREIGN KEY (org_unit_model_id) REFERENCES org_unit_model(org_unit_model_id);


--
-- TOC entry 2674 (class 2606 OID 203535)
-- Dependencies: 274 2480 277
-- Name: fk_dfeq1vnw6d3ooeqf1stt4x276; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_variable_budget_sub_field
    ADD CONSTRAINT fk_dfeq1vnw6d3ooeqf1stt4x276 FOREIGN KEY (var_id) REFERENCES importation_scheme_variable(var_id);


--
-- TOC entry 2673 (class 2606 OID 203525)
-- Dependencies: 2482 275 276
-- Name: fk_eu352p1mmft8pwwyylmwe63q8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_variable_budget_element
    ADD CONSTRAINT fk_eu352p1mmft8pwwyylmwe63q8 FOREIGN KEY (var_fle_id) REFERENCES importation_scheme_variable_flexible_element(var_fle_id);


--
-- TOC entry 2669 (class 2606 OID 203495)
-- Dependencies: 2476 274 272
-- Name: fk_khgoedwqkg3au5a2o3fe4g398; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_variable
    ADD CONSTRAINT fk_khgoedwqkg3au5a2o3fe4g398 FOREIGN KEY (sch_id) REFERENCES importation_scheme(sch_id);


--
-- TOC entry 2670 (class 2606 OID 203505)
-- Dependencies: 275 2478 273
-- Name: fk_kr8tjw9mvseef9x0il6dojoh9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_variable_flexible_element
    ADD CONSTRAINT fk_kr8tjw9mvseef9x0il6dojoh9 FOREIGN KEY (sch_mod_id) REFERENCES importation_scheme_model(sch_mod_id);


--
-- TOC entry 2675 (class 2606 OID 203540)
-- Dependencies: 277 276 2484
-- Name: fk_ms0uq981iysge90gt1o3pf40q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_variable_budget_sub_field
    ADD CONSTRAINT fk_ms0uq981iysge90gt1o3pf40q FOREIGN KEY (var_fle_id) REFERENCES importation_scheme_variable_budget_element(var_fle_id);


--
-- TOC entry 2671 (class 2606 OID 203510)
-- Dependencies: 275 274 2480
-- Name: fk_nbgxlc3whl76ws07c99f70r6n; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_variable_flexible_element
    ADD CONSTRAINT fk_nbgxlc3whl76ws07c99f70r6n FOREIGN KEY (var_id) REFERENCES importation_scheme_variable(var_id);


--
-- TOC entry 2676 (class 2606 OID 203545)
-- Dependencies: 2474 277 271
-- Name: fk_og2mv36vlu4uu885yrpnhbl2q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_variable_budget_sub_field
    ADD CONSTRAINT fk_og2mv36vlu4uu885yrpnhbl2q FOREIGN KEY (id_budget_sub_field) REFERENCES budget_sub_field(id_budget_sub_field);


--
-- TOC entry 2668 (class 2606 OID 203482)
-- Dependencies: 273 2476 272
-- Name: fk_prpi3dmykj4nbdeyk3dhb51jn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_model
    ADD CONSTRAINT fk_prpi3dmykj4nbdeyk3dhb51jn FOREIGN KEY (sch_id) REFERENCES importation_scheme(sch_id);


--
-- TOC entry 2672 (class 2606 OID 203515)
-- Dependencies: 2292 183 275
-- Name: fk_q4la1it8wgg6nkosxi6rpd4cp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY importation_scheme_variable_flexible_element
    ADD CONSTRAINT fk_q4la1it8wgg6nkosxi6rpd4cp FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element);


--
-- TOC entry 2490 (class 2606 OID 203059)
-- Dependencies: 161 263 2454
-- Name: fka126572f494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT fka126572f494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2491 (class 2606 OID 203064)
-- Dependencies: 161 203 2330
-- Name: fka126572f8c0165bb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT fka126572f8c0165bb FOREIGN KEY (locationtypeid) REFERENCES locationtype(locationtypeid) DEFERRABLE;


--
-- TOC entry 2586 (class 2606 OID 203069)
-- Dependencies: 230 231 2388
-- Name: fka1812f6692e83e47; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT fka1812f6692e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;


--
-- TOC entry 2587 (class 2606 OID 203074)
-- Dependencies: 230 229 2384
-- Name: fka1812f66e25e8842; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT fka1812f66e25e8842 FOREIGN KEY (id_privacy_group) REFERENCES privacy_group(id_privacy_group) DEFERRABLE;


--
-- TOC entry 2576 (class 2606 OID 203079)
-- Dependencies: 222 263 2454
-- Name: fka9a62c88494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT fka9a62c88494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2577 (class 2606 OID 203084)
-- Dependencies: 221 222 2366
-- Name: fka9a62c8879d901c9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT fka9a62c8879d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2621 (class 2606 OID 203089)
-- Dependencies: 245 246 2420
-- Name: fkb0b3e55883d8e9ca; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY quality_criterion_type
    ADD CONSTRAINT fkb0b3e55883d8e9ca FOREIGN KEY (id_quality_framework) REFERENCES quality_framework(id_quality_framework) DEFERRABLE;


--
-- TOC entry 2613 (class 2606 OID 203094)
-- Dependencies: 241 240 2406
-- Name: fkb29299a98fa2795f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT fkb29299a98fa2795f FOREIGN KEY (projectmodelid) REFERENCES projectreportmodel(id) DEFERRABLE;


--
-- TOC entry 2614 (class 2606 OID 203099)
-- Dependencies: 241 241 2408
-- Name: fkb29299a9ae53865a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT fkb29299a9ae53865a FOREIGN KEY (parentsectionmodelid) REFERENCES projectreportmodelsection(id) DEFERRABLE;


--
-- TOC entry 2552 (class 2606 OID 203104)
-- Dependencies: 208 204 2332
-- Name: fkb4d3b8b29bc5c4da; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_group
    ADD CONSTRAINT fkb4d3b8b29bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;


--
-- TOC entry 2555 (class 2606 OID 203109)
-- Dependencies: 210 236 2398
-- Name: fkb526bd5cd196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_model
    ADD CONSTRAINT fkb526bd5cd196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2629 (class 2606 OID 203114)
-- Dependencies: 2292 251 183
-- Name: fkbc80a6f120d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_element
    ADD CONSTRAINT fkbc80a6f120d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2630 (class 2606 OID 203119)
-- Dependencies: 2406 240 251
-- Name: fkbc80a6f154081a85; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY report_element
    ADD CONSTRAINT fkbc80a6f154081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id);


--
-- TOC entry 2615 (class 2606 OID 203124)
-- Dependencies: 239 2404 242
-- Name: fkc093868b39413f9b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT fkc093868b39413f9b FOREIGN KEY (report_id) REFERENCES projectreport(id) DEFERRABLE;


--
-- TOC entry 2616 (class 2606 OID 203129)
-- Dependencies: 242 264 2458
-- Name: fkc093868b54402265; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT fkc093868b54402265 FOREIGN KEY (editor_userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2603 (class 2606 OID 203134)
-- Dependencies: 2362 237 219
-- Name: fkc10e64e87d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT fkc10e64e87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2604 (class 2606 OID 203139)
-- Dependencies: 236 237 2398
-- Name: fkc10e64ed196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT fkc10e64ed196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2580 (class 2606 OID 203144)
-- Dependencies: 198 2320 226
-- Name: fkc11f1f6544f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f6544f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2581 (class 2606 OID 203149)
-- Dependencies: 236 226 2398
-- Name: fkc11f1f65d196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f65d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2582 (class 2606 OID 203154)
-- Dependencies: 227 2378 226
-- Name: fkc11f1f65e0174a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f65e0174a FOREIGN KEY (definition_id) REFERENCES phase_model_definition(id_phase_model_definition) DEFERRABLE;


--
-- TOC entry 2665 (class 2606 OID 203443)
-- Dependencies: 271 270 2472
-- Name: fkc12629c1a251b09; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY budget_sub_field
    ADD CONSTRAINT fkc12629c1a251b09 FOREIGN KEY (id_budget_element) REFERENCES budget_element(id_flexible_element);


--
-- TOC entry 2649 (class 2606 OID 203159)
-- Dependencies: 231 2388 262
-- Name: fkc37e36d192e83e47; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT fkc37e36d192e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;


--
-- TOC entry 2650 (class 2606 OID 203164)
-- Dependencies: 261 262 2450
-- Name: fkc37e36d1b3ab1d1c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT fkc37e36d1b3ab1d1c FOREIGN KEY (id_user_unit) REFERENCES user_unit(id_user_unit) DEFERRABLE;


--
-- TOC entry 2498 (class 2606 OID 203169)
-- Dependencies: 164 165 2254
-- Name: fkc514f4bc7b49ebc6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment_history_token
    ADD CONSTRAINT fkc514f4bc7b49ebc6 FOREIGN KEY (amendment_id_amendment) REFERENCES amendment(id_amendment) DEFERRABLE;


--
-- TOC entry 2499 (class 2606 OID 203174)
-- Dependencies: 189 165 2302
-- Name: fkc514f4bcbc854628; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY amendment_history_token
    ADD CONSTRAINT fkc514f4bcbc854628 FOREIGN KEY (values_id_history_token) REFERENCES history_token(id_history_token) DEFERRABLE;


--
-- TOC entry 2637 (class 2606 OID 203179)
-- Dependencies: 2454 263 255
-- Name: fkc69ddee494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT fkc69ddee494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2638 (class 2606 OID 203184)
-- Dependencies: 255 264 2458
-- Name: fkc69ddeea5c52bc6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT fkc69ddeea5c52bc6 FOREIGN KEY (owneruserid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2602 (class 2606 OID 203189)
-- Dependencies: 226 236 2376
-- Name: fkc7b83283792a5c7c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_model
    ADD CONSTRAINT fkc7b83283792a5c7c FOREIGN KEY (id_root_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;


--
-- TOC entry 2558 (class 2606 OID 203194)
-- Dependencies: 2332 212 204
-- Name: fkc979ef199bc5c4da; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT fkc979ef199bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;


--
-- TOC entry 2559 (class 2606 OID 203199)
-- Dependencies: 206 212 2336
-- Name: fkc979ef19e41dae8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT fkc979ef19e41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;


--
-- TOC entry 2521 (class 2606 OID 203204)
-- Dependencies: 231 2388 187
-- Name: fkcb8783eb92e83e47; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_permission
    ADD CONSTRAINT fkcb8783eb92e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;


--
-- TOC entry 2597 (class 2606 OID 203209)
-- Dependencies: 198 234 2320
-- Name: fkce2cbb1c44f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_details
    ADD CONSTRAINT fkce2cbb1c44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2598 (class 2606 OID 203214)
-- Dependencies: 234 236 2398
-- Name: fkce2cbb1cd196f951; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY project_details
    ADD CONSTRAINT fkce2cbb1cd196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;


--
-- TOC entry 2654 (class 2606 OID 203219)
-- Dependencies: 2458 265 264
-- Name: fkd265581a48b34b53; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a48b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2655 (class 2606 OID 203224)
-- Dependencies: 265 263 2454
-- Name: fkd265581a494bd9e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;


--
-- TOC entry 2656 (class 2606 OID 203229)
-- Dependencies: 2366 265 221
-- Name: fkd265581a79d901c9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a79d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2535 (class 2606 OID 203234)
-- Dependencies: 2412 197 243
-- Name: fkd2af174536d186ad; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT fkd2af174536d186ad FOREIGN KEY (qualitycriterion_id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;


--
-- TOC entry 2536 (class 2606 OID 203239)
-- Dependencies: 197 241 2408
-- Name: fkd2af1745d8178e71; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT fkd2af1745d8178e71 FOREIGN KEY (sectionid) REFERENCES projectreportmodelsection(id) DEFERRABLE;


--
-- TOC entry 2522 (class 2606 OID 203244)
-- Dependencies: 2458 189 264
-- Name: fkd692428edd0ca99c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY history_token
    ADD CONSTRAINT fkd692428edd0ca99c FOREIGN KEY (id_user) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2519 (class 2606 OID 202419)
-- Dependencies: 185 184 2294
-- Name: fkdca84b0af33647b9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY global_export_content
    ADD CONSTRAINT fkdca84b0af33647b9 FOREIGN KEY (global_export_id) REFERENCES global_export(id);


--
-- TOC entry 2633 (class 2606 OID 203249)
-- Dependencies: 257 2442 253
-- Name: fkdcfe056f44c2434b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reportingperiod
    ADD CONSTRAINT fkdcfe056f44c2434b FOREIGN KEY (siteid) REFERENCES site(siteid) DEFERRABLE;


--
-- TOC entry 2501 (class 2606 OID 203254)
-- Dependencies: 168 161 2248
-- Name: fkdd8c951780bf17db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT fkdd8c951780bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;


--
-- TOC entry 2502 (class 2606 OID 203259)
-- Dependencies: 2258 168 167
-- Name: fkdd8c9517da7c5e3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT fkdd8c9517da7c5e3 FOREIGN KEY (attributegroupid) REFERENCES attributegroup(attributegroupid) DEFERRABLE;


--
-- TOC entry 2505 (class 2606 OID 203264)
-- Dependencies: 264 170 2458
-- Name: fkddeeae9848b34b53; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authentication
    ADD CONSTRAINT fkddeeae9848b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2607 (class 2606 OID 203269)
-- Dependencies: 183 239 2292
-- Name: fke0b8458d3cdc69db; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d3cdc69db FOREIGN KEY (flexibleelement_id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2608 (class 2606 OID 203274)
-- Dependencies: 240 239 2406
-- Name: fke0b8458d54081a85; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d54081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id) DEFERRABLE;


--
-- TOC entry 2609 (class 2606 OID 203279)
-- Dependencies: 2410 239 242
-- Name: fke0b8458d5a50539e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d5a50539e FOREIGN KEY (currentversion_id) REFERENCES projectreportversion(id) DEFERRABLE;


--
-- TOC entry 2610 (class 2606 OID 203284)
-- Dependencies: 2390 232 239
-- Name: fke0b8458d84058733; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d84058733 FOREIGN KEY (project_databaseid) REFERENCES project(databaseid) DEFERRABLE;


--
-- TOC entry 2611 (class 2606 OID 203289)
-- Dependencies: 2366 239 221
-- Name: fke0b8458db2590b2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458db2590b2 FOREIGN KEY (orgunit_partnerid) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2628 (class 2606 OID 203294)
-- Dependencies: 249 2428 250
-- Name: fke116c072e22ec8c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reminder
    ADD CONSTRAINT fke116c072e22ec8c FOREIGN KEY (id_list) REFERENCES reminder_list(id_reminder_list) DEFERRABLE;


--
-- TOC entry 2512 (class 2606 OID 203299)
-- Dependencies: 183 2292 177
-- Name: fke1e36e8020d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY checkbox_element
    ADD CONSTRAINT fke1e36e8020d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2531 (class 2606 OID 203304)
-- Dependencies: 2292 183 194
-- Name: fkeb796c7620d5ae49; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicators_list_element
    ADD CONSTRAINT fkeb796c7620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;


--
-- TOC entry 2588 (class 2606 OID 203309)
-- Dependencies: 231 219 2362
-- Name: fked8e89a987d1466c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT fked8e89a987d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;


--
-- TOC entry 2569 (class 2606 OID 203314)
-- Dependencies: 220 264 2458
-- Name: fkf10e425774ec9247; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT fkf10e425774ec9247 FOREIGN KEY (user_userid) REFERENCES userlogin(userid) DEFERRABLE;


--
-- TOC entry 2570 (class 2606 OID 203319)
-- Dependencies: 220 2366 221
-- Name: fkf10e4257d3cc239c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT fkf10e4257d3cc239c FOREIGN KEY (unit_id) REFERENCES partner(partnerid) DEFERRABLE;


--
-- TOC entry 2529 (class 2606 OID 203324)
-- Dependencies: 190 193 2304
-- Name: fkf23eb7b447c62157; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatordatasource
    ADD CONSTRAINT fkf23eb7b447c62157 FOREIGN KEY (indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2530 (class 2606 OID 203329)
-- Dependencies: 190 193 2304
-- Name: fkf23eb7b45038b772; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicatordatasource
    ADD CONSTRAINT fkf23eb7b45038b772 FOREIGN KEY (indicatorsourceid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2532 (class 2606 OID 203334)
-- Dependencies: 190 195 2304
-- Name: fkf8bf56b6530fdd8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicators_list_value
    ADD CONSTRAINT fkf8bf56b6530fdd8 FOREIGN KEY (id_indicator) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2565 (class 2606 OID 203339)
-- Dependencies: 217 198 2320
-- Name: fkfdcfbc0244f6265a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT fkfdcfbc0244f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;


--
-- TOC entry 2566 (class 2606 OID 203344)
-- Dependencies: 217 2360 218
-- Name: fkfdcfbc024ba27d70; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT fkfdcfbc024ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;


--
-- TOC entry 2528 (class 2606 OID 203349)
-- Dependencies: 192 2304 190
-- Name: fkfe14c44f52429f27; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY indicator_labels
    ADD CONSTRAINT fkfe14c44f52429f27 FOREIGN KEY (indicator_indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;


--
-- TOC entry 2661 (class 2606 OID 203420)
-- Dependencies: 2352 269 214
-- Name: monitored_point_history_id_monitored_point_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY monitored_point_history
    ADD CONSTRAINT monitored_point_history_id_monitored_point_fkey FOREIGN KEY (id_monitored_point) REFERENCES monitored_point(id_monitored_point);


--
-- TOC entry 2660 (class 2606 OID 203407)
-- Dependencies: 268 249 2426
-- Name: reminder_history_id_reminder_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reminder_history
    ADD CONSTRAINT reminder_history_id_reminder_fkey FOREIGN KEY (id_reminder) REFERENCES reminder(id_reminder);

