package org.sigmah.offline.js;

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
public class PrepareFileUploadJS extends CommandJS {
	
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
