package org.sigmah.client;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.computation.ClientValueResolver;
import org.sigmah.client.computation.ComputationTriggerManager;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.ExceptionHandler;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.EventBusImpl;
import org.sigmah.client.page.PageManager;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.security.SecureDispatchAsync;
import org.sigmah.client.security.SecureExceptionHandler;
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
import org.sigmah.client.ui.theme.SigmahTheme;
import org.sigmah.client.ui.theme.Theme;
import org.sigmah.client.ui.view.ApplicationView;
import org.sigmah.client.ui.view.CreateProjectView;
import org.sigmah.client.ui.view.CreditsView;
import org.sigmah.client.ui.view.DashboardView;
import org.sigmah.client.ui.view.HelpView;
import org.sigmah.client.ui.view.LoginView;
import org.sigmah.client.ui.view.MockUpView;
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
import org.sigmah.client.ui.view.password.ChangeOwnPasswordView;
import org.sigmah.client.ui.view.password.LostPasswordView;
import org.sigmah.client.ui.view.password.ResetPasswordView;
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
import org.sigmah.client.ui.view.reports.AttachFileView;
import org.sigmah.client.ui.view.reports.ReportCreateView;
import org.sigmah.client.ui.view.reports.ReportsView;
import org.sigmah.client.ui.view.zone.AppLoaderView;
import org.sigmah.client.ui.view.zone.AuthenticationBannerView;
import org.sigmah.client.ui.view.zone.MenuBannerView;
import org.sigmah.client.ui.view.zone.MessageBannerView;
import org.sigmah.client.ui.view.zone.OfflineBannerView;
import org.sigmah.client.ui.view.zone.OrganizationBannerView;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ImageProvider;
import org.sigmah.offline.dao.AuthenticationAsyncDAO;
import org.sigmah.offline.dao.CategoryElementAsyncDAO;
import org.sigmah.offline.dao.CategoryTypeAsyncDAO;
import org.sigmah.offline.dao.ComputationAsyncDAO;
import org.sigmah.offline.dao.CountryAsyncDAO;
import org.sigmah.offline.dao.FileDataAsyncDAO;
import org.sigmah.offline.dao.HistoryAsyncDAO;
import org.sigmah.offline.dao.LogFrameAsyncDAO;
import org.sigmah.offline.dao.LogoAsyncDAO;
import org.sigmah.offline.dao.MonitoredPointAsyncDAO;
import org.sigmah.offline.dao.OrgUnitAsyncDAO;
import org.sigmah.offline.dao.OrgUnitModelAsyncDAO;
import org.sigmah.offline.dao.OrganizationAsyncDAO;
import org.sigmah.offline.dao.PageAccessAsyncDAO;
import org.sigmah.offline.dao.PersonalCalendarAsyncDAO;
import org.sigmah.offline.dao.PhaseAsyncDAO;
import org.sigmah.offline.dao.PhaseModelAsyncDAO;
import org.sigmah.offline.dao.ProfileAsyncDAO;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.ProjectModelAsyncDAO;
import org.sigmah.offline.dao.ProjectReportAsyncDAO;
import org.sigmah.offline.dao.ProjectTeamMembersAsyncDAO;
import org.sigmah.offline.dao.ReminderAsyncDAO;
import org.sigmah.offline.dao.ReportReferenceAsyncDAO;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dao.UserAsyncDAO;
import org.sigmah.offline.dao.UserUnitAsyncDAO;
import org.sigmah.offline.dao.ValueAsyncDAO;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;
import org.sigmah.offline.fileapi.FileReader;
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
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.presenter.FileSelectionPresenter;
import org.sigmah.offline.status.ApplicationStateManager;
import org.sigmah.offline.sync.Synchronizer;
import org.sigmah.offline.view.FileSelectionView;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.pivot.content.GXTStateManager;
import org.sigmah.shared.dto.pivot.content.IStateManager;
import org.sigmah.shared.file.DirectTransfertManager;
import org.sigmah.shared.file.Html5TransfertManager;
import org.sigmah.shared.file.TransfertManager;
import org.sigmah.shared.file.TransfertManagerProvider;

import com.google.gwt.core.client.GWT;

public class ClientFactoryImpl implements ClientFactory {

	EventBus eventBus;

	DispatchAsync dispatch;

	LocalDispatchServiceAsync localdispatchAsynch;

	PageManager pageManeger;

	Theme theme;

	AuthenticationProvider authenticationProvider;

	ApplicationStateManager applicationStateManager;

	IStateManager iStateManager;

	TransfertManager TransferManager;
	
	public static boolean html5EngineActive = GWT.isProdMode() && (ClientUtils.isFF() || ClientUtils.isSafari() || ClientUtils.isIE10() || ClientUtils.isIE11());

	Synchronizer synchronizer;

	UserLocalCache userLocalCache;
	
	ExceptionHandler exceptionHandler;

	// --------------------------------------------------
	//
	// Asynchronous DAO (offline mode).
	//
	// --------------------------------------------------

	FileDataAsyncDAO fileDataAsyncDAO;

	TransfertAsyncDAO transfertAsyncDAO;

	// --------------------------------------------------
	//
	// Presenters.
	//
	// --------------------------------------------------

	ApplicationPresenter applicationPresenter;

	// ---- Zones presenters.

	MockUpPresenter mockUpPresenter;

	OrganizationBannerPresenter organizationBannerPresenter;

	AuthenticationBannerPresenter authenticationBannerPresenter;

	OfflineBannerPresenter offlineBannerPresenter;

	AppLoaderPresenter appLoaderPresenter;

	MenuBannerPresenter menuBannerPresenter;

	MessageBannerPresenter messageBannerPresenter;

	CreditsPresenter creditsPresenter;

	HelpPresenter helpPresenter;

	// ---- Pages presenters.

	DashboardPresenter homePresenter;

	LoginPresenter loginPresenter;

	LostPasswordPresenter lostPasswordPresenter;

	ResetPasswordPresenter resetPasswordPresenter;

	ChangeOwnPasswordPresenter changeOwnPasswordPresenter;

	CreateProjectPresenter createProjectPresenter;

	CalendarEventPresenter calendarEventPresenter;

	ReportCreatePresenter reportCreatePresenter;

	AttachFilePresenter attachFilePresenter;

	ImportationPresenter importationPresenter;

	// ---- Project presenters.

	ProjectPresenter projectPresenter;

	ProjectDashboardPresenter projectDashboardPresenter;

	ProjectLogFramePresenter projectLogFramePresenter;

	ProjectDetailsPresenter projectDetailsPresenter;

	ProjectTeamMembersPresenter projectTeamMembersPresenter;

	ProjectCalendarPresenter projectCalendarPresenter;

	ProjectReportsPresenter projectReportsPresenter;

	ReminderEditPresenter reminderEditPresenter;

	ReminderHistoryPresenter reminderHistoryPresenter;

	LinkedProjectPresenter linkedProjectPresenter;

	ProjectIndicatorEntriesPresenter projectIndicatorEntriesPresenter;

	ProjectIndicatorManagementPresenter projectIndicatorManagementPresenter;

	ProjectIndicatorMapPresenter projectIndicatorMapPresenter;

	EditIndicatorPresenter editIndicatorPresenter;

	EditSitePresenter editSitePresenter;

	ExportContactsPresenter exportContactsPresenter;

	ExportContactsSettingPresenter exportContactsSettingPresenter;

	ExportProjectsPresenter exportProjectsPresenter;

	ExportProjectsSettingPresenter exportProjectsSettingPresenter;

	ProjectCoreRenameVersionPresenter projectCoreRenameVersionPresenter;

	ProjectCoreDiffPresenter projectCoreDiffPresenter;

	// ---- OrgUnit presenters.

	OrgUnitPresenter orgUnitPresenter;

	OrgUnitDashboardPresenter orgUnitDashboradPresenter;

	OrgUnitDetailsPresenter orgUnitDetailsPresenter;

	OrgUnitCalendarPresenter orgUnitCalendarPresenter;

	OrgUnitReportsPresenter orgUnitReportsPresenter;
	
	OrgUnitAsyncDAO orgUnitAsyncDAO;

	// ---- Contact presenters

	ContactPresenter contactPresenter;

	ContactDetailsPresenter contactDetailsPresenter;

	ContactRelationshipsPresenter contactRelationshipsPresenter;

	ContactHistoryPresenter ContactHistoryPresenter;

	// ---- Admin presenters

	AdminPresenter adminPresenter;

	ParametersAdminPresenter parametersAdminPresenter;

	UsersAdminPresenter usersAdminPresenter;

	PrivacyGroupEditPresenter privacyGroupEditPresenter;

	ProfileEditPresenter profileEditPresenter;

	UserEditPresenter userEditPresenter;

	OrgUnitsAdminPresenter orgUnitsAdminPresenter;

	AddOrgUnitAdminPresenter addOrgUnitAdminPresenter;

	MoveOrgUnitAdminPresenter moveOrgUnitAdminPresenter;

	CategoriesAdminPresenter categoriesAdminPresenter;

	OrgUnitModelsAdminPresenter orgUnitModelsAdminPresenter;

	AddOrgUnitModelAdminPresenter addOrgUnitModelAdminPresenter;

	ProjectModelsAdminPresenter projectModelsAdminPresenter;

	AddProjectModelAdminPresenter addProjectModelAdminPresenter;

	ContactModelsAdminPresenter contactModelsAdminPresenter;

	AddContactModelAdminPresenter addContactModelAdminPresenter;

	AddImportationSchemePresenter addImportationSchemePresenter;

	ReportModelsAdminPresenter reportModelsAdminPresenter;

	EditPhaseModelAdminPresenter editPhaseModelAdminPresenter;

	EditLayoutGroupAdminPresenter editLayoutGroupAdminPresenter;

	EditFlexibleElementAdminPresenter editFlexibleElementAdminPresenter;

	AddBudgetSubFieldPresenter addBudSubFieldPresenter;

	ImportModelPresenter importModelPresenter;

	ImportationSchemeAdminPresenter importationShemePresenter;

	AddVariableImporationSchemePresenter addVariableImporationSchemePresenter;

	AddImportationSchemeModelsAdminPresenter addImportationSchemeModelsAdminPresenter;

	AddMatchingRuleImportationShemeModelsAdminPresenter addMatchingRuleImportationShemeModelsAdminPresenter;

	UpdateDiaryAsyncDAO updateDiaryAsyncDAO;

	// ---- Offline presenters

	FileSelectionPresenter fileSelectionPresenter;

	TransfertManagerProvider transferManagerProvider;

	MonitoredPointAsyncDAO monitoredPointAsyncDAO;

	ReminderAsyncDAO reminderAsyncDAO;

	ImageProvider provider;

	LogoAsyncDAO logoAsyncDAO;

	AuthenticationBannerView authenticationBannerView;

	//// Views
	ProjectDashboardView projectDashboardView;

	ProjectLogFrameView projectLogFrameView;

	ProjectDetailsView projectDetailsView;

	ProjectTeamMembersView projectTeamMembersView;

	ProjectCalendarView projectCalendarView;

	ProjectReportsView projectReportsView;

	ReminderEditView reminderEditView;

	ReminderHistoryView reminderHistoryView;

	LinkedProjectView linkedProjectView;

	ProjectIndicatorEntriesView projectIndicatorEntriesView;

	ProjectIndicatorManagementView projectIndicatorManagementView;

