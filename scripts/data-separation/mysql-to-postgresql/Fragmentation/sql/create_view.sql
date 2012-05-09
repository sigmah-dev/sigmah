CREATE OR REPLACE VIEW organization_view AS 
SELECT DISTINCT * 
FROM organization 
WHERE id_organization=${ID_ORGANIZATION};

CREATE OR REPLACE VIEW privacy_group_view AS
SELECT DISTINCT pg.*
FROM privacy_group pg, organization_view o
WHERE pg.id_organization=o.id_organization
ORDER BY pg.id_privacy_group;

CREATE OR REPLACE VIEW org_unit_model_view AS
SELECT DISTINCT oum.org_unit_model_id, 
oum.can_contain_projects,
oum.has_budget,
oum.name,
oum.title,
oum.status,
oum.id_organization
FROM org_unit_model oum
WHERE oum.id_organization IN (SELECT id_organization FROM organization_view)
OR oum.org_unit_model_id IN (SELECT p.id_org_unit_model FROM partner p, organization_view o WHERE p.organization_id_organization=o.id_organization)
ORDER BY oum.org_unit_model_id;

CREATE OR REPLACE VIEW org_unit_banner_view AS
SELECT DISTINCT oub.*
FROM org_unit_banner oub
WHERE oub.id_org_unit_model IN (SELECT org_unit_model_id FROM org_unit_model_view)
ORDER BY oub.banner_id;

CREATE OR REPLACE VIEW org_unit_details_view AS
SELECT DISTINCT oud.*
FROM org_unit_details oud
WHERE oud.id_org_unit_model IN (SELECT org_unit_model_id FROM org_unit_model_view)
ORDER BY oud.details_id;

CREATE OR REPLACE VIEW locationtype_view AS
SELECT DISTINCT lt.*
FROM locationtype lt
ORDER BY lt.locationtypeid;

CREATE OR REPLACE VIEW adminlevel_view AS
SELECT DISTINCT a.*
FROM adminlevel a
ORDER BY a.adminlevelid;

CREATE OR REPLACE VIEW country_view AS
SELECT DISTINCT c.*
FROM country c
ORDER BY c.countryid;

CREATE OR REPLACE VIEW quality_framework_view AS
SELECT DISTINCT qcf.*
FROM quality_framework qcf, organization_view o
WHERE qcf.id_organization=o.id_organization
ORDER BY qcf.id_quality_framework;

CREATE OR REPLACE VIEW quality_criterion_view AS
SELECT DISTINCT qc.*
FROM quality_criterion qc, quality_framework_view qf
WHERE qc.id_quality_framework=qf.id_quality_framework
ORDER BY qc.id_quality_criterion;

CREATE OR REPLACE VIEW quality_criterion_type_view AS
SELECT DISTINCT qct.*
FROM quality_criterion_type qct, quality_framework_view qf
WHERE qct.id_quality_framework=qf.id_quality_framework
ORDER BY qct.id_criterion_type;

CREATE OR REPLACE VIEW quality_criterion_children_view AS
SELECT DISTINCT qcc.*
FROM quality_criterion_children qcc
WHERE qcc.id_quality_criterion in (SELECT id_quality_criterion FROM quality_criterion_view)
OR qcc.id_quality_criterion in (SELECT id_quality_criterion FROM quality_criterion_view);

CREATE OR REPLACE VIEW keyquestion_view AS
SELECT DISTINCT kq.*
FROM keyquestion kq, quality_criterion_view qc
WHERE kq.qualitycriterion_id_quality_criterion=qc.id_quality_criterion
ORDER BY kq.id;

CREATE OR REPLACE VIEW userlogin_view AS
SELECT DISTINCT ul.*
FROM userlogin ul, organization_view o
WHERE ul.id_organization=o.id_organization
AND ul.userid > -1
ORDER BY ul.userid;

CREATE OR REPLACE VIEW userdatabase_view AS
SELECT DISTINCT ud.*
FROM userdatabase ud, userlogin_view ul
WHERE ud.owneruserid=ul.userid
ORDER BY ud.databaseid;

