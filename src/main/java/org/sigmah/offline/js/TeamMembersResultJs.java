package org.sigmah.offline.js;

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
		teamMembersResult.setTeamMemberProfiles(teamMembersResult.getTeamMemberProfiles());
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