	ProjectIndicatorMapView projectIndicatorMapView;

	EditIndicatorView editIndicatorView;

	EditSiteView editSiteView;

	ExportContactsView exportContactsView;

	ExportContactsSettingView exportContactsSettingView;

	ExportProjectsView exportProjectsView;

	ExportProjectsSettingView exportProjectsSettingView;

	ProjectCoreRenameVersionView projectCoreRenameVersionView;

	ProjectCoreDiffView projectCoreDiffView;

	OrgUnitView orgUnitView;

	OrgUnitDashboardView orgUnitDashboardView;

	OrgUnitDetailsView orgUnitDetailsView;

	OrgUnitCalendarView orgUnitCalendarView;

	OrgUnitReportsView orgUnitReportsView;

	ContactView contactView;

	ContactDetailsView contactDetailsView;

	ContactRelationshipsView contactRelationshipsView;

	ContactHistoryView contactHistoryView;

	AdminView adminView;

	ParametersAdminView parametersAdminView;

	UsersAdminView usersAdminView;

	PrivacyGroupEditView privacyGroupEditView;

	ProfileEditView profileEditView;

	UserEditView userEditView;

	OrgUnitsAdminView orgUnitsAdminView;

	AddOrgUnitAdminView addOrgUnitAdminView;

	MoveOrgUnitAdminView moveOrgUnitAdminView;

	CategoriesAdminView categoriesAdminView;

	OrgUnitModelsAdminView orgUnitModelsAdminView;

	AddOrgUnitModelAdminView addOrgUnitModelAdminView;

	ProjectModelsAdminView projectModelsAdminView;

	AddProjectModelAdminView addProjectModelAdminView;

	ContactModelsAdminView contactModelsAdminView;

	AddContactModelAdminView addContactModelAdminView;

	AddImportationSchemeView addImportationSchemeView;

	ReportModelsAdminView reportModelsAdminView;

	EditPhaseModelAdminView editPhaseModelAdminView;

	EditLayoutGroupAdminView editLayoutGroupAdminView;

	EditFlexibleElementAdminView editFlexibleElementAdminView;

	AddBudgetSubFieldView addBudSubFieldView;

	ImportModelView importModelView;

	ImportationSchemeAdminView importationSchemeAdminView;

	AddVariableImporationSchemeView addVariableImporationSchemeView;

	AddImportationSchemeModelsAdminView addImportationSchemeModelsAdminView;

	AddMatchingRuleImportationShemeModelsAdminView addMatchingRuleImportationShemeModelsAdminView;

	ProjectLogFrameGrid projectLogFrameGrid;

	FileSelectionView fileSelectionView;

	ProjectPivotContainer projectPivotContainer;

	PivotGridPanel pivotGridPanel;

	SiteGridPanel siteGridPanel;

	ComputationTriggerManager computationTriggerManager;

	ClientValueResolver clientValueResolver;

	ReportsPresenter reportsPresenter;

	ReportsView reportsView;
	
	CalendarPresenter calendarPresenter;
	
	CalendarView calendarView;
	
	FlexibleElementsAdminPresenter<OrgUnitModelDTO> flexibleElementsAdminPresenter;
	
	FlexibleElementsAdminPresenter<ProjectModelDTO> flexibleElementsAdminPresenter2;
	
	FlexibleElementsAdminView flexibleElementsAdminView;
	
	 ImportationSchemeModelsAdminView importationSchemeModelsAdminView;
	    
	ImportationSchemeModelsAdminPresenter<OrgUnitModelDTO> importationSchemeModelsAdminPresenter;
	
	PhaseModelsAdminPresenter phaseModelsAdminPresenter;
	
	PhaseModelsAdminView phaseModelsAdminView;
	
	LogFrameModelsAdminPresenter logFrameModelsAdminPresenter;
	 
	LogFrameModelsAdminView logFrameModelsAdminView;
	
	ImportationSchemeModelsAdminPresenter<ProjectModelDTO> importationSchemeModelsAdminPresenter2;
	
	FlexibleElementsAdminPresenter<ContactModelDTO> flexibleElementsAdminPresenter3;
	
	ProjectAsyncDAO projectAsyncDAO;
	
	ApplicationView applicationView;
	
	MockUpView mockupView;
	
	OrganizationBannerView organizationBannerView;
	
	ImageProvider imageProvider;
	
	MessageBannerView messageBannerView;

	CreditsView creditsView;

	HelpView helpView;

	ContactsListView contactsListView;

	ProjectsListView projectsListView;

	DashboardView dashboardView;

	LoginView loginView;

	LostPasswordView lostPasswordView;

	ResetPasswordView resetPasswordView;

	ChangeOwnPasswordView changeOwnPasswordView;

	CreateProjectView createProjectView;

	CalendarEventView calendarEventView;

	ReportCreateView reportCreateView;

	AttachFileView attachFileView;

	ImportationView importationView;

	ProjectView projectView;

	ProjectModelAsyncDAO projectModelAsyncDAO;

	PhaseAsyncDAO phaseAsyncDAO;

	LogFrameAsyncDAO logFrameAsyncDAO;

	ValueAsyncDAO valueAsyncDAO;

	OrgUnitModelAsyncDAO orgUnitModelAsyncDAO;

	CountryAsyncDAO countryAsyncDAO;
	
	OfflineBannerView offlineBannerView;
	
	AppLoaderView appLoaderView;
	
	MenuBannerView menuBannerView;
	
	PhaseModelAsyncDAO phaseModelAsyncDAO;
	
	ComputationAsyncDAO computationAsyncDAO;
	
	BatchCommandAsyncHandler batchCommandAsyncHandler;

	CreateEntityAsyncHandler createEntityAsyncHandler;

	DeleteAsyncHandler deleteAsyncHandler;

	GetCalendarAsyncHandler getCalendarAsyncHandler;

	GetCategoriesAsyncHandler getCategoriesAsyncHandler;

	GetCountriesAsyncHandler getCountriesAsyncHandler;

	GetCountryAsyncHandler getCountryAsyncHandler;

	GetHistoryAsyncHandler getHistoryAsyncHandler;

	GetLinkedProjectsAsyncHandler getLinkedProjectsAsyncHandler;

	GetMonitoredPointsAsyncHandler getMonitoredPointsAsyncHandler;

	GetOrganizationAsyncHandler getOrganizationAsyncHandler;

	GetOrgUnitAsyncHandler getOrgUnitAsyncHandler;

	GetOrgUnitsAsyncHandler getOrgUnitsAsyncHandler;

	GetProfilesAsyncHandler getProfilesAsyncHandler;

	GetProjectAsyncHandler getProjectAsyncHandler;

	GetProjectsAsyncHandler getProjectsAsyncHandler;

	GetProjectsFromIdAsyncHandler getProjectsFromIdAsyncHandler;

	GetProjectDocumentsAsyncHandler getProjectDocumentsAsyncHandler;

	GetProjectReportAsyncHandler getProjectReportAsyncHandler;

	GetProjectReportsAsyncHandler getProjectReportsAsyncHandler;

	GetProjectTeamMembersAsyncHandler getProjectTeamMembersAsyncHandler;

	GetRemindersAsyncHandler getRemindersAsyncHandler;

	GetSitesCountAsyncHandler getSitesCountAsyncHandler;

	GetUsersByOrganizationAsyncHandler getUsersByOrganizationAsyncHandler;

	GetUsersByOrgUnitAsyncHandler getUsersByOrgUnitAsyncHandler;

	GetUserUnitsByUserAsyncHandler getUserUnitsByUserAsyncHandler;

	GetValueAsyncHandler getValueAsyncHandler;

	GetValueFromLinkedProjectsAsyncHandler getValueFromLinkedProjectsAsyncHandler;

	PrepareFileUploadAsyncHandler prepareFileUploadAsyncHandler;

	SecureNavigationAsyncHandler secureNavigationAsyncHandler;

	UpdateEntityAsyncHandler updateEntityAsyncHandler;

	UpdateLogFrameAsyncHandler updateLogFrameAsyncHandler;

	UpdateMonitoredPointsAsyncHandler updateMonitoredPointsAsyncHandler;

	UpdateProjectAsyncHandler updateProjectAsyncHandler;

	UpdateProjectFavoriteAsyncHandler updateProjectFavoriteAsyncHandler;

	UpdateProjectTeamMembersAsyncHandler updateProjectTeamMembersAsyncHandler;

	UpdateRemindersAsyncHandler updateRemindersAsyncHandler;
	
	PersonalCalendarAsyncDAO personalCalendarAsyncDAO;
	

	 ActivityCalendarAsyncHandler activityCalendarAsyncHandler; 

	
	 PersonalCalendarAsyncHandler personalCalendarAsyncHandler; 

	
	 MonitoredPointCalendarAsyncHandler monitoredPointCalendarAsyncHandler; 

	
	 ReminderCalendarAsyncHandler reminderCalendarAsyncHandler; 

	
	 org.sigmah.offline.dao.CategoryTypeAsyncDAO categoryTypeAsyncDAO; 

	
	 HistoryAsyncDAO historyAsyncDAO; 

	
	 OrganizationAsyncDAO organizationAsyncDAO; 

	
	 ProfileAsyncDAO profileAsyncDAO; 

	
	 UserAsyncDAO userAsyncDAO; 

	
	 UserUnitAsyncDAO userUnitAsyncDAO; 

	
	 AuthenticationAsyncDAO authenticationAsyncDAO; 

	
	 PageAccessAsyncDAO pageAccessAsyncDAO; 

	
	 ProjectReportAsyncDAO projectReportAsyncDAO; 

	
	 ProjectTeamMembersAsyncDAO projectTeamMembersAsyncDAO;

	 ReportReferenceAsyncDAO reportReferenceAsyncDAO;
	 
	 CategoryElementAsyncDAO categoryElementAsyncDAO;

	@Override
	public EventBus getEventBus() {
		if (eventBus == null)
			eventBus = new EventBusImpl(this);

		return eventBus;
	}

	@Override
	public DispatchAsync getDispatch() {
		if (dispatch == null){
			dispatch = new SecureDispatchAsync(getAuthenticationProvider(), getEventBus(), getPageManager(),
					getApplicationStateManager(), getExceptionHandler());
		}
		
		return dispatch;
	}

	@Override
	public LocalDispatchServiceAsync getLocalDispatch() {
		if (localdispatchAsynch == null)
			localdispatchAsynch = new LocalDispatchServiceAsync(getAuthenticationProvider());
		return localdispatchAsynch;
	}

	@Override
	public PageManager getPageManager() {
		if (pageManeger == null) {

			pageManeger = new PageManager(getEventBus());
		}
		return pageManeger;
	}

	@Override
	public Theme getTheme() {
		if (theme == null) {
			theme = new SigmahTheme();
		}

		return theme;
	}

	@Override
	public AuthenticationProvider getAuthenticationProvider() {
		if (authenticationProvider == null) {

			authenticationProvider = new AuthenticationProvider();
		}
		return authenticationProvider;
	}

	@Override
	public ApplicationStateManager getApplicationStateManager() {
		if (applicationStateManager == null) {

			applicationStateManager = new ApplicationStateManager(getEventBus(), getUpdateDiaryAsyncDAO());
		}
		return applicationStateManager;
	}