CREATE OR REPLACE VIEW partner_view AS
SELECT DISTINCT p.*
FROM partner p
WHERE (p.organization_id_organization IN (SELECT id_organization FROM organization_view)
OR (p.id_org_unit_model IN (SELECT org_unit_model_id FROM org_unit_model_view)
AND (p.organization_id_organization IN (SELECT id_organization FROM organization_view)
OR p.organization_id_organization IS NULL))
OR p.partnerid in (SELECT partnerid FROM partnerindatabase WHERE databaseid IN (SELECT databaseid FROM userdatabase_view)))
AND (p.parent_partnerid IN (SELECT partnerid FROM partner)
OR p.parent_partnerid IS NULL)
ORDER BY p.partnerid;

CREATE OR REPLACE VIEW project_userlogin_view AS
SELECT DISTINCT pul.*
FROM project_userlogin pul, userlogin_view ul
WHERE pul.favoriteusers_userid=ul.userid;

CREATE OR REPLACE VIEW project_view AS
SELECT DISTINCT p.*
FROM project p, userlogin_view ul
WHERE p.id_manager=ul.userid
AND p.id_manager > -1
AND p.databaseid > -1
ORDER BY p.databaseid;

CREATE OR REPLACE VIEW project_funding_view AS
SELECT DISTINCT pf.*
FROM project_funding pf
WHERE pf.id_project_funded IN (SELECT databaseid FROM project_view)
AND pf.id_project_funding IN (SELECT databaseid FROM project_view)
ORDER BY pf.id_funding;

CREATE OR REPLACE VIEW project_model_visibility_view AS
SELECT DISTINCT pmv.*
FROM project_model_visibility pmv, organization_view o
WHERE pmv.id_organization=o.id_organization
ORDER BY pmv.id_visibility;

CREATE OR REPLACE VIEW project_model_view AS
SELECT DISTINCT pm.*
FROM project_model pm
WHERE pm.id_project_model IN (SELECT id_project_model FROM project_model_visibility_view)
OR pm.id_project_model IN (SELECT id_project_model FROM project_view)
ORDER BY pm.id_project_model;

CREATE OR REPLACE VIEW phase_view AS
SELECT DISTINCT ph.*
FROM phase ph, project_view pr
WHERE ph.id_project=pr.databaseid
AND ph.id_phase > -1
ORDER BY ph.id_phase;

CREATE OR REPLACE VIEW phase_model_view AS
SELECT DISTINCT phm.*
FROM phase_model phm
WHERE phm.id_phase_model IN (SELECT id_phase_model FROM phase_view)
OR phm.id_project_model IN (SELECT id_project_model FROM project_model_view)
ORDER BY phm.id_phase_model;

CREATE OR REPLACE VIEW phase_model_sucessors_view AS
SELECT DISTINCT pms.*
FROM phase_model_sucessors pms
WHERE pms.id_phase_model IN (SELECT id_phase_model FROM phase_model_view)
OR pms.id_phase_model_successor IN (SELECT id_phase_model FROM phase_model_view);

CREATE OR REPLACE VIEW project_details_view AS
SELECT DISTINCT pd.*
FROM project_details pd, project_model_view pm
WHERE pm.id_project_model=pd.id_project_model
ORDER BY pd.id;

CREATE OR REPLACE VIEW project_banner_view AS
SELECT DISTINCT pb.*
FROM project_banner pb, project_model_view pm
WHERE pm.id_project_model=pb.id_project_model
ORDER BY pb.id;

CREATE OR REPLACE VIEW layout_view AS
SELECT DISTINCT l.*
FROM layout l
WHERE l.id_layout IN (SELECT id_layout FROM project_details_view)
OR l.id_layout IN (SELECT id_layout FROM project_banner_view)
OR l.id_layout IN (SELECT id_layout FROM phase_model_view)
OR l.id_layout IN (SELECT id_layout FROM org_unit_banner_view)
OR l.id_layout IN (SELECT id_layout FROM org_unit_details_view)
ORDER BY l.id_layout;

CREATE OR REPLACE VIEW layout_group_view AS
SELECT DISTINCT lg.*
FROM layout_group lg
WHERE lg.id_layout IN (SELECT id_layout FROM layout_view)
ORDER BY lg.id_layout_group;

CREATE OR REPLACE VIEW layout_constraint_view AS
SELECT DISTINCT lc.*
FROM layout_constraint lc, layout_group_view lg
WHERE lc.id_layout_group=lg.id_layout_group
ORDER BY id_layout_constraint;

