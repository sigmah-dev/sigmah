ALTER TABLE public.contact_list_element
    ADD COLUMN id_checkbox_element bigint;
ALTER TABLE public.contact_list_element
    ADD FOREIGN KEY (id_checkbox_element)
    REFERENCES public.checkbox_element (id_flexible_element) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