	@Override
	public TransfertManager getTransfertManager() {
		if (TransferManager == null) {
			 DirectTransfertManager directTransfertManager = new DirectTransfertManager(getAuthenticationProvider(), getPageManager(), getEventBus());
			if (html5EngineActive && IndexedDB.isSupported() && FileReader.isSupported()) {
				TransferManager = new Html5TransfertManager(getDispatch(), getFileDataAsyncDAO(), getTransfertAsyncDAO(), getEventBus(), directTransfertManager);
		      } else {
		    	  TransferManager = directTransfertManager;
		      }

		}
		return TransferManager;
	}

	@Override
	public Synchronizer getSynchronizer() {
		if (synchronizer == null)
			synchronizer = new Synchronizer(getUpdateDiaryAsyncDAO(), getReminderAsyncDAO(),
					getMonitoredPointAsyncDAO(), getTransfertAsyncDAO(), getFileDataAsyncDAO(), getDispatch(),
					getOrgUnitAsyncDAO());
		return synchronizer;
	}

	@Override
	public UserLocalCache getClientCache() {
		if (userLocalCache == null) {

			userLocalCache = new UserLocalCache(getDispatch(), getLocalDispatch(), getAuthenticationProvider());
		}
		return userLocalCache;
	}

	@Override
	public FileDataAsyncDAO getFileDataAsyncDAO() {
		if (fileDataAsyncDAO == null)
			fileDataAsyncDAO = new FileDataAsyncDAO();
		return fileDataAsyncDAO;
	}

	@Override
	public TransfertAsyncDAO getTransfertAsyncDAO() {
		if (transfertAsyncDAO == null)
			transfertAsyncDAO = new TransfertAsyncDAO();
		return transfertAsyncDAO;
	}

	@Override
	public ApplicationPresenter getApplicationPresenter() {
		if (applicationPresenter == null)
			applicationPresenter = new ApplicationPresenter(getApplicationView(), this);
		return applicationPresenter;
	}

	@Override
	public MockUpPresenter getMockUpPresenter() {
		if (mockUpPresenter == null)
			mockUpPresenter = new MockUpPresenter(getMockUpView(), this);
		return mockUpPresenter;
	}

	@Override
	public OrganizationBannerPresenter getOrganizationBannerPresenter() {
		if (organizationBannerPresenter == null)
			organizationBannerPresenter = new OrganizationBannerPresenter(getOrganizationBannerView(), this,
					getLogoAsyncDAO(), getImageProvider());
		return organizationBannerPresenter;
	}

	@Override
	public AuthenticationBannerPresenter getAuthenticationBannerPresenter() {
		if (authenticationBannerPresenter == null)
			authenticationBannerPresenter = new AuthenticationBannerPresenter(getAuthenticationBannerView(), this);
		return authenticationBannerPresenter;
	}

	@Override
	public OfflineBannerPresenter getOfflineBannerPresenter() {

		if (offlineBannerPresenter == null)
			offlineBannerPresenter = new OfflineBannerPresenter(getOfflineBannerView(), this, getTransfertManager());

		return offlineBannerPresenter;
	}

	@Override
	public AppLoaderPresenter getAppLoaderPresenter() {

		if (appLoaderPresenter == null)
			appLoaderPresenter = new AppLoaderPresenter(getAppLoaderView(), this);

		return appLoaderPresenter;
	}

	@Override
	public MenuBannerPresenter getMenuBannerPresenter() {
		if (menuBannerPresenter == null)
			menuBannerPresenter = new MenuBannerPresenter(getMenuBannerView(), this);
		return menuBannerPresenter;
	}

	@Override
	public MessageBannerPresenter getMessageBannerPresenter() {
		if (messageBannerPresenter == null)
			messageBannerPresenter = new MessageBannerPresenter(getMessageBannerView(), this);
		return messageBannerPresenter;
	}

	@Override
	public CreditsPresenter getCreditsPresenter() {

		if (creditsPresenter == null)
			creditsPresenter = new CreditsPresenter(getCreditsView(), this);

		return creditsPresenter;
	}

	@Override
	public HelpPresenter getHelpPresenter() {
		if (helpPresenter == null)
			helpPresenter = new HelpPresenter(getHelpView(), this);
		return helpPresenter;
	}

	@Override
	public DashboardPresenter getHomePresenter() {

		if (homePresenter == null)
			homePresenter = new DashboardPresenter(getDashboardView(), this);

		return homePresenter;
	}

	@Override
	public LoginPresenter getLoginPresenter() {

		if (loginPresenter == null)
			loginPresenter = new LoginPresenter(getLoginView(), this);
		return loginPresenter;
	}

	@Override
	public LostPasswordPresenter getLostPasswordPresenter() {

		if (lostPasswordPresenter == null)
			lostPasswordPresenter = new LostPasswordPresenter(getLostPasswordView(), this);
		return lostPasswordPresenter;
	}

	@Override
	public ResetPasswordPresenter getResetPasswordPresenter() {
		if (resetPasswordPresenter == null)
			resetPasswordPresenter = new ResetPasswordPresenter(getResetPasswordView(), this);

		return null;
	}

	@Override
	public ChangeOwnPasswordPresenter getChangeOwnPasswordPresenter() {

		if (changeOwnPasswordPresenter == null)
			changeOwnPasswordPresenter = new ChangeOwnPasswordPresenter(getChangeOwnPasswordView(), this);

		return changeOwnPasswordPresenter;
	}

	@Override
	public CreateProjectPresenter getCreateProjectPresenter() {

		if (createProjectPresenter == null)
			createProjectPresenter = new CreateProjectPresenter(getCreateProjectView(), this);

		return createProjectPresenter;
	}

	@Override
	public CalendarEventPresenter getCalendarEventPresenter() {

		if (calendarEventPresenter == null)
			calendarEventPresenter = new CalendarEventPresenter(getCalendarEventView(), this);

		return calendarEventPresenter;
	}

	@Override
	public ReportCreatePresenter getReportCreatePresenter() {

		if (reportCreatePresenter == null)
			reportCreatePresenter = new ReportCreatePresenter(getReportCreateView(), this);

		return reportCreatePresenter;
	}

	@Override
	public AttachFilePresenter getAttachFilePresenter() {

		if (attachFilePresenter == null)
			attachFilePresenter = new AttachFilePresenter(getAttachFileView(), this);

		return attachFilePresenter;

	}

	@Override
	public ImportationPresenter getImportationPresenter() {

		if (importationPresenter == null)
			importationPresenter = new ImportationPresenter(getImportationView(), this);

		return importationPresenter;
	}

	@Override
	public ProjectPresenter getProjectPresenter() {
		if (projectPresenter == null)
			projectPresenter = new ProjectPresenter(getProjectView(), this);

		return projectPresenter;
	}

	/////////////// start from here

	@Override
	public ProjectDashboardPresenter getProjectDashboardPresenter() {
		if (projectDashboardPresenter == null)
			projectDashboardPresenter = new ProjectDashboardPresenter(getProjectDashboardView(), this);

		return projectDashboardPresenter;
	}

	@Override
	public ProjectLogFramePresenter getProjectLogFramePresenter() {
		if (projectLogFramePresenter == null)
			projectLogFramePresenter = new ProjectLogFramePresenter(getProjectLogFrameView(), this);

		return projectLogFramePresenter;
	}

	@Override
	public ProjectDetailsPresenter getProjectDetailsPresenter() {
		if (projectDetailsPresenter == null)
			projectDetailsPresenter = new ProjectDetailsPresenter(getProjectDetailsView(), this);

		return projectDetailsPresenter;
	}

	@Override
	public ProjectTeamMembersPresenter getProjectTeamMembersPresenter() {
		if (projectTeamMembersPresenter == null)
			projectTeamMembersPresenter = new ProjectTeamMembersPresenter(getProjectTeamMembersView(), this);

		return projectTeamMembersPresenter;
	}

	@Override
	public ProjectCalendarPresenter getProjectCalendarPresenter() {
		if (projectCalendarPresenter == null)
			projectCalendarPresenter = new ProjectCalendarPresenter(getProjectCalendarView(), this);

		return projectCalendarPresenter;
	}

	@Override
	public ProjectReportsPresenter getProjectReportsPresenter() {
		if (projectReportsPresenter == null)
			projectReportsPresenter = new ProjectReportsPresenter(getProjectReportsView(), this);

		return projectReportsPresenter;
	}

	@Override
	public ReminderEditPresenter getReminderEditPresenter() {
		if (reminderEditPresenter == null)
			reminderEditPresenter = new ReminderEditPresenter(getReminderEditView(), this);

		return reminderEditPresenter;
	}

	@Override
	public ReminderHistoryPresenter getReminderHistoryPresenter() {
		if (reminderHistoryPresenter == null)
			reminderHistoryPresenter = new ReminderHistoryPresenter(getReminderHistoryView(), this);

		return reminderHistoryPresenter;
	}

	@Override
	public LinkedProjectPresenter getLinkedProjectPresenter() {
		if (linkedProjectPresenter == null)
			linkedProjectPresenter = new LinkedProjectPresenter(getLinkedProjectView(), this);

		return linkedProjectPresenter;
	}

	@Override
	public ProjectIndicatorEntriesPresenter getProjectIndicatorEntriesPresenter() {
		if (projectIndicatorEntriesPresenter == null)
			projectIndicatorEntriesPresenter = new ProjectIndicatorEntriesPresenter(getProjectIndicatorEntriesView(),
					this);

		return projectIndicatorEntriesPresenter;
	}

	@Override
	public ProjectIndicatorManagementPresenter getProjectIndicatorManagementPresenter() {
		if (projectIndicatorManagementPresenter == null)
			projectIndicatorManagementPresenter = new ProjectIndicatorManagementPresenter(
					getProjectIndicatorManagementView(), this);

		return projectIndicatorManagementPresenter;
	}

	@Override
	public ProjectIndicatorMapPresenter getProjectIndicatorMapPresenter() {
		if (projectIndicatorMapPresenter == null)
			projectIndicatorMapPresenter = new ProjectIndicatorMapPresenter(getProjectIndicatorMapView(), this);

		return projectIndicatorMapPresenter;
	}

	@Override
	public EditIndicatorPresenter getEditIndicatorPresenter() {
		if (editIndicatorPresenter == null)
			editIndicatorPresenter = new EditIndicatorPresenter(getEditIndicatorView(), this);

		return editIndicatorPresenter;
	}

	@Override
	public EditSitePresenter getEditSitePresenter() {
		if (editSitePresenter == null)
			editSitePresenter = new EditSitePresenter(getEditSiteView(), this);

		return editSitePresenter;
	}

	@Override
	public ExportContactsPresenter getExportContactsPresenter() {
		if (exportContactsPresenter == null)
			exportContactsPresenter = new ExportContactsPresenter(getExportContactsView(), this);

		return exportContactsPresenter;
	}

	@Override
	public ExportContactsSettingPresenter getExportContactsSettingPresenter() {
		if (exportContactsSettingPresenter == null)
			exportContactsSettingPresenter = new ExportContactsSettingPresenter(getExportContactsSettingView(), this);

		return exportContactsSettingPresenter;
	}

	@Override
	public ExportProjectsPresenter getExportProjectsPresenter() {
		if (exportProjectsPresenter == null)
			exportProjectsPresenter = new ExportProjectsPresenter(getExportProjectsView(), this);

		return exportProjectsPresenter;
	}

