CREATE TABLE project_team_members
(
  id_project integer NOT NULL,
  userid integer NOT NULL,
  CONSTRAINT fk_70qe4sufg4oab00hik1d550jq FOREIGN KEY (id_project)
      REFERENCES project (databaseid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_npm2g3bmepw61ye0vvce18614 FOREIGN KEY (userid)
      REFERENCES userlogin (userid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_aiug08fx4cx6p93ynvqlgp7xu UNIQUE (id_project, userid)
)
WITH (
  OIDS=FALSE
);


CREATE TABLE project_team_member_profiles
(
  id_project integer NOT NULL,
  id_profile integer NOT NULL,
  CONSTRAINT fk_9jyuxw6qomv6jou6tt5ogs4yq FOREIGN KEY (id_project)
      REFERENCES project (databaseid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ho3qygde3ttb969mpa5k1wn7k FOREIGN KEY (id_profile)
      REFERENCES profile (id_profile) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_asrruyupsb7jmfcgbrr9i8vn4 UNIQUE (id_project, id_profile)
)
WITH (
  OIDS=FALSE
);

INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'VIEW_MY_PROJECTS', p.id_profile FROM global_permission p WHERE p.permission='VIEW_PROJECT';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'VIEW_ALL_PROJECTS', p.id_profile FROM global_permission p WHERE p.permission='VIEW_PROJECT';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'EDIT_ALL_PROJECTS', p.id_profile FROM global_permission p WHERE p.permission='EDIT_PROJECT';
INSERT INTO global_permission (id_global_permission, permission, id_profile) SELECT nextval('hibernate_sequence'), 'EDIT_MY_PROJECTS', p.id_profile FROM global_permission p WHERE p.permission='EDIT_PROJECT';

DELETE FROM global_permission p WHERE p.permission='VIEW_PROJECT';
DELETE FROM global_permission p WHERE p.permission='EDIT_PROJECT';
