package org.sigmah.server.handler.util;

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


import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.GlobalPermission;
import org.sigmah.server.domain.profile.OrgUnitProfile;
import org.sigmah.server.domain.profile.PrivacyGroupPermission;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.util.Languages;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;
import org.sigmah.shared.security.UnauthorizedAccessException;
import org.sigmah.shared.util.Month;
import org.sigmah.shared.util.OrgUnitUtils;

/**
 * Convenient methods for {@link CommandHandler} implementations.
 *
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class Handlers {

	private Handlers() {
		// Utility class.
	}

	public static Month monthFromRange(Date date1, Date date2) {

		final Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		if (c1.get(Calendar.DAY_OF_MONTH) != 1) {
			return null;
		}

		final Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		if (c2.get(Calendar.DAY_OF_MONTH) != c2.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			return null;
		}

		if (c2.get(Calendar.MONTH) != c1.get(Calendar.MONTH) || c2.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
			return null;
		}

		return new Month(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH) + 1);
	}

	/**
	 * Creates a new {@link Authentication} with the given arguments.
	 *
	 * @param user
	 *          The {@link User} instance, may be {@code null}.
	 * @param language
	 *          The {@link Language} value, may be {@code null}.
	 * @param mapper
	 *          The {@link Mapper} service.
	 * @return The created {@link Authentication}. Its language property is never {@code null}.
	 */
	public static Authentication createAuthentication(final User user, final Language language,
		Set<Integer> memberOfProjectIds, Set<Integer> secondaryOrgUnitIds, final Mapper mapper) {

		final Organization organization = user.getOrganization();
		final OrgUnitProfile orgUnitWithProfiles = user.getMainOrgUnitWithProfiles();

		final Integer organizationId = organization != null ? organization.getId() : null;
		final String organizationName = organization != null ? organization.getName() : null;
		final String organizationLogo = organization != null ? organization.getLogo() : null;
		final Integer orgUnitId = orgUnitWithProfiles != null && orgUnitWithProfiles.getOrgUnit() != null ? orgUnitWithProfiles.getOrgUnit().getId() : null;

		return new Authentication(user.getId(), user.getEmail(), user.getName(), user.getFirstName(), Languages.notNull(language), organizationId,
			organizationName, organizationLogo, orgUnitId, secondaryOrgUnitIds, Handlers.aggregateProfiles(user, mapper), memberOfProjectIds);
	}

	/**
	 * <p>
	 * Aggregates the list of profiles of a {@code user}.
	 * </p>
	 * <p>
	 * The {@link User} may have several profiles which link it to its {@link OrgUnit}.
	 * This handler merges also all the profiles in one <em>aggregated profile</em>.
	 * </p>
	 * <p>
	 * It returns a map relating the id of a OrgUnit with an aggregated ProfileDTO.
	 * Each OrgUnit is crawled and put into the map with the best matching (i.e. the nearest) ProfileDTO.
	 * </p>
	 *
	 * @param user
	 *          The user.
	 * @param mapper
	 *          The mapper service.
	 * @return The aggregated profile DTO.
	 */
	public static Map<Integer, ProfileDTO> aggregateProfiles(final User user, final Mapper mapper) {
		Map<Integer, ProfileDTO> aggregatedProfiles = new HashMap<>();
		if (user == null) {
			return aggregatedProfiles;
		}

		Map<Integer, Integer> orgUnitDistance = new HashedMap<>();
		for (OrgUnitProfile orgUnitProfile : user.getOrgUnitsWithProfiles()) {
			ProfileDTO aggretatedProfileDTO = new ProfileDTO();
		aggretatedProfileDTO.setName("AGGREGATED_PROFILE");
		aggretatedProfileDTO.setGlobalPermissions(new HashSet<GlobalPermissionEnum>());
		aggretatedProfileDTO.setPrivacyGroups(new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>());

		if (CollectionUtils.isEmpty(orgUnitProfile.getProfiles())) {
			aggregatedProfiles.put(orgUnitProfile.getOrgUnit().getId(), aggretatedProfileDTO);
			continue;
		}

		for (Profile profile : orgUnitProfile.getProfiles()) {
			if (profile.getGlobalPermissions() != null) {
				for (GlobalPermission p : profile.getGlobalPermissions()) {
					// Aggregates global permissions among profiles.
					aggretatedProfileDTO.getGlobalPermissions().add(p.getPermission());
				}
			}

			if (profile.getPrivacyGroupPermissions() != null) {
					for (PrivacyGroupPermission p : profile.getPrivacyGroupPermissions()) {
						PrivacyGroupDTO groupDTO = mapper.map(p.getPrivacyGroup(), new PrivacyGroupDTO());
					// Aggregates privacy groups among profiles.
					if (aggretatedProfileDTO.getPrivacyGroups().get(groupDTO) != PrivacyGroupPermissionEnum.WRITE) {
						aggretatedProfileDTO.getPrivacyGroups().put(groupDTO, p.getPermission());
					}
				}
			}
		}

			List<OrgUnit> orgUnits = new ArrayList<>();
			crawlUnits(orgUnitProfile.getOrgUnit(), orgUnits, true);
			for (int i = 0; i < orgUnits.size(); i++) {
				Integer orgUnitId = orgUnits.get(i).getId();
				if (aggregatedProfiles.containsKey(orgUnitId) && orgUnitDistance.get(orgUnitId) <= i) {
					// The OrgUnit is already inside the Map
					// and the current profile is farther than the one inside the map so let's try another OrgUnitProfile
					// or else
					break;
				}

				aggregatedProfiles.put(orgUnitId, aggretatedProfileDTO);
				orgUnitDistance.put(orgUnitId, i);
			}
		}

		return aggregatedProfiles;
	}

	/**
	 * Adds recursively all the OrgUnits children of a {@code user} in a collection.
	 *
	 * @param user
	 *          The {@link User} from which the hierarchy is traversed.
	 * @param units
	 *          The current collection in which the units are added.
	 * @param addRoot
	 *          If the root must be added too.
	 */
	public static void crawlUnits(final User user, final Collection<OrgUnit> units, final boolean addRoot) {

		if (user == null || user.getOrgUnitsWithProfiles() == null) {
			// No units available.
			return;
		}

		for (OrgUnitProfile orgUnitProfile : user.getOrgUnitsWithProfiles()) {
			crawlUnits(orgUnitProfile.getOrgUnit(), units, addRoot);
		}
	}

	/**
	 * Adds recursively all the children of an unit in a collection.
	 *
	 * @param root
	 *          The root unit from which the hierarchy is traversed.
	 * @param units
	 *          The current collection in which the units are added.
	 * @param addRoot
	 *          If the root must be added too.
	 */
	public static void crawlUnits(final OrgUnit root, final Collection<OrgUnit> units, final boolean addRoot) {

		if (addRoot) {
			units.add(root);
		}

		final Set<OrgUnit> children = root.getChildrenOrgUnits();
		if (children != null) {
			for (final OrgUnit child : children) {
				crawlUnits(child, units, true);
			}
		}
	}

	/**
	 * Returns if the project is visible for the given user.
	 *
	 * @param project
	 *          The project.
	 * @param user
	 *          The user.
	 * @return If the project is visible for the user.
	 */
	public static boolean isProjectVisible(final Project project, final User user) {
		return isProjectAccessible(project, user, false);
	}

	public static boolean isProjectEditable(final Project project, final User user) {
		return isProjectAccessible(project, user, true);
	}

	public static boolean isProjectAccessible(Project project, User user, boolean edition) {
		
		// Checks that the project is not deleted
		if (project.isDeleted()) {
			return false;
		}

		// Owner.
		final User owner = project.getOwner();
		if (owner != null && owner.getId().equals(user.getId())) {
			return true;
		}

		// Manager.
		final User manager = project.getManager();
		if (manager != null && manager.getId().equals(user.getId())) {
			return true;
		}

		// Checks that the user can see this project.
		// let's get the nearest OrgUnitProfile from the target OrgUnit
		int minDistance = Integer.MAX_VALUE;
		OrgUnitProfile targetedOrgUnitProfile = null;
		for (OrgUnitProfile orgUnitProfile : user.getOrgUnitsWithProfiles()) {
			if (Objects.equals(orgUnitProfile.getOrgUnit().getId(), project.getOrgUnit().getId())) {
				// This OrgUnitProfile is directly related to the targeted OrgUnit, so he is obviously the nearest OrgUnitProfile
				targetedOrgUnitProfile = orgUnitProfile;
					break;
				}

			if (minDistance == 1) {
				// The nearest OrgUnitProfile is either the current one or a OrgUnitProfile directly related to the targeted OrgUnit
				continue;
			}

			List<OrgUnit> orgUnits = new ArrayList<>();
			crawlUnits(orgUnitProfile.getOrgUnit(), orgUnits, false);
			int currentDistance = 1;
			for (OrgUnit orgUnit : orgUnits) {
				// This loop is over the distance of the currently selected OrgUnitProfile
				if (currentDistance >= minDistance) {
				break;
			}

				if (Objects.equals(orgUnit.getId(), project.getOrgUnit().getId())) {
					targetedOrgUnitProfile = orgUnitProfile;
					minDistance = currentDistance;
					break;
		}

				currentDistance++;
			}
		}
		if (targetedOrgUnitProfile == null) {
			return false;
		}

		boolean canSeeHisProjects = false;
		boolean canEditHisProjects = false;
		for (Profile profile : targetedOrgUnitProfile.getProfiles()) {
			for (GlobalPermission globalPermission : profile.getGlobalPermissions()) {
				if (globalPermission.getPermission() == GlobalPermissionEnum.EDIT_ALL_PROJECTS) {
					// If the profile has EDIT_ALL_PROJECTS permission, it has VIEW_ALL_PROJECTS too
					return true;
				}
				if (globalPermission.getPermission() == GlobalPermissionEnum.VIEW_ALL_PROJECTS && !edition) {
					return true;
				}
				if (globalPermission.getPermission() == GlobalPermissionEnum.VIEW_MY_PROJECTS) {
					canSeeHisProjects = true;
				} else if (globalPermission.getPermission() == GlobalPermissionEnum.EDIT_PROJECT) {
					canEditHisProjects = true;
					// If the profile has EDIT_PROJECT permission, it has VIEW_MY_PROJECTS too
					canSeeHisProjects = true;
				}
			}
		}

		if ((!edition && !canSeeHisProjects) || (edition && !canEditHisProjects)) {
			return false;
		}

		// Let's see if the user belongs to the project team
		for (User teamMember : project.getTeamMembers()) {
			if (teamMember.equals(user)) {
				return true;
			}
		}
		for (Profile profile : targetedOrgUnitProfile.getProfiles()) {
			if (project.getTeamMemberProfiles().contains(profile)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns if the given {@code orgUnit} is visible to the given {@code user}.
	 *
	 * @param orgUnit
	 *          The org unit.
	 * @param user
	 *          The user (the user's linked OrgUnit must be loaded).
	 * @return {@code true} if the given {@code orgUnit} is visible to the given {@code user}, {@code false} otherwise.
	 * @throws NullPointerException
	 *           If one of the arguments is {@code null}.
	 */
	public static boolean isOrgUnitVisible(final OrgUnit orgUnit, final User user) {

		if (orgUnit.getDeleted() != null) {
			return false;
		}

		// Checks that the user can see this org unit.
		final HashSet<OrgUnit> units = new HashSet<OrgUnit>();
		for (OrgUnitProfile orgUnitProfile : user.getOrgUnitsWithProfiles()) {
			Handlers.crawlUnits(orgUnitProfile.getOrgUnit(), units, true);
		}

		for (final OrgUnit unit : units) {
			if (orgUnit.getId().equals(unit.getId())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Utiliy to check the user's grant for a given permission.
	 * 
	 * @param userOrgUnit
	 *			Link between user and orgunit to check.
	 * @param permission
	 *			Permission to search.
	 * @return <code>true</code> if the given user is granted the given permission,
	 * <code>false</code> otherwise.
	 */
	public static boolean isGranted(final OrgUnitProfile userOrgUnit, final GlobalPermissionEnum permission) {
		List<Profile> profiles = userOrgUnit.getProfiles();

		for (final Profile profile : profiles) {
			if (profile.getGlobalPermissions() != null) {
				for (final GlobalPermission p : profile.getGlobalPermissions()) {
					if (p.getPermission().equals(permission)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isGranted(List<OrgUnitProfile> userUnits, GlobalPermissionEnum permission) {
		for (OrgUnitProfile userUnit : userUnits) {
			if (isGranted(userUnit, permission)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isGranted(List<OrgUnitProfile> userUnits, OrgUnit targetOrgUnit, GlobalPermissionEnum permission) {
		for (OrgUnitProfile userUnit : userUnits) {
			if (!OrgUnitUtils.areOrgUnitsEqualOrParent(userUnit.getOrgUnit(), targetOrgUnit.getId())) {
				continue;
			}
			if (isGranted(userUnit, permission)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Asserts that the user has permission to modify the structure of the given database.
	 * 
	 * NOTE: Design privilege from Activity Info have been removed.
	 * To satisfy this check a user must now be able to view the given database
	 * and must have the {@link GlobalPermissionEnum#EDIT_INDICATOR} permission.
	 * 
	 * @param user
	 *          The user for whom to check permissions.
	 * @param project
	 *          The project the user is trying to modify.
	 * @throws UnauthorizedAccessException
	 *           If the user does not have design permission.
	 */
	public static void assertDesignPrivileges(final User user, final Project project) throws UnauthorizedAccessException {
		
		if (!isGranted(user.getOrgUnitsWithProfiles(), GlobalPermissionEnum.EDIT_INDICATOR)) {
			throw new UnauthorizedAccessException("Access denied to project '" + project.getId() + "'.");
		}
		if (!isProjectVisible(project, user)) {
			throw new UnauthorizedAccessException("Project '" + project.getId() + "' is not visible by user '" + user.getEmail() + "'.");
		}
	}

}