	@Override
	public ExportProjectsSettingPresenter getExportProjectsSettingPresenter() {
		if (exportProjectsSettingPresenter == null)
			exportProjectsSettingPresenter = new ExportProjectsSettingPresenter(getExportProjectsSettingView(), this);

		return exportProjectsSettingPresenter;
	}

	@Override
	public ProjectCoreRenameVersionPresenter getProjectCoreRenameVersionPresenter() {
		if (projectCoreRenameVersionPresenter == null)
			projectCoreRenameVersionPresenter = new ProjectCoreRenameVersionPresenter(getProjectCoreRenameVersionView(),
					this);

		return projectCoreRenameVersionPresenter;
	}

	@Override
	public ProjectCoreDiffPresenter getProjectCoreDiffPresenter() {
		if (projectCoreDiffPresenter == null)
			projectCoreDiffPresenter = new ProjectCoreDiffPresenter(getProjectCoreDiffView(), this);

		return projectCoreDiffPresenter;
	}

	@Override
	public OrgUnitPresenter getOrgUnitPresenter() {
		if (orgUnitPresenter == null)
			orgUnitPresenter = new OrgUnitPresenter(getOrgUnitView(), this);

		return orgUnitPresenter;
	}

	@Override
	public OrgUnitDashboardPresenter getOrgUnitDashboardPresenter() {

		if (orgUnitDashboradPresenter == null)
			orgUnitDashboradPresenter = new OrgUnitDashboardPresenter(getOrgUnitDashboardView(), this);

		return orgUnitDashboradPresenter;
	}

	@Override
	public OrgUnitDetailsPresenter getOrgUnitDetailsPresenter() {
		if (orgUnitDetailsPresenter == null)
			orgUnitDetailsPresenter = new OrgUnitDetailsPresenter(getOrgUnitDetailsView(), this);

		return orgUnitDetailsPresenter;
	}

	@Override
	public OrgUnitCalendarPresenter getOrgUnitCalendarPresenter() {
		if (orgUnitCalendarPresenter == null)
			orgUnitCalendarPresenter = new OrgUnitCalendarPresenter(getOrgUnitCalendarView(), this);

		return orgUnitCalendarPresenter;
	}

	@Override
	public OrgUnitReportsPresenter getOrgUnitReportsPresenter() {
		if (orgUnitReportsPresenter == null)
			orgUnitReportsPresenter = new OrgUnitReportsPresenter(getOrgUnitReportsView(), this);

		return orgUnitReportsPresenter;
	}

	@Override
	public ContactPresenter getContactPresenter() {
		if (contactPresenter == null)
			contactPresenter = new ContactPresenter(getContactView(), this, getContactDetailsPresenter(), getContactRelationshipsPresenter(), getContactHistoryPresenter(), getImageProvider());

		return contactPresenter;
	}

	@Override
	public ContactDetailsPresenter getContactDetailsPresenter() {
		if (contactDetailsPresenter == null)
			contactDetailsPresenter = new ContactDetailsPresenter(getContactDetailsView(), this);

		return contactDetailsPresenter;
	}

	@Override
	public ContactRelationshipsPresenter getContactRelationshipsPresenter() {
		if (contactRelationshipsPresenter == null)
			contactRelationshipsPresenter = new ContactRelationshipsPresenter(getContactRelationshipsView(), this);

		return contactRelationshipsPresenter;
	}

	@Override
	public ContactHistoryPresenter getContactHistoryPresenter() {
		if (ContactHistoryPresenter == null)
			ContactHistoryPresenter = new ContactHistoryPresenter(getContactHistoryView(), this);

		return ContactHistoryPresenter;
	}

	@Override
	public AdminPresenter getAdminPresenter() {
		if (adminPresenter == null)
			adminPresenter = new AdminPresenter(getAdminView(), this);

		return adminPresenter;
	}

	@Override
	public ParametersAdminPresenter getParametersAdminPresenter() {
		if (parametersAdminPresenter == null)
			parametersAdminPresenter = new ParametersAdminPresenter(getParametersAdminView(), this);

		return parametersAdminPresenter;
	}

	@Override
	public UsersAdminPresenter getUsersAdminPresenter() {
		if (usersAdminPresenter == null)
			usersAdminPresenter = new UsersAdminPresenter(getUsersAdminView(), this);

		return usersAdminPresenter;
	}

	@Override
	public PrivacyGroupEditPresenter getPrivacyGroupEditPresenter() {
		if (privacyGroupEditPresenter == null)
			privacyGroupEditPresenter = new PrivacyGroupEditPresenter(getPrivacyGroupEditView(), this);

		return privacyGroupEditPresenter;
	}

	@Override
	public ProfileEditPresenter getProfileEditPresenter() {
		if (profileEditPresenter == null)
			profileEditPresenter = new ProfileEditPresenter(getProfileEditView(), this);

		return profileEditPresenter;
	}

	@Override
	public UserEditPresenter getUserEditPresenter() {
		if (userEditPresenter == null)
			userEditPresenter = new UserEditPresenter(getUserEditView(), this);

		return userEditPresenter;
	}

	@Override
	public OrgUnitsAdminPresenter getOrgUnitsAdminPresenter() {
		if (orgUnitsAdminPresenter == null)
			orgUnitsAdminPresenter = new OrgUnitsAdminPresenter(getOrgUnitsAdminView(), this);

		return orgUnitsAdminPresenter;
	}

	@Override
	public AddOrgUnitAdminPresenter getAddOrgUnitAdminPresenter() {
		if (addOrgUnitAdminPresenter == null)
			addOrgUnitAdminPresenter = new AddOrgUnitAdminPresenter(getAddOrgUnitAdminView(), this);

		return addOrgUnitAdminPresenter;
	}

	@Override
	public MoveOrgUnitAdminPresenter getMoveOrgUnitAdminPresenter() {
		if (moveOrgUnitAdminPresenter == null)
			moveOrgUnitAdminPresenter = new MoveOrgUnitAdminPresenter(getMoveOrgUnitAdminView(), this);

		return moveOrgUnitAdminPresenter;
	}

	@Override
	public CategoriesAdminPresenter getCategoriesAdminPresenter() {
		if (categoriesAdminPresenter == null)
			categoriesAdminPresenter = new CategoriesAdminPresenter(getCategoriesAdminView(), this);

		return categoriesAdminPresenter;
	}

	@Override
	public OrgUnitModelsAdminPresenter getOrgUnitModelsAdminPresenter() {
		if (orgUnitModelsAdminPresenter == null)
			orgUnitModelsAdminPresenter = new OrgUnitModelsAdminPresenter(getOrgUnitModelsAdminView(), this);

		return orgUnitModelsAdminPresenter;
	}

	@Override
	public AddOrgUnitModelAdminPresenter getAddOrgUnitModelAdminPresenter() {
		if (addOrgUnitModelAdminPresenter == null)
			addOrgUnitModelAdminPresenter = new AddOrgUnitModelAdminPresenter(getAddOrgUnitModelAdminView(), this);

		return addOrgUnitModelAdminPresenter;
	}

	@Override
	public ProjectModelsAdminPresenter getProjectModelsAdminPresenter() {
		if (projectModelsAdminPresenter == null)
			projectModelsAdminPresenter = new ProjectModelsAdminPresenter(getProjectModelsAdminView(), this);

		return projectModelsAdminPresenter;
	}

	@Override
	public AddProjectModelAdminPresenter getAddProjectModelAdminPresenter() {
		if (addProjectModelAdminPresenter == null)
			addProjectModelAdminPresenter = new AddProjectModelAdminPresenter(getAddProjectModelAdminView(), this);

		return addProjectModelAdminPresenter;
	}

	@Override
	public ContactModelsAdminPresenter getContactModelsAdminPresenter() {
		if (contactModelsAdminPresenter == null)
			contactModelsAdminPresenter = new ContactModelsAdminPresenter(getContactModelsAdminView(), this);

		return contactModelsAdminPresenter;
	}

	@Override
	public AddContactModelAdminPresenter getAddContactModelAdminPresenter() {
		if (addContactModelAdminPresenter == null)
			addContactModelAdminPresenter = new AddContactModelAdminPresenter(getAddContactModelAdminView(), this);

		return addContactModelAdminPresenter;
	}

	@Override
	public AddImportationSchemePresenter getAddImportationSchemePresenter() {
		if (addImportationSchemePresenter == null)
			addImportationSchemePresenter = new AddImportationSchemePresenter(getAddImportationSchemeView(), this);

		return addImportationSchemePresenter;
	}

	@Override
	public ReportModelsAdminPresenter getReportModelsAdminPresenter() {
		if (reportModelsAdminPresenter == null)
			reportModelsAdminPresenter = new ReportModelsAdminPresenter(getReportModelsAdminView(), this);

		return reportModelsAdminPresenter;
	}

	@Override
	public EditPhaseModelAdminPresenter getEditPhaseModelAdminPresenter() {
		if (editPhaseModelAdminPresenter == null)
			editPhaseModelAdminPresenter = new EditPhaseModelAdminPresenter(getEditPhaseModelAdminView(), this);

		return editPhaseModelAdminPresenter;
	}

	@Override
	public EditLayoutGroupAdminPresenter getEditLayoutGroupAdminPresenter() {
		if (editLayoutGroupAdminPresenter == null)
			editLayoutGroupAdminPresenter = new EditLayoutGroupAdminPresenter(getEditLayoutGroupAdminView(), this);

		return editLayoutGroupAdminPresenter;
	}

	@Override
	public EditFlexibleElementAdminPresenter getEditFlexibleElementAdminPresenter() {
		if (editFlexibleElementAdminPresenter == null)
			editFlexibleElementAdminPresenter = new EditFlexibleElementAdminPresenter(getEditFlexibleElementAdminView(),
					this);

		return editFlexibleElementAdminPresenter;
	}

	@Override
	public AddBudgetSubFieldPresenter getAddBudgetSubFieldPresenter() {
		if (addBudSubFieldPresenter == null)
			addBudSubFieldPresenter = new AddBudgetSubFieldPresenter(getAddBudSubFieldView(), this);

		return addBudSubFieldPresenter;
	}

	@Override
	public ImportModelPresenter getImportModelPresenter() {
		if (importModelPresenter == null)
			importModelPresenter = new ImportModelPresenter(getImportModelView(), this);

		return importModelPresenter;
	}

	@Override
	public ImportationSchemeAdminPresenter getImportationShemePresenter() {
		if (importationShemePresenter == null)
			importationShemePresenter = new ImportationSchemeAdminPresenter(getImportationSchemeAdminView(), this);

		return importationShemePresenter;
	}

	@Override
	public AddVariableImporationSchemePresenter getAddVariableImporationSchemePresenter() {
		if (addVariableImporationSchemePresenter == null)
			addVariableImporationSchemePresenter = new AddVariableImporationSchemePresenter(
					getAddVariableImporationSchemeView(), this);

		return addVariableImporationSchemePresenter;
	}

	@Override
	public AddImportationSchemeModelsAdminPresenter getAddImportationSchemeModelsAdminPresenter() {
		if (addImportationSchemeModelsAdminPresenter == null)
			addImportationSchemeModelsAdminPresenter = new AddImportationSchemeModelsAdminPresenter(
					getAddImportationSchemeModelsAdminView(), this);

		return addImportationSchemeModelsAdminPresenter;
	}

