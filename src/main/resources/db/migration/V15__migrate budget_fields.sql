
--
-- Migrate all old budget fields into v2.2 new budget ratio format
-- 
--
CREATE OR REPLACE FUNCTION migrate_budget_fields(p_droptables boolean default false) RETURNS INTEGER AS $$
DECLARE	
	--
	-- Get budget_element fields
	--
	budget_elements_cursor CURSOR FOR
		SELECT be.id_flexible_element old_id_flexible_element, be.id_ratio_divisor old_subfield_id_ratio_divisor, be.id_ratio_dividend old_subfield_id_ratio_dividend, 
			lc.id_layout_constraint, lc.id_layout_group, lc.sort_order, NULL as new_id_flexible_element, NULL as new_id_element_divisor, NULL as new_id_element_dividend
		FROM budget_element be
			inner join layout_constraint lc on lc.id_flexible_element = be.id_flexible_element
			inner join layout_group lg on lg.id_layout_group = lc.id_layout_group
		WHERE lg.id_layout not in (select id_layout from project_banner union select id_layout from org_unit_banner);
	budget_element_record RECORD;

	budget_layout_constraint_banner_cursor CURSOR FOR
		SELECT lc.id_layout_constraint
		FROM budget_element be
			inner join layout_constraint lc on lc.id_flexible_element = be.id_flexible_element
			inner join layout_group lg on lg.id_layout_group = lc.id_layout_group
		WHERE lg.id_layout in (select id_layout from project_banner union select id_layout from org_unit_banner);
	budget_layout_constraint_banner_record RECORD;

	--
	-- Get budget history values
	--
	budget_history_values_cursor CURSOR FOR
	SELECT ht.id_history_token, ht.history_date, ht.id_element, ht.id_project, ht.change_type, ht.value, ht.id_user, ht.comment, ht.core_version
	FROM history_token ht
		inner join budget_element be on be.id_flexible_element = ht.id_element
	ORDER BY history_date, id_history_token;
	budget_history_values_record RECORD;

	--
	-- Get budget importation scheme mapping
	--
	budget_importation_mapping_cursor CURSOR FOR
	SELECT ivbsf.id_budget_sub_field old_id_budget_sub_field, ivbsf.var_id, ivbsf.var_fle_id
	FROM importation_variable_budget_sub_field ivbsf ;
	budget_importation_mapping_record RECORD;
	
	v_result INTEGER;
	v_tmp_id INTEGER;
	v_planned_id INTEGER;
	v_spent_id INTEGER;
	v_received_id INTEGER;
	v_newplanned_id INTEGER;
	v_newspent_id INTEGER;
	v_newreceived_id INTEGER;
	v_value TEXT;
	v_oldvalue TEXT;
	v_droptruncate_action TEXT;
