package org.sigmah.client;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.computation.ClientValueResolver;
import org.sigmah.client.computation.ComputationTriggerManager;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.ExceptionHandler;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.PageManager;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.ui.presenter.ApplicationPresenter;
import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.client.ui.presenter.CreditsPresenter;
import org.sigmah.client.ui.presenter.DashboardPresenter;
import org.sigmah.client.ui.presenter.HelpPresenter;
import org.sigmah.client.ui.presenter.LoginPresenter;
import org.sigmah.client.ui.presenter.MockUpPresenter;
import org.sigmah.client.ui.presenter.admin.AdminPresenter;
import org.sigmah.client.ui.presenter.admin.CategoriesAdminPresenter;
import org.sigmah.client.ui.presenter.admin.ParametersAdminPresenter;
import org.sigmah.client.ui.presenter.admin.ReportModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.importation.AddImportationSchemePresenter;
import org.sigmah.client.ui.presenter.admin.importation.AddVariableImporationSchemePresenter;
import org.sigmah.client.ui.presenter.admin.importation.ImportationSchemeAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.AddBudgetSubFieldPresenter;
import org.sigmah.client.ui.presenter.admin.models.EditFlexibleElementAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.EditLayoutGroupAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.FlexibleElementsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.contact.AddContactModelAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.contact.ContactModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.AddImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.AddMatchingRuleImportationShemeModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportModelPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.orgunit.AddOrgUnitModelAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.orgunit.OrgUnitModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.AddProjectModelAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.EditPhaseModelAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.LogFrameModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.PhaseModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.ProjectModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.orgunits.AddOrgUnitAdminPresenter;
import org.sigmah.client.ui.presenter.admin.orgunits.MoveOrgUnitAdminPresenter;
import org.sigmah.client.ui.presenter.admin.orgunits.OrgUnitsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.users.PrivacyGroupEditPresenter;
import org.sigmah.client.ui.presenter.admin.users.ProfileEditPresenter;
import org.sigmah.client.ui.presenter.admin.users.UserEditPresenter;
import org.sigmah.client.ui.presenter.admin.users.UsersAdminPresenter;
import org.sigmah.client.ui.presenter.calendar.CalendarEventPresenter;
import org.sigmah.client.ui.presenter.calendar.CalendarPresenter;
import org.sigmah.client.ui.presenter.contact.ContactDetailsPresenter;
import org.sigmah.client.ui.presenter.contact.ContactHistoryPresenter;
import org.sigmah.client.ui.presenter.contact.ContactPresenter;
import org.sigmah.client.ui.presenter.contact.ContactRelationshipsPresenter;
import org.sigmah.client.ui.presenter.contact.dashboardlist.ContactsListWidget;
import org.sigmah.client.ui.presenter.contact.export.ExportContactsPresenter;
import org.sigmah.client.ui.presenter.contact.export.ExportContactsSettingPresenter;
import org.sigmah.client.ui.presenter.importation.ImportationPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitCalendarPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitDashboardPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitDetailsPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitReportsPresenter;
import org.sigmah.client.ui.presenter.password.ChangeOwnPasswordPresenter;
import org.sigmah.client.ui.presenter.password.LostPasswordPresenter;
import org.sigmah.client.ui.presenter.password.ResetPasswordPresenter;
import org.sigmah.client.ui.presenter.project.LinkedProjectPresenter;
import org.sigmah.client.ui.presenter.project.ProjectCalendarPresenter;
import org.sigmah.client.ui.presenter.project.ProjectDetailsPresenter;
import org.sigmah.client.ui.presenter.project.ProjectPresenter;
import org.sigmah.client.ui.presenter.project.ProjectReportsPresenter;
import org.sigmah.client.ui.presenter.project.ProjectTeamMembersPresenter;
import org.sigmah.client.ui.presenter.project.dashboard.PhasesPresenter;
import org.sigmah.client.ui.presenter.project.dashboard.ProjectDashboardPresenter;
import org.sigmah.client.ui.presenter.project.export.ExportProjectsPresenter;
import org.sigmah.client.ui.presenter.project.export.ExportProjectsSettingPresenter;
import org.sigmah.client.ui.presenter.project.indicator.EditIndicatorPresenter;
import org.sigmah.client.ui.presenter.project.indicator.EditSitePresenter;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorEntriesPresenter;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorManagementPresenter;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorMapPresenter;
import org.sigmah.client.ui.presenter.project.logframe.ProjectLogFramePresenter;
import org.sigmah.client.ui.presenter.project.projectcore.ProjectCoreDiffPresenter;
import org.sigmah.client.ui.presenter.project.projectcore.ProjectCoreRenameVersionPresenter;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.presenter.reminder.ReminderEditPresenter;
import org.sigmah.client.ui.presenter.reminder.ReminderHistoryPresenter;
import org.sigmah.client.ui.presenter.reports.AttachFilePresenter;
import org.sigmah.client.ui.presenter.reports.ReportCreatePresenter;
import org.sigmah.client.ui.presenter.reports.ReportsPresenter;
import org.sigmah.client.ui.presenter.zone.AppLoaderPresenter;
import org.sigmah.client.ui.presenter.zone.AuthenticationBannerPresenter;
import org.sigmah.client.ui.presenter.zone.MenuBannerPresenter;
import org.sigmah.client.ui.presenter.zone.MessageBannerPresenter;
import org.sigmah.client.ui.presenter.zone.OfflineBannerPresenter;
import org.sigmah.client.ui.presenter.zone.OrganizationBannerPresenter;
import org.sigmah.client.ui.theme.Theme;
import org.sigmah.client.ui.view.*;
import org.sigmah.client.ui.view.admin.AdminView;
import org.sigmah.client.ui.view.admin.CategoriesAdminView;
import org.sigmah.client.ui.view.admin.ParametersAdminView;
import org.sigmah.client.ui.view.admin.ReportModelsAdminView;
import org.sigmah.client.ui.view.admin.importation.AddImportationSchemeView;
import org.sigmah.client.ui.view.admin.importation.AddVariableImporationSchemeView;
import org.sigmah.client.ui.view.admin.importation.ImportationSchemeAdminView;
import org.sigmah.client.ui.view.admin.models.AddBudgetSubFieldView;
import org.sigmah.client.ui.view.admin.models.EditFlexibleElementAdminView;
import org.sigmah.client.ui.view.admin.models.EditLayoutGroupAdminView;
import org.sigmah.client.ui.view.admin.models.FlexibleElementsAdminView;
import org.sigmah.client.ui.view.admin.models.contact.AddContactModelAdminView;
import org.sigmah.client.ui.view.admin.models.contact.ContactModelsAdminView;
import org.sigmah.client.ui.view.admin.models.importer.AddImportationSchemeModelsAdminView;
import org.sigmah.client.ui.view.admin.models.importer.AddMatchingRuleImportationShemeModelsAdminView;
import org.sigmah.client.ui.view.admin.models.importer.ImportModelView;
import org.sigmah.client.ui.view.admin.models.importer.ImportationSchemeModelsAdminView;
import org.sigmah.client.ui.view.admin.models.orgunit.AddOrgUnitModelAdminView;
import org.sigmah.client.ui.view.admin.models.orgunit.OrgUnitModelsAdminView;
import org.sigmah.client.ui.view.admin.models.project.AddProjectModelAdminView;
import org.sigmah.client.ui.view.admin.models.project.EditPhaseModelAdminView;
import org.sigmah.client.ui.view.admin.models.project.LogFrameModelsAdminView;
import org.sigmah.client.ui.view.admin.models.project.PhaseModelsAdminView;
import org.sigmah.client.ui.view.admin.models.project.ProjectModelsAdminView;
import org.sigmah.client.ui.view.admin.orgunits.AddOrgUnitAdminView;
import org.sigmah.client.ui.view.admin.orgunits.MoveOrgUnitAdminView;
import org.sigmah.client.ui.view.admin.orgunits.OrgUnitsAdminView;
import org.sigmah.client.ui.view.admin.users.PrivacyGroupEditView;
import org.sigmah.client.ui.view.admin.users.ProfileEditView;
import org.sigmah.client.ui.view.admin.users.UserEditView;
import org.sigmah.client.ui.view.admin.users.UsersAdminView;
import org.sigmah.client.ui.view.calendar.CalendarEventView;
import org.sigmah.client.ui.view.calendar.CalendarView;
import org.sigmah.client.ui.view.contact.ContactDetailsView;
import org.sigmah.client.ui.view.contact.ContactHistoryView;
import org.sigmah.client.ui.view.contact.ContactRelationshipsView;
import org.sigmah.client.ui.view.contact.ContactView;
import org.sigmah.client.ui.view.contact.dashboardlist.ContactsListView;
import org.sigmah.client.ui.view.contact.export.ExportContactsSettingView;
import org.sigmah.client.ui.view.contact.export.ExportContactsView;
import org.sigmah.client.ui.view.importation.ImportationView;
import org.sigmah.client.ui.view.orgunit.OrgUnitCalendarView;
import org.sigmah.client.ui.view.orgunit.OrgUnitDashboardView;
import org.sigmah.client.ui.view.orgunit.OrgUnitDetailsView;
import org.sigmah.client.ui.view.orgunit.OrgUnitReportsView;
import org.sigmah.client.ui.view.orgunit.OrgUnitView;
import org.sigmah.client.ui.view.password.*;
import org.sigmah.client.ui.view.pivot.ProjectPivotContainer;
import org.sigmah.client.ui.view.pivot.table.PivotGridPanel;
import org.sigmah.client.ui.view.project.LinkedProjectView;
import org.sigmah.client.ui.view.project.ProjectCalendarView;
import org.sigmah.client.ui.view.project.ProjectDetailsView;
import org.sigmah.client.ui.view.project.ProjectReportsView;
import org.sigmah.client.ui.view.project.ProjectTeamMembersView;
import org.sigmah.client.ui.view.project.ProjectView;
import org.sigmah.client.ui.view.project.dashboard.PhasesView;
import org.sigmah.client.ui.view.project.dashboard.ProjectDashboardView;
import org.sigmah.client.ui.view.project.export.ExportProjectsSettingView;
import org.sigmah.client.ui.view.project.export.ExportProjectsView;
import org.sigmah.client.ui.view.project.indicator.EditIndicatorView;
import org.sigmah.client.ui.view.project.indicator.EditSiteView;
import org.sigmah.client.ui.view.project.indicator.ProjectIndicatorEntriesView;
import org.sigmah.client.ui.view.project.indicator.ProjectIndicatorManagementView;
import org.sigmah.client.ui.view.project.indicator.ProjectIndicatorMapView;
import org.sigmah.client.ui.view.project.indicator.SiteGridPanel;
import org.sigmah.client.ui.view.project.logframe.ProjectLogFrameGrid;
import org.sigmah.client.ui.view.project.logframe.ProjectLogFrameView;
import org.sigmah.client.ui.view.project.projectcore.ProjectCoreDiffView;
import org.sigmah.client.ui.view.project.projectcore.ProjectCoreRenameVersionView;
import org.sigmah.client.ui.view.project.treegrid.ProjectsListView;
import org.sigmah.client.ui.view.reminder.ReminderEditView;
import org.sigmah.client.ui.view.reminder.ReminderHistoryView;
import org.sigmah.client.ui.view.reports.*;
import org.sigmah.client.ui.view.zone.*;
import org.sigmah.client.util.ImageProvider;
import org.sigmah.offline.dao.*;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;
import org.sigmah.offline.handler.ActivityCalendarAsyncHandler;
import org.sigmah.offline.handler.BatchCommandAsyncHandler;
import org.sigmah.offline.handler.CreateEntityAsyncHandler;
import org.sigmah.offline.handler.DeleteAsyncHandler;
import org.sigmah.offline.handler.GetCalendarAsyncHandler;
import org.sigmah.offline.handler.GetCategoriesAsyncHandler;
import org.sigmah.offline.handler.GetCountriesAsyncHandler;
import org.sigmah.offline.handler.GetCountryAsyncHandler;
import org.sigmah.offline.handler.GetHistoryAsyncHandler;
import org.sigmah.offline.handler.GetLinkedProjectsAsyncHandler;
import org.sigmah.offline.handler.GetMonitoredPointsAsyncHandler;
import org.sigmah.offline.handler.GetOrgUnitAsyncHandler;
import org.sigmah.offline.handler.GetOrgUnitsAsyncHandler;
import org.sigmah.offline.handler.GetOrganizationAsyncHandler;
import org.sigmah.offline.handler.GetProfilesAsyncHandler;
import org.sigmah.offline.handler.GetProjectAsyncHandler;
import org.sigmah.offline.handler.GetProjectDocumentsAsyncHandler;
import org.sigmah.offline.handler.GetProjectReportAsyncHandler;
import org.sigmah.offline.handler.GetProjectReportsAsyncHandler;
import org.sigmah.offline.handler.GetProjectTeamMembersAsyncHandler;
import org.sigmah.offline.handler.GetProjectsAsyncHandler;
import org.sigmah.offline.handler.GetProjectsFromIdAsyncHandler;
import org.sigmah.offline.handler.GetRemindersAsyncHandler;
import org.sigmah.offline.handler.GetSitesCountAsyncHandler;
import org.sigmah.offline.handler.GetUserUnitsByUserAsyncHandler;
import org.sigmah.offline.handler.GetUsersByOrgUnitAsyncHandler;
import org.sigmah.offline.handler.GetUsersByOrganizationAsyncHandler;
import org.sigmah.offline.handler.GetValueAsyncHandler;
import org.sigmah.offline.handler.GetValueFromLinkedProjectsAsyncHandler;
import org.sigmah.offline.handler.MonitoredPointCalendarAsyncHandler;
import org.sigmah.offline.handler.PersonalCalendarAsyncHandler;
import org.sigmah.offline.handler.PrepareFileUploadAsyncHandler;
import org.sigmah.offline.handler.ReminderCalendarAsyncHandler;
import org.sigmah.offline.handler.SecureNavigationAsyncHandler;
import org.sigmah.offline.handler.UpdateEntityAsyncHandler;
import org.sigmah.offline.handler.UpdateLogFrameAsyncHandler;
import org.sigmah.offline.handler.UpdateMonitoredPointsAsyncHandler;
import org.sigmah.offline.handler.UpdateProjectAsyncHandler;
import org.sigmah.offline.handler.UpdateProjectFavoriteAsyncHandler;
import org.sigmah.offline.handler.UpdateProjectTeamMembersAsyncHandler;
import org.sigmah.offline.handler.UpdateRemindersAsyncHandler;
import org.sigmah.offline.presenter.FileSelectionPresenter;
import org.sigmah.offline.status.ApplicationStateManager;
import org.sigmah.offline.sync.Synchronizer;
import org.sigmah.offline.view.FileSelectionView;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.pivot.content.IStateManager;
import org.sigmah.shared.file.TransfertManager;
import org.sigmah.shared.file.TransfertManagerProvider;

