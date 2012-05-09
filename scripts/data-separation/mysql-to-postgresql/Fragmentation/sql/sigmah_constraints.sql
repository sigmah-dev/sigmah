ALTER TABLE ONLY activity
    ADD CONSTRAINT activity_pkey PRIMARY KEY (activityid);
ALTER TABLE ONLY adminentity
    ADD CONSTRAINT adminentity_pkey PRIMARY KEY (adminentityid);
ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT adminlevel_pkey PRIMARY KEY (adminlevelid);
ALTER TABLE ONLY amendment
    ADD CONSTRAINT amendment_pkey PRIMARY KEY (id_amendment);
ALTER TABLE ONLY attribute
    ADD CONSTRAINT attribute_pkey PRIMARY KEY (attributeid);
ALTER TABLE ONLY attributegroup
    ADD CONSTRAINT attributegroup_pkey PRIMARY KEY (attributegroupid);
ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT attributegroupinactivity_pkey PRIMARY KEY (activityid, attributegroupid);
ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT attributevalue_pkey PRIMARY KEY (attributeid, siteid);
ALTER TABLE ONLY authentication
    ADD CONSTRAINT authentication_pkey PRIMARY KEY (authtoken);
ALTER TABLE ONLY budget_distribution_element
    ADD CONSTRAINT budget_distribution_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY budget_part
    ADD CONSTRAINT budget_part_pkey PRIMARY KEY (id_budget_part);
ALTER TABLE ONLY budget_parts_list_value
    ADD CONSTRAINT budget_parts_list_value_pkey PRIMARY KEY (id_budget_parts_list);
ALTER TABLE ONLY budget
    ADD CONSTRAINT budget_pkey PRIMARY KEY (id_budget);
ALTER TABLE ONLY category_element
    ADD CONSTRAINT category_element_pkey PRIMARY KEY (id_category_element);
ALTER TABLE ONLY category_type
    ADD CONSTRAINT category_type_pkey PRIMARY KEY (id_category_type);
ALTER TABLE ONLY checkbox_element
    ADD CONSTRAINT checkbox_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY country
    ADD CONSTRAINT country_pkey PRIMARY KEY (countryid);
ALTER TABLE ONLY default_flexible_element
    ADD CONSTRAINT default_flexible_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY file_meta
    ADD CONSTRAINT file_meta_pkey PRIMARY KEY (id_file);
ALTER TABLE ONLY file_version
    ADD CONSTRAINT file_version_id_file_version_number_key UNIQUE (id_file, version_number);
ALTER TABLE ONLY file_version
    ADD CONSTRAINT file_version_pkey PRIMARY KEY (id_file_version);
ALTER TABLE ONLY files_list_element
    ADD CONSTRAINT files_list_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY flexible_element
    ADD CONSTRAINT flexible_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY global_permission
    ADD CONSTRAINT global_permission_pkey PRIMARY KEY (id_global_permission);
ALTER TABLE ONLY history_token
    ADD CONSTRAINT history_token_pkey PRIMARY KEY (id_history_token);
ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT indicator_datasource_pkey PRIMARY KEY (indicatorid, indicatorsourceid);
ALTER TABLE ONLY indicator_labels
    ADD CONSTRAINT indicator_labels_pkey PRIMARY KEY (indicator_indicatorid, code);
ALTER TABLE ONLY indicator
    ADD CONSTRAINT indicator_pkey PRIMARY KEY (indicatorid);
ALTER TABLE ONLY indicators_list_element
    ADD CONSTRAINT indicators_list_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY indicators_list_value
    ADD CONSTRAINT indicators_list_value_pkey PRIMARY KEY (id_indicators_list, id_indicator);
ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT indicatorvalue_pkey PRIMARY KEY (indicatorid, reportingperiodid);
ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT keyquestion_pkey PRIMARY KEY (id);
ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT layout_constraint_pkey PRIMARY KEY (id_layout_constraint);
ALTER TABLE ONLY layout_group
    ADD CONSTRAINT layout_group_pkey PRIMARY KEY (id_layout_group);
ALTER TABLE ONLY layout
    ADD CONSTRAINT layout_pkey PRIMARY KEY (id_layout);
