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