public interface ClientFactory {
	
	
	EventBus getEventBus();

	DispatchAsync getDispatch();

	LocalDispatchServiceAsync getLocalDispatch();

	PageManager getPageManager();

	Theme getTheme();

	AuthenticationProvider getAuthenticationProvider();

	ApplicationStateManager getApplicationStateManager();
	
	IStateManager getIStateManager();

	TransfertManager getTransfertManager();

	Synchronizer getSynchronizer();

	UserLocalCache getClientCache();

	// --------------------------------------------------
	//
	// Asynchronous DAO (offline mode).
	//
	// --------------------------------------------------

	FileDataAsyncDAO getFileDataAsyncDAO();

	TransfertAsyncDAO getTransfertAsyncDAO();

	// --------------------------------------------------
	//
	// Presenters.
	//
	// --------------------------------------------------

	ApplicationPresenter getApplicationPresenter();

	// ---- Zones presenters.

	MockUpPresenter getMockUpPresenter();

	OrganizationBannerPresenter getOrganizationBannerPresenter();

	AuthenticationBannerPresenter getAuthenticationBannerPresenter();

	OfflineBannerPresenter getOfflineBannerPresenter();

	AppLoaderPresenter getAppLoaderPresenter();

	MenuBannerPresenter getMenuBannerPresenter();

