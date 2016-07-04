CREATE TABLE iteration_history_token
(
  id_iteration_history_token integer NOT NULL,
  history_date timestamp without time zone NOT NULL,
  id_layout_group_iteration integer NOT NULL,
  id_layout_group integer NOT NULL,
  id_project integer NOT NULL,
  name varchar(30) NOT NULL,
  core_version integer,
  CONSTRAINT iteration_history_token_pkey PRIMARY KEY (id_iteration_history_token),
  CONSTRAINT fk_iteration_history_token_core_version FOREIGN KEY (core_version)
      REFERENCES amendment (id_amendment) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_iteration_history_token_layout_group FOREIGN KEY (id_layout_group)
      REFERENCES layout_group (id_layout_group) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