ALTER TABLE ONLY location
    ADD CONSTRAINT location_pkey PRIMARY KEY (locationid);
ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT locationadminlink_pkey PRIMARY KEY (locationid, adminentityid);
ALTER TABLE ONLY locationtype
    ADD CONSTRAINT locationtype_pkey PRIMARY KEY (locationtypeid);
ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT log_frame_activity_pkey PRIMARY KEY (id_element);
ALTER TABLE ONLY log_frame_element
    ADD CONSTRAINT log_frame_element_pkey PRIMARY KEY (id_element);
ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT log_frame_expected_result_pkey PRIMARY KEY (id_element);
ALTER TABLE ONLY log_frame_group
    ADD CONSTRAINT log_frame_group_pkey PRIMARY KEY (id_group);
ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT log_frame_indicators_pkey PRIMARY KEY (log_frame_element_id_element, indicators_indicatorid);
ALTER TABLE ONLY log_frame_model
    ADD CONSTRAINT log_frame_model_pkey PRIMARY KEY (id_log_frame);
ALTER TABLE ONLY log_frame
    ADD CONSTRAINT log_frame_pkey PRIMARY KEY (id_log_frame);
ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT log_frame_prerequisite_pkey PRIMARY KEY (id_prerequisite);
ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT log_frame_specific_objective_pkey PRIMARY KEY (id_element);
ALTER TABLE ONLY message_element
    ADD CONSTRAINT message_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY monitored_point_list
    ADD CONSTRAINT monitored_point_list_pkey PRIMARY KEY (id_monitored_point_list);
ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT monitored_point_pkey PRIMARY KEY (id_monitored_point);
ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT org_unit_banner_pkey PRIMARY KEY (banner_id);
ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT org_unit_details_pkey PRIMARY KEY (details_id);
ALTER TABLE ONLY org_unit_model
    ADD CONSTRAINT org_unit_model_pkey PRIMARY KEY (org_unit_model_id);
ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id_organization);
ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT orgunitpermission_pkey PRIMARY KEY (id);
ALTER TABLE ONLY partner
    ADD CONSTRAINT partner_pkey PRIMARY KEY (partnerid);
ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT partnerindatabase_pkey PRIMARY KEY (databaseid, partnerid);
ALTER TABLE ONLY personalcalendar
    ADD CONSTRAINT personalcalendar_pkey PRIMARY KEY (id);
ALTER TABLE ONLY personalevent
    ADD CONSTRAINT personalevent_pkey PRIMARY KEY (id);
ALTER TABLE ONLY phase_model_definition
    ADD CONSTRAINT phase_model_definition_pkey PRIMARY KEY (id_phase_model_definition);
ALTER TABLE ONLY phase_model
    ADD CONSTRAINT phase_model_pkey PRIMARY KEY (id_phase_model);
ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT phase_model_sucessors_id_phase_model_id_phase_model_success_key UNIQUE (id_phase_model, id_phase_model_successor);
ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT phase_model_sucessors_id_phase_model_successor_key UNIQUE (id_phase_model_successor);
ALTER TABLE ONLY phase
    ADD CONSTRAINT phase_pkey PRIMARY KEY (id_phase);
ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT privacy_group_permission_pkey PRIMARY KEY (id_permission);
ALTER TABLE ONLY privacy_group
    ADD CONSTRAINT privacy_group_pkey PRIMARY KEY (id_privacy_group);
ALTER TABLE ONLY profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (id_profile);
ALTER TABLE ONLY project_banner
    ADD CONSTRAINT project_banner_pkey PRIMARY KEY (id);
ALTER TABLE ONLY project_details
    ADD CONSTRAINT project_details_pkey PRIMARY KEY (id);
ALTER TABLE ONLY project_funding
    ADD CONSTRAINT project_funding_pkey PRIMARY KEY (id_funding);
ALTER TABLE ONLY project_model
    ADD CONSTRAINT project_model_pkey PRIMARY KEY (id_project_model);
ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT project_model_visibility_pkey PRIMARY KEY (id_visibility);
ALTER TABLE ONLY project
    ADD CONSTRAINT project_pkey PRIMARY KEY (databaseid);
ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT project_userlogin_pkey PRIMARY KEY (project_databaseid, favoriteusers_userid);
ALTER TABLE ONLY projectreport
    ADD CONSTRAINT projectreport_pkey PRIMARY KEY (id);
