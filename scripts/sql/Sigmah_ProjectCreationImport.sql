--
--
-- Functions to easily create or import projects
--
-- Note: designed for Sigmah database v2.2
--


--
-- Create a new project, and returns new project id_project
--
CREATE OR REPLACE FUNCTION create_project(p_organization_id integer, p_project_model_id integer, p_project_code character varying(50), p_project_title character varying(500), p_planned_budget numeric(2), p_orgunit_id integer, p_author_id integer, p_history_comment character varying(255) default null) RETURNS INTEGER AS $$
DECLARE
	v_databaseid integer;
	v_calendarid integer;
	phase_models_cursor CURSOR FOR
		SELECT id_phase_model 
		FROM phase_model 
		WHERE id_project_model = p_project_model_id
		ORDER BY display_order;
	phase_model_record RECORD;
	v_current_phase_id integer;
	v_monitored_point_list_id integer;
	v_reminder_list_id integer;
	default_fields_cursor CURSOR FOR
		SELECT dfe.id_flexible_element, dfe.type
		FROM default_flexible_element dfe
			inner join layout_constraint lc on (lc.id_flexible_element = dfe.id_flexible_element)
			inner join layout_group lg on (lg.id_layout_group = lc.id_layout_group)
			left outer join phase_model phm on (phm.id_layout = lg.id_layout  )
			left outer join project_details prd on (prd.id_layout = lg.id_layout  )
		WHERE dfe.type IN ('COUNTRY', 'OWNER', 'MANAGER', 'ORG_UNIT', 'CODE', 'TITLE', 'START_DATE')
			and prd.id_project_model=p_project_model_id;
	default_field_record RECORD;
	v_value character varying(500);
	v_logframe_id integer;
	log_frame_model_record RECORD;
	v_planned_budget_subfield_id integer;
