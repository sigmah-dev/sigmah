--
-- Create easily understandable views of all project models with all project data (like Global export)
--
-- Note : materialized views are only available since PostgreSQL v9.3
CREATE OR REPLACE FUNCTION create_sigmah_datamart_views(drop_views boolean default false, use_materialized_views boolean default false) RETURNS BOOLEAN AS $$
DECLARE
	project_models_cursor CURSOR FOR select quote_ident(prjm.id_project_model || '-' || prjm.name) as project_model, prjm.id_project_model from project_model prjm;
	project_model_record RECORD;
	fields_cursor CURSOR FOR
		select quote_ident(prjm.id_project_model || '-' || prjm.name) as project_model, 
		fe.id_flexible_element as column_id,
		CASE
		WHEN fe.label is null THEN quote_ident('c' || fe.id_flexible_element || '-' || dfe.type)
		ELSE quote_ident('c' || fe.id_flexible_element || '-' || substring(fe.label from 0 for 20))
		END as column_name, 
		'Field ''' || quote_literal(fe.label) || ''' in group ''' || quote_literal(lg.title) || ''' of phase ''' || quote_literal(pm.name) || ''' (empty if project details)' as column_comment 
		from flexible_element fe
		inner join layout_constraint lc on (lc.id_flexible_element = fe.id_flexible_element)
		inner join layout_group lg on (lc.id_layout_group = lg.id_layout_group)
		left outer join phase_model pm on (pm.id_layout = lg.id_layout)
		left outer join project_details pd on (pd.id_layout = lg.id_layout)
		left outer join project_model prjm on (prjm.id_project_model = pd.id_project_model or prjm.id_project_model = pm.id_project_model)
		left outer join default_flexible_element dfe on (dfe.id_flexible_element = fe.id_flexible_element)
		where prjm.name is not null
		order by project_model, pm.display_order, lg.row_index, lc.sort_order;
	field_record RECORD;
	create_view_query TEXT;
BEGIN

FOR project_model_record IN project_models_cursor 
LOOP
	
	--Drop View
	IF (drop_views)
	THEN
		IF (use_materialized_views)
		THEN
			EXECUTE 'DROP MATERIALIZED VIEW ' || project_model_record.project_model;
		ELSE
			EXECUTE 'DROP VIEW ' || project_model_record.project_model;
		END IF;

		-- Continue looping but don't build view creation query
		CONTINUE;
	END IF;


	-- Columns
	IF (use_materialized_views)
	THEN
		create_view_query := 'CREATE MATERIALIZED VIEW ' || project_model_record.project_model || ' (project_code, project_title';
	ELSE
		create_view_query := 'CREATE OR REPLACE VIEW ' || project_model_record.project_model || ' (project_code, project_title';
	END IF;
	FOR field_record IN fields_cursor 
	LOOP
		IF (field_record.project_model = project_model_record.project_model)
		THEN
			create_view_query := create_view_query || ', ' || field_record.column_name;
		END IF;
	END LOOP;

	-- Select clause
	create_view_query := create_view_query || ') AS SELECT udb.name as project_code, udb.fullname as project_title ';
	FOR field_record IN fields_cursor 
	LOOP
		IF (field_record.project_model = project_model_record.project_model)
		THEN
			create_view_query := create_view_query 
				|| ', CASE '
				|| ' WHEN ' || field_record.column_id || ' IN (SELECT id_flexible_element FROM default_flexible_element) '
				|| '     THEN CASE (SELECT type FROM default_flexible_element WHERE id_flexible_element = ' || field_record.column_id || ' ) '
				|| '          WHEN ''CODE'' '
				|| '              THEN udb.name '
				|| '          WHEN ''TITLE'' '
				|| '              THEN udb.fullname '
				|| '          WHEN ''COUNTRY'' '
				|| '              THEN (SELECT cntry.name FROM country cntry WHERE cntry.countryid = udb.countryid) '
				|| '          WHEN ''ORG_UNIT'' '
				|| '              THEN (SELECT p.name || ''('' || p.fullname || '')'' FROM partner p inner join partnerindatabase pid on pid.partnerid = p.partnerid WHERE pid.databaseid = udb.databaseid) '
				|| '          WHEN ''OWNER'' '
				|| '              THEN (SELECT u.firstname || '' '' || u.name FROM userlogin u WHERE u.userid = udb.owneruserid) '
				|| '          WHEN ''MANAGER'' '
				|| '              THEN (SELECT u.firstname || '' '' || u.name FROM userlogin u WHERE u.userid = prj.id_manager) '
				|| '          WHEN ''START_DATE''  '
				|| '              THEN CAST( CAST( udb.startdate AS date ) AS text) '
				|| '          WHEN ''END_DATE''  '
				|| '              THEN CAST( CAST( prj.end_date AS date ) AS text) '
				|| '          WHEN ''BUDGET'' ' 
				|| '              THEN (SELECT budgarray.budgrow[2]'
				|| '                   FROM (SELECT string_to_array(unnest( string_to_array(v' || field_record.column_id || '.value, ''~'')),''%'') as budgrow) as budgarray '
				|| '                   WHERE CAST(budgarray.budgrow[1] AS bigint) = (SELECT id_ratio_dividend FROM budget_element WHERE id_flexible_element = ' || field_record.column_id || '))'
				|| '                   || '' / '' || '
				|| '                   (SELECT budgarray.budgrow[2]'
				|| '                   FROM (SELECT string_to_array(unnest( string_to_array(v' || field_record.column_id || '.value, ''~'')),''%'') as budgrow) as budgarray'
				|| '                   WHERE CAST(budgarray.budgrow[1] AS bigint) = (SELECT id_ratio_divisor FROM budget_element WHERE id_flexible_element = ' || field_record.column_id || '))'
				|| '          END  '
				|| ' WHEN ' || field_record.column_id || ' IN (SELECT id_question FROM question_choice_element) '
				|| '     THEN (SELECT qce.label FROM question_choice_element qce WHERE CAST(qce.id_choice AS text) = ANY(string_to_array(v' || field_record.column_id || '.value, ''~'')) )  '
				|| ' WHEN ' || field_record.column_id || ' IN (SELECT id_flexible_element FROM files_list_element)  '
				|| '     THEN NULL  '
				|| ' WHEN ' || field_record.column_id || ' IN (SELECT id_flexible_element FROM textarea_element WHERE type=''D'') THEN CAST( CAST( to_timestamp( TRUNC( CAST( v' || field_record.column_id || '.value AS bigint  ) / 1000 )) AS date) AS text) '
				|| ' ELSE v' || field_record.column_id || '.value END';
		END IF;
	END LOOP;

	-- From clause
	create_view_query := create_view_query || ' FROM userdatabase udb inner join project prj on (prj.databaseid = udb.databaseid) ';
	FOR field_record IN fields_cursor 
	LOOP
		IF (field_record.project_model = project_model_record.project_model)
		THEN
			create_view_query := create_view_query 
				|| ' left outer join value v' || field_record.column_id || ' on (v' || field_record.column_id || '.id_project = udb.databaseid and v' || field_record.column_id || '.id_flexible_element=' || field_record.column_id || ') ';
		END IF;
	END LOOP;

	-- Create View
	EXECUTE create_view_query;
		
	-- Add comment to MATERIALIZED VIEW columns
	IF (use_materialized_views and not drop_views)
	--IF (false) --IF clause commented used to disable comments because prevent column names to appear in PGAdmin III
	THEN
		FOR field_record IN fields_cursor 
		LOOP
			IF (field_record.project_model = project_model_record.project_model AND field_record.column_comment IS NOT NULL)
			THEN
				EXECUTE 'COMMENT ON COLUMN ' || field_record.project_model || '.' || field_record.column_name || ' IS ''' || field_record.column_comment || '''';
			END IF;
		END LOOP;
	END IF;
	
END LOOP;


RETURN true;
END;
$$ LANGUAGE plpgsql;


--
-- Export as CSV file the views created above
--
CREATE OR REPLACE FUNCTION exportcsv_sigmah_datamart_views(export_folder text, refresh_materialized_views boolean default false, view_name text default null) RETURNS BOOLEAN AS $$
DECLARE
	project_models_cursor CURSOR FOR select quote_ident(prjm.id_project_model || '-' || prjm.name) as project_model, prjm.id_project_model from project_model prjm;
	project_model_record RECORD;
BEGIN

IF(view_name IS NULL)
THEN
	FOR project_model_record IN project_models_cursor 
	LOOP
		
		IF (refresh_materialized_views)
		THEN
			EXECUTE 'REFRESH MATERIALIZED VIEW ' || project_model_record.project_model;
		END IF;

		EXECUTE 'COPY (SELECT * FROM ' || project_model_record.project_model || ') TO ''' || export_folder || '\project_model_' || project_model_record.id_project_model || '.csv' || ''' WITH CSV HEADER FORCE QUOTE *';
	END LOOP;
ELSE		
	IF (refresh_materialized_views)
	THEN
		EXECUTE 'REFRESH MATERIALIZED VIEW ' || view_name;
	END IF;
		
	EXECUTE 'COPY (SELECT * FROM ' || view_name || ') TO ''' || export_folder || '\sigmah_datamart_view_export.csv' || ''' WITH CSV HEADER FORCE QUOTE *';
END IF;

RETURN true;
END;
$$ LANGUAGE plpgsql;