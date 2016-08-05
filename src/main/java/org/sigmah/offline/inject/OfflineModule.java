package org.sigmah.offline.inject;

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

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.security.SecureDispatchAsync;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;
import org.sigmah.offline.handler.GetCalendarAsyncHandler;
import org.sigmah.offline.handler.GetCategoriesAsyncHandler;
import org.sigmah.offline.handler.GetCountriesAsyncHandler;
import org.sigmah.offline.handler.GetCountryAsyncHandler;
import org.sigmah.offline.handler.GetHistoryAsyncHandler;
import org.sigmah.offline.handler.GetMonitoredPointsAsyncHandler;
import org.sigmah.offline.handler.GetOrgUnitAsyncHandler;
import org.sigmah.offline.handler.GetOrganizationAsyncHandler;
import org.sigmah.offline.handler.GetProjectAsyncHandler;
import org.sigmah.offline.handler.GetProjectsAsyncHandler;
import org.sigmah.offline.handler.GetProjectsFromIdAsyncHandler;
import org.sigmah.offline.handler.GetRemindersAsyncHandler;
import org.sigmah.offline.handler.GetUsersByOrganizationAsyncHandler;
import org.sigmah.offline.handler.GetValueAsyncHandler;
import org.sigmah.offline.handler.PrepareFileUploadAsyncHandler;
import org.sigmah.offline.handler.SecureNavigationAsyncHandler;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.GetCountry;
import org.sigmah.shared.command.GetHistory;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetProjectsFromId;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.PrepareFileUpload;
import org.sigmah.shared.command.SecureNavigationCommand;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.sigmah.offline.handler.BatchCommandAsyncHandler;
import org.sigmah.offline.handler.CreateEntityAsyncHandler;
import org.sigmah.offline.handler.DeleteAsyncHandler;
import org.sigmah.offline.handler.GetLinkedProjectsAsyncHandler;
import org.sigmah.offline.handler.GetProjectDocumentsAsyncHandler;
import org.sigmah.offline.handler.GetProjectReportAsyncHandler;
import org.sigmah.offline.handler.GetProjectReportsAsyncHandler;
import org.sigmah.offline.handler.GetSitesCountAsyncHandler;
import org.sigmah.offline.handler.GetValueFromLinkedProjectsAsyncHandler;
import org.sigmah.offline.handler.UpdateEntityAsyncHandler;
import org.sigmah.offline.handler.UpdateLogFrameAsyncHandler;
import org.sigmah.offline.handler.UpdateMonitoredPointsAsyncHandler;
import org.sigmah.offline.handler.UpdateProjectAsyncHandler;
import org.sigmah.offline.handler.UpdateProjectFavoriteAsyncHandler;
import org.sigmah.offline.handler.UpdateRemindersAsyncHandler;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetLinkedProjects;
import org.sigmah.shared.command.GetProjectDocuments;
import org.sigmah.shared.command.GetProjectReport;
import org.sigmah.shared.command.GetProjectReports;
import org.sigmah.shared.command.GetSitesCount;
import org.sigmah.shared.command.GetValueFromLinkedProjects;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.command.UpdateMonitoredPoints;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.UpdateProjectFavorite;
import org.sigmah.shared.command.UpdateReminders;
import org.sigmah.shared.file.TransfertManager;
import org.sigmah.shared.file.TransfertManagerProvider;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineModule extends AbstractGinModule {

	private LocalDispatchServiceAsync localDispatchAsync;
	private SecureDispatchAsync remoteDispatchAsync;
	
	@Override
	protected void configure() {
        bind(TransfertManager.class).toProvider(TransfertManagerProvider.class).in(Singleton.class);
	}
	
	@Provides
    protected LocalDispatchServiceAsync provideLocalDispatcher(DispatchAsync dispatchAsync, 
			AuthenticationProvider authenticationProvider,
			
			// Injecting command handlers
			BatchCommandAsyncHandler batchCommandAsyncHandler,
			CreateEntityAsyncHandler createEntityAsyncHandler,
			DeleteAsyncHandler deleteAsyncHandler,
			GetCalendarAsyncHandler getCalendarAsyncHandler,
			GetCategoriesAsyncHandler getCategoriesAsyncHandler,
			GetCountriesAsyncHandler getCountriesAsyncHandler,
			GetCountryAsyncHandler getCountryAsyncHandler,
			GetHistoryAsyncHandler getHistoryAsyncHandler,
            GetLinkedProjectsAsyncHandler getLinkedProjectsAsyncHandler,
			GetMonitoredPointsAsyncHandler getMonitoredPointsAsyncHandler,
			GetOrganizationAsyncHandler getOrganizationAsyncHandler,
			GetOrgUnitAsyncHandler getOrgUnitAsyncHandler,
			GetProjectAsyncHandler getProjectAsyncHandler,
			GetProjectsAsyncHandler getProjectsAsyncHandler,
			GetProjectsFromIdAsyncHandler getProjectsFromIdAsyncHandler,
			GetProjectDocumentsAsyncHandler getProjectDocumentsAsyncHandler,
			GetProjectReportAsyncHandler getProjectReportAsyncHandler,
			GetProjectReportsAsyncHandler getProjectReportsAsyncHandler,
			GetRemindersAsyncHandler getRemindersAsyncHandler,
			GetSitesCountAsyncHandler getSitesCountAsyncHandler,
			GetUsersByOrganizationAsyncHandler getUsersByOrganizationAsyncHandler,
			GetValueAsyncHandler getValueAsyncHandler,
			GetValueFromLinkedProjectsAsyncHandler getValueFromLinkedProjectsAsyncHandler,
            PrepareFileUploadAsyncHandler prepareFileUploadAsyncHandler,
			SecureNavigationAsyncHandler secureNavigationAsyncHandler,
			UpdateEntityAsyncHandler updateEntityAsyncHandler,
			UpdateLogFrameAsyncHandler updateLogFrameAsyncHandler,
			UpdateMonitoredPointsAsyncHandler updateMonitoredPointsAsyncHandler,
			UpdateProjectAsyncHandler updateProjectAsyncHandler,
			UpdateProjectFavoriteAsyncHandler updateProjectFavoriteAsyncHandler,
			UpdateRemindersAsyncHandler updateRemindersAsyncHandler) {
		
		localDispatchAsync = new LocalDispatchServiceAsync(authenticationProvider);
		if(dispatchAsync instanceof SecureDispatchAsync) {
			remoteDispatchAsync = (SecureDispatchAsync) dispatchAsync;
			remoteDispatchAsync.setOfflineService(localDispatchAsync);
		} else {
			throw new IllegalArgumentException("Given DispatchAsync type is not supported (SecureDispatchAsync is required).");
		}
		
		batchCommandAsyncHandler.setDispatcher(localDispatchAsync);
		
        registerHandler(BatchCommand.class, batchCommandAsyncHandler);
        registerHandler(CreateEntity.class, createEntityAsyncHandler);
        registerHandler(Delete.class, deleteAsyncHandler);
        registerHandler(GetCalendar.class, getCalendarAsyncHandler);
        registerHandler(GetCategories.class, getCategoriesAsyncHandler);
        registerHandler(GetCountries.class, getCountriesAsyncHandler);
        registerHandler(GetCountry.class, getCountryAsyncHandler);
        registerHandler(GetHistory.class, getHistoryAsyncHandler);
        registerHandler(GetLinkedProjects.class, getLinkedProjectsAsyncHandler);
        registerHandler(GetMonitoredPoints.class, getMonitoredPointsAsyncHandler);
        registerHandler(GetOrganization.class, getOrganizationAsyncHandler);
        registerHandler(GetOrgUnit.class, getOrgUnitAsyncHandler);
        registerHandler(GetProject.class, getProjectAsyncHandler);
        registerHandler(GetProjects.class, getProjectsAsyncHandler);
        registerHandler(GetProjectsFromId.class, getProjectsFromIdAsyncHandler);
        registerHandler(GetProjectDocuments.class, getProjectDocumentsAsyncHandler);
        registerHandler(GetProjectReport.class, getProjectReportAsyncHandler);
        registerHandler(GetProjectReports.class, getProjectReportsAsyncHandler);
        registerHandler(GetReminders.class, getRemindersAsyncHandler);
        registerHandler(GetSitesCount.class, getSitesCountAsyncHandler);
        registerHandler(GetUsersByOrganization.class, getUsersByOrganizationAsyncHandler);
        registerHandler(GetValue.class, getValueAsyncHandler);
        registerHandler(GetValueFromLinkedProjects.class, getValueFromLinkedProjectsAsyncHandler);
		registerHandler(PrepareFileUpload.class, prepareFileUploadAsyncHandler);
        registerHandler(SecureNavigationCommand.class, secureNavigationAsyncHandler);
        registerHandler(UpdateEntity.class, updateEntityAsyncHandler);
        registerHandler(UpdateLogFrame.class, updateLogFrameAsyncHandler);
		registerHandler(UpdateMonitoredPoints.class, updateMonitoredPointsAsyncHandler);
        registerHandler(UpdateProject.class, updateProjectAsyncHandler);
        registerHandler(UpdateProjectFavorite.class, updateProjectFavoriteAsyncHandler);
		registerHandler(UpdateReminders.class, updateRemindersAsyncHandler);
				
        return localDispatchAsync;
	}
	
	private <C extends Command<R>, R extends Result> void registerHandler(Class<C> commandClass, AsyncCommandHandler<C, R> handler) {
		localDispatchAsync.registerHandler(commandClass, handler);
		
		if(handler instanceof DispatchListener) {
			final DispatchListener<C, R> listener = (DispatchListener<C, R>)handler;
			remoteDispatchAsync.registerListener(commandClass, listener);
		}
	}
}
