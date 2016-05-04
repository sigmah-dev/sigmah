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

CREATE OR REPLACE FUNCTION contact_model_create_default_layout_constraint(p_type VARCHAR, p_id_layout_group INTEGER, p_id_contact_model INTEGER, p_sort_order INTEGER, p_recycle_flexible_element_from_details BOOLEAN) RETURNS INTEGER AS $$
    DECLARE
        p_id_flexible_element INTEGER;
        p_id_layout_constraint INTEGER;
    BEGIN
        p_id_layout_constraint := nextval('hibernate_sequence');

        IF p_recycle_flexible_element_from_details THEN
            -- Let's find the flexible element with the same type in the details layout
            -- Sharing the flexible element between card and details indicates to the application that a flexible
            -- element in the details layout appears in the contact card
            SELECT dcf.id_flexible_element INTO p_id_flexible_element
            FROM contact_details cd
            JOIN layout_group dlg ON (dlg.id_layout = cd.id_layout)
            JOIN layout_constraint dlc ON (dlc.id_layout_group = dlg.id_layout_group)
            JOIN default_contact_flexible_element dcf ON (dcf.id_flexible_element = dlc.id_flexible_element AND dcf.type = p_type)
            WHERE cd.id_contact_model = p_id_contact_model;
        ELSE
            p_id_flexible_element := nextval('hibernate_sequence');

            INSERT INTO flexible_element (id_flexible_element, amendable, validates, exportable, globally_exportable, creation_date)
            SELECT p_id_flexible_element, false, false, true, false, NOW();

            INSERT INTO default_contact_flexible_element (id_flexible_element, type)
            SELECT p_id_flexible_element, p_type;
        END IF;

        INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group)
        SELECT p_id_layout_constraint, p_sort_order, p_id_flexible_element, p_id_layout_group;

        RETURN p_id_layout_constraint;
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION contact_model_create_card_and_details(p_id_contact_model INTEGER) RETURNS VOID AS $$
    DECLARE
        p_id_contact_card INTEGER;
        p_id_contact_card_layout INTEGER;
        p_id_contact_photo_layout_group INTEGER;
        p_id_contact_card_layout_group INTEGER;
        p_id_contact_details INTEGER;
        p_id_contact_details_layout INTEGER;
        p_id_contact_details_layout_group INTEGER;
        p_type VARCHAR;
        p_counter INTEGER;
    BEGIN
        -- ContactDetails creation
        p_id_contact_details := nextval('hibernate_sequence');
        p_id_contact_details_layout := nextval('hibernate_sequence');
        p_id_contact_details_layout_group := nextval('hibernate_sequence');

        INSERT INTO layout (id_layout, columns_count, rows_count)
        SELECT p_id_contact_details_layout, 1, 1;

        INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout)
        SELECT p_id_contact_details_layout_group, 0, 0, 'Default details group', p_id_contact_details_layout;

        p_counter := 1;
        FOREACH p_type IN ARRAY ARRAY['FAMILY_NAME', 'FIRST_NAME', 'ORGANIZATION_NAME', 'MAIN_ORG_UNIT', 'SECONDARY_ORG_UNITS', 'CREATION_DATE', 'LOGIN', 'EMAIL_ADDRESS', 'PHONE_NUMBER', 'POSTAL_ADDRESS', 'PHOTO', 'COUNTRY', 'DIRECT_MEMBERSHIP', 'TOP_MEMBERSHIP']
        LOOP
            PERFORM contact_model_create_default_layout_constraint(p_type, p_id_contact_details_layout_group, p_id_contact_model, p_counter, false);
            p_counter := p_counter + 1;
        END LOOP;

        INSERT INTO contact_details (id_contact_details, id_contact_model, id_layout)
        SELECT p_id_contact_details, p_id_contact_model, p_id_contact_details_layout;

        -- ContactCard creation
        p_id_contact_card := nextval('hibernate_sequence');
        p_id_contact_card_layout := nextval('hibernate_sequence');

        INSERT INTO layout (id_layout, columns_count, rows_count)
        SELECT p_id_contact_card_layout, 2, 1;

        -- Photo group
        p_id_contact_photo_layout_group := nextval('hibernate_sequence');

        INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout)
        SELECT p_id_contact_photo_layout_group, 0, 0, 'Avatar group', p_id_contact_card_layout;

        PERFORM contact_model_create_default_layout_constraint('PHOTO', p_id_contact_photo_layout_group, p_id_contact_model, 0, true);

        -- Card information group
        p_id_contact_card_layout_group := nextval('hibernate_sequence');

        INSERT INTO layout_group (id_layout_group, column_index, row_index, title, id_layout)
        SELECT p_id_contact_card_layout_group, 0, 1, 'Default card group', p_id_contact_card_layout;

        PERFORM contact_model_create_default_layout_constraint('FAMILY_NAME', p_id_contact_card_layout_group, p_id_contact_model, 1, true);
        PERFORM contact_model_create_default_layout_constraint('ORGANIZATION_NAME', p_id_contact_card_layout_group, p_id_contact_model, 1, true);
        PERFORM contact_model_create_default_layout_constraint('FIRST_NAME', p_id_contact_card_layout_group, p_id_contact_model, 2, true);
        PERFORM contact_model_create_default_layout_constraint('TOP_MEMBERSHIP', p_id_contact_card_layout_group, p_id_contact_model, 3, true);
        PERFORM contact_model_create_default_layout_constraint('EMAIL_ADDRESS', p_id_contact_card_layout_group, p_id_contact_model, 4, true);
        PERFORM contact_model_create_default_layout_constraint('PHONE_NUMBER', p_id_contact_card_layout_group, p_id_contact_model, 5, true);
        PERFORM contact_model_create_default_layout_constraint('POSTAL_ADDRESS', p_id_contact_card_layout_group, p_id_contact_model, 6, true);
        PERFORM contact_model_create_default_layout_constraint('COUNTRY', p_id_contact_card_layout_group, p_id_contact_model, 7, true);

        INSERT INTO contact_card (id_contact_card, id_contact_model, id_layout)
        SELECT p_id_contact_card, p_id_contact_model, p_id_contact_card_layout;
    END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION contact_model_create(p_id_organization INTEGER, p_type VARCHAR, p_name VARCHAR) RETURNS INTEGER AS $$
    DECLARE
        p_id_contact_model INTEGER;
    BEGIN
        p_id_contact_model := nextval('hibernate_sequence');

        INSERT INTO contact_model (id_contact_model, name, status, type, id_organization)
        SELECT p_id_contact_model, p_name, 'USED', p_type, p_id_organization;

        PERFORM contact_model_create_card_and_details(p_id_contact_model);

        RETURN p_id_contact_model;
    END;
$$ LANGUAGE plpgsql;

-- Insert contact models
SELECT
  contact_model_create(o.id_organization, 'ORGANIZATION', o.name || ' model'),
  contact_model_create(o.id_organization, 'INDIVIDUAL', 'Sigmah user model')
FROM organization o;