	MessageBannerPresenter getMessageBannerPresenter();

	CreditsPresenter getCreditsPresenter();

	HelpPresenter getHelpPresenter();

	// ---- Pages presenters.

	DashboardPresenter getHomePresenter();

	LoginPresenter getLoginPresenter();

	LostPasswordPresenter getLostPasswordPresenter();

	ResetPasswordPresenter getResetPasswordPresenter();

	ChangeOwnPasswordPresenter getChangeOwnPasswordPresenter();

	CreateProjectPresenter getCreateProjectPresenter();

	CalendarEventPresenter getCalendarEventPresenter();

	ReportCreatePresenter getReportCreatePresenter();

	AttachFilePresenter getAttachFilePresenter();

	ImportationPresenter getImportationPresenter();

	// ---- Project presenters.

	ProjectPresenter getProjectPresenter();

	ProjectDashboardPresenter getProjectDashboardPresenter();

	ProjectLogFramePresenter getProjectLogFramePresenter();

	ProjectDetailsPresenter getProjectDetailsPresenter();

	ProjectTeamMembersPresenter getProjectTeamMembersPresenter();

	ProjectCalendarPresenter getProjectCalendarPresenter();

	ProjectReportsPresenter getProjectReportsPresenter();

	ReminderEditPresenter getReminderEditPresenter();