ALTER TABLE ONLY projectreportmodel
    ADD CONSTRAINT projectreportmodel_pkey PRIMARY KEY (id);
ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT projectreportmodelsection_pkey PRIMARY KEY (id);
ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT projectreportversion_pkey PRIMARY KEY (id);
ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT quality_criterion_children_id_quality_criterion_id_quality__key UNIQUE (id_quality_criterion, id_quality_criterion_child);
ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT quality_criterion_children_pkey PRIMARY KEY (id_quality_criterion_child);
ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT quality_criterion_pkey PRIMARY KEY (id_quality_criterion);
ALTER TABLE ONLY quality_criterion_type
    ADD CONSTRAINT quality_criterion_type_pkey PRIMARY KEY (id_criterion_type);
ALTER TABLE ONLY quality_framework
    ADD CONSTRAINT quality_framework_pkey PRIMARY KEY (id_quality_framework);
ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT question_choice_element_pkey PRIMARY KEY (id_choice);
ALTER TABLE ONLY question_element
    ADD CONSTRAINT question_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY reminder_list
    ADD CONSTRAINT reminder_list_pkey PRIMARY KEY (id_reminder_list);
ALTER TABLE ONLY reminder
    ADD CONSTRAINT reminder_pkey PRIMARY KEY (id_reminder);
ALTER TABLE ONLY report_element
    ADD CONSTRAINT report_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT report_list_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY reportingperiod
    ADD CONSTRAINT reportingperiod_pkey PRIMARY KEY (reportingperiodid);
ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT reportsubscription_pkey PRIMARY KEY (reporttemplateid, userid);
ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT reporttemplate_pkey PRIMARY KEY (reporttemplateid);
ALTER TABLE ONLY richtextelement
    ADD CONSTRAINT richtextelement_pkey PRIMARY KEY (id);
ALTER TABLE ONLY site
    ADD CONSTRAINT site_pkey PRIMARY KEY (siteid);
ALTER TABLE ONLY textarea_element
    ADD CONSTRAINT textarea_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY triplet_value
    ADD CONSTRAINT triplet_value_pkey PRIMARY KEY (id_triplet);
ALTER TABLE ONLY triplets_list_element
    ADD CONSTRAINT triplets_list_element_pkey PRIMARY KEY (id_flexible_element);
ALTER TABLE ONLY user_unit
    ADD CONSTRAINT user_unit_pkey PRIMARY KEY (id_user_unit);
ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT user_unit_profiles_id_user_unit_id_profile_key UNIQUE (id_user_unit, id_profile);
ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT userdatabase_pkey PRIMARY KEY (databaseid);
ALTER TABLE ONLY userlogin
    ADD CONSTRAINT userlogin_email_key UNIQUE (email);
ALTER TABLE ONLY userlogin
    ADD CONSTRAINT userlogin_pkey PRIMARY KEY (userid);
ALTER TABLE ONLY userpermission
    ADD CONSTRAINT userpermission_pkey PRIMARY KEY (userpermissionid);
ALTER TABLE ONLY value
    ADD CONSTRAINT value_id_flexible_element_id_project_key UNIQUE (id_flexible_element, id_project);
ALTER TABLE ONLY value
    ADD CONSTRAINT value_pkey PRIMARY KEY (id_value);
ALTER TABLE ONLY category_type
    ADD CONSTRAINT fk1432f9db87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY user_unit
    ADD CONSTRAINT fk143d4d78b7206e89 FOREIGN KEY (id_org_unit) REFERENCES partner(partnerid) DEFERRABLE;
ALTER TABLE ONLY user_unit
    ADD CONSTRAINT fk143d4d78dd0ca99c FOREIGN KEY (id_user) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY org_unit_model
    ADD CONSTRAINT fk15d234e987d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT fk17871bd711158eaf FOREIGN KEY (id_category_element) REFERENCES category_element(id_category_element) DEFERRABLE;