BEGIN
	-- Create user database
	-- Example : INSERT INTO userdatabase (databaseid, datedeleted, fullname, lastschemaupdate, name, startdate, countryid, owneruserid) VALUES (12783370, NULL, 'testmanualcreation', '2016-07-22 16:34:05.359', 'testmanualcreation', '2016-07-22 16:34:05.359', 403, 122);
	SELECT nextval('hibernate_sequence') INTO v_databaseid;
	INSERT INTO userdatabase(
            databaseid, datedeleted, fullname, lastschemaupdate, name, startdate, 
            countryid, owneruserid)
		SELECT v_databaseid, NULL, p_project_title, localtimestamp, p_project_code, localtimestamp,
			ptn.office_country_id, p_author_id
		FROM partner ptn
		WHERE ptn.partnerid = p_orgunit_id;


	-- Create personalcalendar
	-- Example : INSERT INTO personalcalendar (id, name) VALUES (12783369, 'EvÃ©nements');
	SELECT nextval('hibernate_sequence') INTO v_calendarid;
	INSERT INTO personalcalendar (id, name) VALUES (v_calendarid, 'Events');


	-- Create reminders (monitored points & reminder list)
	-- Example 1: INSERT INTO monitored_point_list (id_monitored_point_list) VALUES (12783371);
	-- Example 2: INSERT INTO reminder_list (id_reminder_list) VALUES (12783372);
	SELECT nextval('hibernate_sequence') INTO v_monitored_point_list_id;
	SELECT nextval('hibernate_sequence') INTO v_reminder_list_id;
	INSERT INTO monitored_point_list (id_monitored_point_list) VALUES (v_monitored_point_list_id);
	INSERT INTO reminder_list (id_reminder_list) VALUES (v_reminder_list_id);

	-- Create project
	-- Example : INSERT INTO project (activity_advancement, amendment_revision, amendment_status, amendment_version, calendarid, close_date, end_date, planned_budget, received_budget, spend_budget, databaseid, id_current_phase, id_manager, id_monitored_points_list, id_project_model, id_reminder_list, mainsite) 
	--         VALUES (NULL, 1, 'DRAFT', 1, 12783369, NULL, NULL, NULL, NULL, NULL, 12783370, 12783373, 122, 12783371, 1613, 12783372, NULL);
	INSERT INTO project(
            activity_advancement, amendment_revision, amendment_status, amendment_version, 
            calendarid, close_date, end_date, planned_budget, received_budget, 
            spend_budget, databaseid, id_current_phase, id_manager, id_monitored_points_list, 
            id_project_model, id_reminder_list, mainsite)
		VALUES( NULL, 1, 'DRAFT', 1, 
			v_calendarid, NULL, NULL, NULL, NULL,
			NULL, v_databaseid, NULL, p_author_id, v_monitored_point_list_id,
			p_project_model_id, v_reminder_list_id, NULL);
				
	-- Create phases
	v_current_phase_id := 0;
	FOR phase_model_record IN phase_models_cursor 
	LOOP		
		IF (v_current_phase_id = 0)
		THEN	
			SELECT nextval('hibernate_sequence') INTO v_current_phase_id;
			INSERT INTO phase (id_phase, end_date, start_date, id_phase_model, id_project)
				VALUES (v_current_phase_id, NULL, localtimestamp, phase_model_record.id_phase_model, v_databaseid);
		ELSE			
			INSERT INTO phase (id_phase, end_date, start_date, id_phase_model, id_project)
				SELECT nextval('hibernate_sequence'), NULL, NULL, phase_model_record.id_phase_model, v_databaseid;
		END IF;
	END LOOP;
	UPDATE project
		SET id_current_phase = v_current_phase_id
		WHERE databaseid = v_databaseid;

	
	-- Update history
	-- Examples:
	-- INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) VALUES (12783383, '2016-07-22 16:34:05.629', 1602, 12783370, 'ADD', '403', 122, NULL, NULL);	
	-- INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) VALUES (12783384, '2016-07-22 16:34:05.629', 1603, 12783370, 'ADD', 'Julien Carlier', 122, NULL, NULL);
	-- INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) VALUES (12783385, '2016-07-22 16:34:05.629', 1604, 12783370, 'ADD', '122', 122, NULL, NULL);
	-- INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) VALUES (12783386, '2016-07-22 16:34:05.629', 1605, 12783370, 'ADD', '761', 122, NULL, NULL);
	-- INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) VALUES (12783387, '2016-07-22 16:34:05.629', 1595, 12783370, 'ADD', 'testmanualcreation', 122, NULL, NULL);
	-- INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) VALUES (12783388, '2016-07-22 16:34:05.629', 1596, 12783370, 'ADD', 'testmanualcreation', 122, NULL, NULL);
	-- INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) VALUES (12783390, '2016-07-22 16:34:05.629', 1600, 12783370, 'ADD', '1469198045359', 122, NULL, NULL);
	FOR default_field_record IN default_fields_cursor 
	LOOP		
		CASE default_field_record.type
			WHEN 'COUNTRY'
				THEN
					SELECT CAST(office_country_id AS character varying(500))
						INTO v_value
						FROM partner
						WHERE partnerid = p_orgunit_id;
			WHEN 'OWNER'
				THEN v_value:= CAST(p_author_id AS character varying(500));
			WHEN 'MANAGER'
				THEN v_value:= CAST(p_author_id AS character varying(500));
			WHEN 'ORG_UNIT'
				THEN v_value:= CAST(p_orgunit_id AS character varying(500));
			WHEN 'CODE'
				THEN v_value:= CAST(p_project_code AS character varying(500));
			WHEN 'TITLE'
				THEN v_value:= p_project_title;
			WHEN 'START_DATE'
				THEN v_value:= CAST(ROUND(EXTRACT(EPOCH FROM LOCALTIMESTAMP)*1000) AS character varying(500));
		END CASE;
		INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) 
			SELECT nextval('hibernate_sequence'), localtimestamp, default_field_record.id_flexible_element, v_databaseid, 'ADD', v_value, p_author_id, p_history_comment, NULL;	
	END LOOP;

	-- Create logframe
	-- Example : INSERT INTO log_frame (id_log_frame, main_objective, id_log_frame_model, id_project) VALUES (12783377, NULL, 1614, 12783370);
	SELECT nextval('hibernate_sequence') INTO v_logframe_id;
	SELECT id_log_frame, a_enable_groups, er_enable_groups, p_enable_groups, so_enable_groups
		INTO log_frame_model_record
		FROM log_frame_model
		WHERE id_project_model = p_project_model_id;
	INSERT INTO log_frame (id_log_frame, main_objective, id_log_frame_model, id_project)
		VALUES(v_logframe_id,NULL, log_frame_model_record.id_log_frame, v_databaseid);
	
	-- Create logframe groups
	-- Examples:
	-- INSERT INTO log_frame_group (id_group, label, type, id_log_frame) VALUES (12783378, '-', 'SPECIFIC_OBJECTIVE', 12783377);
	-- INSERT INTO log_frame_group (id_group, label, type, id_log_frame) VALUES (12783379, '-', 'EXPECTED_RESULT', 12783377);
	-- INSERT INTO log_frame_group (id_group, label, type, id_log_frame) VALUES (12783380, '-', 'ACTIVITY', 12783377);
	-- INSERT INTO log_frame_group (id_group, label, type, id_log_frame) VALUES (12783381, '-', 'PREREQUISITE', 12783377);
	IF(log_frame_model_record.so_enable_groups)
	THEN
		INSERT INTO log_frame_group (id_group, label, type, id_log_frame) 
			SELECT nextval('hibernate_sequence'), '-', 'SPECIFIC_OBJECTIVE', v_logframe_id;
	END IF;
	IF(log_frame_model_record.er_enable_groups)
	THEN
		INSERT INTO log_frame_group (id_group, label, type, id_log_frame) 
			SELECT nextval('hibernate_sequence'), '-', 'EXPECTED_RESULT', v_logframe_id;
	END IF;
	IF(log_frame_model_record.a_enable_groups)
	THEN
		INSERT INTO log_frame_group (id_group, label, type, id_log_frame) 
			SELECT nextval('hibernate_sequence'), '-', 'ACTIVITY', v_logframe_id;
	END IF;
	IF(log_frame_model_record.p_enable_groups)
	THEN
		INSERT INTO log_frame_group (id_group, label, type, id_log_frame) 
			SELECT nextval('hibernate_sequence'), '-', 'PREREQUISITE', v_logframe_id;
	END IF;

	-- Create partnerindatabase
	-- Example : INSERT INTO partnerindatabase (partnerid, databaseid) VALUES (761, 12783370);
	INSERT INTO partnerindatabase (partnerid, databaseid) VALUES (p_orgunit_id, v_databaseid);
	
	-- Create userpermission
	INSERT INTO userpermission(
		    userpermissionid, allowdesign, allowedit, alloweditall, allowmanageallusers, 
		    allowmanageusers, allowview, allowviewall, lastschemaupdate, 
		    databaseid, partnerid, userid)
	SELECT nextval('hibernate_sequence'), true, true, true, true,
		true, true, true, localtimestamp,
		v_databaseid, org.id_root_org_unit, usl.userid
	FROM organization org
		inner join userlogin usl on usl.id_organization = org.id_organization
	WHERE org.id_organization = p_organization_id;

	-- Create budget value	
	SELECT id_budget_sub_field 
		INTO v_planned_budget_subfield_id
		FROM budget_sub_field bsf
			inner join default_flexible_element dfe on (dfe.id_flexible_element = bsf.id_budget_element)
			inner join layout_constraint lc on (lc.id_flexible_element = dfe.id_flexible_element)
			inner join layout_group lg on (lg.id_layout_group = lc.id_layout_group)
			left outer join phase_model phm on (phm.id_layout = lg.id_layout  )
			left outer join project_details prd on (prd.id_layout = lg.id_layout  )
		WHERE dfe.type IN ('BUDGET')
			and prd.id_project_model=p_project_model_id;

	-- Set planned budget if not null
	IF (p_planned_budget IS NOT NULL)
	THEN
		perform update_field(v_planned_budget_subfield_id, CAST(p_planned_budget as text), v_databaseid, p_author_id, p_history_comment);
	END IF;
