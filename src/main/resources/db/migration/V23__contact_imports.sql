ALTER TABLE importation_scheme_model ADD id_contact_model integer;
ALTER TABLE importation_scheme_model ADD CONSTRAINT importation_scheme_model_id_contact_model_fkey
  FOREIGN KEY (id_contact_model) REFERENCES contact_model(id_contact_model) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
