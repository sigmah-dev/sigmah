CREATE TABLE contact_model (
  id_contact_model INTEGER PRIMARY KEY,
  id_organization INTEGER NOT NULL
    REFERENCES organization(id_organization),
  type VARCHAR(255) NOT NULL,
  name VARCHAR(8192) NOT NULL,
  status VARCHAR(255) NOT NULL,
  date_deleted TIMESTAMP WITHOUT TIME ZONE,
  date_maintenance TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE contact_card (
  id_contact_card INTEGER PRIMARY KEY,
  id_contact_model INTEGER NOT NULL
    REFERENCES contact_model(id_contact_model)
    ON DELETE CASCADE,
  id_layout INTEGER REFERENCES layout(id_layout)
);

CREATE TABLE contact_details (
  id_contact_details INTEGER PRIMARY KEY,
  id_contact_model INTEGER NOT NULL
    REFERENCES contact_model(id_contact_model)
    ON DELETE CASCADE,
  id_layout INTEGER REFERENCES layout(id_layout)
);

CREATE TABLE default_contact_flexible_element (
  id_flexible_element INTEGER NOT NULL
    PRIMARY KEY
    REFERENCES flexible_element (id_flexible_element)
    ON DELETE CASCADE,
  type VARCHAR(255)
);