RETURN v_databaseid;
END;
$$ LANGUAGE plpgsql;





--
-- Return as a string the field type from any field (aka flexible element) id.
-- Also returns the budget sub-field type if a budget sub-field it is given.
--
-- Note: id_flexible_element and id_budget_sub_field have never the same value
--
CREATE OR REPLACE FUNCTION get_field_type(p_field_id integer) RETURNS character varying(20) AS $$
DECLARE
	v_type character varying(20);
BEGIN
	SELECT CASE
		WHEN p_field_id IN (SELECT id_flexible_element FROM default_flexible_element)
			THEN (SELECT type FROM default_flexible_element WHERE id_flexible_element = p_field_id )
		WHEN p_field_id IN (SELECT id_budget_sub_field FROM budget_sub_field)
			THEN (SELECT type FROM budget_sub_field WHERE id_budget_sub_field = p_field_id )
		WHEN p_field_id IN (SELECT id_question FROM question_choice_element) 
			THEN 'QUESTION'
		WHEN p_field_id IN (SELECT id_flexible_element FROM files_list_element)
			THEN 'FILES'
		WHEN p_field_id IN (SELECT id_flexible_element FROM checkbox_element)
			THEN 'CHECKBOX'
		WHEN p_field_id IN (SELECT id_flexible_element FROM computation_element)
			THEN 'COMPUTATION'
		WHEN p_field_id IN (SELECT id_flexible_element FROM textarea_element)
			THEN CASE (SELECT type FROM textarea_element WHERE id_flexible_element=p_field_id)
				WHEN 'D'
					THEN 'DATE'
				WHEN 'N'
					THEN 'NUMBER'
				WHEN 'P'
					THEN 'PARAGRAPH'
				WHEN 'T'
					THEN 'TEXT'
				END
		ELSE 'other' END
	INTO v_type;


RETURN v_type;
END;
$$ LANGUAGE plpgsql;