CREATE OR REPLACE VIEW flexible_element_view AS
SELECT DISTINCT fe.*
FROM flexible_element fe
WHERE fe.id_privacy_group IN (SELECT id_privacy_group FROM privacy_group_view)
OR fe.id_flexible_element IN (SELECT id_flexible_element FROM layout_constraint_view)
ORDER BY fe.id_flexible_element;

CREATE OR REPLACE VIEW checkbox_element_view AS
SELECT DISTINCT ce.*
FROM checkbox_element ce, flexible_element_view fe
WHERE ce.id_flexible_element=fe.id_flexible_element
ORDER BY ce.id_flexible_element;

CREATE OR REPLACE VIEW default_flexible_element_view AS
SELECT DISTINCT dfe.*
FROM default_flexible_element dfe, flexible_element_view fe
WHERE dfe.id_flexible_element=fe.id_flexible_element
ORDER BY dfe.id_flexible_element;

CREATE OR REPLACE VIEW files_list_element_view AS
SELECT DISTINCT fle.*
FROM files_list_element fle, flexible_element_view fe
WHERE fle.id_flexible_element=fe.id_flexible_element
ORDER BY fle.id_flexible_element;

CREATE OR REPLACE VIEW report_element_view AS
SELECT DISTINCT re.*
FROM report_element re, flexible_element_view fe
WHERE re.id_flexible_element=fe.id_flexible_element
ORDER BY re.id_flexible_element;

CREATE OR REPLACE VIEW report_list_element_view AS
SELECT DISTINCT rle.*
FROM report_list_element rle, flexible_element_view fe
WHERE rle.id_flexible_element=fe.id_flexible_element
ORDER BY rle.id_flexible_element;

CREATE OR REPLACE VIEW triplets_list_element_view AS
SELECT DISTINCT tle.*
FROM triplets_list_element tle, flexible_element_view fe
WHERE tle.id_flexible_element=fe.id_flexible_element
ORDER BY tle.id_flexible_element;

CREATE OR REPLACE VIEW indicators_list_element_view AS
SELECT DISTINCT ile.*
FROM indicators_list_element ile, flexible_element_view fe
WHERE ile.id_flexible_element=fe.id_flexible_element
ORDER BY ile.id_flexible_element;

CREATE OR REPLACE VIEW question_element_view AS
SELECT DISTINCT qe.*
FROM question_element qe, flexible_element_view fe
WHERE qe.id_flexible_element=fe.id_flexible_element
ORDER BY qe.id_flexible_element;

CREATE OR REPLACE VIEW question_choice_element_view AS
SELECT DISTINCT qce.*
FROM question_choice_element qce, question_element_view qe
WHERE qce.id_question=qe.id_flexible_element
ORDER BY qce.id_choice;

CREATE OR REPLACE VIEW message_element_view AS
SELECT DISTINCT me.*
FROM message_element me, flexible_element_view fe
WHERE me.id_flexible_element=fe.id_flexible_element
ORDER BY me.id_flexible_element;

CREATE OR REPLACE VIEW budget_distribution_element_view AS
SELECT DISTINCT bde.*
FROM budget_distribution_element bde, flexible_element_view fe
WHERE bde.id_flexible_element=fe.id_flexible_element
ORDER BY bde.id_flexible_element;

CREATE OR REPLACE VIEW textarea_element_view AS
SELECT DISTINCT te.*
FROM textarea_element te, flexible_element_view fe
WHERE te.id_flexible_element=fe.id_flexible_element
ORDER BY te.id_flexible_element;

CREATE OR REPLACE VIEW indicator_view AS
SELECT DISTINCT i.`IndicatorId`
, i.`Aggregation`
, i.`Category`
, i.`CollectIntervention`
, i.`CollectMonitoring`
, i.`dateDeleted`
, i.`description`
, i.`ListHeader`
, i.`Name`
, i.`SortOrder`
, i.`Units`
, i.`ActivityId`
, i.`id_quality_criterion`
, i.`Objective`
, i.`DatabaseId`
, i.`sourceOfVerification`
, i.`directdataentryenabled`
FROM indicator i, userdatabase_view ud
WHERE ud.databaseid=i.databaseid
ORDER BY i.indicatorid;

