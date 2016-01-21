package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.profile.GlobalPermission;
import org.sigmah.server.domain.profile.PrivacyGroupPermission;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProfilesWithDetails;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetProfilesWithDetails} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetProfilesWithDetailsHandler extends AbstractCommandHandler<GetProfilesWithDetails, ListResult<ProfileDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetProfilesWithDetailsHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProfileDTO> execute(final GetProfilesWithDetails cmd, final UserExecutionContext context) throws CommandException {

		final List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();

		final List<Profile> resultProfiles;

		final TypedQuery<Profile> query;

		if (cmd.getProfileId() == null) {

			query = em().createQuery("SELECT p FROM Profile p WHERE p.organization.id = :orgid ORDER BY p.id", Profile.class);
			query.setParameter("orgid", context.getUser().getOrganization().getId());
			resultProfiles = query.getResultList();

		}

		else {
			query = em().createQuery("SELECT p FROM Profile p WHERE p.organization.id = :orgid and p.id = :profileid", Profile.class);
			query.setParameter("orgid", context.getUser().getOrganization().getId());
			query.setParameter("profileid", cmd.getProfileId());
			resultProfiles = new ArrayList<Profile>();
			resultProfiles.add(query.getSingleResult());
		}

		if (resultProfiles != null) {

			LOG.debug("Found {} profiles.", resultProfiles.size());

			for (final Profile oneProfile : resultProfiles) {
				ProfileDTO profile = mapper().map(oneProfile, new ProfileDTO());
				// Global Permissions
				Set<GlobalPermissionEnum> permissions = new HashSet<GlobalPermissionEnum>();
				for (final GlobalPermission globalPermission : oneProfile.getGlobalPermissions()) {
					permissions.add(globalPermission.getPermission());
				}
				profile.setGlobalPermissions(permissions);
				// Privacy Groups
				Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> privacyGroups = new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>();
				for (final PrivacyGroupPermission privacyGroupPermission : oneProfile.getPrivacyGroupPermissions()) {
					if (privacyGroupPermission.getPrivacyGroup() != null) {
						PrivacyGroupDTO privacyGroupDTO = mapper().map(privacyGroupPermission.getPrivacyGroup(), new PrivacyGroupDTO());
						privacyGroups.put(privacyGroupDTO, privacyGroupPermission.getPermission());
					}
				}
				profile.setPrivacyGroups(privacyGroups);
				profiles.add(profile);
			}
		}

		return new ListResult<ProfileDTO>(profiles);
	}

}