--
-- Update the value of a field.
--
--    Impossible for field type 'OWNER', 'COUNTRY' and 'BUDGET'.
--    This function doesn't take into account field limits
--
-- Parameter format for p_field_value according to field type:
--   - START_DATE, END_DATE : date format like '25/12/2015'
--   - CODE : text, but no longer than 50 characters
--   - MANAGER : user id like '35'
--   - ORG_UNIT : partner id like '602'
--   - SPENT, RECEIVED, PLANNED: budget sub-field value like '25080' or '78450.23'
--   - DATE : date format like '25/12/2015'
--   - CHECKBOX : 'true' or 'false'
--   - QUESTION : question_choice_element id like '325'
--          for multiple choices, multiple ids must be separatd by ~ like '325~328'
--   - NUMBER : number like '25080' or '78450.23'
--   - TEXT : text like 'plusieurs mots'
--   - PARAGRAPH : one more more lines of text, each line of text must be separated only by a NEWLINE (ASCII code 10) character, like 'first line' || chr(10) || ' and second important line'
CREATE OR REPLACE FUNCTION update_field(p_field_id integer, p_field_value text, p_project_id integer, p_author_id integer, p_history_comment character varying(255), p_layout_group_iteration_id integer DEFAULT NULL) RETURNS boolean AS $$
DECLARE
	v_type character varying(20);
	v_history_last_change_record RECORD;
	v_history_change_type character varying(255);
	v_action_last_modif character(1);
	v_budget_field_id integer;
	v_value text;
	v_history_date timestamp without time zone;
