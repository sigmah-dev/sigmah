UPDATE history_token SET value='' WHERE value IS NULL;
ALTER TABLE history_token ALTER COLUMN value SET NOT NULL;

UPDATE org_unit_model SET title='' WHERE title IS NULL;
ALTER TABLE org_unit_model ALTER COLUMN title SET NOT NULL;

UPDATE question_choice_element SET label='' WHERE label IS NULL;
ALTER TABLE question_choice_element ALTER COLUMN label SET NOT NULL;

DROP SEQUENCE IF EXISTS sort_order_sequence CASCADE;

UPDATE privacy_group SET id_organization=NULL WHERE id_privacy_group=1;