CREATE OR REPLACE VIEW indicator_datasource_view AS
SELECT DISTINCT id.*
FROM indicator_datasource id, indicator_view i
WHERE id.indicatorid=i.indicatorid;

CREATE OR REPLACE VIEW indicator_labels_view AS
SELECT DISTINCT il.*
FROM indicator_labels il, indicator_view i
WHERE il.indicator_indicatorid=i.indicatorid
ORDER BY il.indicator_indicatorid;

CREATE OR REPLACE VIEW indicators_list_value_view AS
SELECT DISTINCT ilv.*
FROM indicators_list_value ilv, indicator_view i
WHERE ilv.id_indicator=i.indicatorid;

CREATE OR REPLACE VIEW indicatorvalue_view AS
SELECT DISTINCT iv.*
FROM indicatorvalue iv, indicator_view i
WHERE iv.indicatorid=i.indicatorid;

CREATE OR REPLACE VIEW value_view AS
SELECT DISTINCT v.*
FROM value v, flexible_element_view fe
WHERE v.id_flexible_element=fe.id_flexible_element
ORDER BY v.id_value;

CREATE OR REPLACE VIEW privacy_group_permission_view AS
SELECT DISTINCT pgp.*
FROM privacy_group_permission pgp, privacy_group_view pg, profile p
WHERE pgp.id_privacy_group=pg.id_privacy_group
AND pgp.id_profile=p.id_profile
AND p.id_organization IN (SELECT id_organization FROM organization_view)
ORDER BY pgp.id_permission;

CREATE OR REPLACE VIEW category_element_view AS
SELECT DISTINCT ce.*
FROM category_element ce
WHERE ce.id_organization IN (SELECT id_organization FROM organization_view)
OR ce.id_category_element IN (SELECT id_category_element FROM question_choice_element_view)
ORDER BY ce.id_category_element;

CREATE OR REPLACE VIEW projectreport_view AS
SELECT DISTINCT prr.*
FROM projectreport prr
WHERE prr.project_databaseid IN (SELECT databaseid FROM project_view)
OR (prr.orgunit_partnerid IN (SELECT partnerid FROM partner_view) AND prr.project_databaseid IN (SELECT databaseid FROM project_view))
OR (prr.flexibleelement_id_flexible_element IN (SELECT id_flexible_element FROM flexible_element_view) AND prr.project_databaseid IN (SELECT databaseid FROM project_view))
ORDER BY prr.id;

CREATE OR REPLACE VIEW projectreportversion_view AS
SELECT DISTINCT prv.*
FROM projectreportversion prv
WHERE prv.report_id IN (SELECT id FROM projectreport_view)
OR (prv.editor_userid IN (SELECT userid FROM userlogin_view) AND prv.report_id IN (SELECT id FROM projectreport_view))
ORDER BY prv.id;

CREATE OR REPLACE VIEW projectreportmodel_view AS
SELECT DISTINCT prm.*
FROM projectreportmodel prm
WHERE prm.id_organization IN (SELECT id_organization FROM organization_view)
OR prm.id IN (SELECT model_id FROM projectreport_view)
OR prm.id IN (SELECT model_id FROM report_element_view)
OR prm.id IN (SELECT model_id FROM report_list_element_view);

CREATE OR REPLACE VIEW richtextelement_view AS
SELECT DISTINCT rte.*
FROM richtextelement rte, projectreportversion_view prv
WHERE rte.version_id=prv.id
ORDER BY rte.id;

CREATE OR REPLACE VIEW projectreportmodelsection_view AS
SELECT DISTINCT prms.*
FROM projectreportmodelsection prms
WHERE prms.projectmodelid IN (SELECT id FROM projectreportmodel_view)
OR prms.id IN (SELECT sectionid FROM keyquestion_view)
OR prms.parentsectionmodelid IN (SELECT DISTINCT prms.id FROM projectreportmodelsection prms WHERE prms.projectmodelid IN (SELECT id FROM projectreportmodel_view) OR prms.id IN (SELECT sectionid FROM keyquestion_view))
ORDER BY prms.id;

CREATE OR REPLACE VIEW orgunitpermission_view AS
SELECT DISTINCT oup.*
FROM orgunitpermission oup
WHERE oup.unit_id IN (SELECT partnerid FROM partner_view)
OR oup.user_userid IN (SELECT userid FROM userlogin_view)
ORDER BY oup.id;

