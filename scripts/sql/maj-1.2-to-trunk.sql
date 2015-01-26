-- Adds tables for managing reminders and monitored points history  (issue #550)
-- ------

-- POSTGRESQL

CREATE TABLE reminder_history (
        id_reminder_history integer PRIMARY KEY,
        generated_date timestamp without time zone NOT NULL,
        id_reminder integer REFERENCES reminder NOT NULL,
        id_user integer NOT NULL,
        value text,
        change_type character varying(255) NOT NULL
);

CREATE TABLE monitored_point_history (
        id_monitored_point_history integer PRIMARY KEY,
        generated_date timestamp without time zone NOT NULL,
        id_monitored_point integer REFERENCES monitored_point NOT NULL,
        id_user integer NOT NULL,
        value text,
        change_type character varying(255) NOT NULL
);


-- Adds tables for managing the flexibility of budget element and sets by default the planned and spent budget  (issue #386)
-- ------

-- POSTGRESQL

CREATE TABLE budget_element
(
  id_flexible_element bigint NOT NULL,
  id_ratio_divisor bigint,
  id_ratio_dividend bigint,
  CONSTRAINT budget_element_pkey PRIMARY KEY (id_flexible_element),
  CONSTRAINT fk1ba06002a82c370 FOREIGN KEY (id_flexible_element)
      REFERENCES default_flexible_element (id_flexible_element) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE budget_sub_field
(
  id_budget_sub_field bigint NOT NULL,
  label character varying(255),
  id_budget_element bigint NOT NULL,
  fieldorder integer,
  type character varying(255),
  CONSTRAINT budget_sub_field_pkey PRIMARY KEY (id_budget_sub_field),
  CONSTRAINT fkc12629c1a251b09 FOREIGN KEY (id_budget_element)
      REFERENCES budget_element (id_flexible_element) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE budget_element ADD  CONSTRAINT fk1ba0600222f4f59 FOREIGN KEY (id_ratio_divisor)
      REFERENCES budget_sub_field (id_budget_sub_field) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
          
ALTER TABLE budget_element ADD  CONSTRAINT fk1ba06002a2a3285a FOREIGN KEY (id_ratio_dividend)
      REFERENCES budget_sub_field (id_budget_sub_field) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
          

INSERT INTO budget_element (id_flexible_element)
        SELECT id_flexible_element FROM default_flexible_element WHERE default_flexible_element.type = 'BUDGET' AND default_flexible_element.id_flexible_element NOT IN (SELECT budget_element.id_flexible_element FROM budget_element );
INSERT INTO budget_sub_field (id_budget_sub_field,label, id_budget_element, fieldorder, type) 
                        SELECT nextval('hibernate_sequence'), NULL, budget_element.id_flexible_element, 0, 'RECEIVED' FROM  budget_element;
INSERT INTO budget_sub_field (id_budget_sub_field,label, id_budget_element, fieldorder, type) 
                        SELECT nextval('hibernate_sequence'), NULL, budget_element.id_flexible_element, 0, 'SPENT' FROM  budget_element;
INSERT INTO budget_sub_field (id_budget_sub_field,label, id_budget_element, fieldorder, type) 
                        SELECT nextval('hibernate_sequence'), NULL, budget_element.id_flexible_element, 0, 'PLANNED' FROM  budget_element;
        UPDATE budget_element  SET id_ratio_divisor = bsf.id_budget_sub_field FROM budget_sub_field bsf WHERE  id_flexible_element =  bsf.id_budget_element AND bsf.type = 'PLANNED';
        UPDATE budget_element  SET id_ratio_dividend = bsf.id_budget_sub_field FROM budget_sub_field bsf WHERE  id_flexible_element =  bsf.id_budget_element AND bsf.type = 'SPENT';




-- Updates history_token and value to table to fit the new structure of a budget element (issue #386)
-- ------

-- POSTGRESQL

UPDATE history_token SET value = planned_budget_id || '%' || coalesce(budgetValues[1], '0.0') || '~' || received_budget_id || '%' || coalesce(budgetValues[2], '0.0')  || '~' || spent_budget_id || '%' ||  coalesce(budgetValues[3], '0.0') 
FROM (
        SELECT value, string_to_array(value, '~') AS budgetValues, flexible_element.id_flexible_element, id_history_token,
                planned.id_budget_sub_field  AS planned_budget_id,
                spent.id_budget_sub_field    AS spent_budget_id,
                received.id_budget_sub_field AS received_budget_id
        FROM history_token AS ht
        INNER JOIN flexible_element ON ( flexible_element.id_flexible_element = ht.id_element )
        LEFT  JOIN budget_sub_field AS planned  ON ( planned.id_budget_element = flexible_element.id_flexible_element  AND planned.type = 'PLANNED' )
        LEFT  JOIN budget_sub_field AS spent    ON ( spent.id_budget_element = flexible_element.id_flexible_element    AND spent.type = 'SPENT' )
        LEFT  JOIN budget_sub_field AS received ON ( received.id_budget_element = flexible_element.id_flexible_element AND received.type = 'RECEIVED' )
        WHERE flexible_element.id_flexible_element IN (Select id_flexible_element from budget_element)
) AS ssrequete
WHERE history_token.id_history_token = ssrequete.id_history_token;


INSERT INTO value (id_value, id_project, action_last_modif, date_last_modif, value, id_flexible_element, id_user_last_modif)
SELECT nextval('hibernate_sequence'), ht.id_project, CASE change_type WHEN 'EDIT' THEN 'U' ELSE 'C' END, history_date, value, ht.id_element, id_user
FROM history_token AS ht JOIN ( SELECT history_token.id_project, history_token.id_element, MAX (id_history_token) AS max_history_id FROM history_token GROUP BY id_project, id_element) AS requete
  ON requete.id_project = ht.id_project AND requete.id_element = ht.id_element AND requete.max_history_id = ht.id_history_token
WHERE (ht.id_project, ht.id_element) NOT IN (SELECT id_project, id_flexible_element FROM value) AND ht.id_element IN (SELECT id_flexible_element FROM budget_element);


-- add importation sheme table