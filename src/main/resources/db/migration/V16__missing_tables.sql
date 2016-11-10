CREATE TABLE project_model_default_team_member_profiles
(
  id_project_model integer NOT NULL,
  id_profile integer NOT NULL,
  CONSTRAINT fk_ajm59jjhow30eg599t8b0j6q3 FOREIGN KEY (id_project_model)
    REFERENCES project_model (id_project_model) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_g3tko0lysn2obww1uqlil1rpv FOREIGN KEY (id_profile)
    REFERENCES profile (id_profile) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_807c5vq0ec0if8hfkyj321esw UNIQUE (id_project_model, id_profile)
);

CREATE TABLE contact_list_element_allowed_model
(
  id_flexible_element integer NOT NULL,
  id_contact_model integer NOT NULL,
  CONSTRAINT fk_5qa1sd88f5nv931x5ggoqsur7 FOREIGN KEY (id_contact_model)
    REFERENCES contact_model (id_contact_model) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_bj6vsf9ar64b8t88axx6y53fw FOREIGN KEY (id_flexible_element)
    REFERENCES contact_list_element (id_flexible_element) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_qfg7qfdhextm6lj18iuoerrph UNIQUE (id_flexible_element, id_contact_model)
);
