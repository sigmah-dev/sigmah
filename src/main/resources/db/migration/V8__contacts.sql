CREATE TABLE contact (
  id_contact INTEGER PRIMARY KEY,
  id_contact_model INTEGER NOT NULL REFERENCES contact_model(id_contact_model),
  id_user INTEGER REFERENCES userlogin(userid),
  id_organization INTEGER REFERENCES organization(id_organization),
  name VARCHAR,
  firstname VARCHAR,
  id_main_org_unit INTEGER REFERENCES partner(partnerid),
  login VARCHAR,
  email VARCHAR,
  phone_number VARCHAR,
  postal_address VARCHAR,
  photo VARCHAR,
  id_country INTEGER REFERENCES country(countryid),
  id_parent INTEGER REFERENCES contact(id_contact),
  date_created TIMESTAMP WITHOUT TIME ZONE,
  date_deleted TIMESTAMP WITHOUT TIME ZONE
);

CREATE OR REPLACE FUNCTION contact_check_type (p_id_user INTEGER, p_id_organization INTEGER, p_id_contact_model INTEGER) RETURNS BOOLEAN AS $$
    DECLARE
        p_valid BOOLEAN;
    BEGIN
        IF p_id_user IS NOT NULL AND p_id_organization IS NOT NULL THEN
            -- This contact is both related to a user and an organization
            -- This is not valid
            p_valid = false;
        ELSIF p_id_user IS NOT NULL THEN
            -- The related model should be an 'INDIVIDUAL' model
            SELECT type = 'INDIVIDUAL' INTO p_valid
            FROM contact_model
            WHERE id_contact_model = p_id_contact_model;
        ELSIF p_id_organization IS NOT NULL THEN
            -- The related model should be an 'ORGANIZATION' model
            SELECT type = 'ORGANIZATION' INTO p_valid
            FROM contact_model
            WHERE id_contact_model = p_id_contact_model;
        ELSE
            -- Not related to a user or an organization
            -- the type of the related model can either be 'INDIVIDUAL' or 'ORGANIZATION'
            p_valid = true;
        END IF;

        RETURN p_valid;
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION is_parent_an_organization (p_id_contact INTEGER) RETURNS BOOLEAN AS $$
    SELECT cm.id_contact_model IS NOT NULL
    FROM contact c
    LEFT JOIN contact_model cm ON (c.id_contact_model = c.id_contact_model AND cm.type = 'ORGANIZATION')
    WHERE c.id_contact = p_id_contact;
$$ LANGUAGE SQL;

ALTER TABLE contact
  ADD CONSTRAINT is_parent_an_organization_constraint
  CHECK (is_parent_an_organization(id_parent)) ;

ALTER TABLE contact
  ADD CONSTRAINT check_type_constraint
  CHECK (contact_check_type(id_user, id_organization, id_contact_model));

CREATE TABLE contact_unit (
  id_contact INTEGER NOT NULL REFERENCES contact(id_contact),
  id_org_unit INTEGER NOT NULL REFERENCES partner(partnerid),
  PRIMARY KEY (id_contact, id_org_unit)
);

-- Let's create a contact for each user and for each organization
-- Do not add user | organization data in contact table as these data will be retrieved from user | organization table when required

INSERT INTO contact (id_contact, id_contact_model, id_organization, date_created)
SELECT nextval('hibernate_sequence'), cm.id_contact_model, o.id_organization, NOW()
FROM organization o
JOIN contact_model cm ON (cm.id_organization = o.id_organization AND cm.name = o.name || ' model');

INSERT INTO contact (id_contact, id_contact_model, id_user, id_parent, date_created)
SELECT nextval('hibernate_sequence'), cm.id_contact_model, u.userid, parent.id_contact, NOW()
FROM userlogin u
JOIN contact_model cm ON (cm.id_organization = u.id_organization AND cm.name = 'Sigmah user model')
JOIN contact parent ON (parent.id_organization = u.id_organization);