BEGIN	
	v_result := 0;
	
	-- temp table to keep all mapping between old and new ids
	CREATE TEMPORARY TABLE tmp_migrate_budget_field AS
	SELECT be.id_flexible_element old_id_flexible_element, 
		be.id_ratio_divisor old_subfield_id_ratio_divisor, be.id_ratio_dividend old_subfield_id_ratio_dividend, 0 as old_subfield_id_received,
		lc.id_layout_constraint, lc.id_layout_group, lc.sort_order, 
		0 as new_id_flexible_element, 0 as new_id_element_divisor, 0 as new_id_element_dividend, 0 as new_id_element_received
	FROM budget_element be
		inner join layout_constraint lc on lc.id_flexible_element = be.id_flexible_element
		inner join layout_group lg on lg.id_layout_group = lc.id_layout_group
	WHERE lg.id_layout not in (select id_layout from project_banner union select id_layout from org_unit_banner);		

	--
	-- 1. CREATE BUDGET RATIO FIELDS
	--
	FOR budget_element_record IN budget_elements_cursor 
	LOOP	
		-- --
		-- sort_order management: leave room for the new fields
		UPDATE layout_constraint
			SET sort_order = sort_order + 3
			WHERE id_layout_group = budget_element_record.id_layout_group and sort_order > budget_element_record.sort_order;

		-- --
		-- create divisor number field ("PLANNED")
		SELECT nextval('hibernate_sequence') INTO v_tmp_id;
		INSERT INTO flexible_element(
				id_flexible_element, amendable, label, validates, id_privacy_group, 
				exportable, globally_exportable, creation_date, is_disabled, 
				disabled_date, code)
			SELECT v_tmp_id, fe.amendable, 'Planned budget', fe.validates, fe.id_privacy_group,
				fe.exportable, fe.globally_exportable, fe.creation_date, fe.is_disabled,
				fe.disabled_date, fe.code || '_planned'
			FROM flexible_element fe
			WHERE fe.id_flexible_element = budget_element_record.old_id_flexible_element;
		INSERT INTO textarea_element (is_decimal, type, id_flexible_element)
			VALUES(TRUE, 'N', v_tmp_id);
		-- "spent budget" set in new position just below old full budget field
		INSERT INTO layout_constraint(
				id_layout_constraint, sort_order, id_flexible_element, id_layout_group)
			SELECT nextval('hibernate_sequence'), budget_element_record.sort_order + 1, v_tmp_id, budget_element_record.id_layout_group;
		UPDATE tmp_migrate_budget_field 
			SET new_id_element_divisor = v_tmp_id 
			WHERE old_id_flexible_element = budget_element_record.old_id_flexible_element;
			
		-- --
		-- create dividend number field ("SPENT")
		SELECT nextval('hibernate_sequence') INTO v_tmp_id;
		INSERT INTO flexible_element(
				id_flexible_element, amendable, label, validates, id_privacy_group, 
				exportable, globally_exportable, creation_date, is_disabled, 
				disabled_date, code)
			SELECT v_tmp_id, fe.amendable, 'Spent budget', fe.validates, fe.id_privacy_group,
				fe.exportable, fe.globally_exportable, fe.creation_date, fe.is_disabled,
				fe.disabled_date, fe.code || '_spent'
			FROM flexible_element fe
			WHERE fe.id_flexible_element = budget_element_record.old_id_flexible_element;
		INSERT INTO textarea_element (is_decimal, type, id_flexible_element)
			VALUES(TRUE, 'N', v_tmp_id);
		-- "planned budget" set in new position just two points below old full budget field
		INSERT INTO layout_constraint(
				id_layout_constraint, sort_order, id_flexible_element, id_layout_group)
			SELECT nextval('hibernate_sequence'), budget_element_record.sort_order + 2, v_tmp_id, budget_element_record.id_layout_group;
		UPDATE tmp_migrate_budget_field 
			SET new_id_element_dividend = v_tmp_id 
			WHERE old_id_flexible_element = budget_element_record.old_id_flexible_element;
			
		-- --
		-- create received number field ("RECEIVED")
		SELECT nextval('hibernate_sequence') INTO v_tmp_id;
		INSERT INTO flexible_element(
				id_flexible_element, amendable, label, validates, id_privacy_group, 
				exportable, globally_exportable, creation_date, is_disabled, 
				disabled_date, code)
			SELECT v_tmp_id, fe.amendable, 'Received budget', fe.validates, fe.id_privacy_group,
				fe.exportable, fe.globally_exportable, fe.creation_date, fe.is_disabled,
				fe.disabled_date, fe.code || '_received'
			FROM flexible_element fe
			WHERE fe.id_flexible_element = budget_element_record.old_id_flexible_element;
		INSERT INTO textarea_element (is_decimal, type, id_flexible_element)
			VALUES(TRUE, 'N', v_tmp_id);
		-- "received budget" set in new position three points below old full budget field
		INSERT INTO layout_constraint(
				id_layout_constraint, sort_order, id_flexible_element, id_layout_group)
			SELECT nextval('hibernate_sequence'), budget_element_record.sort_order + 3, v_tmp_id, budget_element_record.id_layout_group;
		UPDATE tmp_migrate_budget_field 
			SET new_id_element_received = v_tmp_id,
				old_subfield_id_received = (SELECT id_budget_sub_field
								FROM budget_sub_field 
								WHERE type = 'RECEIVED' 
									and id_budget_element = budget_element_record.old_id_flexible_element )
			WHERE old_id_flexible_element = budget_element_record.old_id_flexible_element;

		
		-- --
		-- create budget ratio default field
		SELECT nextval('hibernate_sequence') INTO v_tmp_id;
		INSERT INTO flexible_element(
				id_flexible_element, amendable, label, validates, id_privacy_group, 
				exportable, globally_exportable, creation_date, is_disabled, 
				disabled_date, code)
			SELECT v_tmp_id, fe.amendable, 'Budget consumption', fe.validates, fe.id_privacy_group,
				fe.exportable, fe.globally_exportable, fe.creation_date, fe.is_disabled,
				fe.disabled_date, fe.code
			FROM flexible_element fe
			WHERE fe.id_flexible_element = budget_element_record.old_id_flexible_element;
		INSERT INTO budget_ratio_element (id_flexible_element, id_spent_field, id_planned_field)
			SELECT v_tmp_id, new_id_element_dividend,  new_id_element_divisor
			FROM tmp_migrate_budget_field
			WHERE old_id_flexible_element = budget_element_record.old_id_flexible_element;
		INSERT INTO default_flexible_element(type, id_flexible_element)
			VALUES ('BUDGET_RATIO', v_tmp_id);
		-- "budget ratio" field set in same position as old full budget field
		UPDATE layout_constraint
			SET id_flexible_element = v_tmp_id				 
			WHERE id_layout_group = budget_element_record.id_layout_group and id_flexible_element = budget_element_record.old_id_flexible_element;
		UPDATE tmp_migrate_budget_field 
			SET new_id_flexible_element = v_tmp_id 
			WHERE old_id_flexible_element = budget_element_record.old_id_flexible_element;

	END LOOP;	
	-- --
	-- update banners
	FOR budget_layout_constraint_banner_record IN budget_layout_constraint_banner_cursor 
	LOOP
		UPDATE layout_constraint lc
			SET id_flexible_element = (select new_id_flexible_element 
						from tmp_migrate_budget_field 
						where old_id_flexible_element = lc.id_flexible_element)
			WHERE lc.id_layout_constraint = budget_layout_constraint_banner_record.id_layout_constraint;
	END LOOP;
	
	--
	-- 2. CREATE BUDGET RATIO VALUES AND MODIF HISTORY
	--
	FOR budget_history_values_record IN budget_history_values_cursor
	LOOP
		SELECT old_subfield_id_ratio_divisor, old_subfield_id_ratio_dividend, old_subfield_id_received,
			new_id_element_divisor, new_id_element_dividend, new_id_element_received
			INTO v_planned_id, v_spent_id, v_received_id, v_newplanned_id, v_newspent_id, v_newreceived_id
			FROM tmp_migrate_budget_field
			WHERE old_id_flexible_element = budget_history_values_record.id_element;

		IF ( budget_history_values_record.change_type = 'ADD' )
		THEN
			-- if old budget history change type is initial 'ADD', create initial value for each new field
			
			-- planned
			v_value := regexp_matches(budget_history_values_record.value, v_planned_id || '%' || '[0-9.]+');
			v_value := split_part(trim(both '{}' from v_value),'%',2);
			IF (v_value is not NULL)
			THEN
				PERFORM update_field_anytime(v_newplanned_id, v_value, budget_history_values_record.id_project, 
					budget_history_values_record.id_user, budget_history_values_record.comment, budget_history_values_record.history_date);
			END IF;
			
			-- spent
			v_value := regexp_matches(budget_history_values_record.value, v_spent_id || '%' || '[0-9.]+');
			v_value := split_part(trim(both '{}' from v_value),'%',2);
			IF (v_value is not NULL)
			THEN
				PERFORM update_field_anytime(v_newspent_id, v_value, budget_history_values_record.id_project, 
					budget_history_values_record.id_user, budget_history_values_record.comment, budget_history_values_record.history_date);
			END IF;
			
			-- received
			v_value := regexp_matches(budget_history_values_record.value, v_received_id || '%' || '[0-9.]+');
			v_value := split_part(trim(both '{}' from v_value),'%',2);
			IF (v_value is not NULL)
			THEN
				PERFORM update_field_anytime(v_newreceived_id, v_value, budget_history_values_record.id_project, 
					budget_history_values_record.id_user, budget_history_values_record.comment, budget_history_values_record.history_date);
			END IF;
		ELSE
			-- if old budget history change type is 'EDIT', only update changed values
			
			-- planned
			v_value := regexp_matches(budget_history_values_record.value, v_planned_id || '%' || '[0-9.]+');
			v_value := split_part(trim(both '{}' from v_value),'%',2);
			SELECT value 
				INTO v_oldvalue 
				FROM value 
				WHERE id_flexible_element = v_newplanned_id
					and id_project = budget_history_values_record.id_project;
			IF (v_value is not NULL and v_value != v_oldvalue)
			THEN
				PERFORM update_field_anytime(v_newplanned_id, v_value, budget_history_values_record.id_project, 
					budget_history_values_record.id_user, budget_history_values_record.comment, budget_history_values_record.history_date);				
			END IF;
			
			-- spent
			v_value := regexp_matches(budget_history_values_record.value, v_spent_id || '%' || '[0-9.]+');
			v_value := split_part(trim(both '{}' from v_value),'%',2);
			SELECT value 
				INTO v_oldvalue 
				FROM value 
				WHERE id_flexible_element = v_newspent_id
					and id_project = budget_history_values_record.id_project;
			IF (v_value is not NULL and v_value != v_oldvalue)
			THEN
				PERFORM update_field_anytime(v_newspent_id, v_value, budget_history_values_record.id_project, 
					budget_history_values_record.id_user, budget_history_values_record.comment, budget_history_values_record.history_date);				
			END IF;
			
			-- received
			v_value := regexp_matches(budget_history_values_record.value, v_received_id || '%' || '[0-9.]+');
			v_value := split_part(trim(both '{}' from v_value),'%',2);
			SELECT value 
				INTO v_oldvalue 
				FROM value 
				WHERE id_flexible_element = v_newreceived_id
					and id_project = budget_history_values_record.id_project;
			IF (v_value is not NULL and v_value != v_oldvalue)
			THEN
				PERFORM update_field_anytime(v_newreceived_id, v_value, budget_history_values_record.id_project, 
					budget_history_values_record.id_user, budget_history_values_record.comment, budget_history_values_record.history_date);				
			END IF;

		END IF;
		
	END LOOP;	
	
	--
	-- 3. UPDATE IMPORTATION SCHEME
	--
	FOR budget_importation_mapping_record IN budget_importation_mapping_cursor
	LOOP
		v_tmp_id := 0;
		CASE( get_field_type(budget_importation_mapping_record.old_id_budget_sub_field) )
			WHEN 'PLANNED'
			THEN
				SELECT new_id_element_divisor
					INTO v_tmp_id
					FROM tmp_migrate_budget_field
					WHERE old_subfield_id_ratio_divisor = budget_importation_mapping_record.old_id_budget_sub_field;
			WHEN 'SPENT'
			THEN
				SELECT new_id_element_dividend
					INTO v_tmp_id
					FROM tmp_migrate_budget_field
					WHERE old_subfield_id_ratio_dividend = budget_importation_mapping_record.old_id_budget_sub_field;
			WHEN 'RECEIVED'
			THEN
				SELECT new_id_element_received
					INTO v_tmp_id
					FROM tmp_migrate_budget_field
					WHERE old_subfield_id_received = budget_importation_mapping_record.old_id_budget_sub_field;
		END CASE;
			
		UPDATE importation_scheme_variable_flexible_element
			SET id_flexible_element = v_tmp_id,
				var_id = budget_importation_mapping_record.var_id
			WHERE var_fle_id = budget_importation_mapping_record.var_fle_id;
	END LOOP;


	--
	-- 3. CHECK, CLEAN AND DELETE TABLES
	--
	IF ( p_droptables )
	THEN
		v_droptruncate_action := 'DROP';
	ELSE
		v_droptruncate_action := 'TRUNCATE';
	END IF;
	
	-- clean importation_variable_budget_sub_field
	DELETE FROM importation_variable_budget_sub_field
		WHERE id_budget_sub_field IN (	(SELECT old_subfield_id_ratio_divisor FROM tmp_migrate_budget_field)
					UNION (SELECT old_subfield_id_ratio_dividend FROM tmp_migrate_budget_field)
					UNION (SELECT old_subfield_id_received FROM tmp_migrate_budget_field)	) ;
	SELECT COUNT(*) INTO v_result FROM importation_variable_budget_sub_field;
	IF ( v_result > 0)
	THEN
		RAISE EXCEPTION 'Some importation budget sub-fields mapping have not been migrated!';
	ELSE
		PERFORM v_droptruncate_action || ' TABLE importation_variable_budget_sub_field';
	END IF;
	
	-- clean importation_scheme_variable_budget_element
	PERFORM v_droptruncate_action || ' DROP TABLE importation_scheme_variable_budget_element';	
	
	-- clean budget_sub_field (see ratio to NULL in budget_element because cross FK constraints)
	UPDATE budget_element
		SET id_ratio_divisor = NULL, id_ratio_dividend = NULL
		WHERE id_flexible_element IN (SELECT old_id_flexible_element FROM tmp_migrate_budget_field);
	DELETE FROM budget_sub_field
		WHERE id_budget_element IN (SELECT old_id_flexible_element FROM tmp_migrate_budget_field);
	SELECT COUNT(*) INTO v_result FROM budget_sub_field;
	IF ( v_result > 0)
	THEN
		RAISE EXCEPTION 'Some budget sub-fields have not been migrated!';
	END IF;
	
	-- clean budget_element
	DELETE FROM budget_element
		WHERE id_flexible_element IN (SELECT old_id_flexible_element FROM tmp_migrate_budget_field);
	SELECT COUNT(*) INTO v_result FROM budget_element;
	IF ( v_result > 0)
	THEN
		RAISE EXCEPTION 'Some budget fields have not been migrated!';
	ELSE
		PERFORM v_droptruncate_action || ' TABLE budget_element, budget_sub_field';
	END IF;

	-- clean default_flexible_element
	DELETE FROM default_flexible_element
		WHERE id_flexible_element IN (SELECT old_id_flexible_element FROM tmp_migrate_budget_field);
	SELECT COUNT(*) INTO v_result FROM default_flexible_element WHERE type = 'BUDGET';
	IF ( v_result > 0)
	THEN
		RAISE EXCEPTION 'Some budget default fields have not been migrated!';
	END IF;
	
	-- clean value
	DELETE FROM value
		WHERE id_flexible_element IN (SELECT old_id_flexible_element FROM tmp_migrate_budget_field);
	
	-- clean history_token
	DELETE FROM history_token
		WHERE id_element IN (SELECT old_id_flexible_element FROM tmp_migrate_budget_field);

	-- clean flexible_element
	DELETE FROM flexible_element
		WHERE id_flexible_element IN (SELECT old_id_flexible_element FROM tmp_migrate_budget_field);
	
	RETURN v_result;