CREATE OR REPLACE VIEW partnerindatabase_view AS
SELECT DISTINCT pid.*
FROM partnerindatabase pid
WHERE pid.databaseid IN (SELECT databaseid FROM userdatabase_view)
OR pid.partnerid IN (SELECT partnerid FROM partner_view);

CREATE OR REPLACE VIEW phase_model_definition_view AS
SELECT DISTINCT pmd.*
FROM phase_model_definition pmd, phase_model_view pm
WHERE pmd.id_phase_model_definition=pm.definition_id
ORDER BY pmd.id_phase_model_definition;

CREATE OR REPLACE VIEW reportsubscription_view AS
SELECT DISTINCT rs.*
FROM reportsubscription rs
WHERE rs.userid IN (SELECT userid FROM userlogin_view)
OR rs.invitinguserid IN (SELECT userid FROM userlogin_view);

CREATE OR REPLACE VIEW user_unit_view AS
SELECT DISTINCT uu.*
FROM user_unit uu
WHERE uu.id_user IN (SELECT userid FROM userlogin_view)
OR uu.id_org_unit IN (SELECT partnerid FROM partner_view)
ORDER BY uu.id_user_unit;

CREATE OR REPLACE VIEW user_unit_profiles_view AS
SELECT DISTINCT uup.*
FROM user_unit_profiles uup, user_unit_view uu
WHERE uup.id_user_unit=uu.id_user_unit;

CREATE OR REPLACE VIEW profile_view AS
SELECT DISTINCT p.*
FROM profile p
WHERE p.id_organization IN (SELECT id_organization FROM organization_view)
OR p.id_profile IN (SELECT id_profile FROM privacy_group_permission_view)
OR p.id_profile IN (SELECT id_profile FROM user_unit_profiles_view)
ORDER BY p.id_profile;

CREATE OR REPLACE VIEW global_permission_view AS
SELECT DISTINCT gp.*
FROM global_permission gp, profile_view p
WHERE gp.id_profile=p.id_profile
ORDER BY gp.id_global_permission;

CREATE OR REPLACE VIEW category_type_view AS
SELECT DISTINCT ct.*
FROM category_type ct
WHERE ct.id_organization IN (SELECT id_organization FROM organization_view)
OR ct.id_category_type IN (SELECT id_category_type FROM category_element_view)
OR ct.id_category_type IN (SELECT id_category_type FROM question_element_view)
ORDER BY ct.id_category_type;	

CREATE OR REPLACE VIEW reminder_list_view AS
SELECT DISTINCT rl.*
FROM reminder_list rl, project_view p
WHERE rl.id_reminder_list=p.id_reminder_list
ORDER BY rl.id_reminder_list;

CREATE OR REPLACE VIEW reminder_view AS
SELECT DISTINCT r.*
FROM reminder r, reminder_list_view rl
WHERE r.id_list=rl.id_reminder_list
ORDER BY r.id_reminder;

CREATE OR REPLACE VIEW monitored_point_list_view AS
SELECT DISTINCT mpl.*
FROM monitored_point_list mpl, project_view p
WHERE mpl.id_monitored_point_list=p.id_monitored_points_list
ORDER BY mpl.id_monitored_point_list;

CREATE OR REPLACE VIEW monitored_point_view AS
SELECT DISTINCT mp.*
FROM monitored_point mp, monitored_point_list_view mpl
WHERE mp.id_list=mpl.id_monitored_point_list
ORDER BY mp.id_monitored_point;

CREATE OR REPLACE VIEW file_version_view AS
SELECT DISTINCT fv.*
FROM file_version fv, userlogin_view ul
WHERE fv.id_author=ul.userid
ORDER BY fv.id_file_version;

CREATE OR REPLACE VIEW file_meta_view AS
SELECT DISTINCT fm.*
FROM file_meta fm
WHERE fm.id_file IN (SELECT id_file FROM file_version_view)
OR fm.id_file IN (SELECT id_file FROM monitored_point_view)
ORDER BY fm.id_file;

