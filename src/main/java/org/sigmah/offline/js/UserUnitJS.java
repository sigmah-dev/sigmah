package org.sigmah.offline.js;

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
