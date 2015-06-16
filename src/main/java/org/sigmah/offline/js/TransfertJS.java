package org.sigmah.offline.js;

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
