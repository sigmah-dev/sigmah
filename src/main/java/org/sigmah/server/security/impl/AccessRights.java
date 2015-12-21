package org.sigmah.server.security.impl;

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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.sigmah.client.page.Page;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.shared.command.*;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access rights configuration.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class AccessRights {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AccessRights.class);

	/**
	 * Permissions map linking a secured token to a set of {@link GlobalPermissionEnum}.
	 */
	private static final Map<String, Pair<GrantType, Set<GlobalPermissionEnum>>> permissions = new HashMap<>();

	/**
	 * Unchecked resources tokens (they are always granted).
	 */
	private static final Set<String> grantedTokens = new HashSet<>();

	/**
	 * Token representing <em>missing tokens</em>.
	 * If a token is not declared among security permissions, this token is used.
	 */
	private static final String MISSING_TOKEN = "*";

	/**
	 * Permissions configuration.
	 */
	// TODO Complete permissions.
	static {

		// FIXME For the time being, all missing tokens are considered NOT secured. This line should be deleted in
		// production.
		sperm(MISSING_TOKEN, GrantType.BOTH);

		// Pages.
		sperm(pageToken(Page.LOGIN), GrantType.ANONYMOUS_ONLY);
		sperm(pageToken(Page.RESET_PASSWORD), GrantType.ANONYMOUS_ONLY);
		sperm(pageToken(Page.LOST_PASSWORD), GrantType.ANONYMOUS_ONLY);
		sperm(pageToken(Page.CHANGE_OWN_PASSWORD), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.CHANGE_PASSWORD);
		sperm(pageToken(Page.MOCKUP), GrantType.BOTH);
		sperm(pageToken(Page.CREDITS), GrantType.AUTHENTICATED_ONLY);
		sperm(pageToken(Page.HELP), GrantType.AUTHENTICATED_ONLY);

		sperm(pageToken(Page.DASHBOARD), GrantType.AUTHENTICATED_ONLY);

		sperm(pageToken(Page.PROJECT_DASHBOARD), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS);
		sperm(pageToken(Page.PROJECT_DETAILS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS);
		sperm(pageToken(Page.PROJECT_CALENDAR), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS, GlobalPermissionEnum.VIEW_PROJECT_AGENDA);
		sperm(pageToken(Page.PROJECT_INDICATORS_ENTRIES), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS, GlobalPermissionEnum.VIEW_INDICATOR);
		sperm(pageToken(Page.PROJECT_INDICATORS_MANAGEMENT), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS, GlobalPermissionEnum.VIEW_INDICATOR);
		sperm(pageToken(Page.PROJECT_INDICATORS_MAP), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS, GlobalPermissionEnum.VIEW_INDICATOR);
		sperm(pageToken(Page.PROJECT_LOGFRAME), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS, GlobalPermissionEnum.VIEW_LOGFRAME);
		sperm(pageToken(Page.PROJECT_REPORTS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS);
		sperm(pageToken(Page.PROJECT_TEAM_MEMBERS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS, GlobalPermissionEnum.VIEW_PROJECT_TEAM_MEMBERS);

		sperm(pageToken(Page.INDICATOR_EDIT), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.MANAGE_INDICATOR);
		sperm(pageToken(Page.SITE_EDIT), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.MANAGE_INDICATOR);

		sperm(pageToken(Page.CREATE_PROJECT), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.CREATE_PROJECT);

		sperm(pageToken(Page.ORGUNIT_DASHBOARD), GrantType.AUTHENTICATED_ONLY);
		sperm(pageToken(Page.ORGUNIT_CALENDAR), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT_AGENDA);
		sperm(pageToken(Page.ORGUNIT_DETAILS), GrantType.AUTHENTICATED_ONLY);
		sperm(pageToken(Page.ORGUNIT_REPORTS), GrantType.AUTHENTICATED_ONLY);

		sperm(pageToken(Page.ADMIN_PARAMETERS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_SETTINGS);
		sperm(pageToken(Page.ADMIN_CATEGORIES), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_CATEGORIES);
		sperm(pageToken(Page.ADMIN_ORG_UNITS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_ORG_UNITS);
		sperm(pageToken(Page.ADMIN_USERS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_USERS);
		sperm(pageToken(Page.ADMIN_PROJECTS_MODELS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_PROJECT_MODELS);
		sperm(pageToken(Page.ADMIN_REPORTS_MODELS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_REPORT_MODELS);
		sperm(pageToken(Page.ADMIN_ORG_UNITS_MODELS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_ORG_UNIT_MODELS);
		sperm(pageToken(Page.ADMIN_IMPORTATION_SCHEME), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_IMPORTATION_SCHEMES);
		sperm(pageToken(Page.ADMIN_ADD_IMPORTATION_SCHEME), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_IMPORTATION_SCHEMES);
		sperm(pageToken(Page.ADMIN_ADD_VARIABLE_IMPORTATION_SCHEME), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_IMPORTATION_SCHEMES);

		// Commands.
		sperm(commandToken(AddOrgUnit.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(AddPartner.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(AmendmentActionCommand.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(BackupArchiveManagementCommand.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(BatchCommand.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(ChangePasswordCommand.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.CHANGE_PASSWORD);
		sperm(commandToken(ChangePhase.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.CHANGE_PHASE);
		sperm(commandToken(CheckModelUsage.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(CreateEntity.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(CopyLogFrame.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.EDIT_LOGFRAME);
		sperm(commandToken(DeactivateUsers.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.MANAGE_USERS);
		sperm(commandToken(DisableFlexibleElements.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(DeleteCategories.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(DeleteFlexibleElements.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(Delete.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(DeleteImportationSchemeModels.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(DeleteImportationSchemes.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(DeletePrivacyGroups.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(DeleteProfiles.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.MANAGE_USERS);
		sperm(commandToken(DeleteReportModels.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.MANAGE_REPORT_MODELS);
		sperm(commandToken(DownloadSlice.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GenerateElement.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetAdminEntities.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetAvailableStatusForModel.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetBaseMaps.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetCalendar.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetCategories.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetCountries.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetCountry.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetFilesFromFavoriteProjects.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetGlobalExportSettings.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetGlobalExports.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetHistory.class), GrantType.AUTHENTICATED_ONLY);
		// TODO: Add the missing commands
		sperm(commandToken(GetLinkedProjects.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_MY_PROJECTS);
		sperm(commandToken(GetMonitoredPoints.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetOrganization.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetOrgUnit.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetOrgUnitModel.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProject.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjectDocuments.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjectModel.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjectModels.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjectReport.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjectReports.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjects.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjectsByModel.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjectsFromId.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjectTeamMembers.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT_TEAM_MEMBERS);
		sperm(commandToken(GetReminders.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetUsersByOrganization.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetUsers.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetUsersWithProfiles.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetUsersByOrgUnit.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetValue.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(Synchronize.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(UpdateProject.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(UpdateProjectFavorite.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(UpdateProjectTeamMembers.class), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.EDIT_PROJECT_TEAM_MEMBERS);
		sperm(commandToken(UploadSlice.class), GrantType.AUTHENTICATED_ONLY);

		// Servlet methods.
		sperm(servletToken(Servlet.FILE, ServletMethod.DOWNLOAD_LOGO), GrantType.AUTHENTICATED_ONLY);
		sperm(servletToken(Servlet.FILE, ServletMethod.DOWNLOAD_FILE), GrantType.AUTHENTICATED_ONLY);
		sperm(servletToken(Servlet.FILE, ServletMethod.DOWNLOAD_ARCHIVE), GrantType.AUTHENTICATED_ONLY);
	}

	/**
	 * Granted tokens that are always granted in order to optimize application processes.
	 */
	static {
		grantedTokens.add(commandToken(SecureNavigationCommand.class));
	}

	/**
	 * Grants or refuse {@code user} access to the given {@code token}.
	 *
	 * @param user
	 *          The user (authenticated or anonymous).
	 * @param token
	 *          The resource token (page, command, servlet method, etc.).
	 * @param originPageToken
	 *          The origin page token <em>(TODO Not used yet)</em>.
	 * @param mapper
	 *          The mapper service.
	 * @return {@code true} if the user is granted, {@code false} otherwise.
	 */
	static boolean isGranted(final User user, final String token, final String originPageToken, final Mapper mapper) {

		if (grantedTokens.contains(token)) {
			// Granted tokens ; avoids profile aggregation if user is authenticated.
			return true;
		}

		if (!permissions.containsKey(token)) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("No security permission can be found for token '{}'. Did you forget to declare corresponding 'sperm'?", token);
			}
			return isGranted(user, MISSING_TOKEN, originPageToken, mapper);
		}

		final Pair<GrantType, Set<GlobalPermissionEnum>> grantData = permissions.get(token);
		final GrantType grantType = grantData.left;

		final boolean granted;

		if (user == null || ServletExecutionContext.ANONYMOUS_USER.equals(user)) {
			// Anonymous user.
			granted = grantType != null && grantType != GrantType.AUTHENTICATED_ONLY;

		} else {
			// Authenticated user.
			if (grantType != null && grantType == GrantType.ANONYMOUS_ONLY) {
				granted = false;

			} else {
				final ProfileDTO aggregatedProfile = Handlers.aggregateProfiles(user, mapper);
				granted = CollectionUtils.containsAll(aggregatedProfile.getGlobalPermissions(), grantData.right);
			}
		}

		return granted;
	}

	// -------------------------------------------------------------------------------------
	//
	// TOKEN METHODS.
	//
	// -------------------------------------------------------------------------------------

	/**
	 * Return the <em>resource</em> token for the given servlet arguments.
	 *
	 * @param servlet
	 *          The {@link Servlet} name.
	 * @param method
	 *          The {@link Servlet} method.
	 * @return the <em>resource</em> token for the given servlet arguments, or {@code null}.
	 */
	static String servletToken(final Servlet servlet, final ServletMethod method) {
		if (servlet == null || method == null) {
			return null;
		}
		return servlet.name() + '#' + method.name();
	}

	/**
	 * Return the <em>resource</em> token for the given {@code commandClass}.
	 *
	 * @param commandClass
	 *          The {@link Command} class.
	 * @return the <em>resource</em> token for the given {@code commandClass}, or {@code null}.
	 */
	@SuppressWarnings("rawtypes")
	static String commandToken(final Class<? extends Command> commandClass) {
		if (commandClass == null) {
			return null;
		}
		return commandClass.getName();
	}

	/**
	 * Return the <em>resource</em> token for the given {@code page}.
	 *
	 * @param page
	 *          The {@link Page} instance.
	 * @return the <em>resource</em> token for the given {@code page}, or {@code null}.
	 */
	static String pageToken(final Page page) {
		if (page == null) {
			return null;
		}
		return page.getToken();
	}

	// -------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------

	private static enum GrantType {

		/**
		 * Access granted to <em>anonymous</em> user <b>only</b>.
		 */
		ANONYMOUS_ONLY,

		/**
		 * Access granted to <em>authenticated</em> users <b>only</b>.
		 */
		AUTHENTICATED_ONLY,

		/**
		 * Access granted to <em>anonymous</em> <b>and</b> <em>authenticated</em> users.
		 */
		BOTH;

	}

	/**
	 * <p>
	 * Registers a new <u>S</u>ecurity <u>PERM</u>ission for the given {@code token}.
	 * </p>
	 * <p>
	 * ;-)
	 * </p>
	 *
	 * @param token
	 *          The resource token.
	 * @param grantType
	 *          The grant type, see {@link GrantType}.
	 * @param gpes
	 *          The {@link GlobalPermissionEnum} that the user needs to possess in order to be granted for the
	 *          {@code token}.
	 */
	private static void sperm(final String token, final GrantType grantType, final GlobalPermissionEnum... gpes) {
		permissions.put(token, new Pair<>(grantType, toSet(gpes)));
	}

	/**
	 * Transforms the given {@code gpes} array into a {@link Set}.
	 * Ignores {@code null} values in the process.
	 *
	 * @param gpes
	 *          The {@link GlobalPermissionEnum} array.
	 * @return the given {@code gpes} array transformed into a {@link Set} with no {@code null} values.
	 */
	private static Set<GlobalPermissionEnum> toSet(final GlobalPermissionEnum... gpes) {

		final Set<GlobalPermissionEnum> set = new HashSet<GlobalPermissionEnum>();

		if (ArrayUtils.isEmpty(gpes)) {
			return set;
		}

		for (final GlobalPermissionEnum gpe : gpes) {
			if (gpe == null) {
				continue;
			}
			set.add(gpe);
		}

		return set;
	}

	/**
	 * Utility class constructor.
	 */
	private AccessRights() {
		// Only provides static constants.
	}

}