END;
$$ LANGUAGE plpgsql;







--
-- Return as a string the field type from any field (aka flexible element) id.
-- Also returns the budget sub-field type if a budget sub-field it is given.
--
-- Note: id_flexible_element and id_budget_sub_field have never the same value
--
CREATE OR REPLACE FUNCTION get_field_type(p_field_id bigint) RETURNS character varying(20) AS $$
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
CREATE OR REPLACE FUNCTION get_field_type(p_field_id integer) RETURNS character varying(20) AS $$
BEGIN
	RETURN get_field_type(CAST(p_field_id as bigint));
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
--   - NUMBER : number like '25080' or '78450.23'
--   - TEXT : text like 'plusieurs mots'
--   - PARAGRAPH : one more more lines of text, each line of text must be separated only by a NEWLINE (ASCII code 10) character, like 'first line' || chr(10) || ' and second important line'
CREATE OR REPLACE FUNCTION update_field_anytime(p_field_id integer, p_field_value text, p_project_id integer, p_author_id integer, p_history_comment character varying(255), p_timestamp timestamp without time zone) RETURNS boolean AS $$
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
			WHERE id_element = v_budget_field_id and id_project = p_project_id;
	ELSE
		SELECT change_type, history_date
			INTO v_history_last_change_record 
			FROM history_token
			WHERE id_element = p_field_id and id_project = p_project_id;
	END IF;
	IF(v_history_last_change_record IS NULL)
	THEN
		v_history_change_type := 'ADD';
		v_action_last_modif := 'C';
		v_history_date := p_timestamp;
	ELSE
		v_history_change_type := 'EDIT';
		v_action_last_modif := 'U';
		IF (p_timestamp - v_history_last_change_record.history_date < interval '1 minute')
		THEN
			v_history_date := v_history_last_change_record.history_date + interval '1 minute';
		ELSE
			v_history_date := p_timestamp;
		END IF;
	END IF;

	CASE v_type
		WHEN 'START_DATE'
			THEN
			UPDATE userdatabase u SET startdate = CAST(p_field_value AS timestamp without time zone) 
				WHERE u.databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, CAST(ROUND(EXTRACT(EPOCH FROM DATE(p_field_value))*1000) AS text), p_author_id, p_history_comment;
		WHEN 'CODE'
			THEN 
			UPDATE userdatabase u SET name = CAST(p_field_value AS character varying(50)) 
				WHERE u.databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, p_field_value, p_author_id, p_history_comment;
		WHEN 'COUNTRY'
			THEN 
			RETURN false;
		WHEN 'END_DATE'
			THEN 
			UPDATE project SET end_date = CAST(p_field_value AS timestamp without time zone) 
				WHERE databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, CAST(ROUND(EXTRACT(EPOCH FROM DATE(p_field_value))*1000) AS text), p_author_id, p_history_comment;
		WHEN 'MANAGER'
			THEN 
			UPDATE project SET id_manager = CAST(p_field_value AS integer) 
				WHERE databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, p_field_value, p_author_id, p_history_comment;
		WHEN 'ORG_UNIT'
			THEN 
			UPDATE partnerindatabase SET partnerid = CAST(p_field_value AS integer) 
				WHERE databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, p_field_value, p_author_id, p_history_comment;
		WHEN 'TITLE'
			THEN 
			UPDATE userdatabase u SET fullname = CAST(p_field_value AS character varying(500)) 
				WHERE u.databaseid = p_project_id;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type as change_type, p_field_value, p_author_id, p_history_comment;
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
					    id_flexible_element, id_user_last_modif)
				    SELECT nextval('hibernate_sequence'), p_project_id, v_action_last_modif, localtimestamp, v_value,
						v_budget_field_id, p_author_id;
			ELSE
				UPDATE value  SET action_last_modif = 'U', date_last_modif = localtimestamp,
					value = v_value, id_user_last_modif = p_author_id 
					WHERE id_flexible_element = v_budget_field_id and id_project = p_project_id;
			END IF;
			
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, v_budget_field_id, p_project_id, 
					v_history_change_type, v_value, p_author_id, p_history_comment;
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
					    id_flexible_element, id_user_last_modif)
				    SELECT nextval('hibernate_sequence'), p_project_id, v_action_last_modif, localtimestamp, v_value,
						p_field_id, p_author_id;
			ELSE
				UPDATE value  SET action_last_modif = 'U', date_last_modif = localtimestamp,
					value = v_value, id_user_last_modif = p_author_id 
					WHERE id_flexible_element = p_field_id and id_project = p_project_id;
			END IF;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type, v_value, p_author_id, p_history_comment;
		WHEN 'NUMBER', 'TEXT', 'PARAGRAPH'
			THEN 
			IF( v_action_last_modif = 'C')
			THEN
				INSERT INTO value(
					    id_value, id_project, action_last_modif, date_last_modif, value, 
					    id_flexible_element, id_user_last_modif)
				    SELECT nextval('hibernate_sequence'), p_project_id, v_action_last_modif, localtimestamp, p_field_value,
						p_field_id, p_author_id;
			ELSE
				UPDATE value  SET action_last_modif = 'U', date_last_modif = localtimestamp,
					value = p_field_value, id_user_last_modif = p_author_id 
					WHERE id_flexible_element = p_field_id and id_project = p_project_id;
			END IF;
			--no management of the "core_version" column because the column is only filled when validating a core version from the User Interface
			INSERT INTO history_token(id_history_token, history_date, id_element, id_project, 
					change_type, value, id_user, comment) 
				SELECT nextval('hibernate_sequence'), v_history_date, p_field_id, p_project_id, 
					v_history_change_type, p_field_value, p_author_id, p_history_comment;
		ELSE
			return false;
	END CASE;


RETURN true;
END;
$$ LANGUAGE plpgsql;



-- Call the budget field migration function
START transaction;
SELECT migrate_budget_fields();
DROP FUNCTION migrate_budget_fields(boolean);
DROP FUNCTION get_field_type(integer);
DROP FUNCTION get_field_type(bigint);
DROP FUNCTION update_field_anytime(integer, text, integer, integer, character varying, timestamp without time zone);
COMMIT;


	