	@Override
	public AddMatchingRuleImportationShemeModelsAdminPresenter getAddMatchingRuleImportationShemeModelsAdminPresenter() {
		if (addMatchingRuleImportationShemeModelsAdminPresenter == null)
			addMatchingRuleImportationShemeModelsAdminPresenter = new AddMatchingRuleImportationShemeModelsAdminPresenter(
					getAddMatchingRuleImportationShemeModelsAdminView(), this);

		return addMatchingRuleImportationShemeModelsAdminPresenter;
	}

	@Override
	public FileSelectionPresenter getFileSelectionPresenter() {
		if (fileSelectionPresenter == null)
			fileSelectionPresenter = new FileSelectionPresenter(getFileSelectionView(), this);

		return fileSelectionPresenter;
	}

	@Override
	public UpdateDiaryAsyncDAO getUpdateDiaryAsyncDAO() {
		
		if (updateDiaryAsyncDAO == null)
			  updateDiaryAsyncDAO = new UpdateDiaryAsyncDAO();

		return updateDiaryAsyncDAO;
	}

	@Override
	public TransfertManagerProvider getTransferManagerProvider() {

		if (transferManagerProvider == null) {

			transferManagerProvider = new TransfertManagerProvider(getDispatch(), getAuthenticationProvider(),
					getPageManager(), getEventBus(), getFileDataAsyncDAO(), getTransfertAsyncDAO());
		}

		return transferManagerProvider;
	}

	@Override
	public MonitoredPointAsyncDAO getMonitoredPointAsyncDAO() {
		if (monitoredPointAsyncDAO == null)
			monitoredPointAsyncDAO = new MonitoredPointAsyncDAO();

		return monitoredPointAsyncDAO;
	}

	@Override
	public OrgUnitAsyncDAO getOrgUnitAsyncDAO() {
		
		
		if (orgUnitAsyncDAO == null)
			orgUnitAsyncDAO = new OrgUnitAsyncDAO(getOrgUnitModelAsyncDAO(), getCountryAsyncDAO());

		return orgUnitAsyncDAO;
	}

	@Override
	public ReminderAsyncDAO getReminderAsyncDAO() {
		if (reminderAsyncDAO == null)
			reminderAsyncDAO = new ReminderAsyncDAO();

		return reminderAsyncDAO;
	}

	@Override
	public ApplicationView getApplicationView() {
		if (applicationView == null)
			applicationView = new ApplicationView();

		return applicationView;
	}

	@Override
	public MockUpView getMockUpView() {
		
		if (mockupView == null)
			mockupView = new MockUpView();

		return mockupView;
	}

	@Override
	public OrganizationBannerView getOrganizationBannerView() {
		
		if (organizationBannerView == null)
			organizationBannerView = new OrganizationBannerView();

		return organizationBannerView;
	}

	@Override
	public ImageProvider getImageProvider() {
		if (imageProvider == null)
			imageProvider = new ImageProvider(this);

		return imageProvider;
	}

	@Override
	public LogoAsyncDAO getLogoAsyncDAO() {
		if (logoAsyncDAO == null) {
			logoAsyncDAO = new LogoAsyncDAO();
			logoAsyncDAO.setAuthenticationProvider(getAuthenticationProvider());
		}

		return logoAsyncDAO;
	}

	@Override
	public AuthenticationBannerView getAuthenticationBannerView() {
		if (authenticationBannerView == null)
			authenticationBannerView = new AuthenticationBannerView();

		return authenticationBannerView;
	}

	@Override
	public OfflineBannerView getOfflineBannerView() {

		if (offlineBannerView == null)
			offlineBannerView = new OfflineBannerView();

		return offlineBannerView;
	}

	@Override
	public AppLoaderView getAppLoaderView() {
		
	if (appLoaderView == null)
		appLoaderView = new AppLoaderView();

	return appLoaderView;
		
	}

	@Override
	public MenuBannerView getMenuBannerView() {
		
		if (menuBannerView == null)
			menuBannerView = new MenuBannerView();

		return menuBannerView;
	}

	@Override
	public MessageBannerView getMessageBannerView() {
		if (messageBannerView == null)
			messageBannerView = new MessageBannerView();

		return messageBannerView;
	}

	@Override
	public CreditsView getCreditsView() {
		
		if (creditsView == null)
			creditsView = new CreditsView();

		return creditsView;
	}

	@Override
	public HelpView getHelpView() {
		if (helpView == null)
			helpView = new HelpView();

		return helpView;
	}

	@Override // Not a singleton so create a new instance every time
	public ContactsListWidget getContactsListWidget() {

		return new ContactsListWidget(getContactsListView(), this);
	}

	@Override // Not a singleton so create a new instance every time
	public ProjectsListWidget getProjectsListWidget() {

		return new ProjectsListWidget(getProjectsListView(), this);
	}

	@Override
	public ContactsListView getContactsListView() {
		if (contactsListView == null)
			contactsListView = new ContactsListView();

		return contactsListView;
	}

	@Override
	public ProjectsListView getProjectsListView() {
		if (projectsListView == null)
			projectsListView = new ProjectsListView();

		return projectsListView;
	}

	@Override
	public DashboardView getDashboardView() {

		if (dashboardView == null)
			dashboardView = new DashboardView(this);

		return dashboardView;
	}

	@Override
	public LoginView getLoginView() {
		if (loginView == null)
			loginView = new LoginView();

		return loginView;
	}

	@Override
	public LostPasswordView getLostPasswordView() {
		if (lostPasswordView == null)
			lostPasswordView = new LostPasswordView();

		return lostPasswordView;
	}

	@Override
	public ResetPasswordView getResetPasswordView() {
		if (resetPasswordView == null)
			resetPasswordView = new ResetPasswordView();

		return resetPasswordView;
	}

	@Override
	public ChangeOwnPasswordView getChangeOwnPasswordView() {
	   if (changeOwnPasswordView == null)
		changeOwnPasswordView = new ChangeOwnPasswordView();

	return changeOwnPasswordView;
	}

	@Override
	public CreateProjectView getCreateProjectView() {
		
		   if (createProjectView == null)
			createProjectView = new CreateProjectView();

		return createProjectView;
	}

	@Override
	public CalendarEventView getCalendarEventView() {
		   
		   if (calendarEventView == null)
			calendarEventView = new CalendarEventView();

		return calendarEventView;
	}

	@Override
	public ReportCreateView getReportCreateView() {
		if (reportCreateView == null)
			reportCreateView = new ReportCreateView();

		return reportCreateView;
	}

	@Override
	public AttachFileView getAttachFileView() {
		if (attachFileView == null)
			attachFileView = new AttachFileView();

		return attachFileView;
	}

	@Override
	public ImportationView getImportationView() {
		if (importationView == null)
			importationView = new ImportationView();

		return importationView;
	}

	@Override
	public ProjectDashboardView getProjectDashboardView() {

		if (projectDashboardView == null) {

			projectDashboardView = new ProjectDashboardView(this);

		}

		return projectDashboardView;
	}

	@Override
	public ProjectLogFrameView getProjectLogFrameView() {

		if (projectLogFrameView == null) {

			projectLogFrameView = new ProjectLogFrameView(getProjectLogFrameGrid());

		}

		return projectLogFrameView;
	}

	@Override
	public ProjectDetailsView getProjectDetailsView() {

		if (projectDetailsView == null) {

			projectDetailsView = new ProjectDetailsView();

		}

		return projectDetailsView;
	}

	@Override
	public ProjectTeamMembersView getProjectTeamMembersView() {

		if (projectTeamMembersView == null) {

			projectTeamMembersView = new ProjectTeamMembersView();

		}

		return projectTeamMembersView;
	}

	@Override
	public ProjectCalendarView getProjectCalendarView() {

		if (projectCalendarView == null) {

			projectCalendarView = new ProjectCalendarView();

		}

		return projectCalendarView;
	}

	@Override
	public ProjectReportsView getProjectReportsView() {

		if (projectReportsView == null) {

			projectReportsView = new ProjectReportsView();

		}

		return projectReportsView;
	}

	@Override
	public ReminderEditView getReminderEditView() {

		if (reminderEditView == null) {

			reminderEditView = new ReminderEditView();

		}

		return reminderEditView;
	}

	@Override
	public ReminderHistoryView getReminderHistoryView() {

		if (reminderHistoryView == null) {

			reminderHistoryView = new ReminderHistoryView();

		}

		return reminderHistoryView;
	}

	@Override
	public LinkedProjectView getLinkedProjectView() {

		if (linkedProjectView == null) {

			linkedProjectView = new LinkedProjectView();

		}

		return linkedProjectView;
	}

	@Override
	public ProjectIndicatorEntriesView getProjectIndicatorEntriesView() {

		if (projectIndicatorEntriesView == null) {

			projectIndicatorEntriesView = new ProjectIndicatorEntriesView(getProjectPivotContainer());

		}

		return projectIndicatorEntriesView;
	}

	@Override
	public ProjectIndicatorManagementView getProjectIndicatorManagementView() {

		if (projectIndicatorManagementView == null) {

			projectIndicatorManagementView = new ProjectIndicatorManagementView();

		}

		return projectIndicatorManagementView;
	}

	@Override
	public ProjectIndicatorMapView getProjectIndicatorMapView() {
		if (projectIndicatorMapView == null) {

			projectIndicatorMapView = new ProjectIndicatorMapView(getSiteGridPanel());

		}

		return projectIndicatorMapView;
	}

	@Override
	public EditIndicatorView getEditIndicatorView() {

		if (editIndicatorView == null) {

			editIndicatorView = new EditIndicatorView(getDispatch(), getPivotGridPanel());

		}

		return editIndicatorView;
	}

	@Override
	public EditSiteView getEditSiteView() {

		if (editSiteView == null) {

			editSiteView = new EditSiteView();

		}

		return editSiteView;
	}

	@Override
	public ExportContactsView getExportContactsView() {

		if (exportContactsView == null) {

			exportContactsView = new ExportContactsView();

		}

		return exportContactsView;
	}

	@Override
	public ExportContactsSettingView getExportContactsSettingView() {

		if (exportContactsSettingView == null) {

			exportContactsSettingView = new ExportContactsSettingView();

		}

		return exportContactsSettingView;
	}

	@Override
	public ExportProjectsView getExportProjectsView() {

		if (exportProjectsView == null) {

			exportProjectsView = new ExportProjectsView();

		}

		return exportProjectsView;
	}

	@Override
	public ExportProjectsSettingView getExportProjectsSettingView() {

		if (exportProjectsSettingView == null) {

			exportProjectsSettingView = new ExportProjectsSettingView();

		}

		return exportProjectsSettingView;
	}

	@Override
	public ProjectCoreRenameVersionView getProjectCoreRenameVersionView() {

		if (projectCoreRenameVersionView == null) {

			projectCoreRenameVersionView = new ProjectCoreRenameVersionView();

		}

		return projectCoreRenameVersionView;
	}

	@Override
	public ProjectCoreDiffView getProjectCoreDiffView() {

		if (projectCoreDiffView == null) {

			projectCoreDiffView = new ProjectCoreDiffView();

		}

		return projectCoreDiffView;
	}

	@Override
	public OrgUnitView getOrgUnitView() {

		if (orgUnitView == null) {

			orgUnitView = new OrgUnitView();

		}

		return orgUnitView;
	}

