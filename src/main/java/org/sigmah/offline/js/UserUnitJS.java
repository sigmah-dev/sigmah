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
import java.util.List;

import org.sigmah.server.domain.OrgUnit;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.UserUnitDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

public final class UserUnitJS extends JavaScriptObject {

	protected UserUnitJS() {
	}

	public static UserUnitJS toJavaScript(UserUnitDTO userUnitDTO) {
		final UserUnitJS userUnitJS = Values.createJavaScriptObject(UserUnitJS.class);

		userUnitJS.setId(userUnitDTO.getId());
		userUnitJS.setOrgUnit(userUnitDTO.getOrgUnit());
		userUnitJS.setProfiles(userUnitDTO.getProfiles());
		userUnitJS.setMainUserUnit(userUnitDTO.getMainUserUnit());

		return userUnitJS;
	}

	public UserUnitDTO toDTO() {
		final UserUnitDTO userUnitDTO = new UserUnitDTO();

		userUnitDTO.setId(getId());
		userUnitDTO.setOrgUnit(getOrgUnitDTO());
		userUnitDTO.setProfiles(getProfilesDTO());
		userUnitDTO.setMainUserUnit(isMainUserUnit());

		return userUnitDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public OrgUnitDTO getOrgUnitDTO() {
		return getOrgUnit().toDTO();
	}

	public native OrgUnitJS getOrgUnit() /*-{
		return this.orgUnit;
	}-*/;

	public void setOrgUnit(OrgUnitDTO orgUnitDTO) {
		setOrgUnit(OrgUnitJS.toJavaScript(orgUnitDTO));
	}

	public native void setOrgUnit(OrgUnitJS orgUnit) /*-{
		this.orgUnit = orgUnit;
	}-*/;

	public native JsArray<ProfileJS> getProfiles() /*-{
		return this.profiles;
	}-*/;

	public List<ProfileDTO> getProfilesDTO() {
		if(getProfiles() != null) {
			final List<ProfileDTO> profilesDTO = new ArrayList<ProfileDTO>();

			final JsArray<ProfileJS> profilesJS = getProfiles();
			for(int index = 0; index < profilesJS.length(); index++) {
				profilesDTO.add(profilesJS.get(index).toDTO());
			}

			return profilesDTO;
		}
		return null;
	}

	public void setProfiles(List<ProfileDTO> profilesDTO) {
		if(profilesDTO != null) {
			final JsArray<ProfileJS> profilesJS = Values.createTypedJavaScriptArray(ProfileJS.class);

			for(final ProfileDTO profileDTO : profilesDTO) {
				profilesJS.push(ProfileJS.toJavaScript(profileDTO));
			}
			setProfiles(profilesJS);
		}
	}

	public native void setProfiles(JsArray<ProfileJS> profiles) /*-{
		this.profiles = profiles;
	}-*/;

	public native boolean isMainUserUnit() /*-{
		return this.mainUserUnit;
	}-*/;

	public native void setMainUserUnit(boolean mainUserUnit) /*-{
		this.mainUserUnit = mainUserUnit;
	}-*/;
}
