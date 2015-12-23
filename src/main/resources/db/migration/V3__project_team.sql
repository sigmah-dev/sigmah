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