BEGIN
	v_type := get_field_type(p_field_id);
	IF(v_type = 'PLANNED' or v_type = 'SPENT' or v_type = 'RECEIVED')
	THEN
		SELECT id_budget_element INTO v_budget_field_id FROM budget_sub_field WHERE id_budget_sub_field=p_field_id; 
		SELECT change_type, history_date
			INTO v_history_last_change_record 
			FROM history_token
			WHERE id_element = v_budget_field_id and id_project = p_project_id
				and (p_layout_group_iteration_id is NULL or id_layout_group_iteration = p_layout_group_iteration_id);
	ELSE
		SELECT change_type, history_date
			INTO v_history_last_change_record 
			FROM history_token
			WHERE id_element = p_field_id and id_project = p_project_id
				and (p_layout_group_iteration_id is NULL or id_layout_group_iteration = p_layout_group_iteration_id);
	END IF;
	IF(v_history_last_change_record IS NULL)
	THEN
		v_history_change_type := 'ADD';
		v_action_last_modif := 'C';
		v_history_date := localtimestamp;
	ELSE
		v_history_change_type := 'EDIT';
		v_action_last_modif := 'U';
		IF (v_history_last_change_record.history_date - localtimestamp < interval '1 minute')
		THEN
			v_history_date := v_history_last_change_record.history_date + interval '1 minute';
		ELSE
			v_history_date := localtimestamp;
		END IF;
	END IF;

	CASE v_type
		WHEN 'START_DATE'
			THEN
			UPDATE userdatabase u SET startdate = CAST(p_field_value AS timestamp without time zone) 
				WHERE u.databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, CAST(ROUND(EXTRACT(EPOCH FROM DATE(p_field_value))*1000) AS text), p_author_id, p_history_comment, p_layout_group_iteration_id;
		WHEN 'CODE'
			THEN 
			UPDATE userdatabase u SET name = CAST(p_field_value AS character varying(50)) 
				WHERE u.databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, p_field_value, p_author_id, p_history_comment, p_layout_group_iteration_id;
		WHEN 'COUNTRY'
			THEN 
			RETURN false;
		WHEN 'END_DATE'
			THEN 
			UPDATE project SET end_date = CAST(p_field_value AS timestamp without time zone) 
				WHERE databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, CAST(ROUND(EXTRACT(EPOCH FROM DATE(p_field_value))*1000) AS text), p_author_id, p_history_comment, p_layout_group_iteration_id;
		WHEN 'MANAGER'
			THEN 
			UPDATE project SET id_manager = CAST(p_field_value AS integer) 
				WHERE databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, p_field_value, p_author_id, p_history_comment, p_layout_group_iteration_id;
		WHEN 'ORG_UNIT'
			THEN 
			UPDATE partnerindatabase SET partnerid = CAST(p_field_value AS integer) 
				WHERE databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, p_field_value, p_author_id, p_history_comment, p_layout_group_iteration_id;
		WHEN 'TITLE'
			THEN 
			UPDATE userdatabase u SET fullname = CAST(p_field_value AS character varying(500)) 
				WHERE u.databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, p_field_value, p_author_id, p_history_comment, p_layout_group_iteration_id;
		WHEN 'BUDGET'
			THEN RETURN false;
		WHEN 'SPENT', 'RECEIVED', 'PLANNED'
			THEN 
			-- Create budget value
			-- Example : INSERT INTO value (id_value, id_project, action_last_modif, date_last_modif, value, id_flexible_element, id_user_last_modif) VALUES (12783382, 12783370, 'C', '2016-07-22 16:34:05.619', '380%1.23456789E8~382%0.0~383%0.0', 1598, 122);
			-- INSERT INTO history_token (id_history_token, history_date, id_element, id_project, change_type, value, id_user, comment, core_version) VALUES (12783389, '2016-07-22 16:34:05.629', 1598, 12783370, 'ADD', '380%1.23456789E8~382%0.0~383%0.0', 122, NULL, NULL);
			v_value := get_updated_budget_string(p_field_id, p_project_id, p_field_value);
			IF( v_action_last_modif = 'C')
			THEN
				INSERT INTO value(
					    id_value, id_project, action_last_modif, date_last_modif, value, 
					    id_flexible_element, id_user_last_modif, id_layout_group_iteration)
				    SELECT nextval('hibernate_sequence'), p_project_id, v_action_last_modif, localtimestamp, v_value,
						v_budget_field_id, p_author_id, p_layout_group_iteration_id;
			ELSE
				UPDATE value SET action_last_modif = 'U', date_last_modif = localtimestamp,
					value = v_value, id_user_last_modif = p_author_id 
					WHERE id_flexible_element = v_budget_field_id and id_project = p_project_id 
						and (p_layout_group_iteration_id is NULL or id_layout_group_iteration = p_layout_group_iteration_id);
			END IF;
			
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, v_budget_field_id, p_project_id, 
					v_history_change_type, v_value, p_author_id, p_history_comment, p_layout_group_iteration_id;
		WHEN 'OWNER'
			THEN 
			RETURN false;
		WHEN 'DATE'
			THEN 
			v_value := CAST(ROUND(EXTRACT(EPOCH FROM DATE(p_field_value))*1000) AS text);
			IF( v_action_last_modif = 'C')
			THEN
				INSERT INTO value(
					    id_value, id_project, action_last_modif, date_last_modif, value, 
					    id_flexible_element, id_user_last_modif, id_layout_group_iteration)
				    SELECT nextval('hibernate_sequence'), p_project_id, v_action_last_modif, localtimestamp, v_value,
						p_field_id, p_author_id, p_layout_group_iteration_id;
			ELSE
				UPDATE value  SET action_last_modif = 'U', date_last_modif = localtimestamp,
					value = v_value, id_user_last_modif = p_author_id 
					WHERE id_flexible_element = p_field_id and id_project = p_project_id 
						and (p_layout_group_iteration_id is NULL or id_layout_group_iteration = p_layout_group_iteration_id);
			END IF;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type, v_value, p_author_id, p_history_comment, p_layout_group_iteration_id;
		WHEN 'NUMBER', 'TEXT', 'PARAGRAPH', 'CHECKBOX', 'QUESTION'
			THEN 
			IF( v_action_last_modif = 'C')
			THEN
				INSERT INTO value(
					    id_value, id_project, action_last_modif, date_last_modif, value, 
					    id_flexible_element, id_user_last_modif, id_layout_group_iteration)
				    SELECT nextval('hibernate_sequence'), p_project_id, v_action_last_modif, localtimestamp, p_field_value,
						p_field_id, p_author_id, p_layout_group_iteration_id;
			ELSE
				UPDATE value  SET action_last_modif = 'U', date_last_modif = localtimestamp,
					value = p_field_value, id_user_last_modif = p_author_id 
					WHERE id_flexible_element = p_field_id and id_project = p_project_id 
						and (p_layout_group_iteration_id is NULL or id_layout_group_iteration = p_layout_group_iteration_id);
			END IF;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment, id_layout_group_iteration) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type, p_field_value, p_author_id, p_history_comment, p_layout_group_iteration_id;
		ELSE
			return false;
	END CASE;


RETURN true;
END;
$$ LANGUAGE plpgsql;
	

--
-- Build budget string from subfield value
--
CREATE OR REPLACE FUNCTION get_updated_budget_string(p_budget_subfield_id integer, p_project_id integer, p_value text) RETURNS TEXT AS $$
DECLARE
	v_budgetstring text;
	v_budget_field_id integer;
	v_budget_subfield_cursor CURSOR FOR
		SELECT id_budget_sub_field 
		FROM budget_sub_field 
		WHERE id_budget_element = (SELECT id_budget_element FROM budget_sub_field WHERE id_budget_sub_field = p_budget_subfield_id); 
	v_budget_subfield_record RECORD;
	v_delimiter character(1);
	