	ReminderHistoryPresenter getReminderHistoryPresenter();

	LinkedProjectPresenter getLinkedProjectPresenter();

	ProjectIndicatorEntriesPresenter getProjectIndicatorEntriesPresenter();

	ProjectIndicatorManagementPresenter getProjectIndicatorManagementPresenter();

	ProjectIndicatorMapPresenter getProjectIndicatorMapPresenter();

	EditIndicatorPresenter getEditIndicatorPresenter();

	EditSitePresenter getEditSitePresenter();

	ExportContactsPresenter getExportContactsPresenter();

	ExportContactsSettingPresenter getExportContactsSettingPresenter();

	ExportProjectsPresenter getExportProjectsPresenter();

	ExportProjectsSettingPresenter getExportProjectsSettingPresenter();

	ProjectCoreRenameVersionPresenter getProjectCoreRenameVersionPresenter();

	ProjectCoreDiffPresenter getProjectCoreDiffPresenter();

	// ---- OrgUnit presenters.

	OrgUnitPresenter getOrgUnitPresenter();

	OrgUnitDashboardPresenter getOrgUnitDashboardPresenter();

	OrgUnitDetailsPresenter getOrgUnitDetailsPresenter();

	OrgUnitCalendarPresenter getOrgUnitCalendarPresenter();

