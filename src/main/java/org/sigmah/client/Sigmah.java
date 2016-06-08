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
	public static final String VERSION = "2.0.2";

	/**
	 * GIN injector.
	 */
	private Injector injector;

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
		injector = GWT.create(Injector.class);
		

		// Set GXT theme.
		if (Log.isDebugEnabled()) {
			Log.debug("Application > Sets GWT default theme.");
		}
		GXT.setDefaultTheme(injector.getTheme(), true);
		

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
		
		Profiler.INSTANCE.setAuthenticationProvider(injector.getAuthenticationProvider());
		Profiler.INSTANCE.setApplicationStateManager(injector.getApplicationStateManager());

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
		injector.getLocalDispatch();
		injector.getApplicationStateManager();
		// Application presenters.
		injector.getApplicationPresenter();
		injector.getHomePresenter();
		injector.getMockUpPresenter();
		injector.getCreditsPresenter();
		injector.getHelpPresenter();
		// Zones.
		injector.getOrganizationBannerPresenter();
		injector.getAuthenticationBannerPresenter();
		injector.getOfflineBannerPresenter();
		injector.getAppLoaderPresenter();
		injector.getMenuBannerPresenter();
		injector.getMessageBannerPresenter();
		
		// Pages.
		injector.getLoginPresenter();
		injector.getLostPasswordPresenter();
		injector.getResetPasswordPresenter();
		injector.getChangeOwnPasswordPresenter();
		injector.getProjectPresenter();
		injector.getProjectDashboardPresenter();
		injector.getProjectLogFramePresenter();
		injector.getProjectDetailsPresenter();
		injector.getProjectCalendarPresenter();
		injector.getProjectReportsPresenter();
		injector.getProjectIndicatorEntriesPresenter();
		injector.getProjectIndicatorManagementPresenter();
		injector.getProjectIndicatorMapPresenter();
		injector.getEditIndicatorPresenter();
		injector.getEditSitePresenter();
		injector.getExportProjectsPresenter();
		injector.getExportProjectsSettingPresenter();
		injector.getProjectCoreRenameVersionPresenter();
		injector.getProjectCoreDiffPresenter();

		injector.getCreateProjectPresenter();
		injector.getReminderEditPresenter();
		injector.getReminderHistoryPresenter();
		injector.getLinkedProjectPresenter();
		injector.getCalendarEventPresenter();
		injector.getReportCreatePresenter();
		injector.getAttachFilePresenter();
		injector.getImportationPresenter();

		injector.getAdminPresenter();
		injector.getUsersAdminPresenter();
		injector.getParametersAdminPresenter();
		injector.getPrivacyGroupEditPresenter();
		injector.getProfileEditPresenter();
		injector.getUserEditPresenter();
		injector.getOrgUnitsAdminPresenter();
		injector.getAddOrgUnitAdminPresenter();
		injector.getMoveOrgUnitAdminPresenter();
		injector.getCategoriesAdminPresenter();
		injector.getOrgUnitModelsAdminPresenter();
		injector.getAddOrgUnitModelAdminPresenter();
		injector.getProjectModelsAdminPresenter();
		injector.getAddProjectModelAdminPresenter();
		injector.getReportModelsAdminPresenter();
		injector.getEditPhaseModelAdminPresenter();
		injector.getEditLayoutGroupAdminPresenter();
		injector.getEditFlexibleElementAdminPresenter();
		injector.getAddBudgetSubFieldPresenter();
		injector.getImportModelPresenter();
		injector.getImportationShemePresenter();
		injector.getAddVariableImporationSchemePresenter();
		injector.getAddImportationSchemeModelsAdminPresenter();
		injector.getAddMatchingRuleImportationShemeModelsAdminPresenter();

		injector.getOrgUnitPresenter();
		injector.getOrgUnitDashboradPresenter();
		injector.getOrgUnitCalendarPresenter();
		injector.getOrgUnitDetailsPresenter();
		injector.getOrgUnitReportsPresenter();
		injector.getAddImportationSchemePresenter();

		injector.getFileSelectionPresenter();

		// Propagates the network state.
		injector.getApplicationStateManager().fireCurrentState(new Runnable() {

			@Override
			public void run() {
				// Go to the current page.
				injector.getPageManager().fireCurrentPlace();
			}
		});
	}

}
