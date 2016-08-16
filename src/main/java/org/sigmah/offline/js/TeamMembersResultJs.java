package org.sigmah.offline.js;

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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

public final class TeamMembersResultJs extends JavaScriptObject {
	protected TeamMembersResultJs() {
	}

	public static TeamMembersResultJs toJavascript(TeamMembersResult teamMembersResult) {
		TeamMembersResultJs teamMembersResultJs = Values.createJavaScriptObject(TeamMembersResultJs.class);
		teamMembersResultJs.setProjectId(teamMembersResult.getProjectId());
		teamMembersResultJs.setProjectManager(teamMembersResult.getProjectManager());
		teamMembersResultJs.setTeamMembers(teamMembersResult.getTeamMembers());
		teamMembersResultJs.setTeamMemberProfiles(teamMembersResult.getTeamMemberProfiles());
		return teamMembersResultJs;
	}

	public native int getProjectId() /*-{
		return this.id;
	}-*/;

	public native UserJS getProjectManager() /*-{
		return this.projectManager;
	}-*/;

	public native void setProjectId(int projectId) /*-{
		this.id = projectId;
	}-*/;

	public UserDTO getProjectManagerDTO() {
		return getProjectManager().toDTO();
	}

	public void setProjectManager(UserDTO projectManager) {
		if (projectManager == null) {
			return;
		}

		setProjectManager(UserJS.toJavaScript(projectManager));
	}

	public native void setProjectManager(UserJS projectManager) /*-{
		this.projectManager = projectManager;
	}-*/;

	public native JsArray<UserJS> getTeamMembers() /*-{
		return this.teamMembers;
	}-*/;

	public List<UserDTO> getTeamMembersDTO() {
		JsArray<UserJS> teamMembers = getTeamMembers();
		if (teamMembers == null) {
			return Collections.emptyList();
		}

		List<UserDTO> userDTOs = new ArrayList<UserDTO>(teamMembers.length());
		for (int i = 0; i < teamMembers.length(); i++) {
			userDTOs.add(teamMembers.get(i).toDTO());
		}
		return userDTOs;
	}

	public void setTeamMembers(List<UserDTO> teamMembers) {
		if (teamMembers == null) {
			return;
		}

		JsArray<UserJS> userJs = Values.createTypedJavaScriptArray(UserJS.class);
		for (final UserDTO userDTO : teamMembers) {
			userJs.push(UserJS.toJavaScript(userDTO));
		}
		setTeamMembers(userJs);
	}

	public native void setTeamMembers(JsArray<UserJS> teamMembers) /*-{
		this.teamMembers = teamMembers;
	}-*/;

	public native JsArray<ProfileJS> getTeamMemberProfiles() /*-{
		return this.teamMemberProfiles;
	}-*/;

	public List<ProfileDTO> getTeamMemberProfilesDTO() {
		JsArray<ProfileJS> teamMemberProfiles = getTeamMemberProfiles();
		if (teamMemberProfiles == null) {
			return Collections.emptyList();
		}

		List<ProfileDTO> userDTOs = new ArrayList<ProfileDTO>(teamMemberProfiles.length());
		for (int i = 0; i < teamMemberProfiles.length(); i++) {
			userDTOs.add(teamMemberProfiles.get(i).toDTO());
		}
		return userDTOs;
	}

	public void setTeamMemberProfiles(List<ProfileDTO> teamMemberProfiles) {
		if (teamMemberProfiles == null) {
			return;
		}

		JsArray<ProfileJS> profilesJS = Values.createTypedJavaScriptArray(ProfileJS.class);
		for (final ProfileDTO profileDTO : teamMemberProfiles) {
			profilesJS.push(ProfileJS.toJavaScript(profileDTO));
		}
		setTeamMemberProfiles(profilesJS);
	}

	public native void setTeamMemberProfiles(JsArray<ProfileJS> teamMemberProfiles) /*-{
		this.teamMemberProfiles = teamMemberProfiles;
	}-*/;

	public TeamMembersResult toDTO() {
		TeamMembersResult teamMembersResult = new TeamMembersResult();
		teamMembersResult.setProjectId(getProjectId());
		teamMembersResult.setProjectManager(getProjectManagerDTO());
		teamMembersResult.setTeamMembers(getTeamMembersDTO());
		teamMembersResult.setTeamMemberProfiles(getTeamMemberProfilesDTO());
		return teamMembersResult;
	}
}
