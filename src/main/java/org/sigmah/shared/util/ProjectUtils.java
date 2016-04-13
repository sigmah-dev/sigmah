package org.sigmah.shared.util;

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
			profileDTO.getGlobalPermissions().contains(GlobalPermissionEnum.EDIT_MY_PROJECTS);
	}
}