BEGIN
	v_budgetstring := NULL;
	SELECT id_budget_element INTO v_budget_field_id FROM budget_sub_field WHERE id_budget_sub_field = p_budget_subfield_id; 
	SELECT value
		INTO v_budgetstring
		FROM value
		WHERE id_project = p_project_id and id_flexible_element = v_budget_field_id;		
	IF( v_budgetstring IS NULL )
	THEN
		v_budgetstring := '';
		v_delimiter := '';
		FOR v_budget_subfield_record IN v_budget_subfield_cursor 
		LOOP		
			v_budgetstring := v_budgetstring || v_delimiter || v_budget_subfield_record.id_budget_sub_field || '%';
			IF (v_budget_subfield_record.id_budget_sub_field = p_budget_subfield_id)
			THEN
				v_budgetstring := v_budgetstring || p_value;				
			ELSE			
				v_budgetstring := v_budgetstring || '0';
			END IF;
			v_delimiter := '~';
		END LOOP;
	ELSE
		v_budgetstring := regexp_replace ( v_budgetstring, p_budget_subfield_id || '%' || '[0-9.]+', p_budget_subfield_id || '%' || p_value);
	END IF;
	

	RETURN v_budgetstring;
END;
$$ LANGUAGE plpgsql;




--
-- Import values if for a new project
--
CREATE OR REPLACE FUNCTION import_new_project(p_organization_id integer, p_project_model_id integer, p_project_code character varying(50), p_project_title character varying(500), p_orgunit_id integer, p_author_id integer, p_history_comment character varying(255), p_fieldid_value_array text[][]) RETURNS INTEGER AS $$
DECLARE
	v_project_id integer;	
	v_fieldid_value_pair text[];
BEGIN
	SELECT count(*)
		INTO v_project_id
		FROM userdatabase udb
			inner join project prj on prj.databaseid = udb.databaseid
		WHERE udb.datedeleted is null 
			and udb.name = p_project_code and prj.id_project_model = p_project_model_id;
	IF( v_project_id = 0 )
	THEN
		v_project_id := create_project(p_organization_id, p_project_model_id, p_project_code, 
			p_project_title, NULL, p_orgunit_id , p_author_id , p_history_comment);
		FOREACH v_fieldid_value_pair SLICE 1 IN ARRAY p_fieldid_value_array LOOP
			perform update_field(CAST(v_fieldid_value_pair[1] AS integer), v_fieldid_value_pair[2], v_project_id , p_author_id, p_history_comment);
		END LOOP;
	END IF;
	

	RETURN v_project_id;
END;
$$ LANGUAGE plpgsql;




--
-- Import values in an existing project, if single project with that code for that project model exists
--
CREATE OR REPLACE FUNCTION update_project(p_organization_id integer, p_project_model_id integer, p_project_code character varying(50), p_author_id integer, p_history_comment character varying(255), p_fieldid_value_array text[][], p_projectlastschemaupdate_limit timestamp without time zone default null) RETURNS INTEGER AS $$
DECLARE
	v_project_id integer;	
	v_fieldid_value_pair text[];
BEGIN
	IF (p_projectlastschemaupdate_limit IS NULL)
	THEN
		SELECT count(*)
			INTO v_project_id
			FROM userdatabase udb
				inner join project prj on prj.databaseid = udb.databaseid
			WHERE udb.datedeleted is null 
				and udb.name = p_project_code and prj.id_project_model = p_project_model_id;

	ELSE
		SELECT count(*)
			INTO v_project_id
			FROM userdatabase udb
				inner join project prj on prj.databaseid = udb.databaseid
			WHERE udb.datedeleted is null 
				and udb.name = p_project_code and prj.id_project_model = p_project_model_id
				and udb.lastschemaupdate >= p_projectlastschemaupdate_limit;
	END IF;
	IF( v_project_id = 1 )
	THEN
		SELECT udb.databaseid
			INTO v_project_id
			FROM userdatabase udb
				inner join project prj on prj.databaseid = udb.databaseid
			WHERE udb.datedeleted is null 
				and udb.name = p_project_code and prj.id_project_model = p_project_model_id;
		FOREACH v_fieldid_value_pair SLICE 1 IN ARRAY p_fieldid_value_array LOOP
			perform update_field(CAST(v_fieldid_value_pair[1] AS integer), v_fieldid_value_pair[2], v_project_id , p_author_id, p_history_comment);
		END LOOP;
	END IF;
	

	RETURN v_project_id;
END;
$$ LANGUAGE plpgsql;