	@Override
	public OrgUnitDashboardView getOrgUnitDashboardView() {

		if (orgUnitDashboardView == null) {

			orgUnitDashboardView = new OrgUnitDashboardView(this);

		}

		return orgUnitDashboardView;
	}

	@Override
	public OrgUnitDetailsView getOrgUnitDetailsView() {

		if (orgUnitDetailsView == null) {

			orgUnitDetailsView = new OrgUnitDetailsView();

		}

		return orgUnitDetailsView;
	}

	@Override
	public OrgUnitCalendarView getOrgUnitCalendarView() {

		if (orgUnitCalendarView == null) {

			orgUnitCalendarView = new OrgUnitCalendarView();

		}

		return orgUnitCalendarView;
	}

	@Override
	public OrgUnitReportsView getOrgUnitReportsView() {

		if (orgUnitReportsView == null) {

			orgUnitReportsView = new OrgUnitReportsView();

		}

		return orgUnitReportsView;
	}

	@Override
	public ContactView getContactView() {

		if (contactView == null) {

			contactView = new ContactView();

		}

		return contactView;
	}

	@Override
	public ContactDetailsView getContactDetailsView() {

		if (contactDetailsView == null) {

			contactDetailsView = new ContactDetailsView();

		}

		return contactDetailsView;
	}

	@Override
	public ContactRelationshipsView getContactRelationshipsView() {

		if (contactRelationshipsView == null) {

			contactRelationshipsView = new ContactRelationshipsView();

		}

		return contactRelationshipsView;
	}

	@Override
	public ContactHistoryView getContactHistoryView() {

		if (contactHistoryView == null) {

			contactHistoryView = new ContactHistoryView();

		}

		return contactHistoryView;
	}

	@Override
	public AdminView getAdminView() {

		if (adminView == null) {

			adminView = new AdminView();

		}

		return adminView;
	}

	@Override
	public ParametersAdminView getParametersAdminView() {

		if (parametersAdminView == null) {

			parametersAdminView = new ParametersAdminView();

		}

		return parametersAdminView;
	}

	@Override
	public UsersAdminView getUsersAdminView() {

		if (usersAdminView == null) {

			usersAdminView = new UsersAdminView();

		}

		return usersAdminView;
	}

	@Override
	public PrivacyGroupEditView getPrivacyGroupEditView() {

		if (privacyGroupEditView == null) {

			privacyGroupEditView = new PrivacyGroupEditView();

		}

		return privacyGroupEditView;
	}

	@Override
	public ProfileEditView getProfileEditView() {

		if (profileEditView == null) {

			profileEditView = new ProfileEditView();

		}

		return profileEditView;
	}

	@Override
	public UserEditView getUserEditView() {

		if (userEditView == null) {

			userEditView = new UserEditView();

		}

		return userEditView;
	}

	@Override
	public OrgUnitsAdminView getOrgUnitsAdminView() {

		if (orgUnitsAdminView == null) {

			orgUnitsAdminView = new OrgUnitsAdminView();

		}

		return orgUnitsAdminView;
	}

	@Override
	public AddOrgUnitAdminView getAddOrgUnitAdminView() {

		if (addOrgUnitAdminView == null) {

			addOrgUnitAdminView = new AddOrgUnitAdminView();

		}

		return addOrgUnitAdminView;
	}

	@Override
	public MoveOrgUnitAdminView getMoveOrgUnitAdminView() {

		if (moveOrgUnitAdminView == null) {

			moveOrgUnitAdminView = new MoveOrgUnitAdminView();

		}

		return moveOrgUnitAdminView;
	}

	@Override
	public CategoriesAdminView getCategoriesAdminView() {

		if (categoriesAdminView == null) {

			categoriesAdminView = new CategoriesAdminView();

		}

		return categoriesAdminView;
	}

	@Override
	public OrgUnitModelsAdminView getOrgUnitModelsAdminView() {

		if (orgUnitModelsAdminView == null) {

			orgUnitModelsAdminView = new OrgUnitModelsAdminView();

		}

		return orgUnitModelsAdminView;
	}

	@Override
	public AddOrgUnitModelAdminView getAddOrgUnitModelAdminView() {

		if (addOrgUnitModelAdminView == null) {

			addOrgUnitModelAdminView = new AddOrgUnitModelAdminView();

		}

		return addOrgUnitModelAdminView;
	}

	@Override
	public ProjectModelsAdminView getProjectModelsAdminView() {

		if (projectModelsAdminView == null) {

			projectModelsAdminView = new ProjectModelsAdminView();

		}

		return projectModelsAdminView;
	}

	@Override
	public AddProjectModelAdminView getAddProjectModelAdminView() {

		if (addProjectModelAdminView == null) {

			addProjectModelAdminView = new AddProjectModelAdminView();

		}

		return addProjectModelAdminView;
	}

	@Override
	public ContactModelsAdminView getContactModelsAdminView() {

		if (contactModelsAdminView == null) {

			contactModelsAdminView = new ContactModelsAdminView();

		}

		return contactModelsAdminView;
	}

	@Override
	public AddContactModelAdminView getAddContactModelAdminView() {

		if (addContactModelAdminView == null) {

			addContactModelAdminView = new AddContactModelAdminView();

		}

		return addContactModelAdminView;
	}

	@Override
	public AddImportationSchemeView getAddImportationSchemeView() {

		if (addImportationSchemeView == null) {

			addImportationSchemeView = new AddImportationSchemeView();

		}

		return addImportationSchemeView;
	}

	@Override
	public ReportModelsAdminView getReportModelsAdminView() {

		if (reportModelsAdminView == null) {

			reportModelsAdminView = new ReportModelsAdminView();

		}

		return reportModelsAdminView;
	}

	@Override
	public EditPhaseModelAdminView getEditPhaseModelAdminView() {

		if (editPhaseModelAdminView == null) {

			editPhaseModelAdminView = new EditPhaseModelAdminView();

		}

		return editPhaseModelAdminView;
	}

	@Override
	public EditLayoutGroupAdminView getEditLayoutGroupAdminView() {

		if (editLayoutGroupAdminView == null) {

			editLayoutGroupAdminView = new EditLayoutGroupAdminView();

		}

		return editLayoutGroupAdminView;
	}

	@Override
	public EditFlexibleElementAdminView getEditFlexibleElementAdminView() {

		if (editFlexibleElementAdminView == null) {

			editFlexibleElementAdminView = new EditFlexibleElementAdminView();

		}

		return editFlexibleElementAdminView;
	}

	@Override
	public AddBudgetSubFieldView getAddBudSubFieldView() {

		if (addBudSubFieldView == null) {

			addBudSubFieldView = new AddBudgetSubFieldView();

		}

		return addBudSubFieldView;
	}

	@Override
	public ImportModelView getImportModelView() {

		if (importModelView == null) {

			importModelView = new ImportModelView();

		}

		return importModelView;
	}

	@Override
	public ImportationSchemeAdminView getImportationSchemeAdminView() {

		if (importationSchemeAdminView == null) {

			importationSchemeAdminView = new ImportationSchemeAdminView();

		}

		return importationSchemeAdminView;
	}

	@Override
	public AddVariableImporationSchemeView getAddVariableImporationSchemeView() {

		if (addVariableImporationSchemeView == null) {

			addVariableImporationSchemeView = new AddVariableImporationSchemeView();

		}

		return addVariableImporationSchemeView;
	}

	@Override
	public AddImportationSchemeModelsAdminView getAddImportationSchemeModelsAdminView() {

		if (addImportationSchemeModelsAdminView == null) {

			addImportationSchemeModelsAdminView = new AddImportationSchemeModelsAdminView();

		}

		return addImportationSchemeModelsAdminView;
	}

	@Override
	public AddMatchingRuleImportationShemeModelsAdminView getAddMatchingRuleImportationShemeModelsAdminView() {

		if (addMatchingRuleImportationShemeModelsAdminView == null) {

			addMatchingRuleImportationShemeModelsAdminView = new AddMatchingRuleImportationShemeModelsAdminView();

		}

		return addMatchingRuleImportationShemeModelsAdminView;
	}

	@Override
	public FileSelectionView getFileSelectionView() {

		if (fileSelectionView == null) {

			fileSelectionView = new FileSelectionView();

		}

		return fileSelectionView;
	}

	// not a singleton, so needs to return a new instance each time
	@Override
	public PhasesPresenter getPhasesPresenter() {
		return new PhasesPresenter(getPhasesView(), this);
	}

	@Override
	public PhasesView getPhasesView() {

		return new PhasesView();
	}

	@Override
	public ProjectLogFrameGrid getProjectLogFrameGrid() {
		if (projectLogFrameGrid == null)
			projectLogFrameGrid = new ProjectLogFrameGrid(getEventBus());

		return projectLogFrameGrid;
	}

	@Override
	public ProjectPivotContainer getProjectPivotContainer() {

		if (projectPivotContainer == null)
			projectPivotContainer = new ProjectPivotContainer(getEventBus(), getDispatch(), getPivotGridPanel(),
					getIStateManager());

		return projectPivotContainer;

	}

	@Override
	public IStateManager getIStateManager() {
		if (iStateManager == null)
			iStateManager = new GXTStateManager();

		return iStateManager;

	}

	@Override
	public PivotGridPanel getPivotGridPanel() {

		if (pivotGridPanel == null)
			pivotGridPanel = new PivotGridPanel(getEventBus(), getDispatch());

		return pivotGridPanel;
	}

	@Override
	public SiteGridPanel getSiteGridPanel() {

		if (siteGridPanel == null)
			siteGridPanel = new SiteGridPanel(getEventBus(), getDispatch(), getAuthenticationProvider());

		return siteGridPanel;

	}

	@Override
	public ComputationTriggerManager getComputationTriggerManager() {

		if (computationTriggerManager == null)
			computationTriggerManager = new ComputationTriggerManager(getClientValueResolver());

		return computationTriggerManager;

	}

	public ClientValueResolver getClientValueResolver() {

		if (clientValueResolver == null)
			clientValueResolver = new ClientValueResolver(getDispatch());

		return clientValueResolver;

	}

	@Override
	public ProjectView getProjectView() {
		   if (projectView == null)
			projectView = new ProjectView();

		return projectView;
	}

	@Override
	public ReportsPresenter getReportsPresenter() {
		if (reportsPresenter == null)
			reportsPresenter = new ReportsPresenter(getReportsView(), this);

		return reportsPresenter;
	}

	@Override
	public ReportsView getReportsView() {

		if (reportsView == null)
			reportsView = new ReportsView();

		return reportsView;
	}

	@Override
	public CalendarPresenter getCalendarPresenter() {
		
		
		if (calendarPresenter == null)
			calendarPresenter = new CalendarPresenter(getCalendarView(), this);

		return calendarPresenter;
		
	}

	@Override
	public CalendarView getCalendarView() {
		if (calendarView == null)
			calendarView = new CalendarView();

		return calendarView;
	}

	@Override
	public FlexibleElementsAdminPresenter<OrgUnitModelDTO> getFlexibleElementsAdminPresenter() {
	
		
		if (flexibleElementsAdminPresenter == null){
			  flexibleElementsAdminPresenter = new FlexibleElementsAdminPresenter<OrgUnitModelDTO>(getFlexibleElementsAdminView(), this);
		}
			

		return flexibleElementsAdminPresenter;
	}