CREATE OR REPLACE VIEW reportingperiod_view AS
SELECT DISTINCT rp.*
FROM reportingperiod rp, indicatorvalue_view iv
WHERE rp.reportingperiodid=iv.reportingperiodid
ORDER BY rp.reportingperiodid;

CREATE OR REPLACE VIEW activity_view AS
SELECT DISTINCT a.*
FROM activity a, indicator_view i
WHERE a.activityid=i.activityid
ORDER BY a.activityid;

CREATE OR REPLACE VIEW site_view AS
SELECT DISTINCT s.*
FROM site s
WHERE (s.activityid IN (SELECT activityid FROM activity_view)
OR s.siteid IN (SELECT siteid FROM reportingperiod_view)
OR s.databaseid IN (SELECT databaseid FROM userdatabase_view)
OR s.partnerid IN (SELECT partnerid FROM partner_view))
AND s.databaseid IN (SELECT databaseid FROM userdatabase WHERE owneruserid > -1)
ORDER BY s.siteid;

CREATE OR REPLACE VIEW location_view AS
SELECT DISTINCT l.*
FROM location l
WHERE l.locationid IN (SELECT location_locationid FROM partner_view)
OR l.locationid IN (SELECT locationid FROM site_view)
ORDER BY l.locationid;

CREATE OR REPLACE VIEW locationadminlink_view AS
SELECT DISTINCT lal.*
FROM locationadminlink lal, location_view l
WHERE lal.locationid=l.locationid;

CREATE OR REPLACE VIEW attributevalue_view AS
SELECT DISTINCT av.*
FROM attributevalue av, site_view s
WHERE av.siteid=s.siteid;

CREATE OR REPLACE VIEW attribute_view AS
SELECT DISTINCT a.*
FROM attribute a, attributevalue_view av
WHERE a.attributeid=av.attributeid
ORDER BY a.attributeid;

CREATE OR REPLACE VIEW attributegroupinactivity_view AS
SELECT DISTINCT agia.*
FROM attributegroupinactivity agia, activity_view a
WHERE agia.activityid=a.activityid;

CREATE OR REPLACE VIEW attributegroup_view AS
SELECT DISTINCT ag.*
FROM attributegroup ag
WHERE ag.attributegroupid IN (SELECT attributegroupid FROM attribute_view)
OR ag.attributegroupid IN (SELECT attributegroupid FROM attributegroupinactivity_view)
ORDER BY ag.attributegroupid;

CREATE OR REPLACE VIEW userpermission_view AS
SELECT DISTINCT up.*
FROM userpermission up
WHERE (up.databaseid IN (SELECT databaseid FROM userdatabase_view)
OR up.userid IN (SELECT userid FROM userlogin_view)
OR up.partnerid IN (SELECT partnerid FROM partner_view))
AND up.databaseid IN (SELECT databaseid FROM userdatabase WHERE owneruserid > -1)
ORDER BY up.userpermissionid;

CREATE OR REPLACE VIEW reporttemplate_view AS
SELECT DISTINCT rt.*
FROM reporttemplate rt
WHERE rt.databaseid IN (SELECT databaseid FROM userdatabase_view)
OR rt.reporttemplateid IN (SELECT reporttemplateid FROM reportsubscription_view)
OR rt.owneruserid IN (SELECT userid FROM userlogin_view)
ORDER BY rt.reporttemplateid;

CREATE OR REPLACE VIEW history_token_view AS
SELECT DISTINCT ht.*
FROM history_token ht
WHERE ht.id_user IN (SELECT userid FROM userlogin_view)
ORDER BY ht.id_history_token;	
	
CREATE OR REPLACE VIEW amendment_view AS
SELECT DISTINCT a.*
FROM amendment a, project_view p
WHERE a.id_project=p.databaseid
ORDER BY a.id_amendment;

CREATE OR REPLACE VIEW amendment_history_token_view AS
SELECT DISTINCT aht.*
FROM amendment_history_token aht
WHERE aht.amendment_id_amendment IN (SELECT id_amendment FROM amendment)
OR aht.values_id_history_token IN (SELECT id_history_token FROM history_token_view);

CREATE OR REPLACE VIEW log_frame_view AS
SELECT DISTINCT lf.*
FROM log_frame lf
WHERE lf.id_log_frame IN (SELECT id_log_frame FROM amendment_view)
OR lf.id_project IN (SELECT databaseid FROM project_view)
ORDER BY lf.id_log_frame;

