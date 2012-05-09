UPDATE org_unit_model SET id_organization=${ID_ORGANIZATION} WHERE org_unit_model_id=1;
UPDATE org_unit_model SET status='UNAVAILABLE' WHERE org_unit_model_id=1 AND 1!=${ID_ORGANIZATION};
UPDATE org_unit_model SET status='AVAILABLE' WHERE org_unit_model_id=1 AND 1=${ID_ORGANIZATION};
UPDATE profile SET id_organization=1 WHERE id_profile=1;
UPDATE privacy_group SET id_organization=1 WHERE id_privacy_group=1;

UPDATE quality_criterion SET id_organization=${ID_ORGANIZATION};
UPDATE quality_framework SET id_organization=${ID_ORGANIZATION};

DELETE FROM partnerindatabase WHERE databaseid=1;
DELETE FROM site WHERE siteid=1;
DELETE FROM partner WHERE partner.organization_id_organization!=1 AND parent_partnerid=1;