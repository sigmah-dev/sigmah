ALTER TABLE user_unit ADD COLUMN user_unit_type varchar(32);
UPDATE user_unit SET user_unit_type = 'MAIN';
