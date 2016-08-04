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

import com.google.gwt.core.client.JsArray;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.UpdateProjectTeamMembers;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

public class UpdateProjectTeamMembersJs extends CommandJS {
  protected UpdateProjectTeamMembersJs() {
  }

  public static UpdateProjectTeamMembersJs toJavascript(UpdateProjectTeamMembers updateProjectTeamMembers) {
    UpdateProjectTeamMembersJs updateProjectTeamMembersJs = Values.createJavaScriptObject(UpdateProjectTeamMembersJs.class);
    updateProjectTeamMembersJs.setProjectId(updateProjectTeamMembers.getProjectId());
    updateProjectTeamMembersJs.setTeamMembers(updateProjectTeamMembers.getTeamMembers());
    updateProjectTeamMembersJs.setTeamMemberProfiles(updateProjectTeamMembers.getTeamMemberProfiles());
    return updateProjectTeamMembersJs;
  }

  public UpdateProjectTeamMembers toUpdateProjectTeamMembers() {
    return new UpdateProjectTeamMembers(getProjectId(), getTeamMembersDTO(), getTeamMemberProfilesDTO());
  }

  public native int getProjectId() /*-{
    return this.projectId;
  }-*/;

  public native void setProjectId(int projectId) /*-{
    this.projectId = projectId;
  }-*/;

  public native JsArray<UserJS> getTeamMembers() /*-{
    return this.teamMembers;
  }-*/;

  public List<UserDTO> getTeamMembersDTO() {
    JsArray<UserJS> teamMembersJs = getTeamMembers();
    List<UserDTO> teamMembers = new ArrayList<UserDTO>();
    for (int i = 0; i < teamMembersJs.length(); i++) {
      teamMembers.add(teamMembersJs.get(i).toDTO());
    }
    return teamMembers;
  }

  public native void setTeamMembers(JsArray<UserJS> teamMembers) /*-{
    this.teamMembers = teamMembers;
  }-*/;

  public void setTeamMembers(List<UserDTO> teamMembers) {
    JsArray<UserJS> teamMembersJs = JsArray.createArray().cast();
    for (UserDTO teamMember : teamMembers) {
      teamMembersJs.push(UserJS.toJavaScript(teamMember));
    }
    setTeamMembers(teamMembersJs);
  }

  public native JsArray<ProfileJS> getTeamMemberProfiles() /*-{
    return this.teamMemberProfiles;
  }-*/;

  public List<ProfileDTO> getTeamMemberProfilesDTO() {
    JsArray<ProfileJS> teamMemberProfilesJs = getTeamMemberProfiles();
    List<ProfileDTO> teamMemberProfiles = new ArrayList<ProfileDTO>();
    for (int i = 0; i < teamMemberProfilesJs.length(); i++) {
      teamMemberProfiles.add(teamMemberProfilesJs.get(i).toDTO());
    }
    return teamMemberProfiles;
  }

  public native void setTeamMemberProfiles(JsArray<ProfileJS> teamMemberProfiles) /*-{
    this.teamMemberProfiles = teamMemberProfiles;
  }-*/;

  public void setTeamMemberProfiles(List<ProfileDTO> teamMemberProfiles) {
    JsArray<ProfileJS> teamMemberProfilesJs = JsArray.createArray().cast();
    for (ProfileDTO teamMemberProfile : teamMemberProfiles) {
      teamMemberProfilesJs.push(ProfileJS.toJavaScript(teamMemberProfile));
    }
    setTeamMemberProfiles(teamMemberProfilesJs);
  }
}
