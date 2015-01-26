package org.sigmah.offline.js;

import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class PrivacyGroupJS extends JavaScriptObject {

	protected PrivacyGroupJS() {
	}
	
	public static PrivacyGroupJS toJavaScript(PrivacyGroupDTO privacyGroupDTO) {
		final PrivacyGroupJS privacyGroupJS = (PrivacyGroupJS)JavaScriptObject.createObject();
		
		privacyGroupJS.setId(privacyGroupDTO.getId());
		privacyGroupJS.setCode(privacyGroupDTO.getCode());
		privacyGroupJS.setTitle(privacyGroupDTO.getTitle());
		
		return privacyGroupJS;
	}
	
	public PrivacyGroupDTO toDTO() {
		final PrivacyGroupDTO privacyGroupDTO = new PrivacyGroupDTO();
		
		privacyGroupDTO.setId(getId());
		if(hasCode()) {
			privacyGroupDTO.setCode(getCode());
		}
		privacyGroupDTO.setTitle(getTitle());
		
		return privacyGroupDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native boolean hasCode() /*-{
		return typeof this.code != 'undefined';
	}-*/;
			
	public native int getCode() /*-{
		return this.code;
	}-*/;

	public void setCode(Integer code) {
		if(code != null) {
			setCode(code.intValue());
		}
	}
	public native void setCode(int code) /*-{
		this.code = code;
	}-*/;

	public native String getTitle() /*-{
		return this.title;
	}-*/;

	public native void setTitle(String title) /*-{
		this.title = title;
	}-*/;
}