	OrgUnitReportsPresenter getOrgUnitReportsPresenter();

	// ---- Contact presenters

	ContactPresenter getContactPresenter();

	ContactDetailsPresenter getContactDetailsPresenter();

	ContactRelationshipsPresenter getContactRelationshipsPresenter();

	ContactHistoryPresenter getContactHistoryPresenter();

	// ---- Admin presenters

	AdminPresenter getAdminPresenter();

	ParametersAdminPresenter getParametersAdminPresenter();

	UsersAdminPresenter getUsersAdminPresenter();

	PrivacyGroupEditPresenter getPrivacyGroupEditPresenter();

	ProfileEditPresenter getProfileEditPresenter();

	UserEditPresenter getUserEditPresenter();

	OrgUnitsAdminPresenter getOrgUnitsAdminPresenter();

	AddOrgUnitAdminPresenter getAddOrgUnitAdminPresenter();

	MoveOrgUnitAdminPresenter getMoveOrgUnitAdminPresenter();

	CategoriesAdminPresenter getCategoriesAdminPresenter();

	OrgUnitModelsAdminPresenter getOrgUnitModelsAdminPresenter();

	AddOrgUnitModelAdminPresenter getAddOrgUnitModelAdminPresenter();

	ProjectModelsAdminPresenter getProjectModelsAdminPresenter();

	AddProjectModelAdminPresenter getAddProjectModelAdminPresenter();

	ContactModelsAdminPresenter getContactModelsAdminPresenter();

	AddContactModelAdminPresenter getAddContactModelAdminPresenter();

	AddImportationSchemePresenter getAddImportationSchemePresenter();

	ReportModelsAdminPresenter getReportModelsAdminPresenter();

	EditPhaseModelAdminPresenter getEditPhaseModelAdminPresenter();

	EditLayoutGroupAdminPresenter getEditLayoutGroupAdminPresenter();

	EditFlexibleElementAdminPresenter getEditFlexibleElementAdminPresenter();

	AddBudgetSubFieldPresenter getAddBudgetSubFieldPresenter();

