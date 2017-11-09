ALTER TABLE contact_list_element
    ADD COLUMN id_checkbox_element bigint;
ALTER TABLE contact_list_element
    ADD FOREIGN KEY (id_checkbox_element)
    REFERENCES checkbox_element (id_flexible_element) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
