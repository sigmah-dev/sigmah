package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class AuthenticationJS extends JavaScriptObject {
	
	protected AuthenticationJS() {
	}
	
	public static AuthenticationJS toJavaScript(Authentication authentication) {
		final AuthenticationJS authenticationJS = Values.createJavaScriptObject(AuthenticationJS.class);
		
		authenticationJS.setAuthenticationToken(authentication.getAuthenticationToken());
		authenticationJS.setUserId(authentication.getUserId());
		authenticationJS.setUserEmail(authentication.getUserEmail());
		authenticationJS.setUserName(authentication.getUserName());
		authenticationJS.setUserFirstName(authentication.getUserFirstName());
		authenticationJS.setLanguage(authentication.getLanguage());
		authenticationJS.setOrganizationId(authentication.getOrganizationId());
		authenticationJS.setOrganizationName(authentication.getOrganizationName());
		authenticationJS.setOrganizationLogo(authentication.getOrganizationLogo());
		authenticationJS.setOrgUnitId(authentication.getOrgUnitId());
		authenticationJS.setAggregatedProfile(authentication.getAggregatedProfile());
		
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
				getOrgUnitId(), 
				getAggregatedProfileDTO(),
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

	public Integer getOrgUnitId() {
		return Values.getInteger(this, "orgUnitId");
	}

	public void setOrgUnitId(Integer orgUnitId) {
		Values.setInteger(this, "orgUnitId", orgUnitId);
	}

	public native ProfileJS getAggregatedProfile() /*-{
		return this.aggregatedProfile;
	}-*/;
	
	public ProfileDTO getAggregatedProfileDTO() {
		if(getAggregatedProfile() != null) {
			return getAggregatedProfile().toDTO();
		} else {
			return null;
		}
	}

	public native void setAggregatedProfile(ProfileJS aggregatedProfile) /*-{
		this.aggregatedProfile = aggregatedProfile;
	}-*/;
	
	public void setAggregatedProfile(ProfileDTO aggregatedProfile) {
        if(aggregatedProfile != null) {
            setAggregatedProfile(ProfileJS.toJavaScript(aggregatedProfile));
        }
	}
	
}
