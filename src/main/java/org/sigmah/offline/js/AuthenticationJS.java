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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;

import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 * JavaScript version of <code>Authentication</code>.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class AuthenticationJS extends JavaScriptObject {

	public static final int DEFAULT_ID = 1;

	protected AuthenticationJS() {
	}

	public static AuthenticationJS toJavaScript(Authentication authentication) {
		final AuthenticationJS authenticationJS = Values.createJavaScriptObject(AuthenticationJS.class);

		authenticationJS.setId(DEFAULT_ID);
		authenticationJS.setAuthenticationToken(authentication.getAuthenticationToken());
		authenticationJS.setUserId(authentication.getUserId());
		authenticationJS.setUserEmail(authentication.getUserEmail());
		authenticationJS.setUserName(authentication.getUserName());
		authenticationJS.setUserFirstName(authentication.getUserFirstName());
		authenticationJS.setLanguage(authentication.getLanguage());
		authenticationJS.setOrganizationId(authentication.getOrganizationId());
		authenticationJS.setOrganizationName(authentication.getOrganizationName());
		authenticationJS.setOrganizationLogo(authentication.getOrganizationLogo());
		authenticationJS.setMainOrgUnitId(authentication.getMainOrgUnitId());
		authenticationJS.setSecondaryOrgUnitIds(authentication.getSecondaryOrgUnitIds());
		authenticationJS.setAggregatedProfiles(authentication.getAggregatedProfiles());
		authenticationJS.setMemberOfProjectIds(authentication.getMemberOfProjectIds());
		authenticationJS.setOrganizationSolrCoreUrl(authentication.getOrganizationSolrCoreUrl());

		return authenticationJS;
	}

	public Authentication toAuthentication() {
		final Authentication authentication = new Authentication(
			getUserId(),
			getUserEmail(),
			getUserName(),
			getUserFirstName(),
			getLanguage(),
			getOrganizationId(),
			getOrganizationName(),
			getOrganizationLogo(),
			getOrganizationSolrCoreUrl(),
			getMainOrgUnitId(),
			getSecondaryOrgUnitIdsDTO(),
			getAggregatedProfilesDTO(),
			getMemberOfProjectIdsDTO(),
			getUserId() != null);
		authentication.setAuthenticationToken(getAuthenticationToken());
		return authentication;
	}

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native String getAuthenticationToken() /*-{
		return this.authenticationToken;
	}-*/;

	public native void setAuthenticationToken(String authenticationToken) /*-{
		this.authenticationToken = authenticationToken;
	}-*/;

	public Integer getUserId() {
		return Values.getInteger(this, "userId");
	}

	public void setUserId(Integer userId) {
		Values.setInteger(this, "userId", userId);
	}

	public native String getUserEmail() /*-{
		return this.userEmail;
	}-*/;

	public native void setUserEmail(String userEmail) /*-{
		this.userEmail = userEmail;
	}-*/;

	public native String getUserName() /*-{
		return this.userName;
	}-*/;

	public native void setUserName(String userName) /*-{
		this.userName = userName;
	}-*/;

	public native String getUserFirstName() /*-{
		return this.userFirstName;
	}-*/;

	public native void setUserFirstName(String userFirstName) /*-{
		this.userFirstName = userFirstName;
	}-*/;

	public Language getLanguage() {
		return Values.getEnum(this, "language", Language.class);
	}

	public void setLanguage(Language language) {
		Values.setEnum(this, "language", language);
	}

	public Integer getOrganizationId() {
		return Values.getInteger(this, "organizationId");
	}

	public void setOrganizationId(Integer organizationId) {
		Values.setInteger(this, "organizationId", organizationId);
	}

	public native String getOrganizationName() /*-{
		return this.organizationName;
	}-*/;

	public native void setOrganizationName(String organizationName) /*-{
		this.organizationName = organizationName;
	}-*/;

	public native String getOrganizationLogo() /*-{
		return this.organizationLogo;
	}-*/;

	public native void setOrganizationLogo(String organizationLogo) /*-{
		this.organizationLogo = organizationLogo;
	}-*/;
	
	public native String getOrganizationSolrCoreUrl() /*-{
		return this.organizationSolrCoreUrl;
	}-*/;

	public native void setOrganizationSolrCoreUrl(String organizationSolrCoreUrl) /*-{
		this.organizationSolrCoreUrl = organizationSolrCoreUrl;
	}-*/;

	public Integer getMainOrgUnitId() {
		return Values.getInteger(this, "orgUnitId");
	}

	public void setMainOrgUnitId(Integer orgUnitId) {
		Values.setInteger(this, "orgUnitId", orgUnitId);
	}

	public Set<Integer> getSecondaryOrgUnitIdsDTO() {
		Set<Integer> orgUnitIds = new HashSet<Integer>();
		JsArrayInteger secondaryOrgUnitIds = getSecondaryOrgUnitIds();
		if (secondaryOrgUnitIds == null) {
			return Collections.emptySet();
		}

		for (int i = 0; i < secondaryOrgUnitIds.length(); i++) {
			orgUnitIds.add(secondaryOrgUnitIds.get(i));
		}
		return orgUnitIds;
	}

	public native JsArrayInteger getSecondaryOrgUnitIds() /*-{
		return this.secondaryOrgUnitIds;
	}-*/;

	public native void setSecondaryOrgUnitIds(JsArrayInteger secondaryOrgUnitIds) /*-{
		this.secondaryOrgUnitIds = secondaryOrgUnitIds;
	}-*/;

	public void setSecondaryOrgUnitIds(Set<Integer> secondaryOrgUnitIds) {
		JsArrayInteger array = JavaScriptObject.createArray().cast();
		for (Integer orgUnitId : secondaryOrgUnitIds) {
			array.push(orgUnitId);
		}
		setSecondaryOrgUnitIds(array);
	}

	public native JsMap<Integer, ProfileJS> getAggregatedProfiles() /*-{
		if (this.aggregatedProfiles) {
			return this.aggregatedProfiles;
		}

		var aggregatedProfiles = [];
		if (this.orgUnitId && this.aggregatedProfile) {
			var aggregatedProfiles = {};
			aggregatedProfiles[this.orgUnitId] = this.aggregatedProfile;
			return [aggregatedProfiles];
		}
		return [];
	}-*/;

	public Map<Integer, ProfileDTO> getAggregatedProfilesDTO() {
		JsMap<Integer, ProfileJS> aggregatedProfilesJS = getAggregatedProfiles();
		if (aggregatedProfilesJS == null) {
			return null;
		}

		HashMap<Integer, ProfileDTO> aggregatedProfiles = new HashMap<Integer, ProfileDTO>();
		JsArrayString keyArray = aggregatedProfilesJS.keyArray();
		for (int i = 0; i < keyArray.length(); i++) {
			int orgUnitId = Integer.parseInt(keyArray.get(i));
			aggregatedProfiles.put(orgUnitId, aggregatedProfilesJS.get(orgUnitId).toDTO());
		}
		return aggregatedProfiles;
	}

	public native void setAggregatedProfiles(JsMap<Integer, ProfileJS> aggregatedProfiles) /*-{
		this.aggregatedProfiles = aggregatedProfiles;
	}-*/;

	public void setAggregatedProfiles(Map<Integer, ProfileDTO> aggregatedProfiles) {
		if (aggregatedProfiles == null) {
			return;
		}

		JsMap<Integer, ProfileJS> aggregatedProfilesJS = JsMap.<Integer, ProfileJS>createMap();
		for (Map.Entry<Integer, ProfileDTO> aggregatedProfileEntry : aggregatedProfiles.entrySet()) {
			aggregatedProfilesJS.put(aggregatedProfileEntry.getKey(), ProfileJS.toJavaScript(aggregatedProfileEntry.getValue()));
		}
		setAggregatedProfiles(aggregatedProfilesJS);
	}

	public native JsArrayInteger getMemberOfProjectIds() /*-{
		return this.memberOfProjectIds;
	}-*/;

	public Set<Integer> getMemberOfProjectIdsDTO() {
		Set<Integer> projectIds = new HashSet<Integer>();
		JsArrayInteger memberOfProjectIds = getMemberOfProjectIds();
		if (memberOfProjectIds == null) {
			return Collections.emptySet();
		}

		for (int i = 0; i < memberOfProjectIds.length(); i++) {
			projectIds.add(memberOfProjectIds.get(i));
		}
		return projectIds;
	}

	public native void setMemberOfProjectIds(JsArrayInteger memberOfProjectIds) /*-{
		this.memberOfProjectIds = memberOfProjectIds;
	}-*/;

	public void setMemberOfProjectIds(Set<Integer> memberOfProjectIds) {
		JsArrayInteger array = JavaScriptObject.createArray().cast();
		for (Integer memberOfProjectId : memberOfProjectIds) {
			array.push(memberOfProjectId);
		}
		setMemberOfProjectIds(array);
	}
}
