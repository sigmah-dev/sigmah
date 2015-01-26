package org.sigmah.offline.js;

import org.sigmah.offline.fileapi.Int8Array;
import org.sigmah.shared.util.FileType;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class FileDataJS extends JavaScriptObject {
	
	protected FileDataJS() {
	}
	
	public static FileDataJS createFileDataJS(FileVersionJS fileVersionJS, Int8Array data) {
		final FileDataJS fileDataJS = Values.createJavaScriptObject(FileDataJS.class);
		
		fileDataJS.setFileVersion(fileVersionJS);
		fileDataJS.setMimeType(FileType.fromExtension(fileVersionJS.getExtension(), FileType._DEFAULT).getContentType());
		fileDataJS.setData(data);
		
		return fileDataJS;
	}
	
	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public native FileVersionJS getFileVersion() /*-{
		return this.fileVersion;
	}-*/;

	public native void setFileVersion(FileVersionJS fileVersion) /*-{
		this.fileVersion = fileVersion;
	}-*/;
	
	public native String getMimeType() /*-{
		return this.mimeType;
	}-*/;
	
	public native void setMimeType(String mimeType) /*-{
		this.mimeType = mimeType;
	}-*/;
	
	public native Int8Array getData() /*-{
		return this.data;
	}-*/;

	public native void setData(Int8Array data) /*-{
		this.data = data;
	}-*/;
	
}
