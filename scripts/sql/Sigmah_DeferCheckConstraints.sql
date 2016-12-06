--
-- Defer all check constraints if database schema is v2.2 or newer
--
-- NB: only works for Postgres >= 9.2 because "NOT VALID" option only available since 9.2
-- 
--
CREATE OR REPLACE FUNCTION defer_checkconstraints() RETURNS INTEGER AS $$
DECLARE	
	v_result INTEGER;
BEGIN
	-- Check if schema contains 'contact' table: if yes, schema is v2.2 or higher, and can have its check constraints deferred
	SELECT count(*) INTO v_result FROM pg_tables WHERE tablename='contact';
	
	IF ( v_result = 1 )
	THEN
		ALTER TABLE contact DROP CONSTRAINT check_type_constraint;
		ALTER TABLE contact ADD CONSTRAINT check_type_constraint CHECK (contact_check_type(id_user, id_organization, id_contact_model)) NOT VALID;
		
		ALTER TABLE contact DROP CONSTRAINT is_parent_an_organization_constraint;
		ALTER TABLE contact ADD CONSTRAINT is_parent_an_organization_constraint CHECK (is_parent_an_organization(id_parent)) NOT VALID;
		
		ALTER TABLE framework_hierarchy DROP CONSTRAINT framework_hierarchy_level_check;
		ALTER TABLE framework_hierarchy ADD CONSTRAINT framework_hierarchy_level_check CHECK ((level >= 0)) NOT VALID;
		
		ALTER TABLE layout_group_iteration DROP CONSTRAINT does_layout_group_have_iterations_constraint;
		ALTER TABLE layout_group_iteration ADD CONSTRAINT does_layout_group_have_iterations_constraint CHECK (does_layout_group_have_iterations(id_layout_group)) NOT VALID;
		
	END IF;
	
	RETURN v_result;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION restore_checkconstraints() RETURNS INTEGER AS $$
DECLARE	
	v_result INTEGER;
BEGIN
	-- Check if schema contains 'contact' table: if yes, schema is v2.2 or higher, and can have its check constraints deferred
	SELECT count(*) INTO v_result FROM pg_tables WHERE tablename='contact';
	
	IF ( v_result = 1 )
	THEN
		ALTER TABLE contact DROP CONSTRAINT check_type_constraint;
		ALTER TABLE contact ADD CONSTRAINT check_type_constraint CHECK (contact_check_type(id_user, id_organization, id_contact_model));
		
		ALTER TABLE contact DROP CONSTRAINT is_parent_an_organization_constraint;
		ALTER TABLE contact ADD CONSTRAINT is_parent_an_organization_constraint CHECK (is_parent_an_organization(id_parent));
		
		ALTER TABLE framework_hierarchy DROP CONSTRAINT framework_hierarchy_level_check;
		ALTER TABLE framework_hierarchy ADD CONSTRAINT framework_hierarchy_level_check CHECK ((level >= 0));
		
		ALTER TABLE layout_group_iteration DROP CONSTRAINT does_layout_group_have_iterations_constraint;
		ALTER TABLE layout_group_iteration ADD CONSTRAINT does_layout_group_have_iterations_constraint CHECK (does_layout_group_have_iterations(id_layout_group));
		
	END IF;
	
	RETURN v_result;
END;
$$ LANGUAGE plpgsql;




	
