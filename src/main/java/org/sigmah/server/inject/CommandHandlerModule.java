package org.sigmah.server.inject;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import org.sigmah.server.handler.AddOrgUnitHandler;
import org.sigmah.server.handler.AmendmentActionCommandHandler;
import org.sigmah.server.handler.AutomatedImportHandler;
import org.sigmah.server.handler.BackupArchiveManagementHandler;
import org.sigmah.server.handler.BatchCommandHandler;
import org.sigmah.server.handler.ChangePasswordCommandHandler;
import org.sigmah.server.handler.ChangePhaseHandler;
import org.sigmah.server.handler.CheckContactDuplicationHandler;
import org.sigmah.server.handler.CheckModelUsageHandler;
import org.sigmah.server.handler.CopyLogFrameHandler;
import org.sigmah.server.handler.CreateEntityHandler;
import org.sigmah.server.handler.DeactivateUsersHandler;
import org.sigmah.server.handler.DedupeContactHandler;
import org.sigmah.server.handler.DeleteCategoriesHandler;
import org.sigmah.server.handler.DeleteFlexibleElementsHandler;
import org.sigmah.server.handler.DeleteHandler;
import org.sigmah.server.handler.DeleteImportationSchemeModelsHandler;
import org.sigmah.server.handler.DeleteImportationSchemesHandler;
import org.sigmah.server.handler.DeletePrivacyGroupsHandler;
import org.sigmah.server.handler.DeleteProfilesHandler;
import org.sigmah.server.handler.DeleteReportModelsHandler;
import org.sigmah.server.handler.DisableFlexibleElementsHandler;
import org.sigmah.server.handler.DownloadSliceHandler;
import org.sigmah.server.handler.GenerateElementHandler;
import org.sigmah.server.handler.GetAdminEntitiesHandler;
import org.sigmah.server.handler.GetAvailableFrameworksHandler;
import org.sigmah.server.handler.GetAvailableStatusForModelHandler;
import org.sigmah.server.handler.GetBaseMapsHandler;
import org.sigmah.server.handler.GetCalendarHandler;
import org.sigmah.server.handler.GetCategoriesHandler;
import org.sigmah.server.handler.GetContactDuplicatedPropertiesHandler;
import org.sigmah.server.handler.GetContactHandler;
import org.sigmah.server.handler.GetContactHistoryHandler;
import org.sigmah.server.handler.GetContactModelCopyHandler;
import org.sigmah.server.handler.GetContactModelHandler;
import org.sigmah.server.handler.GetContactModelsHandler;
import org.sigmah.server.handler.GetContactRelationshipsHandler;
import org.sigmah.server.handler.GetContactsHandler;
import org.sigmah.server.handler.GetCountriesHandler;
import org.sigmah.server.handler.GetCountryHandler;
import org.sigmah.server.handler.GetFilesFromFavoriteProjectsHandler;
import org.sigmah.server.handler.GetFrameworkFulfillmentsByProjectModelIdHandler;
import org.sigmah.server.handler.GetGlobalContactExportSettingsHandler;
import org.sigmah.server.handler.GetGlobalContactExportsHandler;
import org.sigmah.server.handler.GetGlobalExportSettingsHandler;
import org.sigmah.server.handler.GetGlobalExportsHandler;
import org.sigmah.server.handler.GetHistoryHandler;
import org.sigmah.server.handler.GetImportInformationHandler;
import org.sigmah.server.handler.GetImportationSchemeModelsHandler;
import org.sigmah.server.handler.GetImportationSchemesHandler;
import org.sigmah.server.handler.GetIndicatorDataSourcesHandler;
import org.sigmah.server.handler.GetIndicatorsHandler;
import org.sigmah.server.handler.GetLayoutGroupIterationsHandler;
import org.sigmah.server.handler.GetLinkedProjectsHandler;
import org.sigmah.server.handler.GetMainSiteHandler;
import org.sigmah.server.handler.GetMonitoredPointsHandler;
import org.sigmah.server.handler.GetMonthlyReportsHandler;
import org.sigmah.server.handler.GetOrgUnitHandler;
import org.sigmah.server.handler.GetOrgUnitModelCopyHandler;
import org.sigmah.server.handler.GetOrgUnitModelHandler;
import org.sigmah.server.handler.GetOrgUnitModelsHandler;
import org.sigmah.server.handler.GetOrgUnitsByModelHandler;
import org.sigmah.server.handler.GetOrgUnitsHandler;
import org.sigmah.server.handler.GetOrganizationHandler;
import org.sigmah.server.handler.GetPrivacyGroupsHandler;
import org.sigmah.server.handler.GetProfilesHandler;
import org.sigmah.server.handler.GetProfilesWithDetailsHandler;
import org.sigmah.server.handler.GetProjectDocumentsHandler;
import org.sigmah.server.handler.GetProjectHandler;
import org.sigmah.server.handler.GetProjectModelCopyHandler;
import org.sigmah.server.handler.GetProjectModelHandler;
import org.sigmah.server.handler.GetProjectModelsHandler;
import org.sigmah.server.handler.GetProjectReportHandler;
import org.sigmah.server.handler.GetProjectReportModelsHandler;
import org.sigmah.server.handler.GetProjectReportsHandler;
import org.sigmah.server.handler.GetProjectTeamMembersHandler;
import org.sigmah.server.handler.GetProjectsByModelHandler;
import org.sigmah.server.handler.GetProjectsFromIdHandler;
import org.sigmah.server.handler.GetProjectsHandler;
import org.sigmah.server.handler.GetPropertiesHandler;
import org.sigmah.server.handler.GetRemindersHandler;
import org.sigmah.server.handler.GetReportDefHandler;
import org.sigmah.server.handler.GetReportElementsHandler;
import org.sigmah.server.handler.GetReportModelsHandler;
import org.sigmah.server.handler.GetReportTemplatesHandler;
import org.sigmah.server.handler.GetSchemaHandler;
import org.sigmah.server.handler.GetSitePointsHandler;
import org.sigmah.server.handler.GetSitesCountHandler;
import org.sigmah.server.handler.GetSitesHandler;
import org.sigmah.server.handler.GetSyncRegionsHandler;
import org.sigmah.server.handler.GetTestProjectsHandler;
import org.sigmah.server.handler.GetUserDatabaseHandler;
import org.sigmah.server.handler.GetUserUnitsByUserHandler;
import org.sigmah.server.handler.GetUsersByOrgUnitHandler;
import org.sigmah.server.handler.GetUsersByOrganizationHandler;
import org.sigmah.server.handler.GetUsersWithProfilesHandler;
import org.sigmah.server.handler.GetValueFromLinkedProjectsHandler;
import org.sigmah.server.handler.GetValueHandler;
import org.sigmah.server.handler.LoginCommandHandler;
import org.sigmah.server.handler.MoveOrgUnitHandler;
import org.sigmah.server.handler.PasswordManagementCommandHandler;
import org.sigmah.server.handler.PingHandler;
import org.sigmah.server.handler.PrepareFileUploadHandler;
import org.sigmah.server.handler.PromoteProjectReportDraftHandler;
import org.sigmah.server.handler.RemoveOrgUnitHandler;
import org.sigmah.server.handler.RemoveProjectReportDraftHandler;
import org.sigmah.server.handler.SecureNavigationCommandHandler;
import org.sigmah.server.handler.SendProbeReportHandler;
import org.sigmah.server.handler.SynchronizeHandler;
import org.sigmah.server.handler.UpdateContactHandler;
import org.sigmah.server.handler.UpdateEntityHandler;
import org.sigmah.server.handler.UpdateGlobalContactExportSettingsHandler;
import org.sigmah.server.handler.UpdateGlobalExportSettingsHandler;
import org.sigmah.server.handler.UpdateLayoutGroupIterationsHandler;
import org.sigmah.server.handler.UpdateLogFrameHandler;
import org.sigmah.server.handler.UpdateMonitoredPointsHandler;
import org.sigmah.server.handler.UpdateMonthlyReportsHandler;
import org.sigmah.server.handler.UpdateOrganizationHandler;
import org.sigmah.server.handler.UpdateProjectFavoriteHandler;
import org.sigmah.server.handler.UpdateProjectHandler;
import org.sigmah.server.handler.UpdateProjectReportModelHandler;
import org.sigmah.server.handler.UpdateProjectTeamMembersHandler;
import org.sigmah.server.handler.UpdateRemindersHandler;
import org.sigmah.server.handler.UpdateSubscriptionHandler;
import org.sigmah.server.handler.UploadSliceHandler;
import org.sigmah.server.inject.dispatch.AbstractCommandHandlerModule;
import org.sigmah.server.search.FilesSolrManagerHandler;
import org.sigmah.shared.command.*;

