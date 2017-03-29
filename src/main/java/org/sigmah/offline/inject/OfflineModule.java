package org.sigmah.offline.inject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineModule  {

//	private LocalDispatchServiceAsync localDispatchAsync;
//	private SecureDispatchAsync remoteDispatchAsync;
//	
//	@Override
//	protected void configure() {
//        bind(TransfertManager.class).toProvider(TransfertManagerProvider.class).in(Singleton.class);
//	}
//	
//    protected LocalDispatchServiceAsync provideLocalDispatcher(DispatchAsync dispatchAsync, 
//			AuthenticationProvider authenticationProvider,
//			
//			// Injecting command handlers
//			BatchCommandAsyncHandler batchCommandAsyncHandler,
//			CreateEntityAsyncHandler createEntityAsyncHandler,
//			DeleteAsyncHandler deleteAsyncHandler,
//			GetCalendarAsyncHandler getCalendarAsyncHandler,
//			GetCategoriesAsyncHandler getCategoriesAsyncHandler,
//			GetCountriesAsyncHandler getCountriesAsyncHandler,
//			GetCountryAsyncHandler getCountryAsyncHandler,
//			GetHistoryAsyncHandler getHistoryAsyncHandler,
//            GetLinkedProjectsAsyncHandler getLinkedProjectsAsyncHandler,
//			GetMonitoredPointsAsyncHandler getMonitoredPointsAsyncHandler,
//			GetOrganizationAsyncHandler getOrganizationAsyncHandler,
//			GetOrgUnitAsyncHandler getOrgUnitAsyncHandler,
//		GetOrgUnitsAsyncHandler getOrgUnitsAsyncHandler,
//		GetProfilesAsyncHandler getProfilesAsyncHandler,
//			GetProjectAsyncHandler getProjectAsyncHandler,
//			GetProjectsAsyncHandler getProjectsAsyncHandler,
//			GetProjectsFromIdAsyncHandler getProjectsFromIdAsyncHandler,
//			GetProjectDocumentsAsyncHandler getProjectDocumentsAsyncHandler,
//			GetProjectReportAsyncHandler getProjectReportAsyncHandler,
//			GetProjectReportsAsyncHandler getProjectReportsAsyncHandler,
//			GetProjectTeamMembersAsyncHandler getProjectTeamMembersAsyncHandler,
//			GetRemindersAsyncHandler getRemindersAsyncHandler,
//			GetSitesCountAsyncHandler getSitesCountAsyncHandler,
//			GetUsersByOrganizationAsyncHandler getUsersByOrganizationAsyncHandler,
//			GetUsersByOrgUnitAsyncHandler getUsersByOrgUnitAsyncHandler,
//		GetUserUnitsByUserAsyncHandler getUserUnitsByUserAsyncHandler,
//			GetValueAsyncHandler getValueAsyncHandler,
//			GetValueFromLinkedProjectsAsyncHandler getValueFromLinkedProjectsAsyncHandler,
//            PrepareFileUploadAsyncHandler prepareFileUploadAsyncHandler,
//			SecureNavigationAsyncHandler secureNavigationAsyncHandler,
//			UpdateEntityAsyncHandler updateEntityAsyncHandler,
//			UpdateLogFrameAsyncHandler updateLogFrameAsyncHandler,
//			UpdateMonitoredPointsAsyncHandler updateMonitoredPointsAsyncHandler,
//			UpdateProjectAsyncHandler updateProjectAsyncHandler,
//			UpdateProjectFavoriteAsyncHandler updateProjectFavoriteAsyncHandler,
//			UpdateProjectTeamMembersAsyncHandler updateProjectTeamMembersAsyncHandler,
//			UpdateRemindersAsyncHandler updateRemindersAsyncHandler) {
//		
//		localDispatchAsync = new LocalDispatchServiceAsync(authenticationProvider);
//		if(dispatchAsync instanceof SecureDispatchAsync) {
//			remoteDispatchAsync = (SecureDispatchAsync) dispatchAsync;
//			remoteDispatchAsync.setOfflineService(localDispatchAsync);
//		} else {
//			throw new IllegalArgumentException("Given DispatchAsync type is not supported (SecureDispatchAsync is required).");
//		}
//		
//		batchCommandAsyncHandler.setDispatcher(localDispatchAsync);
//		
//        registerHandler(BatchCommand.class, batchCommandAsyncHandler);
//        registerHandler(CreateEntity.class, createEntityAsyncHandler);
//        registerHandler(Delete.class, deleteAsyncHandler);
//        registerHandler(GetCalendar.class, getCalendarAsyncHandler);
//        registerHandler(GetCategories.class, getCategoriesAsyncHandler);
//        registerHandler(GetCountries.class, getCountriesAsyncHandler);
//        registerHandler(GetCountry.class, getCountryAsyncHandler);
//        registerHandler(GetHistory.class, getHistoryAsyncHandler);
//        registerHandler(GetLinkedProjects.class, getLinkedProjectsAsyncHandler);
//        registerHandler(GetMonitoredPoints.class, getMonitoredPointsAsyncHandler);
//        registerHandler(GetOrganization.class, getOrganizationAsyncHandler);
//        registerHandler(GetOrgUnit.class, getOrgUnitAsyncHandler);
//		registerHandler(GetOrgUnits.class, getOrgUnitsAsyncHandler);
//		registerHandler(GetProfiles.class, getProfilesAsyncHandler);
//        registerHandler(GetProject.class, getProjectAsyncHandler);
//        registerHandler(GetProjects.class, getProjectsAsyncHandler);
//        registerHandler(GetProjectsFromId.class, getProjectsFromIdAsyncHandler);
//        registerHandler(GetProjectDocuments.class, getProjectDocumentsAsyncHandler);
//        registerHandler(GetProjectReport.class, getProjectReportAsyncHandler);
//        registerHandler(GetProjectReports.class, getProjectReportsAsyncHandler);
//		registerHandler(GetProjectTeamMembers.class, getProjectTeamMembersAsyncHandler);
//        registerHandler(GetReminders.class, getRemindersAsyncHandler);
//        registerHandler(GetSitesCount.class, getSitesCountAsyncHandler);
//        registerHandler(GetUsersByOrganization.class, getUsersByOrganizationAsyncHandler);
//		registerHandler(GetUsersByOrgUnit.class, getUsersByOrgUnitAsyncHandler);
//		registerHandler(GetUserUnitsByUser.class, getUserUnitsByUserAsyncHandler);
//        registerHandler(GetValue.class, getValueAsyncHandler);
//        registerHandler(GetValueFromLinkedProjects.class, getValueFromLinkedProjectsAsyncHandler);
//		registerHandler(PrepareFileUpload.class, prepareFileUploadAsyncHandler);
//        registerHandler(SecureNavigationCommand.class, secureNavigationAsyncHandler);
//        registerHandler(UpdateEntity.class, updateEntityAsyncHandler);
//        registerHandler(UpdateLogFrame.class, updateLogFrameAsyncHandler);
//		registerHandler(UpdateMonitoredPoints.class, updateMonitoredPointsAsyncHandler);
//        registerHandler(UpdateProject.class, updateProjectAsyncHandler);
//        registerHandler(UpdateProjectFavorite.class, updateProjectFavoriteAsyncHandler);
//		registerHandler(UpdateReminders.class, updateRemindersAsyncHandler);
//		registerHandler(UpdateProjectTeamMembers.class, updateProjectTeamMembersAsyncHandler);
//				
//        return localDispatchAsync;
//	}
//	
//	private <C extends Command<R>, R extends Result> void registerHandler(Class<C> commandClass, AsyncCommandHandler<C, R> handler) {
//		localDispatchAsync.registerHandler(commandClass, handler);
//		
//		if(handler instanceof DispatchListener) {
//			final DispatchListener<C, R> listener = (DispatchListener<C, R>)handler;
//			remoteDispatchAsync.registerListener(commandClass, listener);
//		}
//	}
}
