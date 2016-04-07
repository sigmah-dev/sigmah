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


import org.sigmah.server.handler.*;
import org.sigmah.server.inject.dispatch.AbstractCommandHandlerModule;
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
		bindHandler(AddPartner.class, AddPartnerHandler.class);
		bindHandler(AmendmentActionCommand.class, AmendmentActionCommandHandler.class);
		bindHandler(AutomatedImport.class, AutomatedImportHandler.class);
		bindHandler(BackupArchiveManagementCommand.class, BackupArchiveManagementHandler.class);
		bindHandler(BatchCommand.class, BatchCommandHandler.class);
		bindHandler(ChangePasswordCommand.class, ChangePasswordCommandHandler.class);
		bindHandler(ChangePhase.class, ChangePhaseHandler.class);
		bindHandler(CheckModelUsage.class, CheckModelUsageHandler.class);
		bindHandler(CreateEntity.class, CreateEntityHandler.class);
		bindHandler(CopyLogFrame.class, CopyLogFrameHandler.class);
		bindHandler(DeactivateUsers.class, DeactivateUsersHandler.class);
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
		bindHandler(GetCountries.class, GetCountriesHandler.class);
		bindHandler(GetCountry.class, GetCountryHandler.class);
		bindHandler(GetFilesFromFavoriteProjects.class, GetFilesFromFavoriteProjectsHandler.class);
		bindHandler(GetFrameworkFulfillmentsByProjectModelId.class, GetFrameworkFulfillmentsByProjectModelIdHandler.class);
		bindHandler(GetGlobalExportSettings.class, GetGlobalExportSettingsHandler.class);
		bindHandler(GetGlobalExports.class, GetGlobalExportsHandler.class);
		bindHandler(GetHistory.class, GetHistoryHandler.class);
		bindHandler(GetImportationSchemeModels.class, GetImportationSchemeModelsHandler.class);
		bindHandler(GetImportationSchemes.class, GetImportationSchemesHandler.class);
		bindHandler(GetImportInformation.class, GetImportInformationHandler.class);
		bindHandler(GetIndicatorDataSources.class, GetIndicatorDataSourcesHandler.class);
		bindHandler(GetIndicators.class, GetIndicatorsHandler.class);
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
		bindHandler(GetUsers.class, GetUsersHandler.class);
		bindHandler(GetUsersWithProfiles.class, GetUsersWithProfilesHandler.class);
		bindHandler(GetValue.class, GetValueHandler.class);
		bindHandler(LoginCommand.class, LoginCommandHandler.class);
		bindHandler(MoveOrgUnit.class, MoveOrgUnitHandler.class);
		bindHandler(PasswordManagementCommand.class, PasswordManagementCommandHandler.class);
		bindHandler(Ping.class, PingHandler.class);
		bindHandler(PrepareFileUpload.class, PrepareFileUploadHandler.class);
		bindHandler(PromoteProjectReportDraft.class, PromoteProjectReportDraftHandler.class);
		bindHandler(RemoveOrgUnit.class, RemoveOrgUnitHandler.class);
		bindHandler(RemovePartner.class, RemovePartnerHandler.class);
		bindHandler(RemoveProjectReportDraft.class, RemoveProjectReportDraftHandler.class);
		bindHandler(SecureNavigationCommand.class, SecureNavigationCommandHandler.class);
		bindHandler(Synchronize.class, SynchronizeHandler.class);
		bindHandler(UpdateEntity.class, UpdateEntityHandler.class);
		bindHandler(UpdateGlobalExportSettingsCommand.class, UpdateGlobalExportSettingsHandler.class);
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
	}

}