	ImportModelPresenter getImportModelPresenter();

	ImportationSchemeAdminPresenter getImportationShemePresenter();

	AddVariableImporationSchemePresenter getAddVariableImporationSchemePresenter();

	AddImportationSchemeModelsAdminPresenter getAddImportationSchemeModelsAdminPresenter();

	AddMatchingRuleImportationShemeModelsAdminPresenter getAddMatchingRuleImportationShemeModelsAdminPresenter();

	// ---- Offline presenters

	FileSelectionPresenter getFileSelectionPresenter();
	
	UpdateDiaryAsyncDAO getUpdateDiaryAsyncDAO();
	
	TransfertManagerProvider getTransferManagerProvider();
	
	MonitoredPointAsyncDAO getMonitoredPointAsyncDAO();
	
	OrgUnitAsyncDAO getOrgUnitAsyncDAO();
	
	ReminderAsyncDAO getReminderAsyncDAO();
	
	ApplicationView getApplicationView();
	
	MockUpView getMockUpView();
	
	OrganizationBannerView getOrganizationBannerView();
	
	AuthenticationBannerView getAuthenticationBannerView();
	
	ImageProvider getImageProvider();
	
	LogoAsyncDAO getLogoAsyncDAO();
	
	OfflineBannerView getOfflineBannerView();
	
	AppLoaderView getAppLoaderView();
	
	MenuBannerView getMenuBannerView();
	
	MessageBannerView getMessageBannerView();
	
    CreditsView getCreditsView();
    
    HelpView getHelpView();
    
    ContactsListWidget getContactsListWidget();
    
    ProjectsListWidget getProjectsListWidget();
    
    ContactsListView getContactsListView();
    
    ProjectsListView getProjectsListView();
    
    DashboardView getDashboardView();
    
    LoginView getLoginView();
    
    LostPasswordView getLostPasswordView();
    
    ResetPasswordView getResetPasswordView();
    
    ChangeOwnPasswordView getChangeOwnPasswordView();
    
    CreateProjectView getCreateProjectView();
    
    CalendarEventView getCalendarEventView();
    
    ReportCreateView getReportCreateView();
    
    AttachFileView getAttachFileView();
    
    ImportationView getImportationView();
    
    ProjectView getProjectView();
    
    ProjectDashboardView getProjectDashboardView();

    ProjectLogFrameView getProjectLogFrameView();

    ProjectDetailsView getProjectDetailsView();

    ProjectTeamMembersView getProjectTeamMembersView();

    ProjectCalendarView getProjectCalendarView();

    ProjectReportsView getProjectReportsView();

    ReminderEditView getReminderEditView();

    ReminderHistoryView getReminderHistoryView();

    LinkedProjectView getLinkedProjectView();

    ProjectIndicatorEntriesView getProjectIndicatorEntriesView();

    ProjectIndicatorManagementView getProjectIndicatorManagementView();

    ProjectIndicatorMapView getProjectIndicatorMapView();

    EditIndicatorView getEditIndicatorView();

    EditSiteView getEditSiteView();

    ExportContactsView getExportContactsView();

    ExportContactsSettingView getExportContactsSettingView();

    ExportProjectsView getExportProjectsView();

    ExportProjectsSettingView getExportProjectsSettingView();

    ProjectCoreRenameVersionView getProjectCoreRenameVersionView();

    ProjectCoreDiffView getProjectCoreDiffView();

    OrgUnitView getOrgUnitView();

    OrgUnitDashboardView getOrgUnitDashboardView();

    OrgUnitDetailsView getOrgUnitDetailsView();

    OrgUnitCalendarView getOrgUnitCalendarView();

    OrgUnitReportsView getOrgUnitReportsView();

    ContactView getContactView();

    ContactDetailsView getContactDetailsView();

    ContactRelationshipsView getContactRelationshipsView();

    ContactHistoryView getContactHistoryView();

    AdminView getAdminView();

    ParametersAdminView getParametersAdminView();

    UsersAdminView getUsersAdminView();

    PrivacyGroupEditView getPrivacyGroupEditView();

    ProfileEditView getProfileEditView();

    UserEditView getUserEditView();

    OrgUnitsAdminView getOrgUnitsAdminView();

