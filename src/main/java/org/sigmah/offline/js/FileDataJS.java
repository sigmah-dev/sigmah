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