--
-- Create or update matching project 
-- 
-- Note: doesn't update project code or project title of an existing project, unless they are provided in the p_fieldid_value_array
--
CREATE OR REPLACE FUNCTION create_or_update_project(p_organization_id integer, p_project_model_id integer, p_project_code character varying(50), p_project_title character varying(500), p_orgunit_id integer, p_author_id integer, p_history_comment character varying(255), p_fieldid_value_array text[][]) RETURNS INTEGER AS $$
DECLARE
	v_project_id integer;	
	v_fieldid_value_pair text[];
BEGIN
	SELECT count(*)
		INTO v_project_id
		FROM userdatabase udb
			inner join project prj on prj.databaseid = udb.databaseid
		WHERE udb.datedeleted is null 
			and udb.name = p_project_code and prj.id_project_model = p_project_model_id;
	IF( v_project_id = 0 )
	THEN
		v_project_id := create_project(p_organization_id, p_project_model_id, p_project_code, 
			p_project_title, NULL, p_orgunit_id , p_author_id , p_history_comment);
		FOREACH v_fieldid_value_pair SLICE 1 IN ARRAY p_fieldid_value_array LOOP
			perform update_field(CAST(v_fieldid_value_pair[1] AS integer), v_fieldid_value_pair[2], v_project_id , p_author_id, p_history_comment);
		END LOOP;
	ELSE
		SELECT update_project(p_organization_id, p_project_model_id, p_project_code, p_author_id, p_history_comment, p_fieldid_value_array)
		INTO v_project_id;
	END IF;
	

	RETURN v_project_id;
END;
$$ LANGUAGE plpgsql;



--
-- Create a new group iteration in a project and import values into it
--
CREATE OR REPLACE FUNCTION create_new_iteration(p_organization_id integer, p_project_model_id integer, p_project_code character varying(50), p_layout_group_id integer, p_iteration_title character varying(30), p_author_id integer, p_history_comment character varying(255), p_fieldid_value_array text[][]) RETURNS INTEGER AS $$
DECLARE
	v_project_id integer;	
	v_layout_group_iteration_id integer;
	v_fieldid_value_pair text[];
BEGIN
	SELECT count(*)
		INTO v_project_id
		FROM userdatabase udb
			inner join project prj on prj.databaseid = udb.databaseid
		WHERE udb.datedeleted is null 
			and udb.name = p_project_code and prj.id_project_model = p_project_model_id;
	IF( v_project_id != 0 )
	THEN
		SELECT udb.databaseid
		INTO v_project_id
		FROM userdatabase udb
			inner join project prj on prj.databaseid = udb.databaseid
		WHERE udb.datedeleted is null 
			and udb.name = p_project_code and prj.id_project_model = p_project_model_id;
			
		-- create iteration
		SELECT nextval('hibernate_sequence') INTO v_layout_group_iteration_id;
		INSERT INTO layout_group_iteration (id_layout_group_iteration, id_layout_group, id_container, name) 
			VALUES (v_layout_group_iteration_id, p_layout_group_id, v_project_id, p_iteration_title);
		
		-- fill iteration with given values
		FOREACH v_fieldid_value_pair SLICE 1 IN ARRAY p_fieldid_value_array LOOP
			perform update_field(CAST(v_fieldid_value_pair[1] AS integer), v_fieldid_value_pair[2], v_project_id , p_author_id, p_history_comment, v_layout_group_iteration_id);
		END LOOP;
	END IF;
	

	RETURN v_layout_group_iteration_id;
END;
$$ LANGUAGE plpgsql;



--
--
-- Get project if from project code
--
-- Handy function to get a project id from a project code
CREATE OR REPLACE FUNCTION get_project_id_from_code(p_project_code character varying(50)) RETURNS integer AS $$
DECLARE
	v_result integer;
BEGIN
	SELECT databaseid
	INTO v_result
	FROM userdatabase
	WHERE name = p_project_code
	ORDER BY databaseid;

	RETURN v_result;
END;
$$ LANGUAGE plpgsql;


--
-- Edit project link
--
-- Parameters:
--	p_funding_code : project code of funding project
--	p_funded_code : project code of funded project
--	p_percentage : funded project budget funding percentage
--	p_remove : true to remove the project link
--	p_duplicates_management : in case of duplicates on one or two the project codes, 'a' to update all projects, 'n' to update none
--
CREATE OR REPLACE FUNCTION edit_project_link(p_funding_code character varying(50), p_funded_code character varying(50), p_percentage double precision, p_remove boolean default false, p_duplicates_management character default 'n') RETURNS boolean AS $$
DECLARE
	v_id_funding integer;
	v_id_project_funding integer;
	v_id_project_funded integer;
	v_matchingprojects_count integer;
	cur_matching_fundingprojects CURSOR FOR 
		SELECT databaseid FROM userdatabase WHERE name = p_funding_code;
	cur_matching_fundedprojects CURSOR FOR 
		SELECT databaseid FROM userdatabase WHERE name = p_funded_code;
	rec_matching_fundingprojects RECORD;
	rec_matching_fundedprojects RECORD;
	v_result boolean;