    AddOrgUnitAdminView getAddOrgUnitAdminView();

    MoveOrgUnitAdminView getMoveOrgUnitAdminView();

    CategoriesAdminView getCategoriesAdminView();

    OrgUnitModelsAdminView getOrgUnitModelsAdminView();

    AddOrgUnitModelAdminView getAddOrgUnitModelAdminView();

    ProjectModelsAdminView getProjectModelsAdminView();

    AddProjectModelAdminView getAddProjectModelAdminView();

    ContactModelsAdminView getContactModelsAdminView();

    AddContactModelAdminView getAddContactModelAdminView();

    AddImportationSchemeView getAddImportationSchemeView();

    ReportModelsAdminView getReportModelsAdminView();

    EditPhaseModelAdminView getEditPhaseModelAdminView();

    EditLayoutGroupAdminView getEditLayoutGroupAdminView();

    EditFlexibleElementAdminView getEditFlexibleElementAdminView();

    AddBudgetSubFieldView getAddBudSubFieldView();

    ImportModelView getImportModelView();

    ImportationSchemeAdminView getImportationSchemeAdminView();

    AddVariableImporationSchemeView getAddVariableImporationSchemeView();

    AddImportationSchemeModelsAdminView getAddImportationSchemeModelsAdminView();

    AddMatchingRuleImportationShemeModelsAdminView getAddMatchingRuleImportationShemeModelsAdminView();

    FileSelectionView getFileSelectionView();
    
    PhasesPresenter getPhasesPresenter();
    
    PhasesView getPhasesView();
    
    ProjectLogFrameGrid getProjectLogFrameGrid();
    
    ProjectPivotContainer getProjectPivotContainer();
    
    PivotGridPanel getPivotGridPanel();
    
    SiteGridPanel getSiteGridPanel();
    
    ComputationTriggerManager getComputationTriggerManager();
    
    ClientValueResolver getClientValueResolver();
    
    ReportsPresenter getReportsPresenter();
    
    ReportsView getReportsView();
    
    CalendarPresenter getCalendarPresenter();
    
    CalendarView getCalendarView();
    
    FlexibleElementsAdminPresenter<OrgUnitModelDTO> getFlexibleElementsAdminPresenter();
    
    FlexibleElementsAdminPresenter<ProjectModelDTO> getFlexibleElementsAdminPresenter2(); 
    
    FlexibleElementsAdminPresenter<ContactModelDTO> getFlexibleElementsAdminPresenter3(); 
    
    FlexibleElementsAdminView getFlexibleElementsAdminView();
    
    ImportationSchemeModelsAdminView getImportationSchemeModelsAdminView();
    
    ImportationSchemeModelsAdminPresenter<OrgUnitModelDTO> getImportationSchemeModelsAdminPresenter();
    
    ImportationSchemeModelsAdminPresenter<ProjectModelDTO> getImportationSchemeModelsAdminPresenter2();
    
    PhaseModelsAdminPresenter getPhaseModelsAdminPresenter();
    
    PhaseModelsAdminView getPhaseModelsAdminView();
    
    LogFrameModelsAdminPresenter getLogFrameModelsAdminPresenter();
    
    LogFrameModelsAdminView getLogFrameModelsAdminView();
    
    ProjectAsyncDAO getProjectAsyncDAO();
    
    ProjectModelAsyncDAO getProjectModelAsyncDAO();
    
    PhaseAsyncDAO getPhaseAsyncDAO();
    
    LogFrameAsyncDAO getLogFrameAsyncDAO();
    
    ValueAsyncDAO getValueAsyncDAO();
    
    OrgUnitModelAsyncDAO getOrgUnitModelAsyncDAO();
    
    CountryAsyncDAO getCountryAsyncDAO();
    
    PhaseModelAsyncDAO getPhaseModelAsyncDAO();
    
    ComputationAsyncDAO getComputationAsyncDAO();
    
    BatchCommandAsyncHandler getBatchCommandAsyncHandler();

    CreateEntityAsyncHandler getCreateEntityAsyncHandler();

    DeleteAsyncHandler getDeleteAsyncHandler();

    GetCalendarAsyncHandler getGetCalendarAsyncHandler();

    GetCategoriesAsyncHandler getGetCategoriesAsyncHandler();

