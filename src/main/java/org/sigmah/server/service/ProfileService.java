package org.sigmah.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.profile.GlobalPermission;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.domain.profile.PrivacyGroupPermission;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Handler for updating profile command.
 * 
 * @author nrebiai (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class ProfileService extends AbstractEntityService<Profile, Integer, ProfileDTO> {

	/**
	 * Application injector.
	 */
	private final Injector injector;

	@Inject
	public ProfileService(final Injector injector) {
		this.injector = injector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Profile create(final PropertyMap properties, final UserExecutionContext context) {

		Profile profileFound = null;
		Profile profileToPersist = new Profile();
		final List<GlobalPermission> gps = new ArrayList<GlobalPermission>();
		final List<PrivacyGroupPermission> pgs = new ArrayList<PrivacyGroupPermission>();

		final ProfileDTO profileDTO = (ProfileDTO) properties.get(ProfileDTO.PROFILE);
		final Set<GlobalPermissionEnum> gpEnumList = profileDTO.getGlobalPermissions();
		final Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> privacyGroupsPerms = profileDTO.getPrivacyGroups();

		if (profileDTO.getId() != null) {
			profileFound = em().find(Profile.class, profileDTO.getId());

			for (final GlobalPermission globalPerm : profileFound.getGlobalPermissions()) {
				em().remove(globalPerm);
			}

			for (final PrivacyGroupPermission privacyGroupPerm : profileFound.getPrivacyGroupPermissions()) {
				em().remove(privacyGroupPerm);
			}
		}

		if (profileFound != null) {
			profileToPersist = profileFound;
		}

		if (profileDTO.getName() != null) {
			profileToPersist.setName(profileDTO.getName());
		}

		for (final GlobalPermissionEnum gpEnum : gpEnumList) {
			final GlobalPermission gpToPersist = new GlobalPermission();
			gpToPersist.setPermission(gpEnum);
			gpToPersist.setProfile(profileToPersist);
			gps.add(gpToPersist);
		}

		profileToPersist.setGlobalPermissions(gps);

		for (final Entry<PrivacyGroupDTO, PrivacyGroupPermissionEnum> p : privacyGroupsPerms.entrySet()) {

			final PrivacyGroup privacyGroup = em().find(PrivacyGroup.class, p.getKey().getId());

			final PrivacyGroupPermission pgp = new PrivacyGroupPermission();
			pgp.setPermission(p.getValue());
			pgp.setPrivacyGroup(privacyGroup);
			pgp.setProfile(profileToPersist);
			pgs.add(pgp);
		}

		profileToPersist.setPrivacyGroupPermissions(pgs);
		profileToPersist.setOrganization(context.getUser().getOrganization());

		if (profileFound != null) {
			// update profile
			profileToPersist = em().merge(profileToPersist);

		} else {
			em().persist(profileToPersist);
		}

		em().flush();
		return profileToPersist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Profile update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("No policy update operation implemented for '" + entityClass.getSimpleName() + "' entity.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityDTO<?> handleMapping(final Profile createdProfile) throws CommandException {

		injector.getInstance(UserPermissionPolicy.class).updateUserPermissionByProfile(createdProfile.getId());

		return super.handleMapping(createdProfile);
	}

}
