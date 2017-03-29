package org.sigmah.client;

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


import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.PageManager;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.offline.appcache.ApplicationCacheManager;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.GXT;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;

/**
 * GWT module entry point.
 *
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class Sigmah implements EntryPoint {
	
	/**
	 * Version number. Hard coded to avoid querying the server to identify
	 * the current version number.
	 */
	public static final String VERSION = "2.2-SNAPSHOT";

	/**
	 * our factory for DI 
	 */
	private ClientFactory factory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onModuleLoad() {

		if (Log.isDebugEnabled()) {
			Log.debug("Application > Client init start.");
		}
		
		// Listening ApplicationCache events.
		// Done early to avoid missing some events and to keep track of the
		// last update date.
		ApplicationCacheManager.ensureHandlers();
		
		// GIN injector instantiation.
		if (Log.isDebugEnabled()) {
			Log.debug("Application > Creates GIN injector.");
		}
		factory = GWT.create(ClientFactory.class);
		

		// Set GXT theme.
		if (Log.isDebugEnabled()) {
			Log.debug("Application > Sets GWT default theme.");
		}
		GXT.setDefaultTheme(factory.getTheme(), true);
		

		// Uncaught exception handler.
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("Uncaught exception on client-side.", e);
				}
				// TODO [i18n] Uncaught exception error message.
				N10N.error("An unexpected error has occured.");
			}
		});

		clientInitializing();
		
		Profiler.INSTANCE.setAuthenticationProvider(factory.getAuthenticationProvider());
		Profiler.INSTANCE.setApplicationStateManager(factory.getApplicationStateManager());

		if (Log.isDebugEnabled()) {
			Log.debug("Application > Client init end.");
		}

	}

	/**
	 * Initializes client presenters and requests page access to the {@link PageManager}.
	 */
	private void clientInitializing() {

		if (Log.isDebugEnabled()) {
			Log.debug("Application > Register presenters and navigation handlers.");
		}

		// Offline dispatcher
		factory.getLocalDispatch();
		factory.getApplicationStateManager();
		// Application presenters.
		factory.getApplicationPresenter();
		factory.getHomePresenter();
		factory.getMockUpPresenter();
		factory.getCreditsPresenter();
		factory.getHelpPresenter();
		// Zones.
		factory.getOrganizationBannerPresenter();
		factory.getAuthenticationBannerPresenter();
		factory.getOfflineBannerPresenter();
		factory.getAppLoaderPresenter();
		factory.getMenuBannerPresenter();
		factory.getMessageBannerPresenter();
		
		// Pages.
		factory.getLoginPresenter();
		factory.getLostPasswordPresenter();
		factory.getResetPasswordPresenter();
		factory.getChangeOwnPasswordPresenter();
		factory.getProjectPresenter();
		factory.getProjectDashboardPresenter();
		factory.getProjectLogFramePresenter();
		factory.getProjectDetailsPresenter();
		factory.getProjectTeamMembersPresenter();
		factory.getProjectCalendarPresenter();
		factory.getProjectReportsPresenter();
		factory.getProjectIndicatorEntriesPresenter();
		factory.getProjectIndicatorManagementPresenter();
		factory.getProjectIndicatorMapPresenter();
		factory.getEditIndicatorPresenter();
		factory.getEditSitePresenter();
		factory.getExportContactsPresenter();
		factory.getExportContactsSettingPresenter();
		factory.getExportProjectsPresenter();
		factory.getExportProjectsSettingPresenter();
		factory.getProjectCoreRenameVersionPresenter();
		factory.getProjectCoreDiffPresenter();

		factory.getCreateProjectPresenter();
		factory.getReminderEditPresenter();
		factory.getReminderHistoryPresenter();
		factory.getLinkedProjectPresenter();
		factory.getCalendarEventPresenter();
		factory.getReportCreatePresenter();
		factory.getAttachFilePresenter();
		factory.getImportationPresenter();

		factory.getAdminPresenter();
		factory.getUsersAdminPresenter();
		factory.getParametersAdminPresenter();
		factory.getPrivacyGroupEditPresenter();
		factory.getProfileEditPresenter();
		factory.getUserEditPresenter();
		factory.getOrgUnitsAdminPresenter();
		factory.getAddOrgUnitAdminPresenter();
		factory.getMoveOrgUnitAdminPresenter();
		factory.getCategoriesAdminPresenter();
		factory.getOrgUnitModelsAdminPresenter();
		factory.getAddOrgUnitModelAdminPresenter();
		factory.getProjectModelsAdminPresenter();
		factory.getAddProjectModelAdminPresenter();
		factory.getContactModelsAdminPresenter();
		factory.getAddContactModelAdminPresenter();
		factory.getReportModelsAdminPresenter();
		factory.getEditPhaseModelAdminPresenter();
		factory.getEditLayoutGroupAdminPresenter();
		factory.getEditFlexibleElementAdminPresenter();
		factory.getAddBudgetSubFieldPresenter();
		factory.getImportModelPresenter();
		factory.getImportationShemePresenter();
		factory.getAddVariableImporationSchemePresenter();
		factory.getAddImportationSchemeModelsAdminPresenter();
		factory.getAddMatchingRuleImportationShemeModelsAdminPresenter();

		factory.getOrgUnitPresenter();
		factory.getOrgUnitDashboardPresenter();
		factory.getOrgUnitCalendarPresenter();
		factory.getOrgUnitDetailsPresenter();
		factory.getOrgUnitReportsPresenter();
		factory.getAddImportationSchemePresenter();

		factory.getContactPresenter();
		factory.getContactDetailsPresenter();
		factory.getContactRelationshipsPresenter();
		factory.getContactHistoryPresenter();

		factory.getFileSelectionPresenter();

		// Propagates the network state.
		factory.getApplicationStateManager().fireCurrentState(new Runnable() {

			@Override
			public void run() {
				// Go to the current page.
				factory.getPageManager().fireCurrentPlace();
			}
		});
	}

}