	@Override
	public FlexibleElementsAdminView getFlexibleElementsAdminView() {
		
		if (flexibleElementsAdminView == null)
			flexibleElementsAdminView = new FlexibleElementsAdminView();

		return flexibleElementsAdminView;
	}

	@Override
	public ImportationSchemeModelsAdminView getImportationSchemeModelsAdminView() {
		 
		if (importationSchemeModelsAdminView == null)
			importationSchemeModelsAdminView = new ImportationSchemeModelsAdminView();

		return importationSchemeModelsAdminView;
	}

	@Override
	public ImportationSchemeModelsAdminPresenter<OrgUnitModelDTO> getImportationSchemeModelsAdminPresenter() {
		
		if (importationSchemeModelsAdminPresenter == null)
			importationSchemeModelsAdminPresenter = new ImportationSchemeModelsAdminPresenter<OrgUnitModelDTO>(getImportationSchemeModelsAdminView(), this);

		return importationSchemeModelsAdminPresenter;
	}

	@Override
	public FlexibleElementsAdminPresenter<ProjectModelDTO> getFlexibleElementsAdminPresenter2() {
		if (flexibleElementsAdminPresenter2 == null){
			  flexibleElementsAdminPresenter2 = new FlexibleElementsAdminPresenter<ProjectModelDTO>(getFlexibleElementsAdminView(), this);
		}
			

		return flexibleElementsAdminPresenter2;
	}

	@Override
	public PhaseModelsAdminPresenter getPhaseModelsAdminPresenter() {
		if (phaseModelsAdminPresenter == null)
			phaseModelsAdminPresenter = new PhaseModelsAdminPresenter(getPhaseModelsAdminView(), this);

		return phaseModelsAdminPresenter;
	}

	@Override
	public PhaseModelsAdminView getPhaseModelsAdminView() {
		if (phaseModelsAdminView == null)
			phaseModelsAdminView = new PhaseModelsAdminView();

		return phaseModelsAdminView;
	}

	@Override
	public LogFrameModelsAdminPresenter getLogFrameModelsAdminPresenter() {
		if (logFrameModelsAdminPresenter == null)
			logFrameModelsAdminPresenter = new LogFrameModelsAdminPresenter(getLogFrameModelsAdminView(), this);

		return logFrameModelsAdminPresenter;
	}

	@Override
	public LogFrameModelsAdminView getLogFrameModelsAdminView() {
		if (logFrameModelsAdminView == null)
			logFrameModelsAdminView = new LogFrameModelsAdminView();

		return logFrameModelsAdminView;
	}

	@Override
	public ImportationSchemeModelsAdminPresenter<ProjectModelDTO> getImportationSchemeModelsAdminPresenter2() {
		if (importationSchemeModelsAdminPresenter2 == null)
			importationSchemeModelsAdminPresenter2 = new ImportationSchemeModelsAdminPresenter<ProjectModelDTO>(getImportationSchemeModelsAdminView(), this);

		return importationSchemeModelsAdminPresenter2;
	}

	@Override
	public FlexibleElementsAdminPresenter<ContactModelDTO> getFlexibleElementsAdminPresenter3() {
		if (flexibleElementsAdminPresenter3 == null){
			  flexibleElementsAdminPresenter3 = new FlexibleElementsAdminPresenter<ContactModelDTO>(getFlexibleElementsAdminView(), this);
		}
			
		return flexibleElementsAdminPresenter3;
	}

	@Override
	public ProjectAsyncDAO getProjectAsyncDAO() {
		if (projectAsyncDAO == null)
			projectAsyncDAO = new ProjectAsyncDAO(this);

		return projectAsyncDAO;
	}

	@Override
	public ProjectModelAsyncDAO getProjectModelAsyncDAO() {
		if (projectModelAsyncDAO == null)
			projectModelAsyncDAO = new ProjectModelAsyncDAO(this);

		return projectModelAsyncDAO;
	}

	@Override
	public PhaseAsyncDAO getPhaseAsyncDAO() {
		if (phaseAsyncDAO == null)
			phaseAsyncDAO = new PhaseAsyncDAO(getPhaseModelAsyncDAO());

		return phaseAsyncDAO;
	}

	@Override
	public LogFrameAsyncDAO getLogFrameAsyncDAO() {
		if (logFrameAsyncDAO == null)
			logFrameAsyncDAO = new LogFrameAsyncDAO();

		return logFrameAsyncDAO;
	}

	@Override
	public ValueAsyncDAO getValueAsyncDAO() {
		if (valueAsyncDAO == null)
			valueAsyncDAO = new ValueAsyncDAO(getFileDataAsyncDAO());

		return valueAsyncDAO;
	}

	@Override
	public OrgUnitModelAsyncDAO getOrgUnitModelAsyncDAO() {
		if (orgUnitModelAsyncDAO == null)
			orgUnitModelAsyncDAO = new OrgUnitModelAsyncDAO();

		return orgUnitModelAsyncDAO;
	}

	@Override
	public CountryAsyncDAO getCountryAsyncDAO() {
		if (countryAsyncDAO == null)
			countryAsyncDAO = new CountryAsyncDAO();

		return countryAsyncDAO;
	}

	@Override
	public PhaseModelAsyncDAO getPhaseModelAsyncDAO() {
		if (phaseModelAsyncDAO == null)
			phaseModelAsyncDAO = new PhaseModelAsyncDAO();

		return phaseModelAsyncDAO;
	}

