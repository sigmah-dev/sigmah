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

import java.util.Map;

import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

public class ProjectUtils {
	public static boolean isProjectVisible(ProjectDTO project, Authentication authentication) {
		if (project.getOwner() != null) {
			if (project.getOwner().getId().equals(authentication.getUserId())) {
				return true;
			}
		}

		// Manager.
		if (project.getManager() != null) {
			if (project.getManager().getId().equals(authentication.getUserId())) {
				return true;
			}
		}

		Map<Integer, ProfileDTO> aggregatedProfiles = authentication.getAggregatedProfiles();
		if (aggregatedProfiles == null) {
			return false;
		}
		ProfileDTO profileDTO = aggregatedProfiles.get(project.getOrgUnitId());
		if (profileDTO == null) {
			return false;
		}

		if (profileDTO.getGlobalPermissions().contains(GlobalPermissionEnum.VIEW_ALL_PROJECTS)) {
			return true;
		}

		return authentication.getMemberOfProjectIds().contains(project.getId()) &&
			profileDTO.getGlobalPermissions().contains(GlobalPermissionEnum.VIEW_MY_PROJECTS);
	}

	public static boolean isProjectEditable(ProjectDTO project, Authentication authentication) {
		if (!isProjectVisible(project, authentication)) {
			return false;
		}

		if (project.getOrgUnitId() == null) {
			// only draft projects does not have any org units :
			// if you can see the project you can do what you want with it
			return true;
		}

		Map<Integer, ProfileDTO> aggregatedProfiles = authentication.getAggregatedProfiles();
		if (aggregatedProfiles == null) {
			return false;
		}

		ProfileDTO profileDTO = aggregatedProfiles.get(project.getOrgUnitId());
		if (profileDTO == null) {
			return false;
		}

		if (profileDTO.getGlobalPermissions().contains(GlobalPermissionEnum.EDIT_ALL_PROJECTS)) {
			return true;
		}

		return authentication.getMemberOfProjectIds().contains(project.getId()) &&
			profileDTO.getGlobalPermissions().contains(GlobalPermissionEnum.EDIT_PROJECT);
	}
}
