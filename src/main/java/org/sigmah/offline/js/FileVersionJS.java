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
		fileVersionDTO.setAvailable(!hasAvailable() || isAvailable());
		
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

	public native boolean hasAvailable() /*-{
		return typeof this.available !== 'undefined';
	}-*/;
	
	public native boolean isAvailable() /*-{
		return this.available;
	}-*/;

	public native void setAvailable(boolean available) /*-{
		this.available = available;
	}-*/;
}
