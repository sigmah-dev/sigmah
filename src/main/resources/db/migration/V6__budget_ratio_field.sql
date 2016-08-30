--
-- Database update for budget ratio elements.
-- Author: Cihan Yagan (cihan.yagan@netapsys.fr)
-- Update date: 2 august 2016
--
CREATE TABLE budget_ratio_element
(
  id_flexible_element bigint NOT NULL,
  id_spent_field bigint,
  id_planned_field bigint,
  CONSTRAINT budget_ratio_element_pkey PRIMARY KEY (id_flexible_element),
  CONSTRAINT budget_ratio_element_fkey1 FOREIGN KEY (id_spent_field)
      REFERENCES flexible_element (id_flexible_element) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT budget_ratio_element_fkey2 FOREIGN KEY (id_planned_field)
      REFERENCES flexible_element (id_flexible_element) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE budget_ratio_element OWNER TO sigmah;