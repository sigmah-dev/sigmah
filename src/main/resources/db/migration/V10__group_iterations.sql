CREATE TABLE layout_group_iteration (
  id_layout_group_iteration integer NOT NULL,
  id_layout_group integer NOT NULL,
  id_container integer NOT NULL,
  name varchar(30) NOT NULL,
  CONSTRAINT layout_group_iteration_pkey PRIMARY KEY (id_layout_group_iteration),
  CONSTRAINT layout_group_iteration_id_layout_group_fkey FOREIGN KEY (id_layout_group)
    REFERENCES layout_group (id_layout_group) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION does_layout_group_have_iterations (p_id_layout_group integer) RETURNS boolean AS $$
  DECLARE
    p_has_iterations boolean;
  BEGIN
    SELECT has_iterations INTO p_has_iterations
    FROM layout_group
    WHERE id_layout_group = p_id_layout_group;

    RETURN p_has_iterations;
  END;
$$ LANGUAGE plpgsql;

ALTER TABLE layout_group_iteration
  ADD CONSTRAINT does_layout_group_have_iterations_constraint
  CHECK (does_layout_group_have_iterations(id_layout_group)) ;

ALTER TABLE layout_group ADD has_iterations boolean NOT NULL DEFAULT false;

ALTER TABLE value ADD id_layout_group_iteration integer;
ALTER TABLE value DROP CONSTRAINT uk_ev3lt5f4afcgkonu6exlm9be8;
ALTER TABLE value DROP CONSTRAINT value_id_flexible_element_key;
ALTER TABLE value ADD CONSTRAINT uk_ev3lt5f4afcgkonu6exlm9be8
  UNIQUE (id_flexible_element, id_project, id_layout_group_iteration);

ALTER TABLE history_token ADD id_layout_group_iteration integer;
