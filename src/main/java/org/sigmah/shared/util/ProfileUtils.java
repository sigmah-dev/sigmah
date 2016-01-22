package org.sigmah.shared.util;

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

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

/**
 * Utility class to manipulate profiles.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ProfileUtils {

	/**
	 * Provides only static methods.
	 */
	private ProfileUtils() {
		// Provides only static methods.
	}

	/**
	 * Returns if the authentication is granted for all the given permissions.
	 * 
	 * @param authentication
	 *          The authentication.
	 * @param permissions
	 *          The list of permissions.
	 * @return If the authentication is granted for this list of permissions.
	 */
	public static boolean isGranted(final Authentication authentication, final GlobalPermissionEnum... permissions) {

		if (authentication == null) {
			return false;
		}

		return isGranted(authentication.getAggregatedProfile(), permissions);
	}

	/**
	 * Returns if the given profile is granted for all the given permissions.
	 * 
	 * @param profile
	 *          The profile.
	 * @param permissions
	 *          The list of permissions.
	 * @return If the profile is granted for this list of permissions.
	 */
	public static boolean isGranted(final ProfileDTO profile, final GlobalPermissionEnum... permissions) {

		boolean granted = false;

		if (profile == null) {
			// The profile must not be null.
			return granted;
		}

		if (ClientUtils.isEmpty(permissions)) {
			// No permission needed.
			granted = true;

		} else if (profile.getGlobalPermissions() != null) {

			// Checks if the permissions is contained in the profile.
			granted = true;

			for (final GlobalPermissionEnum permission : permissions) {
				if (!profile.getGlobalPermissions().contains(permission)) {
					// Profile is not granted for one permission (at least).
					granted = false;
					break;
				}
			}
		}

		return granted;
	}

	/**
	 * Returns the permission {@link PrivacyGroupPermissionEnum} for the given authentication and the given privacy group.
	 * 
	 * @param authentication
	 *          The authentication.
	 * @param group
	 *          The privacy group.
	 * @return The permission for the authentication and this privacy group.
	 */
	public static PrivacyGroupPermissionEnum getPermission(final Authentication authentication, final PrivacyGroupDTO group) {

		if (authentication == null) {
			return PrivacyGroupPermissionEnum.NONE;
		}

		return getPermission(authentication.getAggregatedProfile(), group);
	}

	/**
	 * Returns the permission {@link PrivacyGroupPermissionEnum} for the given profile and the given privacy group.
	 * 
	 * @param profile
	 *          The profile.
	 * @param group
	 *          The privacy group.
	 * @return The permission for the profile and this privacy group.
	 */
	public static PrivacyGroupPermissionEnum getPermission(final ProfileDTO profile, final PrivacyGroupDTO group) {

		PrivacyGroupPermissionEnum permission = PrivacyGroupPermissionEnum.NONE;

		if (profile == null) {
			// The profile must not be null.
			return permission;
		}

		if (group == null) {
			// No permission needed.
			permission = PrivacyGroupPermissionEnum.WRITE;

		} else if (profile.getPrivacyGroups() != null) {
			// Checks if the privacy group is contained in the profile.
			final PrivacyGroupPermissionEnum p = profile.getPrivacyGroups().get(group);
			if (p != null) {
				permission = p;
			}
		}

		return permission;
	}

}