    GetCountriesAsyncHandler getGetCountriesAsyncHandler();

    GetCountryAsyncHandler getGetCountryAsyncHandler();

    GetHistoryAsyncHandler getGetHistoryAsyncHandler();

    GetLinkedProjectsAsyncHandler getGetLinkedProjectsAsyncHandler();

    GetMonitoredPointsAsyncHandler getGetMonitoredPointsAsyncHandler();

    GetOrganizationAsyncHandler getGetOrganizationAsyncHandler();

    GetOrgUnitAsyncHandler getGetOrgUnitAsyncHandler();

    GetOrgUnitsAsyncHandler getGetOrgUnitsAsyncHandler();

    GetProfilesAsyncHandler getGetProfilesAsyncHandler();

    GetProjectAsyncHandler getGetProjectAsyncHandler();

    GetProjectsAsyncHandler getGetProjectsAsyncHandler();

    GetProjectsFromIdAsyncHandler getGetProjectsFromIdAsyncHandler();

    GetProjectDocumentsAsyncHandler getGetProjectDocumentsAsyncHandler();

    GetProjectReportAsyncHandler getGetProjectReportAsyncHandler();

    GetProjectReportsAsyncHandler getGetProjectReportsAsyncHandler();

    GetProjectTeamMembersAsyncHandler getGetProjectTeamMembersAsyncHandler();

    GetRemindersAsyncHandler getGetRemindersAsyncHandler();

    GetSitesCountAsyncHandler getGetSitesCountAsyncHandler();

    GetUsersByOrganizationAsyncHandler getGetUsersByOrganizationAsyncHandler();

    GetUsersByOrgUnitAsyncHandler getGetUsersByOrgUnitAsyncHandler();

    GetUserUnitsByUserAsyncHandler getGetUserUnitsByUserAsyncHandler();

    GetValueAsyncHandler getGetValueAsyncHandler();

    GetValueFromLinkedProjectsAsyncHandler getGetValueFromLinkedProjectsAsyncHandler();

    PrepareFileUploadAsyncHandler getPrepareFileUploadAsyncHandler();

    SecureNavigationAsyncHandler getSecureNavigationAsyncHandler();

    UpdateEntityAsyncHandler getUpdateEntityAsyncHandler();

    UpdateLogFrameAsyncHandler getUpdateLogFrameAsyncHandler();
    
    PersonalCalendarAsyncDAO getPersonalCalendarAsyncDAO();

    UpdateMonitoredPointsAsyncHandler getUpdateMonitoredPointsAsyncHandler();

    UpdateProjectAsyncHandler getUpdateProjectAsyncHandler();

    UpdateProjectFavoriteAsyncHandler getUpdateProjectFavoriteAsyncHandler();

    UpdateProjectTeamMembersAsyncHandler getUpdateProjectTeamMembersAsyncHandler();

    UpdateRemindersAsyncHandler getUpdateRemindersAsyncHandler();
    
    ActivityCalendarAsyncHandler getActivityCalendarAsyncHandler();

    PersonalCalendarAsyncHandler getPersonalCalendarAsyncHandler();

    MonitoredPointCalendarAsyncHandler getMonitoredPointCalendarAsyncHandler();

    ReminderCalendarAsyncHandler getReminderCalendarAsyncHandler();
    
    CategoryTypeAsyncDAO getCategoryTypeAsyncDAO();
    
    HistoryAsyncDAO getHistoryAsyncDAO();
    
    OrganizationAsyncDAO getOrganizationAsyncDAO();
    
    ProfileAsyncDAO getProfileAsyncDAO();
    
    UserAsyncDAO getUserAsyncDAO();
    
    UserUnitAsyncDAO getUserUnitAsyncDAO();
    
    AuthenticationAsyncDAO getAuthenticationAsyncDAO();

    PageAccessAsyncDAO getPageAccessAsyncDAO();
    
    ProjectReportAsyncDAO getProjectReportAsyncDAO();
    
    ProjectTeamMembersAsyncDAO getProjectTeamMembersAsyncDAO();
    
    ReportReferenceAsyncDAO getReportReferenceAsyncDAO();
    
    CategoryElementAsyncDAO getCategoryElementAsyncDAO();
    
    ExceptionHandler getExceptionHandler();
}
