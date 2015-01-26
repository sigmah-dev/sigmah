package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProfileJS extends JavaScriptObject {
	
	protected ProfileJS() {
	}
	
	public static ProfileJS toJavaScript(ProfileDTO profileDTO) {
		final ProfileJS profileJS = Values.createJavaScriptObject(ProfileJS.class);
		
		profileJS.setId(profileDTO.getId());
		profileJS.setName(profileDTO.getName());
		profileJS.setGlobalPermissions(profileDTO.getGlobalPermissions());
		profileJS.setPrivacyGroups(profileDTO.getPrivacyGroups());
		
		return profileJS;
	}
	
	public ProfileDTO toDTO() {
		final ProfileDTO profileDTO = new ProfileDTO();
		
		profileDTO.setId(getId());
		profileDTO.setName(getName());
		profileDTO.setGlobalPermissions(getGlobalPermissionsSet());
		profileDTO.setPrivacyGroups(getPrivacyGroupsMap());
		
		return profileDTO;
	}

	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native JsArrayString getGlobalPermissions() /*-{
		return this.globalPermissions;
	}-*/;

	public native void setGlobalPermissions(JsArrayString globalPermissions) /*-{
		this.globalPermissions = globalPermissions;
	}-*/;

	public Set<GlobalPermissionEnum> getGlobalPermissionsSet() {
		final JsArrayString globalPermissions = getGlobalPermissions();
		if(globalPermissions != null) {
			final EnumSet<GlobalPermissionEnum> set = EnumSet.noneOf(GlobalPermissionEnum.class);
			for(int index = 0; index < globalPermissions.length(); index++) {
				set.add(GlobalPermissionEnum.valueOf(globalPermissions.get(index)));
			}
			return set;
		}
		return null;
	}
	
	public void setGlobalPermissions(Set<GlobalPermissionEnum> globalPermissions) {
		if(globalPermissions != null) {
			JsArrayString array = Values.createJavaScriptArray(JsArrayString.class);
			for(final GlobalPermissionEnum globalPermission : globalPermissions) {
				if(globalPermission != null) {
					array.push(globalPermission.name());
				}
			}
			setGlobalPermissions(array);
		}
	}

	public native JsMap<String, String> getPrivacyGroups() /*-{
		return this.privacyGroups;
	}-*/;

	public native void setPrivacyGroups(JsMap<String, String> privacyGroups) /*-{
		this.privacyGroups = privacyGroups;
	}-*/;
	
	public Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> getPrivacyGroupsMap() {
		final JsMap<String, String> nativeMap = getPrivacyGroups();
		if(nativeMap != null) {
			final HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum> result = new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>();
			
			final AutoBoxingJsMap<PrivacyGroupDTO, String> privacyGroups = new AutoBoxingJsMap<PrivacyGroupDTO, String>(nativeMap, new PrivacyGroupJsMapBoxer());
			for(final Map.Entry<PrivacyGroupDTO, String> privacyGroup : privacyGroups.entrySet()) {
				result.put(privacyGroup.getKey(), PrivacyGroupPermissionEnum.valueOf(privacyGroup.getValue()));
			}
		}
		return null;
	}

	public void setPrivacyGroups(Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> privacyGroups) {
		if(privacyGroups != null) {
			final AutoBoxingJsMap<PrivacyGroupDTO, String> map = new AutoBoxingJsMap<PrivacyGroupDTO, String>(new PrivacyGroupJsMapBoxer());
			
			for(final Map.Entry<PrivacyGroupDTO, PrivacyGroupPermissionEnum> entry : privacyGroups.entrySet()) {
				map.put(entry.getKey(), entry.getValue().name());
			}
			
			setPrivacyGroups(map.getNativeMap());
		}
	}
	
}
