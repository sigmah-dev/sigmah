CREATE TABLE global_contact_export
(
  id bigint NOT NULL,
  generated_date timestamp without time zone NOT NULL,
  organization_id integer NOT NULL,
  CONSTRAINT global_contact_export_pkey PRIMARY KEY (id),
  CONSTRAINT global_contact_export_organization_fkey FOREIGN KEY (organization_id)
      REFERENCES organization (id_organization) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE global_contact_export_content
(
  id bigint NOT NULL,
  csv_content text,
  contact_model_name character varying(8192) NOT NULL,
  global_export_id bigint NOT NULL,
  CONSTRAINT global_contact_export_content_pkey PRIMARY KEY (id),
  CONSTRAINT global_contact_export_content_global_export_fkey FOREIGN KEY (global_export_id)
      REFERENCES global_contact_export (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE global_contact_export_settings
(
  id bigint NOT NULL,
  auto_delete_frequency integer,
  auto_export_frequency integer,
  default_organization_export_format character varying(255),
  export_format character varying(255),
  last_export_date timestamp without time zone,
  locale_string character varying(4) NOT NULL,
  organization_id integer NOT NULL,
  CONSTRAINT global_contact_export_settings_pkey PRIMARY KEY (id),
  CONSTRAINT global_contact_export_settings_organization_fkey FOREIGN KEY (organization_id)
      REFERENCES organization (id_organization) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
