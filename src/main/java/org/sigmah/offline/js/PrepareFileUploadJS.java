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

import com.google.gwt.core.client.JsArrayString;
import java.util.HashMap;
import java.util.Map;
import org.sigmah.shared.command.PrepareFileUpload;
import org.sigmah.shared.dto.value.FileUploadUtils;

/**
 * JavaScript version of the {@link PrepareFileUpload} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class PrepareFileUploadJS extends CommandJS {
	
	protected PrepareFileUploadJS() {
	}
	
	public static PrepareFileUploadJS toJavaScript(PrepareFileUpload prepareFileUpload) {
		final PrepareFileUploadJS prepareFileUploadJS = Values.createJavaScriptObject(PrepareFileUploadJS.class);
		
		prepareFileUploadJS.setFileName(prepareFileUpload.getFileName());
		prepareFileUploadJS.setSize(prepareFileUpload.getSize());
		prepareFileUploadJS.setProperties(prepareFileUpload.getProperties());
		
		return prepareFileUploadJS;
	}
	
	public PrepareFileUpload toPrepareFileUpload() {
		final Map<String, String> properties = getPropertyMap();
		properties.put(FileUploadUtils.GENERATED_ID, Integer.toString(getId()));
		
		return new PrepareFileUpload(getFileName(), getSize(), properties);
	}

	public native JsMap<String, String> getProperties() /*-{
		return this.properties;
	}-*/;

	public native void setProperties(JsMap<String, String> properties) /*-{
		this.properties = properties;
	}-*/;
	
	public Map<String, String> getPropertyMap() {
		if(getProperties() != null) {
			final HashMap<String, String> map = new HashMap<String, String>();
			
			final JsMap<String, String> properties = getProperties();
			final JsArrayString keys = properties.keyArray();
			
			for(int index = 0; index < keys.length(); index++) {
				final String key = keys.get(index);
				map.put(key, properties.get(key));
			}
			
			return map;
		}
		return null;
	}
	
	public void setProperties(Map<String, String> properties) {
		if(properties != null) {
			final JsMap<String, String> map = JsMap.createMap();
			
			for(final Map.Entry<String, String> entry : properties.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
			
			setProperties(map);
		}
	}

	public native String getFileName() /*-{
		return this.fileName;
	}-*/;

	public native void setFileName(String fileName) /*-{
		this.fileName = fileName;
	}-*/;

	public native int getSize() /*-{
		return this.size;
	}-*/;

	public native void setSize(int size) /*-{
		this.size = size;
	}-*/;
	
}
