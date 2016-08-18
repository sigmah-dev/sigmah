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

import java.util.List;

import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;

import java.util.ArrayList;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UserJS extends JavaScriptObject {

	protected UserJS() {
	}

	public static UserJS toJavaScript(UserDTO userDTO) {
		final UserJS userJS = Values.createJavaScriptObject(UserJS.class);

		userJS.setId(userDTO.getId());
		userJS.setOrganization((Integer)userDTO.get("organization"));
		userJS.setName(userDTO.getName());
		userJS.setEmail(userDTO.getEmail());
		userJS.setFirstName(userDTO.getFirstName());
		userJS.setCompleteName(userDTO.getCompleteName());
		userJS.setLocale(userDTO.getLocale());
		userJS.setMainOrgUnit(userDTO.getMainOrgUnit());
		userJS.setSecondaryOrgUnits(userDTO.getSecondaryOrgUnits());
		userJS.setProfiles(userDTO.getProfiles());
		userJS.computeOrgUnits();

		return userJS;
	}

	public UserDTO toDTO() {
		final UserDTO userDTO = new UserDTO();

		userDTO.setId(getId());
		userDTO.setName(getName());
		userDTO.setEmail(getEmail());
		userDTO.setFirstName(getFirstName());
		userDTO.setCompleteName(getCompleteName());
		userDTO.setLocale(getLocale());
		userDTO.setProfiles(getProfilesDTO());

		return userDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native void setOrganization() /*-{
		this.organization = undefined;
	}-*/;

	public void setOrganization(Integer id) {
		if(id != null) {
			setOrganization(id.intValue());
		} else {
			setOrganization();
		}
	}

	public native void setOrganization(int id) /*-{
		this.organization = id;
	}-*/;

	public native int getOrganization() /*-{
		return this.organization;
	}-*/;

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native String getEmail() /*-{
		return this.email;
	}-*/;

	public native void setEmail(String email) /*-{
		this.email = email;
	}-*/;

	public native String getFirstName() /*-{
		return this.firstName;
	}-*/;

	public native void setFirstName(String firstName) /*-{
		this.firstName = firstName;
	}-*/;

	public native String getCompleteName() /*-{
		return this.completeName;
	}-*/;

	public native void setCompleteName(String completeName) /*-{
		this.completeName = completeName;
	}-*/;

	public native String getLocale() /*-{
		return this.locale;
	}-*/;

	public native void setLocale(String locale) /*-{
		this.locale = locale;
	}-*/;

	public native boolean hasMainOrgUnit() /*-{
		return typeof this.mainOrgUnit != 'undefined';
	}-*/;

	public native int getMainOrgUnit() /*-{
		return this.mainOrgUnit;
	}-*/;

	public void setMainOrgUnit(OrgUnitDTO mainOrgUnitDTO) {
		if (mainOrgUnitDTO != null) {
			setMainOrgUnit(mainOrgUnitDTO.getId());
		}
	}

	public native void setMainOrgUnit(int mainOrgUnit) /*-{
		this.mainOrgUnit = mainOrgUnit;
	}-*/;

	public native JsArrayInteger getSecondaryOrgUnits() /*-{
		return this.secondaryOrgUnits;
	}-*/;

	public void setSecondaryOrgUnits(List<OrgUnitDTO> secondaryOrgUnitDTOs) {
		if (secondaryOrgUnitDTOs == null) {
			return;
		}

		JsArrayInteger array = JavaScriptObject.createArray().cast();
		for (OrgUnitDTO orgUnitDTO : secondaryOrgUnitDTOs) {
			array.push(orgUnitDTO.getId());
		}
		setSecondaryOrgUnits(array);
	}

	public native void setSecondaryOrgUnits(JsArrayInteger secondaryOrgUnits) /*-{
		this.secondaryOrgUnits = secondaryOrgUnits;
	}-*/;

	public native JsArrayInteger computeOrgUnits() /*-{
		if (!this.mainOrgUnit) {
			// if mainOrgUnit is not defined, secondaryOrgUnits too
			return [];
		}

		if (!this.secondaryOrgUnits) {
			return [ this.mainOrgUnit ];
		}

		this.orgUnits = [].concat(this.mainOrgUnit, this.secondaryOrgUnits);
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

	public native boolean isActive() /*-{
		return this.active;
	}-*/;

	public native void setActive(boolean active) /*-{
		this.active = active;
	}-*/;
}