ALTER TABLE ONLY question_choice_element
    ADD CONSTRAINT fk17871bd7d92f832c FOREIGN KEY (id_question) REFERENCES question_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT fk17e5a9f1a023ddc FOREIGN KEY (indicators_indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;
ALTER TABLE ONLY log_frame_indicators
    ADD CONSTRAINT fk17e5a9f1f6e4c4b8 FOREIGN KEY (log_frame_element_id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;
ALTER TABLE ONLY project_banner
    ADD CONSTRAINT fk1bc8331244f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;
ALTER TABLE ONLY project_banner
    ADD CONSTRAINT fk1bc83312d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;
ALTER TABLE ONLY site
    ADD CONSTRAINT fk2753671fcde08d FOREIGN KEY (assessmentsiteid) REFERENCES site(siteid) DEFERRABLE;
ALTER TABLE ONLY site
    ADD CONSTRAINT fk275367368ddfa7 FOREIGN KEY (locationid) REFERENCES location(locationid) DEFERRABLE;
ALTER TABLE ONLY site
    ADD CONSTRAINT fk275367494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;
ALTER TABLE ONLY site
    ADD CONSTRAINT fk27536779d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;
ALTER TABLE ONLY site
    ADD CONSTRAINT fk27536780bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;
ALTER TABLE ONLY adminentity
    ADD CONSTRAINT fk2e3083f227f5cac7 FOREIGN KEY (adminlevelid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;
ALTER TABLE ONLY adminentity
    ADD CONSTRAINT fk2e3083f2ff2bada7 FOREIGN KEY (adminentityparentid) REFERENCES adminentity(adminentityid) DEFERRABLE;
ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8350d2271 FOREIGN KEY (location_locationid) REFERENCES location(locationid) DEFERRABLE;
ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a84ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;
ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a85179b874 FOREIGN KEY (parent_partnerid) REFERENCES partner(partnerid) DEFERRABLE;
ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8cf94c360 FOREIGN KEY (organization_id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY partner
    ADD CONSTRAINT fk33f574a8faec4abb FOREIGN KEY (office_country_id) REFERENCES country(countryid) DEFERRABLE;
ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f790911741f030 FOREIGN KEY (reporttemplateid) REFERENCES reporttemplate(reporttemplateid) DEFERRABLE;
ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f7909148b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY reportsubscription
    ADD CONSTRAINT fk35f7909173633c59 FOREIGN KEY (invitinguserid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba320d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba370812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;
ALTER TABLE ONLY question_element
    ADD CONSTRAINT fk3d05bba3b6ab611d FOREIGN KEY (id_category_type) REFERENCES category_type(id_category_type) DEFERRABLE;
ALTER TABLE ONLY organization
    ADD CONSTRAINT fk4644ed33754a9e7e FOREIGN KEY (id_root_org_unit) REFERENCES partner(partnerid) DEFERRABLE;
ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT fk46aeba86a5c52bc6 FOREIGN KEY (owneruserid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY userdatabase
    ADD CONSTRAINT fk46aeba86b6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;
ALTER TABLE ONLY default_flexible_element
    ADD CONSTRAINT fk48d914c620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT fk4a73751d70812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;
ALTER TABLE ONLY quality_criterion_children
    ADD CONSTRAINT fk4a73751dfe03d96d FOREIGN KEY (id_quality_criterion_child) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;
ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;
ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef70812310 FOREIGN KEY (id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;
ALTER TABLE ONLY indicator
    ADD CONSTRAINT fk4d01ddef80bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;
ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT fk4ed7045544c2434b FOREIGN KEY (siteid) REFERENCES site(siteid) DEFERRABLE;
ALTER TABLE ONLY attributevalue
    ADD CONSTRAINT fk4ed70455afed0b31 FOREIGN KEY (attributeid) REFERENCES attribute(attributeid) DEFERRABLE;
ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT fk50408394368ddfa7 FOREIGN KEY (locationid) REFERENCES location(locationid) DEFERRABLE;
ALTER TABLE ONLY locationadminlink
    ADD CONSTRAINT fk50408394cd1204fd FOREIGN KEY (adminentityid) REFERENCES adminentity(adminentityid) DEFERRABLE;
ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;
ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f955bb91b6 FOREIGN KEY (id_manager) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9b07b74ff FOREIGN KEY (id_monitored_points_list) REFERENCES monitored_point_list(id_monitored_point_list) DEFERRABLE;
ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;
ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9dffa476a FOREIGN KEY (id_current_phase) REFERENCES phase(id_phase) DEFERRABLE;
ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9e2910b71 FOREIGN KEY (id_reminder_list) REFERENCES reminder_list(id_reminder_list) DEFERRABLE;
ALTER TABLE ONLY file_version
    ADD CONSTRAINT fk52157d152c2c465c FOREIGN KEY (id_author) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY file_version
    ADD CONSTRAINT fk52157d15d4cd29db FOREIGN KEY (id_file) REFERENCES file_meta(id_file) DEFERRABLE;
ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd74485e32a FOREIGN KEY (id_project_funding) REFERENCES project(databaseid) DEFERRABLE;
ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd7597e985f FOREIGN KEY (id_funding) REFERENCES project_funding(id_funding) DEFERRABLE;
ALTER TABLE ONLY project_funding
    ADD CONSTRAINT fk52f38bd7c908f825 FOREIGN KEY (id_project_funded) REFERENCES project(databaseid) DEFERRABLE;
ALTER TABLE ONLY triplets_list_element
    ADD CONSTRAINT fk532b05fd20d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY message_element
    ADD CONSTRAINT fk553ccec420d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY log_frame_element
    ADD CONSTRAINT fk5a2e206f4f6005ee FOREIGN KEY (id_group) REFERENCES log_frame_group(id_group) DEFERRABLE;
ALTER TABLE ONLY budget_part
    ADD CONSTRAINT fk5a830ade653f90a FOREIGN KEY (id_budget_parts_list) REFERENCES budget_parts_list_value(id_budget_parts_list) DEFERRABLE;
ALTER TABLE ONLY files_list_element
    ADD CONSTRAINT fk6459a12320d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY locationtype
    ADD CONSTRAINT fk65214af20feb745 FOREIGN KEY (boundadminlevelid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;
ALTER TABLE ONLY locationtype
    ADD CONSTRAINT fk65214afb6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;
ALTER TABLE ONLY phase
    ADD CONSTRAINT fk65b097bb13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;
ALTER TABLE ONLY phase
    ADD CONSTRAINT fk65b097bc9c78c91 FOREIGN KEY (id_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;
ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT fk676020c247c62157 FOREIGN KEY (indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;
ALTER TABLE ONLY indicatorvalue
    ADD CONSTRAINT fk676020c284811db7 FOREIGN KEY (reportingperiodid) REFERENCES reportingperiod(reportingperiodid) DEFERRABLE;
ALTER TABLE ONLY category_element
    ADD CONSTRAINT fk67dfa4bb87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY category_element
    ADD CONSTRAINT fk67dfa4bbb6ab611d FOREIGN KEY (id_category_type) REFERENCES category_type(id_category_type) DEFERRABLE;
ALTER TABLE ONLY budget_parts_list_value
    ADD CONSTRAINT fk69676b09c9ce70ad FOREIGN KEY (id_budget) REFERENCES budget(id_budget) DEFERRABLE;
ALTER TABLE ONLY value
    ADD CONSTRAINT fk6ac917120d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY value
    ADD CONSTRAINT fk6ac91712922bbb3 FOREIGN KEY (id_user_last_modif) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY privacy_group
    ADD CONSTRAINT fk74e7b70887d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY location
    ADD CONSTRAINT fk752a03d58c0165bb FOREIGN KEY (locationtypeid) REFERENCES locationtype(locationtypeid) DEFERRABLE;
ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT fk76d1d76183d8e9ca FOREIGN KEY (id_quality_framework) REFERENCES quality_framework(id_quality_framework) DEFERRABLE;
ALTER TABLE ONLY quality_criterion
    ADD CONSTRAINT fk76d1d76187d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY attribute
    ADD CONSTRAINT fk7839ca7cda7c5e3 FOREIGN KEY (attributegroupid) REFERENCES attributegroup(attributegroupid) DEFERRABLE;
ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT fk7a142472181ec2f8 FOREIGN KEY (id_phase_model_successor) REFERENCES phase_model(id_phase_model) DEFERRABLE;
ALTER TABLE ONLY phase_model_sucessors
    ADD CONSTRAINT fk7a142472c9c78c91 FOREIGN KEY (id_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;
ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT fk7a87f87547c62157 FOREIGN KEY (indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;
ALTER TABLE ONLY indicator_datasource
    ADD CONSTRAINT fk7a87f8755038b772 FOREIGN KEY (indicatorsourceid) REFERENCES indicator(indicatorid) DEFERRABLE;
ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT fk8076a4d884058733 FOREIGN KEY (project_databaseid) REFERENCES project(databaseid) DEFERRABLE;
ALTER TABLE ONLY project_userlogin
    ADD CONSTRAINT fk8076a4d8efbea106 FOREIGN KEY (favoriteusers_userid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY quality_framework
    ADD CONSTRAINT fk807dbabe87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY amendment
    ADD CONSTRAINT fk807f02ed9bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;
ALTER TABLE ONLY amendment
    ADD CONSTRAINT fk807f02edb13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;
ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT fk8104218620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY report_list_element
    ADD CONSTRAINT fk8104218654081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id) DEFERRABLE;
ALTER TABLE ONLY layout_group
    ADD CONSTRAINT fk8435cd2a44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;
ALTER TABLE ONLY projectreportmodel
    ADD CONSTRAINT fk85b7359c87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY log_frame
    ADD CONSTRAINT fk88122cb2b13b3e6c FOREIGN KEY (id_project) REFERENCES project(databaseid) DEFERRABLE;
ALTER TABLE ONLY log_frame
    ADD CONSTRAINT fk88122cb2eee3ae75 FOREIGN KEY (id_log_frame_model) REFERENCES log_frame_model(id_log_frame) DEFERRABLE;
ALTER TABLE ONLY budget_distribution_element
    ADD CONSTRAINT fk881d68fb20d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT fk88c951234f6005ee FOREIGN KEY (id_group) REFERENCES log_frame_group(id_group) DEFERRABLE;
ALTER TABLE ONLY log_frame_prerequisite
    ADD CONSTRAINT fk88c951239bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;
ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT fk89611ffc8012bc39 FOREIGN KEY (id_result) REFERENCES log_frame_expected_result(id_element) DEFERRABLE;
ALTER TABLE ONLY log_frame_activity
    ADD CONSTRAINT fk89611ffce41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;
ALTER TABLE ONLY userlogin
    ADD CONSTRAINT fk8aa0da3e87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY textarea_element
    ADD CONSTRAINT fk8d80a2f720d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT fk8df3554a3dc0a3b1 FOREIGN KEY (id_list) REFERENCES monitored_point_list(id_monitored_point_list) DEFERRABLE;
ALTER TABLE ONLY monitored_point
    ADD CONSTRAINT fk8df3554ad4cd29db FOREIGN KEY (id_file) REFERENCES file_meta(id_file) DEFERRABLE;
ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT fk90ee7d6c44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;
ALTER TABLE ONLY org_unit_banner
    ADD CONSTRAINT fk90ee7d6c4ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;
ALTER TABLE ONLY flexible_element
    ADD CONSTRAINT fk91725e88e25e8842 FOREIGN KEY (id_privacy_group) REFERENCES privacy_group(id_privacy_group) DEFERRABLE;
ALTER TABLE ONLY richtextelement
    ADD CONSTRAINT fk9752ca7398d45965 FOREIGN KEY (version_id) REFERENCES projectreportversion(id) DEFERRABLE;
ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT fk99d3ddf7d88379d4 FOREIGN KEY (id_specific_objective) REFERENCES log_frame_specific_objective(id_element) DEFERRABLE;
ALTER TABLE ONLY log_frame_expected_result
    ADD CONSTRAINT fk99d3ddf7e41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;
ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT fk9bb4b21220d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY layout_constraint
    ADD CONSTRAINT fk9bb4b212da924c21 FOREIGN KEY (id_layout_group) REFERENCES layout_group(id_layout_group) DEFERRABLE;
ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT fk9ec33d95b6676e25 FOREIGN KEY (countryid) REFERENCES country(countryid) DEFERRABLE;
ALTER TABLE ONLY adminlevel
    ADD CONSTRAINT fk9ec33d95e01b109c FOREIGN KEY (parentid) REFERENCES adminlevel(adminlevelid) DEFERRABLE;
ALTER TABLE ONLY activity
    ADD CONSTRAINT fka126572f494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;
ALTER TABLE ONLY activity
    ADD CONSTRAINT fka126572f8c0165bb FOREIGN KEY (locationtypeid) REFERENCES locationtype(locationtypeid) DEFERRABLE;
ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT fka1812f6692e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;
ALTER TABLE ONLY privacy_group_permission
    ADD CONSTRAINT fka1812f66e25e8842 FOREIGN KEY (id_privacy_group) REFERENCES privacy_group(id_privacy_group) DEFERRABLE;
ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT fka9a62c88494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;
ALTER TABLE ONLY partnerindatabase
    ADD CONSTRAINT fka9a62c8879d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;
ALTER TABLE ONLY quality_criterion_type
    ADD CONSTRAINT fkb0b3e55883d8e9ca FOREIGN KEY (id_quality_framework) REFERENCES quality_framework(id_quality_framework) DEFERRABLE;
ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT fkb29299a98fa2795f FOREIGN KEY (projectmodelid) REFERENCES projectreportmodel(id) DEFERRABLE;
ALTER TABLE ONLY projectreportmodelsection
    ADD CONSTRAINT fkb29299a9ae53865a FOREIGN KEY (parentsectionmodelid) REFERENCES projectreportmodelsection(id) DEFERRABLE;
ALTER TABLE ONLY log_frame_group
    ADD CONSTRAINT fkb4d3b8b29bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;
ALTER TABLE ONLY log_frame_model
    ADD CONSTRAINT fkb526bd5cd196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;
ALTER TABLE ONLY report_element
    ADD CONSTRAINT fkbc80a6f120d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY report_element
    ADD CONSTRAINT fkbc80a6f154081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id) DEFERRABLE;
ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT fkc093868b39413f9b FOREIGN KEY (report_id) REFERENCES projectreport(id) DEFERRABLE;
ALTER TABLE ONLY projectreportversion
    ADD CONSTRAINT fkc093868b54402265 FOREIGN KEY (editor_userid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT fkc10e64e87d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY project_model_visibility
    ADD CONSTRAINT fkc10e64ed196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;
ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f6544f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;
ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f65d196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;
ALTER TABLE ONLY phase_model
    ADD CONSTRAINT fkc11f1f65e0174a FOREIGN KEY (definition_id) REFERENCES phase_model_definition(id_phase_model_definition) DEFERRABLE;
ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT fkc37e36d192e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;
ALTER TABLE ONLY user_unit_profiles
    ADD CONSTRAINT fkc37e36d1b3ab1d1c FOREIGN KEY (id_user_unit) REFERENCES user_unit(id_user_unit) DEFERRABLE;
ALTER TABLE ONLY amendment_history_token
    ADD CONSTRAINT fkc514f4bc7b49ebc6 FOREIGN KEY (amendment_id_amendment) REFERENCES amendment(id_amendment) DEFERRABLE;
ALTER TABLE ONLY amendment_history_token
    ADD CONSTRAINT fkc514f4bcbc854628 FOREIGN KEY (values_id_history_token) REFERENCES history_token(id_history_token) DEFERRABLE;
ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT fkc69ddee494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;
ALTER TABLE ONLY reporttemplate
    ADD CONSTRAINT fkc69ddeea5c52bc6 FOREIGN KEY (owneruserid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY project_model
    ADD CONSTRAINT fkc7b83283792a5c7c FOREIGN KEY (id_root_phase_model) REFERENCES phase_model(id_phase_model) DEFERRABLE;
ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT fkc979ef199bc5c4da FOREIGN KEY (id_log_frame) REFERENCES log_frame(id_log_frame) DEFERRABLE;
ALTER TABLE ONLY log_frame_specific_objective
    ADD CONSTRAINT fkc979ef19e41dae8 FOREIGN KEY (id_element) REFERENCES log_frame_element(id_element) DEFERRABLE;
ALTER TABLE ONLY global_permission
    ADD CONSTRAINT fkcb8783eb92e83e47 FOREIGN KEY (id_profile) REFERENCES profile(id_profile) DEFERRABLE;
ALTER TABLE ONLY project_details
    ADD CONSTRAINT fkce2cbb1c44f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;
ALTER TABLE ONLY project_details
    ADD CONSTRAINT fkce2cbb1cd196f951 FOREIGN KEY (id_project_model) REFERENCES project_model(id_project_model) DEFERRABLE;
ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a48b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a494bd9e FOREIGN KEY (databaseid) REFERENCES userdatabase(databaseid) DEFERRABLE;
ALTER TABLE ONLY userpermission
    ADD CONSTRAINT fkd265581a79d901c9 FOREIGN KEY (partnerid) REFERENCES partner(partnerid) DEFERRABLE;
ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT fkd2af174536d186ad FOREIGN KEY (qualitycriterion_id_quality_criterion) REFERENCES quality_criterion(id_quality_criterion) DEFERRABLE;
ALTER TABLE ONLY keyquestion
    ADD CONSTRAINT fkd2af1745d8178e71 FOREIGN KEY (sectionid) REFERENCES projectreportmodelsection(id) DEFERRABLE;
ALTER TABLE ONLY history_token
    ADD CONSTRAINT fkd692428edd0ca99c FOREIGN KEY (id_user) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY reportingperiod
    ADD CONSTRAINT fkdcfe056f44c2434b FOREIGN KEY (siteid) REFERENCES site(siteid) DEFERRABLE;
ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT fkdd8c951780bf17db FOREIGN KEY (activityid) REFERENCES activity(activityid) DEFERRABLE;
ALTER TABLE ONLY attributegroupinactivity
    ADD CONSTRAINT fkdd8c9517da7c5e3 FOREIGN KEY (attributegroupid) REFERENCES attributegroup(attributegroupid) DEFERRABLE;
ALTER TABLE ONLY authentication
    ADD CONSTRAINT fkddeeae9848b34b53 FOREIGN KEY (userid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d3cdc69db FOREIGN KEY (flexibleelement_id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d54081a85 FOREIGN KEY (model_id) REFERENCES projectreportmodel(id) DEFERRABLE;
ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d5a50539e FOREIGN KEY (currentversion_id) REFERENCES projectreportversion(id) DEFERRABLE;
ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458d84058733 FOREIGN KEY (project_databaseid) REFERENCES project(databaseid) DEFERRABLE;
ALTER TABLE ONLY projectreport
    ADD CONSTRAINT fke0b8458db2590b2 FOREIGN KEY (orgunit_partnerid) REFERENCES partner(partnerid) DEFERRABLE;
ALTER TABLE ONLY reminder
    ADD CONSTRAINT fke116c072e22ec8c FOREIGN KEY (id_list) REFERENCES reminder_list(id_reminder_list) DEFERRABLE;
ALTER TABLE ONLY checkbox_element
    ADD CONSTRAINT fke1e36e8020d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY indicators_list_element
    ADD CONSTRAINT fkeb796c7620d5ae49 FOREIGN KEY (id_flexible_element) REFERENCES flexible_element(id_flexible_element) DEFERRABLE;
ALTER TABLE ONLY profile
    ADD CONSTRAINT fked8e89a987d1466c FOREIGN KEY (id_organization) REFERENCES organization(id_organization) DEFERRABLE;
ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT fkf10e425774ec9247 FOREIGN KEY (user_userid) REFERENCES userlogin(userid) DEFERRABLE;
ALTER TABLE ONLY orgunitpermission
    ADD CONSTRAINT fkf10e4257d3cc239c FOREIGN KEY (unit_id) REFERENCES partner(partnerid) DEFERRABLE;
ALTER TABLE ONLY indicators_list_value
    ADD CONSTRAINT fkf8bf56b6530fdd8 FOREIGN KEY (id_indicator) REFERENCES indicator(indicatorid) DEFERRABLE;
ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT fkfdcfbc0244f6265a FOREIGN KEY (id_layout) REFERENCES layout(id_layout) DEFERRABLE;
ALTER TABLE ONLY org_unit_details
    ADD CONSTRAINT fkfdcfbc024ba27d70 FOREIGN KEY (id_org_unit_model) REFERENCES org_unit_model(org_unit_model_id) DEFERRABLE;
ALTER TABLE ONLY indicator_labels
    ADD CONSTRAINT fkfe14c44f52429f27 FOREIGN KEY (indicator_indicatorid) REFERENCES indicator(indicatorid) DEFERRABLE;