CREATE TABLE framework
(
  id_framework integer NOT NULL,
  availability_status character varying(255) NOT NULL,
  implementation_status character varying(255) NOT NULL,
  label character varying(255) NOT NULL,
  id_organization integer,
  CONSTRAINT framework_pkey PRIMARY KEY (id_framework),
  CONSTRAINT fk_3l3o8bnug9ymilr8irjecxfxy FOREIGN KEY (id_organization)
      REFERENCES organization (id_organization) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE framework_fulfillment
(
  id_framework_fulfillment integer NOT NULL,
  reject_reason character varying(255),
  id_framework integer NOT NULL,
  id_project_model integer NOT NULL,
  CONSTRAINT framework_fulfillment_pkey PRIMARY KEY (id_framework_fulfillment),
  CONSTRAINT fk_hn13s8iybgi1vdo47xkqqd0b7 FOREIGN KEY (id_framework)
      REFERENCES framework (id_framework) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_rs0vcqcllktrd5d12o1blowqe FOREIGN KEY (id_project_model)
      REFERENCES project_model (id_project_model) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT uk_rs0vcqcllktrd5d12o1blowqe UNIQUE (id_project_model, id_framework)
);

CREATE TABLE framework_hierarchy
(
  id_framework_hierarchy integer NOT NULL,
  label character varying(255) NOT NULL,
  level integer NOT NULL,
  id_framework integer NOT NULL,
  parent_hierarchy integer, -- XXX: Useless if the hierarchy is not a tree
  CONSTRAINT framework_hierarchy_pkey PRIMARY KEY (id_framework_hierarchy),
  CONSTRAINT fk_6ap8cn4cxh5vj7163tyvlk03b FOREIGN KEY (parent_hierarchy)
      REFERENCES framework_hierarchy (id_framework_hierarchy) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_g7s48h8iyf2po13qyi6sd5qtb FOREIGN KEY (id_framework)
      REFERENCES framework (id_framework) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT framework_hierarchy_level_check CHECK (level >= 0)
);

CREATE TABLE framework_element
(
  id_framework_element integer NOT NULL,
  data_type character varying(255) NOT NULL,
  label character varying(255) NOT NULL,
  value_rule character varying(255) NOT NULL,
  id_framework_hierarchy integer NOT NULL,
  CONSTRAINT framework_element_pkey PRIMARY KEY (id_framework_element),
  CONSTRAINT fk_j2vqfnb56b43qyrxp0cuoyd60 FOREIGN KEY (id_framework_hierarchy)
      REFERENCES framework_hierarchy (id_framework_hierarchy) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE framework_element_implementation
(
  id_framework_element_implementation integer NOT NULL,
  id_flexible_element integer NOT NULL,
  id_framework_element integer NOT NULL,
  id_framework_fulfillment integer NOT NULL,
  CONSTRAINT framework_element_implementation_pkey PRIMARY KEY (id_framework_element_implementation),
  CONSTRAINT fk_1q2w7ryycg4e220id3s69kdgu FOREIGN KEY (id_framework_element)
      REFERENCES framework_element (id_framework_element) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_haj6o47cyolg7xjob4uymvndw FOREIGN KEY (id_flexible_element)
      REFERENCES flexible_element (id_flexible_element) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_mq0iewhji722rna3m050mwmfj FOREIGN KEY (id_framework_fulfillment)
      REFERENCES framework_fulfillment (id_framework_fulfillment) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT uk_mq0iewhji722rna3m050mwmfj UNIQUE (id_flexible_element, id_framework_element)
);
