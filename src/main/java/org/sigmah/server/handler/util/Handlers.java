package org.sigmah.server.handler.util;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
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
import org.sigmah.shared.util.Month;

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
	public static Authentication createAuthentication(final User user, final Language language, final Mapper mapper) {

		final Organization organization = user.getOrganization();
		final OrgUnitProfile orgUnitWithProfiles = user.getOrgUnitWithProfiles();

		final Integer organizationId = organization != null ? organization.getId() : null;
		final String organizationName = organization != null ? organization.getName() : null;
		final String organizationLogo = organization != null ? organization.getLogo() : null;
		final Integer orgUnitId = orgUnitWithProfiles != null && orgUnitWithProfiles.getOrgUnit() != null ? orgUnitWithProfiles.getOrgUnit().getId() : null;

		return new Authentication(user.getId(), user.getEmail(), user.getName(), user.getFirstName(), Languages.notNull(language), organizationId,
			organizationName, organizationLogo, orgUnitId, Handlers.aggregateProfiles(user, mapper));
	}

	/**
	 * <p>
	 * Aggregates the list of profiles of a {@code user}.
	 * </p>
	 * <p>
	 * The {@link User} may have several profiles which link it to its {@link OrgUnit}.<br/>
	 * This handler merges also all the profiles in one <em>aggregated profile</em>.
	 * </p>
	 * 
	 * @param user
	 *          The user.
	 * @param mapper
	 *          The mapper service.
	 * @return The aggregated profile DTO.
	 */
	public static ProfileDTO aggregateProfiles(final User user, final Mapper mapper) {

		final ProfileDTO aggretatedProfileDTO = new ProfileDTO();
		aggretatedProfileDTO.setName("AGGREGATED_PROFILE");
		aggretatedProfileDTO.setGlobalPermissions(new HashSet<GlobalPermissionEnum>());
		aggretatedProfileDTO.setPrivacyGroups(new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>());

		if (user == null || user.getOrgUnitWithProfiles() == null || CollectionUtils.isEmpty(user.getOrgUnitWithProfiles().getProfiles())) {
			return aggretatedProfileDTO;
		}

		// For each profile.
		for (final Profile profile : user.getOrgUnitWithProfiles().getProfiles()) {

			// Global permissions.
			if (profile.getGlobalPermissions() != null) {
				for (final GlobalPermission p : profile.getGlobalPermissions()) {

					// Aggregates global permissions among profiles.
					aggretatedProfileDTO.getGlobalPermissions().add(p.getPermission());
				}
			}

			// Privacy groups.
			if (profile.getPrivacyGroupPermissions() != null) {
				for (final PrivacyGroupPermission p : profile.getPrivacyGroupPermissions()) {

					final PrivacyGroupDTO groupDTO = mapper.map(p.getPrivacyGroup(), new PrivacyGroupDTO());

					// Aggregates privacy groups among profiles.
					if (aggretatedProfileDTO.getPrivacyGroups().get(groupDTO) != PrivacyGroupPermissionEnum.WRITE) {
						aggretatedProfileDTO.getPrivacyGroups().put(groupDTO, p.getPermission());
					}
				}
			}
		}

		return aggretatedProfileDTO;
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

		if (user == null || user.getOrgUnitWithProfiles() == null || user.getOrgUnitWithProfiles().getOrgUnit() == null) {
			// No units available.
			return;
		}

		crawlUnits(user.getOrgUnitWithProfiles().getOrgUnit(), units, addRoot);
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

		// Checks that the project is not deleted
		if (project.isDeleted()) {
			return false;
		}

		// Owner.
		if (project.getOwner() != null) {
			if (project.getOwner().getId().equals(user.getId())) {
				return true;
			}
		}

		// Manager.
		if (project.getManager() != null) {
			if (project.getManager().getId().equals(user.getId())) {
				return true;
			}
		}

		// Checks that the user can see this project.
		final HashSet<OrgUnit> units = new HashSet<OrgUnit>();
		crawlUnits(user.getOrgUnitWithProfiles().getOrgUnit(), units, true);

		for (final OrgUnit partner : project.getPartners()) {
			for (final OrgUnit unit : units) {
				if (partner.getId().equals(unit.getId())) {
					return true;
				}
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
		Handlers.crawlUnits(user.getOrgUnitWithProfiles().getOrgUnit(), units, true);

		for (final OrgUnit unit : units) {
			if (orgUnit.getId().equals(unit.getId())) {
				return true;
			}
		}

		return false;
	}

}