BEGIN
	v_id_funding := 0;
	v_result := true;
	v_id_project_funding := get_project_id_from_code(p_funding_code);
	v_id_project_funded := get_project_id_from_code(p_funded_code);

	IF (v_id_project_funding IS NULL)
	THEN
		RAISE NOTICE 'No project found for project code "%"', p_funding_code;
		v_result := false;
	END IF;

	IF (v_id_project_funded IS NULL)
	THEN
		RAISE NOTICE 'No project found for project code "%"', p_funded_code;
		v_result := false;
	END IF;

	-- Continue only if all code match with existing projects
	IF (v_result)
	THEN
		SELECT count(*)
		INTO v_matchingprojects_count
		FROM userdatabase udb
		WHERE udb.name IN (p_funding_code, p_funded_code);

		IF (v_matchingprojects_count > 2)
		THEN
			-- Duplicates exist for given project codes
			IF (p_duplicates_management = 'n')
			THEN
				RAISE NOTICE 'Duplicates found for given project codes (%, %), so no update because "update none" duplicates management policy requested.', p_funding_code, p_funded_code;
				RETURN false;
			ELSIF (p_duplicates_management = 'a')
			THEN
				RAISE NOTICE 'Duplicates found for given project codes (%, %), so all duplicates will be updated because "update all" duplicates management policy requested.', p_funding_code, p_funded_code;
			ELSE
				RAISE NOTICE 'Duplicates found for given project codes (%, %), and no update because no valid duplicates management policy ("a" or "n") requested!', p_funding_code, p_funded_code;
				RETURN false;
			END IF;
		END IF;
		
		FOR rec_matching_fundingprojects IN cur_matching_fundingprojects
		LOOP
			v_id_project_funding := rec_matching_fundingprojects.databaseid;
			FOR rec_matching_fundedprojects IN cur_matching_fundedprojects
			LOOP
				v_id_project_funded := rec_matching_fundedprojects.databaseid;
				IF (p_remove)
				THEN
					-- Remove project link
					DELETE FROM project_funding
					WHERE id_project_funded = v_id_project_funded
						and id_project_funding = v_id_project_funding;
					RAISE NOTICE 'Project link "%"(id:%)=>"%"(id:%) successfully removed.', p_funding_code, v_id_project_funding, p_funded_code, v_id_project_funded;
				ELSE
					-- Create or Update project link
					SELECT id_funding
					INTO v_id_funding
					FROM project_funding
					WHERE id_project_funded = v_id_project_funded
						and id_project_funding = v_id_project_funding;
					IF (v_id_funding IS NULL)
					THEN
						-- no project link exists yet => Create it
						INSERT INTO project_funding(
							id_funding, percentage, id_project_funded, id_project_funding)
						SELECT nextval('hibernate_sequence'), p_percentage, v_id_project_funded, v_id_project_funding;
						RAISE NOTICE 'Project link "%"(id:%)=>"%"(id:%)(% %%) successfully created.', p_funding_code, v_id_project_funding, p_funded_code, v_id_project_funded, p_percentage;
					ELSE
						-- a project link already exists => Update it
						UPDATE project_funding
							SET percentage = p_percentage
						WHERE id_project_funded = v_id_project_funded
							and id_project_funding = v_id_project_funding;				
						RAISE NOTICE 'Project link "%"(id:%)=>"%"(id:%) percentage value successfully updated to % %%.', p_funding_code, v_id_project_funding, p_funded_code, v_id_project_funded, p_percentage;
					END IF;
				END IF;
			END LOOP;
		END LOOP;
	END IF;

	RETURN v_result;
END;
$$ LANGUAGE plpgsql;







-- DROPS
-- drop function create_project(integer, integer, character varying(50), character varying(500), numeric(2), integer, integer, character varying(255));
-- drop function get_updated_budget_string(integer, integer, text);
-- drop function update_field(integer, text, integer, integer, character varying(255), integer);
-- drop function import_new_project(integer, integer, character varying(50), character varying(500), integer, integer, character varying(255), text[][]);
-- drop function update_project(integer, integer, character varying(50), integer, character varying(255), text[][], timestamp without time zone);
-- drop function create_or_update_project(integer, integer, character varying(50), character varying(500), integer, integer, character varying(255), text[][]);
-- drop function create_new_iteration(integer, integer, character varying(50), integer, character varying(30), integer, character varying(255),  text[][]);
-- drop function get_project_id_from_code(character varying(50));
-- drop function edit_project_link(character varying(50), character varying(50), double precision, boolean, character);