/**
 * <p>
 * Command-Handler module. Installs automatically dispatch module.
 * </p>
 * <p>
 * Simply bind command classes to their corresponding handler class.
 * </p>
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class CommandHandlerModule extends AbstractCommandHandlerModule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureHandlers() {
		// Thank you for maintaining alphabetical order.
		bindHandler(AddOrgUnit.class, AddOrgUnitHandler.class);
		bindHandler(AmendmentActionCommand.class, AmendmentActionCommandHandler.class);
		bindHandler(AutomatedImport.class, AutomatedImportHandler.class);
		bindHandler(BackupArchiveManagementCommand.class, BackupArchiveManagementHandler.class);
		bindHandler(FilesSolrIndexCommand.class, FilesSolrManagerHandler.class);
		bindHandler(BatchCommand.class, BatchCommandHandler.class);
		bindHandler(ChangePasswordCommand.class, ChangePasswordCommandHandler.class);
		bindHandler(ChangePhase.class, ChangePhaseHandler.class);
		bindHandler(CheckContactDuplication.class, CheckContactDuplicationHandler.class);
		bindHandler(CheckModelUsage.class, CheckModelUsageHandler.class);
		bindHandler(CreateEntity.class, CreateEntityHandler.class);
		bindHandler(CopyLogFrame.class, CopyLogFrameHandler.class);
		bindHandler(DeactivateUsers.class, DeactivateUsersHandler.class);
		bindHandler(DedupeContact.class, DedupeContactHandler.class);
		bindHandler(DisableFlexibleElements.class, DisableFlexibleElementsHandler.class);
		bindHandler(DeleteCategories.class, DeleteCategoriesHandler.class);
		bindHandler(DeleteFlexibleElements.class, DeleteFlexibleElementsHandler.class);
		bindHandler(Delete.class, DeleteHandler.class);
		bindHandler(DeleteImportationSchemeModels.class, DeleteImportationSchemeModelsHandler.class);
		bindHandler(DeleteImportationSchemes.class, DeleteImportationSchemesHandler.class);
		bindHandler(DeletePrivacyGroups.class, DeletePrivacyGroupsHandler.class);
		bindHandler(DeleteProfiles.class, DeleteProfilesHandler.class);
		bindHandler(DeleteReportModels.class, DeleteReportModelsHandler.class);
		bindHandler(DownloadSlice.class, DownloadSliceHandler.class);
		bindHandler(GenerateElement.class, GenerateElementHandler.class);
		bindHandler(GetAdminEntities.class, GetAdminEntitiesHandler.class);
		bindHandler(GetAvailableFrameworks.class, GetAvailableFrameworksHandler.class);
		bindHandler(GetAvailableStatusForModel.class, GetAvailableStatusForModelHandler.class);
		bindHandler(GetBaseMaps.class, GetBaseMapsHandler.class);
		bindHandler(GetCalendar.class, GetCalendarHandler.class);
		bindHandler(GetCategories.class, GetCategoriesHandler.class);
		bindHandler(GetContact.class, GetContactHandler.class);
		bindHandler(GetContacts.class, GetContactsHandler.class);
		bindHandler(GetContactDuplicatedProperties.class, GetContactDuplicatedPropertiesHandler.class);
		bindHandler(GetContactHistory.class, GetContactHistoryHandler.class);
		bindHandler(GetContactModel.class, GetContactModelHandler.class);
		bindHandler(GetContactModelCopy.class, GetContactModelCopyHandler.class);
		bindHandler(GetContactModels.class, GetContactModelsHandler.class);
		bindHandler(GetContactRelationships.class, GetContactRelationshipsHandler.class);
		bindHandler(GetCountries.class, GetCountriesHandler.class);
		bindHandler(GetCountry.class, GetCountryHandler.class);
		bindHandler(GetFilesFromFavoriteProjects.class, GetFilesFromFavoriteProjectsHandler.class);
		bindHandler(GetFrameworkFulfillmentsByProjectModelId.class, GetFrameworkFulfillmentsByProjectModelIdHandler.class);
		bindHandler(GetGlobalContactExportSettings.class, GetGlobalContactExportSettingsHandler.class);
		bindHandler(GetGlobalExportSettings.class, GetGlobalExportSettingsHandler.class);
		bindHandler(GetGlobalContactExports.class, GetGlobalContactExportsHandler.class);
		bindHandler(GetGlobalExports.class, GetGlobalExportsHandler.class);
		bindHandler(GetHistory.class, GetHistoryHandler.class);
		bindHandler(GetImportationSchemeModels.class, GetImportationSchemeModelsHandler.class);
		bindHandler(GetImportationSchemes.class, GetImportationSchemesHandler.class);
		bindHandler(GetImportInformation.class, GetImportInformationHandler.class);
		bindHandler(GetIndicatorDataSources.class, GetIndicatorDataSourcesHandler.class);
		bindHandler(GetIndicators.class, GetIndicatorsHandler.class);
		bindHandler(GetLayoutGroupIterations.class, GetLayoutGroupIterationsHandler.class);
		bindHandler(GetLinkedProjects.class, GetLinkedProjectsHandler.class);
		bindHandler(GetMainSite.class, GetMainSiteHandler.class);
		bindHandler(GetMonitoredPoints.class, GetMonitoredPointsHandler.class);
		bindHandler(GetMonthlyReports.class, GetMonthlyReportsHandler.class);
		bindHandler(GetOrganization.class, GetOrganizationHandler.class);
		bindHandler(GetOrgUnit.class, GetOrgUnitHandler.class);
		bindHandler(GetOrgUnits.class, GetOrgUnitsHandler.class);
		bindHandler(GetOrgUnitModelCopy.class, GetOrgUnitModelCopyHandler.class);
		bindHandler(GetOrgUnitModel.class, GetOrgUnitModelHandler.class);
		bindHandler(GetOrgUnitModels.class, GetOrgUnitModelsHandler.class);
		bindHandler(GetOrgUnitsByModel.class, GetOrgUnitsByModelHandler.class);
		bindHandler(GetUserUnitsByUser.class, GetUserUnitsByUserHandler.class);
		bindHandler(GetPrivacyGroups.class, GetPrivacyGroupsHandler.class);
		bindHandler(GetProfiles.class, GetProfilesHandler.class);
		bindHandler(GetProfilesWithDetails.class, GetProfilesWithDetailsHandler.class);
		bindHandler(GetProject.class, GetProjectHandler.class);
		bindHandler(GetProjectDocuments.class, GetProjectDocumentsHandler.class);
		bindHandler(GetProjectModelCopy.class, GetProjectModelCopyHandler.class);
		bindHandler(GetProjectModel.class, GetProjectModelHandler.class);
		bindHandler(GetProjectModels.class, GetProjectModelsHandler.class);
		bindHandler(GetProjectReport.class, GetProjectReportHandler.class);
		bindHandler(GetProjectReportModels.class, GetProjectReportModelsHandler.class);
		bindHandler(GetProjectReports.class, GetProjectReportsHandler.class);
		bindHandler(GetProjects.class, GetProjectsHandler.class);
		bindHandler(GetProjectsByModel.class, GetProjectsByModelHandler.class);
		bindHandler(GetProjectsFromId.class, GetProjectsFromIdHandler.class);
		bindHandler(GetProjectTeamMembers.class, GetProjectTeamMembersHandler.class);
		bindHandler(GetProperties.class, GetPropertiesHandler.class);
		bindHandler(GetReminders.class, GetRemindersHandler.class);
		bindHandler(GetReportDef.class, GetReportDefHandler.class);
		bindHandler(GetReportElements.class, GetReportElementsHandler.class);
		bindHandler(GetReportModels.class, GetReportModelsHandler.class);
		bindHandler(GetReportTemplates.class, GetReportTemplatesHandler.class);
		bindHandler(GetSchema.class, GetSchemaHandler.class);
		bindHandler(GetSitePoints.class, GetSitePointsHandler.class);
		bindHandler(GetSites.class, GetSitesHandler.class);
		bindHandler(GetSitesCount.class, GetSitesCountHandler.class);
		bindHandler(GetSyncRegions.class, GetSyncRegionsHandler.class);
		bindHandler(GetTestProjects.class, GetTestProjectsHandler.class);
		bindHandler(GetUserDatabase.class, GetUserDatabaseHandler.class);
		bindHandler(GetUsersByOrganization.class, GetUsersByOrganizationHandler.class);
		bindHandler(GetUsersByOrgUnit.class, GetUsersByOrgUnitHandler.class);
		bindHandler(GetUsersWithProfiles.class, GetUsersWithProfilesHandler.class);
		bindHandler(GetValue.class, GetValueHandler.class);
		bindHandler(GetValueFromLinkedProjects.class, GetValueFromLinkedProjectsHandler.class);
		bindHandler(LoginCommand.class, LoginCommandHandler.class);
		bindHandler(MoveOrgUnit.class, MoveOrgUnitHandler.class);
		bindHandler(PasswordManagementCommand.class, PasswordManagementCommandHandler.class);
		bindHandler(Ping.class, PingHandler.class);
		bindHandler(PrepareFileUpload.class, PrepareFileUploadHandler.class);
		bindHandler(PromoteProjectReportDraft.class, PromoteProjectReportDraftHandler.class);
		bindHandler(RemoveOrgUnit.class, RemoveOrgUnitHandler.class);
		bindHandler(RemoveProjectReportDraft.class, RemoveProjectReportDraftHandler.class);
		bindHandler(SecureNavigationCommand.class, SecureNavigationCommandHandler.class);
		bindHandler(Synchronize.class, SynchronizeHandler.class);
		bindHandler(UpdateContact.class, UpdateContactHandler.class);
		bindHandler(UpdateEntity.class, UpdateEntityHandler.class);
		bindHandler(UpdateGlobalContactExportSettingsCommand.class, UpdateGlobalContactExportSettingsHandler.class);
		bindHandler(UpdateGlobalExportSettingsCommand.class, UpdateGlobalExportSettingsHandler.class);
		bindHandler(UpdateLayoutGroupIterations.class, UpdateLayoutGroupIterationsHandler.class);
		bindHandler(UpdateLogFrame.class, UpdateLogFrameHandler.class);
		bindHandler(UpdateMonitoredPoints.class, UpdateMonitoredPointsHandler.class);
		bindHandler(UpdateMonthlyReports.class, UpdateMonthlyReportsHandler.class);
		bindHandler(UpdateOrganization.class, UpdateOrganizationHandler.class);
		bindHandler(UpdateProject.class, UpdateProjectHandler.class);
		bindHandler(UpdateProjectFavorite.class, UpdateProjectFavoriteHandler.class);
		bindHandler(UpdateProjectReportModel.class, UpdateProjectReportModelHandler.class);
		bindHandler(UpdateProjectTeamMembers.class, UpdateProjectTeamMembersHandler.class);
		bindHandler(UpdateReminders.class, UpdateRemindersHandler.class);
		bindHandler(UpdateSubscription.class, UpdateSubscriptionHandler.class);
		bindHandler(UploadSlice.class, UploadSliceHandler.class);
		bindHandler(SendProbeReport.class, SendProbeReportHandler.class);
	}

}