	@Override
	public ComputationAsyncDAO getComputationAsyncDAO() {
		if (computationAsyncDAO == null)
			computationAsyncDAO = new ComputationAsyncDAO();

		return computationAsyncDAO;
	}
	////////////////////
	@Override
	public BatchCommandAsyncHandler getBatchCommandAsyncHandler() {
		if (batchCommandAsyncHandler == null)
		       batchCommandAsyncHandler = new BatchCommandAsyncHandler();

			return batchCommandAsyncHandler;
	}  
	@Override
	public CreateEntityAsyncHandler getCreateEntityAsyncHandler() {
		if (createEntityAsyncHandler == null)
		       createEntityAsyncHandler = new CreateEntityAsyncHandler(this);

			return createEntityAsyncHandler;
	}  
	@Override
	public DeleteAsyncHandler getDeleteAsyncHandler() {
		if (deleteAsyncHandler == null)
		       deleteAsyncHandler = new DeleteAsyncHandler(this);

			return deleteAsyncHandler;
	}  
	@Override
	public GetCalendarAsyncHandler getGetCalendarAsyncHandler() {
		if (getCalendarAsyncHandler == null)
		       getCalendarAsyncHandler = new GetCalendarAsyncHandler(this);

			return getCalendarAsyncHandler;
	}  
	@Override
	public GetCategoriesAsyncHandler getGetCategoriesAsyncHandler() {
		if (getCategoriesAsyncHandler == null)
		       getCategoriesAsyncHandler = new GetCategoriesAsyncHandler(this);

			return getCategoriesAsyncHandler;
	}  
	@Override
	public GetCountriesAsyncHandler getGetCountriesAsyncHandler() {
		if (getCountriesAsyncHandler == null)
		       getCountriesAsyncHandler = new GetCountriesAsyncHandler(getCountryAsyncDAO());

			return getCountriesAsyncHandler;
	}  
	@Override
	public GetCountryAsyncHandler getGetCountryAsyncHandler() {
		if (getCountryAsyncHandler == null)
		       getCountryAsyncHandler = new GetCountryAsyncHandler(getCountryAsyncDAO());

			return getCountryAsyncHandler;
	}  
	@Override
	public GetHistoryAsyncHandler getGetHistoryAsyncHandler() {
		if (getHistoryAsyncHandler == null)
		       getHistoryAsyncHandler = new GetHistoryAsyncHandler(getHistoryAsyncDAO());

			return getHistoryAsyncHandler;
	}  
	@Override
	public GetLinkedProjectsAsyncHandler getGetLinkedProjectsAsyncHandler() {
		if (getLinkedProjectsAsyncHandler == null)
		       getLinkedProjectsAsyncHandler = new GetLinkedProjectsAsyncHandler(getProjectAsyncDAO());

			return getLinkedProjectsAsyncHandler;
	}  
	@Override
	public GetMonitoredPointsAsyncHandler getGetMonitoredPointsAsyncHandler() {
		if (getMonitoredPointsAsyncHandler == null)
		       getMonitoredPointsAsyncHandler = new GetMonitoredPointsAsyncHandler(getMonitoredPointAsyncDAO(), getProjectAsyncDAO());

			return getMonitoredPointsAsyncHandler;
	}  
	@Override
	public GetOrganizationAsyncHandler getGetOrganizationAsyncHandler() {
		if (getOrganizationAsyncHandler == null)
		       getOrganizationAsyncHandler = new GetOrganizationAsyncHandler(getOrganizationAsyncDAO());

			return getOrganizationAsyncHandler;
	}  
	@Override
	public GetOrgUnitAsyncHandler getGetOrgUnitAsyncHandler() {
		if (getOrgUnitAsyncHandler == null)
		       getOrgUnitAsyncHandler = new GetOrgUnitAsyncHandler(getOrgUnitAsyncDAO());

			return getOrgUnitAsyncHandler;
	}  
	@Override
	public GetOrgUnitsAsyncHandler getGetOrgUnitsAsyncHandler() {
		if (getOrgUnitsAsyncHandler == null)
		       getOrgUnitsAsyncHandler = new GetOrgUnitsAsyncHandler(getOrgUnitAsyncDAO());

			return getOrgUnitsAsyncHandler;
	}  
	@Override
	public GetProfilesAsyncHandler getGetProfilesAsyncHandler() {
		if (getProfilesAsyncHandler == null)
		       getProfilesAsyncHandler = new GetProfilesAsyncHandler(getProfileAsyncDAO());

			return getProfilesAsyncHandler;
	}  
	@Override
	public GetProjectAsyncHandler getGetProjectAsyncHandler() {
		if (getProjectAsyncHandler == null)
		       getProjectAsyncHandler = new GetProjectAsyncHandler(getProjectAsyncDAO());

			return getProjectAsyncHandler;
	}  
	@Override
	public GetProjectsAsyncHandler getGetProjectsAsyncHandler() {
		if (getProjectsAsyncHandler == null)
		       getProjectsAsyncHandler = new GetProjectsAsyncHandler(getAuthenticationProvider().get(), getProjectAsyncDAO(), getOrgUnitAsyncDAO());

			return getProjectsAsyncHandler;
	}  
	@Override
	public GetProjectsFromIdAsyncHandler getGetProjectsFromIdAsyncHandler() {
		if (getProjectsFromIdAsyncHandler == null)
		       getProjectsFromIdAsyncHandler = new GetProjectsFromIdAsyncHandler(getProjectAsyncDAO());

			return getProjectsFromIdAsyncHandler;
	}  
	@Override
	public GetProjectDocumentsAsyncHandler getGetProjectDocumentsAsyncHandler() {
		//here
		if (getProjectDocumentsAsyncHandler == null)
		       getProjectDocumentsAsyncHandler = new GetProjectDocumentsAsyncHandler(getValueAsyncDAO());

			return getProjectDocumentsAsyncHandler;
	}  
	@Override
	public GetProjectReportAsyncHandler getGetProjectReportAsyncHandler() {
		if (getProjectReportAsyncHandler == null)
		       getProjectReportAsyncHandler = new GetProjectReportAsyncHandler(getProjectReportAsyncDAO());

			return getProjectReportAsyncHandler;
	}  
	@Override
	public GetProjectReportsAsyncHandler getGetProjectReportsAsyncHandler() {
		if (getProjectReportsAsyncHandler == null)
		       getProjectReportsAsyncHandler = new GetProjectReportsAsyncHandler(getReportReferenceAsyncDAO());

			return getProjectReportsAsyncHandler;
	}  
	@Override
	public GetProjectTeamMembersAsyncHandler getGetProjectTeamMembersAsyncHandler() {
		if (getProjectTeamMembersAsyncHandler == null)
		       getProjectTeamMembersAsyncHandler = new GetProjectTeamMembersAsyncHandler(getProjectTeamMembersAsyncDAO());

			return getProjectTeamMembersAsyncHandler;
	}  
	@Override
	public GetRemindersAsyncHandler getGetRemindersAsyncHandler() {
		if (getRemindersAsyncHandler == null)
		       getRemindersAsyncHandler = new GetRemindersAsyncHandler(getReminderAsyncDAO(), getProjectAsyncDAO());

			return getRemindersAsyncHandler;
	}  
	@Override
	public GetSitesCountAsyncHandler getGetSitesCountAsyncHandler() {
		if (getSitesCountAsyncHandler == null)
		       getSitesCountAsyncHandler = new GetSitesCountAsyncHandler();

			return getSitesCountAsyncHandler;
	}  
	@Override
	public GetUsersByOrganizationAsyncHandler getGetUsersByOrganizationAsyncHandler() {
		if (getUsersByOrganizationAsyncHandler == null)
		       getUsersByOrganizationAsyncHandler = new GetUsersByOrganizationAsyncHandler(getUserAsyncDAO());

			return getUsersByOrganizationAsyncHandler;
	}  
	@Override
	public GetUsersByOrgUnitAsyncHandler getGetUsersByOrgUnitAsyncHandler() {
		if (getUsersByOrgUnitAsyncHandler == null)
		       getUsersByOrgUnitAsyncHandler = new GetUsersByOrgUnitAsyncHandler(getOrgUnitAsyncDAO(), getUserAsyncDAO());

			return getUsersByOrgUnitAsyncHandler;
	}  
	@Override
	public GetUserUnitsByUserAsyncHandler getGetUserUnitsByUserAsyncHandler() {
		if (getUserUnitsByUserAsyncHandler == null)
		       getUserUnitsByUserAsyncHandler = new GetUserUnitsByUserAsyncHandler(getUserUnitAsyncDAO());

			return getUserUnitsByUserAsyncHandler;
	}  
	@Override
	public GetValueAsyncHandler getGetValueAsyncHandler() {
		if (getValueAsyncHandler == null)
		       getValueAsyncHandler = new GetValueAsyncHandler(getValueAsyncDAO());

			return getValueAsyncHandler;
	}  
	@Override
	public GetValueFromLinkedProjectsAsyncHandler getGetValueFromLinkedProjectsAsyncHandler() {
		if (getValueFromLinkedProjectsAsyncHandler == null)
		       getValueFromLinkedProjectsAsyncHandler = new GetValueFromLinkedProjectsAsyncHandler(this);

			return getValueFromLinkedProjectsAsyncHandler;
	}  
	@Override
	public PrepareFileUploadAsyncHandler getPrepareFileUploadAsyncHandler() {
		if (prepareFileUploadAsyncHandler == null)
		       prepareFileUploadAsyncHandler = new PrepareFileUploadAsyncHandler(getValueAsyncDAO(), getUpdateDiaryAsyncDAO());

			return prepareFileUploadAsyncHandler;
	}  
	@Override
	public SecureNavigationAsyncHandler getSecureNavigationAsyncHandler() {
		if (secureNavigationAsyncHandler == null)
		       secureNavigationAsyncHandler = new SecureNavigationAsyncHandler(getAuthenticationAsyncDAO(), getPageAccessAsyncDAO());

			return secureNavigationAsyncHandler;
	}  
	@Override
	public UpdateEntityAsyncHandler getUpdateEntityAsyncHandler() {
		if (updateEntityAsyncHandler == null)
		       updateEntityAsyncHandler = new UpdateEntityAsyncHandler(this);

			return updateEntityAsyncHandler;
	}  
	@Override
	public UpdateLogFrameAsyncHandler getUpdateLogFrameAsyncHandler() {
		if (updateLogFrameAsyncHandler == null)
		       updateLogFrameAsyncHandler = new UpdateLogFrameAsyncHandler(getProjectAsyncDAO(), getUpdateDiaryAsyncDAO());

			return updateLogFrameAsyncHandler;
	}  
	@Override
	public UpdateMonitoredPointsAsyncHandler getUpdateMonitoredPointsAsyncHandler() {
		if (updateMonitoredPointsAsyncHandler == null)
		       updateMonitoredPointsAsyncHandler = new UpdateMonitoredPointsAsyncHandler(getMonitoredPointAsyncDAO(), getUpdateDiaryAsyncDAO());

			return updateMonitoredPointsAsyncHandler;
	}  
	@Override
	public UpdateProjectAsyncHandler getUpdateProjectAsyncHandler() {
		if (updateProjectAsyncHandler == null)
		       updateProjectAsyncHandler = new UpdateProjectAsyncHandler(this);

			return updateProjectAsyncHandler;
	}  
	@Override
	public UpdateProjectFavoriteAsyncHandler getUpdateProjectFavoriteAsyncHandler() {
		if (updateProjectFavoriteAsyncHandler == null)
		       updateProjectFavoriteAsyncHandler = new UpdateProjectFavoriteAsyncHandler(getProjectAsyncDAO(), getUpdateDiaryAsyncDAO());

			return updateProjectFavoriteAsyncHandler;
	}  
	@Override
	public UpdateProjectTeamMembersAsyncHandler getUpdateProjectTeamMembersAsyncHandler() {
		if (updateProjectTeamMembersAsyncHandler == null)
		       updateProjectTeamMembersAsyncHandler = new UpdateProjectTeamMembersAsyncHandler(getProjectTeamMembersAsyncDAO(), getUpdateDiaryAsyncDAO());

			return updateProjectTeamMembersAsyncHandler;
	}  
	@Override
	public UpdateRemindersAsyncHandler getUpdateRemindersAsyncHandler() {
		if (updateRemindersAsyncHandler == null)
		       updateRemindersAsyncHandler = new UpdateRemindersAsyncHandler(getReminderAsyncDAO(), getUpdateDiaryAsyncDAO());

			return updateRemindersAsyncHandler;
	}

	@Override
	public PersonalCalendarAsyncDAO getPersonalCalendarAsyncDAO() {
		if(personalCalendarAsyncDAO == null)
			personalCalendarAsyncDAO = new PersonalCalendarAsyncDAO();

		return personalCalendarAsyncDAO;
	}

	@Override
	public ActivityCalendarAsyncHandler getActivityCalendarAsyncHandler() {
	
		if (activityCalendarAsyncHandler == null)
			activityCalendarAsyncHandler = new ActivityCalendarAsyncHandler(getProjectAsyncDAO());

		return activityCalendarAsyncHandler;
	}

	@Override
	public PersonalCalendarAsyncHandler getPersonalCalendarAsyncHandler() {
		if (personalCalendarAsyncHandler == null)
			personalCalendarAsyncHandler = new PersonalCalendarAsyncHandler(getPersonalCalendarAsyncDAO());

		return personalCalendarAsyncHandler;
	}

	@Override
	public MonitoredPointCalendarAsyncHandler getMonitoredPointCalendarAsyncHandler() {
		if (monitoredPointCalendarAsyncHandler == null)
			monitoredPointCalendarAsyncHandler = new MonitoredPointCalendarAsyncHandler(getMonitoredPointAsyncDAO());

		return monitoredPointCalendarAsyncHandler;
	}

	@Override
	public ReminderCalendarAsyncHandler getReminderCalendarAsyncHandler() {
		if (reminderCalendarAsyncHandler == null)
			reminderCalendarAsyncHandler = new ReminderCalendarAsyncHandler(getReminderAsyncDAO());

		return reminderCalendarAsyncHandler;
	}

	@Override
	public CategoryTypeAsyncDAO getCategoryTypeAsyncDAO() {
		
		if (categoryTypeAsyncDAO == null)
			categoryTypeAsyncDAO = new CategoryTypeAsyncDAO(getCategoryElementAsyncDAO());

		return categoryTypeAsyncDAO;
	}

	@Override
	public HistoryAsyncDAO getHistoryAsyncDAO() {
		  if (historyAsyncDAO == null)
			historyAsyncDAO = new HistoryAsyncDAO();

		return historyAsyncDAO;
	}

	@Override
	public OrganizationAsyncDAO getOrganizationAsyncDAO() {
		
		if (organizationAsyncDAO == null)
			organizationAsyncDAO = new OrganizationAsyncDAO(getOrgUnitAsyncDAO());

		return organizationAsyncDAO;
	}

	@Override
	public ProfileAsyncDAO getProfileAsyncDAO() {
		if (profileAsyncDAO == null)
			profileAsyncDAO = new ProfileAsyncDAO();

		return profileAsyncDAO;
	}

	@Override
	public UserAsyncDAO getUserAsyncDAO() {
		if (userAsyncDAO == null)
			userAsyncDAO = new UserAsyncDAO(getOrgUnitAsyncDAO());

		return userAsyncDAO;
	}

	@Override
	public UserUnitAsyncDAO getUserUnitAsyncDAO() {
		if (userUnitAsyncDAO == null)
			userUnitAsyncDAO = new UserUnitAsyncDAO();

		return userUnitAsyncDAO;
	}

	@Override
	public AuthenticationAsyncDAO getAuthenticationAsyncDAO() {
		if (authenticationAsyncDAO == null)
			authenticationAsyncDAO = new AuthenticationAsyncDAO();

		return authenticationAsyncDAO;
	}

	@Override
	public PageAccessAsyncDAO getPageAccessAsyncDAO() {
		if (pageAccessAsyncDAO == null)
			pageAccessAsyncDAO = new PageAccessAsyncDAO();

		return pageAccessAsyncDAO;
	}

	@Override
	public ProjectReportAsyncDAO getProjectReportAsyncDAO() {
		if (projectReportAsyncDAO == null)
			projectReportAsyncDAO = new ProjectReportAsyncDAO();

		return projectReportAsyncDAO;
	}

	@Override
	public ProjectTeamMembersAsyncDAO getProjectTeamMembersAsyncDAO() {
		if (projectTeamMembersAsyncDAO == null)
			projectTeamMembersAsyncDAO = new ProjectTeamMembersAsyncDAO();

		return projectTeamMembersAsyncDAO;
	}

	@Override
	public ReportReferenceAsyncDAO getReportReferenceAsyncDAO() {
		if (reportReferenceAsyncDAO == null)
			reportReferenceAsyncDAO = new ReportReferenceAsyncDAO();

		return reportReferenceAsyncDAO;
	}

	@Override
	public CategoryElementAsyncDAO getCategoryElementAsyncDAO() {
		if (categoryElementAsyncDAO == null)
			categoryElementAsyncDAO = new CategoryElementAsyncDAO();

		return categoryElementAsyncDAO;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		if (exceptionHandler == null)
			exceptionHandler = new SecureExceptionHandler(getEventBus());

		return exceptionHandler;
	}
 
	
}
