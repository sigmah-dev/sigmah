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
import java.util.HashMap;
import java.util.Map;
import org.sigmah.offline.fileapi.Int8Array;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.file.TransfertType;

/**
 * File upload progression.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class TransfertJS extends JavaScriptObject {
	
	public static TransfertJS createTransfertJS(FileVersionDTO fileVersionDTO, TransfertType type) {
		final TransfertJS transfertJS = Values.createJavaScriptObject(TransfertJS.class);
		transfertJS.setFileVersion(FileVersionJS.toJavaScript(fileVersionDTO));
		transfertJS.setData(Values.createTypedJavaScriptArray(Int8Array.class));
		transfertJS.setProgress(0);
		transfertJS.setType(type);
		return transfertJS;
	}
	
	protected TransfertJS() {
	}
	
	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public TransfertType getType() {
		return Values.getEnum(this, "type", TransfertType.class);
	}

	public void setType(TransfertType type) {
		Values.setEnum(this, "type", type);
	}
	
	public native FileVersionJS getFileVersion() /*-{
		return this.fileVersion;
	}-*/;

	public native void setFileVersion(FileVersionJS fileVersion) /*-{
		this.fileVersion = fileVersion;
	}-*/;
	
	public native JsArray<Int8Array> getData() /*-{
		return this.data;
	}-*/;

	public native void setData(JsArray<Int8Array> data) /*-{
		this.data = data;
	}-*/;

	public native void setData(Int8Array data) /*-{
		this.data = [data];
	}-*/;

	public native int getProgress() /*-{
		return this.progress;
	}-*/;

	public native void setProgress(int progress) /*-{
		this.progress = progress;
	}-*/;
	
	public native JsMap<String, String> getProperties() /*-{
		return this.properties;
	}-*/;
	
	public native void setProperties(JsMap<String, String> properties) /*-{
		this.properties = properties;
	}-*/;
	
	public Map<String, String> getPropertyMap() {
		if(getProperties() != null) {
			return new HashMap<String, String>(new AutoBoxingJsMap<String, String>(getProperties(), AutoBoxingJsMap.STRING_BOXER));
		}
		return null;
	}
	
	public void setProperties(Map<String, String> map) {
		if(map != null) {
			final JsMap<String, String> jsMap = JsMap.createMap();
			jsMap.putAll(map);
			setProperties(jsMap);
		}
	}
}
