package org.sigmah.offline.js;

import org.sigmah.shared.dto.value.FileVersionDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class FileVersionJS extends JavaScriptObject {
	
	protected FileVersionJS() {
	}
	
	public static FileVersionJS toJavaScript(FileVersionDTO fileVersionDTO) {
		final FileVersionJS fileVersionJS = Values.createJavaScriptObject(FileVersionJS.class);
		
		fileVersionJS.setId(fileVersionDTO.getId());
		fileVersionJS.setVersionNumber(fileVersionDTO.getVersionNumber());
		fileVersionJS.setPath(fileVersionDTO.getPath());
		fileVersionJS.setAddedDate(Values.toJsDate(fileVersionDTO.getAddedDate()));
		fileVersionJS.setSize(fileVersionDTO.getSize());
		fileVersionJS.setAuthorName(fileVersionDTO.getAuthorName());
		fileVersionJS.setAuthorFirstName(fileVersionDTO.getAuthorFirstName());
		fileVersionJS.setName(fileVersionDTO.getName());
		fileVersionJS.setExtension(fileVersionDTO.getExtension());
		
		return fileVersionJS;
	}
	
	public FileVersionDTO toDTO() {
		final FileVersionDTO fileVersionDTO = new FileVersionDTO();
		
		fileVersionDTO.setId(getId());
		fileVersionDTO.setVersionNumber(getVersionNumber());
		fileVersionDTO.setPath(getPath());
		fileVersionDTO.setAddedDate(Values.toDate(getAddedDate()));
		fileVersionDTO.setSize((long)getSize());
		fileVersionDTO.setAuthorName(getAuthorName());
		fileVersionDTO.setAuthorFirstName(getAuthorFirstName());
		fileVersionDTO.setName(getName());
		fileVersionDTO.setExtension(getExtension());
		
		return fileVersionDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native int getVersionNumber() /*-{
		return this.versionNumber;
	}-*/;

	public native void setVersionNumber(int versionNumber) /*-{
		this.versionNumber = versionNumber;
	}-*/;

	public native String getPath() /*-{
		return this.path;
	}-*/;

	public native void setPath(String path) /*-{
		this.path = path;
	}-*/;

	public native JsDate getAddedDate() /*-{
		return this.addedDate;
	}-*/;

	public native void setAddedDate(JsDate addedDate) /*-{
		this.addedDate = addedDate;
	}-*/;

	public native double getSize() /*-{
		return this.size;
	}-*/;

	public native void setSize(double size) /*-{
		this.size = size;
	}-*/;

	public native String getAuthorName() /*-{
		return this.authorName;
	}-*/;

	public native void setAuthorName(String authorName) /*-{
		this.authorName = authorName;
	}-*/;

	public native String getAuthorFirstName() /*-{
		return this.authorFirstName;
	}-*/;

	public native void setAuthorFirstName(String authorFirstName) /*-{
		this.authorFirstName = authorFirstName;
	}-*/;

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native String getExtension() /*-{
		return this.extension;
	}-*/;

	public native void setExtension(String extension) /*-{
		this.extension = extension;
	}-*/;
}
