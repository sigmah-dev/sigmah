CREATE INDEX contact_fullname_idx ON contact USING gin(LOWER(name||' '||firstname) gin_trgm_ops);
CREATE INDEX contact_email_idx ON contact (LOWER(email));
CREATE INDEX userlogin_fullname_idx ON userlogin USING gin(LOWER(name||' '||firstname) gin_trgm_ops);
CREATE INDEX userlogin_email_idx ON userlogin (LOWER(email));
CREATE INDEX organization_name_idx ON organization USING gin(LOWER(name) gin_trgm_ops);