CREATE OR REPLACE VIEW log_frame_model_view AS
SELECT DISTINCT lfm.*
FROM log_frame_model lfm
WHERE lfm.id_log_frame IN (SELECT id_log_frame_model FROM log_frame_view)
OR lfm.id_project_model IN (SELECT id_project_model FROM project_model_view)
ORDER BY lfm.id_log_frame;

CREATE OR REPLACE VIEW log_frame_group_view AS
SELECT DISTINCT lfg.id_group,
lfg.label,
lfg.type,
lfg.id_log_frame
FROM log_frame_group lfg, log_frame_view lf
WHERE lfg.id_log_frame=lf.id_log_frame
ORDER BY lfg.id_group;

CREATE OR REPLACE VIEW log_frame_element_view AS
SELECT DISTINCT lfe.*
FROM log_frame_element lfe, log_frame_group_view lfg
WHERE lfe.id_group=lfg.id_group
ORDER BY lfe.id_element;

CREATE OR REPLACE VIEW log_frame_prerequisite_view AS
SELECT DISTINCT lfp.id_prerequisite,
lfp.code,
lfp.content,
lfp.position,
lfp.id_group,
lfp.id_log_frame
FROM log_frame_prerequisite lfp
WHERE lfp.id_group IN (SELECT id_group FROM log_frame_group_view)
OR lfp.id_log_frame IN (SELECT id_log_frame FROM log_frame_view)
ORDER BY lfp.id_prerequisite;	

CREATE OR REPLACE VIEW log_frame_expected_result_view AS
SELECT DISTINCT lfer.*
FROM log_frame_expected_result lfer, log_frame_element_view lfe
WHERE lfer.id_element=lfe.id_element
ORDER BY lfer.id_element;

CREATE OR REPLACE VIEW log_frame_specific_objective_view AS
SELECT DISTINCT lfso.*
FROM log_frame_specific_objective lfso
WHERE lfso.id_element IN (SELECT id_specific_objective FROM log_frame_expected_result_view)
OR lfso.id_log_frame IN (SELECT id_log_frame FROM log_frame_view)
ORDER BY lfso.id_element;

CREATE OR REPLACE VIEW log_frame_activity_view AS
SELECT DISTINCT lfa.*
FROM log_frame_activity lfa
WHERE lfa.id_result IN (SELECT id_element FROM log_frame_expected_result_view)
OR lfa.id_element IN (SELECT id_element FROM log_frame_element_view)
ORDER BY lfa.id_element;

CREATE OR REPLACE VIEW log_frame_indicators_view AS
SELECT DISTINCT lfi.*
FROM log_frame_indicators lfi
WHERE lfi.log_frame_element_id_element IN (SELECT id_element FROM log_frame_element_view)
OR lfi.indicators_indicatorid IN (SELECT indicatorid FROM indicator_view);

CREATE OR REPLACE VIEW personalcalendar_view AS
SELECT DISTINCT pec.*
FROM personalcalendar pec
WHERE pec.id IN (SELECT calendarid FROM project_view)
OR pec.id IN (SELECT calendarid FROM partner_view)
ORDER BY pec.id;

CREATE OR REPLACE VIEW personalevent_view AS
SELECT DISTINCT pe.*
FROM personalevent pe, personalcalendar_view pc
WHERE pe.calendarid=pc.id;

CREATE OR REPLACE VIEW authentication_view AS
SELECT DISTINCT a.*
FROM authentication a, userlogin_view ul
WHERE a.userid=ul.userid;

CREATE OR REPLACE VIEW budget_part_view AS
SELECT DISTINCT bp.*
FROM budget_part bp;

CREATE OR REPLACE VIEW bugdet_parts_list_value_view AS
SELECT DISTINCT bplv.*
FROM budget_parts_list_value bplv, budget_part_view bp
WHERE bplv.id_budget_parts_list=bp.id_budget_parts_list;

CREATE OR REPLACE VIEW budget_view AS
SELECT DISTINCT b.*
FROM budget b, bugdet_parts_list_value_view bplv
WHERE b.id_budget=bplv.id_budget;

CREATE OR REPLACE VIEW triplet_value_view AS
SELECT DISTINCT tv.*
FROM triplet_value tv;